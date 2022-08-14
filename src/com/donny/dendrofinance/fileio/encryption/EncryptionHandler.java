package com.donny.dendrofinance.fileio.encryption;

import com.donny.dendrofinance.instance.Instance;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncryptionHandler {
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

    public byte[] encrypt(byte[] bytes) {
        if (keysInitiated()) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return cipher.doFinal(bytes);
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException |
                     NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong attempting to encrypt\n" + e);
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password");
        return null;
    }

    public byte[] decrypt(byte[] bytes) {
        if (keysInitiated()) {
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                return cipher.doFinal(bytes);
            } catch (BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                     IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong attempting to decrypt\n" + e);
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password");
        return null;
    }

    public boolean checkPassword() {
        File directory = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Entries");
        File[] directoryList = directory.listFiles();
        if (directory.isDirectory() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(".xtbl")) {
                    try (DecryptionInputStream test = new DecryptionInputStream(f, CURRENT_INSTANCE)) {
                        if (!test.check()) {
                            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "File: " + f + ", Status: " + test.getStatus());
                            return false;
                        }
                    } catch (FileNotFoundException e) {
                        CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Something has gone horribly wrong");
                        return false;
                    } catch (IOException e) {
                        CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "An error occured while testing password\n" + e);
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }
}
