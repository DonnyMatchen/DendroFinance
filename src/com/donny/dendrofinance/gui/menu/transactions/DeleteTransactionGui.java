package com.donny.dendrofinance.gui.menu.transactions;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.awt.*;

public class DeleteTransactionGui extends ModalFrame {
    public final MainGui CALLER;
    public final long UUID;

    public DeleteTransactionGui(MainGui caller, long uuid, Instance curInst) {
        super(caller, "Delete Transaction Entry", curInst);
        UUID = uuid;
        CALLER = caller;
        //draw gui
        JScrollPane pane = DendroFactory.getScrollField(false, 5, 40);
        JTextArea area = (JTextArea) pane.getViewport().getView();
        {
            JLabel a = new JLabel("Are you sure you'd like to delete");
            JLabel b = new JLabel("the capsule bellow?");

            JButton ok = DendroFactory.getButton("Yes");
            ok.addActionListener(event -> yes());
            JButton cancel = DendroFactory.getButton("No");
            cancel.addActionListener(event -> dispose());
            //Group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
        }
        area.setText(CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.get(UUID).toString());
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void yes() {
        CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.delete(UUID);
        CALLER.updateTable();
        dispose();
    }
}
