package com.ldapauth.password.onetimepwd.impl;

import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.password.onetimepwd.AbstractOtpAuthn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailOtpAuthn extends AbstractOtpAuthn {
    private static final Logger _logger = LoggerFactory.getLogger(MailOtpAuthn.class);

    String subject = "One Time PassWord";

    String messageTemplate = "{0} You Token is {1} , it validity in {2}  minutes.";

    public MailOtpAuthn() {
        otpType = OtpTypes.EMAIL;
    }

	public MailOtpAuthn(String subject, String messageTemplate) {
		otpType = OtpTypes.EMAIL;
		this.subject = subject;
		this.messageTemplate = messageTemplate;
	}



	@Override
    public boolean produce(UserInfo userInfo,String otpMsgType) {
        return false;
    }

    @Override
    public boolean validate(UserInfo userInfo, String token) {
        return this.optTokenStore.validate(userInfo, token, OtpTypes.EMAIL, interval);
    }

    @Override
    public boolean sendNotice(String mobile, String templateCode) {
        return false;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }


}
