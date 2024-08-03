package com.ldapauth.synchronizer.ldap.domain;

import lombok.Data;

import java.util.List;

@Data
public class LdapGroup {

    String nameInNamespace;

    String cn;

    String entryUUID;

    List<String> members;
}
