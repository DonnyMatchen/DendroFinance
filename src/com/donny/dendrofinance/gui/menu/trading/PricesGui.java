package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.meta.LedgerMetadata;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;

public class PricesGui extends RegisterFrame {
    private final SearchBox CUR;
    private final DefaultTableModel TABLE_ACCESS;

    public PricesGui(MainGui caller, Instance curInst) {
        super(caller, "Trading Prices", curInst);
        //Draw Gui
        {
            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "Date", "Price"
            }, new Object[][]{}, false);
            JTable table = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) table.getModel();
            CUR = new SearchBox("Asset", curInst.getAllUniqueAssetsAsStrings());
            CUR.addListSelectionListener(event -> update());

            //back
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            pack();
        }
    }

    private void update() {
        if (CUR.getSelectedItem() != null) {
            while (TABLE_ACCESS.getRowCount() > 0) {
                TABLE_ACCESS.removeRow(0);
            }
            LCurrency currency = CURRENT_INSTANCE.getLCurrency(CUR.getSelectedItem());
            for (LedgerMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.getLedgerMeta(currency)) {
                if (meta.MAIN_VALUE.compareTo(BigDecimal.ZERO) > 0 && meta.FROM_AMNT.compareTo(BigDecimal.ZERO) < 0 && meta.TO_AMNT.compareTo(BigDecimal.ZERO) > 0) {
                    if (meta.FROM.equals(currency)) {
                        TABLE_ACCESS.addRow(new String[]{
                                meta.DATE.toString(), CURRENT_INSTANCE.$$(meta.fromMain())
                        });
                    } else {
                        TABLE_ACCESS.addRow(new String[]{
                                meta.DATE.toString(), CURRENT_INSTANCE.$$(meta.toMain())
                        });
                    }
                }
            }
        }
    }
}
