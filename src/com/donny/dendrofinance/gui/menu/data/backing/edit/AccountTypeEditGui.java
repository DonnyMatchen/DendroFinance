package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class AccountTypeEditGui extends BackingEditGui<AccountType> {
    private JTextField name;
    private JComboBox<String> type;

    public AccountTypeEditGui(BackingTableGui<AccountType> caller, BackingTableCore<AccountType> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        type = new JComboBox<>();
        for (BroadAccountType t : BroadAccountType.values()) {
            type.addItem(t.toString());
        }

        JLabel a = new JLabel("Name");
        JLabel b = new JLabel("Type");

        JButton cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JButton save = DendroFactory.getButton("Save");
        save.addActionListener(event -> saveAction());

        //populate if in edit mode
        if (INDEX >= 0) {
            AccountType at = TABLE.getElement(INDEX);
            name.setText(at.NAME);
            type.setSelectedItem(at.TYPE.toString());
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
                                                    type, 250, 250, Short.MAX_VALUE
                                            )
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
                                    type, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
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
            AccountType temp = new AccountType(
                    Validation.validateString(name),
                    (String) type.getSelectedItem()
            );
            if (INDEX >= 0) {
                TABLE.replace(INDEX, temp);
            } else {
                TABLE.add(temp);
            }
            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!  " + ex.getMessage());
        }
    }
}
