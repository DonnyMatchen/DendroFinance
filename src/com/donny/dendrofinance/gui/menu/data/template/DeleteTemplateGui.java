package com.donny.dendrofinance.gui.menu.data.template;

import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.awt.*;

public class DeleteTemplateGui extends ModalFrame {
    public DeleteTemplateGui(JFrame caller, String name, Instance curInst) {
        super(caller, "Delete Template", curInst);
        //draw gui
        {
            JLabel a = new JLabel("Are you sure you want to delete this?");
            JScrollPane pane = DendroFactory.getScrollField();
            JTextArea text = (JTextArea) pane.getViewport().getView();
            text.setEditable(false);
            text.setText(curInst.DATA_HANDLER.DATABASE.TEMPLATES.get(name).toString());
            JButton cancel = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());
            JButton delete = DendroFactory.getButton("Ok");
            delete.addActionListener(event -> deleteAction(name, caller));

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void deleteAction(String name, JFrame caller) {
        CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TEMPLATES.delete(name);
        if (caller instanceof TemplateGui) {
            ((TemplateGui) caller).updateTable();
        }
        dispose();
    }
}
