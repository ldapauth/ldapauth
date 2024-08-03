


package com.ldapauth.password.onetimepwd;

import java.io.Serializable;

public class OneTimePassword  implements Serializable {
    private static final long serialVersionUID = -1637133296702014021L;
    private String id;
    private String username;
    private String userId;
    private String email;
    private String mobile;

    private String type;
    private String token;
    private long   validity;

    private String receiver;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public long getValidity() {
		return validity;
	}

	public void setValidity(long validity) {
		this.validity = validity;
	}


	public OneTimePassword() {
		super();
	}

	public OneTimePassword(String id) {
		super();
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OneTimePassword [id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(username);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", email=");
		builder.append(email);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", type=");
		builder.append(type);
		builder.append(", token=");
		builder.append(token);
		builder.append(", validity=");
		builder.append(validity);
		builder.append(", receiver=");
		builder.append(receiver);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append("]");
		return builder.toString();
	}

}
