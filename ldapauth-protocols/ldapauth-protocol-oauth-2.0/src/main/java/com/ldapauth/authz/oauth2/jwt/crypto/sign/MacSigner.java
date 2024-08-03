package com.ldapauth.authz.oauth2.jwt.crypto.sign;

import java.security.GeneralSecurityException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author Luke Taylor
 */
public class MacSigner implements SignerVerifier {
	private static final String DEFAULT_ALGORITHM = "HMACSHA256";

	private final String algorithm;
	private final SecretKey key;

	public MacSigner(byte[] key) {
		this(new SecretKeySpec(key, DEFAULT_ALGORITHM));
	}

	public MacSigner(String key) {
		this(new SecretKeySpec(key.getBytes(), DEFAULT_ALGORITHM));
	}

	public MacSigner(SecretKey key) {
		this(DEFAULT_ALGORITHM, key);
	}

	public MacSigner(String algorithm, SecretKey key) {
		this.key = key;
		this.algorithm = algorithm;
	}

//	val keyLength = key.getEncoded.length * 8

	public byte[] sign(byte[] bytes) {
		try {
			Mac mac = Mac.getInstance(algorithm);
			mac.init(key);
			return mac.doFinal(bytes);
		}
		catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

  public void verify(byte[] content, byte[] signature) {
    byte[] signed = sign(content);
    if (!isEqual(signed, signature)) {
      throw new InvalidSignatureException("Calculated signature did not match actual value");
    }
  }

  private boolean isEqual(byte[] b1, byte[] b2) {
    if (b1.length != b2.length) {
      return false;
    }
    int xor = 0;
    for (int i = 0; i < b1.length; i++) {
      xor |= b1[i] ^ b2[i];
    }

    return xor == 0;
  }

	public String algorithm() {
		return algorithm;
	}

	@Override
	public String toString() {
		return "MacSigner: " + algorithm;
	}
}
