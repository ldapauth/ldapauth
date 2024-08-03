package com.ldapauth.authn.session;

import java.time.LocalDateTime;

/**
 * 会话管理接口
 *
 * @author Crystal.Sea
 *
 */
 public interface SessionManager {

     void create(String sessionId, Session session);
     Session remove(String sessionId);
     Session get(String sessionId);

     Session refresh(String sessionId ,LocalDateTime refreshTime);

     Session refresh(String sessionId);
}
