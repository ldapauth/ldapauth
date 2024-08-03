package com.ldapauth.controller;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;


@RequestMapping("/oidc")
@Controller
@Slf4j
public class OIDCController {

    @Value("${oidc.login-uri}")
    String redirectUri;

    @Value("${oidc.token-uri}")
    String tokenUri;

    @Value("${oidc.userinfo-uri}")
    String userinfoUri;

    @Value("${oidc.kid}")
    String kid;

    @ResponseBody
    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request){
        log.info("redirect idp:{}",redirectUri);
        return new ModelAndView("redirect:"+redirectUri);
    }



    @ResponseBody
    @RequestMapping("/sso")
    public String sso(HttpServletRequest request) {
        String code = request.getParameter("code");

        String formattokenUri = String.format(tokenUri,code);
        log.info("tokenUri:{}",formattokenUri);
        String tokenResponce = HttpUtil.post(formattokenUri,new HashMap<>());
        log.info("tokenResponce:{}",tokenResponce);
        JSONObject jsonObject = JSONObject.parseObject(tokenResponce);
        String token = jsonObject.getString("access_token");
        String formatUserinfoUri = String.format(userinfoUri,token);

        String id_token = jsonObject.getString("id_token");
        //idtoken签名解密
        if (Objects.nonNull(id_token)) {
            parseIdToken(id_token);
        }
        log.info("userinfoUri:{}",formatUserinfoUri);
        String userResponce = HttpUtil.post(formatUserinfoUri,new HashMap<>());
        log.info("userResponce:{}",userResponce);
        JSONObject userjsonObject = JSONObject.parseObject(userResponce);


        return "sso-user:"+userjsonObject.getString("user");
    }


    private boolean parseIdToken(String idToken){
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            log.info("verify begin..");
            RSASSAVerifier rsaKey = getRSASSAVerifier();
            if (Objects.nonNull(rsaKey)) {
                //验签过程
                signedJWT.verify(rsaKey);
                log.info("verify success ");
                //获取token对象
                JWTClaimsSet jwtClaim = signedJWT.getJWTClaimsSet();
                log.info("idToken Issuer {}", jwtClaim.getIssuer());
                log.info("idToken Subject {}", jwtClaim.getSubject());
                log.info("idToken Audience {}", jwtClaim.getAudience());
                log.info("idToken ExpirationTime {}", jwtClaim.getExpirationTime());
                return true;
            }
        } catch (Exception e){
            log.error("验签异常",e);
        }
        return false;
    }

    /**
     * 读取RSA公钥
     * @return
     */
    private RSASSAVerifier getRSASSAVerifier() {
        try {
            File file = ResourceUtils.getFile("classpath:jwk.jwks");
            JWKSet jwkSet= JWKSet.load(file);
            //kid为IDP颁发提供
            RSASSAVerifier rsaSSAVerifier = new RSASSAVerifier(((RSAKey) jwkSet.getKeyByKeyId(kid)).toRSAPublicKey());
            return rsaSSAVerifier;
        } catch (Exception e) {
            log.error("加载RSA公钥失败",e);
        }
        return null;
    }
}
