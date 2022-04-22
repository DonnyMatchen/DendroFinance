package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class AccountEditGui extends BackingEditGui<Account> {
    private JTextField name, aid, budget;
    private SearchBox currency, type;
    private JLabel a, b, c;
    private JButton cancel, save;

    public AccountEditGui(BackingTableGui<Account> caller, BackingTableCore<Account> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        aid = new JTextField();
        budget = new JTextField();

        currency = new SearchBox("Currency", CURRENT_INSTANCE.getAllAssetsAsStrings());
        type = new SearchBox("Account Type", CURRENT_INSTANCE.getAccountTypesAsStrings());

        a = new JLabel("Account Name");
        b = new JLabel("Account ID");
        c = new JLabel("Budget");

        cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        save = DendroFactory.getButton("Save");
        save.addActionListener(event -> {
            if (INDEX >= 0) {
                try {
                    TABLE.replace(INDEX, new Account(
                            Validation.validateString(name),
                            Validation.validateInteger(aid).intValue(),
                            CURRENT_INSTANCE.getLCurrency(currency.getSelectedItem()),
                            CURRENT_INSTANCE.ACCOUNT_TYPES.getElement(type.getSelectedItem()),
                            Validation.validateStringAllowEmpty(budget),
                            CURRENT_INSTANCE,
                            true
                    ));
                    dispose();
                } catch (ValidationFailedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "You did a badness!");
                }
            } else {
                try {
                    TABLE.add(new Account(
                            Validation.validateString(name),
                            Validation.validateInteger(aid).intValue(),
                            CURRENT_INSTANCE.getLCurrency(currency.getSelectedItem()),
                            CURRENT_INSTANCE.ACCOUNT_TYPES.getElement(type.getSelectedItem()),
                            Validation.validateStringAllowEmpty(budget),
                            CURRENT_INSTANCE,
                            true
                    ));
                    dispose();
                } catch (ValidationFailedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "You did a badness!");
                }
            }
        });

        //populate if in edit mode
        if (INDEX >= 0) {
            Account acc = TABLE.getElement(INDEX);
            name.setText(acc.getName());
            aid.setText("" + acc.getAid());
            budget.setText(acc.getBudgetType());
            currency.setSelectedIndex("" + acc.getCurrency());
            type.setSelectedIndex(acc.getAccountType().NAME);
            if (acc.inUse()) {
                name.setEnabled(false);
                name.setBackground(DendroFactory.BACKDROP);
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
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    aid, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    budget, 250, 250, Short.MAX_VALUE
                                            )
                                    )
                            ).addComponent(
                                    currency, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addComponent(
                                    type, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                            main.createSequentialGroup().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            name, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            aid, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            budget, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            currency, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            type, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
