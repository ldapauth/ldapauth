package com.ldapauth.authz.oauth2.jwt.crypto.sign;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;

/**
 * Verifies signatures using an RSA public key.
 *
 * The key can be supplied directly, or as an SSH public or private key string (in
 * the standard format produced by <tt>ssh-keygen</tt>).
 *
 * @author Luke Taylor
 */
public class RsaVerifier implements SignatureVerifier {
	private final RSAPublicKey key;
	private final String algorithm;

	public RsaVerifier(BigInteger n, BigInteger e) {
		this(RsaKeyHelper.createPublicKey(n, e));
	}

	public RsaVerifier(RSAPublicKey key) {
		this(key, RsaSigner.DEFAULT_ALGORITHM);
	}

	public RsaVerifier(RSAPublicKey key, String algorithm) {
		this.key = key;
		this.algorithm = algorithm;
	}

	public RsaVerifier(String key) {
		this(RsaKeyHelper.parsePublicKey(key.trim()), RsaSigner.DEFAULT_ALGORITHM);
	}

	public void verify(byte[] content, byte[] sig) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(key);
			signature.update(content);

			if (!signature.verify(sig)) {
				throw new InvalidSignatureException("RSA Signature did not match content");
			}
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public String algorithm() {
		return algorithm;
	}
}
