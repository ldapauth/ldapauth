package com.ldapauth.persistence.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.IdsListDto;
import com.ldapauth.pojo.dto.OrganizationQueryDTO;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.vo.Result;

import java.util.Date;
import java.util.List;

/**
 * @author Shi.bl
 *
 */
public interface OrganizationService extends IService<Organization> {

    /**
     * 分页查询方法
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<Organization> fetch(OrganizationQueryDTO queryDTO);

    /**
     * @Description: 新增组织机构
     * @Param: [organization]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> saveOrg(Organization organization);

    /**
     * @Description: 修改组织机构
     * @Param: [organization]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> editOrg(Organization organization);

    /**
     * @Description: 获取组织树
     * @Param: []
     * @return: com.ldapauth.pojo.vo.Result<java.util.List<cn.hutool.core.lang.tree.Tree<java.lang.String>>>
     */
    Result<List<Tree<String>>> tree(Long id);

    /**
     * 根据开放ID查询组织对象
     * @param objectFrom 来源
     * @param openDepartmentId 开放ID
     * @return
     */
    Organization getOrganizationByOpenDepartmentId(String objectFrom,String openDepartmentId);
    /**
     * 根据开放ID查询组织对象
     * @param ldapId
     * @return
     */
    Organization getOrganizationByLdapId(String ldapId);


    /**
     * @Description: 禁用组织机构
     * @Param: [idsListDto, updateBy, updateTime]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> disableBatch(IdsListDto idsListDto, Long updateBy, Date updateTime);

    /**
     * @Description: 启用组织机构
     * @Param: [idsListDto, updateBy, updateTime]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> activeBatch(IdsListDto idsListDto, Long updateBy, Date updateTime);

    /**
     * @Description: 删除组织机构
     * @Param: [idsListDto, updateBy, updateTime]
     * @return: com.ldapauth.pojo.vo.Result<java.lang.String>
     */
    Result<String> deleteBatch(IdsListDto idsListDto);


    /**
     * 修改ldapdn
     * @param id
     * @param ldapId
     * @param ldapDn
     * @return
     */
    boolean updateLdapDnAndLdapIdById(Long id,String ldapId,String ldapDn);

    /**
     * 修改ldapdn
     * @param ldapId
     * @param ldapDn
     * @return
     */
    boolean updateLdapDnAndByLdapId(String ldapId,String ldapDn);
}
