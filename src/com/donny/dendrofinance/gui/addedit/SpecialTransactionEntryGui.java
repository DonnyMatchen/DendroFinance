package com.donny.dendrofinance.gui.addedit;

import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.util.ArrayList;

public class SpecialTransactionEntryGui extends ModalFrame {
    private final MainGui MAIN;
    private final JTabbedPane BACK;
    private final JPanel BS, BF, IP, MN, CT, TT, TD;
    private final JLabel BS_A, BS_B, BS_C,
            BF_A, BF_B, BF_C, BF_D, BF_E,
            IP_A, IP_B, IP_C, IP_D,
            MN_A, MN_B, MN_C, MN_D, MN_E,
            CT_A, CT_B, CT_C, CT_D,
            TT_A, TT_B, TT_C, TT_D, TT_E, TT_F,
            TD_A, TD_B, TD_C, TD_D;
    private final JTextField BS_DATE, BS_AMOUNT, BS_COST,
            BF_DATE, BF_AMOUNT, BF_COST, BF_FEE, BF_UNIT,
            IP_DATE, IP_DESC, IP_AMouNT, IP_UNIT,
            MN_DATE, MN_DESC, MN_CUR, MN_AMouNT, MN_UNIT,
            CT_DATE, CT_F_AMOUNT, CT_T_AMOUNT, CT_UNIT,
            TT_DATE, TT_F_AMOUNT, TT_T_AMOUNT, TT_FEE, TT_L_UNIT, TT_F_UNIT,
            TD_DATE, TD_F_AMOUNT, TD_T_AMOUNT, TD_UNIT;
    private final SearchBox BS_EXCHANGE, BS_CUR,
            BF_EXCHANGE, BF_CUR, BF_FEE_CUR,
            IP_EXCHANGE, IP_CUR,
            MN_ACCOUNT,
            CT_F_EXCHANGE, CT_T_EXCHANGE, CT_CUR,
            TT_F_EXCHANGE, TT_T_EXCHANGE, TT_L_CUR, TT_F_CUR,
            TD_EXCHANGE, TD_F_CUR, TD_T_CUR;
    private final JButton BS_CANCEL, BS_SAVE,
            BF_CANCEL, BF_SAVE,
            IP_CANCEL, IP_SAVE,
            MN_CANCEL, MN_SAVE,
            CT_CANCEL, CT_SAVE,
            TT_CANCEL, TT_SAVE,
            TD_CANCEL, TD_SAVE;

