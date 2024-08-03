package com.ldapauth.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldapauth.pojo.dto.GroupAppsQueryDTO;
import com.ldapauth.pojo.entity.GroupApps;
import com.ldapauth.pojo.vo.GroupAppsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface GroupAppsMapper extends BaseMapper<GroupApps> {

    /**
     * 查询授权的应用
     * @param page
     * @param query
     * @return
     */
    Page<GroupAppsVO> fetchList(Page page, @Param("query") GroupAppsQueryDTO query);


}
