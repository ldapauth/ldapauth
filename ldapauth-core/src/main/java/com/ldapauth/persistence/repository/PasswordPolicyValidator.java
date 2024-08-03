package com.ldapauth.persistence.repository;

import com.ldapauth.constants.ConstsStatus;
import com.ldapauth.persistence.service.PolicyLoginService;
import com.ldapauth.persistence.service.PolicyPasswordService;
import com.ldapauth.pojo.entity.PolicyLogin;
import com.ldapauth.pojo.entity.PolicyPassword;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.web.WebContext;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Objects;

public class PasswordPolicyValidator {
    private static Logger _logger = LoggerFactory.getLogger(PasswordPolicyValidator.class);

    PolicyRepository passwordPolicyRepository;

    protected PolicyPasswordService policyPasswordService;

    MessageSource messageSource;

    public PasswordPolicyValidator() {
    }

    public PasswordPolicyValidator(PolicyLoginService policyLoginService, PolicyPasswordService policyPasswordService, MessageSource messageSource) {
        this.messageSource=messageSource;
        this.policyPasswordService = policyPasswordService;
        this.passwordPolicyRepository = new PolicyRepository(policyLoginService,policyPasswordService);

    }

    /**
     * static validator .
     * @return boolean
     */
    public boolean validator() {

       /* String password = changePassword.getPassword();
        String username = changePassword.getUsername();

        if(StringUtils.isBlank(password)){
            _logger.debug("password  is Empty ");
            return false;
        }

        PasswordValidator validator = new PasswordValidator(
                new PasswordPolicyMessageResolver(messageSource),passwordPolicyRepository.getPasswordPolicyRuleList());

        RuleResult result = validator.validate(new PasswordData(username,password));

        if (result.isValid()) {
            _logger.debug("Password is valid");
            return true;
        } else {
            _logger.debug("Invalid password:");
            String passwordPolicyMessage = "";
            int i = 1;
            for (String msg : validator.getMessages(result)) {
                passwordPolicyMessage += i+"."+ msg + " ";
                i++;
                _logger.debug("Rule Message {}" , msg);
            }
            WebContext.setAttribute(PasswordPolicyValidator.PASSWORD_POLICY_VALIDATE_RESULT, passwordPolicyMessage);
            return false;
        }*/
        return true;
    }


    /**
     * dynamic passwordPolicy Valid for user login.
     * @param userInfo
     * @return boolean
     */
    public boolean passwordPolicyValid(UserInfo userInfo) {

        PolicyLogin policyLogin = passwordPolicyRepository.getPolicyLogin();

        DateTime currentdateTime = new DateTime();
        int badPasswordCount = userInfo.getBadPasswordCount();
        if (badPasswordCount <= 0) {
            badPasswordCount = 1;
        } else {
            badPasswordCount = badPasswordCount + 1;
        }
        /*

         * check login attempts fail times
         */
        if (badPasswordCount > policyLogin.getPasswordAttempts() && userInfo.getBadPasswordTime() != null) {
            _logger.debug("login Attempts is {}" , userInfo.getBadPasswordCount());
            //duration
            _logger.trace("bad Password Time {}" , userInfo.getBadPasswordTime());

            DateTime badPasswordTime = new DateTime(userInfo.getBadPasswordTime());
            Duration duration = new Duration(badPasswordTime, currentdateTime);
            int intDuration = Integer.parseInt(duration.getStandardMinutes() + "");
            _logger.debug("bad Password duration {} , " +
                            "password policy Duration {} , "+
                            "validate result {}" ,
                    intDuration,
                    policyLogin.getLoginLockInterval(),
                    (intDuration > policyLogin.getLoginLockInterval())
            );
            //auto unlock attempts when intDuration >= set Duration
            if(intDuration >= policyLogin.getLoginLockInterval()) {
                _logger.debug("resetAttempts ...");
                resetAttempts(userInfo);
            }else {
                lockUser(userInfo);
                throw new BadCredentialsException(
                        WebContext.getI18nValue("login.error.attempts",
                                new Object[]{userInfo.getBadPasswordCount(), policyLogin.getLoginLockInterval()})
                );
            }
        }

        //locked
        if(userInfo.getIsLocked() == ConstsStatus.LOCK) {
            throw new BadCredentialsException(
                    userInfo.getUsername()+ " "+
                            WebContext.getI18nValue("login.error.locked")
            );
        }
        // inactive
        if(userInfo.getStatus() != ConstsStatus.DATA_ACTIVE) {
            throw new BadCredentialsException(
                    userInfo.getUsername()+
                            WebContext.getI18nValue("login.error.inactive")
            );
        }
        return true;
    }

    public void applyPasswordPolicy(UserInfo userInfo) {
        resetBadPasswordCount(userInfo);
    }

    /**
     * lockUser
     *
     * @param userInfo
     */
    public void lockUser(UserInfo userInfo) {
        try {
            passwordPolicyRepository.updateLockStatus(userInfo.getId(),ConstsStatus.LOCK);
            userInfo.setIsLocked(ConstsStatus.LOCK);
        } catch (Exception e) {
            _logger.error("lockUser Exception",e);
        }
    }


    /**
     * unlockUser
     *
     * @param userInfo
     */
    public void unlockUser(UserInfo userInfo) {
        try {
            if (userInfo != null && Objects.nonNull(userInfo.getId())) {
                passwordPolicyRepository.updateLockStatus(userInfo.getId(),ConstsStatus.ACTIVE);
                userInfo.setIsLocked(ConstsStatus.ACTIVE);
            }
        } catch (Exception e) {
            _logger.error("unlockUser Exception",e);
        }
    }

    /**
     * reset BadPasswordCount And Lockout
     *
     * @param userInfo
     */
    public void resetAttempts(UserInfo userInfo) {
        try {
            if (userInfo != null && Objects.nonNull(userInfo.getId())) {
                passwordPolicyRepository.resetAttempts(userInfo.getId(),ConstsStatus.ACTIVE,0);
                userInfo.setIsLocked(ConstsStatus.ACTIVE);
                userInfo.setBadPasswordCount(0);
            }
        } catch (Exception e) {
            _logger.error("resetAttempts Exception",e);
        }
    }

    /**
     * if login password is error ,BadPasswordCount++ and set bad date
     *
     */
    private void setBadPasswordCount(Long userId,int badPasswordCount) {
        try {
            passwordPolicyRepository.setBadPasswordCount(userId,badPasswordCount);
        } catch (Exception e) {
            _logger.error("setBadPasswordCount Exception",e);
        }
    }

    public void plusBadPasswordCount(UserInfo userInfo) {
        if (userInfo != null && Objects.nonNull(userInfo.getId())) {
           userInfo.setBadPasswordCount(userInfo.getBadPasswordCount() + 1);
            setBadPasswordCount(userInfo.getId(),userInfo.getBadPasswordCount());
            PolicyLogin policyLogin = passwordPolicyRepository.getPolicyLogin();
            if(userInfo.getBadPasswordCount() >= policyLogin.getPasswordAttempts()) {
                _logger.debug("Bad Password Count {} , Max Attempts {}",
                        userInfo.getBadPasswordCount() + 1,policyLogin.getPasswordAttempts());
                this.lockUser(userInfo);
            }
        }
    }

    public void resetBadPasswordCount(UserInfo userInfo) {
        if (userInfo != null && Objects.nonNull(userInfo.getId())) {
            if(userInfo.getBadPasswordCount() > 0) {
                setBadPasswordCount(userInfo.getId(),0);
            }
        }
    }


    public PolicyRepository getPasswordPolicyRepository() {
        return passwordPolicyRepository;
    }

}
