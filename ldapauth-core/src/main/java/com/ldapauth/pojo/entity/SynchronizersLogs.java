package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: 24096
 * @time: 2/7/2023 AM10:43
 */


@TableName(value = "lda_synchronizers_logs")
@Schema(name = "SynchronizersLogs", description = "同步器日志表")
@EqualsAndHashCode(callSuper = false)
@Data
public class SynchronizersLogs extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "synchronizerId", description = "同步器标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long synchronizerId;

	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@Schema(name = "trackingUniqueId", description = "任务跟踪ID，根据ID可查询单次执行的任务同步那些数据")
	Long trackingUniqueId;

	@Schema(name = "syncData", description = "同步对象内容")
	String syncData;

	@Schema(name = "syncType", description = "同步类型 0=组织 1用户")
	Integer syncType;

	@Schema(name = "syncActionType", description = "操作方法，add=新增 update=修改 delete=删除")
	String syncActionType;

	@Schema(name = "syncApiUri", description = "接口地址")
	String syncApiUri;

	@Schema(name = "syncResultStatus", description = "同步结果 0=成功 1失败")
	Integer syncResultStatus;

	@Schema(name = "syncMessage", description = "同步消息")
	String syncMessage;


}
