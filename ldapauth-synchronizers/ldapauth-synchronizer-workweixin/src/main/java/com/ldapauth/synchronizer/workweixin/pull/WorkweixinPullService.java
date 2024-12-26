package com.ldapauth.synchronizer.workweixin.pull;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.ldapauth.cache.CacheService;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.crypto.password.PasswordReciprocal;
import com.ldapauth.domain.WorkweixinDeptment;
import com.ldapauth.domain.WorkweixinUser;
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
import com.ldapauth.utils.WorkWeixinClient;
import com.ldapauth.utils.responce.WorkWeixinDeptmentResponce;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WorkweixinPullService extends AbstractSynchronizerService implements ISynchronizerPullService {

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

	@Autowired
	PasswordEncoder passwordEncoder;
	/**
	 * 同步入口
	 */
	public void sync() {
		trackingUniqueId = identifierGenerator.nextId("tku").longValue();
		List<Integer> orgIds = syncOrgs();
		if (CollectionUtil.isNotEmpty(orgIds)) {
			syncUser(orgIds);
		}
	}

	/**
	 * 进行同步飞书组织
	 */
	private List<Integer> syncOrgs() {
		Synchronizers synchronizers = getSynchronizer();
		List<Integer> ids = new ArrayList<>();
		WorkWeixinClient client = WorkWeixinClient
				.newBuilder(synchronizers.getClientId(),
						synchronizers.getUserClientSecret(),
						synchronizers.getClientSecret(),
						synchronizers.getBaseApi())
				.build();
		int maxOrder = 0;
		//通讯录token用于创建和修改信息的凭证
		String userAccessToken = client.getUserAccessToken();
		if (StringUtils.isBlank(userAccessToken)) {
			log.error("获取通讯录token失败...");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"获取通讯录token失败");
		}
		//APP应用token查询部门列表，需要设置应用可见范围,用于查询部门列表
		String appToken = client.getAppToken();
		if (StringUtils.isBlank(appToken)) {
			log.error("获取应用token失败...");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"获取应用token失败");
		}
		WorkWeixinDeptmentResponce deptmentResponce = client.deptment().token(appToken).departmentsList();
		log.debug("get dept:{}",deptmentResponce);
		if (Objects.nonNull(deptmentResponce) && deptmentResponce.getErrcode() == 0) {
			ids = deptmentResponce.getDepartment().stream().map(WorkweixinDeptment::getId).collect(Collectors.toList());
			List<WorkweixinDeptment> deptments = deptmentResponce.getDepartment();
			//根据ID排序
			Collections.sort(deptments, (o1, o2) -> o1.getId() - o2.getId());
			//处理排序
			orders(deptments);
			for (WorkweixinDeptment deptment : deptments) {
				Organization organization = buildOrgs(deptment);
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
		} else {
			log.error("获取部门失败...");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"获取部门失败");
		}
		return ids;
	}

	/**
	 * 处理排序
	 * @param deptments
	 */
	private void orders(List<WorkweixinDeptment> deptments){
		//根据order,越大越靠前排序
		Collections.sort(deptments, (o1, o2) -> o2.getOrder() - o1.getOrder());
		int i = 0;
		//重新排序
		for(WorkweixinDeptment deptment : deptments) {
			i++;
			deptment.setOrder(i);
		}
		//重新根据ID排序，防止上下级丢失
		Collections.sort(deptments, (o1, o2) -> o1.getId() - o2.getId());
	}
	/**
	 * 同步用户
	 *
	 * @param orgList 部门id集合
	 */
	public void syncUser(List<Integer> orgList) {
		Synchronizers synchronizers = getSynchronizer();

		WorkWeixinClient client = WorkWeixinClient
				.newBuilder(synchronizers.getClientId(),
						synchronizers.getUserClientSecret(),
						synchronizers.getClientSecret(),
						synchronizers.getBaseApi())
				.build();
		//通讯录token用于创建和修改信息的凭证
		String userAccessToken = client.getUserAccessToken();
		if (StringUtils.isBlank(userAccessToken)) {
			log.error("获取通讯录token失败...");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"获取通讯录token失败");
		}
		//APP应用token查询部门列表，需要设置应用可见范围,用于查询部门列表
		String appToken = client.getAppToken();
		if (StringUtils.isBlank(appToken)) {
			log.error("获取应用token失败...");
			throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"获取应用token失败");
		}
		List<WorkweixinUser> userList = client.deptment().token(appToken).userList(orgList);
		//根据userid去重
		userList = userList.stream().collect(
				Collectors.toMap(WorkweixinUser::getUserid, fruit -> fruit,
						(existing, replacement) -> existing)).values().stream().collect(Collectors.toList());

		if (CollectionUtil.isNotEmpty(userList)) {
			Map<String,UserInfo> userMap = getUserMapByObjectFrom(ConstsSynchronizers.WORKWEIXIN);
			batchUser(userList,userMap);
		}
	}

	/**
	 * 批处理用户，批量更新或者新增
	 * @param userList
	 * @param userMap
	 */
	private void batchUser(List<WorkweixinUser> userList, Map<String,UserInfo> userMap){
		List<UserInfo> batchAdd = new ArrayList<>();
		List<UserInfo> batchUpdate = new ArrayList<>();
		//定义需要删除的用户集合,根据openid比对数据库记录
		List<UserInfo> batchDelete = new ArrayList<>();
		List<String> allOpenIds = new ArrayList<>();
		String defPassword = ConstsSynchronizers.DEF_PASSWORD;
		String password = passwordEncoder.encode(defPassword);
		String decipherable = PasswordReciprocal.getInstance().encode(defPassword);
		for (WorkweixinUser user : userList) {
			allOpenIds.add(user.getUserid());
			//判断用户是否存在
			UserInfo oldUserInfo = userMap.get(user.getUserid());
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
	 * 构建系统组织对象
	 *
	 * @param department
	 * @return
	 */
	private Organization buildOrgs(WorkweixinDeptment department) {
		Long id = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.WORKWEIXIN,String.valueOf(department.getId()));
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
		organization.setObjectFrom(ConstsSynchronizers.WORKWEIXIN);
		organization.setOrgName(department.getName());
		Long rootId = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.WORKWEIXIN,String.valueOf(department.getParentid()));
		if(rootId.longValue() == 0) {
			rootId = getWorkweixinRootId();
		}
		organization.setParentId(rootId);
		organization.setOpenDepartmentId(String.valueOf(department.getId()));
		organization.setSortIndex(department.getOrder());
		organization.setStatus(0);
		return organization;
	}

	/**
	 * 构建系统组织对象
	 *
	 * @param user
	 * @return
	 */
	private UserInfo buildUser(WorkweixinUser user,UserInfo oldUserInfo) {
		UserInfo userInfo = new UserInfo();
		if (Objects.nonNull(oldUserInfo)) {
			userInfo.setId(oldUserInfo.getId());
			userInfo.setUpdateTime(new Date());
			userInfo.setUpdateBy(0L);
		} else {
			userInfo.setUpdateTime(new Date());
			userInfo.setUpdateBy(0L);
			userInfo.setCreateTime(new Date());
			userInfo.setCreateBy(0L);
			userInfo.setBadPasswordCount(0);
			userInfo.setIsLocked(0);
		}
		userInfo.setOpenId(user.getUserid());
		userInfo.setObjectFrom(ConstsSynchronizers.WORKWEIXIN);
		userInfo.setUsername(user.getUserid());
		userInfo.setDisplayName(user.getName());
		userInfo.setMobile(handLeMobile(user.getMobile()));
		userInfo.setEmail(user.getEmail());
		userInfo.setGender(0);
		userInfo.setAvatar(user.getAvatar());
		Long oneDeptID = 0L;
		//平台只用户输入一个部门
		if (user.getDepartment().size() >= 1) {
			oneDeptID = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.WORKWEIXIN,String.valueOf(user.getDepartment().get(0)));
		}
		userInfo.setDepartmentId(oneDeptID);
		if(user.getStatus()==1) {
			userInfo.setStatus(0);
		} else {
			userInfo.setStatus(1);
		}
		return userInfo;
	}

	/**
	 * 获取根节点ID
	 *
	 * @return
	 */
	private Long getRootID() {
		try {
			return Long.valueOf(getSynchronizer().getRootId());
		} catch (Exception e) {
			log.error("getRootId error", e);
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
	private Long getWorkweixinRootId(){
		Long rootId = getOrgIdByOpenDepartmentId(trackingUniqueId,ConstsSynchronizers.WORKWEIXIN,"workweixinRootId");
		if (rootId.intValue() == 0) {
			Organization root = new Organization();
			root.setCreateTime(new Date());
			root.setCreateBy(0L);
			root.setClassify(ConstsSynchronizers.ClassifyType.ORGS);
			root.setObjectFrom(ConstsSynchronizers.WORKWEIXIN);
			root.setOpenDepartmentId("workweixinRootId");
			root.setOrgName("企业微信根节点");
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
			userRoot.setObjectFrom(ConstsSynchronizers.WORKWEIXIN);
			userRoot.setOrgName("people");
			userRoot.setOpenDepartmentId("workweixinUserRootId");
			userRoot.setParentId(rootId);
			userRoot.setSortIndex(1);
			userRoot.setStatus(0);
			organizationService.saveOrg(userRoot);
		}
		return rootId;
	}
}
