package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.HashMap;

public class BalanceSheetGui extends RegisterFrame {
    private final JPanel BACK;
    private final JLabel A, B;
    private final JTextField DATE, SEARCH;
    private final JButton ENTER;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public BalanceSheetGui(MainGui caller, Instance curInst) {
        super(caller, curInst);
        //draw gui
        {
            BACK = new JPanel();

            A = new JLabel("Date");
            B = new JLabel("Search");

            DATE = new JTextField();
            SEARCH = new JTextField();

            ENTER = DendroFactory.getButton("Enter");
            ENTER.addActionListener(event -> updateTable());

            PANE = DendroFactory.getTable(new String[]{
                    "Account", "Debit", "Credit", "Tax", "Tracking"
            }, new Object[][]{}, false);
            TABLE = (JTable) PANE.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();

            //back layout
            {
                GroupLayout main = new GroupLayout(BACK);
                BACK.setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            add(BACK);

            pack();
        }
        DATE.setText(LDate.now(curInst).toDateString());
        updateTable();
    }

    public final void updateTable() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        int y, m, d;
        String[] temp = DATE.getText().split("/");
        y = Integer.parseInt(temp[2]);
        if (CURRENT_INSTANCE.american) {
            m = Integer.parseInt(temp[0]);
            d = Integer.parseInt(temp[1]);
        } else {
            d = Integer.parseInt(temp[0]);
            m = Integer.parseInt(temp[1]);
        }
        HashMap<Account, BigDecimal> acc = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(y, m, d);
        Aggregation<AccountType> accTyp = new Aggregation<>();
        Aggregation<BroadAccountType> typ = new Aggregation<>();
        for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
            if (acc.containsKey(a)) {
                BigDecimal compare = BigDecimal.ONE;
                if (a.getCurrency().getPlaces() > 0) {
                    compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
                }
                if (acc.get(a).compareTo(compare) >= 0 || acc.get(a).compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0) {
                    typ.add(a.getBroadAccountType(), acc.get(a));
                    accTyp.add(a.getAccountType(), acc.get(a));
                    if (a.getName().toLowerCase().contains(SEARCH.getText().toLowerCase())) {
                        switch (a.getBroadAccountType()) {
                            case ASSET, EQUITY_MINUS, EXPENSE -> TABLE_ACCESS.addRow(new String[]{
                                    a.getName(), a.getCurrency().encode(acc.get(a)), "", "", ""
                            });
                            case LIABILITY, EQUITY_PLUS, REVENUE -> TABLE_ACCESS.addRow(new String[]{
                                    a.getName(), "", a.getCurrency().encode(acc.get(a)), "", ""
                            });
                            case GHOST -> TABLE_ACCESS.addRow(new String[]{
                                    a.getName(), "", "", a.getCurrency().encode(acc.get(a)), ""
                            });
                            case TRACKING -> TABLE_ACCESS.addRow(new String[]{
                                    a.getName(), "", "", "", a.getCurrency().encode(acc.get(a))
                            });
                        }
                    }
                }
            }
        }
        if (SEARCH.getText().equals("")) {
            TABLE_ACCESS.addRow(new String[]{});
            for (AccountType type : CURRENT_INSTANCE.ACCOUNT_TYPES) {
                if (accTyp.containsKey(type)) {
                    switch (type.TYPE) {
                        case ASSET, EQUITY_MINUS, EXPENSE -> TABLE_ACCESS.addRow(new String[]{
                                type.NAME, CURRENT_INSTANCE.$(accTyp.get(type)), "", "", ""
                        });
                        case LIABILITY, EQUITY_PLUS, REVENUE -> TABLE_ACCESS.addRow(new String[]{
                                type.NAME, "", CURRENT_INSTANCE.$(accTyp.get(type)), "", ""
                        });
                        case GHOST, TRACKING -> {
                        }
                    }
                }
            }
            TABLE_ACCESS.addRow(new String[]{});
            TABLE_ACCESS.addRow(new String[]{
                    "Assets", CURRENT_INSTANCE.$(typ.get(BroadAccountType.ASSET)), "", "", ""
            });
            TABLE_ACCESS.addRow(new String[]{
                    "Liabilities", "", CURRENT_INSTANCE.$(typ.get(BroadAccountType.LIABILITY)), "", ""
            });
            TABLE_ACCESS.addRow(new String[]{
                    "Equity", "",
                    CURRENT_INSTANCE.$(
                            typ.get(BroadAccountType.EQUITY_PLUS).subtract(typ.get(BroadAccountType.EQUITY_MINUS))
                                    .add(typ.get(BroadAccountType.REVENUE)).subtract(typ.get(BroadAccountType.EXPENSE))
                    ), "", ""
            });
            TABLE_ACCESS.addRow(new String[]{
                    "Compare", CURRENT_INSTANCE.$(typ.get(BroadAccountType.ASSET)),
                    CURRENT_INSTANCE.$(
                            typ.get(BroadAccountType.LIABILITY).add(typ.get(BroadAccountType.EQUITY_PLUS))
                                    .subtract(typ.get(BroadAccountType.EQUITY_MINUS)).add(typ.get(BroadAccountType.REVENUE))
                                    .subtract(typ.get(BroadAccountType.EXPENSE))
                    ),
                    "", ""
            });
        }
    }
}
