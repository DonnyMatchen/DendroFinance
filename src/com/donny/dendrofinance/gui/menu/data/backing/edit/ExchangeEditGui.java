package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class ExchangeEditGui extends BackingEditGui<Exchange> {
    private JTextField name, alt;
    private JTextArea sText, tText, fText;

    public ExchangeEditGui(BackingTableGui<Exchange> caller, BackingTableCore<Exchange> core, int index, ProgramInstance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        name = new JTextField();
        alt = new JTextField();
        JScrollPane supports = DendroFactory.getScrollField();
        sText = (JTextArea) supports.getViewport().getView();
        JScrollPane stakes = DendroFactory.getScrollField();
        tText = (JTextArea) stakes.getViewport().getView();
        JScrollPane fees = DendroFactory.getScrollField();
        fText = (JTextArea) fees.getViewport().getView();

        JLabel a = new JLabel("Exchange Name");
        JLabel b = new JLabel("API Name");
        JLabel c = new JLabel("Supported Assets");
        JLabel d = new JLabel("Separate Staking");
        JLabel e = new JLabel("Separate Fees");

        JButton cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JButton save = DendroFactory.getButton("Save");
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
            Exchange exchange;
            if (tText.getText().isEmpty()) {
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
            if (!fText.getText().isEmpty()) {
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
