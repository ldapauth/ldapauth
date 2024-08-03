package com.ldapauth.synchronizer.ldap.domain;

import lombok.Data;

@Data
public class LdapUser {

    String nameInNamespace;
    String uid;
    String displayName;
    String cn;
    String initials;
    String sn;
    String mobile;
    String email;
    String parentNameInNamespace;
    String entryUUID;
    String parentEntryUUID;
}
