package com.ldapauth.persistence.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ldapauth.pojo.dto.SynchronizersLogsQueryDTO;
import com.ldapauth.pojo.entity.SynchronizersLogs;


/**
 * @author Shi.bl
 *
 */
public interface SynchronizersLogsService extends IService<SynchronizersLogs> {


    /**
     * 分页查询方法
     * @param queryDTO 分页查询参数
     * @return
     */
    Page<SynchronizersLogs> fetch(SynchronizersLogsQueryDTO queryDTO);

}
