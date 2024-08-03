


package com.ldapauth.authn.web;

import com.ldapauth.authn.annotation.CurrentUser;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.web.WebConstants;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/**
 * CurrentUser注解的注入实现
 *
 * @author Crystal.Sea
 *
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
    	//读取认证信息
    	Authentication  authentication  =
    			(Authentication ) webRequest.getAttribute(
    					WebConstants.AUTHENTICATION, RequestAttributes.SCOPE_SESSION);
    	UserInfo userInfo  = AuthorizationUtils.getUserInfo(authentication);
    	if (userInfo != null) {
            return userInfo;
        }
        throw new MissingServletRequestPartException("currentUser");
    }

    /**
     * 判断参数类型及注解
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(UserInfo.class)
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

}
