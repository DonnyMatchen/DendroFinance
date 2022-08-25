package com.donny.dendrofinance.gui.menu.file;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class ExportGui extends RegisterFrame {
    private final JTextField NAME;
    private final JComboBox<String> TYPE;

    public ExportGui(MainGui caller, Instance curInst) {
        super(caller, "Export", curInst);

        //draw gui
        {
            JLabel a = new JLabel("Label");
            JLabel b = new JLabel("Extension");

            NAME = new JTextField();
            TYPE = new JComboBox<>();
            TYPE.addItem("JSON");
            TYPE.addItem("XTBL");

            JButton export = DendroFactory.getButton("Export");
            export.addActionListener(event -> exportAction());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        NAME, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                ).addComponent(
                                                        TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                )
                                        )
                                ).addComponent(
                                        export, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                export, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
            pack();
        }
    }

    private void exportAction() {
        CURRENT_INSTANCE.EXPORT_HANDLER.export((String) TYPE.getSelectedItem(), NAME.getText());
        dispose();
    }
}
