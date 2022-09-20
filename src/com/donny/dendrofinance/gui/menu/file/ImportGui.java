package com.donny.dendrofinance.gui.menu.file;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImportGui extends RegisterFrame {
    private final String DIR;
    private final MainGui MAIN_GUI;
    private final JComboBox<String> MODE;
    private final JList<String> LIST;
    private final DefaultListModel<String> LIST_ACCESS;

    public ImportGui(MainGui caller, Instance curInst) {
        super(caller, "Import", curInst);

        DIR = CURRENT_INSTANCE.data.getPath() + File.separator + "Imports";
        MAIN_GUI = caller;

        //draw gui
        {
            JLabel a = new JLabel("File");
            JLabel b = new JLabel("Mode");

            MODE = new JComboBox<>();
            MODE.addItem("Keep Both");
            MODE.addItem("Ignore New");
            MODE.addItem("Replace Old");

            JScrollPane pane = DendroFactory.getScrollPane(false, true);
            LIST = DendroFactory.getList();
            LIST_ACCESS = (DefaultListModel<String>) LIST.getModel();
            pane.setViewportView(LIST);
            JButton importButton = DendroFactory.getButton("Import");
            importButton.addActionListener(event -> importAction());
            JButton importAll = DendroFactory.getButton("Import All");
            importAll.addActionListener(event -> importAllAction());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                MODE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                importButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                importAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        MODE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        importButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        importAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
        }
        updateList();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
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
