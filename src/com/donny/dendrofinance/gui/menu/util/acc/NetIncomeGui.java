package com.donny.dendrofinance.gui.menu.util.acc;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;

public class NetIncomeGui extends ProgramRegisterFrame {
    private final JTextField DATE;

    public NetIncomeGui(ProgramMainGui caller, ProgramInstance curInst) {
        super(caller, "Net Income Automator", curInst);

        //draw gui
        {
            DATE = new JTextField();
            JLabel a = new JLabel("Date");
            JButton save = DendroFactory.getButton("Save");
            save.addActionListener(event -> saveAction());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DATE, 250, 250, Short.MAX_VALUE
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
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }

        LDate date = LDate.now(CURRENT_INSTANCE);
        date = new LDate(date.getYear(), date.getMonth(), LDate.lastDay(date.getYear(), date.getMonth(), CURRENT_INSTANCE), 23, 59, 59, 250, CURRENT_INSTANCE);
        DATE.setText(date.toString());

        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void saveAction() {
        try {
            LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
            BigDecimal net = BigDecimal.ZERO;
            HashMap<Account, BigDecimal> accounts = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(date);
            for (Account a : accounts.keySet()) {
                if (a.getBroadAccountType() == BroadAccountType.REVENUE) {
                    net = net.add(accounts.get(a));
                } else if (a.getBroadAccountType() == BroadAccountType.EXPENSE) {
                    net = net.subtract(accounts.get(a));
                }
            }
            StringBuilder sb = new StringBuilder();
            if (net.compareTo(BigDecimal.ZERO) >= 0) {
                sb.append("C!Net_Income(").append(net).append(")");
            } else {
                sb.append("D!Net_Income(").append(net.abs()).append(")");
            }
            for (Account a : accounts.keySet()) {
                if ((a.getBroadAccountType() == BroadAccountType.REVENUE || a.getBroadAccountType() == BroadAccountType.EXPENSE) && accounts.get(a).compareTo(BigDecimal.ZERO) != 0) {
                    sb.append(", ").append(a.getDefaultColumn(false)).append("!").append(a.getName())
                            .append("(").append(accounts.get(a)).append(")");
                }
            }
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            capsule.insert(
                    date,
                    "ACC",
                    "",
                    "Net Income",
                    new LAccountSet(sb.toString(), CURRENT_INSTANCE)
            );
            CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
            CALLER.updateTable();
            dispose();
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Not a valid date: " + DATE.getText());
        }
    }
}
