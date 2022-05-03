package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.util.Aggregation;
import com.donny.dendrofinance.util.Curation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Objects;

public class BudgetGui extends RegisterFrame {
    private final JTabbedPane BACK;
    private final JPanel VIEW, EDIT;
    private final JLabel A, B;
    private final JRadioButton EXPANDED, COLLAPSED;
    private final ButtonGroup RADIO_GROUP;
    private final JComboBox<String> EDIT_B, BUDGET, YEAR, PERIOD;
    private final JButton ADD, REMOVE, SAVE, RESET;
    private final JScrollPane VIEW_PANE, EDIT_PANE;
    private final JTable VIEW_TABLE, EDIT_TABLE;
    private final DefaultTableModel VIEW_TABLE_ACCESS, EDIT_TABLE_ACCESS;

    public BudgetGui(MainGui caller, Instance curInst) {
        super(caller, "Budgets", curInst);

        //draw GUI
        {
            BACK = new JTabbedPane();
            //view tab
            {
                VIEW = new JPanel();
                VIEW.setBorder(null);
                VIEW_PANE = DendroFactory.getTable(new String[]{
                        "Line Item", "Value", "Budgeted", "Balance"
                }, new Object[][]{}, false);
                VIEW_TABLE = (JTable) VIEW_PANE.getViewport().getView();
                VIEW_TABLE_ACCESS = (DefaultTableModel) VIEW_TABLE.getModel();
                BUDGET = new JComboBox<>();
                BUDGET.addItemListener(event -> updateView());
                YEAR = new JComboBox<>();
                A = new JLabel("Budget");
                B = new JLabel("Period");
                EXPANDED = new JRadioButton("By Account");
                COLLAPSED = new JRadioButton("By Type");
                RADIO_GROUP = new ButtonGroup();
                RADIO_GROUP.add(EXPANDED);
                RADIO_GROUP.add(COLLAPSED);
                COLLAPSED.setSelected(true);
                EXPANDED.addActionListener(event -> updateView());
                COLLAPSED.addActionListener(event -> updateView());
                Curation<Integer> years = new Curation<>();
                for (TransactionEntry entry : curInst.DATA_HANDLER.readTransactions()) {
                    if (!entry.getEntity().equals("PRIOR")) {
                        years.add(entry.getDate().getYear());
                    }
                }
                years.sort(Comparator.naturalOrder());
                for (int y : years) {
                    YEAR.addItem("" + y);
                }
                YEAR.addItemListener(event -> updateView());
                PERIOD = new JComboBox<>();
                CURRENT_INSTANCE.installPeriod(PERIOD);
                PERIOD.addItemListener(event -> updateView());

                //group layout
                {
                    GroupLayout main = new GroupLayout(VIEW);
                    VIEW.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    BUDGET, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            VIEW_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BUDGET, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                    VIEW_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("View", VIEW);
            //edit tab
            {
                EDIT = new JPanel();
                EDIT.setBorder(null);
                EDIT_PANE = DendroFactory.getTable(new String[]{}, new Object[][]{}, true);
                EDIT_TABLE = (JTable) EDIT_PANE.getViewport().getView();
                EDIT_TABLE.setModel(new DefaultTableModel(new Object[][]{}, new String[]{
                        "Line Item", "Budgeted"
                }) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return column >= 1;
                    }
                });
                EDIT_TABLE_ACCESS = (DefaultTableModel) EDIT_TABLE.getModel();
                EDIT_B = new JComboBox<>();
                EDIT_B.addItemListener(event -> updateEdit());

                ADD = DendroFactory.getButton("Add");
                ADD.addActionListener(event -> new NewBudgetGui(this, CURRENT_INSTANCE).setVisible(true));
                REMOVE = DendroFactory.getButton("Remove");
                REMOVE.addActionListener(event -> new RemoveBudgetGui(this, (String) EDIT_B.getSelectedItem(), CURRENT_INSTANCE).setVisible(true));
                RESET = DendroFactory.getButton("Reset");
                RESET.addActionListener(event -> updateEdit());
                SAVE = DendroFactory.getButton("Save");
                SAVE.addActionListener(event -> saveBudget());

                //group layout
                {
                    GroupLayout main = new GroupLayout(EDIT);
                    EDIT.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    REMOVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    EDIT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    ADD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addComponent(
                                            EDIT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    RESET, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            REMOVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            EDIT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ADD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    EDIT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            RESET, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Edit", EDIT);

            add(BACK);

            pack();
        }
        updateBudget();
        updateView();
        updateEdit();
    }

    public final void updateBudget() {
        BUDGET.removeAllItems();
        BUDGET.addItem("<none>");
        for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
            BUDGET.addItem(entry.getName());
        }

        EDIT_B.removeAllItems();
        for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
            EDIT_B.addItem(entry.getName());
        }
    }

    private void updateView() {
        BigDecimal total = BigDecimal.ZERO, bTotal = BigDecimal.ZERO;
        BigDecimal beta = BigDecimal.ONE;
        String period = Objects.requireNonNullElse((String) PERIOD.getSelectedItem(), "Year");
        if (period.length() == 2) {
            if (period.contains("S")) {
                beta = new BigDecimal("0.5");
            } else {
                beta = new BigDecimal("0.25");
            }
        } else if (!period.equals("Year")) {
            beta = new BigDecimal("0.08" + ("0".repeat(CURRENT_INSTANCE.precision.getPrecision() - 2)));
        }
        if (COLLAPSED.isSelected()) {
            Aggregation<String> budgets = new Aggregation<>();
            for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                if (!entry.getDescription().contains("Net Income")) {
                    if (entry.getDate().getYear() == Integer.parseInt((String) YEAR.getSelectedItem())) {
                        boolean flag;
                        if (period.equals("Year")) {
                            flag = true;
                        } else if (period.length() == 2) {
                            if (period.contains("S")) {
                                flag = period.equals(entry.getDate().getSemi());
                            } else {
                                flag = period.equals(entry.getDate().getQuarter());
                            }
                        } else {
                            flag = period.equals(entry.getDate().getMonthString());
                        }
                        if (flag) {
                            for (AccountWrapper wrapper : entry.getAccounts()) {
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
                }
            }
            while (VIEW_TABLE_ACCESS.getRowCount() > 0) {
                VIEW_TABLE_ACCESS.removeRow(0);
            }
            String budgetName = (String) BUDGET.getSelectedItem();
            if (budgetName != null) {
                boolean flag = true;
                if (!budgetName.equals("<none>")) {
                    for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                        if (entry.getName().equals(budgetName)) {
                            flag = false;
                            for (String budgetType : CURRENT_INSTANCE.DATA_HANDLER.getBudgetTypes()) {
                                BigDecimal budgeted = BigDecimal.ZERO;
                                for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                                    if (account.getBudgetType().equals(budgetType)) {
                                        budgeted = budgeted.add(entry.getBudget().getDecimal(account.getName()).decimal);
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
            for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                if (!entry.getDescription().contains("Net Income")) {
                    if (entry.getDate().getYear() == Integer.parseInt((String) YEAR.getSelectedItem())) {
                        boolean flag;
                        if (period.equals("Year")) {
                            flag = true;
                        } else if (period.length() == 2) {
                            if (period.contains("S")) {
                                flag = period.equals(entry.getDate().getSemi());
                            } else {
                                flag = period.equals(entry.getDate().getQuarter());
                            }
                        } else {
                            flag = period.equals(entry.getDate().getMonthString());
                        }
                        if (flag) {
                            for (AccountWrapper wrapper : entry.getAccounts()) {
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
                }
            }
            while (VIEW_TABLE_ACCESS.getRowCount() > 0) {
                VIEW_TABLE_ACCESS.removeRow(0);
            }
            String budgetName = (String) BUDGET.getSelectedItem();
            if (budgetName != null) {
                boolean flag = true;
                if (!budgetName.equals("<none>")) {
                    for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                        if (entry.getName().equals(budgetName)) {
                            flag = false;
                            for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                                if (!account.getBudgetType().equals("")) {
                                    if (accRevExp.get(account).abs().add(entry.getBudget().getDecimal(account.getName()).decimal.abs()).compareTo(BigDecimal.ZERO) > 0) {
                                        VIEW_TABLE_ACCESS.addRow(new String[]{
                                                account.getName(),
                                                CURRENT_INSTANCE.$(accRevExp.get(account)),
                                                CURRENT_INSTANCE.$(entry.getBudget().getDecimal(account.getName()).decimal.multiply(beta)),
                                                CURRENT_INSTANCE.$(accRevExp.get(account).subtract(entry.getBudget().getDecimal(account.getName()).decimal.multiply(beta)))
                                        });
                                        total = total.add(accRevExp.get(account));
                                        bTotal = bTotal.add(entry.getBudget().getDecimal(account.getName()).decimal.multiply(beta));
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
            for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                if (entry.getName().equals(pick)) {
                    for (Account account : CURRENT_INSTANCE.ACCOUNTS) {
                        if (!account.getBudgetType().equals("")) {
                            EDIT_TABLE_ACCESS.addRow(new String[]{
                                    account.getName(),
                                    CURRENT_INSTANCE.$(entry.getBudget().getDecimal(account.getName()).decimal)
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
            BudgetEntry entry = null;
            for (BudgetEntry e : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                if (e.getName().equals(pick)) {
                    entry = e;
                    break;
                }
            }
            for (int i = 0; i < EDIT_TABLE_ACCESS.getRowCount(); i++) {
                String a = (String) EDIT_TABLE_ACCESS.getValueAt(i, 0);
                String b = (String) EDIT_TABLE_ACCESS.getValueAt(i, 1);
                if (entry != null) {
                    entry.getBudget().FIELDS.put(a, new JsonDecimal(CURRENT_INSTANCE.cleanNumber(b)));
                }
            }
            updateBudget();
        }
    }
}
