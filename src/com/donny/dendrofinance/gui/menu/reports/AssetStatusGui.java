package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.util.HashMap;

public class AssetStatusGui extends RegisterFrame {
    private final JTextField DATE;
    private final SearchBox CURRENCY;
    private final DefaultTableModel TABLE_ACCESS;

    public AssetStatusGui(MainGui caller, Instance curInst) {
        super(caller, "Market Asset Status", curInst);
        //draw gui
        {
            JLabel a = new JLabel("Date");

            DATE = new JTextField();
            DATE.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        enterAction(getCurrency());
                    }
                }
            });

            CURRENCY = new SearchBox("Currency", curInst.getAllAssetsAsStrings());

            JButton enter = DendroFactory.getButton("Enter");
            enter.addActionListener(event -> enterAction(getCurrency()));

            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "Account", "Amount", "Value"
            }, new Object[][]{}, false);
            JTable table = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) table.getModel();

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addComponent(
                                        CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                CURRENCY, 150, 150, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }
        }
        DATE.setText(LDate.now(curInst).toDateString());
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public final void enterAction(LCurrency currency) {
        try {
            LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
            while (TABLE_ACCESS.getRowCount() > 0) {
                TABLE_ACCESS.removeRow(0);
            }
            int y = date.getYear(), m = date.getMonth(), d = date.getDay();
            HashMap<Account, BigDecimal> acc = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(y, m, d);
            HashMap<LCurrency, BigDecimal> prices = CURRENT_INSTANCE.DATA_HANDLER.pricesAsOf(currency, date);
            BigDecimal stock = BigDecimal.ZERO, crypt = BigDecimal.ZERO, inv = BigDecimal.ZERO, fiat = BigDecimal.ZERO,
                    main = BigDecimal.ZERO, total = BigDecimal.ZERO, nf = BigDecimal.ZERO, rec = BigDecimal.ZERO, debts = BigDecimal.ZERO;
            for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
                if (acc.containsKey(a)) {
                    if (a.getCurrency().significant(acc.get(a))) {
                        if ((a.getBroadAccountType() == BroadAccountType.ASSET || a.getBroadAccountType() == BroadAccountType.TRACKING)
                                && (!a.getName().equalsIgnoreCase(Account.cryptoName) && !a.getName().equalsIgnoreCase(Account.stockName)
                                && !a.getName().equalsIgnoreCase(Account.inventoryName) && !a.getName().equalsIgnoreCase(Account.fiatName))) {
                            if (prices.get(a.getCurrency()) == null) {
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Missing price: " + a.getCurrency());
                            }
                            TABLE_ACCESS.addRow(new String[]{
                                    a.getName(), a.getCurrency().encode(acc.get(a)), currency.encode(acc.get(a).multiply(prices.get(a.getCurrency())))
                            });
                            if (a.getCurrency() instanceof LStock) {
                                stock = stock.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                            } else if (a.getCurrency() instanceof LInventory) {
                                inv = inv.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                            } else {
                                if (a.getCurrency().isFiat()) {
                                    if (a.getCurrency().equals(CURRENT_INSTANCE.main)) {
                                        if (a.getAccountType().NAME.equalsIgnoreCase(Account.fixedAssetsTypeName)) {
                                            nf = nf.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                                        } else if (a.getAccountType().NAME.equalsIgnoreCase(Account.receiveTypeName)) {
                                            rec = rec.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                                        } else {
                                            main = main.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                                        }
                                    } else {
                                        fiat = fiat.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                                    }
                                } else {
                                    crypt = crypt.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                                }
                            }
                            total = total.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                        } else if (a.getBroadAccountType() == BroadAccountType.LIABILITY) {
                            debts = debts.subtract(acc.get(a).multiply(prices.get(a.getCurrency())));
                            total = total.subtract(acc.get(a).multiply(prices.get(a.getCurrency())));
                        }
                    }
                }
            }
            TABLE_ACCESS.addRow(new String[]{"", "", ""});
            if (main.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Main Currency", "", currency.encode(main)
                });
            }
            if (fiat.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Other Currency", "", currency.encode(fiat)
                });
            }
            if (stock.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Stock Portfolio", "", currency.encode(stock)
                });
            }
            if (crypt.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Cryptocurrency", "", currency.encode(crypt)
                });
            }
            if (inv.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Inventory", "", currency.encode(inv)
                });
            }
            if (nf.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Non Fungibles", "", currency.encode(nf)
                });
            }
            if (rec.compareTo(BigDecimal.ZERO) > 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Receivables", "", currency.encode(rec)
                });
            }
            if (debts.compareTo(BigDecimal.ZERO) < 0) {
                TABLE_ACCESS.addRow(new String[]{
                        "Debts", "", currency.encode(debts)
                });
            }
            TABLE_ACCESS.addRow(new String[]{
                    "Total", "", currency.encode(total)
            });
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Validation error: " + e.getMessage());
        }

    }

    private LCurrency getCurrency() {
        String selected = CURRENCY.getSelectedItem();
        if (selected == null) {
            return CURRENT_INSTANCE.main;
        } else {
            return CURRENT_INSTANCE.getLCurrency(CURRENCY.getSelectedItem());
        }
    }
}
