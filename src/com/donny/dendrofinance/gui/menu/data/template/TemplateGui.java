package com.donny.dendrofinance.gui.menu.data.template;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.AlertGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TemplateGui extends RegisterFrame {
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;

    public TemplateGui(MainGui caller, Instance curInst) {
        super(caller, "Templates", curInst);

        //draw gui
        {
            JScrollPane pane = DendroFactory.getTable(new String[]{"Name", "Ref"}, new Object[][]{}, false);
            TABLE = (JTable) pane.getViewport().getView();
            TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();

            JButton edit = DendroFactory.getButton("Edit");
            edit.addActionListener(event -> {
                if (cursorSelection()) {
                    new NewTemplateGui(this, getCapsuleName(), CURRENT_INSTANCE).setVisible(true);
                } else {
                    new AlertGui(this, "No Template Selected!", CURRENT_INSTANCE).setVisible(true);
                }
            });

            JButton delete = DendroFactory.getButton("Remove");
            delete.addActionListener(event -> {
                if (cursorSelection()) {
                    new DeleteTemplateGui(this, getCapsuleName(), CURRENT_INSTANCE).setVisible(true);
                } else {
                    new AlertGui(this, "No Template Selected!", CURRENT_INSTANCE).setVisible(true);
                }
            });

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                        edit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                edit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
            }

            updateTable();
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public final void updateTable() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.getTemplates().forEach(capsule -> TABLE_ACCESS.addRow(new String[]{
                capsule.getName(),
                Long.toUnsignedString(capsule.getRef())
        }));
    }

    private boolean cursorSelection() {
        return TABLE.getSelectedRow() >= 0;
    }

    private String getCapsuleName() {
        return (String) TABLE_ACCESS.getValueAt(TABLE.getSelectedRow(), 0);
    }
}
