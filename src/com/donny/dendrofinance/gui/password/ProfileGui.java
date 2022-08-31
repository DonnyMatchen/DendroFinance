package com.donny.dendrofinance.gui.password;

import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ProfileGui extends ModalFrame {
    private final JCheckBox LOG, AMER, DAY, LARGE;
    private final JTextField NAME, PRECISION, LOG_LEVEL, CUR, CUR2, BLOCK;
    private final PasswordGui CALLER;

    public ProfileGui(PasswordGui caller, JsonObject config, Instance curInst) {
        super(caller, "Profile Gui", curInst);
        CALLER = caller;
        {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JPanel flags = new JPanel();

            JLabel a = new JLabel("Name");
            JLabel b = new JLabel("Flags");
            JLabel c = new JLabel("Precision");
            JLabel d = new JLabel("Log Level");
            JLabel e = new JLabel("Main Currency");
            JLabel f = new JLabel("Main Extra");
            JLabel g = new JLabel("Block Size");

            LOG = new JCheckBox("Log");
            AMER = new JCheckBox("US Date Format");
            DAY = new JCheckBox("Use Day Not Time");
            LARGE = new JCheckBox("Large");

            NAME = new JTextField();
            PRECISION = new JTextField();
            LOG_LEVEL = new JTextField();
            CUR = new JTextField();
            CUR2 = new JTextField();
            BLOCK = new JTextField();

            JButton save = DendroFactory.getButton("Save");
            save.addActionListener(event -> save());
            JButton cancel = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());

            //group layouts
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
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            LOG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            LARGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    );
                    layout.setVerticalGroup(
                            layout.createSequentialGroup().addContainerGap().addGroup(
                                    layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            LOG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            DAY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            LARGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
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
                                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                                        LOG_LEVEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                                d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                LOG_LEVEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
            }
        }
        if (config.containsKey("name")) {
            NAME.setText(config.getString("name").getString());
        }
        if (config.containsKey("flags")) {
            String flags = config.getString("flags").getString();
            if (flags.contains("l")) {
                LOG.setSelected(false);
            }
            if (flags.contains("L")) {
                LOG.setSelected(true);
            }
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
            if (flags.contains("X")) {
                LARGE.setSelected(true);
            }
            if (flags.contains("x")) {
                LARGE.setSelected(false);
            }
        }
        if (config.containsKey("precision")) {
            PRECISION.setText("" + config.getDecimal("precision").decimal);
        }
        if (config.containsKey("log")) {
            LOG_LEVEL.setText(config.getString("log").getString());
        }
        if (config.containsKey("main")) {
            CUR.setText(config.getString("main").getString());
        }
        if (config.containsKey("main__")) {
            CUR2.setText(config.getString("main__").getString());
        }
        if (config.containsKey("block")) {
            BLOCK.setText("" + config.getDecimal("block").decimal);
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
            if (LOG.isSelected()) {
                flags += "L";
            } else {
                flags += "l";
            }
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
            if (LARGE.isSelected()) {
                flags += "X";
            } else {
                flags += "x";
            }
            config.put("flags", new JsonString(flags));
            config.put("precision", new JsonDecimal(new BigDecimal(Validation.validateInteger(PRECISION))));
            config.put("log", new JsonString(Validation.validateString(LOG_LEVEL)));
            config.put("main", new JsonString(Validation.validateString(CUR)));
            config.put("main__", new JsonString(Validation.validateString(CUR2)));
            config.put("block", new JsonDecimal(new BigDecimal(Validation.validateInteger(BLOCK))));
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
