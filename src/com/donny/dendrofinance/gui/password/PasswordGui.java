package com.donny.dendrofinance.gui.password;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.data.LogHandler;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Donny
 */
public class PasswordGui extends JFrame {
    public final ArrayList<JsonObject> PROFILES;
    private final Instance CURRENT_INSTANCE;
    private final JLabel A, B;
    private final JPasswordField PASSWORD;
    private final JComboBox<String> PROFILE;
    private final JButton ENTER, NEW_INSTANCE, EDIT_PROFILE, NEW_PROFILE;
    public boolean done = false;

    public PasswordGui(Instance curInst) {
        super("Log In");
        CURRENT_INSTANCE = curInst;
        PROFILES = new ArrayList<>();
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
                    CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Mis-formatted Profiles!\n" + e);
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

            pack();
        }
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "PasswordGui created");
    }

    private void enterPressed() {
        for (JsonObject prof : PROFILES) {
            String name = prof.getString("name").getString();
            if (name.equalsIgnoreCase((String) PROFILE.getSelectedItem())) {
                loadConfig(prof);
                break;
            }
        }
        CURRENT_INSTANCE.ENCRYPTION_HANDLER.changeKey(PASSWORD.getPassword());
        PASSWORD.setText("");
        if (CURRENT_INSTANCE.ENCRYPTION_HANDLER.keysInitiated() && CURRENT_INSTANCE.ENCRYPTION_HANDLER.checkPassword()) {
            done = true;
            setVisible(false);
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Incorrect Password Entered");
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
    }

    public void loadConfig(JsonObject config) {
        CURRENT_INSTANCE.data = new File(CURRENT_INSTANCE.data.getPath() + File.separator + config.getString("name").getString());
        if (config.containsKey("flags")) {
            String flags = config.getString("flags").getString();
            if (flags.contains("l")) {
                CURRENT_INSTANCE.log = false;
            }
            if (flags.contains("L")) {
                CURRENT_INSTANCE.log = true;
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
        if (config.containsKey("precision")) {
            CURRENT_INSTANCE.precision = new MathContext(config.getDecimal("precision").decimal.intValue());
        }
        if (config.containsKey("log")) {
            CURRENT_INSTANCE.logLevel = new LogHandler.LogLevel(config.getString("log").getString());
        }
        if (config.containsKey("main")) {
            CURRENT_INSTANCE.mainTicker = (config.getString("main").getString());
            if (config.containsKey("main__")) {
                CURRENT_INSTANCE.main__Ticker = (config.getString("main__").getString());
            } else {
                CURRENT_INSTANCE.main__Ticker = CURRENT_INSTANCE.mainTicker + " Extra";
            }
        }
    }

    public JsonObject getConfig(String name) throws JsonFormattingException {
        JsonObject config = new JsonObject();
        config.put("name", new JsonString(name));
        CURRENT_INSTANCE.log = false;
        CURRENT_INSTANCE.american = true;
        CURRENT_INSTANCE.day = false;
        config.put("flags", new JsonString("lxAd"));
        CURRENT_INSTANCE.precision = new MathContext(20);
        config.put("precision", new JsonDecimal(BigDecimal.valueOf(20)));
        CURRENT_INSTANCE.logLevel = new LogHandler.LogLevel("info");
        config.put("log", new JsonString("info"));
        CURRENT_INSTANCE.mainTicker = "USD";
        config.put("main", new JsonString("USD"));
        CURRENT_INSTANCE.main__Ticker = "USD Extra";
        config.put("main__", new JsonString("USD Extra"));
        return config;
    }

    public void newInstance() {
        new Thread(() -> DendroFinance.main(CURRENT_INSTANCE.ARGS)).start();
    }

    public void newProfile() {
        try {
            new ProfileGui(this, getConfig("DEFAULT"), CURRENT_INSTANCE).setVisible(true);
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Mis-formatted Profiles!\n" + e);
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        }
    }

    public void editProfile() {
        String name = (String) PROFILE.getSelectedItem();
        boolean flag = true;
        for (JsonObject prof : PROFILES) {
            if (prof.containsKey("name")) {
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
            out.addAll(PROFILES);
            File file = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "profiles.json");
            CURRENT_INSTANCE.FILE_HANDLER.write(file, out.print());
        }
    }
}
