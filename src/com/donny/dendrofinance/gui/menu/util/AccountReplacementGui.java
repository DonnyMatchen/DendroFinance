package com.donny.dendrofinance.gui.menu.util;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.AccountWrapper;

import javax.swing.*;
import java.math.BigDecimal;

public class AccountReplacementGui extends RegisterFrame {
    public final JPanel BACK;
    public final SearchBox OLD, NEW;
    public final JButton GO;

    public AccountReplacementGui(MainGui caller, Instance curInst) {
        super(caller, curInst);

        //draw gui
        {
            BACK = new JPanel();
            OLD = new SearchBox("Existing Account", CURRENT_INSTANCE.getAccountsInUseAsStrings());
            NEW = new SearchBox("Replacement Account", CURRENT_INSTANCE.getAccountsAsStrings());
            GO = new JButton("Do Change");
            GO.addActionListener(event -> {
                Account a = CURRENT_INSTANCE.ACCOUNTS.getElement(OLD.getSelectedItem());
                Account b = CURRENT_INSTANCE.ACCOUNTS.getElement(NEW.getSelectedItem());
                for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                    int index = -1;
                    AccountWrapper.AWType column = null;
                    BigDecimal value = null;
                    for (int i = 0; i < entry.getAccounts().getSize(); i++) {
                        if (entry.getAccounts().get(i).ACCOUNT.equals(a)) {
                            index = i;
                            column = entry.getAccounts().get(i).COLUMN;
                            value = entry.getAccounts().get(i).VALUE;
                        }
                    }
                    if (index != -1) {
                        entry.getAccounts().replace(index, new AccountWrapper(b, column.toString(), value));
                    }
                }
                if (!a.inUse()) {
                    OLD.setMaster(CURRENT_INSTANCE.getAccountsInUseAsStrings());
                } else {
                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "I don't know how you fucked this up.  Account not replaced.");
                }
            });

            //group layout
            {
                GroupLayout main = new GroupLayout(BACK);
                BACK.setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        OLD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        NEW, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        GO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                OLD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                NEW, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                GO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            add(BACK);

            pack();
        }
    }
}
