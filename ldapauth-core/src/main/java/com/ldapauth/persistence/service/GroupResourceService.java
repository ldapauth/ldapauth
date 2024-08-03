package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.GroupResourceQueryDTO;
import com.ldapauth.pojo.dto.IdsDTO;
import com.ldapauth.pojo.entity.GroupResource;
import com.ldapauth.pojo.entity.Resource;
import com.ldapauth.pojo.vo.IdsVO;

import java.util.Date;
import java.util.List;


public interface GroupResourceService extends IService<GroupResource> {


    /**
     * 分页查询方法
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<Resource> fetch(GroupResourceQueryDTO queryDTO);


    /**
     * 获取分组已授权资源ID集合
     * @param groupId
     */
    IdsVO getAuthResourceIds(Long groupId);
    /**
     * 授权组资源
     * @param idsDTO
     * @param createdBy 操作人
     * @param createdTime 操作时间
     * @return
     */
    boolean authResource(IdsDTO idsDTO, Long createdBy, Date createdTime);



    /**
     * 获取成员数资源列表
     * @param memberId
     */
    List<Resource> getResourceByMemberId(Long memberId);




}
