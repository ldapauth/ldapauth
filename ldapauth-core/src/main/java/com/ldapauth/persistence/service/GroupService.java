package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.GroupQueryDTO;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.Group;

import java.util.Date;


/**
 * @author Shi.bl
 *
 */
public interface GroupService extends IService<Group> {

    /**
     * 分页查询方法
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<Group> fetch(GroupQueryDTO queryDTO);


    /**
     * 修改用户状态
     * @param changeStatusDTO
     * @param updateBy 修改人
     * @param updateTime 修改时间
     * @return
     */
    boolean updateStatus(ChangeStatusDTO changeStatusDTO, Long updateBy, Date updateTime);


    /**
     * 修改ldapdn
     * @param id
     * @param ldapId
     * @param ldapDn
     * @return
     */
    boolean updateLdapDnAndLdapIdById(Long id,String ldapId,String ldapDn);



    /**
     * 根据ldapID获取组对象
     * @param ldapId
     * @return
     */
    Group getGroupIdByLdapId(String ldapId);


}
