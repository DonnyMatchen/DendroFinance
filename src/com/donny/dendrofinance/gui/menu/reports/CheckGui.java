package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.entry.meta.CheckMetadata;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class CheckGui extends RegisterFrame {
    private final JLabel A, B;
    private final JTextField DATE, SEARCH;
    private final JCheckBox OUTSTANDING;
    private final JButton ENTER;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public CheckGui(MainGui caller, Instance curInst) {
        super(caller, "Check Gui", curInst);

        //draw gui
        {
            A = new JLabel("Date");
            B = new JLabel("Search");

            DATE = new JTextField();
            DATE.setText(LDate.now(CURRENT_INSTANCE).toString(false));
            SEARCH = new JTextField();

            ENTER = DendroFactory.getButton("Search");
            ENTER.addActionListener(event -> updateTable());

            PANE = DendroFactory.getTable(new String[]{
                    "UUID", "Issued", "Cashed", "Check Number", "Value"
            }, new Object[][]{}, false);
            TABLE = (JTable) PANE.getViewport().getView();
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
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addComponent(
                                        OUTSTANDING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                OUTSTANDING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addContainerGap()
                );
            }
            pack();
        }
        updateTable();
    }

    public void updateTable() {
        while (TABLE.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        ArrayList<CheckMetadata> meta;
        try {
            meta = CURRENT_INSTANCE.DATA_HANDLER.getChecks(new LDate(DATE.getText(), CURRENT_INSTANCE));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            meta = CURRENT_INSTANCE.DATA_HANDLER.getChecks();
        }
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
