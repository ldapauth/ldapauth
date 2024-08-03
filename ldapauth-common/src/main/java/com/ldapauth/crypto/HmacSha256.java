


package com.ldapauth.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class HmacSha256 {

	public static final String HMAC_ALGORITHM = "HmacSHA256";

	public static byte[]  encode(String message, String secret)
			throws NoSuchAlgorithmException, InvalidKeyException {
        Mac hmac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(), HMAC_ALGORITHM);
        hmac.init(key);

        return hmac.doFinal(message.getBytes());
    }

	public static String encodeBase64(String message, String secret)
			throws NoSuchAlgorithmException, InvalidKeyException {
        return Base64.encodeBase64String(encode(message,secret));
    }

	public static String encodeHex(String message, String secret)
			throws NoSuchAlgorithmException, InvalidKeyException {
        return HexUtils.bytes2HexString(encode(message,secret));
    }

    public static boolean verify(String message, String secret, byte[] hmac)
    		throws InvalidKeyException, NoSuchAlgorithmException {
    	String hmacB64 =Base64.encodeBase64String(hmac);
    	String hmacEncodeB64 =Base64.encodeBase64String(encode(message, secret));
        return hmacB64.equals(hmacEncodeB64);
    }



    public static boolean verifyHex(String message, String secret, String hmacHex)
    		throws InvalidKeyException, NoSuchAlgorithmException {
        return hmacHex.equals(encodeHex(message, secret));
    }

    public static boolean verifyBase64(String message, String secret, String hmacB64)
    		throws InvalidKeyException, NoSuchAlgorithmException {
        return hmacB64.equals(encodeBase64(message, secret));
    }

}
