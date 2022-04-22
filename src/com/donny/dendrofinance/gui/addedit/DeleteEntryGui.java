package com.donny.dendrofinance.gui.addedit;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class DeleteEntryGui extends JDialog {
    public final Instance CURRENT_INSTANCE;
    public final MainGui MAIN;
    public final long UUID;

    private final JLabel TEXT1, TEXT2;
    private final JButton OK, CANCEL;
    private final JScrollPane PANE;
    private final JTextArea AREA;

    public DeleteEntryGui(MainGui caller, long uuid, Instance curInst) {
        super(caller, true);
        UUID = uuid;
        CURRENT_INSTANCE = curInst;
        MAIN = caller;
        //draw gui
        {
            TEXT1 = new JLabel("Are you sure you'd like to delete");
            TEXT2 = new JLabel("the entry bellow?");

            PANE = DendroFactory.getScrollField(false, 5, 40);
            AREA = (JTextArea) PANE.getViewport().getView();

            OK = DendroFactory.getButton("Yes");
            OK.addActionListener(event -> yes());
            CANCEL = DendroFactory.getButton("No");
            CANCEL.addActionListener(event -> dispose());
            //back
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        TEXT1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        TEXT2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                OK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                TEXT1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                TEXT2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, 350, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        OK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }

            pack();
        }
        AREA.setText(CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID).toString());
    }

    public void yes() {
        CURRENT_INSTANCE.DATA_HANDLER.deleteTransaction(UUID);
        MAIN.updateTable();
        dispose();
    }
}
