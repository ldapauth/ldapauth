package com.ldapauth.authz.oauth2.jwt.crypto.sign;

/**
 * @author Luke Taylor
 */
public class InvalidSignatureException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 7078872206905207270L;

    public InvalidSignatureException(String message) {
        super(message);
    }
}
