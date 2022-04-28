package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.data.LogHandler;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;

/**
 * @author Donny
 */
public class PasswordGui extends JFrame {
    public final String[] ARGS;
    public final ArrayList<JsonObject> PROFILES;
    private final Instance CURRENT_INSTANCE;
    //Swin components
    private final JLabel A, B;
    private final JPasswordField PASSWORD;
    private final JComboBox<String> PROFILE;
    private final JButton ENTER, NEW_INSTANCE, EDIT_PROFILE, NEW_PROFILE;
    //other variables
    public boolean done = false;
    //key ring
    private SecretKeySpec aesKey, bflKey;

    public PasswordGui(String[] args, Instance curInst) {
        super("Log In");
        CURRENT_INSTANCE = curInst;
        PROFILES = new ArrayList<>();
        ARGS = args;
        //draw gui
        {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            A = new JLabel("Password");

            B = new JLabel("Profile");

            PASSWORD = new JPasswordField();
            PASSWORD.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    if (event.getKeyCode() == 10) {
                        enterPressed();
                    }
                }
            });

            PROFILE = new JComboBox<>();
            File file = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "profiles.json");
            if (file.exists()) {
                try {
                    JsonArray array = (JsonArray) JsonItem.sanitizeDigest(CURRENT_INSTANCE.FILE_HANDLER.read(file));
                    for (JsonObject obj : array.getObjectArray()) {
                        addProfile(obj, false);
                    }
                } catch (JsonFormattingException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Mis-formatted Profiles!\n" + e);
                    CURRENT_INSTANCE.LOG_HANDLER.save();
                    System.exit(1);
                }
            }

            ENTER = DendroFactory.getButton("Enter");
            ENTER.addActionListener(event -> enterPressed());

            NEW_INSTANCE = DendroFactory.getButton("New Instance");
            NEW_INSTANCE.addActionListener(event -> newInstance());

            EDIT_PROFILE = DendroFactory.getButton("Edit Profile");
            EDIT_PROFILE.addActionListener(event -> editProfile());

            NEW_PROFILE = DendroFactory.getButton("New Profile");
            NEW_PROFILE.addActionListener(event -> newProfile());

            {
                GroupLayout backgroundLayout = new GroupLayout(getContentPane());
                getContentPane().setLayout(backgroundLayout);
                backgroundLayout.setHorizontalGroup(
                        backgroundLayout.createSequentialGroup().addContainerGap().addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addGap(
                                        DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        PASSWORD, GroupLayout.PREFERRED_SIZE, 230, Short.MAX_VALUE
                                ).addComponent(
                                        ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        EDIT_PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NEW_PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NEW_INSTANCE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                backgroundLayout.setVerticalGroup(
                        backgroundLayout.createSequentialGroup().addContainerGap().addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        EDIT_PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PASSWORD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NEW_PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addGap(
                                        DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                ).addComponent(
                                        ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NEW_INSTANCE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }

            //add(BACK);

            pack();
        }
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "PasswordGui created");
    }

    /**
     * @param key a password as a raw byte array
     * @return <code>SecretKeySpec[]</code>
     * 0 = AES key
     * 1 = Blowfish key
     */
    private static SecretKeySpec[] setKey(char[] key) {
        try {
            byte[] hash = new String(key).getBytes(Charset.forName("unicode"));
            MessageDigest sha = MessageDigest.getInstance("SHA-512");
            hash = sha.digest(hash);
            byte[] aesHash = new byte[32];
            byte[] bflHash = new byte[32];
            System.arraycopy(hash, 0, aesHash, 0, 32);
            System.arraycopy(hash, 32, bflHash, 0, 32);
            Arrays.fill(key, (char) 0);
            return new SecretKeySpec[]{
                    new SecretKeySpec(aesHash, "AES"),
                    new SecretKeySpec(bflHash, "Blowfish")
            };
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Arrays.fill(key, (char) 0);
            return null;
        }
    }

    private void enterPressed() {
        for (JsonObject prof : PROFILES) {
            String name = prof.getString("name").getString();
            if (name.equalsIgnoreCase((String) PROFILE.getSelectedItem())) {
                loadConfig(prof);
                break;
            }
        }
        SecretKeySpec[] keys = setKey(PASSWORD.getPassword());
        aesKey = keys[0];
        bflKey = keys[1];
        PASSWORD.setText("");
        if (aesKey == null || bflKey == null) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Password Hashing Failed!");
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
        File directory = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Entries");
        boolean flag = true, noTB = false;
        File[] directoryList = directory.listFiles();
        if (directory.isDirectory() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(".xtbl")) {
                    String plain = CURRENT_INSTANCE.FILE_HANDLER.readDecrypt(f);
                    if (plain != null) {
                        if (plain.indexOf("passwd") != 0) {
                            flag = false;
                            break;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                }
            }
            if (flag) {
                done = true;
                setVisible(false);
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Password Hashing Failed!");
                CURRENT_INSTANCE.LOG_HANDLER.save();
                System.exit(1);
            }
        } else {
            noTB = true;
        }
        if (noTB) {
            done = true;
            setVisible(false);
        }
    }

    public void changeKey(char[] newKey) {
        SecretKeySpec[] keys = setKey(newKey);
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
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException | NoSuchAlgorithmException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Incorrect password used.");
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
        } catch (BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Incorrect password used.");
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
        return null;
    }

    public void loadConfig(JsonObject config) {
        CURRENT_INSTANCE.data = new File(CURRENT_INSTANCE.data.getPath() + File.separator + config.getString("name").getString());
        if (config.FIELDS.containsKey("flags")) {
            String flags = config.getString("flags").getString();
            if (flags.contains("l")) {
                CURRENT_INSTANCE.log = false;
            }
            if (flags.contains("L")) {
                CURRENT_INSTANCE.log = true;
            }
            if (flags.contains("X")) {
                CURRENT_INSTANCE.export = true;
            }
            if (flags.contains("x")) {
                CURRENT_INSTANCE.export = false;
            }
            if (flags.contains("A")) {
                CURRENT_INSTANCE.american = true;
            }
            if (flags.contains("a")) {
                CURRENT_INSTANCE.american = false;
            }
            if (flags.contains("d")) {
                CURRENT_INSTANCE.day = false;
            }
            if (flags.contains("D")) {
                CURRENT_INSTANCE.day = true;
            }
        }
        if (config.FIELDS.containsKey("twelve-data-key")) {
            CURRENT_INSTANCE.twelveDataApiKey = config.getString("twelve-data-key").getString();
        }
        if (config.FIELDS.containsKey("polygon-key")) {
            CURRENT_INSTANCE.polygonApiKey = config.getString("polygon-key").getString();
        }
        if (config.FIELDS.containsKey("stock-api")) {
            CURRENT_INSTANCE.stockAPI = config.getString("stock-api").getString();
        }
        if (config.FIELDS.containsKey("log")) {
            CURRENT_INSTANCE.logLevel = new LogHandler.LogLevel(config.getString("log").getString());
        }
        if (config.FIELDS.containsKey("main")) {
            CURRENT_INSTANCE.mainTicker = (config.getString("main").getString());
            if (config.FIELDS.containsKey("main__")) {
                CURRENT_INSTANCE.main__Ticker = (config.getString("main__").getString());
            } else {
                CURRENT_INSTANCE.main__Ticker = CURRENT_INSTANCE.mainTicker + " Extra";
            }
        }
    }

    public JsonObject getConfig(String name) throws JsonFormattingException {
        JsonObject config = new JsonObject();
        config.FIELDS.put("name", new JsonString(name));
        String flags = "";
        if (CURRENT_INSTANCE.log) {
            flags += "L";
        } else {
            flags += "l";
        }
        if (CURRENT_INSTANCE.export) {
            flags += "X";
        } else {
            flags += "x";
        }
        if (CURRENT_INSTANCE.american) {
            flags += "A";
        } else {
            flags += "a";
        }
        if (CURRENT_INSTANCE.day) {
            flags += "D";
        } else {
            flags += "d";
        }
        config.FIELDS.put("flags", new JsonString(flags));
        config.FIELDS.put("twelve-data-key", new JsonString(CURRENT_INSTANCE.twelveDataApiKey));
        config.FIELDS.put("polygon-key", new JsonString(CURRENT_INSTANCE.polygonApiKey));
        config.FIELDS.put("stock-api", new JsonString(CURRENT_INSTANCE.stockAPI));
        config.FIELDS.put("log", new JsonString(CURRENT_INSTANCE.logLevel.getName()));
        config.FIELDS.put("main", new JsonString(CURRENT_INSTANCE.mainTicker));
        config.FIELDS.put("main__", new JsonString(CURRENT_INSTANCE.main__Ticker));
        return config;
    }

    public void newInstance() {
        new Thread(() -> DendroFinance.main(ARGS)).start();
    }

    public void newProfile() {
        try {
            new ProfileGui(this, getConfig("DEFAULT"), CURRENT_INSTANCE).setVisible(true);
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Mis-formatted Profiles!\n" + e);
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
    }

    public void editProfile() {
        String name = (String) PROFILE.getSelectedItem();
        boolean flag = true;
        for (JsonObject prof : PROFILES) {
            if (prof.FIELDS.containsKey("name")) {
                if (prof.getString("name").getString().equalsIgnoreCase(name)) {
                    new ProfileGui(this, prof, CURRENT_INSTANCE).setVisible(true);
                    flag = false;
                    break;
                }
            }
        }
        if (flag) {
            newProfile();
        }
    }

    public final void addProfile(JsonObject config, boolean save) {
        boolean flag = true;
        for (JsonObject profile : PROFILES) {
            if (profile.getString("name").getString().equals(
                    config.getString("name").getString()
            )) {
                flag = false;
            }
        }
        if (flag) {
            PROFILES.add(config);
        }
        PROFILES.sort(Comparator.comparing(obj -> obj.getString("name").getString()));
        PROFILE.removeAllItems();
        for (JsonObject prof : PROFILES) {
            PROFILE.addItem(prof.getString("name").getString());
        }
        if (save) {
            JsonArray out = new JsonArray();
            out.ARRAY.addAll(PROFILES);
            File file = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "profiles.json");
            CURRENT_INSTANCE.FILE_HANDLER.write(file, out.print());
        }
    }
}
