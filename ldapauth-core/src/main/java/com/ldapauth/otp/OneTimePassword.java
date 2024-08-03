/*
 * Copyright [2022] [MaxKey of copyright http://www.maxkey.top]
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

package com.ldapauth.otp;

import java.io.Serializable;

public class OneTimePassword implements Serializable {
	private static final long serialVersionUID = -1637133296702014021L;
	private String id;
	private String subject;
	private String userId;
	private String email;
	private String mobile;

	private String type;
	private String code;
	private long validity;

	private String receiver;
	private String createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public long getValidity() {
		return validity;
	}

	public void setValidity(long validity) {
		this.validity = validity;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OneTimePassword [id=");
		builder.append(id);
		builder.append(", username=");
		builder.append(subject);
		builder.append(", userId=");
		builder.append(userId);
		builder.append(", email=");
		builder.append(email);
		builder.append(", mobile=");
		builder.append(mobile);
		builder.append(", type=");
		builder.append(type);
		builder.append(", code=");
		builder.append(code);
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
