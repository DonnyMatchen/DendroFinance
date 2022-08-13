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
import java.math.BigDecimal;

public class ProfileGui extends ModalFrame {
    private final JPanel FLAGS;
    private final JLabel A, B, C, D, E, F, G;
    private final JCheckBox LOG, AMER, DAY, LARGE;
    private final JButton SAVE, CANCEL;
    private final JTextField NAME, PRECISION, LOG_LEVEL, CUR, CUR2, BLOCK;
    private final PasswordGui CALLER;

    public ProfileGui(PasswordGui caller, JsonObject config, Instance curInst) {
        super(caller, "Profile Gui", curInst);
        CALLER = caller;
        {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            FLAGS = new JPanel();

            A = new JLabel("Name");
            B = new JLabel("Flags");
            C = new JLabel("Precision");
            D = new JLabel("Log Level");
            E = new JLabel("Main Currency");
            F = new JLabel("Main Extra");
            G = new JLabel("Block Size");

            LOG = new JCheckBox("Log");
            AMER = new JCheckBox("US Date Format");
            DAY = new JCheckBox("Use day not time");
            LARGE = new JCheckBox("Large");

            NAME = new JTextField();
            PRECISION = new JTextField();
            LOG_LEVEL = new JTextField();
            CUR = new JTextField();
            CUR2 = new JTextField();
            BLOCK = new JTextField();

            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> save());
            CANCEL = DendroFactory.getButton("Cancel");
            CANCEL.addActionListener(event -> dispose());

            //group layouts
            {
                //flags
                {
                    GroupLayout flags = new GroupLayout(FLAGS);
                    FLAGS.setLayout(flags);
                    flags.setHorizontalGroup(
                            flags.createSequentialGroup().addContainerGap().addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DAY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            LOG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            LARGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    );
                    flags.setVerticalGroup(
                            flags.createSequentialGroup().addContainerGap().addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            LOG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
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
                                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        G, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addComponent(
                                                        FLAGS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                                CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createSequentialGroup().addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                FLAGS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                PRECISION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                LOG_LEVEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                CUR2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                G, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addComponent(
                                                BLOCK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }

            pack();
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
            if(flags.contains("X")) {
                LARGE.setSelected(true);
            }
            if(flags.contains("x")) {
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
        if(config.containsKey("block")) {
            BLOCK.setText("" + config.getDecimal("block").decimal);
        }
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
            }else {
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
