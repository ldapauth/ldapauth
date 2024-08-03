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


package com.ldapauth.otp;

import com.ldapauth.otp.code.AbstractOtpCodeStore;
import com.ldapauth.util.StringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AbstractOTPAuthn.
 * @author Administrator
 *
 */
public abstract class AbstractOtp {
    private static final  Logger logger = LoggerFactory.getLogger(AbstractOtp.class);

    protected AbstractOtpCodeStore optTokenStore;

    public static final boolean ENABLE_PASS = true;
    public static final String  PASS_CODE   = "139981";

    //验证码有效間隔
    protected int interval = 30;

    // 验证码长度，范围4～10，默认为6
    protected int digits = 6;

    protected String defaultEncoding ="utf-8";

    StringGenerator stringGenerator;

    protected String otpType = OtpTypes.TIMEBASED_OTP;

    public static final class OtpTypes {
        // 手机
        public static String  MOBILE 			= "mobile";
        // 短信
        public static String SMS 				= "sms";
        // 邮箱
        public static String EMAIL 				= "email";
        //TIMEBASED_OPT
        public static String TIMEBASED_OTP 		= "topt";
        // HmacOTP
        public static String HOTP_OTP 			= "hotp";

        public static String RSA_OTP 			= "rsa";

        public static String CAP_OTP 			= "cap";

        public static String MQ_OTP 			= "mq";

    }

    public  static final class OtpMsgTypes {
    	/**
    	 * login
    	 */
    	 public static String  LOGIN 			= "login";
    	 /**
    	  * register
    	  */
    	 public static String  REGISTER  		= "register";
    	 /**
    	  * Forgot Password
    	  */
    	 public static String  FORGOT			= "forgot";
        /**
         * Appeal Form
         */
        public static String  APPEAL			= "appeal";
    }

    public abstract String produce(String subject,String otpMsgType);

    public abstract boolean validate(String subject, String code,String otpMsgType) ;

    protected String defaultProduce(String subject) {
        return genCode(subject);
    }

    /**
     * genToken.
     * @param
     * @return
     */
    protected String genCode(String subject) {
        if (stringGenerator == null) {
            stringGenerator = new StringGenerator(StringGenerator.DEFAULT_CODE_NUMBER, digits);
        }
        String token = stringGenerator.randomGenerate();
        logger.debug("Generator token " + token);
        return token;
    }

    /**
     *  the interval.
     * @return the interval
     */
    public int getInterval() {
        return interval;
    }

    /**
     * interval the interval to set.
     * @param interval the interval to set
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * digits.
     * @return the digits
     */
    public int getDigits() {
        return digits;
    }

    /**
     * digits the digits to set.
     * @param digits the digits to set
     */
    public void setDigits(int digits) {
        this.digits = digits;
    }

    public String getOtpType() {
        return otpType;
    }

    public void setOtpType(String optType) {
        this.otpType = optType;
    }



    public void initPropertys() {

    }

    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

}