    public SpecialTransactionEntryGui(MainGui caller, Instance curInst) {
        super(caller, "New Special Transaction Entry", curInst);
        MAIN = caller;
        //draw GUI
        {
            BACK = new JTabbedPane();

            //buy sell
            {
                BS = new JPanel();
                BS.setBorder(null);
                BS_A = new JLabel("Date");
                BS_B = new JLabel("Cost");
                BS_C = new JLabel("Amount");
                BS_DATE = new JTextField();
                BS_AMOUNT = new JTextField();
                BS_COST = new JTextField();
                BS_CUR = new SearchBox("Currency", new ArrayList<>());
                BS_EXCHANGE = new SearchBox("Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                BS_EXCHANGE.addListSelectionListener(event -> updateCur(BS_CUR, BS_EXCHANGE));
                updateCur(BS_CUR, BS_EXCHANGE);

                BS_CANCEL = DendroFactory.getButton("Cancel");
                BS_CANCEL.addActionListener(event -> dispose());
                BS_SAVE = DendroFactory.getButton("Save");
                BS_SAVE.addActionListener(event -> bsSaveAction());

                //buy sell layout
                {
                    GroupLayout main = new GroupLayout(BS);
                    BS.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            BS_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BS_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BS_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            BS_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BS_COST, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BS_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    BS_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    BS_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    BS_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    BS_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BS_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BS_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BS_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BS_COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BS_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BS_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    BS_EXCHANGE, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    BS_CUR, 150, 150, 150
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BS_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BS_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Buy/Sell", BS);

            //buy sell with fee
            {
                BF = new JPanel();
                BF.setBorder(null);
                BF_A = new JLabel("Date");
                BF_B = new JLabel("Cost");
                BF_C = new JLabel("Amount");
                BF_D = new JLabel("Fee Amount");
                BF_E = new JLabel("Fee Unit");
                BF_DATE = new JTextField();
                BF_AMOUNT = new JTextField();
                BF_COST = new JTextField();
                BF_FEE = new JTextField();
                BF_UNIT = new JTextField();
                BF_CUR = new SearchBox("Transaction Currency", new ArrayList<>());
                BF_FEE_CUR = new SearchBox("Fee Currency", new ArrayList<>());
                BF_EXCHANGE = new SearchBox("Exchange", CURRENT_INSTANCE.getFeeExchangesAsStrings());
                BF_EXCHANGE.addListSelectionListener(event -> {
                    updateCur(BF_CUR, BF_EXCHANGE);
                    updateFeeCur(BF_FEE_CUR, BF_EXCHANGE);
                });
                updateCur(BF_CUR, BF_EXCHANGE);
                updateFeeCur(BF_FEE_CUR, BF_EXCHANGE);

                BF_CANCEL = DendroFactory.getButton("Cancel");
                BF_CANCEL.addActionListener(event -> dispose());
                BF_SAVE = DendroFactory.getButton("Save");
                BF_SAVE.addActionListener(event -> bfSaveAction());

                //buy sell layout
                {
                    GroupLayout main = new GroupLayout(BF);
                    BF.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            BF_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BF_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BF_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BF_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BF_E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            BF_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BF_COST, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BF_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BF_FEE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            BF_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    BF_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    BF_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    BF_FEE_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    BF_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    BF_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BF_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BF_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BF_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BF_COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BF_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BF_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BF_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BF_FEE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BF_E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BF_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    BF_EXCHANGE, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    BF_CUR, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    BF_FEE_CUR, 150, 150, 150
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            BF_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BF_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Buy/Sell + Fee", BF);

            //income payment
            {
                IP = new JPanel();
                IP.setBorder(null);
                IP_A = new JLabel("Date");
                IP_B = new JLabel("Desc");
                IP_C = new JLabel("Unit");
                IP_D = new JLabel("Amount");
                IP_DATE = new JTextField();
                IP_DESC = new JTextField();
                IP_AMouNT = new JTextField();
                IP_UNIT = new JTextField();
                IP_CUR = new SearchBox("Currencies", new ArrayList<>());
                IP_EXCHANGE = new SearchBox("Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                IP_EXCHANGE.addListSelectionListener(event -> updateCur(IP_CUR, IP_EXCHANGE));
                updateCur(IP_CUR, IP_EXCHANGE);

                IP_CANCEL = DendroFactory.getButton("Cancel");
                IP_CANCEL.addActionListener(event -> dispose());
                IP_SAVE = DendroFactory.getButton("Save");
                IP_SAVE.addActionListener(event -> ipSaveAction());
                //income pay layout
                {
                    GroupLayout main = new GroupLayout(IP);
                    IP.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            IP_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            IP_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            IP_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            IP_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            IP_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            IP_DESC, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            IP_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            IP_AMouNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    IP_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    IP_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    IP_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    IP_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            IP_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            IP_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            IP_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            IP_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_AMouNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    IP_EXCHANGE, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    IP_CUR, 150, 150, 150
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            IP_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Inc/Pay", IP);

            //Mining Income
            {
                MN = new JPanel();
                MN.setBorder(null);
                MN_A = new JLabel("Date");
                MN_B = new JLabel("Desc");
                MN_C = new JLabel("Unit");
                MN_D = new JLabel("Amount");
                MN_E = new JLabel("Currency");
                MN_DATE = new JTextField();
                MN_DESC = new JTextField();
                MN_AMouNT = new JTextField();
                MN_UNIT = new JTextField();
                MN_CUR = new JTextField();
                MN_CUR.setEditable(false);
                MN_CUR.setBackground(DendroFactory.DISABLED);
                MN_ACCOUNT = new SearchBox("Accounts", new ArrayList<>());
                ArrayList<String> master = new ArrayList<>();
                CURRENT_INSTANCE.ACCOUNTS.forEach(acc -> {
                    if (acc.getBroadAccountType() == BroadAccountType.TRACKING && acc.EXPORT) {
                        master.add(acc.getName());
                    }
                });
                MN_ACCOUNT.setMaster(master);
                MN_ACCOUNT.addListSelectionListener(event -> MN_CUR.setText(CURRENT_INSTANCE.ACCOUNTS.getElement(MN_ACCOUNT.getSelectedItem()).getCurrency().toString()));

                MN_CANCEL = DendroFactory.getButton("Cancel");
                MN_CANCEL.addActionListener(event -> dispose());
                MN_SAVE = DendroFactory.getButton("Save");
                MN_SAVE.addActionListener(event -> mnSaveAction());
                //mining layout
                {
                    GroupLayout main = new GroupLayout(MN);
                    MN.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            MN_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            MN_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            MN_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            MN_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            MN_E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            MN_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            MN_DESC, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            MN_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            MN_AMouNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            MN_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addComponent(
                                            MN_ACCOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    MN_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    MN_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            MN_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            MN_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            MN_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            MN_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            MN_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            MN_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            MN_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            MN_AMouNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            MN_E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            MN_CUR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    MN_ACCOUNT, GroupLayout.PREFERRED_SIZE, 150, Short.MAX_VALUE
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            MN_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            MN_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Mining", MN);

            //coin transfer
            {
                CT = new JPanel();
                CT.setBorder(null);
                CT_A = new JLabel("Date");
                CT_B = new JLabel("From Amount");
                CT_C = new JLabel("To Amount");
                CT_D = new JLabel("Unit Price");
                CT_DATE = new JTextField();
                CT_F_AMOUNT = new JTextField();
                CT_T_AMOUNT = new JTextField();
                CT_UNIT = new JTextField();
                CT_CUR = new SearchBox("Currency", new ArrayList<>());
                CT_F_EXCHANGE = new SearchBox("From Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                CT_T_EXCHANGE = new SearchBox("To Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                CT_F_EXCHANGE.addListSelectionListener(event -> update2ECur(CT_CUR, CT_F_EXCHANGE, CT_T_EXCHANGE));
                CT_T_EXCHANGE.addListSelectionListener(event -> update2ECur(CT_CUR, CT_F_EXCHANGE, CT_T_EXCHANGE));
                update2ECur(CT_CUR, CT_F_EXCHANGE, CT_T_EXCHANGE);

                CT_CANCEL = DendroFactory.getButton("Cancel");
                CT_CANCEL.addActionListener(event -> dispose());
                CT_SAVE = DendroFactory.getButton("Save");
                CT_SAVE.addActionListener(event -> ctSaveAction());
                //coin transfer layout
                {
                    GroupLayout main = new GroupLayout(CT);
                    CT.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            CT_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            CT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            CT_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            CT_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            CT_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            CT_F_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            CT_T_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            CT_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    CT_F_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    CT_T_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    CT_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    CT_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    CT_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            CT_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            CT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            CT_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            CT_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    CT_F_EXCHANGE, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    CT_T_EXCHANGE, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    CT_CUR, 150, 150, 150
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            CT_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Coin Transfer", CT);

            //token transfer
            {
                TT = new JPanel();
                TT.setBorder(null);
                TT_A = new JLabel("Date");
                TT_B = new JLabel("From Amount");
                TT_C = new JLabel("To Amount");
                TT_D = new JLabel("Fee Amount");
                TT_E = new JLabel("Transfer Unit");
                TT_F = new JLabel("Fee Unit");
                TT_DATE = new JTextField();
                TT_F_AMOUNT = new JTextField();
                TT_T_AMOUNT = new JTextField();
                TT_FEE = new JTextField();
                TT_F_UNIT = new JTextField();
                TT_L_UNIT = new JTextField();
                TT_L_CUR = new SearchBox("Transfer Currency", new ArrayList<>());
                TT_F_CUR = new SearchBox("Fee Currency", new ArrayList<>());
                TT_F_EXCHANGE = new SearchBox("From Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                TT_T_EXCHANGE = new SearchBox("To Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                TT_F_EXCHANGE.addListSelectionListener(event -> updateTT());
                TT_T_EXCHANGE.addListSelectionListener(event -> updateTT());
                updateTT();

                TT_CANCEL = DendroFactory.getButton("Cancel");
                TT_CANCEL.addActionListener(event -> dispose());
                TT_SAVE = DendroFactory.getButton("Save");
                TT_SAVE.addActionListener(event -> ttSaveAction());
                //token transfer layout
                {
                    GroupLayout main = new GroupLayout(TT);
                    TT.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            TT_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TT_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TT_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TT_E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TT_F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            TT_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TT_F_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TT_T_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TT_FEE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TT_L_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TT_F_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    TT_F_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    TT_T_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    TT_L_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    TT_F_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    TT_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    TT_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_FEE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_L_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_F_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TT_F_EXCHANGE, 125, 125, 125
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TT_T_EXCHANGE, 125, 125, 125
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TT_L_CUR, 125, 125, 125
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TT_F_CUR, 125, 125, 125
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TT_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Token Transfer", TT);

            //trade
            {
                TD = new JPanel();
                TD.setBorder(null);
                TD_A = new JLabel("Date");
                TD_B = new JLabel("From Amount");
                TD_C = new JLabel("To Amount");
                TD_D = new JLabel("From Unit");
                TD_DATE = new JTextField();
                TD_F_AMOUNT = new JTextField();
                TD_T_AMOUNT = new JTextField();
                TD_UNIT = new JTextField();
                TD_F_CUR = new SearchBox("From Currency", new ArrayList<>());
                TD_T_CUR = new SearchBox("To Currency", new ArrayList<>());
                TD_EXCHANGE = new SearchBox("Exchange", CURRENT_INSTANCE.getExchangesAsStrings());
                TD_EXCHANGE.addListSelectionListener(event -> updateTD());
                updateTD();

                TD_CANCEL = DendroFactory.getButton("Cancel");
                TD_CANCEL.addActionListener(event -> dispose());
                TD_SAVE = DendroFactory.getButton("Save");
                TD_SAVE.addActionListener(event -> tdSaveAction());
                //trade layout
                {
                    GroupLayout main = new GroupLayout(TD);
                    TD.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            TD_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TD_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TD_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            TD_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            TD_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TD_F_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TD_T_AMOUNT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            TD_UNIT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    TD_EXCHANGE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    TD_F_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    TD_T_CUR, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    TD_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    TD_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TD_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TD_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TD_C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TD_D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TD_EXCHANGE, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TD_F_CUR, 150, 150, 150
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    TD_T_CUR, 150, 150, 150
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            TD_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Trade", TD);

            add(BACK);

            pack();
        }
    }

    private void updateTT() {
        update2ECur(TT_L_CUR, TT_F_EXCHANGE, TT_T_EXCHANGE);
        updateToken(TT_F_CUR, TT_F_EXCHANGE, TT_T_EXCHANGE);
    }

    private void updateTD() {
        updateCur(TD_F_CUR, TD_EXCHANGE);
        updateCur(TD_T_CUR, TD_EXCHANGE);
    }

    private void updateCur(SearchBox cur, SearchBox exchange) {
        cur.setMaster(CURRENT_INSTANCE.getAllAssetsAsStrings(exchange));
    }

    private void updateFeeCur(SearchBox cur, SearchBox exchange) {
        cur.setMaster(CURRENT_INSTANCE.getAllFeesAsStrings(exchange));
    }

    private void updateToken(SearchBox cur, SearchBox exchange1, SearchBox exchange2) {
        cur.setMaster(CURRENT_INSTANCE.getAllTokensAsStrings(exchange1, exchange2));
    }

    private void update2ECur(SearchBox cur, SearchBox exchange1, SearchBox exchange2) {
        cur.setMaster(CURRENT_INSTANCE.getAllAssetsAsStrings(exchange1, exchange2));
    }

    private void bsSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.buySell(
                    Validation.validateDate(BS_DATE, null, CURRENT_INSTANCE),
                    Validation.validateDecimal(BS_AMOUNT),
                    Validation.validateDecimal(BS_COST),
                    BS_EXCHANGE.getSelectedItem(),
                    BS_CUR.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                BS_EXCHANGE.setListBackground(DendroFactory.WRONG);
                BS_CUR.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void bfSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.buySellFee(
                    Validation.validateDate(BF_DATE, null, CURRENT_INSTANCE),
                    Validation.validateDecimal(BF_AMOUNT),
                    Validation.validateDecimal(BF_COST),
                    Validation.validateDecimal(BF_FEE),
                    Validation.validateDecimal(BF_UNIT),
                    BF_EXCHANGE.getSelectedItem(),
                    BF_CUR.getSelectedItem(),
                    BF_FEE_CUR.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                BF_EXCHANGE.setListBackground(DendroFactory.WRONG);
                BF_CUR.setListBackground(DendroFactory.WRONG);
                BF_FEE_CUR.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void ipSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.incPay(
                    Validation.validateDate(IP_DATE, null, CURRENT_INSTANCE),
                    Validation.validateString(IP_DESC),
                    Validation.validateDecimal(IP_AMouNT),
                    Validation.validateDecimal(IP_UNIT),
                    IP_EXCHANGE.getSelectedItem(),
                    IP_CUR.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                IP_EXCHANGE.setListBackground(DendroFactory.WRONG);
                IP_CUR.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void mnSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.minInc(
                    Validation.validateDate(MN_DATE, null, CURRENT_INSTANCE),
                    Validation.validateString(MN_DESC),
                    Validation.validateDecimal(MN_AMouNT),
                    Validation.validateDecimal(MN_UNIT),
                    MN_ACCOUNT.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                MN_ACCOUNT.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness." + ex.getMessage());
        }
    }

    private void ctSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.coinTransfer(
                    Validation.validateDate(CT_DATE, null, CURRENT_INSTANCE),
                    Validation.validateDecimal(CT_F_AMOUNT),
                    Validation.validateDecimal(CT_T_AMOUNT),
                    Validation.validateDecimal(CT_UNIT),
                    CT_F_EXCHANGE.getSelectedItem(),
                    CT_T_EXCHANGE.getSelectedItem(),
                    CT_CUR.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                CT_F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                CT_T_EXCHANGE.setListBackground(DendroFactory.WRONG);
                CT_CUR.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void ttSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.tokenTransfer(
                    Validation.validateDate(TT_DATE, null, CURRENT_INSTANCE),
                    Validation.validateDecimal(TT_F_AMOUNT),
                    Validation.validateDecimal(TT_T_AMOUNT),
                    Validation.validateDecimal(TT_FEE),
                    Validation.validateDecimal(TT_L_UNIT),
                    Validation.validateDecimal(TT_F_UNIT),
                    TT_F_EXCHANGE.getSelectedItem(),
                    TT_T_EXCHANGE.getSelectedItem(),
                    TT_L_CUR.getSelectedItem(),
                    TT_F_CUR.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                TT_F_EXCHANGE.setListBackground(DendroFactory.WRONG);
                TT_T_EXCHANGE.setListBackground(DendroFactory.WRONG);
                TT_L_CUR.setListBackground(DendroFactory.WRONG);
                TT_F_CUR.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }

    private void tdSaveAction() {
        try {
            if (CURRENT_INSTANCE.DATA_HANDLER.trade(
                    Validation.validateDate(TD_DATE, null, CURRENT_INSTANCE),
                    Validation.validateDecimal(TD_F_AMOUNT),
                    Validation.validateDecimal(TD_T_AMOUNT),
                    Validation.validateDecimal(TD_UNIT),
                    TD_EXCHANGE.getSelectedItem(),
                    TD_F_CUR.getSelectedItem(),
                    TD_T_CUR.getSelectedItem())
            ) {
                MAIN.updateTable();
                dispose();
            } else {
                TD_EXCHANGE.setListBackground(DendroFactory.WRONG);
                TD_F_CUR.setListBackground(DendroFactory.WRONG);
                TD_T_CUR.setListBackground(DendroFactory.WRONG);
            }
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness.");
        }
    }
}
