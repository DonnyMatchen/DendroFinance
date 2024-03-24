package com.donny.dendrofinance.gui.menu.transactions;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.gui.customswing.BTCSearchBox;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramModalFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.customswing.SearchBox;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

public class SpecialTransactionEntryGui extends ProgramModalFrame {
    private final ProgramMainGui MAIN;
    private final JPanel FORM;
    private final ButtonGroup GROUP;
    private final JLabel A, B, C, D, E, F;
    private final JTextField DATE, COST, F_AMOUNT, T_AMOUNT, FEE_AMOUNT, UNIT, FEE_UNIT, AUTOMATIC;
    private final JScrollPane DESCRIPTION;
    private final JTextArea DESCRIPTION_TEXT;
    private final BTCSearchBox<Exchange> F_EXCHANGE, T_EXCHANGE;
    private final BTCSearchBox<LCurrency> F_CURRENCY, T_CURRENCY, FEE_CURRENCY;
    private final BTCSearchBox<Account> ACCOUNTS;
    private final JButton INSERT;

    public SpecialTransactionEntryGui(ProgramMainGui caller, ProgramInstance curInst) {
        super(caller, "New Special Transaction Entry", curInst);
        MAIN = caller;
        //draw GUI
        {
            JPanel radioPanel = new JPanel();
            FORM = new JPanel();
            JPanel buttonsPanel = new JPanel();
            GROUP = new ButtonGroup();
            JRadioButton psButton = new JRadioButton("Purchase/Sale");
            psButton.setSelected(true);
            GROUP.add(psButton);
            JRadioButton pfButton = new JRadioButton("Purchase/Sale With Fee");
            GROUP.add(pfButton);
            JRadioButton ipButton = new JRadioButton("Income/Payment");
            GROUP.add(ipButton);
            JRadioButton mnButton = new JRadioButton("Mining");
            GROUP.add(mnButton);
            JRadioButton ctButton = new JRadioButton("Coin Transfer");
            GROUP.add(ctButton);
            JRadioButton ttButton = new JRadioButton("Token Transfer");
            GROUP.add(ttButton);
            JRadioButton tdButton = new JRadioButton("Trade");
            GROUP.add(tdButton);
            Enumeration<AbstractButton> radio = GROUP.getElements();
            while (radio.hasMoreElements()) {
                radio.nextElement().addActionListener(event -> updateForm());
            }
            JButton cancel = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());
            INSERT = DendroFactory.getButton("Insert");

            //initialize defaults for components
            A = new JLabel("");
            B = new JLabel("");
            C = new JLabel("");
            D = new JLabel("");
            E = new JLabel("");
            F = new JLabel("");
            DATE = new JTextField();
            DESCRIPTION = DendroFactory.getLongField();
            DESCRIPTION_TEXT = (JTextArea) DESCRIPTION.getViewport().getView();
            COST = new JTextField();
            F_AMOUNT = new JTextField();
            T_AMOUNT = new JTextField();
            FEE_AMOUNT = new JTextField();
            UNIT = new JTextField();
            FEE_UNIT = new JTextField();
            AUTOMATIC = new JTextField();
            AUTOMATIC.setBackground(DendroFactory.DISABLED);
            AUTOMATIC.setEditable(false);
            F_EXCHANGE = new BTCSearchBox<>("", new ArrayList<>(), CURRENT_INSTANCE);
            T_EXCHANGE = new BTCSearchBox<>("", new ArrayList<>(), CURRENT_INSTANCE);
            F_CURRENCY = new BTCSearchBox<>("", new ArrayList<>(), CURRENT_INSTANCE);
            T_CURRENCY = new BTCSearchBox<>("", new ArrayList<>(), CURRENT_INSTANCE);
            FEE_CURRENCY = new BTCSearchBox<>("", new ArrayList<>(), CURRENT_INSTANCE);
            ACCOUNTS = new BTCSearchBox<>("", new ArrayList<>(), CURRENT_INSTANCE);

            //group layout
            {
                //radio group layout
                {
                    GroupLayout rad = new GroupLayout(radioPanel);
                    radioPanel.setLayout(rad);
                    rad.setHorizontalGroup(
                            rad.createSequentialGroup().addContainerGap().addGroup(
                                    rad.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            rad.createSequentialGroup().addComponent(
                                                    psButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    pfButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    ipButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
                                            rad.createSequentialGroup().addComponent(
                                                    mnButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    ctButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    ttButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    tdButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    rad.setVerticalGroup(
                            rad.createSequentialGroup().addContainerGap().addGroup(
                                    rad.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            psButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            pfButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ipButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    rad.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            mnButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ctButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ttButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            tdButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                //button group layout
                {
                    GroupLayout button = new GroupLayout(buttonsPanel);
                    buttonsPanel.setLayout(button);
                    button.setHorizontalGroup(
                            button.createSequentialGroup().addContainerGap().addComponent(
                                    cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                            ).addComponent(
                                    INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addContainerGap()
                    );
                    button.setVerticalGroup(
                            button.createSequentialGroup().addContainerGap().addGroup(
                                    button.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        radioPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        FORM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                radioPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                FORM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
        updateForm();
    }

    private void updateForm() {
        int x = getLocation().x + getWidth() / 2, y = getLocation().y + getHeight() / 2;
        FORM.removeAll();
        for (ActionListener listener : INSERT.getActionListeners()) {
            INSERT.removeActionListener(listener);
        }
        SearchBox[] searches = new SearchBox[]{
                F_EXCHANGE, T_EXCHANGE, F_CURRENCY, T_CURRENCY, FEE_CURRENCY, ACCOUNTS
        };
        for (SearchBox search : searches) {
            for (ListSelectionListener listener : search.getListSelectionListeners()) {
                search.removeListSelectionListener(listener);
            }
        }
        switch (getSelectedRadio()) {
            case "Purchase/Sale" -> {
                A.setText("Date");
                B.setText("Main Amount");
                C.setText("Asset Amount");
                F_EXCHANGE.setTitle("Exchange");
                F_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                F_EXCHANGE.addListSelectionListener(event -> updateCur(F_CURRENCY, F_EXCHANGE));
                F_CURRENCY.setTitle("Currency");
                F_CURRENCY.setMaster(new ArrayList<>());
                INSERT.addActionListener(event -> psInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            case "Purchase/Sale With Fee" -> {
                A.setText("Date");
                B.setText("Main Amount");
                C.setText("Asset Amount");
                D.setText("Fee Amount");
                E.setText("Fee Unit");
                F_EXCHANGE.setTitle("Exchange");
                F_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                F_EXCHANGE.addListSelectionListener(event -> {
                    updateCur(F_CURRENCY, F_EXCHANGE);
                    updateFeeCur(FEE_CURRENCY, F_EXCHANGE);
                });
                F_CURRENCY.setTitle("Transaction Currency");
                F_CURRENCY.setMaster(new ArrayList<>());
                FEE_CURRENCY.setTitle("Fee Currency");
                FEE_CURRENCY.setMaster(new ArrayList<>());
                INSERT.addActionListener(event -> pfInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            FEE_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            FEE_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            FEE_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            FEE_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            FEE_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    FEE_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            case "Income/Payment" -> {
                A.setText("Date");
                B.setText("Description");
                C.setText("Asset Amount");
                D.setText("Unit Price");
                F_EXCHANGE.setTitle("Exchange");
                F_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                F_EXCHANGE.addListSelectionListener(event -> updateCur(F_CURRENCY, F_EXCHANGE));
                F_CURRENCY.setTitle("Currency");
                F_CURRENCY.setMaster(new ArrayList<>());
                INSERT.addActionListener(event -> ipInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            DESCRIPTION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DESCRIPTION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            case "Mining" -> {
                A.setText("Date");
                B.setText("Description");
                C.setText("Asset Amount");
                D.setText("Unit Price");
                E.setText("Currency");
                ACCOUNTS.setTitle("Account");
                ArrayList<Account> master = new ArrayList<>();
                CURRENT_INSTANCE.ACCOUNTS.forEach(acc -> {
                    if (acc.getBroadAccountType() == BroadAccountType.TRACKING && acc.EXPORT) {
                        master.add(acc);
                    }
                });
                ACCOUNTS.setMaster(master);
                ACCOUNTS.addListSelectionListener(event -> AUTOMATIC.setText(ACCOUNTS.getSelectedItem().getCurrency().toString()));
                INSERT.addActionListener(event -> mnInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            DESCRIPTION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            AUTOMATIC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            ACCOUNTS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DESCRIPTION, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            AUTOMATIC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    ACCOUNTS, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            case "Coin Transfer" -> {
                A.setText("Date");
                B.setText("From Amount");
                C.setText("To Amount");
                D.setText("Unit Price");
                F_EXCHANGE.setTitle("From Exchange");
                F_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                T_EXCHANGE.setTitle("To Exchange");
                T_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                F_EXCHANGE.addListSelectionListener(event -> update2ECur(F_CURRENCY, F_EXCHANGE, T_EXCHANGE));
                T_EXCHANGE.addListSelectionListener(event -> update2ECur(F_CURRENCY, F_EXCHANGE, T_EXCHANGE));
                F_CURRENCY.setTitle("Currency");
                F_CURRENCY.setMaster(new ArrayList<>());
                INSERT.addActionListener(event -> ctInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            T_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    T_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            case "Token Transfer" -> {
                A.setText("Date");
                B.setText("From Amount");
                C.setText("To Amount");
                D.setText("Fee Amount");
                E.setText("Transfer Unit");
                F.setText("Fee Unit");
                F_EXCHANGE.setTitle("From Exchange");
                F_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                T_EXCHANGE.setTitle("To Exchange");
                T_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                F_EXCHANGE.addListSelectionListener(event -> {
                    update2ECur(F_CURRENCY, F_EXCHANGE, T_EXCHANGE);
                    updateToken(FEE_CURRENCY, F_EXCHANGE, T_EXCHANGE);
                });
                T_EXCHANGE.addListSelectionListener(event -> {
                    update2ECur(F_CURRENCY, F_EXCHANGE, T_EXCHANGE);
                    updateToken(FEE_CURRENCY, F_EXCHANGE, T_EXCHANGE);
                });
                F_CURRENCY.setTitle("Transfer Currency");
                F_CURRENCY.setMaster(new ArrayList<>());
                FEE_CURRENCY.setTitle("Fee Currency");
                FEE_CURRENCY.setMaster(new ArrayList<>());
                INSERT.addActionListener(event -> ttInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            FEE_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            FEE_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            T_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            FEE_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            FEE_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            FEE_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    T_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
            case "Trade" -> {
                A.setText("Date");
                B.setText("From Amount");
                C.setText("To Amount");
                D.setText("From Unit");
                F_EXCHANGE.setTitle("Exchange");
                F_EXCHANGE.setMaster(CURRENT_INSTANCE.EXCHANGES);
                F_EXCHANGE.addListSelectionListener(event -> {
                    updateCur(F_CURRENCY, F_EXCHANGE);
                    updateCur(T_CURRENCY, F_EXCHANGE);
                });
                F_CURRENCY.setTitle("From Currency");
                F_CURRENCY.setMaster(new ArrayList<>());
                T_CURRENCY.setTitle("To Currency");
                T_CURRENCY.setMaster(new ArrayList<>());
                INSERT.addActionListener(event -> tdInsert());
                //group layout
                {
                    GroupLayout main = new GroupLayout(FORM);
                    FORM.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            T_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_EXCHANGE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    F_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    T_CURRENCY, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addContainerGap()
                    );
                }
            }
        }
        pack();
        setLocation(x - getWidth() / 2, y - getHeight() / 2);
    }

    private String getSelectedRadio() {
        Enumeration<AbstractButton> radio = GROUP.getElements();
        while (radio.hasMoreElements()) {
            AbstractButton button = radio.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return "";
    }

    private void updateCur(SearchBox<LCurrency> cur, SearchBox<Exchange> exchange) {
        cur.setMaster(CURRENT_INSTANCE.getAllAssetsByExchange(exchange));
    }

    private void updateFeeCur(SearchBox<LCurrency> fee, SearchBox<Exchange> exchange) {
        fee.setMaster(CURRENT_INSTANCE.getAllFeesByExchange(exchange));
    }

    private void updateToken(SearchBox<LCurrency> cur, SearchBox<Exchange> exchange1, SearchBox<Exchange> exchange2) {
        cur.setMaster(CURRENT_INSTANCE.getAllTokensByDoubleExchange(exchange1, exchange2));
    }

    private void update2ECur(SearchBox<LCurrency> cur, SearchBox<Exchange> exchange1, SearchBox<Exchange> exchange2) {
        cur.setMaster(CURRENT_INSTANCE.getAllAssetsByDoubleExchange(exchange1, exchange2));
    }

    private void psInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.purchaseSale(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(COST),
                    F_EXCHANGE.getSelectedItem(),
                    F_CURRENCY.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                F_CURRENCY.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void pfInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.purchaseSaleFee(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(COST),
                    Validation.validateDecimal(FEE_AMOUNT),
                    Validation.validateDecimal(FEE_UNIT),
                    F_EXCHANGE.getSelectedItem(),
                    F_CURRENCY.getSelectedItem(),
                    FEE_CURRENCY.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                F_CURRENCY.setListBackground(DendroFactory.WRONG);
                FEE_CURRENCY.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void ipInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.incomePayment(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateString(DESCRIPTION_TEXT),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(UNIT),
                    F_EXCHANGE.getSelectedItem(),
                    F_CURRENCY.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                F_CURRENCY.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void mnInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.miningIncome(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateString(DESCRIPTION_TEXT),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(UNIT),
                    ACCOUNTS.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                ACCOUNTS.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness." + ex.getMessage());
        }
    }

    private void ctInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.coinTransfer(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(T_AMOUNT),
                    Validation.validateDecimal(UNIT),
                    F_EXCHANGE.getSelectedItem(),
                    T_EXCHANGE.getSelectedItem(),
                    F_CURRENCY.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                T_EXCHANGE.setListBackground(DendroFactory.WRONG);
                F_CURRENCY.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void ttInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.tokenTransfer(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(T_AMOUNT),
                    Validation.validateDecimal(FEE_AMOUNT),
                    Validation.validateDecimal(UNIT),
                    Validation.validateDecimal(FEE_UNIT),
                    F_EXCHANGE.getSelectedItem(),
                    T_EXCHANGE.getSelectedItem(),
                    F_CURRENCY.getSelectedItem(),
                    FEE_CURRENCY.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                T_EXCHANGE.setListBackground(DendroFactory.WRONG);
                F_CURRENCY.setListBackground(DendroFactory.WRONG);
                FEE_CURRENCY.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void tdInsert() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.trade(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateDecimal(F_AMOUNT),
                    Validation.validateDecimal(T_AMOUNT),
                    Validation.validateDecimal(UNIT),
                    F_EXCHANGE.getSelectedItem(),
                    F_CURRENCY.getSelectedItem(),
                    T_CURRENCY.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                F_CURRENCY.setListBackground(DendroFactory.WRONG);
                T_CURRENCY.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }
}
