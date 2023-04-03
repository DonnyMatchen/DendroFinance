package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.customswing.ProgramModalFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;

import javax.swing.*;
import java.awt.*;

public class NewBudgetGui extends ProgramModalFrame {
    public final BudgetGui CALLER;
    private final JTextField NAME;
    private final JComboBox<String> TEMPLATE;

    public NewBudgetGui(BudgetGui caller, ProgramInstance curInst) {
        super(caller, "New Budget", curInst);
        CALLER = caller;
        //draw gui
        {
            JLabel a = new JLabel("Budget Name");
            JLabel b = new JLabel("Template");
            NAME = new JTextField();
            TEMPLATE = new JComboBox<>();
            TEMPLATE.addItem("Blank");
            for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                TEMPLATE.addItem(capsule.getName());
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
        BudgetCapsule template = null;
        if (NAME.getText().equals("")) {
            flag = false;
        }
        if (flag) {
            for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                if (capsule.getName().equalsIgnoreCase(NAME.getText())) {
                    flag = false;
                    break;
                }
                if (capsule.getName().equals(TEMPLATE.getSelectedItem().toString())) {
                    template = capsule;
                }
            }
        }
        if (flag) {
            if (template == null) {
                CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.add(new BudgetCapsule(NAME.getText(), CURRENT_INSTANCE), ImportHandler.ImportMode.KEEP);
            } else {
                CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.add(new BudgetCapsule(template, NAME.getText()), ImportHandler.ImportMode.KEEP);
            }
            CALLER.updateBudget();
            dispose();
        } else {
            NAME.setBackground(DendroFactory.WRONG);
        }
    }
}
