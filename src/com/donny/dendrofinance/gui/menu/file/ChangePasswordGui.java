package com.donny.dendrofinance.gui.menu.file;

import com.donny.dendrofinance.fileio.encryption.EncryptionHandler;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.AlertGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class ChangePasswordGui extends RegisterFrame {
    private final JPasswordField ORIG, PASS;

    public ChangePasswordGui(MainGui caller, Instance curInst) {
        super(caller, "Change Password", curInst);

        //draw gui
        {
            JLabel a = new JLabel("Current Password");
            JLabel b = new JLabel("New Password");

            ORIG = new JPasswordField();
            PASS = new JPasswordField();

            JButton enter = DendroFactory.getButton("Enter");
            enter.addActionListener(event -> enterAction());

            //group layout
            {
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
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        ORIG, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                ).addComponent(
                                                        PASS, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                )
                                        )
                                ).addComponent(
                                        enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ORIG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PASS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }
        pack();
    }

    private void enterAction() {
        EncryptionHandler test = new EncryptionHandler(CURRENT_INSTANCE);
        test.changeKey(ORIG.getPassword());
        if (test.checkPassword()) {
            CURRENT_INSTANCE.ENCRYPTION_HANDLER.changeKey(PASS.getPassword());
            ORIG.setText("");
            PASS.setText("");
            CURRENT_INSTANCE.save();
            dispose();
        } else {
            ORIG.setText("");
            PASS.setText("");
            new AlertGui(this, "Incorrect Password", CURRENT_INSTANCE).setVisible(true);
        }
    }
}
