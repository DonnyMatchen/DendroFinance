package com.donny.dendrofinance.gui.menu.trading;

import com.donny.dendrofinance.capsules.totals.OrderBookEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Frequency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Objects;

public class OrderBookGui extends RegisterFrame {
    private final JComboBox<String> YEAR, PERIOD;
    private final DefaultTableModel TABLE_ACCESS;

    public OrderBookGui(MainGui caller, Instance curInst) {
        super(caller, "Order Book", curInst);
        //Draw Gui
        {
            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "Bought Ref", "Bought Date", "Sold Ref", "Sold Date", "Asset", "Volume", "Bought", "Sold", "Profit"
            }, new Object[][]{}, false);
            JLabel a = new JLabel("Year");
            JLabel b = new JLabel("Period");
            JTable table = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) table.getModel();
            YEAR = new JComboBox<>();
            PERIOD = new JComboBox<>();
            CURRENT_INSTANCE.installPeriod(PERIOD);
            YEAR.addItemListener(event -> update());
            PERIOD.addItemListener(event -> update());
            LDate first = new LDate(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getMinDate(), CURRENT_INSTANCE);
            LDate last = new LDate(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getMaxDate(), CURRENT_INSTANCE);
            for (int i = first.getYear(); i < last.getYear() + 1; i++) {
                YEAR.addItem(String.valueOf(i));
            }

            //back
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
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
                                        YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PERIOD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void update() {
        if (PERIOD.getSelectedItem() != null) {
            while (TABLE_ACCESS.getRowCount() > 0) {
                TABLE_ACCESS.removeRow(0);
            }
            Frequency freq = Frequency.ANNUAL;
            int param = 0;
            String period = Objects.requireNonNullElse((String) PERIOD.getSelectedItem(), "Year");
            if (period.length() == 2) {
                freq = Frequency.QUARTERLY;
                param = Integer.parseInt(period.substring(1));
            } else if (!period.equals("Year")) {
                freq = Frequency.MONTHLY;
                param = LDate.getMonth(period);
            }
            int y = Integer.parseInt((String) YEAR.getSelectedItem());
            String per = Objects.requireNonNullElse((String) PERIOD.getSelectedItem(), "Year");
            LDate[] range = LDate.getRange(Integer.parseInt((String) YEAR.getSelectedItem()),
                    param, freq, CURRENT_INSTANCE);
            for (OrderBookEntry entry : CURRENT_INSTANCE.DATA_HANDLER.getOrderBook(range[0], range[1])) {
                TABLE_ACCESS.addRow(new String[]{
                        Long.toUnsignedString(entry.START_REF),
                        entry.START.toDateString(),
                        Long.toUnsignedString(entry.END_REF),
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
