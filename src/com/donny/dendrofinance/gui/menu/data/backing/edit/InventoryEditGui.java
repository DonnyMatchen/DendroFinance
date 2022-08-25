package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class InventoryEditGui extends BackingEditGui<LInventory> {
    private JTextField name, ticker, symbol, places, val;
    private JCheckBox merch, comod, publik;

    public InventoryEditGui(BackingTableGui<LInventory> caller, BackingTableCore<LInventory> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        ticker = new JTextField();
        symbol = new JTextField();
        places = new JTextField();
        val = new JTextField();
        merch = new JCheckBox("Merchandise");
        comod = new JCheckBox("Commodity");
        publik = new JCheckBox("Public");

        JLabel a = new JLabel("Inventory Name");
        JLabel b = new JLabel("Commodity Ticker");
        JLabel c = new JLabel("Inventory Symbol");
        JLabel d = new JLabel("Decimal Places");
        JLabel e = new JLabel("Static Value");

        JButton cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JButton save = DendroFactory.getButton("Save");
        save.addActionListener(event -> saveAction());

        //populate if in edit mode
        if (INDEX >= 0) {
            LInventory inv = TABLE.getElement(INDEX);
            name.setText(inv.getName());
            ticker.setText(inv.getTicker());
            symbol.setText(inv.getSymbol().replace("§", ""));
            places.setText("" + inv.getPlaces());
            val.setText(inv.getUnitValue().toString());
            merch.setSelected(inv.isMerchandise());
            comod.setSelected(inv.isCommodity());
            publik.setSelected(inv.isPublic());
            if (inv.inAccount()) {
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
                                            ).addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    ticker, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    symbol, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    places, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    val, 250, 250, Short.MAX_VALUE
                                            )
                                    )
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            merch, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            comod, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            publik, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    symbol, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    places, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    val, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            merch, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            comod, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            publik, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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

    @Override
    protected void saveAction() {
        try {
            LInventory inventory;
            if (comod.isSelected()) {
                inventory = new LInventory(
                        Validation.validateString(name),
                        Validation.validateString(ticker),
                        Validation.validateString(symbol),
                        Validation.validateInteger(places).intValue(),
                        merch.isSelected(),
                        publik.isSelected(),
                        CURRENT_INSTANCE
                );
            } else {
                inventory = new LInventory(
                        Validation.validateString(name),
                        Validation.validateString(ticker),
                        Validation.validateString(symbol),
                        Validation.validateInteger(places).intValue(),
                        Validation.validateDecimal(val),
                        merch.isSelected(),
                        CURRENT_INSTANCE
                );
            }
            if (INDEX >= 0) {
                TABLE.replace(INDEX, inventory);
            } else {
                TABLE.add(inventory);
            }
            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!  " + ex.getMessage());
        }
    }
}
