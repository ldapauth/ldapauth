/*
 * Copyright [2020] [ldapauth of copyright http://www.ldapauth.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.ldapauth.authn.session;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.ldapauth.pojo.entity.client.Client;
import com.ldapauth.web.WebContext;
import org.springframework.security.core.Authentication;

/**
 *
 * 会话属性
 *
 * @author Crystal.Sea
 *
 */
public class Session implements Serializable{
	private static final long   serialVersionUID = 9008067569150338296L;

	public static final  String SESSION_PREFIX = "OT_";

    public static final  int    MAX_EXPIRY_DURATION = 60 * 5; //default 5 minutes.

    public final  class STATUS{
    	 public static final  int    ONLINE 	= 1;
    	 public static final  int    TERMINATE 	= 2;
    	 public static final  int    OFFLINE 	= 7;
    }

    String style;
    /**
     * 会话id
     */
    String id;

    LocalDateTime startTimestamp;

    LocalDateTime lastAccessTime;

    LocalDateTime expiredTime;
    /**
     * 认证信息
     */
    Authentication authentication;

    HashMap<Long , Client> authorizedApps = new HashMap<Long , Client>();

    public class STYLE {
    	public static final String WEB  = "web";
    	public static final String MGMT = "mgmt";
    	public static final String PLAT = "plat";
    }

    public Session() {
        super();
        this.id = WebContext.genId();
        this.startTimestamp = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
    }

    public Session(String sessionId) {
        super();
        this.id = sessionId;
        this.startTimestamp = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
    }

    public Session(String sessionId,Authentication authentication) {
        super();
        this.id = sessionId;
        this.authentication = authentication;
        this.startTimestamp = LocalDateTime.now();
        this.lastAccessTime = LocalDateTime.now();
    }

    public String getId() {
		return id;
	}

	public String getFormattedId() {
        return SESSION_PREFIX + id;
    }

    public void setId(String sessionId) {
        this.id = sessionId;
    }

    public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public LocalDateTime getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(LocalDateTime startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public LocalDateTime getLastAccessTime() {
		return lastAccessTime;
	}

	public void setLastAccessTime(LocalDateTime lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public LocalDateTime getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(LocalDateTime expiredTime) {
		this.expiredTime = expiredTime;
	}

	public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public HashMap<Long, Client> getAuthorizedApps() {
        return authorizedApps;
    }

    public void setAuthorizedApps(HashMap<Long, Client> authorizedApps) {
        this.authorizedApps = authorizedApps;
    }

    public void setAuthorizedApp(Client authorizedApp) {
        this.authorizedApps.put(authorizedApp.getId(), authorizedApp);
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Session [id=");
		builder.append(id);
		builder.append(", startTimestamp=");
		builder.append(startTimestamp);
		builder.append(", lastAccessTime=");
		builder.append(lastAccessTime);
		builder.append("]");
		return builder.toString();
	}


}
