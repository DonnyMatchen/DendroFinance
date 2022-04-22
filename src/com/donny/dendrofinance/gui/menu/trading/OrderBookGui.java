package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.entry.totals.OrderBookEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.util.Curation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Objects;

public class OrderBookGui extends RegisterFrame {
    private final JLabel A, B;
    private final JComboBox<String> YEAR, PERIOD;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public OrderBookGui(MainGui caller, Instance curInst) {
        super(caller, curInst);
        //Draw Gui
        {
            PANE = DendroFactory.getTable(new String[]{
                    "Bought Ref", "Bought Date", "Sold Ref", "Sold Date", "Asset", "Volume", "Bought", "Sold", "Profit"
            }, new Object[][]{}, false);
            A = new JLabel("Year");
            B = new JLabel("Period");
            TABLE = (JTable) PANE.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();
            YEAR = new JComboBox<>();
            PERIOD = new JComboBox<>();
            PERIOD.addItem("Year");
            PERIOD.addItem("S1");
            PERIOD.addItem("S2");
            PERIOD.addItem("Q1");
            PERIOD.addItem("Q2");
            PERIOD.addItem("Q3");
            PERIOD.addItem("Q4");
            PERIOD.addItem("January");
            PERIOD.addItem("February");
            PERIOD.addItem("March");
            PERIOD.addItem("April");
            PERIOD.addItem("May");
            PERIOD.addItem("June");
            PERIOD.addItem("July");
            PERIOD.addItem("August");
            PERIOD.addItem("September");
            PERIOD.addItem("October");
            PERIOD.addItem("November");
            PERIOD.addItem("December");
            YEAR.addItemListener(event -> update());
            PERIOD.addItemListener(event -> update());
            Curation<Integer> years = new Curation<>();
            for (OrderBookEntry entry : CURRENT_INSTANCE.DATA_HANDLER.getOrderBook()) {
                years.add(entry.END.getYear());
            }
            for (int y : years) {
                YEAR.addItem("" + y);
            }
            //back
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                        YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }

            pack();
        }
    }

    private void update() {
        if (PERIOD.getSelectedItem() != null) {
            while (TABLE_ACCESS.getRowCount() > 0) {
                TABLE_ACCESS.removeRow(0);
            }
            int y = Integer.parseInt((String) YEAR.getSelectedItem());
            String per = Objects.requireNonNullElse((String) PERIOD.getSelectedItem(), "Year");
            for (OrderBookEntry entry : CURRENT_INSTANCE.DATA_HANDLER.getOrderBook()) {
                boolean flag = entry.END.getYear() == y;
                if (flag && !per.equals("Year")) {
                    if (per.length() == 2) {
                        if (per.contains("S")) {
                            flag = per.equals(entry.END.getSemi());
                        } else {
                            flag = per.equals(entry.END.getQuarter());
                        }
                    } else {
                        flag = per.equals(entry.END.getMonthString());
                    }
                }
                if (flag) {
                    TABLE_ACCESS.addRow(new String[]{
                            "" + entry.START_REF,
                            entry.START.toDateString(),
                            "" + entry.END_REF,
                            entry.END.toDateString(),
                            entry.ASSET.toString(),
                            entry.ASSET.encode(entry.VOLUME),
                            CURRENT_INSTANCE.$(entry.COST),
                            CURRENT_INSTANCE.$(entry.SOLD),
                            CURRENT_INSTANCE.$(entry.profit()),
                    });
                }
            }
        }
    }
}
