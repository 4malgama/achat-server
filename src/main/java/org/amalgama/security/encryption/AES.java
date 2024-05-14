package org.amalgama.security.encryption;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AES {
    private byte[] key;
    private AESMode mode;
    private String evp;
    public byte[] iv;

    public AES(AESMode mode) {
        this.mode = mode;
        evp = getEVP();
    }

    public AES(byte[] key, AESMode mode) {
        this.key = key;
        this.mode = mode;
        evp = getEVP();
    }

    public AES(byte[] key, AESMode mode, byte[] iv) {
        this.key = key;
        this.mode = mode;
        this.iv = iv;
        evp = getEVP();
    }

    public static byte[] generateKey(int bits) throws Exception {
        if (bits < 16 || bits % 8 != 0)
            throw new IllegalArgumentException("Invalid bits");
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(bits, secureRandom);
        return keyGenerator.generateKey().getEncoded();
    }

    public void applyKey(String key) {
        this.key = key.getBytes();
    }

    public static byte[] generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public AESMode getMode() {
        return mode;
    }

    public void setMode(AESMode mode) {
        this.mode = mode;
    }

    public byte[] encrypt(String text) throws Exception {
        return encrypt(text.getBytes());
    }

    public byte[] encrypt(byte[] bytes) throws Exception {
        if (!checkKey() || bytes.length == 0)
            throw new IllegalArgumentException("Invalid key or text");
        if (mode == AESMode.CBC && !checkIV())
            throw new IllegalArgumentException("Invalid IV");
        Cipher cipher = Cipher.getInstance(evp);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        if (mode == AESMode.ECB) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        }
        else {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        }
        return cipher.doFinal(bytes);
    }

    private boolean checkIV() {
        return (iv != null && iv.length == 16);
    }

    //checks for valid valid key (128/192/256 bits)
    private boolean checkKey() {
        return (key != null && key.length > 8 && key.length % 8 == 0);
    }

    public static byte[] encrypt(String text, byte[] key, AESMode mode) throws Exception {
        return new AES(key, mode).encrypt(text);
    }

    public static byte[] encrypt(String text, byte[] key, byte[] iv,AESMode mode) throws Exception {
        return new AES(key, mode, iv).encrypt(text);
    }

    public byte[] decrypt(byte[] bytes) throws Exception {
        if (!checkKey() || bytes.length == 0)
            throw new IllegalArgumentException("Invalid key or text");
        if (mode == AESMode.CBC && !checkIV())
            throw new IllegalArgumentException("Invalid IV");
        Cipher cipher = Cipher.getInstance(evp);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        if (mode == AESMode.ECB) {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        }
        else {
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        }
        return cipher.doFinal(bytes);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key, AESMode mode) throws Exception {
        return new AES(key, mode).decrypt(ciphertext);
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key, byte[] iv, AESMode mode) throws Exception {
        return new AES(key, mode, iv).decrypt(ciphertext);
    }

    private String getEVP() {
        switch (mode) {
            case CBC:
                return "AES/CBC/PKCS5Padding";
            default:
                return "AES/ECB/PKCS5Padding";
        }
    }

    public String getEvp() {
        return evp;
    }
}
