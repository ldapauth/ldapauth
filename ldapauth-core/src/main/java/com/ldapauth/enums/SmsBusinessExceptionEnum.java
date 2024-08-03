package com.ldapauth.enums;

/**
 * @description:
 * @author: orangeBabu
 * @time: 12/7/2024 PM3:08
 */
public enum SmsBusinessExceptionEnum {
    NOT_CNF_ERROR(500001,"无法读取配置源，请联系管理员配置" ),
    NET_ERROR(500002,"网络异常，无法请求邮件服务器" ),
    EMAIl_REQUIRED_ERROR_SUBJECT(500003,"邮件主题不能为空" ),
    EMAIl_REQUIRED_ERROR_CONTENT(500004,"邮件内容不能为空" ),
    EMAIl_REQUIRED_ERROR_TO(500005,"收件人不能为空" ),

    SMS_REQUIRED_ERROR_TEMPLATE_ID(500006,"阿里云短信模板ID不能为空" ),

    SMS_REQUIRED_ERROR_TO(500007,"手机号码不能为空" ),

    SMS_ALIYUN_ERROR(500008,"阿里云短信发送失败"),

    RES_LOCK_ERROR(500009,"切勿频繁触触发发送,请重新尝试"),

    LIMIT_ERROR(500010,"短信发送限流，请稍后再试");

    String msg;
    Integer code;

    SmsBusinessExceptionEnum(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }

}
