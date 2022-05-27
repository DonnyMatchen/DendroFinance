package com.donny.dendrofinance.gui.addedit;

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
    private final JPanel BS, IP, CT, TT, TD;
    private final JLabel A, B, C,
            D, E, F, G,
            H, I, J, K,
            L, M, N, O, P, Q,
            R, S, T, U;
    private final JTextField BS_DATE, BS_AMOUNT, BS_COST,
            IP_DATE, IP_DESC, IP_AMouNT, IP_UNIT,
            CT_DATE, CT_F_AMOUNT, CT_T_AMOUNT, CT_UNIT,
            TT_DATE, TT_F_AMOUNT, TT_T_AMOUNT, TT_FEE, TT_L_UNIT, TT_F_UNIT,
            TD_DATE, TD_F_AMOUNT, TD_T_AMOUNT, TD_UNIT;
    private final SearchBox BS_EXCHANGE, BS_CUR,
            IP_EXCHANGE, IP_CUR,
            CT_F_EXCHANGE, CT_T_EXCHANGE, CT_CUR,
            TT_F_EXCHANGE, TT_T_EXCHANGE, TT_L_CUR, TT_F_CUR,
            TD_EXCHANGE, TD_F_CUR, TD_T_CUR;
    private final JButton BS_CANCEL, BS_SAVE,
            IP_CANCEL, IP_SAVE,
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
                A = new JLabel("Date");
                B = new JLabel("Cost");
                C = new JLabel("Amount");
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
                                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BS_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            BS_COST, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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

            //income payment
            {
                IP = new JPanel();
                IP.setBorder(null);
                D = new JLabel("Date");
                E = new JLabel("Desc");
                F = new JLabel("Unit");
                G = new JLabel("Amount");
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
                //buy sell layout
                {
                    GroupLayout main = new GroupLayout(IP);
                    IP.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            G, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            F, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            IP_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            G, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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

            //coin transfer
            {
                CT = new JPanel();
                CT.setBorder(null);
                H = new JLabel("Date");
                I = new JLabel("From Amount");
                J = new JLabel("To Amount");
                K = new JLabel("Unit Price");
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
                                                            H, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            I, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            J, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            K, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            H, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            I, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            J, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            CT_T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            K, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                L = new JLabel("Date");
                M = new JLabel("From Amount");
                N = new JLabel("To Amount");
                O = new JLabel("Fee Amount");
                P = new JLabel("Transfer Unit");
                Q = new JLabel("Fee Unit");
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
                                                            L, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            M, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            N, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            O, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            P, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            Q, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            L, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            M, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            N, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            O, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_FEE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            P, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TT_L_UNIT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            Q, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                R = new JLabel("Date");
                S = new JLabel("From Amount");
                T = new JLabel("To Amount");
                U = new JLabel("From Unit");
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
                                                            R, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            S, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            T, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            U, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            R, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            S, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_F_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            T, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            TD_T_AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            U, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
