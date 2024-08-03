package com.ldapauth.domain;

import lombok.Data;

@Data
public class AppData {

    private String clientId;
    private String clientSecret;
    private String appClientSecret;
    private String serverUri;
}
