package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.DoubleAggregation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.Vector;

public class RevExpGui extends RegisterFrame {
    private final JTextField START, END;
    private final JLabel A, B, C, D;
    private final JComboBox<String> PERIOD, TYPE;
    private final JScrollPane PANE;
    private final JTable TABLE;

    public RevExpGui(MainGui caller, Instance curInst) {
        super(caller, "Revenue and Expenses", curInst);

        //draw GUI
        {
            PERIOD = new JComboBox<>();
            PERIOD.addItem("Yearly");
            PERIOD.addItem("Semiannually");
            PERIOD.addItem("Quarterly");
            PERIOD.addItem("Monthly");
            C = new JLabel("Period");
            PERIOD.addItemListener(event -> updateTable());
            A = new JLabel("From");
            START = new JTextField();
            START.setText(CURRENT_INSTANCE.DATA_HANDLER.getPrior().getDate().tomorrow().toDateString());
            B = new JLabel("End");
            END = new JTextField();
            END.setText(LDate.now(CURRENT_INSTANCE).toDateString());
            TYPE = new JComboBox<>();
            TYPE.addItem("All");
            for (String s : CURRENT_INSTANCE.DATA_HANDLER.getBudgetTypes()) {
                TYPE.addItem(s);
            }
            D = new JLabel("Budget");
            TYPE.addItemListener(event -> updateTable());
            TABLE = new JTable();
            TABLE.setCellSelectionEnabled(true);
            TABLE.setColumnSelectionAllowed(true);
            TABLE.getTableHeader().setReorderingAllowed(false);
            TABLE.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            PANE = DendroFactory.getScrollPane(false, true);
            PANE.setViewportView(TABLE);
            updateTable();

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                START, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.LARGE_GAP).addComponent(
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                END, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addComponent(
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        START, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        END, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            pack();
        }
    }

    private DefaultTableModel tableAccess() {
        return (DefaultTableModel) TABLE.getModel();
    }

    private void updateTable() {
        String per = Objects.requireNonNull((String) PERIOD.getSelectedItem(), "Yearly");
        DoubleAggregation<Account, String> data = new DoubleAggregation<>();
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
            if (!entry.getEntity().equals("PRIOR") && !entry.getDescription().equalsIgnoreCase("Net Income")) {
                boolean flag = true;
                String typ = Objects.requireNonNullElse((String) TYPE.getSelectedItem(), "All");
                if (!START.getText().equals("")) {
                    String[] starter = START.getText().split("/");
                    if (CURRENT_INSTANCE.american) {
                        if (entry.getDate().compare(Integer.parseInt(starter[2]), Integer.parseInt(starter[0]), Integer.parseInt(starter[1])) < 0) {
                            flag = false;
                        }
                    } else {
                        if (entry.getDate().compare(Integer.parseInt(starter[2]), Integer.parseInt(starter[1]), Integer.parseInt(starter[0])) < 0) {
                            flag = false;
                        }
                    }
                }
                if (!START.getText().equals("")) {
                    String[] ender = END.getText().split("/");
                    if (CURRENT_INSTANCE.american) {
                        if (entry.getDate().compare(Integer.parseInt(ender[2]), Integer.parseInt(ender[0]), Integer.parseInt(ender[1])) > 0) {
                            flag = false;
                        }
                    } else {
                        if (entry.getDate().compare(Integer.parseInt(ender[2]), Integer.parseInt(ender[1]), Integer.parseInt(ender[0])) > 0) {
                            flag = false;
                        }
                    }
                }
                if (flag) {
                    for (int i = 0; i < entry.getAccounts().getSize(); i++) {
                        AccountWrapper a = entry.getAccounts().get(i);
                        if (typ.equals("All")) {
                            flag = true;
                        } else {
                            flag = a.ACCOUNT.getBudgetType().equals(typ);
                        }
                        if ((a.ACCOUNT.getBroadAccountType() == BroadAccountType.REVENUE || a.ACCOUNT.getBroadAccountType() == BroadAccountType.EXPENSE) && flag) {
                            switch (per) {
                                case "Yearly" -> data.add(a.ACCOUNT, entry.getDate().getYear() + "", a.getAlphaProcessed());
                                case "Semiannually" -> data.add(a.ACCOUNT, entry.getDate().getYear() + " " + entry.getDate().getSemi(), a.getAlphaProcessed());
                                case "Quarterly" -> data.add(a.ACCOUNT, entry.getDate().getYear() + " " + entry.getDate().getQuarter(), a.getAlphaProcessed());
                                case "Monthly" -> data.add(a.ACCOUNT, entry.getDate().getYear() + " " + entry.getDate().getMonth(), a.getAlphaProcessed());
                            }
                        }
                    }
                }
            }
        }
        ArrayList<String> periods = data.innerKeySet();
        periods.sort(Comparator.naturalOrder());
        Vector<String> header = new Vector<>(periods);
        header.add(0, "Account");
        TABLE.setModel(new DefaultTableModel(header, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
            if (data.containsKey(a)) {
                Vector<String> row = new Vector<>();
                row.add(a.getName());
                for (String p : periods) {
                    row.add(CURRENT_INSTANCE.$(data.get(a).get(p)));
                }
                tableAccess().addRow(row);
            }
        }
    }
}
