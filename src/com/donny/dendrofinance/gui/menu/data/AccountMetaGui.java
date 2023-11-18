package com.donny.dendrofinance.gui.menu.data;

import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;
import com.donny.dendroroot.json.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class AccountMetaGui extends ProgramRegisterFrame {
    private final JTextArea GIFT, BRAVE;

    private final JsonObject ACCOUNT_OBJECT;
    private final File ACCOUNTS;

    public AccountMetaGui(ProgramMainGui caller, ProgramInstance curInst) {
        super(caller, "Accounts Metadata Edit", curInst);

        //draw gui
        {
            JScrollPane giftPane = DendroFactory.getLongField();
            GIFT = (JTextArea) giftPane.getViewport().getView();
            JScrollPane bravePane = DendroFactory.getLongField();
            BRAVE = (JTextArea) bravePane.getViewport().getView();
            JLabel a = new JLabel("Gift Cards");
            JLabel b = new JLabel("Brave Devices");
            JButton save = DendroFactory.getButton("Save");
            save.addActionListener(event -> saveAction());

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
                                                        giftPane, 500, 500, Short.MAX_VALUE
                                                ).addComponent(
                                                        bravePane, 500, 500, Short.MAX_VALUE
                                                )
                                        )
                                ).addComponent(
                                        save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        giftPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        bravePane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            ACCOUNTS = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json");
            ACCOUNT_OBJECT = (JsonObject) CURRENT_INSTANCE.FILE_HANDLER.readJson(ACCOUNTS);
            GIFT.setText(ACCOUNT_OBJECT.getArray("gift-cards").toString());
            BRAVE.setText(ACCOUNT_OBJECT.getArray("brave-mobile").toString());
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void saveAction() {
        try {
            ACCOUNT_OBJECT.put("gift-cards", JsonItem.digest(GIFT.getText()));
            ACCOUNT_OBJECT.put("brave-mobile", JsonItem.digest(BRAVE.getText()));
            CURRENT_INSTANCE.FILE_HANDLER.write(ACCOUNTS, ACCOUNT_OBJECT.print());
            dispose();
        } catch (JsonFormattingException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Accounts.json seems to be damaged: " + ex);
        }
    }
}
