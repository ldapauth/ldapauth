package com.ldapauth.password.onetimepwd.token;

import org.joda.time.DateTime;
import com.ldapauth.constants.ConstsTimeInterval;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.password.onetimepwd.OneTimePassword;
public class RedisOtpTokenStore  extends AbstractOtpTokenStore {

    protected int validitySeconds = ConstsTimeInterval.ONE_MINUTE * 5;

    public RedisOtpTokenStore() {
        super();
    }

    public static String PREFIX = "REDIS_OTP_SERVICE_";

    @Override
    public void store(UserInfo userInfo, String token, String receiver, String type) {

    }

    @Override
    public boolean validate(UserInfo userInfo, String token, String type, int interval) {
        return true;
    }

}
