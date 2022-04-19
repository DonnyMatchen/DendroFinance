package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class PassFailGui extends JDialog {
    private final JButton OK;
    private final JLabel A;
    private final JPanel BACK;

    public PassFailGui(PasswordGui parent, Instance curInst) {
        super(parent, true);
        //draw gui
        {
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

            BACK = new JPanel();
            A = new JLabel("Password Incorrect!");

            OK = DendroFactory.getButton("OK");
            OK.addActionListener(event -> dispose());
            {
                GroupLayout backgroundLayout = new GroupLayout(BACK);
                BACK.setLayout(backgroundLayout);
                backgroundLayout.setHorizontalGroup(
                        backgroundLayout.createSequentialGroup().addContainerGap().addGroup(
                                backgroundLayout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        OK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                backgroundLayout.setVerticalGroup(
                        backgroundLayout.createSequentialGroup().addContainerGap().addComponent(
                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                OK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            add(BACK);

            pack();
        }
        curInst.data = curInst.data.getParentFile();
    }
}
