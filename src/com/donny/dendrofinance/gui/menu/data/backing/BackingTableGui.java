package com.donny.dendrofinance.gui.menu.data.backing;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.util.UniqueName;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BackingTableGui<E extends UniqueName> extends ProgramRegisterFrame {
    private final BackingTableCore<E> TABLE_CORE;

    private final JTextField SEARCH;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;
    private final JButton UP, DOWN, EDIT, DELETE;

    public BackingTableGui(ProgramMainGui caller, BackingTableCore<E> core, ProgramInstance curInst) {
        super(caller, core.getName(true), curInst);
        TABLE_CORE = core;

        //draw gui
        {
            SEARCH = new JTextField();
            SEARCH.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateTable();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateTable();
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateTable();
                }
            });

            JScrollPane pane = DendroFactory.getTable(TABLE_CORE.getHeader(), new Object[][]{}, false);
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

            UP = DendroFactory.getButton("Move Up");
            UP.addActionListener(event -> moveElement(true));

            DOWN = DendroFactory.getButton("Move Down");
            DOWN.addActionListener(event -> moveElement(false));

            EDIT = DendroFactory.getButton("Edit");
            EDIT.addActionListener(event -> TABLE_CORE.getEditDialog(this, TABLE_CORE.getIndex(getIdentifier(TABLE.getSelectedRow()))));

            DELETE = DendroFactory.getButton("Remove");
            DELETE.addActionListener(event -> new DeleteBackingGui<>(this, TABLE_CORE, TABLE_CORE.getIndex(getIdentifier(TABLE.getSelectedRow())), curInst).setVisible(true));

            JButton create = DendroFactory.getButton("New");
            create.addActionListener(event -> TABLE_CORE.getEditDialog(this, -1));

            JButton sort = DendroFactory.getButton("Sort");
            sort.addActionListener(event -> sort());
            sort.setEnabled(TABLE_CORE.SORTABLE);
            if (!sort.isEnabled()) {
                sort.setBackground(DendroFactory.DISABLED);
            }

            UP.setEnabled(false);
            UP.setBackground(DendroFactory.DISABLED);
            DOWN.setEnabled(false);
            DOWN.setBackground(DendroFactory.DISABLED);
            EDIT.setEnabled(false);
            EDIT.setBackground(DendroFactory.DISABLED);
            DELETE.setEnabled(false);
            DELETE.setBackground(DendroFactory.DISABLED);

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                        UP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        DOWN, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        EDIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        DELETE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        create, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        sort, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                UP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DOWN, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                EDIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                DELETE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                create, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                sort, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
        updateTable(SEARCH.getText());
    }

    public final void updateTable(String search) {
        while (TABLE.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        TABLE_CORE.getContents(search).forEach(TABLE_ACCESS::addRow);
    }

    private void moveElement(boolean up) {
        TABLE_CORE.move(getIdentifier(TABLE.getSelectedRow()), up);
        updateTable();
    }

    private void sort() {
        TABLE_CORE.sort();
        updateTable();
    }

    private String getIdentifier(int row) {
        return (String) TABLE.getValueAt(row, TABLE_CORE.contentIdentifierIndex());
    }

    public void tableCursorChanged(int row) {
        if (TABLE_CORE.canMove(getIdentifier(row))) {
            UP.setEnabled(true);
            UP.setBackground(DendroFactory.REGULAR);
        } else {
            UP.setEnabled(false);
            UP.setBackground(DendroFactory.DISABLED);
        }
        if (TABLE_CORE.canMove(getIdentifier(row))) {
            DOWN.setEnabled(true);
            DOWN.setBackground(DendroFactory.REGULAR);
        } else {
            DOWN.setEnabled(false);
            DOWN.setBackground(DendroFactory.DISABLED);
        }
        if (TABLE_CORE.canEdit(getIdentifier(row))) {
            EDIT.setEnabled(true);
            EDIT.setBackground(DendroFactory.REGULAR);
        } else {
            EDIT.setEnabled(false);
            EDIT.setBackground(DendroFactory.DISABLED);
        }
        if (TABLE_CORE.canRemove(getIdentifier(row))) {
            DELETE.setEnabled(true);
            DELETE.setBackground(DendroFactory.REGULAR);
        } else {
            DELETE.setEnabled(false);
            DELETE.setBackground(DendroFactory.DISABLED);
        }
    }
}
