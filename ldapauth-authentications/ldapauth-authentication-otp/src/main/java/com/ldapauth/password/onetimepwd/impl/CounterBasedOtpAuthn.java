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


package com.ldapauth.password.onetimepwd.impl;

import com.ldapauth.password.onetimepwd.AbstractOtpAuthn;
import com.ldapauth.pojo.entity.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CounterBasedOtpAuthn extends AbstractOtpAuthn {
    private static final Logger _logger = LoggerFactory.getLogger(CounterBasedOtpAuthn.class);


    public CounterBasedOtpAuthn() {
        otpType = OtpTypes.HOTP_OTP;
    }

    @Override
    public boolean produce(UserInfo userInfo,String otpMsgType) {
        return true;
    }

    @Override
    public boolean validate(UserInfo userInfo, String token) {
        /*_logger.debug("SharedCounter : " + userInfo.getSharedCounter());
        byte[] byteSharedSecret = Base32Utils.decode(userInfo.getSharedSecret());
        String hexSharedSecret = Hex.encodeHexString(byteSharedSecret);
        String counterBasedToken = "";
        if (crypto.equalsIgnoreCase("HmacSHA1")) {
            counterBasedToken = TimeBasedOTP.genOTP(
                    hexSharedSecret,
                    userInfo.getSharedCounter(),
                    "" + digits
                    );
        } else if (crypto.equalsIgnoreCase("HmacSHA256")) {
            counterBasedToken = TimeBasedOTP.genOTPHmacSHA256(
                    hexSharedSecret,
                    userInfo.getSharedCounter(),
                    "" + digits
                    );
        } else if (crypto.equalsIgnoreCase("HmacSHA512")) {
            counterBasedToken = TimeBasedOTP.genOTPHmacSHA512(
                    hexSharedSecret,
                    userInfo.getSharedCounter(),
                    "" + digits
                    );
        }

        _logger.debug("token : " + token);
        _logger.debug("counterBasedToken : " + counterBasedToken);
        if (token.equalsIgnoreCase(counterBasedToken)) {
            return true;
        }*/
        return true;
    }

    @Override
    public boolean sendNotice(String mobile, String templateCode) {
        return false;
    }

}
