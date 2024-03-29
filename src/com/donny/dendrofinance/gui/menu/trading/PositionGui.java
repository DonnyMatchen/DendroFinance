package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.capsules.totals.Position;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class PositionGui extends ProgramRegisterFrame {
    private final JTextField DATE;
    private final DefaultTableModel TABLE_ACCESS;

    public PositionGui(ProgramMainGui caller, ProgramInstance curInst) {
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
            recalculate.addActionListener(event -> update());

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
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public final void update() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        LDate point = LDate.now(CURRENT_INSTANCE);
        if (DATE.getText().isEmpty()) {
            try {
                LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
                if (!date.toDateString().equals(point.toDateString())) {
                    point = date;
                }
            } catch (ValidationFailedException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Not a valid date: " + DATE.getText());
            }
        }
        BigDecimal tCost = BigDecimal.ZERO, tVal = BigDecimal.ZERO;
        ArrayList<Position> positions = CURRENT_INSTANCE.DATA_HANDLER.getPositions(point);
        HashMap<LCurrency, BigDecimal> prices = CURRENT_INSTANCE.DATA_HANDLER.pricesAsOf(CURRENT_INSTANCE.main, point);
        for (LStock stock : CURRENT_INSTANCE.STOCKS) {
            for (Position p : positions) {
                if (p.ASSET.equals(stock)) {
                    BigDecimal[] analog = p.collapse();
                    if (analog[0].compareTo(BigDecimal.ONE.divide(BigDecimal.TEN.pow(p.ASSET.getPlaces() - 1), CURRENT_INSTANCE.precision)) > 0) {
                        BigDecimal val = prices.get(stock).multiply(analog[0]);
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
                        BigDecimal val = prices.get(currency).multiply(analog[0]);
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
                        BigDecimal val = prices.get(inventory).multiply(analog[0]);
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
