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

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.ldapauth.otp.OneTimePassword;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class InMemoryOtpCodeStore  extends AbstractOtpCodeStore {
    private static final  Logger logger = LoggerFactory.getLogger(InMemoryOtpCodeStore.class);

    protected static final Cache<String, OneTimePassword> optCodeStore =
            Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();

    int validitySeconds = 300;

    public InMemoryOtpCodeStore() {
	}

    public InMemoryOtpCodeStore(int validitySeconds) {
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
        optCodeStore.put(otp.getId(), otp);

    }

    @Override
    public boolean validate(String subject, String code, String otpType, String otpMsgType) {
        OneTimePassword otp = optCodeStore.getIfPresent(buildKey(subject,code,otpType,otpMsgType));
        if (otp != null) {
            DateTime currentdateTime = new DateTime();
            DateTime oneCreateTime = DateTime.parse(otp.getCreateTime(),
                    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
            Duration duration = new Duration(oneCreateTime, currentdateTime);
            int intDuration = Integer.parseInt(duration.getStandardSeconds() + "");
            logger.debug("validate duration " + intDuration);
            logger.debug("validate result " + (intDuration <= validitySeconds));
            if (intDuration <= validitySeconds) {
                return true;
            }
            optCodeStore.invalidate(buildKey(subject,code,otpType,otpMsgType));
        }
        return false;
    }


    private String buildKey(String subject, String code, String otpType, String otpMsgType) {
    	return subject + "_" + otpType + "_" + otpMsgType + "_" + code;
    }
}
