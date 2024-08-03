package com.ldapauth.web.contorller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.ldapauth.authn.LoginCredential;
import com.ldapauth.authn.jwt.AuthJwt;
import com.ldapauth.authn.jwt.AuthTokenService;
import com.ldapauth.authn.provider.AbstractAuthenticationProvider;
import com.ldapauth.authn.session.Session;
import com.ldapauth.cache.CacheService;
import com.ldapauth.configuration.ApplicationConfig;
import com.ldapauth.constants.ConstsCacheData;
import com.ldapauth.constants.ConstsLoginType;
import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.exception.BusinessException;
import com.ldapauth.persistence.service.SocialsAssociateService;
import com.ldapauth.persistence.service.SocialsProviderService;
import com.ldapauth.persistence.service.SystemsService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.dto.CallbackDTO;
import com.ldapauth.pojo.entity.SocialsAssociate;
import com.ldapauth.pojo.entity.SocialsProvider;
import com.ldapauth.pojo.entity.Systems;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.pojo.vo.Result;
import com.ldapauth.pojo.vo.SocialsProviderVO;
import com.ldapauth.pojo.vo.SystemVO;
import com.ldapauth.web.WebConstants;
import com.ldapauth.web.WebContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 登录
 * <p>
 * 登录界面初始化/login/get
 * </p>
 *
 * <p>
 * 登录入口/login/signin
 * </p>
 *
 * @author Crystal.Sea
 *
 */
@Tag(
		name = "认证服务",
		description = "提供认证相关接口服务"
)
@RestController
@Slf4j
@RequestMapping(value = "/login")
public class LoginEntryController {


	@Autowired
	AuthTokenService authTokenService;

	@Autowired
	ApplicationConfig applicationConfig;

	@Autowired
	AbstractAuthenticationProvider authenticationProvider ;

	@Autowired
	SocialsProviderService socialsProviderService;

	@Autowired
	SocialsAssociateService socialsAssociateService;

	@Autowired
	UserInfoService userInfoService;

	@Autowired
	IdentifierGenerator identifierGenerator;

	@Autowired
	CacheService cacheService;

	@Autowired
	SystemsService systemsService;

	/**
	 * init login。登录界面初始化信息
	 * @return
	 */
	@Operation(summary = "登录界面初始化信息", description = "返回对象")
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value={"/get"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<SystemVO> get() {
		log.debug("/login.");
		Systems systems = systemsService.get();
		SystemVO systemVO = new SystemVO();
		systemVO.setIsRemeberMe(applicationConfig.getLoginConfig().isRemeberMe());
		systemVO.setCaptcha(applicationConfig.getLoginConfig().isCaptcha());
		systemVO.setState(authTokenService.genRandomJwt());
		systemVO.setTitle(systems.getTitle());
		systemVO.setLogo(systems.getLogo());
		systemVO.setCopyright(systems.getCopyright());
		List<SocialsProvider> list = socialsProviderService.cacheList();
		if (CollectionUtil.isNotEmpty(list)) {
			List<SocialsProviderVO> auths = BeanUtil.copyToList(list, SocialsProviderVO.class);
			systemVO.setAuths(auths);
		} else {
			systemVO.setAuths(new ArrayList<>());
		}
		return Result.success(systemVO);
	}


	/**
	 * 第三方登录认证获取回调地址
	 * @return
	 */
	@Operation(summary = "第三方登录认证获取回调地址", description = "返回对象")
	@ApiResponse(responseCode = "200", description = "成功")
	@GetMapping(value={"/authorize/{id}"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	@ResponseBody
	public Result<String> authorize(@Validated @NotNull(message = "ID不能为空") @PathVariable("id") Long id) {
		log.debug("/authorize.");
		AuthRequest authRequest = socialsProviderService.authorize(id);
		if (Objects.nonNull(authRequest)) {
			return Result.success(authRequest.authorize("ldapAuth"));
		}
		return Result.failed("未知错误");
	}


	/**
	 * 第三方回调获取认证用户
	 */
	@PostMapping("/callback")
	@Operation(summary = "第三方回调获取认证用户", description = "第三方回调获取认证用户")
	@ApiResponse(responseCode = "200",description = "成功")
	@ResponseBody
	public Result<Object> callback(@RequestBody CallbackDTO callbackDTO) {
		AuthUser authUser = socialsProviderService.authCallback(callbackDTO);
		//查找第三方用户是否存在绑定
		SocialsAssociate socialsAssociate = socialsAssociateService.getObjectBySocialsUserId(
				callbackDTO.getId(),authUser.getUuid());
		if (Objects.nonNull(socialsAssociate)) {
			UserInfo userinfo = userInfoService.getById(socialsAssociate.getUserId());
			//用户存在并且是正常状态下
			if (Objects.nonNull(userinfo) && userinfo.getStatus().intValue() == ConstsStatus.DATA_ACTIVE) {
				LoginCredential loginCredential =new LoginCredential(
						userinfo.getUsername(),"", ConstsLoginType.SOCIALSIGNON);
				loginCredential.setProvider(authUser.getCompany());
				Authentication authentication = authenticationProvider.authenticate(loginCredential,true);
				if (authentication != null) {
					//success
					return Result.success(authTokenService.genAuthJwt(authentication));
				}
			} else {
				throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"第三方关联系统用户不存在或被禁用");
			}
		}
		String tempCode = identifierGenerator.nextUUID(socialsAssociate);
		authUser.setSource(String.valueOf(callbackDTO.getId()));
		String key = ConstsCacheData.SOCIALS_PROVIDER_BIND_USER_KEY + tempCode;
		//设置五10分钟缓存
		cacheService.setCacheObject(key,authUser,10, TimeUnit.MINUTES);
		return new Result<>(1000001,"需要绑定用户",tempCode);
	}

 	/**
 	 * 常规用户名和密码登录
 	 * @param loginCredential
 	 * @return
 	 */
	@Operation(summary = "认证接口", description = "返回JWT对象")
	@ApiResponse(responseCode = "200", description = "成功")
 	@PostMapping(value={"/signin"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public Result<AuthJwt> signin(@RequestBody LoginCredential loginCredential) {
 		if (authTokenService.validateJwtToken(loginCredential.getState())){
 			loginCredential.setStyle(Session.STYLE.MGMT);
	 		Authentication  authentication  = authenticationProvider.authenticate(loginCredential);
	 		if(authentication != null) {
				AuthJwt jwt = authTokenService.genAuthJwt(authentication);
				//判断社交登录是否存在临时代码
				if (StringUtils.isNotEmpty(loginCredential.getsTempCode())) {
					String key = ConstsCacheData.SOCIALS_PROVIDER_BIND_USER_KEY + loginCredential.getsTempCode();
					AuthUser authUser = cacheService.getCacheObject(key);
					if (Objects.isNull(authUser)) {
						throw new BusinessException(HttpStatus.BAD_REQUEST.value(),"第三方凭证过期，请重新发起。");
					}
					//绑定系统用户和第三方用户
					socialsAssociateService.bindUser(Long.valueOf(jwt.getId()), authUser);
				}
				 //success
	 			return Result.success(jwt);
	 		}else {//fail
	 			String errorMsg = WebContext.getAttribute(WebConstants.LOGIN_ERROR_SESSION_MESSAGE) == null ?
							      "" : WebContext.getAttribute(WebConstants.LOGIN_ERROR_SESSION_MESSAGE).toString();
	 			log.debug("login fail , message {}",errorMsg);
				return Result.failed(errorMsg);
	 		}
 		}
		return Result.failed("无效凭证");
 	}

}
