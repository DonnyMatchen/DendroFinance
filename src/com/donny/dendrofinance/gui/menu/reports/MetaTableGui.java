package com.donny.dendrofinance.gui.menu.reports;

import com.donny.dendrofinance.data.metatable.MetaTableCore;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MetaTableGui extends ProgramRegisterFrame {
    private final MetaTableCore CORE;

    private final JTextField DATE, SEARCH;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;
    private final JTextArea INFO;

    public MetaTableGui(ProgramMainGui caller, MetaTableCore core, ProgramInstance curInst) {
        super(caller, core.getId(true), curInst);
        CORE = core;

        //draw GUI
        {
            //table
            JPanel TAB = new JPanel();
            {
                JScrollPane pane = DendroFactory.getTable(CORE.getHeader(), new Object[][]{}, false);
                TABLE = (JTable) pane.getViewport().getView();
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
                JLabel a = new JLabel(CORE.getName() + " Table");
                JLabel c = new JLabel("Date");
                JLabel d = new JLabel("Search");
                DATE = new JTextField();
                DATE.setText(LDate.now(CURRENT_INSTANCE).toString(false));
                DATE.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent keyEvent) {
                        if (keyEvent.getKeyChar() == '\n') {
                            updateTable();
                        }
                    }
                });
                SEARCH = new JTextField();
                SEARCH.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent keyEvent) {
                        if (keyEvent.getKeyChar() == '\n') {
                            updateTable();
                        }
                    }
                });
                JButton enter = DendroFactory.getButton("Search");
                enter.addActionListener(event -> updateTable());
                //group layout
                {
                    GroupLayout main = new GroupLayout(TAB);
                    TAB.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                                    enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            )
                                    ).addComponent(
                                            pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }

            //info
            JPanel INF = new JPanel();
            {
                JLabel a = new JLabel(CORE.getName() + " Information");
                JScrollPane pane = DendroFactory.getScrollField(false, 10, 40);
                INFO = (JTextArea) pane.getViewport().getView();
                //group layout
                {
                    GroupLayout main = new GroupLayout(INF);
                    INF.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                    pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
        }
        updateTable();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void updateTable() {
        try {
            LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
            while (TABLE_ACCESS.getRowCount() > 0) {
                TABLE_ACCESS.removeRow(0);
            }
            for (String[] row : CORE.getContents(date, SEARCH.getText())) {
                TABLE_ACCESS.addRow(row);
            }
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Not a valid date: " + DATE.getText());
        }
    }

    private void tableCursorChanged(int row) {
        try {
            LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
            String identifier = (String) TABLE.getValueAt(row, 0);
            String name = (String) TABLE.getValueAt(row, 1);
            if (identifier.isEmpty()) {
                tableCursorChanged(row - 1);
            } else {
                INFO.setText(CORE.print(identifier, name, date));
            }
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Not a valid date: " + DATE.getText());
        }
    }
}
