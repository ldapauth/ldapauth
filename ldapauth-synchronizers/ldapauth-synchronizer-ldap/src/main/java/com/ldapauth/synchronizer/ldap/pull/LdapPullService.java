package com.ldapauth.synchronizer.ldap.pull;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.crypto.password.PasswordReciprocal;
import com.ldapauth.persistence.service.GroupMemberService;
import com.ldapauth.persistence.service.GroupService;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.synchronizer.abstracts.AbstractSynchronizerService;
import com.ldapauth.synchronizer.ISynchronizerPullService;
import com.ldapauth.synchronizer.ldap.domain.LdapGroup;
import com.ldapauth.synchronizer.ldap.domain.LdapOrg;
import com.ldapauth.synchronizer.ldap.domain.LdapUser;
import com.ldapauth.synchronizer.ldap.utils.FormatUtil;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;

@Slf4j
@Service
public class LdapPullService extends AbstractSynchronizerService implements ISynchronizerPullService {

	@Autowired
	OrganizationService organizationService;

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	GroupService groupService;

	@Autowired
	GroupMemberService groupMemberService;

	@Autowired
	IdentifierGenerator identifierGenerator;


	Long trackingUniqueId = 0L;

	Map<String, String> entryUUIDMap = null;

	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * 同步入口
	 */
	public void sync() {
		entryUUIDMap = new HashMap<>();
		trackingUniqueId = identifierGenerator.nextId("tku").longValue();
		Synchronizers synchronizers = getSynchronizer();
		DirContext dirContext = null;
		try {
			LdapUtils utils = new LdapUtils(
					synchronizers.getBaseApi(),
					synchronizers.getClientId(),
					synchronizers.getClientSecret(),
					synchronizers.getBaseDn());
			dirContext = utils.openConnection();
			if (Objects.nonNull(dirContext)) {
				syncOrgs(dirContext);
				syncUser(dirContext);
				syncGroup(dirContext);
			}
		}catch (Exception e){
			log.error("error",e);
		} finally {
			if (Objects.nonNull(dirContext)) {
				try { dirContext.close(); } catch (Exception e){}
			}
			if (Objects.nonNull(entryUUIDMap)) {
				//清空内存
				entryUUIDMap.clear();
			}
		}
	}
	/**
	 * 进行同步飞书组织
	 */
	private void syncOrgs(DirContext dirContext){
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(objectClass=OrganizationalUnit))";
		if (StringUtils.isNotBlank(getSynchronizer().getOrgFilter())) {
			filter = getSynchronizer().getOrgFilter();
		}
		log.info("filter:{}",filter);
		//查询所有属性
		String[] attrts = new String[]{"*","+"};
		constraints.setReturningAttributes(attrts);
		try {
			NamingEnumeration<SearchResult> results =
					dirContext.search(getSynchronizer().getBaseDn(), filter, constraints);
			List<LdapOrg> orgsList = new ArrayList<>();
			long recordCount = 0;
			while (null != results && results.hasMoreElements()) {
				Object obj = results.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult sr = (SearchResult) obj;
					if (StringUtils.isEmpty(sr.getName())) {
						log.info("Skip '' or 'OU=Domain Controllers' .");
						continue;
					}
					log.debug("Sync OrganizationalUnit {} , name [{}] , NameInNamespace [{}]",
							(++recordCount), sr.getName(), sr.getNameInNamespace());
					HashMap<String, Attribute> attributeMap = new HashMap<>();
					NamingEnumeration<? extends Attribute> attrs = sr.getAttributes().getAll();
					while (null != attrs && attrs.hasMoreElements()) {
						Attribute objAttrs = attrs.nextElement();
						log.trace("attribute {} : {}",
								objAttrs.getID(),
								LdapUtils.getAttrStringValue(objAttrs)
						);
						attributeMap.put(objAttrs.getID().toLowerCase(), objAttrs);
					}
					LdapOrg ldapOrg = FormatUtil.formatOrg(entryUUIDMap,attributeMap, sr.getName(), sr.getNameInNamespace());
					if (Objects.nonNull(ldapOrg)) {
						orgsList.add(ldapOrg);
					}
				}
			}
			if (CollectionUtil.isNotEmpty(orgsList)) {
				//转Map
				for (LdapOrg ldapOrg : orgsList) {
					String id = ldapOrg.getNameInNamespace();
					String entryUUID = ldapOrg.getEntryUUID();
					entryUUIDMap.put(id, entryUUID);
				}
				//处理id和parentId
				for (LdapOrg ldapOrg : orgsList) {
					String parentId = ldapOrg.getParentNameInNamespace();
					if (entryUUIDMap.containsKey(parentId)) {
						ldapOrg.setParentEntryUUID(entryUUIDMap.get(parentId));
					} else {
						ldapOrg.setParentEntryUUID(null);
					}
				}

				//根据层级排序从小到大
				Collections.sort(orgsList, (o1, o2) -> {
					int order1 = o1.getOrder();
					int order2 = o2.getOrder();
					return order1 - order2;
				});
				for (LdapOrg ldapOrg : orgsList) {
					Organization organization = buildOrgs(ldapOrg);
					String syncActionType = ConstsSynchronizers.SyncActionType.ADD;
					if (Objects.nonNull(organization.getId()) && organization.getId().intValue() != 0) {
						syncActionType = ConstsSynchronizers.SyncActionType.UPDATE;
					}
					//入库存储
					Result<String> result = organizationService.saveOrg(organization);
					Integer success = ConstsSynchronizers.SyncResultStatus.SUCCESS;
					String message = "成功";
					if(!result.isSuccess()) {
						success = ConstsSynchronizers.SyncResultStatus.FAIL;
						message = result.getMessage();
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
		} catch (Exception e) {
			log.error("同步LDAP异常");
		}

	}



	/**
	 * 同步用户
	 */
	public void syncUser(DirContext dirContext){
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(objectClass=inetOrgPerson))";
		if (StringUtils.isNotBlank(getSynchronizer().getUserFilter())) {
			filter = getSynchronizer().getUserFilter();
		}
		log.info("filter:{}",filter);
		//查询所有属性
		String[] attrts = new String[]{"*","+"};
		constraints.setReturningAttributes(attrts);
		try {
			NamingEnumeration<SearchResult> results =
					dirContext.search(getSynchronizer().getBaseDn(), filter, constraints);
			List<LdapUser> userList = new ArrayList<>();
			long recordCount = 0;
			while (null != results && results.hasMoreElements()) {
				Object obj = results.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult sr = (SearchResult) obj;
					if (StringUtils.isEmpty(sr.getName())) {
						log.info("Skip '' or 'OU=Domain Controllers' .");
						continue;
					}
					log.debug("Sync inetOrgPerson {} , name [{}] , NameInNamespace [{}]",
							(++recordCount), sr.getName(), sr.getNameInNamespace());
					HashMap<String, Attribute> attributeMap = new HashMap<>();
					NamingEnumeration<? extends Attribute> attrs = sr.getAttributes().getAll();
					while (null != attrs && attrs.hasMoreElements()) {
						Attribute objAttrs = attrs.nextElement();
						log.trace("attribute {} : {}",
								objAttrs.getID(),
								LdapUtils.getAttrStringValue(objAttrs)
						);
						attributeMap.put(objAttrs.getID().toLowerCase(), objAttrs);
					}
					LdapUser ldapUser = FormatUtil.formatUser(entryUUIDMap,attributeMap, sr.getName(), sr.getNameInNamespace());
					//过滤默认用户
					if(ldapUser.getUid().contains("dummy")) {
						continue;
					}
					if (Objects.nonNull(ldapUser)) {
						userList.add(ldapUser);
					}
				}
			}
			if (CollectionUtil.isNotEmpty(userList)) {
				Map<String,UserInfo> userMap = getUserMapByObjectFrom(ConstsSynchronizers.OPEN_LDAP);
				//批处理用户
				batchUser(userList,userMap);
			}
		} catch (Exception e) {
			log.error("同步LDAP异常");
		}
	}

	/**
	 * 批处理用户，批量更新或者新增
	 * @param userList
	 * @param userMap
	 */
	private void batchUser(List<LdapUser> userList, Map<String,UserInfo> userMap){
		List<UserInfo> batchAdd = new ArrayList<>();
		List<UserInfo> batchUpdate = new ArrayList<>();
		//定义需要删除的用户集合,根据openid比对数据库记录
		List<UserInfo> batchDelete = new ArrayList<>();
		List<String> allOpenIds = new ArrayList<>();
		String defPassword = ConstsSynchronizers.DEF_PASSWORD;
		String password = passwordEncoder.encode(defPassword);
		String decipherable = PasswordReciprocal.getInstance().encode(defPassword);

		for (LdapUser user : userList) {
			allOpenIds.add(user.getEntryUUID());
			//判断用户是否存在
			UserInfo oldUserInfo = userMap.get(user.getEntryUUID());
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
	 * 同步组
	 */
	public void syncGroup(DirContext dirContext){
		SearchControls constraints = new SearchControls();
		constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(objectClass=groupOfNames))";
		if (StringUtils.isNotBlank(getSynchronizer().getGroupFilter())) {
			filter = getSynchronizer().getGroupFilter();
		}
		log.info("filter:{}",filter);
		//查询所有属性
		String[] attrts = new String[]{"*","+"};
		constraints.setReturningAttributes(attrts);
		try {
			NamingEnumeration<SearchResult> results =
					dirContext.search(getSynchronizer().getBaseDn(), filter, constraints);
			List<LdapGroup> groupList = new ArrayList<>();
			long recordCount = 0;
			while (null != results && results.hasMoreElements()) {
				Object obj = results.nextElement();
				if (obj instanceof SearchResult) {
					SearchResult sr = (SearchResult) obj;
					if (StringUtils.isEmpty(sr.getName())) {
						log.info("Skip '' or 'OU=Domain Controllers' .");
						continue;
					}
					log.debug("Sync groupOfNames {} , name [{}] , NameInNamespace [{}]",
							(++recordCount), sr.getName(), sr.getNameInNamespace());
					HashMap<String, Attribute> attributeMap = new HashMap<>();
					NamingEnumeration<? extends Attribute> attrs = sr.getAttributes().getAll();
					while (null != attrs && attrs.hasMoreElements()) {
						Attribute objAttrs = attrs.nextElement();
						log.trace("attribute {} : {}",
								objAttrs.getID(),
								LdapUtils.getAttrStringValue(objAttrs)
						);
						attributeMap.put(objAttrs.getID().toLowerCase(), objAttrs);
					}
					LdapGroup ldapGroup = FormatUtil.formatGroup(attributeMap, sr.getName(), sr.getNameInNamespace());

					if (Objects.nonNull(ldapGroup)) {
						//设置组成员
						ldapGroup.setMembers(FormatUtil.getMembers(dirContext, getSynchronizer().getBaseDn(),ldapGroup.getCn()));
						groupList.add(ldapGroup);
					}
				}
			}
			if (CollectionUtil.isNotEmpty(groupList)) {
				for (LdapGroup ldapGroup : groupList) {
					Group group = buildGroup(ldapGroup);
					String syncActionType = ConstsSynchronizers.SyncActionType.ADD;
					if (Objects.nonNull(group.getId()) && group.getId().intValue() != 0) {
						syncActionType = ConstsSynchronizers.SyncActionType.UPDATE;
					}
					//入库存储
					boolean flag = groupService.saveOrUpdate(group);
					//组成员关系
					if (CollectionUtil.isNotEmpty(ldapGroup.getMembers())) {
						IdsDTO idsDTO = new IdsDTO();
						idsDTO.setIds(Arrays.asList(group.getId()));
						List<Long> targetIds = new ArrayList<>();
						for (String uid : ldapGroup.getMembers()) {
							UserInfo userInfo = userInfoService.getUserByLdapDn(uid);
							if (Objects.nonNull(userInfo)) {
								targetIds.add(userInfo.getId());
							}
						}
						if (CollectionUtil.isNotEmpty(targetIds)) {
							idsDTO.setTargetIds(targetIds);
							//入库组和成员关系
							groupMemberService.addMember(idsDTO, 0L, new Date());
						}
					}
					Integer success = ConstsSynchronizers.SyncResultStatus.SUCCESS;
					String message = "成功";
					if(!flag) {
						success = ConstsSynchronizers.SyncResultStatus.FAIL;
						message = "失败";
					}
					//记录日志
					super.historyLogs(
							group,
							trackingUniqueId,
							ConstsSynchronizers.SyncType.GROUP,
							syncActionType,
							success,
							message
					);
				}

			}
		} catch (Exception e) {
			log.error("同步LDAP异常");
		}
	}



	/**
	 * 构建系统组织对象
	 * @param department
	 * @return
	 */
	private Organization buildOrgs(LdapOrg department){
		Long id = getOrgIdByLdapId(trackingUniqueId,department.getEntryUUID());
		Organization organization = new Organization();
		if (id.intValue() != 0) {
			organization.setId(id);
			organization.setUpdateTime(new Date());
			organization.setUpdateBy(0L);
		} else {
			organization.setCreateTime(new Date());
			organization.setCreateBy(0L);
			organization.setObjectFrom(ConstsSynchronizers.OPEN_LDAP);
			organization.setClassify(ConstsSynchronizers.ClassifyType.DEPARTMENT);
		}
		organization.setLdapDn(department.getNameInNamespace());
		organization.setOrgName(department.getOu());
		organization.setParentId(getOrgIdByLdapId(trackingUniqueId,department.getParentEntryUUID()));
		organization.setOpenDepartmentId(department.getEntryUUID());
		organization.setSortIndex(Integer.valueOf(department.getOrder()));
		organization.setLdapId(department.getEntryUUID());
		organization.setStatus(0);
		return organization;
	}
	/**
	 * 构建系统组织对象
	 * @param user
	 * @return
	 */
	private UserInfo buildUser(LdapUser user,UserInfo oldUserInfo){
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
		userInfo.setOpenId(user.getEntryUUID());
		userInfo.setObjectFrom(ConstsSynchronizers.OPEN_LDAP);
		userInfo.setUsername(user.getUid());
		userInfo.setDisplayName(user.getDisplayName());
		userInfo.setMobile(handLeMobile(user.getMobile()));
		userInfo.setEmail(user.getEmail());
		Long oneDeptID = 0L;
		//平台只用户输入一个部门
		if(StringUtils.isNotEmpty(user.getParentEntryUUID())) {
			oneDeptID = getOrgIdByLdapId(trackingUniqueId,user.getParentEntryUUID());
		}
		userInfo.setDepartmentId(oneDeptID);
		userInfo.setLdapDn(user.getNameInNamespace());
		userInfo.setLdapId(user.getEntryUUID());
		userInfo.setStatus(0);
		return userInfo;
	}


	/**
	 * 构建系统组对象
	 * @param ldapGroup
	 * @return
	 */
	private Group buildGroup(LdapGroup ldapGroup){
		Long id = getGroupIdByLdapId(trackingUniqueId,ldapGroup.getEntryUUID());
		Group group = new Group();
		if (id.intValue() != 0) {
			group.setId(id);
			group.setUpdateTime(new Date());
			group.setUpdateBy(0L);
		} else {
			group.setCreateTime(new Date());
			group.setCreateBy(0L);
			group.setOpenId(ldapGroup.getEntryUUID());
			group.setObjectFrom(ConstsSynchronizers.OPEN_LDAP);
		}
		group.setName(ldapGroup.getCn());
		group.setLdapDn(ldapGroup.getNameInNamespace());
		group.setLdapId(ldapGroup.getEntryUUID());
		group.setStatus(0);
		return group;
	}

	@Override
	public Synchronizers getSynchronizer() {
		return super.getSynchronizer();
	}
}
