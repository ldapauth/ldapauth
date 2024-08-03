package com.ldapauth.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Schema(name = "SystemVO", description = "系统登录初始化参数")
@EqualsAndHashCode(callSuper = false)
@Data
public class SystemVO {

	@Schema(name = "isRemeberMe", description = "记住我")
	Boolean isRemeberMe;

	@Schema(name = "captcha", description = "记住我")
	Boolean captcha;

	@Schema(name = "state", description = "JWT标记")
	String state;

	@Schema(name = "logo", description = "系统logo")
	String logo;

	@Schema(name = "title", description = "系统名称")
	String title;

	@Schema(name = "copyright", description = "版权")
	String copyright;

	@Schema(name = "auths", description = "其他登录方式")
	List<SocialsProviderVO> auths;
}
