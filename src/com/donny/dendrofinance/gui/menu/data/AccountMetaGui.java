package com.donny.dendrofinance.gui.menu.data;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;

import javax.swing.*;
import java.io.File;

public class AccountMetaGui extends RegisterFrame {
    private final JScrollPane GIFT_PANE, BRAVE_PANE;
    private final JTextArea GIFT, BRAVE;
    private final JLabel A, B;
    private final JButton SAVE;

    private final JsonObject ACCOUNT_OBJECT;
    private final File ACCOUNTS;

    public AccountMetaGui(MainGui caller, Instance curInst) {
        super(caller, "Accounts Metadata Edit", curInst);

        //draw gui
        {
            GIFT_PANE = DendroFactory.getLongField();
            GIFT = (JTextArea) GIFT_PANE.getViewport().getView();
            BRAVE_PANE = DendroFactory.getLongField();
            BRAVE = (JTextArea) BRAVE_PANE.getViewport().getView();
            A = new JLabel("Gift Cards");
            B = new JLabel("Brave Devices");
            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> saveAction());

            //group layout
            {
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
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        GIFT_PANE, 500, 500, Short.MAX_VALUE
                                                ).addComponent(
                                                        BRAVE_PANE, 500, 500, Short.MAX_VALUE
                                                )
                                        )
                                ).addComponent(
                                        SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        GIFT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        BRAVE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            ACCOUNTS = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json");
            ACCOUNT_OBJECT = (JsonObject) CURRENT_INSTANCE.FILE_HANDLER.readJson(ACCOUNTS);
            GIFT.setText(ACCOUNT_OBJECT.getArray("gift-cards").toString());
            BRAVE.setText(ACCOUNT_OBJECT.getArray("brave-mobile").toString());

            pack();
        }
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
