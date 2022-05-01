package com.donny.dendrofinance.gui;

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

public class ProfileGui extends JDialog {
    private final Instance CURRENT_INSTANCE;

    private final JPanel FLAGS;
    private final JLabel A, B, C, D, E, F;
    private final JCheckBox LOG, EXP, AMER, DAY;
    private final JButton SAVE, CANCEL;
    private final JTextField NAME, PRECISION, LOG_LEVEL, CUR, CUR2;
    private final PasswordGui CALLER;

    public ProfileGui(PasswordGui caller, JsonObject config, Instance curInst) {
        super(caller, "Profile Gui", true);
        CURRENT_INSTANCE = curInst;
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

            LOG = new JCheckBox("Log");
            EXP = new JCheckBox("Export");
            AMER = new JCheckBox("US Date Format");
            DAY = new JCheckBox("Use day not time");

            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> save());
            CANCEL = DendroFactory.getButton("Cancel");
            CANCEL.addActionListener(event -> dispose());

            NAME = new JTextField();
            PRECISION = new JTextField();
            LOG_LEVEL = new JTextField();
            CUR = new JTextField();
            CUR2 = new JTextField();

            //group layouts
            {
                //flags
                {
                    GroupLayout flags = new GroupLayout(FLAGS);
                    FLAGS.setLayout(flags);
                    flags.setHorizontalGroup(
                            flags.createSequentialGroup().addContainerGap().addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            LOG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            EXP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DAY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                    flags.setVerticalGroup(
                            flags.createSequentialGroup().addContainerGap().addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            LOG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            AMER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    flags.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            EXP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DAY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
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
        if (config.FIELDS.containsKey("name")) {
            NAME.setText(config.getString("name").getString());
        }
        if (config.FIELDS.containsKey("flags")) {
            String flags = config.getString("flags").getString();
            if (flags.contains("l")) {
                LOG.setSelected(false);
            }
            if (flags.contains("L")) {
                LOG.setSelected(true);
            }
            if (flags.contains("X")) {
                EXP.setSelected(true);
            }
            if (flags.contains("x")) {
                EXP.setSelected(false);
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
        }
        if (config.FIELDS.containsKey("precision")) {
            PRECISION.setText(config.getString("precision").getString());
        }
        if (config.FIELDS.containsKey("log")) {
            LOG_LEVEL.setText(config.getString("log").getString());
        }
        if (config.FIELDS.containsKey("main")) {
            CUR.setText(config.getString("main").getString());
        }
        if (config.FIELDS.containsKey("main__")) {
            CUR2.setText(config.getString("main__").getString());
        }
    }

    public void save() {
        for (int i = 0; i < CALLER.PROFILES.size(); i++) {
            if (CALLER.PROFILES.get(i).FIELDS.containsKey("name")) {
                if (CALLER.PROFILES.get(i).getString("name").getString().equals(NAME.getText())) {
                    CALLER.PROFILES.remove(i);
                    break;
                }
            }
        }
        try {
            JsonObject config = new JsonObject();
            config.FIELDS.put("name", new JsonString(Validation.validateString(NAME)));
            String flagsS = "";
            if (LOG.isSelected()) {
                flagsS += "L";
            } else {
                flagsS += "l";
            }
            if (EXP.isSelected()) {
                flagsS += "X";
            } else {
                flagsS += "x";
            }
            if (AMER.isSelected()) {
                flagsS += "A";
            } else {
                flagsS += "a";
            }
            if (DAY.isSelected()) {
                flagsS += "D";
            } else {
                flagsS += "d";
            }
            config.FIELDS.put("flags", new JsonString(flagsS));
            config.FIELDS.put("precision", new JsonDecimal(new BigDecimal(Validation.validateInteger(PRECISION))));
            config.FIELDS.put("log", new JsonString(Validation.validateString(LOG_LEVEL)));
            config.FIELDS.put("main", new JsonString(Validation.validateString(CUR)));
            config.FIELDS.put("main__", new JsonString(Validation.validateString(CUR2)));
            CALLER.addProfile(config, true);
            dispose();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "Mis-formatted Profiles!\n" + e);
            CURRENT_INSTANCE.LOG_HANDLER.save();
            System.exit(1);
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(this.getClass(), "validation failed!\n" + e);
        }
    }
}
