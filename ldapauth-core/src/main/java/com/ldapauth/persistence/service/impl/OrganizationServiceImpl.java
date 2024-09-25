package com.ldapauth.persistence.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.enums.OrgsBusinessExceptionEnum;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.mapper.OrganizationMapper;
import com.ldapauth.persistence.mapper.UserInfoMapper;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.persistence.service.SynchronizersService;
import com.ldapauth.pojo.dto.IdsListDto;
import com.ldapauth.pojo.dto.OrganizationQueryDTO;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.provision.ProvisionAction;
import com.ldapauth.provision.ProvisionService;
import com.ldapauth.provision.ProvisionTopic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Autowired
    IdentifierGenerator identifierGenerator;
    @Autowired
    OrganizationMapper organizationMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    SynchronizersService synchronizersService;

    @Override
    public Page<Organization> fetch(OrganizationQueryDTO queryDTO) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        //所选中的部门ID
        Long id = queryDTO.getId();
        if (Objects.nonNull(queryDTO.getId())) {
            wrapper.and(queryWrapper ->
                    queryWrapper
                            .eq(Organization::getId, id)
                            .or()
                            .like(Organization::getIdPath, id));
        }
        if (Objects.nonNull(queryDTO.getOrgName())) {
            wrapper.like(Organization::getOrgName, queryDTO.getOrgName());
        }
        //数据来源
        if (Objects.nonNull(queryDTO.getObjectFrom())) {
            wrapper.eq(Organization::getObjectFrom,queryDTO.getObjectFrom());
        }

        wrapper.orderByDesc(Organization::getCreateTime);
        return super.page(queryDTO.build(),wrapper);
    }



    @Override
    public Organization getOrganizationByOpenDepartmentId(String objectFrom,String openDepartmentId) {
        LambdaQueryWrapper<Organization> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Organization::getOpenDepartmentId,openDepartmentId);
        lambdaQueryWrapper.eq(Organization::getObjectFrom,objectFrom);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    public Organization getOrganizationByLdapId(String ldapId) {
        LambdaQueryWrapper<Organization> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Organization::getLdapId,ldapId);
        return super.getOne(lambdaQueryWrapper);
    }

    @Override
    @Transactional
    public Result<String> saveOrg(Organization organization) {
        if (Objects.nonNull(organization.getId())) {
            return editOrg(organization);
        }

        //新增时检查是否有同名的组织
        saveCheckDuplicateOrgs(organization);

        //新增操作
        Long parentId = organization.getParentId();
        long currentId = identifierGenerator.nextId("org").longValue();
        String currentOrgName = organization.getOrgName();
        String idPath = generateIdPath(parentId, currentId);
        String namePath = generateNamePath(parentId, currentOrgName);
        organization.setId(currentId);
        organization.setIdPath(idPath);
        organization.setNamePath(namePath);
        // 设置缺省值
        if (StringUtils.isEmpty(organization.getObjectFrom())) {
            Synchronizers synchronizers = synchronizersService.LdapConfig();
            if (Objects.isNull(synchronizers)) {
                organization.setObjectFrom(ConstsSynchronizers.SYSTEM);
            } else {
                if (synchronizers.getClassify().equalsIgnoreCase(ConstsSynchronizers.ACTIVEDIRECTORY) &&
                        synchronizers.getStatus() == ConstsStatus.DATA_ACTIVE) {
                    organization.setObjectFrom(ConstsSynchronizers.ACTIVEDIRECTORY);
                } else if (synchronizers.getClassify().equalsIgnoreCase(ConstsSynchronizers.OPEN_LDAP) &&
                        synchronizers.getStatus() == ConstsStatus.DATA_ACTIVE) {
                    organization.setObjectFrom(ConstsSynchronizers.OPEN_LDAP);
                } else {
                    organization.setObjectFrom(ConstsSynchronizers.SYSTEM);
                }
            }
        }
        boolean save = super.save(organization);

        if (save) {
            provisionService.send(
                    ProvisionTopic.ORG_TOPIC,
                    organization,
                    ProvisionAction.CREATE_ACTION);
        }

        return save ? Result.success("新增成功") : Result.failed("新增失败");
    }

    @Override
    @Transactional
    public Result<String> editOrg(Organization organization) {
        updateCheckDuplicateOrgs(organization);

        Long parentId;
        if (Objects.nonNull(organization.getParentId())) {
            parentId = organization.getParentId();
        } else {
            parentId = 0L;
        }

        String currentOrgName = organization.getOrgName();
        Long currentId = organization.getId();
        if (Objects.equals(parentId, currentId)) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.ILLEGAL_MOVE_ORG.getCode(),
                    OrgsBusinessExceptionEnum.ILLEGAL_MOVE_ORG.getMsg()
            );
        }
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Organization::getIdPath, currentId);
        queryWrapper.ne(Organization::getId, currentId);
        List<Organization> orgInfos = organizationMapper.selectList(queryWrapper);

        if (orgInfos.stream().anyMatch(orgInfo -> Objects.equals(parentId, orgInfo.getId()))) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.ILLEGAL_MOVE_ORG.getCode(),
                    OrgsBusinessExceptionEnum.ILLEGAL_MOVE_ORG.getMsg()
            );
        }
        organization.setIdPath(generateIdPath(parentId, currentId));
        organization.setNamePath(generateNamePath(parentId, currentOrgName));
        boolean result = super.updateById(organization);
        List<Organization> updatedOrgInfos = orgInfos.stream()
                .map(orgInfo -> {
                    Long subParentId = orgInfo.getParentId();
                    Long subId = orgInfo.getId();
                    String subOrgName = orgInfo.getOrgName();
                    orgInfo.setIdPath(generateIdPath(subParentId, subId));
                    orgInfo.setNamePath(generateNamePath(subParentId, subOrgName));
                    return orgInfo;
                }).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(updatedOrgInfos)) {
            result = super.updateBatchById(updatedOrgInfos);
            if(result &&
                    (organization.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.ACTIVEDIRECTORY) ||
                    organization.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.OPEN_LDAP))
            ){
                Synchronizers synchronizers = synchronizersService.LdapConfig();
                for (Organization chin : updatedOrgInfos){
                    //重新计算ldapDn

                    //重新计算部门用户的ldapDn

                }
            }
        }

        if (result) {
            provisionService.send(
                    ProvisionTopic.ORG_TOPIC,
                    organization,
                    ProvisionAction.UPDATE_ACTION);
        }
        return result? Result.success("修改成功") : Result.failed("修改失败");
    }

    public void updateCheckDuplicateOrgs(Organization entity) {
        Long id = entity.getId();
        String orgName = entity.getOrgName();
        String openDepartmentId = entity.getOpenDepartmentId();
        Long parentId = entity.getParentId();
        List<Organization> duplicateOrgs;
        // 查询同名的其他组织
        if (Objects.nonNull(parentId) && parentId != 0) {
            // 排除当前正在更新的组织
            duplicateOrgs = super.list(new LambdaQueryWrapper<Organization>()
                    .eq(Organization::getOrgName, orgName)
                    .eq(Organization::getParentId, parentId)
                    .ne(Organization::getId, id));
        } else {
            // 排除当前正在更新的组织
            duplicateOrgs = super.list(new LambdaQueryWrapper<Organization>()
                    .eq(Organization::getOrgName, orgName)
                    .and(wrapper -> wrapper.isNull(Organization::getParentId)
                            .or().eq(Organization::getParentId, 0)
                            .or().eq(Organization::getParentId, "")
                    )
                    .ne(Organization::getId, id));
        }


        if (!duplicateOrgs.isEmpty()) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.DUPLICATE_ORGS_EXIST.getCode(),
                    OrgsBusinessExceptionEnum.DUPLICATE_ORGS_EXIST.getMsg()
            );
        }

        if (Objects.nonNull(openDepartmentId)) {
            List<Organization> listByCode = super.list(new LambdaQueryWrapper<Organization>()
                    .eq(Organization::getOpenDepartmentId, openDepartmentId)
                    .eq(Organization::getObjectFrom,entity.getObjectFrom())
                    .ne(Organization::getId, id));
            if (!listByCode.isEmpty()) {
                throw new BusinessException(
                        OrgsBusinessExceptionEnum.DUPLICATE_ORGSCODE_EXIST.getCode(),
                        OrgsBusinessExceptionEnum.DUPLICATE_ORGSCODE_EXIST.getMsg()
                );
            }
        }
    }

    /**
     * @Description: 生成所属的ID路径
     * @Param: [parentId, currentId]
     * @return: java.lang.String
     * @Author: xZen
     * @Date: 11/1/2024 PM3:01
     */
    private String generateIdPath(Long parentId, Long currentId) {
        if (Objects.nonNull(parentId) && parentId.intValue() != 0) {
            String parentPath = generateIdPathRecursive(parentId, 0);
            if (parentPath.isEmpty()) {
                return "/" + currentId;
            } else {
                return "/" + parentPath + "/" + currentId;
            }
        }
        return "/" + currentId;
    }

    /**
     * @Description: 生成所属的NAME路径
     * @Param: [parentId, currentName]
     * @return: java.lang.String
     */
    private String generateNamePath(Long parentId, String currentName) {
        if(Objects.nonNull(parentId) && parentId.intValue() != 0) {
            return "/" + generateNamePathRecursive(parentId) + "/" + currentName;
        }
        return "/" + currentName;
    }

    /**
     * @Description: 递归生成ID路径
     * @Param: [parentId]
     * @return: java.lang.String
     */
    private String generateIdPathRecursive(Long parentId, int depth) {
        if (depth > 9) {
            throw new BusinessException(
                    400,
                    "超出最大组织架构深度10级"
            );
        }

        if (Objects.isNull(parentId) || parentId.intValue() == 0) {
            return "";
        }

        Organization parent = getById(parentId);
        if (Objects.isNull(parent)) {
            return "";
        }

        String parentPath = generateIdPathRecursive(parent.getParentId(), depth + 1);
        if (parentPath.isEmpty()) {
            return String.valueOf(parentId);
        } else {
            return parentPath + "/" + parentId;
        }
    }

    /**
     * @Description: 递归生成Name路径
     * @Param: [parentId]
     * @return: java.lang.String
     */
    private String generateNamePathRecursive(Long parentId) {
        if(Objects.nonNull(parentId) && parentId.intValue() != 0) {
            Organization parent = getById(parentId);
            if(Objects.nonNull(parent)) {
                String namePath = generateNamePathRecursive(parent.getParentId());
                if (namePath.isEmpty()) {
                    return parent.getOrgName();
                } else {
                    return namePath + "/" + parent.getOrgName();
                }
            }
        }
        return "";
    }


    public void saveCheckDuplicateOrgs(Organization entity) {
        String orgName = entity.getOrgName();
        String openDepartmentId = entity.getOpenDepartmentId();
        Long parentId = entity.getParentId();
        List<Organization> list;
        if (Objects.nonNull(parentId)) {
            list = super.list(new LambdaQueryWrapper<Organization>()
                    .eq(Organization::getOrgName, orgName)
                    .eq(Organization::getObjectFrom,entity.getObjectFrom())
                    .eq(Organization::getParentId, entity.getParentId()));
        } else {
            list = super.list(new LambdaQueryWrapper<Organization>()
                    .eq(Organization::getOrgName, orgName)
                    .eq(Organization::getObjectFrom,entity.getObjectFrom())
                    .and(wrapper -> wrapper
                            .isNull(Organization::getParentId)
                            .or().eq(Organization::getParentId, "")
                            .or().eq(Organization::getParentId, 0)
                    ));
        }
        if (!list.isEmpty()) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.DUPLICATE_ORGS_EXIST.getCode(),
                    OrgsBusinessExceptionEnum.DUPLICATE_ORGS_EXIST.getMsg()
            );
        }

        if (Objects.nonNull(openDepartmentId)) {
            List<Organization> listByCode = super.list(new LambdaQueryWrapper<Organization>()
                    .eq(Organization::getOpenDepartmentId, openDepartmentId)
                    .eq(Organization::getObjectFrom,entity.getObjectFrom()));
            if (!listByCode.isEmpty()) {
                throw new BusinessException(
                        OrgsBusinessExceptionEnum.DUPLICATE_ORGSCODE_EXIST.getCode(),
                        OrgsBusinessExceptionEnum.DUPLICATE_ORGSCODE_EXIST.getMsg()
                );
            }
        }
    }

    @Transactional
    @Override
    public boolean updateById(Organization entity) {
        return super.updateById(entity);
    }

    @Override
    public Result<List<Tree<String>>> tree(Long id) {

        List<Organization> organizations = organizationMapper.selectList(Wrappers.<Organization>lambdaQuery()
                .eq(Organization::getStatus, 0));

        Set<Long> disabledOrgIds = Collections.emptySet();

        if (Objects.nonNull(id)) {
            List<Organization> organizationsDisabled = organizationMapper.selectList(Wrappers.<Organization>lambdaQuery()
                    .eq(Organization::getStatus, 0)
                    .like(Organization::getIdPath, id));

            disabledOrgIds = organizationsDisabled.stream()
                    .map(Organization::getId)
                    .collect(Collectors.toSet());
        }





        List<TreeNode<String>> treeNodeList = new ArrayList<>();

        for (Organization org : organizations) {
            String parentId = String.valueOf(org.getParentId());
            TreeNode<String> treeNode = new TreeNode<>(String.valueOf(org.getId()), parentId, org.getOrgName(), org.getSortIndex());

            if (disabledOrgIds.contains(org.getId())) {
                Map<String, Object> extraMap = new HashMap<>();
                extraMap.put("disabled", true);
                treeNode.setExtra(extraMap);
            }

            treeNodeList.add(treeNode);
        }


        List<Tree<String>> tree = TreeUtil.build(treeNodeList, "0");

        if (ObjectUtils.isEmpty(tree)) {
            tree = new ArrayList<>();
        }

        return Result.success(tree);
    }

    @Transactional
    @Override
    public Result<String> disableBatch(IdsListDto idsListDto, Long updateBy, Date updateTime) {
        List<Long> ids = idsListDto.getIds();


        //检查是否有子用户
//            isExistSubUser(id);
        //检查是否有子组织
        isActiveOrg(ids);
        //检查旗下是否有关联的用户
        checkForRelatedUsers(ids);
        //检查是否有子组织
        checkForSubOrganizations(ids);

        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Organization::getId, ids);
        Organization organization = new Organization();
        organization.setStatus(1);
        organization.setUpdateBy(updateBy);
        organization.setUpdateTime(updateTime);

        boolean result = super.update(organization, wrapper);
        return result ? Result.success("禁用成功") : Result.failed("禁用失败");
    }

    @Transactional
    @Override
    public Result<String> activeBatch(IdsListDto idsListDto, Long updateBy, Date updateTime) {
        List<Long> ids = idsListDto.getIds();
        List<Organization> orgInfos = organizationMapper.selectList(new LambdaQueryWrapper<Organization>()
                .in(Organization::getId, ids));
        Set<Long> parentIds = orgInfos.stream()
                .map(Organization::getParentId)
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toSet());

        //检查其父级组织是否活跃
        StringBuilder errorMessage = new StringBuilder(OrgsBusinessExceptionEnum.PARENT_ORGS_FORBIDDEN.getMsg());
        List<Organization> organizations = super.listByIds(parentIds);
        organizations.forEach(organization -> {
            if (Objects.nonNull(organization) && organization.getStatus() != 0) {
                errorMessage.append(" [").append(organization.getOrgName()).append("]");
                throw new BusinessException(
                        OrgsBusinessExceptionEnum.PARENT_ORGS_FORBIDDEN.getCode(),
                        errorMessage.toString()
                );
            }
        });


        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Organization::getId, ids);
        Organization organization = new Organization();
        organization.setStatus(0);
        organization.setUpdateBy(updateBy);
        organization.setUpdateTime(updateTime);

        boolean result = super.update(organization, wrapper);
        return result ? Result.success("启用成功") : Result.failed("启用失败");
    }

    private void isActiveOrg(List<Long> ids) {
        LambdaQueryWrapper<Organization> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Organization::getParentId, ids).eq(Organization::getStatus, 0);
        List<Organization> orgInfos = organizationMapper.selectList(wrapper);
        if (ObjectUtils.isNotEmpty(orgInfos)) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.SUB_ORGS_ACTIVE.getCode(),
                    OrgsBusinessExceptionEnum.SUB_ORGS_ACTIVE.getMsg());
        }
    }

    @Transactional
    @Override
    public Result<String> deleteBatch(IdsListDto idsListDto) {
        List<Long> ids = idsListDto.getIds();

        List<Organization> organizations = super.list(Wrappers.<Organization>lambdaQuery()
                .in(Organization::getId, ids));

        for (Organization organization : organizations) {
            if (organization.getStatus() == 0) {
                throw new BusinessException(
                        OrgsBusinessExceptionEnum.CURRENT_ORGS_ACTIVE.getCode(),
                        OrgsBusinessExceptionEnum.CURRENT_ORGS_ACTIVE.getMsg()
                );
            }
        }


//            isExistSubUser(id);
        //检查是否有子组织
        checkForSubOrganizations(ids);
       /* //检查旗下是否有关联的用户
        checkForRelatedUsers(ids);
        //检查旗下是否有子组织
        checkForSubOrganizations(ids);*/

        boolean result = super.removeBatchByIds(ids);

        if (result) {
            for (Organization organization : organizations) {
                provisionService.send(
                        ProvisionTopic.ORG_TOPIC,
                        organization,
                        ProvisionAction.DELETE_ACTION);
            }
        }
        return result ? Result.success("删除成功") : Result.failed("删除失败");
    }

    @Override
    public boolean updateLdapDnAndLdapIdById(Long id,String ldapId, String ldapDn) {
        LambdaUpdateWrapper<Organization> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Organization::getLdapDn,ldapDn);
        if (StringUtils.isNotEmpty(ldapId)) {
            lambdaUpdateWrapper.set(Organization::getLdapId,ldapId);
        }
        lambdaUpdateWrapper.set(Organization::getUpdateTime,new Date());
        lambdaUpdateWrapper.eq(Organization::getId,id);
        return super.update(lambdaUpdateWrapper);
    }

    @Override
    public boolean updateLdapDnAndByLdapId(String ldapId, String ldapDn) {
        LambdaUpdateWrapper<Organization> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(Organization::getLdapDn,ldapDn);
        lambdaUpdateWrapper.set(Organization::getUpdateTime,new Date());
        lambdaUpdateWrapper.eq(Organization::getLdapId,ldapId);
        return super.update(lambdaUpdateWrapper);
    }



    /**
     * @Description: 检查是否有存在的子组织机构
     * @Param: [ids]
     * @return: void
     */
    private void checkForSubOrganizations(List<Long> ids) {
        LambdaQueryWrapper<Organization> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Organization::getParentId, ids);
        List<Organization> orgInfos = organizationMapper.selectList(queryWrapper);
        if (ObjectUtils.isNotEmpty(orgInfos)) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.SUB_ORGS_EXISTS.getCode(),
                    OrgsBusinessExceptionEnum.SUB_ORGS_EXISTS.getMsg());
        }
    }

    /**
     * @Description: 检查是否有关联的用户
     * @Param: [ids]
     * @return: void
     */
    private void checkForRelatedUsers(List<Long> ids) {
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(UserInfo::getDepartmentId, ids);
        List<UserInfo> userInfoList = userInfoMapper.selectList(wrapper);
        if (ObjectUtils.isNotEmpty(userInfoList)) {
            throw new BusinessException(
                    OrgsBusinessExceptionEnum.SUB_USERS_EXISTS.getCode(),
                    OrgsBusinessExceptionEnum.SUB_USERS_EXISTS.getMsg());
        }
    }
}

