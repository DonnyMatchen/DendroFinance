package com.donny.dendrofinance.gui.menu.util.taxgui;

import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Cleaning;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class TaxGui extends ProgramRegisterFrame {
    private final JTextField VALUE, EXEMPT, RESULT;
    private final JComboBox<String> ITEMS;

    public TaxGui(ProgramMainGui caller, ProgramInstance curInst) {
        super(caller, "Tax Calculator", curInst);

        //draw gui
        {
            VALUE = new JTextField();
            EXEMPT = new JTextField();
            RESULT = new JTextField();
            RESULT.setEditable(false);
            RESULT.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    RESULT.selectAll();
                }
            });
            ITEMS = new JComboBox<>();

            JLabel a = new JLabel("Taxable Value");
            JLabel b = new JLabel("Exemption");
            JLabel c = new JLabel("Tax Item");
            JLabel d = new JLabel("Result");

            JButton calculate = DendroFactory.getButton("Calculate");
            calculate.addActionListener(event -> calculateAction());
            JButton newButton = DendroFactory.getButton("New Tax Item");
            newButton.addActionListener(event -> new NewTaxGui(this, CURRENT_INSTANCE).setVisible(true));
            JButton delete = DendroFactory.getButton("Delete Tax Item");
            delete.addActionListener(event -> deleteAction());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                ).addComponent(
                                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        VALUE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addComponent(
                                                        EXEMPT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addComponent(
                                                        ITEMS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addComponent(
                                                        RESULT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                )
                                        )
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                calculate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(
                                                DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                        ).addComponent(
                                                newButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        )
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        VALUE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        EXEMPT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ITEMS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        RESULT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        calculate, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        newButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
        }
        updateTaxes();
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void updateTaxes() {
        ITEMS.removeAllItems();
        CURRENT_INSTANCE.TAX_ITEMS.forEach(item -> ITEMS.addItem(item.NAME));
    }

    private void calculateAction() {
        if (EXEMPT.getText().isEmpty()) {
            RESULT.setText(CURRENT_INSTANCE.$(CURRENT_INSTANCE.TAX_ITEMS.getElement((String) ITEMS.getSelectedItem()).process(Cleaning.cleanNumber(VALUE.getText()))));
        } else {
            RESULT.setText(CURRENT_INSTANCE.$(CURRENT_INSTANCE.TAX_ITEMS.getElement((String) ITEMS.getSelectedItem()).process(Cleaning.cleanNumber(VALUE.getText()), Cleaning.cleanNumber(EXEMPT.getText()))));
        }
    }

    public void deleteAction() {
        CURRENT_INSTANCE.TAX_ITEMS.deleteElement((String) ITEMS.getSelectedItem());
        updateTaxes();
    }
}
