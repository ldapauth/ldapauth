package com.ldapauth.authz.saml20.metadata.domain;


import lombok.Data;

import java.io.Serializable;

/**
 * Saml20Metadata.
 * @author Crystal.Sea
 *
 */
@Data
public class Saml20Metadata implements Serializable {
    private static final long serialVersionUID = -403743150268165622L;

    public static  final class ContactPersonType {
        public static  final  String TECHNICAL = "technical";
        public static  final  String SUPPORT = "support";
        public static  final  String ADMINISTRATIVE = "administrative";
        public static final  String BILLING = "billing";
        public static final  String OTHER = "other";
    }

    private String orgName;
    private String orgDisplayName;
    private String orgURL;
    private String contactType;
    private String company;
    private String givenName;
    private String surName;
    private String emailAddress;
    private String telephoneNumber;


}
