package com.ldapauth.synchronizer.ldap.domain;

import lombok.Data;

@Data
public class LdapOrg {

    String nameInNamespace;
    String ou;
    String parentNameInNamespace;
    String entryUUID;
    String parentEntryUUID;
    Integer order;

}
