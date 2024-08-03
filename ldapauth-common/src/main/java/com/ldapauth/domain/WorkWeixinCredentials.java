package com.ldapauth.domain;

public class WorkWeixinCredentials {


    private String app_key;

    private String app_secret;


    private String grant_type = "client_credentials";


    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }

    public static WorkWeixinCredentials newBuilder(String appKey, String appSecret) {
        WorkWeixinCredentials credentials = new WorkWeixinCredentials();
        credentials.setApp_key(appKey);
        credentials.setApp_secret(appSecret);
        return credentials;

    }

}
