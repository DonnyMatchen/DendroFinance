package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.awt.*;

public class NewBudgetGui extends ModalFrame {
    public final BudgetGui CALLER;
    private final JTextField NAME;
    private final JComboBox<String> TEMPLATE;

    public NewBudgetGui(BudgetGui caller, Instance curInst) {
        super(caller, "New Budget", curInst);
        CALLER = caller;
        //draw gui
        {
            JLabel a = new JLabel("Budget Name");
            JLabel b = new JLabel("Template");
            NAME = new JTextField();
            TEMPLATE = new JComboBox<>();
            TEMPLATE.addItem("Blank");
            for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                TEMPLATE.addItem(entry.getName());
            }
            JButton cancel = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());
            JButton ok = DendroFactory.getButton("Ok");
            ok.addActionListener(event -> okAction());

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                NAME, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                TEMPLATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        TEMPLATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ok, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void okAction() {
        boolean flag = true;
        BudgetEntry template = null;
        if (NAME.getText().equals("")) {
            flag = false;
        }
        if (flag) {
            for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                if (entry.getName().equalsIgnoreCase(NAME.getText())) {
                    flag = false;
                    break;
                }
                if (entry.getName().equals(TEMPLATE.getSelectedItem().toString())) {
                    template = entry;
                }
            }
        }
        if (flag) {
            if (template == null) {
                CURRENT_INSTANCE.DATA_HANDLER.addBudget(new BudgetEntry(NAME.getText(), CURRENT_INSTANCE));
            } else {
                CURRENT_INSTANCE.DATA_HANDLER.addBudget(new BudgetEntry(template, NAME.getText()));
            }
            CALLER.updateBudget();
            dispose();
        } else {
            NAME.setBackground(DendroFactory.WRONG);
        }
    }
}
