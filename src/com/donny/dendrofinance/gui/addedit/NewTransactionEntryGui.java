package com.donny.dendrofinance.gui.addedit;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ItemField;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Vector;

public class NewTransactionEntryGui extends JDialog {
    public final Instance CURRENT_INSTANCE;
    public final MainGui MAIN;
    public final long UUID;
    private final JTabbedPane BACK;
    private final JPanel SIMP_TAB, ADV_TAB, META_TAB;
    private final JLabel A, B, C, D,
            AA, BB, CC, DD, EE, FF, GG,
            AAA, BBB;
    private final JTextField SIMP_DATE, SIMP_ENT, SIMP_ITM, SIMP_DESC,
            C1, C2, C3, C4, C5, C6,
            ADV_DATE, ADV_ENT, ADV_ITM, ADV_DESC;
    private final JComboBox<String> B1, B2, B3, B4, B5, B6, META_TYPE;
    private final SearchBox A1, A2, A3, A4, A5, A6;
    private final JButton SIMP_INSERT, SIMP_CANCEL,
            ADV_INSERT, ADV_CANCEL,
            META_NEW, META_UPDATE, META_SAVE, META_DELETE;
    private final JScrollPane ADV_VAL_PANE, ADV_META_PANE,
            META_TABLE_PANE, META_TEXT_PANE;
    private final ItemField ACC;
    private final JTextArea VAL, META, META_TEXT;
    private final JTable TABLE;
    public JsonObject metaObject;

    public NewTransactionEntryGui(MainGui caller, Instance curInst) {
        this(caller, 0, curInst);
    }

