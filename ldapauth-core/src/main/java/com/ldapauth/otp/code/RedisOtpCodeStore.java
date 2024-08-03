/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
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


package com.ldapauth.otp.code;
import com.ldapauth.cache.CacheService;
import com.ldapauth.otp.OneTimePassword;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisOtpCodeStore  extends AbstractOtpCodeStore {

    @Autowired
    CacheService cacheService;


    protected int validitySeconds =  300;

    public static String OTP_KEY_FORMATER = "otp:token:%s:%s:%s";

    public RedisOtpCodeStore() {
		super();
	}

	public RedisOtpCodeStore(int validitySeconds) {
		super();
		this.validitySeconds = validitySeconds;
	}

	@Override
    public void store(String subject, String code, String receiver, String otpType,String otpMsgType) {
        DateTime currentDateTime = new DateTime();
        OneTimePassword otp = new OneTimePassword();
        otp.setId(buildKey(subject,code,otpType,otpMsgType));
        otp.setType(otpType);
        otp.setSubject(subject);
        otp.setCode(code);
        otp.setReceiver(receiver);
        otp.setValidity(validitySeconds);
        otp.setCreateTime(currentDateTime.toString("yyyy-MM-dd HH:mm:ss"));
        cacheService.setCacheObject(buildKey(subject,code,otpType,otpMsgType), otp, validitySeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean validate(String subject, String code, String otpType, String otpMsgType) {
        OneTimePassword otp =  cacheService.getCacheObject(buildKey(subject,code,otpType,otpMsgType));
        if (otp != null && otp.getCode().equals(code)) {
            cacheService.deleteObject(buildKey(subject,code,otpType,otpMsgType));
            return true;
        }
        return false;
    }

    private String buildKey(String subject, String code, String otpType,String otpMsgType) {
    	return String.format(OTP_KEY_FORMATER, otpType, otpMsgType, subject);
    }

}
