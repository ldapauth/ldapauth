package com.ldapauth.controller;

import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;


@Slf4j
@RequestMapping("/cas")
@Controller
public class CASTestController {


    @ResponseBody
    @RequestMapping("/sso")
    public String sso(HttpSession session){
        Assertion assertion = (Assertion)session.getAttribute(AbstractCasFilter.CONST_CAS_ASSERTION);
        AttributePrincipal principal = assertion.getPrincipal();
        String loginName = principal.getName();
        log.info("principal Attributes:{}",principal.getAttributes());
        return "sso-user:"+loginName;

    }

}
