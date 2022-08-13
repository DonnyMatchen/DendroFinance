package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.instance.Instance;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionHandler {
    /**
     * @param key a password as a raw byte array
     * @return <code>SecretKeySpec[]</code>
     * 0 = AES key
     * 1 = Blowfish key
     */
    public static SecretKeySpec[] getKeys(char[] key) {
        try {
            byte[] hash = new String(key).getBytes(StandardCharsets.UTF_16);
            MessageDigest sha = MessageDigest.getInstance("SHA-512");
            hash = sha.digest(hash);
            byte[] aesRawKey = new byte[32];
            byte[] bflRawKey = new byte[32];
            System.arraycopy(hash, 0, aesRawKey, 0, 32);
            System.arraycopy(hash, 32, bflRawKey, 0, 32);
            Arrays.fill(key, (char) 0);
            return new SecretKeySpec[]{
                    new SecretKeySpec(aesRawKey, "AES"),
                    new SecretKeySpec(bflRawKey, "Blowfish")
            };
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Arrays.fill(key, (char) 0);
            return null;
        }
    }

    private final Instance CURRENT_INSTANCE;
    private SecretKeySpec aesKey, bflKey;

    public EncryptionHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
    }

    public void changeKey(char[] newKey) {
        SecretKeySpec[] keys = getKeys(newKey);
        if (keys[0] == null || keys[1] == null) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Password Hashing Failed!");
            aesKey = null;
            bflKey = null;
        } else {
            aesKey = keys[0];
            bflKey = keys[1];
        }
    }

    public boolean keysInitiated() {
        return aesKey != null && bflKey != null;
    }

    public String encrypt(byte[] bytes) {
        if (keysInitiated()) {
            try {
                Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                Cipher bflCipher = Cipher.getInstance("Blowfish");
                aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
                bflCipher.init(Cipher.ENCRYPT_MODE, bflKey);
                return Base64.getEncoder().encodeToString(bflCipher.doFinal(aesCipher.doFinal(bytes)));
            } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException |
                     NoSuchAlgorithmException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong attempting to encrypt");
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password");
        return null;
    }

    public byte[] decrypt(String text) {
        if (keysInitiated()) {
            try {
                Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                Cipher bflCipher = Cipher.getInstance("Blowfish");
                aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
                bflCipher.init(Cipher.DECRYPT_MODE, bflKey);
                return aesCipher.doFinal(bflCipher.doFinal(Base64.getDecoder().decode(text)));
            } catch (BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                     IllegalBlockSizeException |
                     InvalidKeyException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong attempting to decrypt");
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
