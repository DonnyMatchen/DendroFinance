package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.gui.customswing.DendroFactory;

import javax.swing.*;

public class ClosePrompt extends JDialog {
    private final JLabel A;
    private final JButton CANCEL, D_SAVE, SAVE;

    public ClosePrompt(MainGui caller) {
        super(caller, "Close", true);

        //make gui
        {
            A = new JLabel("Do you want to save?");
            CANCEL = DendroFactory.getButton("Cancel");
            CANCEL.addActionListener(event -> dispose());
            D_SAVE = DendroFactory.getButton("Don't Save");
            D_SAVE.addActionListener(event -> caller.conclude(false));
            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> caller.conclude(true));

            //GroupLayout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.LARGE_GAP, DendroFactory.LARGE_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                D_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.LARGE_GAP, DendroFactory.LARGE_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addComponent(
                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        D_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }

            pack();
        }
    }
}
