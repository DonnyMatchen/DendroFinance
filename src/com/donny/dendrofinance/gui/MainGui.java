package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.data.metatable.AssetMTC;
import com.donny.dendrofinance.data.metatable.LoanMTC;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.addedit.DeleteEntryGui;
import com.donny.dendrofinance.gui.addedit.NewTransactionEntryGui;
import com.donny.dendrofinance.gui.addedit.SpecialTransactionEntryGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.menu.data.AccountMetaGui;
import com.donny.dendrofinance.gui.menu.data.StatisticsGui;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.file.ChangePasswordGui;
import com.donny.dendrofinance.gui.menu.file.ExportGui;
import com.donny.dendrofinance.gui.menu.file.ImportGui;
import com.donny.dendrofinance.gui.menu.reports.*;
import com.donny.dendrofinance.gui.menu.reports.budget.BudgetGui;
import com.donny.dendrofinance.gui.menu.trading.LedgerGui;
import com.donny.dendrofinance.gui.menu.trading.OrderBookGui;
import com.donny.dendrofinance.gui.menu.trading.PositionGui;
import com.donny.dendrofinance.gui.menu.trading.PricesGui;
import com.donny.dendrofinance.gui.menu.util.AccountReplacementGui;
import com.donny.dendrofinance.gui.menu.util.ArchiveGui;
import com.donny.dendrofinance.gui.menu.util.ConversionGui;
import com.donny.dendrofinance.gui.menu.util.LogClearGui;
import com.donny.dendrofinance.gui.menu.util.acc.AppDepGui;
import com.donny.dendrofinance.gui.menu.util.acc.NetIncomeGui;
import com.donny.dendrofinance.gui.menu.util.acc.TaxZeroGui;
import com.donny.dendrofinance.gui.menu.util.taxgui.TaxGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.ArrayList;

public class MainGui extends JFrame {
    public final ArrayList<RegisterFrame> FRAME_REGISTRY;
    private final Instance CURRENT_INSTANCE;
    private final JPanel TABLE_PANEL, DISPLAY_PANEL;
    private final JLabel A, B, C;
    private final JScrollPane TABLE_PANE, DISPLAY_PANE;
    private final JTextArea DISPLAY;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;
    private final JButton GEN_INSERT, SPEC_INSERT, EDIT, DELETE;
    private final JTextField SEARCH;
    private boolean force = true;

