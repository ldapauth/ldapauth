package com.ldapauth.authz.saml.service;

import java.util.UUID;

public class IDService {

	public String generateID() {
		return "MXK_" + UUID.randomUUID().toString();
	}
}
