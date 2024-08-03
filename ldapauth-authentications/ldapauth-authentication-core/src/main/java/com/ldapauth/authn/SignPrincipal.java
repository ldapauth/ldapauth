


package com.ldapauth.authn;

import java.util.ArrayList;
import java.util.Collection;

import com.ldapauth.authn.session.Session;
import com.ldapauth.pojo.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 登录后颁发签名的当事人，包含用户信息，当前会话，用户角色，用户应用权限等
 *
 * @author Crystal.Sea
 *
 */
public class SignPrincipal implements  UserDetails {
    private static final long serialVersionUID = -110742975439268030L;
    UserInfo userInfo;

    String style;

    UserDetails userDetails;

    Session session;
    ArrayList<GrantedAuthority> grantedAuthority;
    ArrayList<GrantedAuthority> grantedAuthorityApps;
    boolean authenticated;
    boolean roleAdministrators;

	private  boolean accountNonExpired;

	private  boolean accountNonLocked;

	private  boolean credentialsNonExpired;

	private  boolean enabled;

    /**
     * SigninPrincipal.
     */
    public SignPrincipal() {
    }

    /**
     * SignPrincipal.
     */
    public SignPrincipal(UserInfo userInfo) {
        this.userInfo = userInfo;
        this.authenticated = true;
        this.accountNonExpired = true;
        this.accountNonLocked  = true;
        this.credentialsNonExpired =true;
        this.enabled = true;
    }

    public SignPrincipal(UserInfo userInfo,Session session) {
        this.userInfo = userInfo;
        this.authenticated = true;
        this.accountNonExpired = true;
        this.accountNonLocked  = true;
        this.credentialsNonExpired =true;
        this.enabled = true;
        this.session = session;
        this.style = session.getStyle();
    }

    /**
     * SigninPrincipal.
     */
    public SignPrincipal(UserDetails userDetails) {
        this.userDetails = userDetails;
        this.authenticated = true;
    }

    public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthority;
    }

    public ArrayList<GrantedAuthority> getGrantedAuthority() {
        return grantedAuthority;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public void setGrantedAuthority(ArrayList<GrantedAuthority> grantedAuthority) {
        this.grantedAuthority = grantedAuthority;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public boolean isRoleAdministrators() {
        return roleAdministrators;
    }

    public void setRoleAdministrators(boolean roleAdministrators) {
        this.roleAdministrators = roleAdministrators;
    }


    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public ArrayList<GrantedAuthority> getGrantedAuthorityApps() {
        return grantedAuthorityApps;
    }

    public void setGrantedAuthorityApps(ArrayList<GrantedAuthority> grantedAuthorityApps) {
        this.grantedAuthorityApps = grantedAuthorityApps;
    }

    @Override
    public String getUsername() {
        if(this.userInfo != null) {
            return this.userInfo.getUsername();
        }else {
            return this.userDetails.getUsername();
        }
    }

    @Override
    public String getPassword() {
        if(this.userInfo != null) {
            return this.userInfo.getPassword();
        }else {
            return this.userDetails.getPassword();
        }
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SignPrincipal [username=");
		builder.append(getUsername());
		builder.append(", userInfo=");
		builder.append(userInfo);
		builder.append(", userDetails=");
		builder.append(userDetails);
		builder.append(", session=");
		builder.append(session);
		builder.append(", grantedAuthority=");
		builder.append(grantedAuthority);
		builder.append(", grantedAuthorityApps=");
		builder.append(grantedAuthorityApps);
		builder.append(", authenticated=");
		builder.append(authenticated);
		builder.append(", roleAdministrators=");
		builder.append(roleAdministrators);
		builder.append(", accountNonExpired=");
		builder.append(accountNonExpired);
		builder.append(", accountNonLocked=");
		builder.append(accountNonLocked);
		builder.append(", credentialsNonExpired=");
		builder.append(credentialsNonExpired);
		builder.append(", enabled=");
		builder.append(enabled);
		builder.append("]");
		return builder.toString();
	}



}
