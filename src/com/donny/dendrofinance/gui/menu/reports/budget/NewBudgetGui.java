package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class NewBudgetGui extends JDialog {
    public final BudgetGui CALLER;
    private final Instance CURRENT_INSTANCE;
    private final JLabel A;
    private final JTextField NAME;
    private final JButton CANCEL, OK;

    public NewBudgetGui(BudgetGui caller, Instance curInst) {
        super(caller, "New Budget", true);
        CURRENT_INSTANCE = curInst;
        CALLER = caller;
        //draw gui
        {
            A = new JLabel("Budget Name:");
            NAME = new JTextField();
            CANCEL = DendroFactory.getButton("Cancel");
            CANCEL.addActionListener(event -> dispose());
            OK = DendroFactory.getButton("Ok");
            OK.addActionListener(event -> {
                CURRENT_INSTANCE.DATA_HANDLER.addBudget(BudgetEntry.defaultBudget(NAME.getText(), CURRENT_INSTANCE));
                CALLER.updateBudget();
                dispose();
            });

            //back layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                NAME, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                OK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        NAME, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        OK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }

            pack();
        }
    }
}