    public MainGui(Instance curInst) {
        super("Dendrogram Finance");
        CURRENT_INSTANCE = curInst;
        FRAME_REGISTRY = new ArrayList<>();

        //draw Gui
        {
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            getContentPane().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    force = false;
                }
            });
            TABLE_PANEL = new JPanel();
            DISPLAY_PANEL = new JPanel();

            A = new JLabel("Transactions");
            B = new JLabel("Transaction Info");
            C = new JLabel("Search");

            TABLE_PANE = DendroFactory.getTable(new String[]{
                    "UUID", "Date", "Entity", "Items", "Description", "Account", "Debit", "Credit", "Tracking", "Ghost", "Meta"
            }, new Object[][]{}, false);
            TABLE_PANE.getVerticalScrollBar().addAdjustmentListener(this::tablePaneScrolled);
            TABLE = (JTable) TABLE_PANE.getViewport().getView();
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

            DISPLAY_PANE = DendroFactory.getScrollField(false, 5, 40);
            DISPLAY = (JTextArea) DISPLAY_PANE.getViewport().getView();

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
            GEN_INSERT = DendroFactory.getButton("New (General)");
            GEN_INSERT.addActionListener(event -> new NewTransactionEntryGui(this, CURRENT_INSTANCE).setVisible(true));
            SPEC_INSERT = DendroFactory.getButton("New (Specific)");
            SPEC_INSERT.addActionListener(event -> new SpecialTransactionEntryGui(this, CURRENT_INSTANCE).setVisible(true));
            EDIT = DendroFactory.getButton("Edit");
            EDIT.addActionListener(event -> new NewTransactionEntryGui(this, getUUID(TABLE.getSelectedRow()), CURRENT_INSTANCE).setVisible(true));
            DELETE = DendroFactory.getButton("Delete");
            DELETE.addActionListener(event -> new DeleteEntryGui(this, getUUID(TABLE.getSelectedRow()), CURRENT_INSTANCE).setVisible(true));

            //menus
            {
                JMenuBar bar = new JMenuBar();

                //file
                JMenu file = new JMenu("File");

                JMenuItem rel = new JMenuItem("Reload");
                rel.addActionListener(event -> {
                    try {
                        CURRENT_INSTANCE.reloadBackingElements();
                        CURRENT_INSTANCE.reloadEntries();
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed JSON in reload");
                    }
                });
                JMenuItem save = new JMenuItem("Save");
                save.addActionListener(event -> CURRENT_INSTANCE.save());

                JMenuItem imp = new JMenuItem("Import");
                imp.addActionListener(event -> new ImportGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem exp = new JMenuItem("Export");
                exp.addActionListener(event -> new ExportGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem logout = new JMenuItem("Log OUt");
                logout.addActionListener(event -> new ClosePrompt(this, false).setVisible(true));
                JMenuItem change = new JMenuItem("Change Password");
                change.addActionListener(event -> new ChangePasswordGui(this, CURRENT_INSTANCE).setVisible(true));

                file.add(rel);
                file.add(save);
                file.add(new JSeparator());
                file.add(imp);
                file.add(exp);
                file.add(new JSeparator());
                file.add(logout);
                file.add(change);

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
                JMenuItem stats = new JMenuItem("Statistics");
                stats.addActionListener(event -> new StatisticsGui(this, CURRENT_INSTANCE).setVisible(true));

                data.add(curLst);
                data.add(stkLst);
                data.add(invLst);
                data.add(accTypLst);
                data.add(exchLst);
                data.add(accLst);
                data.add(mApiLst);
                data.add(new JSeparator());
                data.add(accountMeta);
                data.add(stats);

                //reports
                JMenu rep = new JMenu("Reports");

                JMenuItem checks = new JMenuItem("Checkbook");
                checks.addActionListener(event -> new CheckGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem bal = new JMenuItem("Balance Sheet");
                bal.addActionListener(event -> new BalanceSheetGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem acc = new JMenuItem("Asset Status");
                acc.addActionListener(event -> new AssetStatusGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem budg = new JMenuItem("Budget");
                budg.addActionListener(event -> new BudgetGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem flow = new JMenuItem("Revenue and Expenses");
                flow.addActionListener(event -> new RevExpGui(this, CURRENT_INSTANCE).setVisible(true));

                JMenuItem asset = new JMenuItem("Non-Fungible Assets");
                asset.addActionListener(event -> new MetaTableGui(this, new AssetMTC(CURRENT_INSTANCE), CURRENT_INSTANCE).setVisible(true));
                JMenuItem loans = new JMenuItem("Loans and Debts");
                loans.addActionListener(event -> new MetaTableGui(this, new LoanMTC(CURRENT_INSTANCE), CURRENT_INSTANCE).setVisible(true));

                rep.add(checks);
                rep.add(new JSeparator());
                rep.add(bal);
                rep.add(acc);
                rep.add(new JSeparator());
                rep.add(budg);
                rep.add(flow);
                rep.add(new JSeparator());
                rep.add(asset);
                rep.add(loans);

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

                JMenu util = new JMenu("Utilities");

                JMenuItem repl = new JMenuItem("Account Replacement");
                repl.addActionListener(event -> new AccountReplacementGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem conv = new JMenuItem("Asset Conversions");
                conv.addActionListener(event -> new ConversionGui(this, CURRENT_INSTANCE).setVisible(true));
                JMenuItem arch = new JMenuItem("Archive Transactions");
                arch.addActionListener(event -> new ArchiveGui(this, CURRENT_INSTANCE).setVisible(true));
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
                util.add(arch);
                util.add(logs);
                util.add(new JSeparator());
                util.add(taxes);
                util.add(appDep);
                util.add(netInc);
                util.add(taxZero);

                //add menus
                bar.add(file);
                bar.add(data);
                bar.add(rep);
                bar.add(trad);
                bar.add(util);

                setJMenuBar(bar);
            }

            //group layout
            {
                //tablePanel
                {
                    GroupLayout tableLayout = new GroupLayout(TABLE_PANEL);
                    TABLE_PANEL.setLayout(tableLayout);
                    tableLayout.setHorizontalGroup(
                            tableLayout.createSequentialGroup().addContainerGap().addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGroup(
                                            tableLayout.createSequentialGroup().addComponent(
                                                    GEN_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    SPEC_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    EDIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    DELETE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
                                            tableLayout.createSequentialGroup().addComponent(
                                                    C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addComponent(
                                            TABLE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    tableLayout.setVerticalGroup(
                            tableLayout.createSequentialGroup().addComponent(
                                    A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            GEN_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SPEC_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            EDIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DELETE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    tableLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TABLE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
                //metaPanel
                {
                    GroupLayout metaLayout = new GroupLayout(DISPLAY_PANEL);
                    DISPLAY_PANEL.setLayout(metaLayout);
                    metaLayout.setHorizontalGroup(
                            metaLayout.createSequentialGroup().addContainerGap().addGroup(
                                    metaLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DISPLAY_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    metaLayout.setVerticalGroup(
                            metaLayout.createSequentialGroup().addComponent(
                                    B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    DISPLAY_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
                //back
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                TABLE_PANEL, GroupLayout.PREFERRED_SIZE, 800, Short.MAX_VALUE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                DISPLAY_PANEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        TABLE_PANEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        DISPLAY_PANEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
            }

            pack();
        }

        updateTable("");
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "MainGui Created");
    }

    public void tablePaneScrolled(AdjustmentEvent event) {
        if (force) {
            event.getAdjustable().setValue(event.getAdjustable().getMaximum());
        }
    }

    public void updateTable() {
        updateTable(SEARCH.getText());
    }

    public final void updateTable(String searchString) {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.huntTransactions(searchString)) {
            for (String[] el : entry.display()) {
                TABLE_ACCESS.addRow(el);
            }
        }
        force = true;
    }

    public void tableCursorChanged(int cursor) {
        TransactionEntry entry = CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(getUUID(cursor));
        DISPLAY.setText(entry.toString());
    }

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
            return Long.parseLong((String) TABLE_ACCESS.getValueAt(cursor, 0));
        }
    }

    public void conclude(boolean save, boolean exit) {
        CURRENT_INSTANCE.conclude(save);
        ArrayList<RegisterFrame> temp = new ArrayList<>(FRAME_REGISTRY);
        for (RegisterFrame frame : temp) {
            frame.dispose();
        }
        super.dispose();
        if (exit) {
            System.exit(0);
        } else {
            new Thread(() -> DendroFinance.main(CURRENT_INSTANCE.ARGS)).start();
        }
    }

    @Override
    public void dispose() {
        new ClosePrompt(this, true).setVisible(true);
    }
}
