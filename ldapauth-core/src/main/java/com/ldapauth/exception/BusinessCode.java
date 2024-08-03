package com.ldapauth.exception;

public enum BusinessCode {

    USER_FORBIDDEN(500008, "账号被禁用"),

    USERNAME_USED(500009, "该登录名称已被使用"),

    MOBILE_USED(500010, "该手机号码已被使用"),

    EMAIL_USED(500011, "该邮箱地址已被使用"),

    USER_VERIFY_MOBILE_ABSENT(500005, "该手机号尚未绑定任何用户");

    String msg;
    Integer code;

    private BusinessCode(Integer code, String msg) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public Integer getCode() {
        return this.code;
    }
}
