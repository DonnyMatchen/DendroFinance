package com.donny.dendrofinance.gui.menu.data;

import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BudgetTypeGui extends RegisterFrame {
    private final JLabel A;
    private final JScrollPane SCROLL;
    private final JTextArea BUDGETS;
    private final JButton SAVE;

    public BudgetTypeGui(MainGui caller, Instance curInst) {
        super(caller, "Budget Type Edit", curInst);

        //draw gui
        {
            A = new JLabel("List of Budget Types");
            SCROLL = DendroFactory.getScrollField();
            BUDGETS = (JTextArea) SCROLL.getViewport().getView();
            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> saveAction());
            StringBuilder builder = new StringBuilder();
            CURRENT_INSTANCE.DATA_HANDLER.getBudgetTypes().forEach(type -> builder.append(type).append("\n"));
            BUDGETS.setText(builder.toString());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SCROLL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                SCROLL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
            pack();
        }
    }

    private void saveAction() {
        String[] lines = BUDGETS.getText().split("\n");
        CURRENT_INSTANCE.DATA_HANDLER.budgetTypesChanged = true;
        CURRENT_INSTANCE.DATA_HANDLER.resetBudgetTypes(new ArrayList<>(Arrays.asList(lines)));
    }
}
