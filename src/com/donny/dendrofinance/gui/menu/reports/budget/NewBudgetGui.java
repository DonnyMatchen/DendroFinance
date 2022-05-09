package com.donny.dendrofinance.gui.menu.reports.budget;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class NewBudgetGui extends JDialog {
    public final BudgetGui CALLER;
    private final Instance CURRENT_INSTANCE;
    private final JLabel A, B;
    private final JTextField NAME;
    private final JComboBox<String> TEMPLATE;
    private final JButton CANCEL, OK;

    public NewBudgetGui(BudgetGui caller, Instance curInst) {
        super(caller, "New Budget", true);
        CURRENT_INSTANCE = curInst;
        CALLER = caller;
        //draw gui
        {
            A = new JLabel("Budget Name:");
            B = new JLabel("Template");
            NAME = new JTextField();
            TEMPLATE = new JComboBox<>();
            TEMPLATE.addItem("Blank");
            for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                TEMPLATE.addItem(entry.getName());
            }
            CANCEL = DendroFactory.getButton("Cancel");
            CANCEL.addActionListener(event -> dispose());
            OK = DendroFactory.getButton("Ok");
            OK.addActionListener(event -> {
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
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                TEMPLATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        TEMPLATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
