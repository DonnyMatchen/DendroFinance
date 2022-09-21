package com.donny.dendrofinance.gui.menu.data.backing;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.util.ExportableToJson;

import javax.swing.*;
import java.awt.*;

public class DeleteBackingGui<E extends ExportableToJson> extends ModalFrame {
    public DeleteBackingGui(BackingTableGui<E> caller, BackingTableCore<E> core, int index, Instance curInst) {
        super(caller, "Delete " + core.getName(false), curInst);

        //draw gui
        {
            JLabel a = new JLabel("Are you sure you want to delete this?");
            JScrollPane pane = DendroFactory.getScrollField();
            JTextArea text = (JTextArea) pane.getViewport().getView();
            text.setEditable(false);
            try {
                text.setText(core.getElement(index).export().toString());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed object" + core.getElement(index));
                dispose();
            }
            JButton cancel = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());
            JButton delete = DendroFactory.getButton("Ok");
            delete.addActionListener(event -> deleteAction(caller, core, index));

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

    public void deleteAction(BackingTableGui<E> caller, BackingTableCore<E> core, int index) {
        core.deleteElement(index);
        caller.updateTable();
        dispose();
    }
}
