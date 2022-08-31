package com.donny.dendrofinance.gui.menu.util;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class LogClearGui extends RegisterFrame {
    private final JList<String> LIST;
    private final JTextArea VIEW;

    private final File LOGS;

    public LogClearGui(MainGui caller, Instance curInst) {
        super(caller, "Log Clearing", curInst);

        LOGS = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Logs");

        //Draw Gui
        {
            LIST = new JList<>(new DefaultListModel<>());
            JScrollPane listPane = DendroFactory.getScrollPane(false, true);
            listPane.setViewportView(LIST);
            JScrollPane viewPane = DendroFactory.getScrollField();
            VIEW = (JTextArea) viewPane.getViewport().getView();
            VIEW.setEditable(false);
            JButton remove = DendroFactory.getButton("Remove");
            remove.addActionListener(event -> removeAction());
            JButton removeAll = DendroFactory.getButton("Remove All");
            removeAll.addActionListener(event -> removeAllAction());
            JButton load = DendroFactory.getButton("Load");
            load.addActionListener(event -> VIEW.setText(
                    CURRENT_INSTANCE.FILE_HANDLER.read(LOGS, LIST.getSelectedValue())
            ));

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        listPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        viewPane, GroupLayout.PREFERRED_SIZE, 1000, Short.MAX_VALUE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        load, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        removeAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                listPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                viewPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                load, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                remove, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                removeAll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        )
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

    private void removeAction() {
        CURRENT_INSTANCE.FILE_HANDLER.delete(LOGS, LIST.getSelectedValue());
        updateList();
    }

    private void removeAllAction() {
        for (int i = 0; i < LIST.getModel().getSize(); i++) {
            CURRENT_INSTANCE.FILE_HANDLER.delete(LOGS, LIST.getModel().getElementAt(i));
        }
        updateList();
    }

    private void updateList() {
        ((DefaultListModel<String>) LIST.getModel()).removeAllElements();
        if (LOGS.exists()) {
            File[] files = LOGS.listFiles();
            if (files != null) {
                ArrayList<String> temp = new ArrayList<>();
                for (File f : files) {
                    temp.add(f.getName());
                }
                temp.sort(String::compareTo);
                for (String s : temp) {
                    ((DefaultListModel<String>) LIST.getModel()).addElement(s);
                }
            }
        }
    }
}
