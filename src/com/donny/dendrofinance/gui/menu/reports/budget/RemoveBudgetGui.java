package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.gui.customswing.ProgramModalFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;

import javax.swing.*;
import java.awt.*;

public class RemoveBudgetGui extends ProgramModalFrame {
    public RemoveBudgetGui(BudgetGui caller, String budget, ProgramInstance curInst) {
        super(caller, "Remove Budget", curInst);
        if (budget == null) {
            dispose();
        } else {
            //draw gui
            {
                JLabel a = new JLabel("Do you want to delete " + budget + "?");
                JButton no = DendroFactory.getButton("No");
                no.addActionListener(event -> dispose());
                JButton yes = DendroFactory.getButton("Yes");
                yes.addActionListener(event -> yesAction(caller, budget));

                //back layout
                {
                    GroupLayout main = new GroupLayout(getContentPane());
                    getContentPane().setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    no, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    yes, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            no, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            yes, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void yesAction(BudgetGui caller, String budget) {
        BudgetCapsule capsule = null;
        for (BudgetCapsule e : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
            if (e.getName().equals(budget)) {
                capsule = e;
                break;
            }
        }
        if (capsule != null) {
            CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.delete(capsule.getName());
            caller.updateBudget();
        }
        dispose();
    }
}
