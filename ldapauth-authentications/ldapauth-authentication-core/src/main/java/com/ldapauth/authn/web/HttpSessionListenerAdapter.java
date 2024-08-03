
package com.ldapauth.authn.web;

import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.ldapauth.authn.SignPrincipal;
import com.ldapauth.util.DateUtils;
import com.ldapauth.web.WebConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

/**
 * 监听会话创建和销毁时间
 *
 * @author Crystal.Sea
 *
 */
@WebListener
public class HttpSessionListenerAdapter implements HttpSessionListener {
    private static final Logger _logger = LoggerFactory.getLogger(HttpSessionListenerAdapter.class);

    public HttpSessionListenerAdapter() {
        super();
        _logger.debug("SessionListenerAdapter inited . ");
    }

    /**
     * session Created
     */
    @Override
    public void sessionCreated(HttpSessionEvent sessionEvent) {
        _logger.trace("new session Created :" + sessionEvent.getSession().getId());
    }

    /**
     * session Destroyed
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent sessionEvent) {
        HttpSession session = sessionEvent.getSession();
        Authentication  authentication  = (Authentication ) session.getAttribute(WebConstants.AUTHENTICATION);
        Object principal  = authentication == null ? null : authentication.getPrincipal();
        _logger.trace("principal {}",principal);
        if(principal != null ) {
        	if(principal instanceof SignPrincipal && ((SignPrincipal)principal).getUserInfo()!=null) {
        		SignPrincipal signPrincipal = (SignPrincipal)principal;
        		_logger.trace("{} HttpSession Id  {} for userId  {} , username {} @Ticket {} Destroyed" ,
        			DateUtils.formatDateTime(new Date()),
        			session.getId(),
        			signPrincipal.getUserInfo().getId(),
        			signPrincipal.getUserInfo().getUsername(),
        			signPrincipal.getSession().getId());
        	}else if(principal instanceof User) {
        		User user = (User)principal;
        		_logger.trace("{} HttpSession Id  {} for username {} password {} Destroyed" ,
        			DateUtils.formatDateTime(new Date()),
        			session.getId(),
        			user.getUsername(),
        			user.getPassword());
        	}else{
        		_logger.trace("{} HttpSession Id  {} for principal {} Destroyed" ,
        			DateUtils.formatDateTime(new Date()),
        			session.getId(),
        			principal);
        	}
        }else {
        	_logger.trace("{} HttpSession Id  {} Destroyed" ,
        			DateUtils.formatDateTime(new Date()),
        			session.getId());
        }
    }

}
