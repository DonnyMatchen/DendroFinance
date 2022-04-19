package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.entry.totals.Position;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;

public class PositionGui extends RegisterFrame {
    private final JPanel BACK;
    private final JButton RECALC;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public PositionGui(MainGui caller, Instance curInst) {
        super(caller, curInst);

        //Draw Gui
        {
            BACK = new JPanel();

            PANE = DendroFactory.getTable(new String[]{
                    "Asset", "Volume", "Cost", "Unit", "Value", "Unit", "Position"
            }, new Object[][]{}, false);
            TABLE = (JTable) PANE.getViewport().getView();
            TABLE_ACCESS = ((DefaultTableModel) TABLE.getModel());

            RECALC = DendroFactory.getButton("Recalculate");
            RECALC.addActionListener(event -> reCalcAction());

            //group layout
            {
                GroupLayout main = new GroupLayout(BACK);
                BACK.setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        RECALC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                RECALC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            add(BACK);

            pack();
        }
        reCalcAction();
    }

    public final void reCalcAction() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        BigDecimal tCost = BigDecimal.ZERO, tVal = BigDecimal.ZERO;
        for (Position p : CURRENT_INSTANCE.DATA_HANDLER.getPositions()) {
            BigDecimal[] analog = p.collapse();
            if (analog[0].compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(p.ASSET.getPlaces() - 1), CURRENT_INSTANCE.PRECISION)) > 0) {
                BigDecimal val = p.ASSET.getTotal(analog[0]);
                tCost = tCost.add(analog[1]);
                tVal = tVal.add(val);
                TABLE_ACCESS.addRow(new String[]{
                        p.ASSET.toString(),
                        p.ASSET.encode(analog[0]),
                        CURRENT_INSTANCE.$$(analog[1]),
                        CURRENT_INSTANCE.$$(analog[2]),
                        CURRENT_INSTANCE.$$(val),
                        CURRENT_INSTANCE.$$(val.divide(analog[0], CURRENT_INSTANCE.PRECISION)),
                        CURRENT_INSTANCE.$$(val.add(analog[1]))
                });
            }
        }
        TABLE_ACCESS.addRow(new String[]{
                "Total",
                "",
                CURRENT_INSTANCE.$$(tCost),
                "",
                CURRENT_INSTANCE.$$(tVal),
                "",
                CURRENT_INSTANCE.$$(tVal.add(tCost))
        });
    }
}
