package com.ldapauth.persistence.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.constants.ConstsSystem;
import com.ldapauth.persistence.mapper.ResourceMapper;
import com.ldapauth.persistence.service.ResourceService;
import com.ldapauth.pojo.dto.ChangeStatusDTO;
import com.ldapauth.pojo.dto.ResourceQueryDTO;
import com.ldapauth.pojo.entity.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

    @Override
    public List<Tree<String>> fetch(ResourceQueryDTO queryDTO) {
        LambdaQueryWrapper<Resource> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (Objects.nonNull(queryDTO.getName())) {
            lambdaQueryWrapper.like(Resource::getName,queryDTO.getName());
        }
        if (Objects.nonNull(queryDTO.getClassify())) {
            lambdaQueryWrapper.eq(Resource::getClassify,queryDTO.getClassify());
        }
        if (Objects.nonNull(queryDTO.getStatus())) {
            lambdaQueryWrapper.eq(Resource::getStatus,queryDTO.getStatus());
        }
        lambdaQueryWrapper.orderByAsc(Resource::getSortOrder);
        List<Resource> resourceList = super.list(lambdaQueryWrapper);
        List<Tree<String>> tree = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(resourceList)) {
            final List<TreeNode<String>> treeNode = new ArrayList<>();
            resourceList.forEach(
                    temp ->{
                        TreeNode node =  new TreeNode<>(
                                String.valueOf(temp.getId()),
                                String.valueOf(temp.getParentId()),
                                temp.getName(),
                                temp.getSortOrder());
                        Map<String, Object> extraMap =  BeanUtil.beanToMap(temp);
                        extraMap.remove("id");
                        extraMap.remove("name");
                        extraMap.remove("parentId");
                        node.setExtra(extraMap);
                        treeNode.add(node);
                    }
            );
            tree = TreeUtil.build(treeNode, String.valueOf(ConstsSystem.SYS_RESOURCE_APP_ID));
            if (ObjectUtils.isEmpty(tree)) {
                tree = new ArrayList<>();
            }
        }
        return tree;

    }

    @Override
    public List<Tree<String>> systemTree() {
        LambdaQueryWrapper<Resource> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Resource::getAppId, ConstsSystem.SYS_RESOURCE_APP_ID);
        //过滤禁用的数据
        lambdaQueryWrapper.eq(Resource::getStatus, ConstsSystem.SYS_RESOURCE_SUCCESS);
        lambdaQueryWrapper.orderByAsc(Resource::getSortOrder);
        List<Resource> resourceList = super.list(lambdaQueryWrapper);
        List<Tree<String>> tree = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(resourceList)) {
            List<TreeNode<String>> treeNode = new ArrayList<>();
            resourceList.forEach(temp -> treeNode.add(new TreeNode<>(
                    String.valueOf(temp.getId()),
                    String.valueOf(temp.getParentId()),
                    temp.getName(),
                    temp.getSortOrder())));
            tree = TreeUtil.build(treeNode, String.valueOf(ConstsSystem.SYS_RESOURCE_APP_ID));
            if (ObjectUtils.isEmpty(tree)) {
                tree = new ArrayList<>();
            }
        }
        return tree;
    }


    @Transactional
    @Override
    public boolean updateStatus(ChangeStatusDTO changeStatusDTO, Long updateBy, Date updateTime) {
        LambdaUpdateWrapper<Resource> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(Resource::getId,changeStatusDTO.getId());
        lambdaUpdateWrapper.set(Resource::getStatus,changeStatusDTO.getStatus());
        lambdaUpdateWrapper.set(Resource::getUpdateBy,updateBy);
        lambdaUpdateWrapper.set(Resource::getUpdateTime,updateTime);
        return super.update(lambdaUpdateWrapper);
    }



    @Transactional
    @Override
    public boolean save(Resource entity) {
        return super.save(entity);
    }

    @Transactional
    @Override
    public boolean updateById(Resource entity) {
        return super.updateById(entity);
    }

}
