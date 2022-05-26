package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.data.metatable.MetaTableCore;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MetaTableGui extends RegisterFrame {
    private final MetaTableCore CORE;

    private final JPanel TAB, INF;
    private final JLabel A, B, C, D;
    private final JTextField DATE, SEARCH;
    private final JButton ENTER;
    private final JScrollPane TABLE_PANE, INFO_PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;
    private final JTextArea INFO;

    public MetaTableGui(MainGui caller, MetaTableCore core, Instance curInst) {
        super(caller, core.getId(true), curInst);
        CORE = core;

        //draw GUI
        {
            //table
            {
                TAB = new JPanel();
                TABLE_PANE = DendroFactory.getTable(CORE.getHeader(), new Object[][]{}, false);
                TABLE = (JTable) TABLE_PANE.getViewport().getView();
                TABLE.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent event) {
                        tableCursorChanged(TABLE.getSelectedRow());
                    }
                });
                TABLE.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent event) {
                        int keyCode = event.getKeyCode();
                        if (keyCode == 38 || keyCode == 40) {
                            int orig = TABLE.getSelectedRow();
                            if ((keyCode != 38 || orig != 0) && (keyCode != 40 || orig != TABLE.getRowCount() - 1)) {
                                if (keyCode == 38) {
                                    tableCursorChanged(orig - 1);
                                } else {
                                    tableCursorChanged(orig + 1);
                                }
                            }

                        }
                    }
                });
                TABLE_ACCESS = (DefaultTableModel) TABLE.getModel();
                A = new JLabel(CORE.getName() + " Table");
                C = new JLabel("Date");
                D = new JLabel("Search");
                DATE = new JTextField();
                DATE.setText(LDate.now(CURRENT_INSTANCE).toString(false));
                SEARCH = new JTextField();
                ENTER = DendroFactory.getButton("Search");
                ENTER.addActionListener(event -> updateTable());
                //group layout
                {
                    GroupLayout main = new GroupLayout(TAB);
                    TAB.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addComponent(
                                            TABLE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addComponent(
                                    A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TABLE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }

            //info
            {
                INF = new JPanel();
                B = new JLabel(CORE.getName() + " Information");
                INFO_PANE = DendroFactory.getScrollField(false, 10, 40);
                INFO = (JTextArea) INFO_PANE.getViewport().getView();
                //group layout
                {
                    GroupLayout main = new GroupLayout(INF);
                    INF.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            INFO_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addComponent(
                                    B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    INFO_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }

            GroupLayout layout = new GroupLayout(getContentPane());
            getContentPane().setLayout(layout);
            layout.setHorizontalGroup(
                    layout.createSequentialGroup().addContainerGap().addComponent(
                            TAB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            INF, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                    ).addContainerGap()
            );
            layout.setVerticalGroup(
                    layout.createSequentialGroup().addContainerGap().addGroup(
                            layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    TAB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addComponent(
                                    INF, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            )
                    ).addContainerGap()
            );

            pack();
        }

        updateTable();
    }

    private void updateTable() {
        while (TABLE_ACCESS.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        for (String[] row : CORE.getContents(new LDate(DATE.getText(), CURRENT_INSTANCE), SEARCH.getText())) {
            TABLE_ACCESS.addRow(row);
        }
    }

    private void tableCursorChanged(int row) {
        String identifier = (String) TABLE.getValueAt(row, 0);
        String name = (String) TABLE.getValueAt(row, 1);
        if (identifier.equals("")) {
            tableCursorChanged(row - 1);
        } else {
            INFO.setText(CORE.print(identifier, name, new LDate(DATE.getText(), CURRENT_INSTANCE)));
        }
    }
}
