package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class ExchangeEditGui extends BackingEditGui<Exchange> {
    private JTextField name, alt;
    private JScrollPane supports, stakes, fees;
    private JTextArea sText, tText, fText;
    private JLabel a, b, c, d, e;
    private JButton cancel, save;

    public ExchangeEditGui(BackingTableGui<Exchange> caller, BackingTableCore<Exchange> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        alt = new JTextField();
        supports = DendroFactory.getScrollField();
        sText = (JTextArea) supports.getViewport().getView();
        stakes = DendroFactory.getScrollField();
        tText = (JTextArea) stakes.getViewport().getView();
        fees = DendroFactory.getScrollField();
        fText = (JTextArea) fees.getViewport().getView();

        a = new JLabel("Exchange Name");
        b = new JLabel("API Name");
        c = new JLabel("Supported Assets");
        d = new JLabel("Separate Staking");
        e = new JLabel("Separate Fees");

        cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        save = DendroFactory.getButton("Save");
        save.addActionListener(event -> saveAction());

        //populate if in edit mode
        if (INDEX >= 0) {
            Exchange exch = TABLE.getElement(INDEX);
            name.setText(exch.NAME);
            alt.setText(exch.ALT);
            StringBuilder sb = new StringBuilder();
            for (String s : exch.SUPPORTED) {
                sb.append(", ").append(s);
            }
            sText.setText(sb.substring(2));
            if (!exch.STAKING.isEmpty()) {
                sb = new StringBuilder();
                for (JsonObject obj : exch.STAKING) {
                    sb.append(", ").append(obj.getString("cur")).append("_").append(obj.getDecimal("places").decimal.intValue());
                }
                tText.setText(sb.substring(2));
            }
            if (exch.hasFees()) {
                sb = new StringBuilder();
                for (String s : exch.FEE) {
                    sb.append(", ").append(s);
                }
                fText.setText(sb.substring(2));
            }
            if (exch.inUse(CURRENT_INSTANCE)) {
                name.setEditable(false);
                name.setBackground(DendroFactory.DISABLED);
            }
        }

        //Group Layout
        {
            GroupLayout main = new GroupLayout(getContentPane());
            getContentPane().setLayout(main);
            main.setHorizontalGroup(
                    main.createSequentialGroup().addContainerGap().addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                                    main.createSequentialGroup().addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    alt, 250, 250, Short.MAX_VALUE
                                            )
                                    )
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            supports, 250, 250, Short.MAX_VALUE
                                    ).addComponent(
                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            stakes, 250, 250, Short.MAX_VALUE
                                    ).addComponent(
                                            e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            fees, 250, 250, Short.MAX_VALUE
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
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    supports, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    stakes, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    fees, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
        sText.setBackground(DendroFactory.CONTENT);
        tText.setBackground(DendroFactory.CONTENT);
        try {
            ArrayList<String> needed = TABLE.getElement(INDEX).aNamesInUse(CURRENT_INSTANCE);
            for (String s : Validation.validateStringAllowEmpty(sText).replace(" ", "").replace("\n", "").split(",")) {
                String str = "";
                if (!sText.getText().equals("")) {
                    for (String n : needed) {
                        if (n.contains(s.split("!")[1])) {
                            str = n;
                            break;
                        }
                    }
                }
                if (!str.equals("")) {
                    needed.remove(str);
                }
            }
            if (!needed.isEmpty()) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You have removed exchange accounts that are in use:\n" + Arrays.toString(needed.toArray()));
                sText.setBackground(DendroFactory.WRONG);
                throw new ValidationFailedException("invalid removal");
            }
            Exchange exchange;
            if (tText.getText().equals("")) {
                exchange = new Exchange(
                        Validation.validateString(name),
                        Validation.validateStringAllowEmpty(alt),
                        new ArrayList<>(Arrays.asList(
                                Validation.validateString(sText).replace(" ", "").replace("\n", "").split(",")
                        )),
                        CURRENT_INSTANCE,
                        true
                );
            } else {
                ArrayList<JsonObject> objs = new ArrayList<>();
                for (String s : Validation.validateStringAllowEmpty(tText).replace(" ", "").replace("\n", "").split(",")) {
                    try {
                        String[] parts = s.split("_");
                        JsonObject obj = new JsonObject();
                        obj.put("cur", new JsonString(parts[0]));
                        obj.put("places", new JsonDecimal(new BigDecimal(parts[1])));
                        objs.add(obj);
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException | JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Staking List");
                        tText.setBackground(DendroFactory.WRONG);
                        throw new ValidationFailedException("This field is not a valid staking list");
                    }
                }
                exchange = new Exchange(
                        Validation.validateString(name),
                        Validation.validateStringAllowEmpty(alt),
                        new ArrayList<>(Arrays.asList(
                                Validation.validateString(sText).replace(" ", "").replace("\n", "").split(",")
                        )),
                        objs,
                        CURRENT_INSTANCE,
                        true
                );
            }
            if (!fText.getText().equals("")) {
                exchange.FEE.clear();
                exchange.FEE.addAll(Arrays.asList(Validation.validateString(fText).replace(" ", "").replace("\n", "").split(",")));
            }
            if (INDEX >= 0) {
                TABLE.replace(INDEX, exchange);
            } else {
                TABLE.add(exchange);
            }
            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!  " + ex.getMessage());
        }
    }
}
