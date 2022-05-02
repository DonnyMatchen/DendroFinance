package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class StockEditGui extends BackingEditGui<LStock> {
    private JTextField name, ticker;
    private JCheckBox publik, dead;
    private JLabel a, b;
    private JButton save, cancel;

    public StockEditGui(BackingTableGui<LStock> caller, BackingTableCore<LStock> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        ticker = new JTextField();
        publik = new JCheckBox("Publicly Traded");
        dead = new JCheckBox("No Longer Traded");

        a = new JLabel("Company Name");
        b = new JLabel("Stock Ticker");

        cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        save = DendroFactory.getButton("Save");
        save.addActionListener(event -> {
            if (INDEX >= 0) {
                try {
                    TABLE.replace(INDEX, new LStock(
                            Validation.validateString(name),
                            Validation.validateString(ticker),
                            publik.isSelected(),
                            dead.isSelected(),
                            CURRENT_INSTANCE
                    ));
                    dispose();
                } catch (ValidationFailedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!");
                }
            } else {
                try {
                    TABLE.add(new LStock(
                            Validation.validateString(name),
                            Validation.validateString(ticker),
                            publik.isSelected(),
                            dead.isSelected(),
                            CURRENT_INSTANCE
                    ));
                    dispose();
                } catch (ValidationFailedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!");
                }
            }
        });

        //populate if in edit mode
        if (INDEX >= 0) {
            LStock stk = TABLE.getElement(INDEX);
            name.setText(stk.getName());
            ticker.setText(stk.getTicker());
            publik.setSelected(stk.isPublic());
            dead.setSelected(stk.isDead());
            if (stk.inAccount()) {
                ticker.setEditable(false);
                ticker.setBackground(DendroFactory.BACKDROP);
            }
        }

        //Group Layout
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
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    ticker, 250, 250, Short.MAX_VALUE
                                            )
                                    )
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            publik, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            dead, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGroup(
                                    main.createSequentialGroup().addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGap(
                                            DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                    ).addComponent(
                                            save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    ).addContainerGap()
            );
            main.setVerticalGroup(
                    main.createSequentialGroup().addContainerGap().addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    name, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    ticker, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            publik, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            dead, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                    ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addContainerGap()
            );
        }
    }
}
