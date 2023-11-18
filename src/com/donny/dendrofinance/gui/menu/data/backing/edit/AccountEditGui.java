package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.BTCSearchBox;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;

import javax.swing.*;

public class AccountEditGui extends BackingEditGui<Account> {
    private JTextField name, aid, budget;
    private BTCSearchBox<LCurrency> currency;
    private BTCSearchBox<AccountType> type;
    private BTCSearchBox<Exchange> exchange;

    public AccountEditGui(BackingTableGui<Account> caller, BackingTableCore<Account> core, int index, ProgramInstance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        aid = new JTextField();
        budget = new JTextField();

        currency = new BTCSearchBox<>("Currency", CURRENT_INSTANCE.getAllAssets(), CURRENT_INSTANCE);
        type = new BTCSearchBox<>("Account Type", CURRENT_INSTANCE.ACCOUNT_TYPES, CURRENT_INSTANCE);
        exchange = new BTCSearchBox<>("Exchange", CURRENT_INSTANCE.EXCHANGES, CURRENT_INSTANCE);

        JLabel a = new JLabel("Account Name");
        JLabel b = new JLabel("Account ID");
        JLabel c = new JLabel("Budget");

        JButton cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JButton save = DendroFactory.getButton("Save");
        save.addActionListener(event -> saveAction());

        //populate if in edit mode
        if (INDEX >= 0) {
            Account acc = TABLE.getElement(INDEX);
            name.setText(acc.getName());
            aid.setText(String.valueOf(acc.getAid()));
            budget.setText(acc.getBudgetType());
            currency.setSelectedIndex(acc.getCurrency());
            type.setSelectedIndex(acc.getAccountType());
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
                            ).addComponent(
                                    exchange, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                    ).addGap(DendroFactory.SMALL_GAP).addComponent(
                            exchange, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
            Account temp = new Account(
                    Validation.validateString(name),
                    Validation.validateInteger(aid).intValue(),
                    currency.getSelectedItem(),
                    type.getSelectedItem(),
                    Validation.validateStringAllowEmpty(budget),
                    exchange.getSelectedItem(),
                    CURRENT_INSTANCE,
                    true
            );
            if (TABLE.getElement(INDEX) != null && TABLE.getElement(INDEX).inSpecial()) {
                Account.changeSpecial(TABLE.getElement(INDEX).getName(), temp.getName());
            }
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
