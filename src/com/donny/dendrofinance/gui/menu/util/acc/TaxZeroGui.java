package com.donny.dendrofinance.gui.menu.util.acc;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.types.LString;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.HashMap;

public class TaxZeroGui extends RegisterFrame {
    private final JTextField DATE;
    private final JLabel A;
    private final JButton SAVE;

    public TaxZeroGui(MainGui caller, Instance curInst) {
        super(caller, curInst);

        //draw gui
        {
            DATE = new JTextField();
            A = new JLabel("Date");
            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> {
                BigDecimal net = BigDecimal.ZERO;
                HashMap<Account, BigDecimal> accounts = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(new LDate(DATE.getText(), CURRENT_INSTANCE));
                StringBuilder sb = new StringBuilder();
                for (Account a : accounts.keySet()) {
                    if (a.getBroadAccountType() == BroadAccountType.GHOST) {
                        sb.append(", G!").append(a.getName()).append("(").append(accounts.get(a).multiply(BigDecimal.valueOf(-1))).append(")");
                    }
                }
                TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
                entry.insert(
                        new LDate(DATE.getText(), CURRENT_INSTANCE),
                        new LString("ACC"),
                        new LString(""),
                        new LString("Tax Zeroing"),
                        new LAccountSet(sb.substring(2), CURRENT_INSTANCE)
                );
                CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
                caller.updateTable();
                dispose();
            });

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DATE, 250, 250, Short.MAX_VALUE
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
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }

        LDate date = LDate.now(CURRENT_INSTANCE);
        date = new LDate(date.getYear(), date.getMonth(), LDate.lastDay(date.getYear(), date.getMonth(), CURRENT_INSTANCE), 23, 59, 59, 500, CURRENT_INSTANCE);
        DATE.setText(date.toString());

        pack();
    }
}
