package com.ldapauth.domain;

import lombok.Data;

@Data
public class Config {

    private String appId;
    private String userinfoSecret;
    private String appClientSecret;
    private String baseUri;
}
