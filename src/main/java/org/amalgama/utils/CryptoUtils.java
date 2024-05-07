package org.amalgama.utils;

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

    /**
     * Generates a Base64 encoded string from the given byte array data.
     *
     * @param  data  the byte array to be encoded
     * @return       the Base64 encoded string representation of the input data
     */
    public static String getBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
