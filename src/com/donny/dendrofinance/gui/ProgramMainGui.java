package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.data.metatable.AssetMTC;
import com.donny.dendrofinance.data.metatable.LoanMTC;
import com.donny.dendrofinance.gui.menu.data.AccountMetaGui;
import com.donny.dendrofinance.gui.menu.data.BudgetTypeGui;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.state.StateGui;
import com.donny.dendrofinance.gui.menu.data.template.NewTemplateGui;
import com.donny.dendrofinance.gui.menu.data.template.TemplateGui;
import com.donny.dendrofinance.gui.menu.file.ExportGui;
import com.donny.dendrofinance.gui.menu.file.ImportGui;
import com.donny.dendrofinance.gui.menu.reports.AssetStatusGui;
import com.donny.dendrofinance.gui.menu.reports.BalanceSheetGui;
import com.donny.dendrofinance.gui.menu.reports.CheckGui;
import com.donny.dendrofinance.gui.menu.reports.MetaTableGui;
import com.donny.dendrofinance.gui.menu.reports.budget.BudgetGui;
import com.donny.dendrofinance.gui.menu.trading.LedgerGui;
import com.donny.dendrofinance.gui.menu.trading.OrderBookGui;
import com.donny.dendrofinance.gui.menu.trading.PositionGui;
import com.donny.dendrofinance.gui.menu.trading.PricesGui;
import com.donny.dendrofinance.gui.menu.transactions.DeleteAllGui;
import com.donny.dendrofinance.gui.menu.transactions.DeleteTransactionGui;
import com.donny.dendrofinance.gui.menu.transactions.NewTransactionEntryGui;
import com.donny.dendrofinance.gui.menu.transactions.SpecialTransactionEntryGui;
import com.donny.dendrofinance.gui.menu.util.AccountReplacementGui;
import com.donny.dendrofinance.gui.menu.util.ConversionGui;
import com.donny.dendrofinance.gui.menu.util.LogClearGui;
import com.donny.dendrofinance.gui.menu.util.acc.AppDepGui;
import com.donny.dendrofinance.gui.menu.util.acc.NetIncomeGui;
import com.donny.dendrofinance.gui.menu.util.acc.TaxZeroGui;
import com.donny.dendrofinance.gui.menu.util.taxgui.TaxGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.collections.Curation;
import com.donny.dendroroot.gui.ClosePrompt;
import com.donny.dendroroot.gui.MainGui;
import com.donny.dendroroot.gui.customswing.AlertGui;
import com.donny.dendroroot.gui.customswing.DateRange;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ProgramMainGui extends MainGui {
    private final ProgramInstance CURRENT_INSTANCE;
    private final JTextArea DISPLAY;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;
    private final JTextField SEARCH, COUNT;
    private final DateRange RANGE;
    private boolean force = true;

    public ProgramMainGui(ProgramInstance curInst) {
        super("Dendrogram Finance", curInst);
        CURRENT_INSTANCE = curInst;

        //draw Gui
        {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            getContentPane().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    force = false;
                }
            });
            JPanel tablePanel = new JPanel();
            JPanel displayPanel = new JPanel();

            JLabel a = new JLabel("Transactions");
            JLabel b = new JLabel("Search");
            JLabel c = new JLabel("Full Range");
            JLabel d = new JLabel("Date Range");
            JLabel e = new JLabel("Count");
            JLabel f = new JLabel("Transaction Info");

            JScrollPane tablePane = DendroFactory.getTable(new String[]{
                    "UUID", "Date", "Entity", "Items", "Description", "Account", "Debit", "Credit", "Ghost", "Tracking", "Meta"
            }, new Object[][]{}, false);
            tablePane.getVerticalScrollBar().addAdjustmentListener(this::tablePaneScrolled);
            TABLE = (JTable) tablePane.getViewport().getView();
            TABLE.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    tableCursorChanged(TABLE.getSelectedRow());
                }
            });
            TABLE.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    int keyCode = event.getKeyCode();
                    if (keyCode == 38 || keyCode == 40) {
                        int orig = TABLE.getSelectedRow();
                        if ((keyCode != 38 || orig != 0) && (keyCode != 40 || orig != TABLE.getRowCount() - 1)) {
                            if (keyCode == 38) {
                                tableCursorChanged(orig - 1);
                            } else {
                                tableCursorChanged(orig + 1);
                            }
                        }

                    }
                }
            });
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();

            JScrollPane displayPane = DendroFactory.getScrollField(false, 5, 40);
            DISPLAY = (JTextArea) displayPane.getViewport().getView();

            SEARCH = new JTextField();
            SEARCH.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateTable();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateTable();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateTable();
                }
            });

            RANGE = new DateRange(false);
            RANGE.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable();
                    }
                }
            });
            DateRange full = new DateRange(false);
            full.setEditable(false);
            full.init(
                    new LDate(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getMinDate(), CURRENT_INSTANCE),
                    new LDate(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getMaxDate(), CURRENT_INSTANCE)
            );

            COUNT = new JTextField();
            COUNT.setEditable(false);
            COUNT.setBackground(DendroFactory.DISABLED);

            //menus
            {
                JMenuBar bar = new JMenuBar();

                //file
                JMenu file = new JMenu("File");

                JMenuItem save = new JMenuItem("Save");
                save.addActionListener(event -> CURRENT_INSTANCE.save());

                JMenuItem imp = new JMenuItem("Import");
                imp.addActionListener(event -> new ImportGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem exp = new JMenuItem("Export");
                exp.addActionListener(event -> new ExportGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem logout = new JMenuItem("Log OUt");
                logout.addActionListener(event -> new ClosePrompt(this, false).setVisible(true));

                file.add(save);
                file.add(new JSeparator());
                file.add(imp);
                file.add(exp);
                file.add(new JSeparator());
                file.add(logout);

                //data
                JMenu data = new JMenu("Data");

                JMenuItem curLst = new JMenuItem("Currencies");
                curLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.CURRENCIES, CURRENT_INSTANCE).setVisible(true));
                JMenuItem stkLst = new JMenuItem("Stocks");
                stkLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.STOCKS, CURRENT_INSTANCE).setVisible(true));
                JMenuItem invLst = new JMenuItem("Inventories");
                invLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.INVENTORIES, CURRENT_INSTANCE).setVisible(true));
                JMenuItem mApiLst = new JMenuItem("Market APIs");
                mApiLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.MARKET_APIS, CURRENT_INSTANCE).setVisible(true));
                JMenuItem accTypLst = new JMenuItem("Account Types");
                accTypLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.ACCOUNT_TYPES, CURRENT_INSTANCE).setVisible(true));
                JMenuItem accLst = new JMenuItem("Accounts");
                accLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.ACCOUNTS, CURRENT_INSTANCE).setVisible(true));
                JMenuItem exchLst = new JMenuItem("Exchanges");
                exchLst.addActionListener(event -> new BackingTableGui<>(this, CURRENT_INSTANCE.EXCHANGES, CURRENT_INSTANCE).setVisible(true));

                JMenuItem accountMeta = new JMenuItem("Account Metadata");
                accountMeta.addActionListener(event -> new AccountMetaGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem budgetTyp = new JMenuItem("Budget Types");
                budgetTyp.addActionListener(event -> new BudgetTypeGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem template = new JMenuItem("Templates");
                template.addActionListener(event -> new TemplateGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem state = new JMenuItem("States");
                state.addActionListener(event -> new StateGui(this, CURRENT_INSTANCE).setVisible(true));

                data.add(curLst);
                data.add(stkLst);
                data.add(invLst);
                data.add(accTypLst);
                data.add(exchLst);
                data.add(accLst);
                data.add(mApiLst);
                data.add(new JSeparator());
                data.add(accountMeta);
                data.add(budgetTyp);
                data.add(template);
                data.add(state);

                //transactions
                JMenu trans = new JMenu("Transactions");

                JMenu newTrans = new JMenu("New");
                JMenuItem newRegTrans = new JMenuItem("Regular Transaction");
                newRegTrans.addActionListener(event -> new NewTransactionEntryGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem newSpecTrans = new JMenuItem("Trading Transaction");
                newSpecTrans.addActionListener(event -> new SpecialTransactionEntryGui(this, CURRENT_INSTANCE).setVisible(true));
                newTrans.add(newRegTrans);
                newTrans.add(newSpecTrans);

                JMenu thisTrans = new JMenu("Current");
                JMenuItem edit = new JMenuItem("Edit");
                edit.addActionListener(event -> {
                    if (validSelection())
                        new NewTransactionEntryGui(this, getUUID(TABLE.getSelectedRow()), false, CURRENT_INSTANCE).setVisible(true);
                });
                JMenuItem clone = new JMenuItem("Clone");
                clone.addActionListener(event -> {
                    if (validSelection())
                        new NewTransactionEntryGui(this, getUUID(TABLE.getSelectedRow()), true, CURRENT_INSTANCE).setVisible(true);
                });
                JMenuItem delete = new JMenuItem("Delete");
                delete.addActionListener(event -> {
                    if (validSelection())
                        new DeleteTransactionGui(this, getUUID(TABLE.getSelectedRow()), CURRENT_INSTANCE).setVisible(true);
                });
                JMenuItem newTemplate = new JMenuItem("Save as Template");
                newTemplate.addActionListener(event -> {
                    if (validSelection())
                        new NewTemplateGui(this, getUUID(TABLE.getSelectedRow()), CURRENT_INSTANCE).setVisible(true);
                });
                thisTrans.add(edit);
                thisTrans.add(clone);
                thisTrans.add(delete);
                thisTrans.add(newTemplate);

                JMenu allTrans = new JMenu("All");
                JMenuItem deleteAll = new JMenuItem("Delete All in Search");
                deleteAll.addActionListener(event -> new DeleteAllGui(this, getAllUUIDs(), CURRENT_INSTANCE).setVisible(true));
                allTrans.add(deleteAll);

                trans.add(newTrans);
                trans.add(thisTrans);
                trans.add(allTrans);

                //reports
                JMenu rep = new JMenu("Reports");

                JMenuItem checks = new JMenuItem("Checkbook");
                checks.addActionListener(event -> new CheckGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem bal = new JMenuItem("Balance Sheet");
                bal.addActionListener(event -> new BalanceSheetGui(this, false, CURRENT_INSTANCE).setVisible(true));
                JMenuItem dif = new JMenuItem("Change in Accounts");
                dif.addActionListener(event -> new BalanceSheetGui(this, true, CURRENT_INSTANCE).setVisible(true));
                JMenuItem acc = new JMenuItem("Asset Status");
                acc.addActionListener(event -> new AssetStatusGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem budg = new JMenuItem("Budget");
                budg.addActionListener(event -> new BudgetGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem asset = new JMenuItem("Non-Fungible Assets");
                asset.addActionListener(event -> new MetaTableGui(this, new AssetMTC(CURRENT_INSTANCE), CURRENT_INSTANCE).setVisible(true));
                JMenuItem loans = new JMenuItem("Loans and Debts");
                loans.addActionListener(event -> new MetaTableGui(this, new LoanMTC(CURRENT_INSTANCE), CURRENT_INSTANCE).setVisible(true));

                rep.add(checks);
                rep.add(new JSeparator());
                rep.add(bal);
                rep.add(dif);
                rep.add(acc);
                rep.add(new JSeparator());
                rep.add(budg);
                rep.add(new JSeparator());
                rep.add(asset);
                rep.add(loans);

                //trading
                JMenu trad = new JMenu("Trading");

                JMenuItem ledg = new JMenuItem("Trading Ledger");
                ledg.addActionListener(event -> new LedgerGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem pos = new JMenuItem("Trading Position");
                pos.addActionListener(event -> new PositionGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem orbk = new JMenuItem("Order Book");
                orbk.addActionListener(event -> new OrderBookGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem prc = new JMenuItem("Trading Prices");
                prc.addActionListener(event -> new PricesGui(this, CURRENT_INSTANCE).setVisible(true));

                trad.add(ledg);
                trad.add(pos);
                trad.add(orbk);
                trad.add(prc);

                //utilities
                JMenu util = new JMenu("Utilities");

                JMenuItem repl = new JMenuItem("Account Replacement");
                repl.addActionListener(event -> new AccountReplacementGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem conv = new JMenuItem("Asset Conversions");
                conv.addActionListener(event -> new ConversionGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem logs = new JMenuItem("Logs");
                logs.addActionListener(event -> new LogClearGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem taxes = new JMenuItem("Tax Calculator");
                taxes.addActionListener(event -> new TaxGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem appDep = new JMenuItem("Appreciation");
                appDep.addActionListener(event -> new AppDepGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem netInc = new JMenuItem("Net Income");
                netInc.addActionListener(event -> new NetIncomeGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem taxZero = new JMenuItem("Tax Zeroing");
                taxZero.addActionListener(event -> new TaxZeroGui(this, CURRENT_INSTANCE).setVisible(true));

                util.add(repl);
                util.add(conv);
                util.add(logs);
                util.add(new JSeparator());
                util.add(taxes);
                util.add(appDep);
                util.add(netInc);
                util.add(taxZero);

                //add menus
                bar.add(file);
                bar.add(data);
                bar.add(trans);
                bar.add(rep);
                bar.add(trad);
                bar.add(util);

                setJMenuBar(bar);
            }

            //group layout
            {
                //tablePanel
                {
                    GroupLayout tableLayout = new GroupLayout(tablePanel);
                    tablePanel.setLayout(tableLayout);
                    tableLayout.setHorizontalGroup(
                            tableLayout.createSequentialGroup().addContainerGap().addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGroup(
                                            tableLayout.createSequentialGroup().addGroup(
                                                    tableLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    tableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            full, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            COUNT, GroupLayout.PREFERRED_SIZE, 50, 50
                                                    )
                                            )
                                    ).addComponent(
                                            tablePane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    tableLayout.setVerticalGroup(
                            tableLayout.createSequentialGroup().addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            full, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            COUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    tablePane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
                //metaPanel
                {
                    GroupLayout metaLayout = new GroupLayout(displayPanel);
                    displayPanel.setLayout(metaLayout);
                    metaLayout.setHorizontalGroup(
                            metaLayout.createSequentialGroup().addContainerGap().addGroup(
                                    metaLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            displayPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    metaLayout.setVerticalGroup(
                            metaLayout.createSequentialGroup().addComponent(
                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    displayPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
                //back
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                tablePanel, GroupLayout.PREFERRED_SIZE, 800, Short.MAX_VALUE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                displayPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        tablePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        displayPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
            }
        }
        RANGE.initRange(CURRENT_INSTANCE.range, CURRENT_INSTANCE);
        updateTable("");
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "MainGui Created");
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public boolean validSelection() {
        long u = getUUID(TABLE.getSelectedRow());
        if (u != 0) {
            return true;
        } else {
            new AlertGui(this, "No Transaction Selected!", CURRENT_INSTANCE).setVisible(true);
            return false;
        }
    }

    public void tablePaneScrolled(AdjustmentEvent event) {
        if (force) {
            event.getAdjustable().setValue(event.getAdjustable().getMaximum());
        }
    }

    public void updateTable() {
        LDate[] range = RANGE.getRange(CURRENT_INSTANCE);
        if (range != null) {
            updateTable(range[0], range[1], SEARCH.getText());
        }
    }

    public void updateTable(String search) {
        LDate[] range = RANGE.getRange(CURRENT_INSTANCE);
        if (range != null) {
            updateTable(range[0], range[1], search);
        }
    }

    public final void updateTable(LDate start, LDate end, String searchString) {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        ArrayList<TransactionCapsule> capsules = CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.hunt(start, end, searchString);
        for (TransactionCapsule capsule : capsules) {
            for (String[] el : capsule.display()) {
                TABLE_ACCESS.addRow(el);
            }
        }
        COUNT.setText(String.valueOf(capsules.size()));
        force = true;
    }

    public void tableCursorChanged(int cursor) {
        TransactionCapsule capsule = CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.get(getUUID(cursor));
        DISPLAY.setText(capsule.toString());
    }

    /**
     * @param cursor cursor to check
     * @return next UUID traversing the table upwards from the initial cursor or 0 if there is no selection or the table is broken
     */
    public long getUUID(int cursor) {
        if (cursor < 0) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No row was selected");
            return 0;
        } else if (TABLE_ACCESS.getValueAt(cursor, 0).equals("")) {
            if (cursor != 0) {
                return getUUID(cursor - 1);
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something has gone catastrophically wrong with the table");
                return 0;
            }
        } else {
            return Long.parseUnsignedLong((String) TABLE_ACCESS.getValueAt(cursor, 0));
        }
    }

    public ArrayList<Long> getAllUUIDs() {
        Curation<Long> out = new Curation<>();
        for (int i = 0; i < TABLE_ACCESS.getRowCount(); i++) {
            out.add(getUUID(i));
        }
        return out;
    }

    public void conclude(boolean save, boolean exit) {
        super.conclude(save, exit);
        if (!exit) {
            new Thread(() -> DendroFinance.main(CURRENT_INSTANCE.ARGS)).start();
        }
    }
}
