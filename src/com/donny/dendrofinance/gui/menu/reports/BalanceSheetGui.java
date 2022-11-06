package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.form.Cleaning;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.HashMap;

public class BalanceSheetGui extends RegisterFrame {
    private final JTextField DATE, FROM, SEARCH, THRESH;
    private final DefaultTableModel TABLE_ACCESS;

    public BalanceSheetGui(MainGui caller, boolean diff, Instance curInst) {
        super(caller, diff ? "Change in Accounts" : "Balance Sheet", curInst);
        //draw gui
        {
            JLabel a = new JLabel("Threshold");
            JLabel b = new JLabel("Date");
            JLabel c = new JLabel("Search");
            JLabel d = new JLabel("From");
            JLabel e = new JLabel("To");

            DATE = new JTextField();
            DATE.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable(diff);
                    }
                }
            });

            FROM = new JTextField();
            FROM.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable(diff);
                    }
                }
            });

            THRESH = new JTextField();
            THRESH.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable(diff);
                    }
                }
            });

            SEARCH = new JTextField();
            SEARCH.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable(diff);
                    }
                }
            });

            JButton enter = DendroFactory.getButton("Enter");
            enter.addActionListener(event -> updateTable(diff));

            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "Account", "Debit", "Credit", "Ghost", "Tracking"
            }, new Object[][]{}, false);
            JTable table = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) table.getModel();

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                if (diff) {
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    THRESH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    FROM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addComponent(
                                            pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            THRESH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            FROM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                } else {
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    THRESH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addComponent(
                                            pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            THRESH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
        }
        DATE.setText(LDate.now(curInst).toDateString());
        FROM.setText(new LDate(LDate.now(curInst).getYear(), 1, 1, curInst).toDateString());
        updateTable(diff);
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public final void updateTable(boolean diff) {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        if (diff) {
            int y1, y2, m1, m2, d1, d2;
            String[] tempFrom = FROM.getText().split("/");
            String[] tempTo = DATE.getText().split("/");
            y1 = Integer.parseInt(tempFrom[2]);
            if (CURRENT_INSTANCE.american) {
                m1 = Integer.parseInt(tempFrom[0]);
                d1 = Integer.parseInt(tempFrom[1]);
            } else {
                d1 = Integer.parseInt(tempFrom[0]);
                m1 = Integer.parseInt(tempFrom[1]);
            }
            y2 = Integer.parseInt(tempTo[2]);
            if (CURRENT_INSTANCE.american) {
                m2 = Integer.parseInt(tempTo[0]);
                d2 = Integer.parseInt(tempTo[1]);
            } else {
                d2 = Integer.parseInt(tempTo[0]);
                m2 = Integer.parseInt(tempTo[1]);
            }
            HashMap<Account, BigDecimal> accBegin = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(y1, m1, d1);
            HashMap<Account, BigDecimal> accEnd = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(y2, m2, d2);
            Aggregation<AccountType> accTyp = new Aggregation<>();
            Aggregation<BroadAccountType> typ = new Aggregation<>();
            for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
                if (accBegin.containsKey(a) || accEnd.containsKey(a)) {
                    BigDecimal compare = BigDecimal.ONE;
                    BigDecimal thresh = Cleaning.cleanNumber(THRESH.getText());
                    if(thresh.compareTo(BigDecimal.ZERO) != 0) {
                        compare = thresh.abs();
                    } else {
                        if (a.getCurrency().getPlaces() > 0) {
                            compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
                        }
                    }
                    BigDecimal begin, end;
                    if (accBegin.containsKey(a) && (accBegin.get(a).compareTo(compare) >= 0
                            || accBegin.get(a).compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0)) {
                        begin = accBegin.get(a);
                    } else {
                        begin = BigDecimal.ZERO;
                    }
                    if (accEnd.containsKey(a) && (accEnd.get(a).compareTo(compare) >= 0
                            || accEnd.get(a).compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0)) {
                        end = accEnd.get(a);
                    } else {
                        end = BigDecimal.ZERO;
                    }
                    BigDecimal change = end.subtract(begin);
                    if (change.compareTo(compare) >= 0 || change.compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0) {
                        typ.add(a.getBroadAccountType(), change);
                        accTyp.add(a.getAccountType(), change);
                        if (
                                a.getName().toLowerCase().contains(SEARCH.getText().toLowerCase()) ||
                                        a.getCurrency().toString().toLowerCase().contains(SEARCH.getText().toLowerCase()) ||
                                        a.getAccountType().toString().toLowerCase().contains(SEARCH.getText().toLowerCase()) ||
                                        a.getBroadAccountType().toString().toLowerCase().contains(SEARCH.getText().toLowerCase())
                        ) {
                            switch (a.getBroadAccountType()) {
                                case ASSET, EQUITY_MINUS, EXPENSE -> TABLE_ACCESS.addRow(new String[]{
                                        a.getName(), a.getCurrency().encode(change), "", "", ""
                                });
                                case LIABILITY, EQUITY_PLUS, REVENUE -> TABLE_ACCESS.addRow(new String[]{
                                        a.getName(), "", a.getCurrency().encode(change), "", ""
                                });
                                case GHOST -> TABLE_ACCESS.addRow(new String[]{
                                        a.getName(), "", "", a.getCurrency().encode(change), ""
                                });
                                case TRACKING -> TABLE_ACCESS.addRow(new String[]{
                                        a.getName(), "", "", "", a.getCurrency().encode(change)
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
        } else {
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
                    BigDecimal thresh = Cleaning.cleanNumber(THRESH.getText());
                    if(thresh.compareTo(BigDecimal.ZERO) != 0) {
                        compare = thresh.abs();
                    } else {
                        if (a.getCurrency().getPlaces() > 0) {
                            compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
                        }
                    }
                    if (acc.get(a).compareTo(compare) >= 0 || acc.get(a).compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0) {
                        typ.add(a.getBroadAccountType(), acc.get(a));
                        accTyp.add(a.getAccountType(), acc.get(a));
                        if (
                                a.getName().toLowerCase().contains(SEARCH.getText().toLowerCase()) ||
                                        a.getCurrency().toString().toLowerCase().contains(SEARCH.getText().toLowerCase()) ||
                                        a.getAccountType().toString().toLowerCase().contains(SEARCH.getText().toLowerCase()) ||
                                        a.getBroadAccountType().toString().toLowerCase().contains(SEARCH.getText().toLowerCase())
                        ) {
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
}
