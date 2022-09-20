package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.entry.totals.Position;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;

public class PositionGui extends RegisterFrame {
    private final JTextField DATE;
    private final DefaultTableModel TABLE_ACCESS;

    public PositionGui(MainGui caller, Instance curInst) {
        super(caller, "Trading Position", curInst);

        //Draw Gui
        {
            JLabel a = new JLabel("Date");
            DATE = new JTextField();
            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "Asset", "Volume", "Cost", "Unit", "Value", "Unit", "Position"
            }, new Object[][]{}, false);
            JTable table = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = ((DefaultTableModel) table.getModel());

            JButton recalculate = DendroFactory.getButton("Recalculate");
            recalculate.addActionListener(event -> reCalcAction());

            //group layout
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
                                        )
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        recalculate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                recalculate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }
        reCalcAction();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public final void reCalcAction() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        boolean useDate;
        LDate point = LDate.now(CURRENT_INSTANCE);
        if (DATE.getText().equals("")) {
            useDate = false;
        } else {
            LDate date = new LDate(DATE.getText(), CURRENT_INSTANCE);
            if (date.toDateString().equals(LDate.now(CURRENT_INSTANCE).toDateString())) {
                useDate = false;
            } else {
                useDate = true;
                point = date;
            }
        }
        BigDecimal tCost = BigDecimal.ZERO, tVal = BigDecimal.ZERO;
        ArrayList<Position> positions = useDate ? CURRENT_INSTANCE.DATA_HANDLER.getPositions(point) : CURRENT_INSTANCE.DATA_HANDLER.getPositions();
        for (LStock stock : CURRENT_INSTANCE.STOCKS) {
            for (Position p : positions) {
                if (p.ASSET.equals(stock)) {
                    BigDecimal[] analog = p.collapse();
                    if (analog[0].compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(p.ASSET.getPlaces() - 1), CURRENT_INSTANCE.precision)) > 0) {
                        BigDecimal val = useDate ? p.ASSET.getTotal(analog[0], point) : p.ASSET.getTotal(analog[0]);
                        tCost = tCost.add(analog[1]);
                        tVal = tVal.add(val);
                        TABLE_ACCESS.addRow(new String[]{
                                p.ASSET.toString(),
                                p.ASSET.encode(analog[0]),
                                CURRENT_INSTANCE.$$(analog[1]),
                                CURRENT_INSTANCE.$$(analog[2]),
                                CURRENT_INSTANCE.$$(val),
                                CURRENT_INSTANCE.$$(val.divide(analog[0], CURRENT_INSTANCE.precision)),
                                CURRENT_INSTANCE.$$(val.add(analog[1]))
                        });
                    }
                }
            }
        }
        for (LCurrency currency : CURRENT_INSTANCE.CURRENCIES) {
            for (Position p : positions) {
                if (p.ASSET.equals(currency)) {
                    BigDecimal[] analog = p.collapse();
                    if (analog[0].compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(p.ASSET.getPlaces() - 1), CURRENT_INSTANCE.precision)) > 0) {
                        BigDecimal val = useDate ? p.ASSET.getTotal(analog[0], point) : p.ASSET.getTotal(analog[0]);
                        tCost = tCost.add(analog[1]);
                        tVal = tVal.add(val);
                        TABLE_ACCESS.addRow(new String[]{
                                p.ASSET.toString(),
                                p.ASSET.encode(analog[0]),
                                CURRENT_INSTANCE.$$(analog[1]),
                                CURRENT_INSTANCE.$$(analog[2]),
                                CURRENT_INSTANCE.$$(val),
                                CURRENT_INSTANCE.$$(val.divide(analog[0], CURRENT_INSTANCE.precision)),
                                CURRENT_INSTANCE.$$(val.add(analog[1]))
                        });
                    }
                }
            }
        }
        for (LInventory inventory : CURRENT_INSTANCE.INVENTORIES) {
            for (Position p : positions) {
                if (p.ASSET.equals(inventory)) {
                    BigDecimal[] analog = p.collapse();
                    boolean over;
                    if (p.ASSET.getPlaces() >= 1) {
                        over = analog[0].compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(p.ASSET.getPlaces() - 1), CURRENT_INSTANCE.precision)) >= 0;
                    } else {
                        over = analog[0].compareTo(BigDecimal.ONE.divide(BigDecimal.TEN, CURRENT_INSTANCE.precision)) >= 0;
                    }
                    if (over) {
                        BigDecimal val = useDate ? p.ASSET.getTotal(analog[0], point) : p.ASSET.getTotal(analog[0]);
                        tCost = tCost.add(analog[1]);
                        tVal = tVal.add(val);
                        TABLE_ACCESS.addRow(new String[]{
                                p.ASSET.toString(),
                                p.ASSET.encode(analog[0]),
                                CURRENT_INSTANCE.$$(analog[1]),
                                CURRENT_INSTANCE.$$(analog[2]),
                                CURRENT_INSTANCE.$$(val),
                                CURRENT_INSTANCE.$$(val.divide(analog[0], CURRENT_INSTANCE.precision)),
                                CURRENT_INSTANCE.$$(val.add(analog[1]))
                        });
                    }
                }
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
