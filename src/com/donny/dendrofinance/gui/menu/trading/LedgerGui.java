package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.meta.LedgerMetadata;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;

public class LedgerGui extends RegisterFrame {
    private final SearchBox CUR;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public LedgerGui(MainGui caller, Instance curInst) {
        super(caller, "Trading Ledgers", curInst);
        //Draw Gui
        {
            PANE = DendroFactory.getTable(new String[]{
                    "Uuid", "Date", "From", "To", "Value", "Price"
            }, new Object[][]{}, false);
            TABLE = (JTable) PANE.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();
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
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            pack();
        }
    }

    private void update() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        if (CUR.getSelectedItem() != null) {
            LCurrency currency = CURRENT_INSTANCE.getLCurrency(CUR.getSelectedItem());
            for (LedgerMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.getLedgerMeta(currency)) {
                if (meta.FROM.equals(currency)) {
                    if (meta.TO_AMNT.compareTo(BigDecimal.ZERO) == 0) {
                        TABLE_ACCESS.addRow(new String[]{
                                "" + meta.UUID, meta.DATE.toDateString(),
                                meta.FROM.encode(meta.FROM_AMNT), meta.TO.encode(meta.TO_AMNT),
                                CURRENT_INSTANCE.$(meta.MAIN_VALUE),
                                ""
                        });
                    } else {
                        TABLE_ACCESS.addRow(new String[]{
                                "" + meta.UUID, meta.DATE.toDateString(),
                                meta.FROM.encode(meta.FROM_AMNT), meta.TO.encode(meta.TO_AMNT),
                                CURRENT_INSTANCE.$(meta.MAIN_VALUE),
                                meta.FROM.encode(BigDecimal.ONE) + " = " + meta.TO.encode(meta.TO_AMNT.divide(meta.FROM_AMNT, CURRENT_INSTANCE.PRECISION).abs())
                        });
                    }
                } else {
                    if (meta.FROM_AMNT.compareTo(BigDecimal.ZERO) == 0) {
                        TABLE_ACCESS.addRow(new String[]{
                                "" + meta.UUID, meta.DATE.toDateString(),
                                meta.FROM.encode(meta.FROM_AMNT), meta.TO.encode(meta.TO_AMNT),
                                CURRENT_INSTANCE.$(meta.MAIN_VALUE),
                                ""
                        });
                    } else {
                        TABLE_ACCESS.addRow(new String[]{
                                "" + meta.UUID, meta.DATE.toDateString(),
                                meta.FROM.encode(meta.FROM_AMNT), meta.TO.encode(meta.TO_AMNT),
                                CURRENT_INSTANCE.$(meta.MAIN_VALUE),
                                meta.TO.encode(BigDecimal.ONE) + " = " + meta.FROM.encode(meta.FROM_AMNT.divide(meta.TO_AMNT, CURRENT_INSTANCE.PRECISION).abs())
                        });
                    }
                }
            }
        }
    }
}
