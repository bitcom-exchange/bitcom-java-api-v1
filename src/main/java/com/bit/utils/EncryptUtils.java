package com.bit.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {
    public static String HMAC_ALGO_SHA256 = "HmacSHA256";

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) {
        Mac mac = null;
        try {
            mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(message);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // TODO: send alarm
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static String calcHmacWithBase64(String secretKey, String strToSign) {
        var hash = hmac(HMAC_ALGO_SHA256, secretKey.getBytes(), strToSign.getBytes());
        return byteArrayToHex(hash);
    }
}
