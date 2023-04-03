package com.donny.dendrofinance.gui.menu.data;

import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class BudgetTypeGui extends ProgramRegisterFrame {
    private final JTextArea BUDGETS;

    public BudgetTypeGui(ProgramMainGui caller, ProgramInstance curInst) {
        super(caller, "Budget Type Edit", curInst);

        //draw gui
        {
            JLabel a = new JLabel("List of Budget Types");
            JScrollPane scroll = DendroFactory.getScrollField();
            BUDGETS = (JTextArea) scroll.getViewport().getView();
            JButton save = DendroFactory.getButton("Save");
            save.addActionListener(event -> saveAction());
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
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        scroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                scroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void saveAction() {
        String[] lines = BUDGETS.getText().split("\n");
        CURRENT_INSTANCE.DATA_HANDLER.budgetTypesChanged = true;
        CURRENT_INSTANCE.DATA_HANDLER.resetBudgetTypes(new ArrayList<>(Arrays.asList(lines)));
    }
}
