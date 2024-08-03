package com.ldapauth.authn.jwt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ldapauth.authn.SignPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 前端认证JWT
 *
 * @author Crystal.Sea
 *
 */
public class AuthJwt implements Serializable {

	private static final long serialVersionUID = -914373258878811144L;

	public static final String ACCESS_TOKEN 	= "access_token";

	public static final String REFRESH_TOKEN 	= "refresh_token";

	public static final String EXPIRES_IN 		= "expired";

	private String ticket;

	private String type = "Bearer";

	private String token;

	@JsonProperty(REFRESH_TOKEN)
	private String refreshToken;

	@JsonProperty(EXPIRES_IN)
	private int expiresIn;

	private String remeberMe;
	private String id;
	private String name;
	private String username;
	private String displayName;
	private String email;
	private int  passwordSetType;
	private List<String> authorities;

	public AuthJwt(String ticket, String type, String token, String refreshToken, int expiresIn, String remeberMe,
			String id, String name, String username, String displayName, String email,
			int passwordSetType, List<String> authorities) {
		super();
		this.ticket = ticket;
		this.type = type;
		this.token = token;
		this.refreshToken = refreshToken;
		this.expiresIn = expiresIn;
		this.remeberMe = remeberMe;
		this.id = id;
		this.name = name;
		this.username = username;
		this.displayName = displayName;
		this.email = email;
		this.passwordSetType = passwordSetType;
		this.authorities = authorities;
	}


	/**
	 * authentication认证构造JWT
	 * @param token
	 * @param authentication
	 * @param expiresIn
	 * @param refreshToken
	 */
	public AuthJwt(String token, Authentication  authentication,int expiresIn,String refreshToken) {
		SignPrincipal principal = ((SignPrincipal)authentication.getPrincipal());

		this.token = token;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;

		this.ticket = principal.getSession().getId();
		this.id = String.valueOf(principal.getUserInfo().getId());
		this.username = principal.getUserInfo().getUsername();
		this.name = this.username;
		this.displayName = principal.getUserInfo().getDisplayName();
		this.email = principal.getUserInfo().getEmail();

		this.authorities = new ArrayList<String>();
		for(GrantedAuthority grantedAuthority :authentication.getAuthorities()) {
			this.authorities.add(grantedAuthority.getAuthority());
		}
	}


	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<String> authorities) {
		this.authorities = authorities;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public int getPasswordSetType() {
		return passwordSetType;
	}

	public void setPasswordSetType(int passwordSetType) {
		this.passwordSetType = passwordSetType;
	}

	public String getRemeberMe() {
		return remeberMe;
	}

	public void setRemeberMe(String remeberMe) {
		this.remeberMe = remeberMe;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public int getExpiresIn() {
		return expiresIn;
	}


	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AuthJwt [token=");
		builder.append(token);
		builder.append(", type=");
		builder.append(type);
		builder.append(", id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", displayName=");
		builder.append(displayName);
		builder.append(", email=");
		builder.append(email);
		builder.append(", authorities=");
		builder.append(authorities);
		builder.append("]");
		return builder.toString();
	}



}
