package com.ldapauth.persistence.service;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.ResourceQueryDTO;
import com.ldapauth.pojo.entity.Resource;

import java.util.Date;
import java.util.List;


/**
 * @author Shi.bl
 *
 */
public interface ResourceService extends IService<Resource> {


    List<Tree<String>> fetch(ResourceQueryDTO queryDTO);

    /**
     * 系统权限资源菜单树
     */
    List<Tree<String>> systemTree();


    /**
     * 修改用户状态
     * @param changeStatusDTO
     * @param updateBy 修改人
     * @param updateTime 修改时间
     * @return
     */
    boolean updateStatus(ChangeStatusDTO changeStatusDTO, Long updateBy, Date updateTime);

}
