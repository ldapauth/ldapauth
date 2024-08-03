package com.ldapauth.autoconfigure;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@AutoConfiguration
public class SwaggerConfig {
	final static Logger _logger = LoggerFactory.getLogger(SwaggerConfig.class);

    @Value("${ldapauth.swagger.title}")
    String title;

    @Value("${ldapauth.swagger.description}")
    String description;

    @Value("${ldapauth.swagger.version}")
    String version;

    @Value("${ldapauth.swagger.enable}")
    boolean enable;

    @Bean
    public GroupedOpenApi userApi(){
        String[] paths = {
        		"/login",
        		"/logout",
        		"/login/**",
				"/apps/**",
				"/file/**",
				"/groups/apps/**",
				"/groups/**",
				"/groups/resource/**",
				"/idp/saml20/metadata/**",
				"/organizations/**",
				"/password/**",
				"/policy/**",
				"/resources/**",
				"/provider/**",
				"/socials-provider/**",
				"/synchronizers/**",
				"/systems/**",
				"/users/**",
				"/token/refresh",
        		"/logout/**",
        		"/authz/**",
        		"/authz/**/**",
        		"/metadata/saml20/**" ,
        		"/onlineticket/validate/**",
        		"/api/connect/v10/userinfo",
        		"/api/oauth/v20/me"

        	};
        String[] packagedToMatch = { "com.ldapauth.web" };
        return GroupedOpenApi.builder().group(title)
                .pathsToMatch(paths)
                .packagesToScan(packagedToMatch).build();
    }

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.components(new Components()
						.addSecuritySchemes("Authorization", // key值，对应接口上方的name
								new SecurityScheme()
										.type(SecurityScheme.Type.APIKEY) //请求认证类型
										.name("Authorization") //密钥名称
										.description("token令牌") //描述
										.in(SecurityScheme.In.HEADER))//API密钥的位置。有效值"query","header"或"cookie"
				);
	}

	@Bean
	public OpenAPI docOpenAPI() {
		return new OpenAPI()
				.info(
					new Info()
						.title(title)
						.description(description)
						.version(version)
						.termsOfService("https://ldapauth.com/")
						.license(
							new License()
								.name("Apache License, Version 2.0")
								.url("http://www.apache.org/licenses/LICENSE-2.0")
						)
				).
				externalDocs(
						new ExternalDocumentation()
						.description("ldapauth.com contact support@maxsso.net")
						.url("https://ldapauth.com/")
				);
	}
}
