package com.donny.dendroroot.fileio;

import com.donny.dendroroot.instance.Instance;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncryptionHandler {
    private static final String ALGO = "AES/CBC/PKCS5Padding";

    /**
     * @param key a password as a raw byte array
     * @return <code>Object[]</code>
     * 0 = AES key
     * 1 = IV
     */
    public static Object[] getKeys(char[] key) {
        try {
            byte[] hash = new String(key).getBytes(Instance.CHARSET);
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            hash = sha.digest(hash);
            byte[] rawKey = new byte[32];
            byte[] rawIv = new byte[16];
            System.arraycopy(hash, 0, rawKey, 0, 32);
            System.arraycopy(hash, 7, rawIv, 0, 16);
            Arrays.fill(key, (char) 0);
            return new Object[]{
                    new SecretKeySpec(rawKey, "AES"),
                    new IvParameterSpec(rawIv)
            };
        } catch (NoSuchAlgorithmException e) {
            Arrays.fill(key, (char) 0);
            return null;
        }
    }

    private final Instance CURRENT_INSTANCE;
    private SecretKeySpec key;
    private IvParameterSpec iv;

    public EncryptionHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
    }

    public void changeKey(char[] newKey) {
        Object[] keys = getKeys(newKey);
        if (
                keys.length < 2 ||
                        keys[0] == null || keys[1] == null ||
                        !(keys[0] instanceof SecretKeySpec) || !(keys[1] instanceof IvParameterSpec)
        ) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Password Hashing Failed!");
            key = null;
            iv = null;
        } else {
            key = (SecretKeySpec) keys[0];
            iv = (IvParameterSpec) keys[1];
        }
    }

    public boolean keysInitiated() {
        return key != null && iv != null;
    }

    public Cipher getEcnryptionCipher() {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            return cipher;
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Your java vendor does not support \"" + ALGO + "\"\n" + e);
            return null;
        }
    }

    public Cipher getDecryptionCipher() {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            return cipher;
        } catch (InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidAlgorithmParameterException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Your java vendor does not support \"" + ALGO + "\"\n" + e);
            return null;
        }
    }

    public byte[] encrypt(byte[] bytes) {
        if (keysInitiated()) {
            try {
                Cipher cipher = getEcnryptionCipher();
                return cipher.doFinal(bytes);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong attempting to encrypt\n" + e);
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password");
        return new byte[0];
    }

    public byte[] decrypt(byte[] bytes) {
        if (keysInitiated()) {
            try {
                Cipher cipher = getDecryptionCipher();
                return cipher.doFinal(bytes);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong attempting to decrypt\n" + e);
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password");
        return new byte[0];
    }

    public String getDataBaseEncryptionString() {
        return "crypt_key=" + getHexString(key.getEncoded()) + ";crypt_iv=" + getHexString(iv.getIV()) + ";crypt_type=" + ALGO;
    }

    private String getHexString(byte[] key) {
        StringBuilder output = new StringBuilder();
        for (byte b : key) {
            String x = Integer.toHexString(b);
            if (x.length() == 1) {
                output.append("0").append(x);
            } else if (x.length() == 2) {
                output.append(x);
            } else {
                output.append(x.substring(x.length() - 2));
            }
        }
        return output.toString();
    }
}
