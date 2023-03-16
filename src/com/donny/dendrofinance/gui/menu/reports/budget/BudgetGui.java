package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.form.Cleaning;
import com.donny.dendrofinance.instance.Frequency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Objects;

public class BudgetGui extends RegisterFrame {
    private final JRadioButton EXPANDED, COLLAPSED;
    private final JComboBox<String> EDIT_B, BUDGET, YEAR, PERIOD;
    private final DefaultTableModel VIEW_TABLE_ACCESS, EDIT_TABLE_ACCESS;

    public BudgetGui(MainGui caller, Instance curInst) {
        super(caller, "Budgets", curInst);

        //draw GUI
        {
            JTabbedPane BACK = new JTabbedPane();
            //view tab
            {
                JPanel view = new JPanel();
                view.setBorder(null);
                JScrollPane viewPane = DendroFactory.getTable(new String[]{
                        "Line Item", "Value", "Budgeted", "Balance"
                }, new Object[][]{}, false);
                JTable viewTable = (JTable) viewPane.getViewport().getView();
                VIEW_TABLE_ACCESS = (DefaultTableModel) viewTable.getModel();
                BUDGET = new JComboBox<>();
                BUDGET.addItemListener(event -> updateView());
                YEAR = new JComboBox<>();
                JLabel a = new JLabel("Budget");
                JLabel b = new JLabel("Period");
                EXPANDED = new JRadioButton("By Account");
                COLLAPSED = new JRadioButton("By Type");
                ButtonGroup radioGroup = new ButtonGroup();
                radioGroup.add(EXPANDED);
                radioGroup.add(COLLAPSED);
                COLLAPSED.setSelected(true);
                EXPANDED.addActionListener(event -> updateView());
                COLLAPSED.addActionListener(event -> updateView());
                LDate first = new LDate(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getMinDate(), CURRENT_INSTANCE);
                LDate last = new LDate(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getMaxDate(), CURRENT_INSTANCE);
                for (int i = first.getYear(); i < last.getYear() + 1; i++) {
                    YEAR.addItem("" + i);
                }
                YEAR.addItemListener(event -> updateView());
                PERIOD = new JComboBox<>();
                CURRENT_INSTANCE.installPeriod(PERIOD);
                PERIOD.addItemListener(event -> updateView());

                //group layout
                {
                    GroupLayout main = new GroupLayout(view);
                    view.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    BUDGET, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    COLLAPSED, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    EXPANDED, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addComponent(
                                            viewPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BUDGET, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            COLLAPSED, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            EXPANDED, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    viewPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }

                BACK.addTab("View", view);
            }
            //edit tab
            {
                JPanel edit = new JPanel();
                edit.setBorder(null);
                JScrollPane editPane = DendroFactory.getTable(new String[]{}, new Object[][]{}, true);
                JTable editTable = (JTable) editPane.getViewport().getView();
                editTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                        "Line Item", "Budgeted"
                }) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column >= 1;
                    }
                });
                EDIT_TABLE_ACCESS = (DefaultTableModel) editTable.getModel();
                EDIT_B = new JComboBox<>();
                EDIT_B.addItemListener(event -> updateEdit());

                JButton add = DendroFactory.getButton("Add");
                add.addActionListener(event -> new NewBudgetGui(this, CURRENT_INSTANCE).setVisible(true));
                JButton remove = DendroFactory.getButton("Remove");
                remove.addActionListener(event -> new RemoveBudgetGui(this, (String) EDIT_B.getSelectedItem(), CURRENT_INSTANCE).setVisible(true));
                JButton reset = DendroFactory.getButton("Reset");
                reset.addActionListener(event -> updateEdit());
                JButton save = DendroFactory.getButton("Save");
                save.addActionListener(event -> saveBudget());

                //group layout
                {
                    GroupLayout main = new GroupLayout(edit);
                    edit.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    EDIT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    add, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addComponent(
                                            editPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    reset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            EDIT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            add, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    editPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            reset, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                BACK.addTab("Edit", edit);
            }

            add(BACK);
        }
        updateBudget();
        updateView();
        updateEdit();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public final void updateBudget() {
        BUDGET.removeAllItems();
        BUDGET.addItem("<none>");
        for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
            BUDGET.addItem(capsule.getName());
        }

        EDIT_B.removeAllItems();
        for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
            EDIT_B.addItem(capsule.getName());
        }
    }

    private void updateView() {
        BigDecimal total = BigDecimal.ZERO, bTotal = BigDecimal.ZERO;
        BigDecimal beta = BigDecimal.ONE;
        Frequency freq = Frequency.ANNUAL;
        int param = 0;
        String period = Objects.requireNonNullElse((String) PERIOD.getSelectedItem(), "Year");
        if (period.length() == 2) {
            beta = new BigDecimal("0.25");
            freq = Frequency.QUARTERLY;
            param = Integer.parseInt(period.substring(1));
        } else if (!period.equals("Year")) {
            beta = new BigDecimal("0.08" + ("3".repeat(CURRENT_INSTANCE.precision.getPrecision() - 2)));
            freq = Frequency.MONTHLY;
            param = LDate.getMonth(period);
        }
        if (COLLAPSED.isSelected()) {
            Aggregation<String> budgets = new Aggregation<>();
            for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getRange(
                    LDate.getRange(Integer.parseInt((String) YEAR.getSelectedItem()),
                            param, freq, CURRENT_INSTANCE))) {
                if (!capsule.getDescription().contains("Net Income")) {
                    for (AccountWrapper wrapper : capsule.getAccounts()) {
                        if (!wrapper.ACCOUNT.getBudgetType().equals("")) {
                            BigDecimal val = wrapper.VALUE;
                            if (wrapper.COLUMN == AWColumn.DEBIT) {
                                val = val.multiply(BigDecimal.valueOf(-1));
                            }
                            budgets.add(wrapper.ACCOUNT.getBudgetType(), val);
                        }
                    }
                }
            }
            while (VIEW_TABLE_ACCESS.getRowCount() > 0) {
                VIEW_TABLE_ACCESS.removeRow(0);
            }
            String budgetName = (String) BUDGET.getSelectedItem();
            if (budgetName != null) {
                boolean flag = true;
                if (!budgetName.equals("<none>")) {
                    for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                        if (capsule.getName().equals(budgetName)) {
                            flag = false;
                            for (String budgetType : CURRENT_INSTANCE.DATA_HANDLER.getBudgetTypes()) {
                                BigDecimal budgeted = BigDecimal.ZERO;
                                for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                                    if (account.getBudgetType().equals(budgetType)) {
                                        budgeted = budgeted.add(capsule.getBudget().getDecimal(account.getName()).decimal);
                                    }
                                }
                                if (budgets.get(budgetType).abs().add(budgeted.abs()).compareTo(BigDecimal.ZERO) > 0) {
                                    VIEW_TABLE_ACCESS.addRow(new String[]{
                                            budgetType,
                                            CURRENT_INSTANCE.$(budgets.get(budgetType)),
                                            CURRENT_INSTANCE.$(budgeted.multiply(beta)),
                                            CURRENT_INSTANCE.$(budgets.get(budgetType).subtract(budgeted.multiply(beta)))
                                    });
                                    total = total.add(budgets.get(budgetType));
                                    bTotal = bTotal.add(budgeted.multiply(beta));
                                }
                            }
                            VIEW_TABLE_ACCESS.addRow(new String[]{
                                    "", "", "", ""
                            });
                            VIEW_TABLE_ACCESS.addRow(new String[]{
                                    "Balance", CURRENT_INSTANCE.$(total), CURRENT_INSTANCE.$(bTotal), ""
                            });
                            break;
                        }
                    }
                }
                if (flag) {
                    for (String budgetType : CURRENT_INSTANCE.DATA_HANDLER.getBudgetTypes()) {
                        if (budgets.get(budgetType).compareTo(BigDecimal.ZERO) != 0) {
                            VIEW_TABLE_ACCESS.addRow(new String[]{
                                    budgetType,
                                    CURRENT_INSTANCE.$(budgets.get(budgetType)),
                                    "",
                                    ""
                            });
                        }
                        total = total.add(budgets.get(budgetType));
                    }
                    VIEW_TABLE_ACCESS.addRow(new String[]{
                            "", "", "", ""
                    });
                    VIEW_TABLE_ACCESS.addRow(new String[]{
                            "Balance", CURRENT_INSTANCE.$(total), "", ""
                    });
                }
            }
        } else if (EXPANDED.isSelected()) {
            Aggregation<Account> accRevExp = new Aggregation<>();
            for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getRange(
                    LDate.getRange(Integer.parseInt((String) YEAR.getSelectedItem()),
                            param, freq, CURRENT_INSTANCE))) {
                if (!capsule.getDescription().contains("Net Income")) {
                    for (AccountWrapper wrapper : capsule.getAccounts()) {
                        if (wrapper.ACCOUNT.getBroadAccountType() == BroadAccountType.REVENUE || wrapper.ACCOUNT.getBroadAccountType() == BroadAccountType.EXPENSE) {
                            BigDecimal val = wrapper.VALUE;
                            if (wrapper.COLUMN == AWColumn.DEBIT) {
                                val = val.multiply(BigDecimal.valueOf(-1));
                            }
                            accRevExp.add(wrapper.ACCOUNT, val);
                        }
                    }
                }
            }
            while (VIEW_TABLE_ACCESS.getRowCount() > 0) {
                VIEW_TABLE_ACCESS.removeRow(0);
            }
            String budgetName = (String) BUDGET.getSelectedItem();
            if (budgetName != null) {
                boolean flag = true;
                if (!budgetName.equals("<none>")) {
                    for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                        if (capsule.getName().equals(budgetName)) {
                            flag = false;
                            for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                                if (!account.getBudgetType().equals("")) {
                                    if (accRevExp.get(account).abs().add(capsule.getBudget().getDecimal(account.getName()).decimal.abs()).compareTo(BigDecimal.ZERO) > 0) {
                                        VIEW_TABLE_ACCESS.addRow(new String[]{
                                                account.getName(),
                                                CURRENT_INSTANCE.$(accRevExp.get(account)),
                                                CURRENT_INSTANCE.$(capsule.getBudget().getDecimal(account.getName()).decimal.multiply(beta)),
                                                CURRENT_INSTANCE.$(accRevExp.get(account).subtract(capsule.getBudget().getDecimal(account.getName()).decimal.multiply(beta)))
                                        });
                                        total = total.add(accRevExp.get(account));
                                        bTotal = bTotal.add(capsule.getBudget().getDecimal(account.getName()).decimal.multiply(beta));
                                    }
                                }
                            }
                            VIEW_TABLE_ACCESS.addRow(new String[]{
                                    "", "", "", ""
                            });
                            VIEW_TABLE_ACCESS.addRow(new String[]{
                                    "Balance", CURRENT_INSTANCE.$(total), CURRENT_INSTANCE.$(bTotal), ""
                            });
                            break;
                        }
                    }
                }
                if (flag) {
                    for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                        if (!account.getBudgetType().equals("")) {
                            if (accRevExp.get(account).compareTo(BigDecimal.ZERO) != 0) {
                                VIEW_TABLE_ACCESS.addRow(new String[]{
                                        account.getName(),
                                        CURRENT_INSTANCE.$(accRevExp.get(account)),
                                        "",
                                        ""
                                });
                                total = total.add(accRevExp.get(account));
                            }
                        }
                    }
                    VIEW_TABLE_ACCESS.addRow(new String[]{
                            "", "", "", ""
                    });
                    VIEW_TABLE_ACCESS.addRow(new String[]{
                            "Balance", CURRENT_INSTANCE.$(total), "", ""
                    });
                }
            }
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "I don't know hwo you did this, but neither radio button is selected.");
        }
    }

    private void updateEdit() {
        while (EDIT_TABLE_ACCESS.getRowCount() > 0) {
            EDIT_TABLE_ACCESS.removeRow(0);
        }
        String pick = (String) EDIT_B.getSelectedItem();
        if (pick != null) {
            for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                if (capsule.getName().equals(pick)) {
                    for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                        if (!account.getBudgetType().equals("")) {
                            EDIT_TABLE_ACCESS.addRow(new String[]{
                                    account.getName(),
                                    CURRENT_INSTANCE.$(capsule.getBudget().getDecimal(account.getName()).decimal)
                            });
                        }
                    }
                    break;
                }
            }
        }
    }

    private void saveBudget() {
        String pick = (String) EDIT_B.getSelectedItem();
        if (pick != null) {
            BudgetCapsule capsule = null;
            for (BudgetCapsule e : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                if (e.getName().equals(pick)) {
                    capsule = e;
                    break;
                }
            }
            for (int i = 0; i < EDIT_TABLE_ACCESS.getRowCount(); i++) {
                String a = (String) EDIT_TABLE_ACCESS.getValueAt(i, 0);
                String b = (String) EDIT_TABLE_ACCESS.getValueAt(i, 1);
                if (capsule != null) {
                    capsule.getBudget().put(a, new JsonDecimal(Cleaning.cleanNumber(b)));
                }
            }
            updateBudget();
        }
    }
}
