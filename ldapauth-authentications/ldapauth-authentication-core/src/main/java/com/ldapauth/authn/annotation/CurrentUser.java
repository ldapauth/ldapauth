package com.ldapauth.authn.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 获取当前登录用户注解
 *
 * <p>
 * 在Controller的方法中通过CurrentUser注解获取当前登录用户
 * </p>
 *
 * <p>
 * 例如@CurrentUser UserInfo currentUser
 * </p>
 * @author Crystal.Sea
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
