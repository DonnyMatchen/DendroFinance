package com.donny.dendrofinance.gui.password;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.instance.Frequency;
import com.donny.dendrofinance.json.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author Donny
 */
public class PasswordGui extends JFrame {
    public final ArrayList<JsonObject> PROFILES;
    private final Instance CURRENT_INSTANCE;
    private final JPasswordField PASSWORD;
    private final JComboBox<String> PROFILE;
    public boolean done = false;

    public PasswordGui(Instance curInst) {
        super("Log In");
        CURRENT_INSTANCE = curInst;
        PROFILES = new ArrayList<>();
        //draw gui
        {
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            JLabel a = new JLabel("Password");
            JLabel b = new JLabel("Profile");

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
                JsonArray array = (JsonArray) CURRENT_INSTANCE.FILE_HANDLER.readJson(file);
                if (array == null) {
                    CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Mis-formatted Profiles");
                    CURRENT_INSTANCE.LOG_HANDLER.save();
                    System.exit(1);
                }
                for (JsonObject obj : array.getObjectArray()) {
                    addProfile(obj, false);
                }
            }

            JButton enter = DendroFactory.getButton("Enter");
            enter.addActionListener(event -> enterPressed());

            JButton newInstance = DendroFactory.getButton("New Instance");
            newInstance.addActionListener(event -> newInstance());

            JButton editProfile = DendroFactory.getButton("Edit Profile");
            editProfile.addActionListener(event -> editProfile());

            JButton newProfile = DendroFactory.getButton("New Profile");
            newProfile.addActionListener(event -> newProfile());

            {
                GroupLayout backgroundLayout = new GroupLayout(getContentPane());
                getContentPane().setLayout(backgroundLayout);
                backgroundLayout.setHorizontalGroup(
                        backgroundLayout.createSequentialGroup().addContainerGap().addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addGap(
                                        DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        PASSWORD, GroupLayout.PREFERRED_SIZE, 230, Short.MAX_VALUE
                                ).addComponent(
                                        enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        editProfile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        newProfile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        newInstance, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                backgroundLayout.setVerticalGroup(
                        backgroundLayout.createSequentialGroup().addContainerGap().addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PROFILE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        editProfile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PASSWORD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        newProfile, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addGap(
                                        DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                ).addComponent(
                                        enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        newInstance, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "PasswordGui created");
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
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
        if (CURRENT_INSTANCE.ENCRYPTION_HANDLER.keysInitiated()) {
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
            if (flags.contains("S")) {
                CURRENT_INSTANCE.auto = true;
            }
            if (flags.contains("s")) {
                CURRENT_INSTANCE.auto = false;
            }
        }
        if (config.containsKey("precision")) {
            CURRENT_INSTANCE.precision = new MathContext(config.getDecimal("precision").decimal.intValue());
        }
        if (config.containsKey("main")) {
            CURRENT_INSTANCE.mainTicker = (config.getString("main").getString());
            if (config.containsKey("main__")) {
                CURRENT_INSTANCE.main__Ticker = (config.getString("main__").getString());
            } else {
                CURRENT_INSTANCE.main__Ticker = CURRENT_INSTANCE.mainTicker + " Extra";
            }
        }
        if (config.containsKey("block")) {
            CURRENT_INSTANCE.blockSize = config.getDecimal("block").decimal.intValue();
        }
        if (config.containsKey("freq")) {
            CURRENT_INSTANCE.freq = Frequency.fromString(config.getString("freq").getString());
        }
        if (config.containsKey("range")) {
            CURRENT_INSTANCE.range = config.getDecimal("range").decimal.intValue();
        }
    }

    public JsonObject getConfig(String name) throws JsonFormattingException {
        JsonObject config = new JsonObject();
        config.put("name", new JsonString(name));
        CURRENT_INSTANCE.american = true;
        CURRENT_INSTANCE.day = false;
        CURRENT_INSTANCE.auto = false;
        config.put("flags", new JsonString("Ads"));
        CURRENT_INSTANCE.precision = new MathContext(20);
        config.put("precision", new JsonDecimal(20));
        CURRENT_INSTANCE.mainTicker = "USD";
        config.put("main", new JsonString("USD"));
        CURRENT_INSTANCE.main__Ticker = "USD Extra";
        config.put("main__", new JsonString("USD Extra"));
        CURRENT_INSTANCE.blockSize = 4;
        config.put("block", new JsonDecimal(4));
        CURRENT_INSTANCE.freq = Frequency.NEVER;
        config.put("freq", new JsonString("NEVER"));
        CURRENT_INSTANCE.range = 90;
        config.put("range", new JsonDecimal(90));
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
