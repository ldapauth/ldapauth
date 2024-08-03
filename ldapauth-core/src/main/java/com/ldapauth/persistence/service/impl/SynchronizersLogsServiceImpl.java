package com.ldapauth.persistence.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ldapauth.persistence.mapper.SynchronizersLogsMapper;
import com.ldapauth.persistence.service.SynchronizersLogsService;
import com.ldapauth.pojo.dto.SynchronizersLogsQueryDTO;
import com.ldapauth.pojo.entity.SynchronizersLogs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Slf4j
@Service
public class SynchronizersLogsServiceImpl extends ServiceImpl<SynchronizersLogsMapper, SynchronizersLogs> implements SynchronizersLogsService {



    @Override
    public Page<SynchronizersLogs> fetch(SynchronizersLogsQueryDTO queryDTO) {
        LambdaQueryWrapper<SynchronizersLogs> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (Objects.nonNull(queryDTO.getSynchronizerId())) {
            lambdaQueryWrapper.eq(SynchronizersLogs::getSynchronizerId,queryDTO.getSynchronizerId());
        }
        if (Objects.nonNull(queryDTO.getTrackingUniqueId())) {
            lambdaQueryWrapper.eq(SynchronizersLogs::getTrackingUniqueId,queryDTO.getTrackingUniqueId());
        }
        if (Objects.nonNull(queryDTO.getSyncType())) {
            lambdaQueryWrapper.eq(SynchronizersLogs::getSyncType,queryDTO.getSyncType());
        }
        if (Objects.nonNull(queryDTO.getSyncResultStatus())) {
            lambdaQueryWrapper.eq(SynchronizersLogs::getSyncResultStatus,queryDTO.getSyncResultStatus());
        }
        if (Objects.nonNull(queryDTO.getSyncActionType())) {
            lambdaQueryWrapper.eq(SynchronizersLogs::getSyncActionType,queryDTO.getSyncActionType());
        }
        lambdaQueryWrapper.orderByDesc(SynchronizersLogs::getCreateTime);
        return super.page(queryDTO.build(),lambdaQueryWrapper);
    }
}
