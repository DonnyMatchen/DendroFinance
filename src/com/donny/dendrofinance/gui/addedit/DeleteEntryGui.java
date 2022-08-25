package com.donny.dendrofinance.gui.addedit;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class DeleteEntryGui extends ModalFrame {
    public final MainGui MAIN;
    public final long UUID;

    public DeleteEntryGui(MainGui caller, long uuid, Instance curInst) {
        super(caller, "Delete Transaction Entry", curInst);
        UUID = uuid;
        MAIN = caller;
        //draw gui
        JTextArea area;
        {
            JLabel text1 = new JLabel("Are you sure you'd like to delete");
            JLabel text2 = new JLabel("the entry bellow?");

            JScrollPane pane = DendroFactory.getScrollField(false, 5, 40);
            area = (JTextArea) pane.getViewport().getView();

            JButton ok = DendroFactory.getButton("Yes");
            ok.addActionListener(event -> yes());
            JButton cancel = DendroFactory.getButton("No");
            cancel.addActionListener(event -> dispose());
            //back
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        text1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        text2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                text1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                text2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, 350, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }

            pack();
        }
        area.setText(CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID).toString());
    }

    public void yes() {
        CURRENT_INSTANCE.DATA_HANDLER.deleteTransaction(UUID);
        MAIN.updateTable();
        dispose();
    }
}
