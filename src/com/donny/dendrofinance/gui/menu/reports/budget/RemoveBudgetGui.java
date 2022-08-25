package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class RemoveBudgetGui extends ModalFrame {
    public RemoveBudgetGui(BudgetGui caller, String budget, Instance curInst) {
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

                pack();
            }
        }
    }

    private void yesAction(BudgetGui caller, String budget) {
        BudgetEntry entry = null;
        for (BudgetEntry e : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
            if (e.getName().equals(budget)) {
                entry = e;
                break;
            }
        }
        if (entry != null) {
            CURRENT_INSTANCE.DATA_HANDLER.deleteBudget(entry.getUUID());
            caller.updateBudget();
        }
        dispose();
    }
}
