package com.donny.dendrofinance.gui.menu.data.backing;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.util.ExportableToJson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BackingTableGui<E extends ExportableToJson> extends RegisterFrame {
    private final BackingTableCore<E> TABLE_CORE;

    private final JPanel BACK;
    private final JScrollPane PANE;
    private final JTable TABLE;
    private final DefaultTableModel TABLE_ACCESS;
    private final JButton UP, DOWN, EDIT, DELETE, CREATE, SORT;

    public BackingTableGui(MainGui caller, BackingTableCore<E> core, Instance curInst) {
        super(caller, curInst);
        TABLE_CORE = core;

        //draw gui
        {
            BACK = new JPanel();
            PANE = DendroFactory.getTable(TABLE_CORE.getHeader(), new Object[][]{}, false);
            TABLE = (JTable) PANE.getViewport().getView();
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
            UP.addActionListener(actionEvent -> {
                TABLE_CORE.move(TABLE.getSelectedRow(), true);
                updateTable();
            });

            DOWN = DendroFactory.getButton("Move Down");
            DOWN.addActionListener(actionEvent -> {
                TABLE_CORE.move(TABLE.getSelectedRow(), false);
                updateTable();
            });

            EDIT = DendroFactory.getButton("Edit");
            EDIT.addActionListener(event -> TABLE_CORE.getEditDialog(this, TABLE.getSelectedRow()));

            DELETE = DendroFactory.getButton("Remove");
            DELETE.addActionListener(actionEvent -> new DeleteBackingGui<>(this, TABLE_CORE, TABLE.getSelectedRow(), curInst).setVisible(true));

            CREATE = DendroFactory.getButton("New");
            CREATE.addActionListener(event -> TABLE_CORE.getEditDialog(this, -1));

            SORT = DendroFactory.getButton("Sort");
            SORT.addActionListener(actionEvent -> {
                TABLE_CORE.sort();
                updateTable();
            });
            SORT.setEnabled(TABLE_CORE.SORTABLE);

            //back layout
            {
                GroupLayout main = new GroupLayout(BACK);
                BACK.setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                        CREATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SORT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                                CREATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                SORT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
            }

            updateTable();

            add(BACK);

            pack();
        }
    }

    public final void updateTable() {
        while (TABLE.getRowCount() > 0) {
            TABLE_ACCESS.removeRow(0);
        }
        TABLE_CORE.getContents().forEach(TABLE_ACCESS::addRow);
    }

    public void tableCursorChanged(int row) {
        if (TABLE_CORE.canMove(row)) {
            UP.setEnabled(true);
            UP.setBackground(DendroFactory.REGULAR);
        } else {
            UP.setEnabled(false);
            UP.setBackground(DendroFactory.DISABLED);
        }
        if (TABLE_CORE.canMove(row)) {
            DOWN.setEnabled(true);
            DOWN.setBackground(DendroFactory.REGULAR);
        } else {
            DOWN.setEnabled(false);
            DOWN.setBackground(DendroFactory.DISABLED);
        }
        if (TABLE_CORE.canEdit(row)) {
            EDIT.setEnabled(true);
            EDIT.setBackground(DendroFactory.REGULAR);
        } else {
            EDIT.setEnabled(false);
            EDIT.setBackground(DendroFactory.DISABLED);
        }
        if (TABLE_CORE.canRemove(row)) {
            DELETE.setEnabled(true);
            DELETE.setBackground(DendroFactory.REGULAR);
        } else {
            DELETE.setEnabled(false);
            DELETE.setBackground(DendroFactory.DISABLED);
        }
    }
}
