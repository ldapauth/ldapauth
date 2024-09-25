package com.ldapauth.synchronizer.feishu.pull;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.core.request.SelfBuiltAppAccessTokenReq;
import com.lark.oapi.core.response.AppAccessTokenResp;
import com.lark.oapi.service.contact.v3.model.*;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.persistence.service.PolicyPasswordService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.synchronizer.abstracts.AbstractSynchronizerService;
import com.ldapauth.synchronizer.ISynchronizerPullService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FeishuPullService extends AbstractSynchronizerService implements ISynchronizerPullService {

	@Autowired
	OrganizationService organizationService;

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	CacheService cacheService;

	@Autowired
	IdentifierGenerator identifierGenerator;


	Long trackingUniqueId = 0L;

	@Autowired
	PolicyPasswordService policyPasswordService;
	/**
	 * 同步入口
	 */
	public void sync() {
		trackingUniqueId = identifierGenerator.nextId("tku").longValue();
		List<String> orgIds = syncOrgs();
		if (CollectionUtil.isNotEmpty(orgIds)) {
			syncUser(orgIds);
		}
	}
	/**
	 * 进行同步飞书组织
	 */
	private List<String> syncOrgs(){
		Synchronizers synchronizers = getSynchronizer();
		List<String> resultIds = new ArrayList<>();
		try {
			Client client = getClient();
			// 发起token请求
			AppAccessTokenResp tokenResp = client.ext().getAppAccessTokenBySelfBuiltApp(
					SelfBuiltAppAccessTokenReq.newBuilder()
							.appId(synchronizers.getClientId())
							.appSecret(synchronizers.getClientSecret())
							.build());
			if (!tokenResp.success()) {
				log.error("feishu get token fail...,{}", tokenResp);
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(), "获取access_token失败");
			}

			ChildrenDepartmentReq req = ChildrenDepartmentReq.newBuilder()
					.departmentId(String.valueOf(getRootID()))
					.fetchChild(true)
					.pageSize(50)
					.build();
			// 获取组织机构
			ChildrenDepartmentResp resp = client
					.contact()
					.department()
					.children(req, RequestOptions.newBuilder()
							.appAccessToken(tokenResp.getAppAccessToken())
							.build());
			// 处理服务端错误
			if (!resp.success()) {
				log.info(String.format("code:%s,msg:%s,reqId:%s",resp.getCode(), resp.getMsg(), resp.getRequestId()));
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(),resp.getMsg());
			}
			List<Department> listDept = Arrays.stream(resp.getData().getItems()).collect(Collectors.toList());
			//判断是否分页token
			if (StringUtils.isNotEmpty(resp.getData().getPageToken())) {
				listDept = recursionDept(client, req, tokenResp.getAppAccessToken(), resp.getData().getPageToken(), listDept);
			}
			if (CollectionUtil.isNotEmpty(listDept)) {
				//提取id集合
				resultIds = listDept.stream().map(Department::getOpenDepartmentId).collect(Collectors.toList());
				for (Department department : listDept) {
					Organization organization = bulidOrgs(department);
					String syncActionType = ConstsSynchronizers.SyncActionType.ADD;
					if (Objects.nonNull(organization.getId()) && organization.getId().intValue() != 0) {
						syncActionType = ConstsSynchronizers.SyncActionType.UPDATE;
					}
					boolean flag = false;
					String message = "成功";
					Integer success = ConstsSynchronizers.SyncResultStatus.SUCCESS;
					try {
						//入库存储
						Result<String> resultd = organizationService.saveOrg(organization);
						if(!resultd.isSuccess()) {
							flag = false;
							message = resultd.getMessage();
						} else {
							flag = true;
						}
					} catch (BusinessException e) {
						flag = false;
						message = e.getMessage();
					}
					if(!flag) {
						success = ConstsSynchronizers.SyncResultStatus.FAIL;
					}
					//记录日志
					super.historyLogs(
							organization, trackingUniqueId,
							ConstsSynchronizers.SyncType.ORGS,
							syncActionType,
							success,
							message
					);
				}

			}
		} catch (Exception e){
			log.error("error",e);
			if (e instanceof BusinessException){
				throw new BusinessException(((BusinessException) e).getCode(),e.getMessage());
			} else {
				throw new BusinessException(500,e.getMessage());
			}
		}
		return resultIds;
	}


	/**
	 * 同步用户
	 * @param orgList 部门id集合
	 */
	public void syncUser(List<String> orgList){
		Synchronizers synchronizers = getSynchronizer();
		Client client = getClient();
		try {
			// 发起token请求
			AppAccessTokenResp tokenResp = client.ext().getAppAccessTokenBySelfBuiltApp(
					SelfBuiltAppAccessTokenReq.newBuilder()
							.appSecret(synchronizers.getClientSecret())
							.appId(synchronizers.getClientId())
							.build());
			if (!tokenResp.success()) {
				log.error("feishu get token fail...,{}", tokenResp);
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"获取access_token失败");
			}
			List<User> resultUser = new ArrayList<>();
			if (CollectionUtil.isNotEmpty(orgList)) {
				for (String id : orgList) {
					String open_department_id = id;
					FindByDepartmentUserReq req = FindByDepartmentUserReq.newBuilder()
							.departmentIdType("open_department_id")
							.departmentId(open_department_id)
							.build();
					// 获取部门用户
					FindByDepartmentUserResp userResp = client.contact().user().findByDepartment(req, RequestOptions.newBuilder().appAccessToken(tokenResp.getAppAccessToken()).build());
					//判断是否成功
					if (userResp.success()
							&& Objects.nonNull(userResp.getData())
							&& Objects.nonNull(userResp.getData().getItems())
							&& userResp.getData().getItems().length > 0){
						List<User> userList = (Arrays.stream(userResp.getData().getItems()).collect(Collectors.toList()));
						//判断是否分页token
						if (StringUtils.isNotEmpty(userResp.getData().getPageToken())) {
							userList = recursionUses(client,req,tokenResp.getAppAccessToken(),userResp.getData().getPageToken(),userList);
						}
						resultUser.addAll(userList);
					}
				}
			}
			if (CollectionUtil.isNotEmpty(resultUser)) {
				//根据openid去重
				resultUser= resultUser.stream().collect(
						Collectors.toMap(User::getOpenId, fruit -> fruit,
								(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());

				for (User user : resultUser) {
					if (StringUtils.isNotEmpty(user.getMobile())) {
						user.setMobile(handLeMobile(user.getMobile()));
					}
					UserInfo userInfo = bulidUser(user);
					String syncActionType = ConstsSynchronizers.SyncActionType.ADD;
					if (Objects.nonNull(userInfo.getId()) && userInfo.getId().intValue() != 0) {
						syncActionType = ConstsSynchronizers.SyncActionType.UPDATE;
					}
					boolean flag = false;
					String message = "成功";
					Integer success = ConstsSynchronizers.SyncResultStatus.SUCCESS;
					try {
						//入库存储
						flag = userInfoService.saveOrUpdate(userInfo);
					} catch (BusinessException e) {
						flag = false;
						message = e.getMessage();
					}
					if(!flag) {
						success = ConstsSynchronizers.SyncResultStatus.FAIL;
					}
					//记录日志
					super.historyLogs(
							userInfo, trackingUniqueId,
							ConstsSynchronizers.SyncType.USER,
							syncActionType,
							success,
							message
					);
				}
			}
		} catch (Exception e){
			log.error("error",e);
			if (e instanceof BusinessException){
				throw new BusinessException(((BusinessException) e).getCode(),e.getMessage());
			} else {
				throw new BusinessException(500,e.getMessage());
			}
		}
	}

	/**
	 * 递归获取部门
	 * @param client 客户端请求
	 * @param req 查询条件
	 * @param appAccessToken 应用tokoen
	 * @param pageToken 分页token
	 * @param list 递归部门集合
	 * @return
	 */
	private List<Department> recursionDept(Client client ,ChildrenDepartmentReq req,String appAccessToken,String pageToken, List<Department> list){
		try {
			req.setPageToken(pageToken);
			ChildrenDepartmentResp departmentResp = client.contact().department().children(req, RequestOptions.newBuilder().appAccessToken(appAccessToken).build());
			// 处理服务端错误
			if (!departmentResp.success()) {
				log.info(String.format("code:%s,msg:%s,reqId:%s"
						, departmentResp.getCode(), departmentResp.getMsg(), departmentResp.getRequestId()));
			}
			//添加返回对象
			list.addAll(Arrays.stream(departmentResp.getData().getItems()).collect(Collectors.toList()));
			//判断是否分页token
			if (StringUtils.isNotEmpty(departmentResp.getData().getPageToken())) {
				//重新迭代
				list = recursionDept(client,req,appAccessToken,departmentResp.getData().getPageToken(),list);
			}
		}catch (Exception e){
			log.error("error",e);
		}
		return list;
	}

	/**
	 * 递归获取用户
	 * @param client 客户端请求
	 * @param req 查询条件
	 * @param appAccessToken 应用tokoen
	 * @param pageToken 分页token
	 * @param list 递归用户集合
	 * @return
	 */
	private List<User> recursionUses( Client client ,FindByDepartmentUserReq req,String appAccessToken,String pageToken, List<User> list){
		try {
			req.setPageToken(pageToken);
			FindByDepartmentUserResp userResp = client.contact().user().findByDepartment(req, RequestOptions.newBuilder().appAccessToken(appAccessToken).build());
			// 处理服务端错误
			if (!userResp.success()) {
				log.info(String.format("code:%s,msg:%s,reqId:%s"
						, userResp.getCode(), userResp.getMsg(), userResp.getRequestId()));
			}
			//添加返回对象
			list.addAll(Arrays.stream(userResp.getData().getItems()).collect(Collectors.toList()));
			//判断是否分页token
			if (StringUtils.isNotEmpty(userResp.getData().getPageToken())) {
				//重新迭代
				list = recursionUses(client,req,appAccessToken,userResp.getData().getPageToken(),list);
			}
		} catch (Exception e){
			log.error("error",e);
		}
		return list;
	}

	/**
	 * 获取飞书SDK客户端对象
	 * @return
	 */
	private Client getClient (){
		Synchronizers synchronizers = getSynchronizer();
		return Client.newBuilder(synchronizers.getClientId(),synchronizers.getClientSecret())
				.marketplaceApp() // 设置 app 类型为商店应用
				.openBaseUrl(synchronizers.getBaseApi())
				.disableTokenCache()
				.requestTimeout(30, TimeUnit.SECONDS) // 设置httpclient 超时时间，默认永不超时
				.logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers,body 等信息。
				.build();
	}

	/**
	 * 构建系统组织对象
	 * @param department
	 * @return
	 */
	private Organization bulidOrgs(Department department){
		Long id = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.FEISHU,department.getOpenDepartmentId());
		Organization organization = new Organization();
		if (id.intValue() != 0) {
			organization.setId(id);
			organization.setUpdateTime(new Date());
			organization.setUpdateBy(0L);
		} else {
			organization.setCreateTime(new Date());
			organization.setCreateBy(0L);
		}
		organization.setClassify(ConstsSynchronizers.ClassifyType.DEPARTMENT);
		organization.setObjectFrom(ConstsSynchronizers.FEISHU);
		organization.setOrgName(department.getName());
		Long rootId = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.FEISHU,department.getParentDepartmentId());
		if(rootId.longValue() == 0) {
			rootId = getFeishuRootId();
		}
		organization.setParentId(rootId);
		organization.setOpenDepartmentId(department.getOpenDepartmentId());
		organization.setSortIndex(Integer.valueOf(department.getOrder()));
		if (department.getStatus().getIsDeleted()) {
			organization.setStatus(1);
		} else {
			organization.setStatus(0);
		}
		return organization;
	}

	/**
	 * 构建系统组织对象
	 * @param user
	 * @return
	 */
	private UserInfo bulidUser(User user){
		Long id = getUserIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.FEISHU,user.getOpenId());
		UserInfo userInfo = new UserInfo();
		if (id.intValue() != 0) {
			userInfo.setId(id);
			userInfo.setUpdateTime(new Date());
			userInfo.setUpdateBy(0L);
		} else {
			userInfo.setCreateTime(new Date());
			userInfo.setCreateBy(0L);
			userInfo.setPassword(randomPassword(policyPasswordService));
			userInfo.setBadPasswordCount(0);
			userInfo.setIsLocked(0);
		}
		userInfo.setOpenId(user.getOpenId());
		userInfo.setObjectFrom(ConstsSynchronizers.FEISHU);
		userInfo.setUsername(user.getOpenId());
		userInfo.setDisplayName(user.getName());
		userInfo.setMobile(handLeMobile(user.getMobile()));
		userInfo.setEmail(user.getEmail());
		userInfo.setGender(user.getGender());
		userInfo.setAvatar(user.getAvatar().getAvatar72());
		Long oneDeptID = 0L;
		//平台只用户输入一个部门
		if(user.getDepartmentIds().length >= 1) {
			oneDeptID = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.FEISHU,user.getDepartmentIds()[0]);
		}
		userInfo.setDepartmentId(oneDeptID);
		//激活的算正常，其他非正常
		if (user.getStatus().getIsActivated()) {
			userInfo.setStatus(0);
		} else {
			userInfo.setStatus(1);
		}
		return userInfo;
	}
	/**
	 * 获取根节点ID
	 * @return
	 */
	private String getRootID(){
		try {
			return getSynchronizer().getRootId();
		}catch (Exception e){
			log.error("feishu getRootId error",e);
		}
		return "0";
	}


	@Override
	public Synchronizers getSynchronizer() {
		return super.getSynchronizer();
	}

	/**
	 * 初始化组织结构
	 */
	private Long getFeishuRootId(){
		Long rootId = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.FEISHU,"feishuRootId");
		if (rootId.intValue() == 0) {
			Organization root = new Organization();
			root.setCreateTime(new Date());
			root.setCreateBy(0L);
			root.setClassify(ConstsSynchronizers.ClassifyType.ORGS);
			root.setObjectFrom(ConstsSynchronizers.FEISHU);
			root.setOrgName("飞书根节点");
			root.setOpenDepartmentId("feishuRootId");
			root.setParentId(0L);
			root.setSortIndex(1);
			root.setStatus(0);
			organizationService.saveOrg(root);
			rootId = root.getId();
			try {
				Thread.sleep(1000);
			}catch (Exception e){

			}
			//用户目录
			Organization userRoot = new Organization();
			userRoot.setCreateTime(new Date());
			userRoot.setCreateBy(0L);
			userRoot.setClassify(ConstsSynchronizers.ClassifyType.DEPARTMENT);
			userRoot.setObjectFrom(ConstsSynchronizers.FEISHU);
			userRoot.setOrgName("people");
			userRoot.setOpenDepartmentId("feishuUserRootId");
			userRoot.setParentId(rootId);
			userRoot.setSortIndex(1);
			userRoot.setStatus(0);
			organizationService.saveOrg(userRoot);



		}
		return rootId;
	}
}
