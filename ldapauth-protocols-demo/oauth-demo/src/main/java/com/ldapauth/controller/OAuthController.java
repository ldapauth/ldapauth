package com.ldapauth.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;


@RequestMapping("/oauth")
@Controller
@Slf4j
public class OAuthController {

    @Value("${oauth.login-uri}")
    String redirectUri;

    @Value("${oauth.token-uri}")
    String tokenUri;

    @Value("${oauth.userinfo-uri}")
    String userinfoUri;

    @ResponseBody
    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request){
        log.info("redirect idp:{}",redirectUri);
        return new ModelAndView("redirect:"+redirectUri);
    }



    @ResponseBody
    @RequestMapping("/sso")
    public String sso(HttpServletRequest request){
        String code = request.getParameter("code");

        String formatTokenUri = String.format(tokenUri,code);
        log.info("formatTokenUri:{}",formatTokenUri);
        String tokenResponce = HttpUtil.post(formatTokenUri,new HashMap<>());
        log.info("tokenResponce:{}",tokenResponce);
        JSONObject jsonObject = JSONObject.parseObject(tokenResponce);
        String token = jsonObject.getString("access_token");


        String formatUserinfoUri = String.format(userinfoUri,token);
        log.info("formatUserinfoUri:{}",formatUserinfoUri);
        String userResponce = HttpUtil.post(formatUserinfoUri,new HashMap<>());
        log.info("userResponce:{}",userResponce);
        JSONObject userjsonObject = JSONObject.parseObject(userResponce);
        return "sso-user:"+userjsonObject.getString("user");
    }

}
