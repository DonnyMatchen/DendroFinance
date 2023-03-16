package com.donny.dendrofinance.gui.menu.data.state;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.capsules.meta.AssetMetadata;
import com.donny.dendrofinance.capsules.meta.LoanMetadata;
import com.donny.dendrofinance.capsules.totals.Position;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DateRange;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class StateGui extends RegisterFrame {
    private final DateRange RANGE;
    private final JComboBox<LDate> DATE;
    private final DefaultTableModel ACCOUNTS, POSITIONS, ASSETS, LOANS;

    public StateGui(MainGui caller, Instance curInst) {
        super(caller, "States", curInst);

        //Draw Gui
        {
            RANGE = new DateRange(false);
            RANGE.initRange(365, CURRENT_INSTANCE);
            DateRange full = new DateRange(false);
            full.setEditable(false);
            full.init(
                    new LDate(CURRENT_INSTANCE.DATA_HANDLER.STATES.getMinDate(), CURRENT_INSTANCE),
                    new LDate(CURRENT_INSTANCE.DATA_HANDLER.STATES.getMaxDate(), CURRENT_INSTANCE)
            );
            DATE = new JComboBox<>();
            DATE.addItemListener(event -> update());
            JScrollPane accounts = DendroFactory.getTable(
                    new String[]{"Account", "Value"},
                    new String[][]{}, false
            ), positions = DendroFactory.getTable(
                    new String[]{"Asset", "Total", "Cost", "Unit"},
                    new String[][]{}, false
            ), assets = DendroFactory.getTable(
                    new String[]{"Name", "Value", "Count"},
                    new String[][]{}, false
            ), loans = DendroFactory.getTable(
                    new String[]{"Name", "Principle", "Rae"},
                    new String[][]{}, false
            );

            ACCOUNTS = (DefaultTableModel) ((JTable) accounts.getViewport().getView()).getModel();
            POSITIONS = (DefaultTableModel) ((JTable) positions.getViewport().getView()).getModel();
            ASSETS = (DefaultTableModel) ((JTable) assets.getViewport().getView()).getModel();
            LOANS = (DefaultTableModel) ((JTable) loans.getViewport().getView()).getModel();

            JLabel a = new JLabel("Full Range");
            JLabel b = new JLabel("Date Range");
            JLabel c = new JLabel("Accounts");
            JLabel d = new JLabel("Positions");
            JLabel e = new JLabel("Assets");
            JLabel f = new JLabel("Loans");

            JButton update = DendroFactory.getButton("Update");
            update.addActionListener(event -> updateDate());

            JButton remove = DendroFactory.getButton("Remove");
            remove.addActionListener(event -> {
                CURRENT_INSTANCE.DATA_HANDLER.STATES.delete(((LDate) DATE.getSelectedItem()).getTime());
                updateDate();
            });
            JButton removeAll = DendroFactory.getButton("Remove All");
            removeAll.addActionListener(event -> {
                for (int i = 0; i < DATE.getItemCount(); i++) {
                    LDate date = DATE.getItemAt(i);
                    CURRENT_INSTANCE.DATA_HANDLER.STATES.delete(date.getTime());
                }
                updateDate();
            });
            JButton add = DendroFactory.getButton("Add New");
            add.addActionListener(event -> new NewStateGui(this, CURRENT_INSTANCE).setVisible(true));
            JButton auto = DendroFactory.getButton("Autogenerate");
            auto.addActionListener(event -> {
                CURRENT_INSTANCE.DATA_HANDLER.createStates();
                updateDate();
            });

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
                                                        full, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addGroup(
                                                        main.createSequentialGroup().addComponent(
                                                                RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                                update, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                        )
                                                )
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                add, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                auto, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                removeAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        accounts, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                )
                                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        positions, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                )
                                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        assets, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                )
                                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        loans, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                )
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        full, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        update, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        add, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        auto, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        removeAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                accounts, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                positions, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                assets, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                loans, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                )
                        ).addContainerGap()
                );
            }
            updateDate();

            pack();
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation(dim.width / 2 - getWidth() / 2, dim.height / 2 - getHeight() / 2);
        }
    }

    public void update() {
        while (ACCOUNTS.getRowCount() > 0) {
            ACCOUNTS.removeRow(0);
        }
        while (POSITIONS.getRowCount() > 0) {
            POSITIONS.removeRow(0);
        }
        while (ASSETS.getRowCount() > 0) {
            ASSETS.removeRow(0);
        }
        while (LOANS.getRowCount() > 0) {
            LOANS.removeRow(0);
        }
        LDate date = (LDate) DATE.getSelectedItem();
        if (date != null) {
            StateCapsule state = CURRENT_INSTANCE.DATA_HANDLER.STATES.get(date.getTime());
            Aggregation<Account> acc = state.getAccounts();
            for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
                if (acc.containsKey(a)) {
                    ACCOUNTS.addRow(new String[]{
                            a.getName(),
                            a.getCurrency().encode(acc.get(a))
                    });
                }
            }

            ArrayList<Position> pos = state.getPositions();
            for (LCurrency c : CURRENT_INSTANCE.getAllAssets()) {
                for (Position p : pos) {
                    if (p.ASSET == c) {
                        BigDecimal[] collapsed = p.collapse();
                        POSITIONS.addRow(new String[]{
                                c.toString(),
                                c.encode(collapsed[0]),
                                CURRENT_INSTANCE.$(collapsed[1]),
                                CURRENT_INSTANCE.$$(collapsed[2])
                        });
                    }
                }
            }

            for (AssetMetadata meta : state.getAssets()) {
                HashMap<LCurrency, BigDecimal> values = meta.getValues();
                HashMap<LCurrency, BigDecimal> counts = meta.getCount();
                boolean first = true;
                for (LCurrency c : CURRENT_INSTANCE.getAllAssets()) {
                    if (values.containsKey(c)) {
                        if (first) {
                            ASSETS.addRow(new String[]{
                                    meta.NAME,
                                    c.encode(values.get(c)),
                                    "" + counts.get(c)
                            });
                        } else {
                            ASSETS.addRow(new String[]{
                                    "",
                                    c.encode(values.get(c)),
                                    "" + counts.get(c)
                            });
                        }
                        first = false;
                    }
                }
            }

            for (LoanMetadata meta : state.getLoans()) {
                LOANS.addRow(new String[]{
                        meta.NAME,
                        meta.CUR.encode(meta.principalRemaining()),
                        CURRENT_INSTANCE.p(meta.RATE)
                });
            }
        }
    }

    void updateDate() {
        DATE.removeAllItems();
        LDate[] range = RANGE.getRange(CURRENT_INSTANCE);
        if (range != null) {
            for (StateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.STATES.getRange(range[0], range[1])) {
                DATE.addItem(capsule.getDate());
            }
        }
    }
}
