package com.ldapauth.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Schema(name = "SynchronizersLogsQueryDTO", description = "查询参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class SynchronizersLogsQueryDTO extends PageQueryDTO {


    @Schema(name = "synchronizerId", description = "同步器标识")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long synchronizerId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(name = "trackingUniqueId", description = "任务跟踪ID，根据ID可查询单次执行的任务同步那些数据")
    Long trackingUniqueId;

    @Schema(name = "syncType", description = "同步类型 0=组织 1用户")
    Integer syncType;

    @Schema(name = "syncActionType", description = "操作方法，add=新增 update=修改 delete=删除")
    String syncActionType;

    @Schema(name = "syncResultStatus", description = "同步结果 0=成功 1失败")
    Integer syncResultStatus;

}
