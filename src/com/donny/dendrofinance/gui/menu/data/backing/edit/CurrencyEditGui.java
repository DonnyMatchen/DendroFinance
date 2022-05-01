package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public class CurrencyEditGui extends BackingEditGui<LCurrency> {
    private JTextField name, alt, ticker, symbol, places;
    private JPanel flags;
    private JCheckBox fiat, extinct, token, forwards;
    private JLabel a, b, c, d, e, f;
    private JButton save, cancel;

    public CurrencyEditGui(BackingTableGui<LCurrency> caller, BackingTableCore<LCurrency> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        alt = new JTextField();
        ticker = new JTextField();
        symbol = new JTextField();
        places = new JTextField();
        flags = new JPanel();
        fiat = new JCheckBox("Fiat");
        extinct = new JCheckBox("Extinct");
        token = new JCheckBox("Token");
        forwards = new JCheckBox("Forwards");

        a = new JLabel("Currency Name");
        b = new JLabel("Alternate Name");
        c = new JLabel("Currency Ticker");
        d = new JLabel("Currency Symbol");
        e = new JLabel("Number of places");
        f = new JLabel("Flags");

        cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        save = DendroFactory.getButton("Save");
        save.addActionListener(event -> {
            if (INDEX >= 0) {
                try {
                    TABLE.replace(INDEX, new LCurrency(
                            Validation.validateString(name),
                            Validation.validateString(ticker),
                            fiat.isSelected(),
                            Validation.validateString(symbol),
                            forwards.isSelected(),
                            Validation.validateInteger(places).intValue(),
                            Validation.validateString(alt),
                            token.isSelected(),
                            extinct.isSelected(),
                            CURRENT_INSTANCE
                    ));
                    dispose();
                } catch (ValidationFailedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!");
                }
            } else {
                try {
                    TABLE.add(new LCurrency(
                            Validation.validateString(name),
                            Validation.validateString(ticker),
                            fiat.isSelected(),
                            Validation.validateString(symbol),
                            forwards.isSelected(),
                            Validation.validateInteger(places).intValue(),
                            Validation.validateString(alt),
                            token.isSelected(),
                            extinct.isSelected(),
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
            LCurrency cur = TABLE.getElement(INDEX);
            name.setText(cur.getName());
            alt.setText(cur.getAltName());
            ticker.setText(cur.getTicker());
            symbol.setText(cur.getSymbol());
            places.setText("" + cur.getPlaces());
            fiat.setSelected(cur.isFiat());
            extinct.setSelected(cur.isDead());
            token.setSelected(cur.isToken());
            forwards.setSelected(cur.isForwards());
            if (cur.inAccount()) {
                ticker.setEditable(false);
                ticker.setBackground(DendroFactory.BACKDROP);
            }
        }

        //Group Layout
        {
            GroupLayout flag = new GroupLayout(flags);
            flags.setLayout(flag);
            flag.setHorizontalGroup(
                    flag.createSequentialGroup().addContainerGap().addGroup(
                            flag.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    fiat, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    token, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            flag.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    extinct, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    forwards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    )
            );
            flag.setVerticalGroup(
                    flag.createSequentialGroup().addContainerGap().addGroup(
                            flag.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    fiat, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    extinct, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            flag.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    token, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    forwards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    )
            );

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
                                            ).addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    alt, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    ticker, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    symbol, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    places, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                            alt, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ticker, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            symbol, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            places, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
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
}
