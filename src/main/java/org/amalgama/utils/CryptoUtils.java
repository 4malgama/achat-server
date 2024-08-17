package org.amalgama.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CryptoUtils {
    public static String getHash(String data, String algorithm) {
        return getHash(data.getBytes(), algorithm);
    }

    public static String getHash(byte[] data, String algorithm) {
        if (data == null)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] hash = md.digest(data);
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static byte[] MD5(byte[] data) {
        if (data == null)
            return null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Generates a Base64 encoded string from the given byte array data.
     *
     * @param  data  the byte array to be encoded
     * @return       the Base64 encoded string representation of the input data
     */
    public static String getBase64(byte[] data) {
        if (data == null)
            return null;
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Generates a Base64 encoded string from the given String data.
     *
     * @param  data  the String to be encoded
     * @return       the Base64 encoded string representation of the input data
     */
    public static String getBase64(String data) {
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static byte[] fromBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    public static String toHex(byte[] bytes) {
        BigInteger bigInteger = new BigInteger(1, bytes);
        String hex = bigInteger.toString(16);

        int paddingLength = (bytes.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static String getSHA256(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(string.getBytes("UTF-8"));
            return toHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
