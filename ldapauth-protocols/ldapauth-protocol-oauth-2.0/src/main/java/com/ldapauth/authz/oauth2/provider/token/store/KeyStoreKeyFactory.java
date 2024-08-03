package com.ldapauth.authz.oauth2.provider.token.store;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;

import org.springframework.core.io.Resource;

/**
 * Factory for RSA key pairs from a JKS keystore file. User provides a {@link Resource} location of a keystore file and
 * the password to unlock it, and the factory grabs the keypairs from the store by name (and optionally password).
 *
 * @author Dave Syer
 *
 */
public class KeyStoreKeyFactory {

	private Resource resource;

	private char[] password;

	private KeyStore store;

	private Object lock = new Object();

	public KeyStoreKeyFactory(Resource resource, char[] password) {
		this.resource = resource;
		this.password = password;
	}

	public KeyPair getKeyPair(String alias) {
		return getKeyPair(alias, password);
	}

	public KeyPair getKeyPair(String alias, char[] password) {
		try {
			synchronized (lock) {
				if (store == null) {
					synchronized (lock) {
						store = KeyStore.getInstance("jks");
						store.load(resource.getInputStream(), this.password);
					}
				}
			}
			RSAPrivateCrtKey key = (RSAPrivateCrtKey) store.getKey(alias, password);
			RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
			PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);
			return new KeyPair(publicKey, key);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot load keys from store: " + resource, e);
		}
	}

}
