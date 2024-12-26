package com.ldapauth.synchronizer.dingtalk.pull;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiV2DepartmentGetRequest;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.request.OapiV2UserListRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.RequestOptions;
import com.lark.oapi.service.contact.v3.model.*;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.crypto.password.PasswordReciprocal;
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
import com.taobao.api.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DingtalkPullService extends AbstractSynchronizerService implements ISynchronizerPullService {

	@Autowired
	OrganizationService organizationService;

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	CacheService cacheService;

	@Autowired
	IdentifierGenerator identifierGenerator;
	@Autowired
	PasswordEncoder passwordEncoder;

	Long trackingUniqueId = 0L;

	static final String TOKEN_CACHE="SYNC:TOKEN:DINGDING:CACHE:";

	@Autowired
	PolicyPasswordService policyPasswordService;

	/**
	 * 同步入口
	 */
	public void sync() {
		trackingUniqueId = identifierGenerator.nextId("tku").longValue();
		List<Long> orgIds = syncOrgs();
		if (CollectionUtil.isNotEmpty(orgIds)) {
			syncUser(orgIds);
		}
	}
	/**
	 * 进行同步飞书组织
	 */
	private List<Long> syncOrgs(){
		Synchronizers synchronizers = getSynchronizer();
		DingTalkClient client = new DefaultDingTalkClient(synchronizers.getBaseApi()+"/topapi/v2/department/get");
		OapiV2DepartmentGetRequest req = new OapiV2DepartmentGetRequest();
		req.setDeptId(getRootID());
		req.setLanguage("zh_CN");
		List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList = new ArrayList<>();
		List<Long> ids = new ArrayList<>();
		try {
			OapiV2DepartmentGetResponse rspDepts = client.execute(req, getAccessToken());
			log.trace("response : " + rspDepts.getBody());
			if (rspDepts.isSuccess()) {
				OapiV2DepartmentListsubResponse.DeptBaseResponse rootNode =
						BeanUtil.copyProperties(rspDepts.getResult(),OapiV2DepartmentListsubResponse.DeptBaseResponse.class);
				//递归子节点
				deptList.add(rootNode);
				deptList = recursionDeptAllSub(getRootID(),deptList);
				ids = deptList.stream().map(OapiV2DepartmentListsubResponse.DeptBaseResponse::getDeptId).collect(Collectors.toList());
				for (OapiV2DepartmentListsubResponse.DeptBaseResponse response : deptList) {
					Organization organization = buildOrgs(response);
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
							message);

				}
			}
		}catch (ApiException e) {
			log.error("error",e);
		}
		return ids;
	}

	/**
	 * 递归部门
	 * @param deptId
	 * @param deptList
	 * @return
	 * @throws com.taobao.api.ApiException
	 */
	private List<OapiV2DepartmentListsubResponse.DeptBaseResponse> recursionDeptAllSub(Long deptId,List<OapiV2DepartmentListsubResponse.DeptBaseResponse> deptList) throws com.taobao.api.ApiException {
		DingTalkClient client = new DefaultDingTalkClient(synchronizer.getBaseApi() + "/topapi/v2/department/listsub");
		OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
		req.setDeptId(deptId);
		req.setLanguage("zh_CN");
		OapiV2DepartmentListsubResponse rspDepts = client.execute(req, getAccessToken());
		log.trace("response : " + rspDepts.getBody());
		if (rspDepts.isSuccess() && !rspDepts.getResult().isEmpty() && rspDepts.getResult().size() >0) {
			for (OapiV2DepartmentListsubResponse.DeptBaseResponse response : rspDepts.getResult()) {
				deptList.add(response);
				deptId = response.getDeptId();
				//继续递归
				recursionDeptAllSub(deptId,deptList);
			}
		}
		return deptList;
	}

	/**
	 * 获取token
	 * @return
	 */
	private String getAccessToken(){
		String key = TOKEN_CACHE + trackingUniqueId + getSynchronizer().getClientId();
		try {
			String accessToken = cacheService.getCacheObject(key);
			if(StringUtils.isNotEmpty(accessToken)) {
				log.debug("dingding appid:[{},cache token:[{}]",getSynchronizer().getClientId(),accessToken);
				return accessToken;
			}
			DingTalkClient client = new DefaultDingTalkClient(getSynchronizer().getBaseApi()+"/gettoken");
			OapiGettokenRequest request = new OapiGettokenRequest();
			request.setAppkey(getSynchronizer().getClientId());
			request.setAppsecret(getSynchronizer().getClientSecret());
			request.setHttpMethod("GET");
			OapiGettokenResponse response = client.execute(request);
			log.info("response : " + response.getBody());
			if (response.getErrcode() == 0) {
				//根据应用凭证，缓存钉钉accessToken30分钟
				cacheService.setCacheObject(key,response.getAccessToken());
				return response.getAccessToken();
			}
			return null;
		} catch (Exception e) {
			log.error("error", e);
		}
		return null;
	}

	/**
	 * 同步用户
	 * @param orgList 部门id集合
	 */
	public void syncUser(List<Long> orgList){
		List<OapiV2UserListResponse.ListUserResponse> userList = new ArrayList<>();
		Map<String,String> unionidMap = new HashMap<>();
		for (Long deptId : orgList) {
			try {
				DingTalkClient client = new DefaultDingTalkClient(getSynchronizer().getBaseApi() + "/topapi/v2/user/list");
				OapiV2UserListRequest req = new OapiV2UserListRequest();
				log.debug("DingTalk deptId : {}",deptId);
				req.setDeptId(deptId);
				req.setCursor(0L);
				req.setSize(100L);
				req.setOrderField("modify_desc");
				req.setContainAccessLimit(true);
				req.setLanguage("zh_CN");
				OapiV2UserListResponse rsp = client.execute(req, getAccessToken());
				log.trace("response : {}", rsp.getBody());
				if (rsp.isSuccess() && !rsp.getResult().getList().isEmpty() && rsp.getResult().getList().size() > 0) {
					for(OapiV2UserListResponse.ListUserResponse response : rsp.getResult().getList()) {
						String unionid = response.getUnionid();
						//用户去重，防止多部门用户存在多个
						if (unionidMap.containsKey(unionid)) {
							continue;
						} else {
							unionidMap.put(unionid,unionid);
						}
						userList.add(response);
					}
				}
			} catch (org.apache.kafka.common.errors.ApiException | com.taobao.api.ApiException e){
				log.error("Sync getUser ApiException .",e);
			}
		}

		if (CollectionUtil.isNotEmpty(userList)) {
			Map<String,UserInfo> userMap = getUserMapByObjectFrom(ConstsSynchronizers.DINGDING);
			//批处理用户
			batchUser(userList,userMap);
		}
	}

	/**
	 * 批处理用户，批量更新或者新增
	 * @param userList
	 * @param userMap
	 */
	private void batchUser(List<OapiV2UserListResponse.ListUserResponse> userList,Map<String,UserInfo> userMap){
		List<UserInfo> batchAdd = new ArrayList<>();
		List<UserInfo> batchUpdate = new ArrayList<>();
		//定义需要删除的用户集合,根据openid比对数据库记录
		List<UserInfo> batchDelete = new ArrayList<>();
		List<String> allOpenIds = new ArrayList<>();
		String defPassword = ConstsSynchronizers.DEF_PASSWORD;
		String password = passwordEncoder.encode(defPassword);
		String decipherable = PasswordReciprocal.getInstance().encode(defPassword);

		for (OapiV2UserListResponse.ListUserResponse user : userList) {
			allOpenIds.add(user.getUnionid());
			//判断用户是否存在
			UserInfo oldUserInfo = userMap.get(user.getUnionid());
			UserInfo userInfo = buildUser(user,oldUserInfo);
			if (Objects.nonNull(oldUserInfo)) {
				batchUpdate.add(userInfo);
			} else {
				userInfo.setPassword(password);
				userInfo.setDecipherable(decipherable);
				userInfo.setPasswordLastSetTime(new Date());
				userInfo.setBadPasswordCount(0);
				batchAdd.add(userInfo);
			}
		}
		//批处理用户存储,1000提交一次
		if (CollectionUtil.isNotEmpty(batchAdd)){
			userInfoService.saveBatch(batchAdd,1000);
			for (UserInfo userInfo : batchAdd) {
				//记录日志
				super.historyLogs(
						userInfo,
						trackingUniqueId,
						ConstsSynchronizers.SyncType.USER,
						ConstsSynchronizers.SyncActionType.ADD,
						ConstsSynchronizers.SyncResultStatus.SUCCESS,
						"新增成功"
				);
				userMap.put(userInfo.getOpenId(),userInfo);
			}
		}
		//批处理用户,1000提交一次
		if (CollectionUtil.isNotEmpty(batchUpdate)){
			userInfoService.updateBatchById(batchUpdate,1000);
			for (UserInfo userInfo : batchUpdate) {
				//记录日志
				super.historyLogs(
						userInfo,
						trackingUniqueId,
						ConstsSynchronizers.SyncType.USER,
						ConstsSynchronizers.SyncActionType.UPDATE,
						ConstsSynchronizers.SyncResultStatus.SUCCESS,
						"更新成功"
				);
			}
		}
		//遍历数据库，不存在的进行删除
		for (String key : userMap.keySet()) {
			UserInfo userInfo = userMap.get(key);
			if (StringUtils.isEmpty(userInfo.getOpenId())) {
				batchDelete.add(userInfo);
				continue;
			}
			//如果openid在数据库不存在，则进行删除
			if (!allOpenIds.contains(userInfo.getOpenId())) {
				batchDelete.add(userInfo);
			}
		}

		//批处理用户,1000提交一次
		if (CollectionUtil.isNotEmpty(batchDelete)){
			userInfoService.removeBatchByIds(batchDelete);
			for (UserInfo userInfo : batchDelete) {
				//记录日志
				super.historyLogs(
						userInfo,
						trackingUniqueId,
						ConstsSynchronizers.SyncType.USER,
						ConstsSynchronizers.SyncActionType.DELETE,
						ConstsSynchronizers.SyncResultStatus.SUCCESS,
						"删除成功"
				);
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
	private Organization buildOrgs(OapiV2DepartmentListsubResponse.DeptBaseResponse department){
		Long id = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.DINGDING,String.valueOf(department.getDeptId()));
		Organization organization = new Organization();
		if (id.intValue() != 0) {
			organization.setId(id);
			organization.setUpdateTime(new Date());
			organization.setUpdateBy(0L);
		} else {
			organization.setCreateTime(new Date());
			organization.setCreateBy(0L);
		}
		Long rootId = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.DINGDING,String.valueOf(department.getParentId()));
		if(rootId.longValue() == 0) {
			rootId = getDingDingRootId();
		}
		organization.setParentId(rootId);
		organization.setClassify(ConstsSynchronizers.ClassifyType.DEPARTMENT);
		organization.setObjectFrom(ConstsSynchronizers.DINGDING);
		organization.setOrgName(department.getName());
		organization.setOpenDepartmentId(String.valueOf(department.getDeptId()));
		organization.setSortIndex(100);
		organization.setStatus(0);
		return organization;
	}

	/**
	 * 构建系统组织对象
	 * @param user
	 * @return
	 */
	private UserInfo buildUser(OapiV2UserListResponse.ListUserResponse user,UserInfo oldUserInfo){
		UserInfo userInfo = new UserInfo();
		if (Objects.nonNull(oldUserInfo)) {
			userInfo.setId(oldUserInfo.getId());
			userInfo.setUpdateTime(new Date());
			userInfo.setUpdateBy(0L);
		} else {
			userInfo.setCreateTime(new Date());
			userInfo.setCreateBy(0L);
			userInfo.setBadPasswordCount(0);
			userInfo.setIsLocked(0);
			userInfo.setUpdateTime(new Date());
			userInfo.setUpdateBy(0L);
		}
		userInfo.setOpenId(user.getUnionid());
		userInfo.setObjectFrom(ConstsSynchronizers.DINGDING);
		userInfo.setUsername(user.getUnionid());
		userInfo.setDisplayName(user.getName());
		userInfo.setMobile(handLeMobile(user.getMobile()));
		userInfo.setEmail(user.getEmail());
		userInfo.setGender(0);
		userInfo.setAvatar(user.getAvatar());
		Long oneDeptID = 0L;
		//平台只用户输入一个部门
		if(user.getDeptIdList().size()>= 1) {
			oneDeptID = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.DINGDING,String.valueOf(user.getDeptIdList().get(0)));
		}
		userInfo.setDepartmentId(oneDeptID);
		userInfo.setStatus(0);
		return userInfo;
	}
	/**
	 * 获取根节点ID
	 * @return
	 */
	private Long getRootID(){
		try {
			return Long.valueOf(getSynchronizer().getRootId());
		}catch (Exception e){
			log.error("getRootId error",e);
		}
		return 1L;
	}


	@Override
	public Synchronizers getSynchronizer() {
		return super.getSynchronizer();
	}

	/**
	 * 初始化组织结构
	 */
	private Long getDingDingRootId(){
		Long rootId = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.DINGDING,"dingdingRootId");
		if (rootId.intValue() == 0) {
			Organization root = new Organization();
			root.setCreateTime(new Date());
			root.setCreateBy(0L);
			root.setClassify(ConstsSynchronizers.ClassifyType.ORGS);
			root.setObjectFrom(ConstsSynchronizers.DINGDING);
			root.setOpenDepartmentId("dingdingRootId");
			root.setOrgName("钉钉根节点");
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
			userRoot.setObjectFrom(ConstsSynchronizers.DINGDING);
			userRoot.setOrgName("people");
			userRoot.setOpenDepartmentId("dingdingUserRootId");
			userRoot.setParentId(rootId);
			userRoot.setSortIndex(1);
			userRoot.setStatus(0);
			organizationService.saveOrg(userRoot);
		}
		return rootId;
	}
}
