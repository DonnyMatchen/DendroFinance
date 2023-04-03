package com.donny.dendrofinance.gui.password;

import com.donny.dendrofinance.gui.customswing.ProgramModalFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.instance.Frequency;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ProfileGui extends ProgramModalFrame {
    private final JCheckBox AMER, DAY, AUTO;
    private final JTextField NAME, PRECISION, CUR, CUR2, BLOCK, RANGE;
    private final JComboBox<Frequency> FREQ;
    private final PasswordGui CALLER;

    public ProfileGui(PasswordGui caller, JsonObject config, ProgramInstance curInst) {
        super(caller, "Profile Gui", curInst);
        CALLER = caller;
        {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JTabbedPane back = new JTabbedPane();
            JPanel regular = new JPanel(), database = new JPanel();
            JPanel flags = new JPanel();

            JLabel a = new JLabel("Name");
            JLabel b = new JLabel("Flags");
            JLabel c = new JLabel("Precision");
            JLabel e = new JLabel("Main Currency");
            JLabel f = new JLabel("Main Extra");
            JLabel g = new JLabel("Block Size");

            JLabel h = new JLabel("State Frequency");
            JLabel i = new JLabel("Default Range in Days");

            AMER = new JCheckBox("US Date Format");
            DAY = new JCheckBox("Use Day Not Time");

            NAME = new JTextField();
            PRECISION = new JTextField();
            CUR = new JTextField();
            CUR2 = new JTextField();
            BLOCK = new JTextField();

            FREQ = new JComboBox<>();
            for (Frequency freq : Frequency.values()) {
                FREQ.addItem(freq);
            }
            RANGE = new JTextField();
            AUTO = new JCheckBox("Automatic States");

            JButton save = DendroFactory.getButton("Save"), save2 = DendroFactory.getButton("Save");
            save.addActionListener(event -> save());
            save2.addActionListener(event -> save());
            JButton cancel = DendroFactory.getButton("Cancel"), cancel2 = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());
            cancel2.addActionListener(event -> dispose());

            //group layouts
            {
                //regular tab
                {
                    //flags
                    {
                        GroupLayout layout = new GroupLayout(flags);
                        flags.setLayout(layout);
                        layout.setHorizontalGroup(
                                layout.createSequentialGroup().addContainerGap().addGroup(
                                        layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                DAY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        );
                        layout.setVerticalGroup(
                                layout.createSequentialGroup().addContainerGap().addComponent(
                                        AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                        DAY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addContainerGap()
                        );
                    }

                    GroupLayout main = new GroupLayout(regular);
                    regular.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            g, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            PRECISION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            CUR2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BLOCK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createSequentialGroup().addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    PRECISION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    CUR2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    g, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    BLOCK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                    back.addTab("General", regular);
                }

                //profile tab
                {
                    GroupLayout main = new GroupLayout(database);
                    database.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            h, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            i, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            FREQ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            AUTO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    cancel2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    save2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createSequentialGroup().addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    h, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    FREQ, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    i, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                            AUTO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.MEDIUM_GAP, Short.MAX_VALUE
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            cancel2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            save2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                    back.addTab("Database", database);
                }
            }
            add(back);
        }
        if (config.containsKey("name")) {
            NAME.setText(config.getString("name").getString());
        }
        if (config.containsKey("flags")) {
            String flags = config.getString("flags").getString();
            if (flags.contains("A")) {
                AMER.setSelected(true);
            }
            if (flags.contains("a")) {
                AMER.setSelected(false);
            }
            if (flags.contains("d")) {
                DAY.setSelected(false);
            }
            if (flags.contains("D")) {
                DAY.setSelected(true);
            }
            if (flags.contains("S")) {
                AUTO.setSelected(true);
            }
            if (flags.contains("s")) {
                AUTO.setSelected(false);
            }
        }
        if (config.containsKey("precision")) {
            PRECISION.setText(String.valueOf(config.getDecimal("precision").decimal));
        }
        if (config.containsKey("main")) {
            CUR.setText(config.getString("main").getString());
        }
        if (config.containsKey("main__")) {
            CUR2.setText(config.getString("main__").getString());
        }
        if (config.containsKey("block")) {
            BLOCK.setText(String.valueOf(config.getDecimal("block").decimal));
        }
        if (config.containsKey("freq")) {
            FREQ.setSelectedItem(Frequency.fromString(config.getString("freq").getString()));
        }
        if (config.containsKey("range")) {
            RANGE.setText(String.valueOf(config.getDecimal("range").decimal));
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void save() {
        for (int i = 0; i < CALLER.PROFILES.size(); i++) {
            if (CALLER.PROFILES.get(i).containsKey("name")) {
                if (CALLER.PROFILES.get(i).getString("name").getString().equals(NAME.getText())) {
                    CALLER.PROFILES.remove(i);
                    break;
                }
            }
        }
        try {
            JsonObject config = new JsonObject();
            config.put("name", new JsonString(Validation.validateString(NAME)));
            String flags = "";
            if (AMER.isSelected()) {
                flags += "A";
            } else {
                flags += "a";
            }
            if (DAY.isSelected()) {
                flags += "D";
            } else {
                flags += "d";
            }
            if (AUTO.isSelected()) {
                flags += "S";
            } else {
                flags += "s";
            }
            config.put("flags", new JsonString(flags));
            config.put("precision", new JsonDecimal(new BigDecimal(Validation.validateInteger(PRECISION))));
            config.put("main", new JsonString(Validation.validateString(CUR)));
            config.put("main__", new JsonString(Validation.validateString(CUR2)));
            config.put("block", new JsonDecimal(new BigDecimal(Validation.validateInteger(BLOCK))));
            config.put("freq", new JsonString((FREQ.getSelectedItem()).toString()));
            CURRENT_INSTANCE.range = 90;
            config.put("range", new JsonDecimal(Integer.parseInt(RANGE.getText())));
            CALLER.addProfile(config, true);
            dispose();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Mis-formatted Profiles!\n" + e);
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "validation failed!\n" + e);
        }
    }
}
