package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.HashMap;

public class AssetStatusGui extends RegisterFrame {
    private final JLabel A;
    private final JTextField DATE;
    private final JButton ENTER;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public AssetStatusGui(MainGui caller, Instance curInst) {
        super(caller, "Market Asset Status", curInst);
        //draw gui
        {
            A = new JLabel("Date");

            DATE = new JTextField();

            ENTER = DendroFactory.getButton("Enter");
            ENTER.addActionListener(event -> enterAction());

            PANE = DendroFactory.getTable(new String[]{
                    "Account", "Amount", "Value"
            }, new Object[][]{}, false);
            TABLE = (JTable) PANE.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();

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
                                                DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            pack();
        }
        DATE.setText(LDate.now(curInst).toDateString());
        enterAction();
    }

    public final void enterAction() {
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
        HashMap<LCurrency, BigDecimal> prices = CURRENT_INSTANCE.DATA_HANDLER.pricesAsOf(y, m, d);
        BigDecimal stock = BigDecimal.ZERO, crypt = BigDecimal.ZERO, inv = BigDecimal.ZERO, fiat = BigDecimal.ZERO,
                main = BigDecimal.ZERO, total = BigDecimal.ZERO, nf = BigDecimal.ZERO, rec = BigDecimal.ZERO, debts = BigDecimal.ZERO;
        for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
            if (acc.containsKey(a)) {
                BigDecimal compare = BigDecimal.ONE;
                if (a.getCurrency().getPlaces() > 0) {
                    compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
                }
                if (acc.get(a).compareTo(compare) >= 0 || acc.get(a).compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0) {
                    if ((a.getBroadAccountType() == BroadAccountType.ASSET || a.getBroadAccountType() == BroadAccountType.TRACKING)
                            && (!a.getName().equalsIgnoreCase(Account.cryptoName) && !a.getName().equalsIgnoreCase(Account.stockName)
                            && !a.getName().equalsIgnoreCase(Account.inventoryName) && !a.getName().equalsIgnoreCase(Account.fiatName))) {
                        if (prices.get(a.getCurrency()) == null) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Missing price: " + a.getCurrency());
                        }
                        TABLE_ACCESS.addRow(new String[]{
                                a.getName(), a.getCurrency().encode(acc.get(a)), CURRENT_INSTANCE.$(acc.get(a).multiply(prices.get(a.getCurrency())))
                        });
                        if (a.getCurrency() instanceof LStock) {
                            stock = stock.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                        } else if (a.getCurrency() instanceof LInventory) {
                            inv = inv.add(acc.get(a).multiply(prices.get(a.getCurrency())));
                        } else {
                            if (a.getCurrency().isFiat()) {
                                if (a.getCurrency().equals(CURRENT_INSTANCE.main)) {
                                    if(a.getAccountType().NAME.equalsIgnoreCase(Account.fixedAssetsTypeName)){
                                        nf = nf.add(acc.get(a));
                                    }else if(a.getAccountType().NAME.equalsIgnoreCase(Account.receiveTypeName)){
                                        rec = rec.add(acc.get(a));
                                    }else{
                                        main = main.add(acc.get(a));
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
                    "Main Currency", "", CURRENT_INSTANCE.$(main)
            });
        }
        if (fiat.compareTo(BigDecimal.ZERO) > 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Other Currency", "", CURRENT_INSTANCE.$(fiat)
            });
        }
        if (stock.compareTo(BigDecimal.ZERO) > 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Stock Portfolio", "", CURRENT_INSTANCE.$(stock)
            });
        }
        if (crypt.compareTo(BigDecimal.ZERO) > 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Cryptocurrency", "", CURRENT_INSTANCE.$(crypt)
            });
        }
        if (inv.compareTo(BigDecimal.ZERO) > 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Inventory", "", CURRENT_INSTANCE.$(inv)
            });
        }
        if (nf.compareTo(BigDecimal.ZERO) > 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Non Fungibles", "", CURRENT_INSTANCE.$(nf)
            });
        }
        if (rec.compareTo(BigDecimal.ZERO) > 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Receivables", "", CURRENT_INSTANCE.$(rec)
            });
        }
        if (debts.compareTo(BigDecimal.ZERO) < 0) {
            TABLE_ACCESS.addRow(new String[]{
                    "Debts", "", CURRENT_INSTANCE.$(debts)
            });
        }
        TABLE_ACCESS.addRow(new String[]{
                "Total", "", CURRENT_INSTANCE.$(total)
        });
    }
}