    public NewTransactionEntryGui(MainGui caller, long uuid, Instance curInst) {
        super(caller, true);
        CURRENT_INSTANCE = curInst;
        MAIN = caller;
        UUID = uuid;
        //draw gui
        {
            setBackground(DendroFactory.REGULAR);

            BACK = new JTabbedPane();
            //simple tab
            {
                SIMP_TAB = new JPanel();

                A = new JLabel("Date");
                B = new JLabel("Entity");
                C = new JLabel("Items");
                D = new JLabel("Description");

                SIMP_INSERT = DendroFactory.getButton("Save");
                SIMP_INSERT.addActionListener(this::simpleInsertAction);
                SIMP_CANCEL = DendroFactory.getButton("Cancel");
                SIMP_CANCEL.addActionListener(event -> dispose());

                SIMP_DATE = new JTextField();
                SIMP_ENT = new JTextField();
                SIMP_ITM = new JTextField();
                SIMP_DESC = new JTextField();
                C1 = new JTextField();
                C2 = new JTextField();
                C3 = new JTextField();
                C4 = new JTextField();
                C5 = new JTextField();
                C6 = new JTextField();

                A1 = new SearchBox("Account 1", curInst.getDCAccountsAsStrings());
                A2 = new SearchBox("Account 2", curInst.getDCAccountsAsStrings());
                A3 = new SearchBox("Account 3", curInst.getDCAccountsAsStrings());
                A4 = new SearchBox("Account 4", curInst.getDCAccountsAsStrings());
                A5 = new SearchBox("Account 5", curInst.getDCAccountsAsStrings());
                A6 = new SearchBox("Account 6", curInst.getDCAccountsAsStrings());

                B1 = new JComboBox<>();
                B1.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Tax", "Tracking"}));
                B1.addItemListener(event -> bChanged(B1, A1));

                B2 = new JComboBox<>();
                B2.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Tax", "Tracking"}));
                B2.addItemListener(event -> bChanged(B2, A2));

                B3 = new JComboBox<>();
                B3.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Tax", "Tracking"}));
                B3.addItemListener(event -> bChanged(B3, A3));

                B4 = new JComboBox<>();
                B4.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Tax", "Tracking"}));
                B4.addItemListener(event -> bChanged(B4, A4));

                B5 = new JComboBox<>();
                B5.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Tax", "Tracking"}));
                B5.addItemListener(event -> bChanged(B5, A5));

                B6 = new JComboBox<>();
                B6.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Tax", "Tracking"}));
                B6.addItemListener(event -> bChanged(B6, A6));

                //group layout
                {
                    GroupLayout main = new GroupLayout(SIMP_TAB);
                    SIMP_TAB.setLayout(main);
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
                                                            SIMP_DATE, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            SIMP_ENT, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            SIMP_ITM, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            SIMP_DESC, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                            A1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            A2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            A3, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            A4, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            A5, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            A6, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                            B1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B3, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B4, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B5, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            B6, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            C1, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            C2, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            C3, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            C4, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            C5, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    ).addComponent(
                                                            C6, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    SIMP_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    SIMP_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createSequentialGroup().addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    SIMP_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    SIMP_ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    SIMP_ITM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    SIMP_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                    main.createSequentialGroup().addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A1, 100, 100, 100
                                            ).addComponent(
                                                    B1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    C1, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A2, 100, 100, 100
                                            ).addComponent(
                                                    B2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    C2, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A3, 100, 100, 100
                                            ).addComponent(
                                                    B3, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    C3, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A4, 100, 100, 100
                                            ).addComponent(
                                                    B4, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    C4, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A5, 100, 100, 100
                                            ).addComponent(
                                                    B5, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    C5, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    A6, 100, 100, 100
                                            ).addComponent(
                                                    B6, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    C6, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            SIMP_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            SIMP_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Simple", SIMP_TAB);
            //advanced tab
            {
                ADV_TAB = new JPanel();

                AA = new JLabel("Date");
                BB = new JLabel("Entity");
                CC = new JLabel("Items");
                DD = new JLabel("Description");
                EE = new JLabel("Accounts");
                FF = new JLabel("Values");
                GG = new JLabel("Meta-Data");

                ADV_DATE = new JTextField();
                ADV_ENT = new JTextField();
                ADV_ITM = new JTextField();
                ADV_DESC = new JTextField();

                ADV_INSERT = DendroFactory.getButton("Save");
                ADV_INSERT.addActionListener(this::advancedInsertAction);
                ADV_CANCEL = DendroFactory.getButton("Cancel");
                ADV_CANCEL.addActionListener(event -> dispose());

                ACC = new ItemField(CURRENT_INSTANCE.getAccountsAsStrings());
                ADV_VAL_PANE = DendroFactory.getLongField();
                VAL = (JTextArea) ADV_VAL_PANE.getViewport().getView();
                ADV_META_PANE = DendroFactory.getScrollField();
                META = (JTextArea) ADV_META_PANE.getViewport().getView();

                //advanced tab
                {
                    GroupLayout main = new GroupLayout(ADV_TAB);
                    ADV_TAB.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            AA, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            BB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            CC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            DD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            EE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            FF, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            GG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    )
                                            ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                            ADV_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ADV_ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ADV_ITM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ADV_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ACC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ADV_VAL_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ADV_META_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    ADV_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    ADV_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createSequentialGroup().addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    AA, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    BB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    CC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_ITM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    DD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    EE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ACC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    FF, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_VAL_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    GG, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_META_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            ADV_CANCEL, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            ADV_INSERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Advanced", ADV_TAB);
            //advanced meta tab
            {
                META_TAB = new JPanel();

                AAA = new JLabel("List");
                BBB = new JLabel("Json");

                META_NEW = DendroFactory.getButton("New");
                META_NEW.addActionListener(this::mNewAction);
                META_UPDATE = DendroFactory.getButton("Update");
                META_UPDATE.addActionListener(this::mUpdateAction);
                META_SAVE = DendroFactory.getButton("Save");
                META_SAVE.addActionListener(this::mSaveAction);
                META_DELETE = DendroFactory.getButton("Delete");
                META_DELETE.addActionListener(this::mDeleteAction);

                META_TEXT_PANE = DendroFactory.getScrollField();
                META_TEXT = (JTextArea) META_TEXT_PANE.getViewport().getView();
                META_TEXT_PANE.setViewportView(META_TEXT);
                META_TYPE = new JComboBox<>();
                META_TYPE.addItemListener(event -> metaTypeChanged());
                META_TABLE_PANE = DendroFactory.getTable(new String[]{}, new Object[][]{}, false);
                TABLE = (JTable) META_TABLE_PANE.getViewport().getView();
                META_TABLE_PANE.setViewportView(TABLE);

                //advanced meta tab
                {
                    GroupLayout main = new GroupLayout(META_TAB);
                    META_TAB.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            META_TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            AAA, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            META_TABLE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    META_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    META_DELETE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    META_NEW, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addComponent(
                                            BBB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            META_TEXT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            META_UPDATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addComponent(
                                    META_TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    AAA, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    META_TABLE_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            META_SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            META_DELETE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            META_NEW, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(
                                    DendroFactory.MEDIUM_GAP, DendroFactory.MEDIUM_GAP, DendroFactory.MEDIUM_GAP
                            ).addComponent(
                                    BBB, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    META_TEXT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    META_UPDATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addContainerGap()
                    );
                }
            }
            BACK.addTab("Adv Metadata", META_TAB);

            add(BACK);

            pack();
        }
        if (UUID == 0) {
            metaObject = new JsonObject();
        } else {
            TransactionEntry entry = CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID);
            LAccountSet set = entry.getAccounts();
            if (set.getSize() <= 6) {
                SIMP_DATE.setText("t(+0) " + entry.getDate().toString());
                SIMP_ENT.setText(entry.getEntity());
                SIMP_ITM.setText(entry.getItems());
                SIMP_DESC.setText(entry.getDescription());
                for (int i = 0; i < set.getSize(); i++) {
                    SearchBox alpha;
                    JComboBox<String> beta;
                    JTextField gamma;
                    switch (i) {
                        case 0 -> {
                            alpha = A1;
                            beta = B1;
                            gamma = C1;
                        }
                        case 1 -> {
                            alpha = A2;
                            beta = B2;
                            gamma = C2;
                        }
                        case 2 -> {
                            alpha = A3;
                            beta = B3;
                            gamma = C3;
                        }
                        case 3 -> {
                            alpha = A4;
                            beta = B4;
                            gamma = C4;
                        }
                        case 4 -> {
                            alpha = A5;
                            beta = B5;
                            gamma = C5;
                        }
                        default -> {
                            alpha = A6;
                            beta = B6;
                            gamma = C6;
                        }
                    }
                    AccountWrapper wrapper = set.get(i);
                    switch (wrapper.COLUMN) {
                        case DEBIT -> beta.setSelectedIndex(0);
                        case CREDIT -> beta.setSelectedIndex(1);
                        case TAX -> beta.setSelectedIndex(2);
                        case TRACKER -> beta.setSelectedIndex(3);
                    }
                    alpha.setSelectedIndex(wrapper.ACCOUNT.getName());
                    gamma.setText(entry.getValues().get(i).toString());
                }
            }
            ADV_DATE.setText("t(+0) " + entry.getDate().toString());
            ADV_ENT.setText(entry.getEntity());
            ADV_ITM.setText(entry.getItems());
            ADV_DESC.setText(entry.getDescription());
            ACC.setText(entry.getAccounts().toString());
            VAL.setText(entry.getValues().toString());
            metaObject = entry.getMeta();
        }
        META.setText(metaObject.toString());
        META_TEXT.setText(metaObject.toString());
        META_TYPE.addItem("New NF Asset");
        META_TYPE.addItem("New Loan");
        META_TYPE.addItem("Trading Ledger");
        META_TYPE.addItem("NF Asset Change");
        META_TYPE.addItem("Loan Change");
    }

    public void bChanged(JComboBox<String> b, SearchBox a) {
        String column = (String) b.getSelectedItem();
        if (column.equals("Tracking")) {
            column = "B";
        }
        int index = a.getSelectedIndex();
        switch (AccountWrapper.AWType.fromString("" + column.charAt(0))) {
            case DEBIT, CREDIT -> a.setMaster(CURRENT_INSTANCE.getDCAccountsAsStrings());
            case TAX -> a.setMaster(CURRENT_INSTANCE.getTaxAccountsAsStrings());
            case TRACKER -> a.setMaster(CURRENT_INSTANCE.getTrackingAccountsAsStrings());
        }
        if (index < a.getListSize()) {
            a.setSelectedIndex(index);
        }
    }

    public void metaTypeChanged() {
        switch (META_TYPE.getSelectedIndex()) {
            case (0) -> {
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("Name", "Description", "Cur", "Value", "Count")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                if (metaObject.FIELDS.containsKey("asset")) {
                    for (JsonObject obj : metaObject.getArray("asset").getObjectArray()) {
                        LCurrency cur = CURRENT_INSTANCE.getLCurrency(obj.getString("currency").getString());
                        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                obj.getString("name").getString(),
                                obj.getString("desc").getString(),
                                cur.toString(),
                                cur.encode(obj.getDecimal("val").decimal),
                                obj.getDecimal("count").decimal.toString(),
                        });
                    }
                }
            }
            case (1) -> {
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("Name", "Description", "Cur", "Princ", "Rate")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                if (metaObject.FIELDS.containsKey("loan")) {
                    for (JsonObject obj : metaObject.getArray("loan").getObjectArray()) {
                        LCurrency cur = CURRENT_INSTANCE.getLCurrency(obj.getString("cur").getString());
                        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                obj.getString("name").getString(),
                                obj.getString("desc").getString(),
                                cur.toString(),
                                cur.encode(obj.getDecimal("princ").decimal),
                                CURRENT_INSTANCE.p(obj.getDecimal("rate").decimal),
                        });
                    }
                }
            }
            case (2) -> {
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("From", "To", "F Amnt", "To Amnt", "Main Amnt")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                if (metaObject.FIELDS.containsKey("ledger")) {
                    for (JsonObject obj : metaObject.getArray("ledger").getObjectArray()) {
                        LCurrency cur1 = CURRENT_INSTANCE.getLCurrency(obj.getString("from-cur").getString()),
                                cur2 = CURRENT_INSTANCE.getLCurrency(obj.getString("to-cur").getString());
                        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                cur1.toString(),
                                cur2.toString(),
                                cur1.encode(obj.getDecimal("from-amnt").decimal),
                                cur2.encode(obj.getDecimal("to-amnt").decimal),
                                CURRENT_INSTANCE.$(obj.getDecimal("main-amnt").decimal)
                        });
                    }
                }
            }
            case (3) -> {
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("Name", "Cur", "Val Change", "Count Change")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                if (metaObject.FIELDS.containsKey("asset-change")) {
                    for (JsonObject obj : metaObject.getArray("asset-change").getObjectArray()) {
                        LCurrency cur = CURRENT_INSTANCE.getLCurrency(obj.getString("currency").getString());
                        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                obj.getString("name").getString(),
                                cur.toString(),
                                cur.encode(obj.getDecimal("change").decimal),
                                obj.getDecimal("count").decimal.toString(),
                        });
                    }
                }
            }
            case (4) -> {
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("Name", "Value")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                });
                if (metaObject.FIELDS.containsKey("loan-change")) {
                    for (JsonObject obj : metaObject.getArray("loan-change").getObjectArray()) {
                        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                obj.getString("name").getString(),
                                obj.getDecimal("change").decimal.toString()
                        });
                    }
                }
            }
        }
    }

    public void simpleInsertAction(ActionEvent event) {
        TransactionEntry entry;
        if (UUID == 0) {
            entry = new TransactionEntry(CURRENT_INSTANCE);
        } else {
            entry = CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID);
        }
        try {
            JsonArray aArr = new JsonArray(), vArr = new JsonArray();
            String column = (String) B1.getSelectedItem();
            if (column.equals("Tracking")) {
                column = "B";
            }
            AccountWrapper.AWType t1 = AccountWrapper.AWType.fromString("" + column.charAt(0));
            aArr.ARRAY.add(new JsonString(t1 + "!" + A1.getSelectedItem()));
            vArr.ARRAY.add(new JsonDecimal(Validation.validateDecimal(C1)));
            if (!A2.getSelectedItem().equals("<Blank>")) {
                column = (String) B2.getSelectedItem();
                if (column.equals("Tracking")) {
                    column = "B";
                }
                AccountWrapper.AWType t2 = AccountWrapper.AWType.fromString("" + column.charAt(0));
                aArr.ARRAY.add(new JsonString(t2 + "!" + A2.getSelectedItem()));
                vArr.ARRAY.add(new JsonDecimal(Validation.validateDecimal(C2)));
                if (!A3.getSelectedItem().equals("<Blank>")) {
                    column = (String) B3.getSelectedItem();
                    if (column.equals("Tracking")) {
                        column = "B";
                    }
                    AccountWrapper.AWType t3 = AccountWrapper.AWType.fromString("" + column.charAt(0));
                    aArr.ARRAY.add(new JsonString(t3 + "!" + A3.getSelectedItem()));
                    vArr.ARRAY.add(new JsonDecimal(Validation.validateDecimal(C3)));
                    if (!A4.getSelectedItem().equals("<Blank>")) {
                        column = (String) B4.getSelectedItem();
                        if (column.equals("Tracking")) {
                            column = "B";
                        }
                        AccountWrapper.AWType t4 = AccountWrapper.AWType.fromString("" + column.charAt(0));
                        aArr.ARRAY.add(new JsonString(t4 + "!" + A4.getSelectedItem()));
                        vArr.ARRAY.add(new JsonDecimal(Validation.validateDecimal(C4)));
                        if (!A5.getSelectedItem().equals("<Blank>")) {
                            column = (String) B5.getSelectedItem();
                            if (column.equals("Tracking")) {
                                column = "B";
                            }
                            AccountWrapper.AWType t5 = AccountWrapper.AWType.fromString("" + column.charAt(0));
                            aArr.ARRAY.add(new JsonString(t5 + "!" + A5.getSelectedItem()));
                            vArr.ARRAY.add(new JsonDecimal(Validation.validateDecimal(C5)));
                            if (!A6.getSelectedItem().equals("<Blank>")) {
                                column = (String) B6.getSelectedItem();
                                if (column.equals("Tracking")) {
                                    column = "B";
                                }
                                AccountWrapper.AWType t6 = AccountWrapper.AWType.fromString("" + column.charAt(0));
                                aArr.ARRAY.add(new JsonString(t6 + "!" + A6.getSelectedItem()));
                                vArr.ARRAY.add(new JsonDecimal(Validation.validateDecimal(C6)));
                            }
                        }
                    }
                }
            }
            entry.insert(
                    Validation.validateDate(SIMP_DATE, entry.getDate(), CURRENT_INSTANCE),
                    new LString(Validation.validateString(SIMP_ENT)),
                    new LString(Validation.validateStringAllowEmpty(SIMP_ITM)),
                    new LString(Validation.validateStringAllowEmpty(SIMP_DESC)),
                    new LAccountSet(aArr, CURRENT_INSTANCE),
                    new LDecimalSet(vArr)
            );
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(this.getClass(), "You did a badness!");
        } catch (JsonFormattingException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Somehow, the strings in account dropdowns are broken.\nThis should not be happening.  What did you do?");
        }
        if (UUID == 0) {
            CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
        }
        MAIN.updateTable();
        dispose();
    }

    public void advancedInsertAction(ActionEvent event) {
        try {
            TransactionEntry entry;
            if (UUID == 0) {
                entry = new TransactionEntry(CURRENT_INSTANCE);
            } else {
                entry = CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID);
            }
            entry.insert(
                    Validation.validateDate(ADV_DATE, entry.getDate(), CURRENT_INSTANCE),
                    new LString(Validation.validateString(ADV_ENT)),
                    new LString(Validation.validateStringAllowEmpty(ADV_ITM)),
                    new LString(Validation.validateStringAllowEmpty(ADV_DESC)),
                    new LAccountSet(Validation.validateString(ACC), CURRENT_INSTANCE),
                    new LDecimalSet(Validation.validateString(VAL))
            );
            entry.insertIntoField("meta-data", new LJson(Validation.validateJsonObject(META)));
            if (UUID == 0) {
                CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
            }
            MAIN.updateTable();
            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(this.getClass(), "You did a badness!");
        }
    }

    public void mNewAction(ActionEvent event) {
        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{});
    }

    public void mUpdateAction(ActionEvent event) {
        try {
            metaObject = (JsonObject) JsonItem.sanitizeDigest(META_TEXT.getText());
            META.setText(metaObject.toString());
        } catch (JsonFormattingException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Json on Json tab");
        }
    }

    public void mSaveAction(ActionEvent event) {
        boolean flag = true;
        JsonArray arr = new JsonArray();
        DefaultTableModel tableAccess = (DefaultTableModel) TABLE.getModel();
        if (TABLE.getRowCount() > 0) {
            switch (META_TYPE.getSelectedIndex()) {
                case (0) -> {
                    try {
                        for (int i = 0; i < TABLE.getRowCount(); i++) {
                            for (int j = 0; j < 5; j++) {
                                if (tableAccess.getValueAt(i, j) == null) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                JsonObject obj = new JsonObject();
                                obj.FIELDS.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.FIELDS.put("desc", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.FIELDS.put("currency", new JsonString(tableAccess.getValueAt(i, 2).toString()));
                                obj.FIELDS.put("val", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.FIELDS.put("count", new JsonDecimal(tableAccess.getValueAt(i, 4).toString()));
                                arr.ARRAY.add(obj);
                            }
                        }
                        metaObject.FIELDS.put("asset", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Entry in Meta Table");
                    }
                }
                case (1) -> {
                    try {
                        for (int i = 0; i < TABLE.getRowCount(); i++) {
                            for (int j = 0; j < 5; j++) {
                                if (tableAccess.getValueAt(i, j) == null) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                JsonObject obj = new JsonObject();
                                obj.FIELDS.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.FIELDS.put("desc", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.FIELDS.put("cur", new JsonString(tableAccess.getValueAt(i, 2).toString()));
                                obj.FIELDS.put("princ", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.FIELDS.put("rate", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 4).toString())
                                ));
                                arr.ARRAY.add(obj);
                            }
                        }
                        metaObject.FIELDS.put("loan", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Entry in Meta Table");
                    }
                }
                case (2) -> {
                    try {
                        for (int i = 0; i < TABLE.getRowCount(); i++) {
                            for (int j = 0; j < 5; j++) {
                                if (tableAccess.getValueAt(i, j) == null) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                JsonObject obj = new JsonObject();
                                obj.FIELDS.put("from-cur", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.FIELDS.put("to-cur", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.FIELDS.put("from-amnt", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 2).toString())
                                ));
                                obj.FIELDS.put("to-amnt", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.FIELDS.put("main-amnt", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 4).toString())
                                ));
                                arr.ARRAY.add(obj);
                            }
                        }
                        metaObject.FIELDS.put("ledger", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Entry in Meta Table");
                    }
                }
                case (3) -> {
                    try {
                        for (int i = 0; i < TABLE.getRowCount(); i++) {
                            for (int j = 0; j < 4; j++) {
                                if (tableAccess.getValueAt(i, j) == null) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                JsonObject obj = new JsonObject();
                                obj.FIELDS.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.FIELDS.put("currency", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.FIELDS.put("change", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 2).toString())
                                ));
                                obj.FIELDS.put("count", new JsonDecimal(tableAccess.getValueAt(i, 3).toString()));
                                arr.ARRAY.add(obj);
                            }
                        }
                        metaObject.FIELDS.put("asset-change", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Entry in Meta Table");
                    }
                }
                case (4) -> {
                    try {
                        for (int i = 0; i < TABLE.getRowCount(); i++) {
                            for (int j = 0; j < 2; j++) {
                                if (tableAccess.getValueAt(i, j) == null) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                JsonObject obj = new JsonObject();
                                obj.FIELDS.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.FIELDS.put("change", new JsonDecimal(tableAccess.getValueAt(i, 1).toString()));
                                arr.ARRAY.add(obj);
                            }
                        }
                        metaObject.FIELDS.put("loan", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Entry in Meta Table");
                    }
                }
            }
            META_TEXT.setText(metaObject.toString());
            META.setText(metaObject.toString());
            metaTypeChanged();
        }
    }

    public void mDeleteAction(ActionEvent event) {
        int x = TABLE.getSelectedRow();
        if (x > -1) {
            ((DefaultTableModel) TABLE.getModel()).removeRow(x);
        }
    }
}
