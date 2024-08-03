package com.ldapauth.authz.oauth2.jwt.crypto.sign;

import com.ldapauth.authz.oauth2.jwt.AlgorithmMetadata;

/**
 * @author Luke Taylor
 */
public interface Signer extends AlgorithmMetadata {
	byte[] sign(byte[] bytes);
}
