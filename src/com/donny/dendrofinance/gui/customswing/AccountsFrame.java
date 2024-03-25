package com.donny.dendrofinance.gui.customswing;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Cleaning;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Objects;

public class AccountsFrame extends JPanel {
    private final ArrayList<SmallSearchBox<Account>> ACCOUNTS = new ArrayList<>();
    private final ArrayList<JComboBox<String>> COLUMNS = new ArrayList<>();
    private final ArrayList<JTextField> VALUES = new ArrayList<>();
    private final ArrayList<JButton> CLOSE = new ArrayList<>();
    private final ProgramInstance CURRENT_INSTANCE;
    private final JButton ADD;

    public AccountsFrame(ProgramInstance instance) {
        super();
        CURRENT_INSTANCE = instance;
        ADD = DendroFactory.getButton("+");
        ADD.addActionListener((evt) -> addRow());
        setBorder(null);
        redraw();
    }

    private void redraw() {
        removeAll();
        GroupLayout main = new GroupLayout(this);
        setLayout(main);

        GroupLayout.ParallelGroup horizontal = main.createParallelGroup(GroupLayout.Alignment.CENTER);
        GroupLayout.SequentialGroup vertical = main.createSequentialGroup();

        GroupLayout.ParallelGroup horizontalAccounts = main.createParallelGroup(GroupLayout.Alignment.TRAILING);
        GroupLayout.ParallelGroup horizontalColumns = main.createParallelGroup(GroupLayout.Alignment.CENTER);
        GroupLayout.ParallelGroup horizontalValues = main.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup horizontalClose = main.createParallelGroup(GroupLayout.Alignment.CENTER);

        if (!ACCOUNTS.isEmpty()) {
            for (int i = 0; i < ACCOUNTS.size(); i++) {
                horizontalAccounts.addComponent(ACCOUNTS.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
                horizontalColumns.addComponent(COLUMNS.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);
                horizontalValues.addComponent(VALUES.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
                horizontalClose.addComponent(CLOSE.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE);

                vertical.addGroup(
                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                ACCOUNTS.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addComponent(
                                COLUMNS.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addComponent(
                                VALUES.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addComponent(
                                CLOSE.get(i), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        )
                ).addGap(DendroFactory.SMALL_GAP);
            }
            horizontal.addGroup(
                    main.createSequentialGroup().addGroup(
                            horizontalAccounts
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            horizontalColumns
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            horizontalValues
                    ).addGap(DendroFactory.LARGE_GAP).addGroup(
                            horizontalClose
                    ).addContainerGap()
            );
        }

        main.setHorizontalGroup(
                horizontal.addComponent(
                        ADD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                )
        );

        main.setVerticalGroup(
                vertical.addComponent(
                        ADD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                )
        );
        repaint();
    }

    private void addRowInternal() {
        SmallSearchBox<Account> acc = new SmallSearchBox<>("Account", CURRENT_INSTANCE.getDCAccounts(), CURRENT_INSTANCE);
        JComboBox<String> col = new JComboBox<>();
        col.addItem("Debit");
        col.addItem("Credit");
        col.addItem("Ghost");
        col.addItem("Trading");
        JTextField val = new JTextField();
        JButton close = DendroFactory.getButton("X");
        close.setBorder(null);
        ACCOUNTS.add(acc);
        COLUMNS.add(col);
        VALUES.add(val);
        CLOSE.add(close);

        col.addItemListener(event -> {
            switch (col.getSelectedIndex()) {
                case 0, 1 -> acc.setMaster(CURRENT_INSTANCE.getDCAccounts());
                case 2 -> acc.setMaster(CURRENT_INSTANCE.getGhostAccounts());
                case 3 -> acc.setMaster(CURRENT_INSTANCE.getTrackingAccounts());
                default -> acc.setMaster(CURRENT_INSTANCE.ACCOUNTS.getMaster());
            }
        });

        close.addActionListener((evt) -> {
            ACCOUNTS.remove(acc);
            COLUMNS.remove(col);
            VALUES.remove(val);
            CLOSE.remove(close);

            redraw();
        });
    }

    public void addRow() {
        addRowInternal();
        redraw();
    }

    public LAccountSet export() {
        LAccountSet set = new LAccountSet(CURRENT_INSTANCE);
        for (int i = 0; i < ACCOUNTS.size(); i++) {
            set.add(new AccountWrapper(
                    ACCOUNTS.get(i).getSelectedItem(),
                    AWColumn.fromString(((String) Objects.requireNonNull(COLUMNS.get(i).getSelectedItem())).charAt(0) + ""),
                    Cleaning.cleanNumber(VALUES.get(i).getText())
            ));
        }
        return set;
    }

    public void load(LAccountSet set) {
        for (AccountWrapper wrapper : set) {
            addRowInternal();
            switch (wrapper.COLUMN) {
                case DEBIT -> COLUMNS.get(COLUMNS.size() - 1).setSelectedIndex(0);
                case CREDIT -> COLUMNS.get(COLUMNS.size() - 1).setSelectedIndex(1);
                case GHOST -> COLUMNS.get(COLUMNS.size() - 1).setSelectedIndex(2);
                case TRACKER -> COLUMNS.get(COLUMNS.size() - 1).setSelectedIndex(3);
            }
            ACCOUNTS.get(ACCOUNTS.size() - 1).setSelectedItem(wrapper.ACCOUNT);
            VALUES.get(VALUES.size() - 1).setText(wrapper.VALUE.toString());
        }
        redraw();
    }
}
