package com.donny.dendrofinance.data;

import com.donny.dendrofinance.instance.Instance;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionHandler {
    private final Instance CURRENT_INSTANCE;
    private SecretKeySpec aesKey, bflKey;

    public EncryptionHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
    }

    /**
     * @param key a password as a raw byte array
     * @return <code>SecretKeySpec[]</code>
     * 0 = AES key
     * 1 = Blowfish key
     */
    public SecretKeySpec[] getKeys(char[] key) {
        try {
            byte[] hash = new String(key).getBytes(Charset.forName("unicode"));
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

    public void changeKey(char[] newKey) {
        SecretKeySpec[] keys = getKeys(newKey);
        if (keys[0] == null || keys[1] == null) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Password Hashing Failed!");
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
        aesKey = keys[0];
        bflKey = keys[1];
    }

    public String encrypt(String text) {
        try {
            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Cipher bflCipher = Cipher.getInstance("Blowfish");
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            bflCipher.init(Cipher.ENCRYPT_MODE, bflKey);
            return Base64.getEncoder().encodeToString(bflCipher.doFinal(aesCipher.doFinal(text.getBytes(Charset.forName("unicode")))));
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException |
                 NoSuchAlgorithmException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Incorrect password used.");
            CURRENT_INSTANCE.LOG_HANDLER.save();
            ex.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public String decrypt(String text) {
        try {
            Cipher aesCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Cipher bflCipher = Cipher.getInstance("Blowfish");
            aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
            bflCipher.init(Cipher.DECRYPT_MODE, bflKey);
            return new String(aesCipher.doFinal(bflCipher.doFinal(Base64.getDecoder().decode(text))), Charset.forName("Unicode"));
        } catch (BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException |
                 InvalidKeyException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Incorrect password used.");
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
        return null;
    }
}
