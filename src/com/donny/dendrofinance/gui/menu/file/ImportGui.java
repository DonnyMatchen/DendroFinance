package com.donny.dendrofinance.gui.menu.file;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.io.File;

public class ImportGui extends RegisterFrame {
    private final String DIR;
    private final MainGui MAIN_GUI;
    private final JLabel A, B;
    private final JComboBox<String> MODE;
    private final JScrollPane PANE;
    private final JList<String> LIST;
    private final DefaultListModel<String> LIST_ACCESS;
    private final JButton IMPORT, IMPORT_ALL;

    public ImportGui(MainGui caller, Instance curInst) {
        super(caller, "Import", curInst);

        DIR = CURRENT_INSTANCE.data.getPath() + File.separator + "Imports";
        MAIN_GUI = caller;

        //draw gui
        {
            A = new JLabel("File");
            B = new JLabel("Mode");

            MODE = new JComboBox<>();
            MODE.addItem("Keep Both");
            MODE.addItem("Ignore New");
            MODE.addItem("Replace Old");

            PANE = DendroFactory.getScrollPane(false, true);
            LIST = DendroFactory.getList();
            LIST_ACCESS = (DefaultListModel<String>) LIST.getModel();
            PANE.setViewportView(LIST);
            IMPORT = DendroFactory.getButton("Import");
            IMPORT.addActionListener(event -> importAction());
            IMPORT_ALL = DendroFactory.getButton("Import All");
            IMPORT_ALL.addActionListener(event -> importAllAction());

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
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                MODE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
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
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        MODE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
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

    private void importAction() {
        if (LIST.getSelectedIndex() >= 0) {
            CURRENT_INSTANCE.IMPORT_HANDLER.load(DIR + File.separator + LIST_ACCESS.get(LIST.getSelectedIndex()),
                    this,
                    ImportHandler.ImportMode.fromString((String) MODE.getSelectedItem()));
            updateList();
            MAIN_GUI.updateTable();
        }
    }

    private void importAllAction() {
        if (LIST_ACCESS.getSize() > 0) {
            for (int i = 0; i < LIST_ACCESS.getSize(); i++) {
                CURRENT_INSTANCE.IMPORT_HANDLER.load(DIR + File.separator + LIST_ACCESS.get(i),
                        this,
                        ImportHandler.ImportMode.fromString((String) MODE.getSelectedItem()));
            }
            updateList();
            MAIN_GUI.updateTable();
        }
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
