package com.donny.dendrofinance.gui.menu.file;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DateRange;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.awt.*;

public class ExportGui extends RegisterFrame {
    private final DateRange RANGE;
    private final JTextField NAME;
    private final JComboBox<String> TYPE;

    public ExportGui(MainGui caller, Instance curInst) {
        super(caller, "Export", curInst);

        //draw gui
        {
            JLabel a = new JLabel("Full Range");
            JLabel b = new JLabel("Date Range");
            JLabel c = new JLabel("Label");
            JLabel d = new JLabel("Extension");

            RANGE = new DateRange(false);
            DateRange display = new DateRange(false);
            display.setEditable(false);
            display.init(
                    new LDate(CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TRANSACTIONS.getMinDate(), CURRENT_INSTANCE),
                    new LDate(CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TRANSACTIONS.getMaxDate(), CURRENT_INSTANCE)
            );

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
                                                ).addComponent(
                                                        c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        display, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                ).addComponent(
                                                        RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addComponent(
                                                        NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                        display, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        RANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                export, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }
        RANGE.initDefault(CURRENT_INSTANCE);
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void exportAction() {
        LDate[] range = RANGE.getRange(CURRENT_INSTANCE);
        if (range != null) {
            CURRENT_INSTANCE.EXPORT_HANDLER.export(range[0], range[1], (String) TYPE.getSelectedItem(), NAME.getText(), this);
            dispose();
        }
    }
}
