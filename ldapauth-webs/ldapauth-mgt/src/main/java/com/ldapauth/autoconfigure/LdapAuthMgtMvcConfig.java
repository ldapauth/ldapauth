package com.ldapauth.autoconfigure;

import java.util.List;

import com.ldapauth.authn.web.CurrentUserMethodArgumentResolver;
import com.ldapauth.authn.web.interceptor.HistorySignOnAppInterceptor;
import com.ldapauth.authn.web.interceptor.PermissionInterceptor;
import com.ldapauth.authn.provider.AbstractAuthenticationProvider;
import com.ldapauth.authn.web.interceptor.SingleSignOnInterceptor;
import com.ldapauth.configuration.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@EnableWebMvc
@AutoConfiguration
@Slf4j
public class LdapAuthMgtMvcConfig implements WebMvcConfigurer {

    @Autowired
  	ApplicationConfig applicationConfig;

    @Autowired
    AbstractAuthenticationProvider authenticationProvider ;

    @Autowired
    PermissionInterceptor permissionInterceptor;

    @Autowired
    SingleSignOnInterceptor singleSignOnInterceptor;

    @Autowired
    HistorySignOnAppInterceptor historySignOnAppInterceptor;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	log.debug("add Resource Handlers");
        log.debug("add statics");
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        log.debug("add templates");
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
        log.debug("add swagger");
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        log.debug("add knife4j");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        log.debug("add Resource Handler finished .");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //addPathPatterns 用于添加拦截规则 ， 先把所有路径都加入拦截， 再一个个排除
        //excludePathPatterns 表示改路径不用拦截
        log.debug("add HttpJwtEntryPoint");
        permissionInterceptor.setMgmt(true);

        registry.addInterceptor(permissionInterceptor)
                .addPathPatterns("/organizations/**")
                .addPathPatterns("/users/**")
                .addPathPatterns("/groups/**")
                .addPathPatterns("/synchronizers/**")
                .addPathPatterns("/audit/**")
                .addPathPatterns("/apps/**")
                .addPathPatterns("/accounts/**")
                .addPathPatterns("/resources/**")
                .addPathPatterns("/policy/**")
                .addPathPatterns("/socials-provider/**")
                .addPathPatterns("/provider/**")
                .addPathPatterns("/systems/**")
                .addPathPatterns("/access/**")
                .addPathPatterns("/permissions/**")
                .addPathPatterns("/config/**")
                .addPathPatterns("/historys/**")
                .addPathPatterns("/file/**")
                //排除用户默认权限
                .excludePathPatterns("/users/sendCode")
                .excludePathPatterns("/users/setPwd")
                .excludePathPatterns("/users/update/profile")
                .excludePathPatterns("/users/update/avatar")
                .excludePathPatterns("/users/update/password")
                .excludePathPatterns("/logout")
                .excludePathPatterns("/policy/password")
                .excludePathPatterns("/file/upload")
                .excludePathPatterns("/file/preview/**")
        ;

        log.debug("add PermissionAdapter");

        //for Single Sign On
        registry.addInterceptor(singleSignOnInterceptor)
                //JWT
                .addPathPatterns("/authz/jwt/*")
                //SAML
                .addPathPatterns("/authz/saml20/idpinit/*")
                .addPathPatterns("/authz/saml20/v1/assertion")
                .addPathPatterns("/authz/saml20/v1/assertion/")
                //CAS
                .addPathPatterns("/auth/cas/*")
                .addPathPatterns("/auth/cas/*/*")
                .addPathPatterns("/auth/cas/login")
                .addPathPatterns("/auth/cas/login/")
                .addPathPatterns("/auth/cas/granting/*")
                .addPathPatterns("/auth/cas/granting**")
                //cas1.0 validate
                .excludePathPatterns("/auth/cas/validate")
                //cas2.0 Validate
                .excludePathPatterns("/auth/cas/serviceValidate")
                .excludePathPatterns("/auth/cas/proxyValidate")
                .excludePathPatterns("/auth/cas/proxy")
                //cas3.0 Validate
                .excludePathPatterns("/auth/cas/p3/serviceValidate")
                .excludePathPatterns("/auth/cas/p3/proxyValidate")
                .excludePathPatterns("/auth/cas/p3/proxy")
                //rest
                .excludePathPatterns("/auth/cas/v1/tickets")
                .excludePathPatterns("/auth/cas/v1/tickets/*")

                //OAuth
                .addPathPatterns("/auth/oauth/v20/authorize")
                .addPathPatterns("/auth/oauth/v20/authorize/*")

                //OAuth TENCENT_IOA
                .addPathPatterns("/oauth2/authorize")
                .addPathPatterns("/oauth2/authorize/*")

                //online ticket Validate
                .excludePathPatterns("/onlineticket/ticketValidate")
                .excludePathPatterns("/onlineticket/ticketValidate/*")
        ;
        log.debug("add Single SignOn Interceptor");
        registry.addInterceptor(historySignOnAppInterceptor)
                //JWT
                .addPathPatterns("/auth/jwt/*")
                //SAML
/*                .addPathPatterns("/auth/saml20/idpinit/*")*/
                .addPathPatterns("/auth/saml20/v1/assertion")
                //CAS
                .addPathPatterns("/auth/cas/granting")
                .addPathPatterns("/auth/cas/granting**")
                //OAuth
                .addPathPatterns("/auth/oauth/v20/authorize")
        ;
        log.debug("add history SignOn App Interceptor");

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentResolver());
    }

    @Bean
    public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
        return new CurrentUserMethodArgumentResolver();
    }

}
