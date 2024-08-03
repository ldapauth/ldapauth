package com.ldapauth.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ldapauth.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description:
 * @author: 24096
 * @time: 2/7/2023 AM10:43
 */


@TableName(value = "lda_resources")
@Schema(name = "Resource", description = "资源表")
@EqualsAndHashCode(callSuper = false)
@Data
public class Resource extends BaseEntity {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	@TableId(type = IdType.ASSIGN_ID)
	@Schema(name = "id", description = "标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long id;

	@Schema(name = "name", description = "资源名称")
	String name;

	@Schema(name = "permission", description = "权限值")
	String permission;

	@Schema(name = "parentId", description = "父级标识")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long parentId;

	@Schema(name = "url", description = "资源地址")
	String url;

	@Schema(name = "namePath", description = "名称路径")
	String namePath;
	@Schema(name = "idPath", description = "ID路径")
	String idPath;
	@Schema(name = "classify", description = "类型 directory=目录 menu-菜单类型 button-按钮类型 page-页面 api-接口类型  other-其他")
	String classify;

	@Schema(name = "routePath", description = "路由地址")
	String routePath;
	@Schema(name = "routeComponent", description = "组件路径")
	String routeComponent;

	@Schema(name = "routeQuery", description = "路由参数")
	String routeQuery;

	@Schema(name = "isFrame", description = "是否为外链 0-否 1-是")
	Integer isFrame;
	@Schema(name = "isCache", description = "是否缓存 0-否 1-是")
	Integer isCache;
	@Schema(name = "isVisible", description = "是否可见 0-否 1-是")
	Integer isVisible;
	@Schema(name = "sortOrder", description = "排序")
	Integer sortOrder;

	@Schema(name = "resAction", description = "资源操作 read-读操作 write-写操作 list-列表操作")
	String resAction;

	@Schema(name = "requestMethod", description = "请求方法 GET/POST")
	String requestMethod;
	@Schema(name = "icon", description = "图标")
	String icon;

	@Schema(name = "appId", description = "应用ID,系统数据为1")
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	Long appId;

	@Schema(name = "status", description = "状态: 0-启用; 1-禁用")
	Integer status;

	@Schema(name = "description", description = "备注")
	String description;


}
