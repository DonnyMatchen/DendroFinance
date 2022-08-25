package com.donny.dendrofinance.gui.menu.data;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.entry.meta.AssetMetadata;
import com.donny.dendrofinance.entry.meta.LoanMetadata;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StatisticsGui extends RegisterFrame {
    private final DefaultTableModel TABLE_ACCESS;

    public StatisticsGui(MainGui caller, Instance curInst) {
        super(caller, "Statistics", curInst);
        {
            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "Name", "Value"
            }, new Object[][]{}, false);
            JTable table = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = ((DefaultTableModel) table.getModel());
            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            pack();
        }

        fillTable();
    }

    public final void fillTable() {
        TABLE_ACCESS.addRow(new String[]{
                "Transaction Count", "" + CURRENT_INSTANCE.DATA_HANDLER.readTransactions().size()
        });
        TABLE_ACCESS.addRow(new String[]{
                "Budget Count", "" + CURRENT_INSTANCE.DATA_HANDLER.readBudgets().size()
        });
        int x = 0;
        for (LCurrency currency : CURRENT_INSTANCE.CURRENCIES) {
            if (currency.isFiat()) {
                x++;
            }
        }
        TABLE_ACCESS.addRow(new String[]{
                "Fiat Currencies", "" + x
        });
        TABLE_ACCESS.addRow(new String[]{
                "Stocks", "" + CURRENT_INSTANCE.STOCKS.size()
        });
        TABLE_ACCESS.addRow(new String[]{
                "Cryptocurrencies", "" + (CURRENT_INSTANCE.CURRENCIES.size() - x)
        });
        TABLE_ACCESS.addRow(new String[]{
                "Inventories", "" + CURRENT_INSTANCE.INVENTORIES.size()
        });
        TABLE_ACCESS.addRow(new String[]{
                "Exchanges", "" + CURRENT_INSTANCE.EXCHANGES.size()
        });
        TABLE_ACCESS.addRow(new String[]{
                "Account Types", "" + CURRENT_INSTANCE.ACCOUNT_TYPES.size()
        });
        TABLE_ACCESS.addRow(new String[]{
                "Accounts", "" + CURRENT_INSTANCE.ACCOUNTS.size()
        });
        int cur = 0, tot = 0;
        for (AssetMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.assetTotals()) {
            tot++;
            if (meta.isCurrent()) {
                cur++;
            }
        }
        TABLE_ACCESS.addRow(new String[]{
                "Outstanding NF Assets", "" + cur
        });
        TABLE_ACCESS.addRow(new String[]{
                "Retired NF Assets", "" + (tot - cur)
        });
        cur = 0;
        tot = 0;
        for (LoanMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.loanTotals()) {
            tot++;
            if (meta.isCurrent()) {
                cur++;
            }
        }
        TABLE_ACCESS.addRow(new String[]{
                "Outstanding Loans", "" + cur
        });
        TABLE_ACCESS.addRow(new String[]{
                "Retired Loans", "" + (tot - cur)
        });
        tot = 0;
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
            if (entry.hasMeta("ledger")) {
                tot += entry.getLedgerMeta().size();
            }
        }
        TABLE_ACCESS.addRow(new String[]{
                "Trading Ledger Count", "" + tot
        });
    }
}
