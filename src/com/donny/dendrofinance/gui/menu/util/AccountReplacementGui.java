package com.donny.dendrofinance.gui.menu.util;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DateRange;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class AccountReplacementGui extends RegisterFrame {
    private final DateRange RANGE;
    private final SearchBox<Account> OLD, NEW;

    public AccountReplacementGui(MainGui caller, Instance curInst) {
        super(caller, "Account Replacement", curInst);

        //draw gui
        {
            RANGE = new DateRange(true);
            OLD = new SearchBox<>("Existing Account", CURRENT_INSTANCE.ACCOUNTS);
            NEW = new SearchBox<>("Replacement Account", CURRENT_INSTANCE.ACCOUNTS);
            JButton go = new JButton("Do Change");
            go.addActionListener(event -> goAction());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        OLD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        NEW, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        go, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                OLD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                NEW, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                go, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }
        RANGE.initDefault(CURRENT_INSTANCE);
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void goAction() {
        LDate[] range = RANGE.getRange(CURRENT_INSTANCE);
        if (range != null) {
            Account a = OLD.getSelectedItem();
            Account b = NEW.getSelectedItem();
            for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TRANSACTIONS.getRange(range[0], range[1])) {
                int index = -1;
                AWColumn column = null;
                BigDecimal value = null;
                for (int i = 0; i < capsule.getAccounts().getSize(); i++) {
                    if (capsule.getAccounts().get(i).ACCOUNT.equals(a)) {
                        index = i;
                        column = capsule.getAccounts().get(i).COLUMN;
                        value = capsule.getAccounts().get(i).VALUE;
                    }
                }
                if (index != -1) {
                    capsule.getAccounts().replace(index, new AccountWrapper(b, column.toString(), value));
                }
            }
        }
    }
}
