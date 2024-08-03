package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.pojo.dto.GroupResourceQueryDTO;
import com.ldapauth.pojo.entity.GroupResource;
import com.ldapauth.pojo.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GroupResourceMapper extends BaseMapper<GroupResource> {

    Page<Resource> fetch(Page page,@Param("query") GroupResourceQueryDTO query);


    List<Resource> selectResourceByMemberId(@Param("memberId") Long memberId);
}
