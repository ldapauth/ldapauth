package com.ldapauth.authz.oauth2.jwt.crypto.cipher;

import com.ldapauth.authz.oauth2.jwt.AlgorithmMetadata;

/**
 * @author Luke Taylor
 */
public interface CipherMetadata extends AlgorithmMetadata {
	/**
	 * @return Size of the key in bits.
	 */
	int keySize();
}
