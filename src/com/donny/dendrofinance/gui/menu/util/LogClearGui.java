package com.donny.dendrofinance.gui.menu.util;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;

public class LogClearGui extends RegisterFrame {
    private final JScrollPane LIST_PANE, VIEW_PANE;
    private final JList<String> LIST;
    private final JTextArea VIEW;
    private final JButton REMOVE, REMOVE_ALL, LOAD;

    private final File LOGS;

    public LogClearGui(MainGui caller, Instance curInst) {
        super(caller, "Log Clearing", curInst);

        LOGS = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Logs");

        //Draw Gui
        {
            LIST = new JList<>(new DefaultListModel<>());
            LIST_PANE = DendroFactory.getScrollPane(false, true);
            LIST_PANE.setViewportView(LIST);
            VIEW_PANE = DendroFactory.getScrollField();
            VIEW = (JTextArea) VIEW_PANE.getViewport().getView();
            VIEW.setEditable(false);
            REMOVE = DendroFactory.getButton("Remove");
            REMOVE.addActionListener(event -> {
                CURRENT_INSTANCE.FILE_HANDLER.delete(LOGS, LIST.getSelectedValue());
                updateList();
            });
            REMOVE_ALL = DendroFactory.getButton("Remove All");
            REMOVE_ALL.addActionListener(event -> {
                for (int i = 0; i < LIST.getModel().getSize(); i++) {
                    CURRENT_INSTANCE.FILE_HANDLER.delete(LOGS, LIST.getModel().getElementAt(i));
                }
                updateList();
            });
            LOAD = DendroFactory.getButton("Load");
            LOAD.addActionListener(event -> VIEW.setText(
                    CURRENT_INSTANCE.FILE_HANDLER.readPlain(LOGS, LIST.getSelectedValue())
            ));

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        LIST_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        VIEW_PANE, GroupLayout.PREFERRED_SIZE, 1000, Short.MAX_VALUE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        LOAD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        REMOVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        REMOVE_ALL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                LIST_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                VIEW_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                LOAD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                REMOVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                REMOVE_ALL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        )
                                )
                        ).addContainerGap()
                );
            }

            pack();
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
