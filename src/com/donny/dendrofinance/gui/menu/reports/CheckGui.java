package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.capsules.meta.CheckMetadata;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DateRange;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CheckGui extends RegisterFrame {
    private final DateRange RANGE;
    private final JTextField SEARCH;
    private final JCheckBox OUTSTANDING;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public CheckGui(MainGui caller, Instance curInst) {
        super(caller, "Check Gui", curInst);

        //draw gui
        {
            JLabel a = new JLabel("Search");
            RANGE = new DateRange(true);
            RANGE.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable();
                    }
                }
            });

            SEARCH = new JTextField();
            SEARCH.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent keyEvent) {
                    if (keyEvent.getKeyChar() == '\n') {
                        updateTable();
                    }
                }
            });

            JButton enter = DendroFactory.getButton("Search");
            enter.addActionListener(event -> updateTable());

            JScrollPane pane = DendroFactory.getTable(new String[]{
                    "UUID", "Issued", "Cashed", "Check Number", "Value"
            }, new Object[][]{}, false);
            TABLE = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();

            OUTSTANDING = new JCheckBox("Only Outstanding Checks");

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addComponent(
                                        OUTSTANDING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                OUTSTANDING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }
        }
        RANGE.initDefault(CURRENT_INSTANCE);
        updateTable();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void updateTable() {
        while (TABLE.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        LDate[] range = RANGE.getRange(CURRENT_INSTANCE);
        if (range != null) {
            ArrayList<CheckMetadata> meta = CURRENT_INSTANCE.DATA_HANDLER.getChecks(range[0], range[1]);
            for (CheckMetadata check : meta) {
                if (
                        check.CHECK_NUMBER.toLowerCase().contains(SEARCH.getText().toLowerCase()) &&
                                (!OUTSTANDING.isSelected() || check.isOutstanding())
                ) {
                    TABLE_ACCESS.addRow(new String[]{
                            "" + check.REF,
                            check.ISSUED.toDateString(),
                            check.isOutstanding() ? "Outstanding" : check.CASHED.toDateString(),
                            check.CHECK_NUMBER,
                            CURRENT_INSTANCE.$(check.VALUE)
                    });
                }
            }
        }
    }
}
