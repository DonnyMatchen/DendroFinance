package com.donny.dendrofinance.gui.menu.file;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.io.File;

public class ImportGui extends RegisterFrame {
    private final String DIR;
    private final JLabel A;
    private final JScrollPane PANE;
    private final JList<String> LIST;
    private final DefaultListModel<String> LIST_ACCESS;
    private final JButton IMPORT, IMPORT_ALL;

    public ImportGui(MainGui caller, Instance curInst) {
        super(caller, "Import", curInst);

        DIR = CURRENT_INSTANCE.data.getPath() + File.separator + "Imports";

        //draw gui
        {
            A = new JLabel("File");
            PANE = DendroFactory.getScrollPane(false, true);
            LIST = DendroFactory.getList();
            LIST_ACCESS = (DefaultListModel<String>) LIST.getModel();
            PANE.setViewportView(LIST);
            IMPORT = DendroFactory.getButton("Import");
            IMPORT.addActionListener(event -> {
                if (LIST.getSelectedIndex() >= 0) {
                    CURRENT_INSTANCE.IMPORT_HANDLER.load(DIR + File.separator + LIST_ACCESS.get(LIST.getSelectedIndex()), this);
                    updateList();
                }
            });
            IMPORT_ALL = DendroFactory.getButton("Import All");
            IMPORT_ALL.addActionListener(event -> {
                if (LIST_ACCESS.getSize() > 0) {
                    for (int i = 0; i < LIST_ACCESS.getSize(); i++) {
                        CURRENT_INSTANCE.IMPORT_HANDLER.load(DIR + File.separator + LIST_ACCESS.get(i), this);
                    }
                    updateList();
                }
            });

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                IMPORT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                IMPORT_ALL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        IMPORT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        IMPORT_ALL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
            pack();
        }
        updateList();
    }

    private void updateList() {
        LIST_ACCESS.removeAllElements();
        File directory = new File(DIR);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    LIST_ACCESS.addElement(file.getName());
                }
            }
        }
    }
}
