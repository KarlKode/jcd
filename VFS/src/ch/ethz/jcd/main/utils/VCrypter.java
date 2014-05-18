package ch.ethz.jcd.main.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class VCrypter {
    private SecretKeySpec key = null;
    private Cipher cipher = null;

    public VCrypter() {
        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
        }
    }

    public void setKey(String keyString) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return;
        }

        byte[] keyBytes;
        try {
            keyBytes = digest.digest(keyString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            return;
        }

        key = new SecretKeySpec(keyBytes, "AES");
    }

    public byte[] encrypt(byte[] bytes) throws InvalidKeyException {
        if (cipher == null || key == null) {
            throw new InvalidKeyException();
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] decrypt(byte[] bytes) throws InvalidKeyException {
        if (cipher == null || key == null) {
            throw new InvalidKeyException();
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(bytes);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
