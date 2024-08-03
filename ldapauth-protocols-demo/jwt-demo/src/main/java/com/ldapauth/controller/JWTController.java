package com.ldapauth.controller;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
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


@RequestMapping("/jwt")
@Controller
@Slf4j
public class JWTController {

    @Value("${jwt.tokenName}")
    String jwtName;

    @Value("${jwt.isSign}")
    boolean isSign;

    @Value("${jwt.server}")
    String server;

    @Value("${jwt.kid}")
    String kid;


    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("redirect:" + server);
    }

    @ResponseBody
    @RequestMapping("/sso")
    public String sso(HttpServletRequest request){
        String token = request.getParameter(jwtName);
        log.info("jwt:{}",token);
        JWTClaimsSet jwtClaim = null;
        String user = "";
        if (!isSign) {
            JWT signedJWT = null;
            try {
                signedJWT = JWTParser.parse(token);
                System.out.println("signedJWT " + signedJWT);
                jwtClaim = signedJWT.getJWTClaimsSet();
                log.info("Issuer {}",jwtClaim.getIssuer());
                log.info("Subject {}",jwtClaim.getSubject());
                log.info("Audience {}",jwtClaim.getAudience());
                log.info("ExpirationTime {}",jwtClaim.getExpirationTime());
                user = jwtClaim.getSubject();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        } else {
            SignedJWT signedJWT = null;
            try {

                signedJWT = SignedJWT.parse(token);
                log.info("verify begin..");
                signedJWT.verify(getRSASSAVerifier());
                log.info("verify success ");
                jwtClaim = signedJWT.getJWTClaimsSet();

                log.info("Issuer {}",jwtClaim.getIssuer());
                log.info("Subject {}",jwtClaim.getSubject());
                log.info("Audience {}",jwtClaim.getAudience());
                log.info("ExpirationTime {}",jwtClaim.getExpirationTime());
                user = jwtClaim.getSubject();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }
        return "sso-user:"+user;
    }

    /**
     * 读取RSA公钥
     * @return
     */
    private RSASSAVerifier getRSASSAVerifier() {
        try {
            File file = ResourceUtils.getFile("classpath:jwk.jwks");
            JWKSet jwkSet= JWKSet.load(file);
            RSASSAVerifier rsaSSAVerifier = new RSASSAVerifier(((RSAKey) jwkSet.getKeyByKeyId(kid)).toRSAPublicKey());
            return rsaSSAVerifier;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
