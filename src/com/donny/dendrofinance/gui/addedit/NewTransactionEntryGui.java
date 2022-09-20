package com.donny.dendrofinance.gui.addedit;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ItemField;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Vector;

public class NewTransactionEntryGui extends ModalFrame {
    public final MainGui MAIN;
    public final long UUID;
    private final JTextField SIMP_DATE, SIMP_ENT, SIMP_ITM, SIMP_DESC,
            C1, C2, C3, C4, C5, C6,
            ADV_DATE, ADV_ENT, ADV_ITM, ADV_DESC;
    private final JComboBox<String> B1, B2, B3, B4, B5, B6, META_TYPE;
    private final SearchBox A1, A2, A3, A4, A5, A6;
    private final ItemField ACC;
    private final JTextArea META, META_TEXT;
    private final JTable TABLE;
    public JsonObject metaObject;

    public NewTransactionEntryGui(MainGui caller, Instance curInst) {
        this(caller, 0, curInst);
    }

    public NewTransactionEntryGui(MainGui caller, long uuid, Instance curInst) {
        super(caller, (uuid == 0 ? "New" : "Edit") + " Transaction Entry", curInst);
        MAIN = caller;
        UUID = uuid;
        //draw gui
        {
            setBackground(DendroFactory.REGULAR);

            JTabbedPane back = new JTabbedPane();
            //simple tab
            {
                JPanel tab = new JPanel();
                tab.setBorder(null);

                JLabel a = new JLabel("Date");
                JLabel b = new JLabel("Entity");
                JLabel c = new JLabel("Items");
                JLabel d = new JLabel("Description");

                JButton insert = DendroFactory.getButton("Save");
                insert.addActionListener(event -> simpleInsertAction());
                JButton cancel = DendroFactory.getButton("Cancel");
                cancel.addActionListener(event -> dispose());

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

                A1 = new SearchBox("Account 1", CURRENT_INSTANCE.getDCAccountsAsStrings());
                A2 = new SearchBox("Account 2", CURRENT_INSTANCE.getDCAccountsAsStrings());
                A3 = new SearchBox("Account 3", CURRENT_INSTANCE.getDCAccountsAsStrings());
                A4 = new SearchBox("Account 4", CURRENT_INSTANCE.getDCAccountsAsStrings());
                A5 = new SearchBox("Account 5", CURRENT_INSTANCE.getDCAccountsAsStrings());
                A6 = new SearchBox("Account 6", CURRENT_INSTANCE.getDCAccountsAsStrings());

                B1 = new JComboBox<>();
                B1.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Ghost", "Tracking"}));
                B1.addItemListener(event -> bChanged(B1, A1));

                B2 = new JComboBox<>();
                B2.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Ghost", "Tracking"}));
                B2.addItemListener(event -> bChanged(B2, A2));

                B3 = new JComboBox<>();
                B3.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Ghost", "Tracking"}));
                B3.addItemListener(event -> bChanged(B3, A3));

                B4 = new JComboBox<>();
                B4.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Ghost", "Tracking"}));
                B4.addItemListener(event -> bChanged(B4, A4));

                B5 = new JComboBox<>();
                B5.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Ghost", "Tracking"}));
                B5.addItemListener(event -> bChanged(B5, A5));

                B6 = new JComboBox<>();
                B6.setModel(new DefaultComboBoxModel<>(new String[]{"Debit", "Credit", "Ghost", "Tracking"}));
                B6.addItemListener(event -> bChanged(B6, A6));

                //group layout
                {
                    GroupLayout main = new GroupLayout(tab);
                    tab.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                                    ).addComponent(
                                                            d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                                    cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                                    SIMP_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    SIMP_ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    SIMP_ITM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                back.addTab("Simple", tab);
            }

            //advanced tab
            {
                JPanel tab = new JPanel();
                tab.setBorder(null);

                JLabel a = new JLabel("Date");
                JLabel b = new JLabel("Entity");
                JLabel c = new JLabel("Items");
                JLabel d = new JLabel("Description");
                JLabel e = new JLabel("Accounts");
                JLabel f = new JLabel("Meta-Data");

                ADV_DATE = new JTextField();
                ADV_ENT = new JTextField();
                ADV_ITM = new JTextField();
                ADV_DESC = new JTextField();

                JButton insert = DendroFactory.getButton("Save");
                insert.addActionListener(event -> advancedInsertAction());
                JButton cancel = DendroFactory.getButton("Cancel");
                cancel.addActionListener(event -> dispose());

                ACC = new ItemField(CURRENT_INSTANCE.getAccountsAsStrings());
                JScrollPane metaPane = DendroFactory.getScrollField();
                META = (JTextArea) metaPane.getViewport().getView();

                //advanced tab
                {
                    GroupLayout main = new GroupLayout(tab);
                    tab.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addGroup(
                                                    main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
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
                                                            metaPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    )
                                            )
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                                    ADV_DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_ITM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ADV_DESC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ACC, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    metaPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                back.addTab("Advanced", tab);
            }

            //advanced meta tab
            {
                JPanel tab = new JPanel();
                tab.setBorder(null);

                JLabel a = new JLabel("List");
                JLabel b = new JLabel("Json");

                JButton metaNew = DendroFactory.getButton("New");
                metaNew.addActionListener(event -> mNewAction());
                JButton update = DendroFactory.getButton("Update");
                update.addActionListener(event -> mUpdateAction());
                JButton save = DendroFactory.getButton("Save");
                save.addActionListener(event -> mSaveAction());
                JButton delete = DendroFactory.getButton("Delete");
                delete.addActionListener(event -> mDeleteAction());

                JScrollPane textPane = DendroFactory.getScrollField();
                META_TEXT = (JTextArea) textPane.getViewport().getView();
                textPane.setViewportView(META_TEXT);
                META_TYPE = new JComboBox<>();
                META_TYPE.addItemListener(event -> metaTypeChanged());
                JScrollPane tablePane = DendroFactory.getTable(new String[]{}, new Object[][]{}, true);
                TABLE = (JTable) tablePane.getViewport().getView();
                tablePane.setViewportView(TABLE);

                //advanced meta tab
                {
                    GroupLayout main = new GroupLayout(tab);
                    tab.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            META_TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            tablePane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addGap(
                                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                            ).addComponent(
                                                    metaNew, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            textPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    ).addComponent(
                                            update, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                    )
                            ).addContainerGap()
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addContainerGap().addComponent(
                                    META_TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    tablePane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            delete, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            metaNew, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(
                                    DendroFactory.MEDIUM_GAP, DendroFactory.MEDIUM_GAP, DendroFactory.MEDIUM_GAP
                            ).addComponent(
                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    textPane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP
                            ).addComponent(
                                    update, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addContainerGap()
                    );
                }

                back.addTab("Adv Metadata", tab);
            }

            add(back);
        }
        if (UUID == 0) {
            metaObject = new JsonObject();
        } else {
            TransactionEntry entry = CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID);
            LAccountSet set = entry.getAccounts();
            if (set.getSize() <= 6) {
                SIMP_DATE.setText(entry.getDate().toString());
                SIMP_ENT.setText(entry.getEntity());
                SIMP_ITM.setText(entry.getItems());
                SIMP_DESC.setText(entry.getDescription());
                for (int i = 0; i < set.getSize(); i++) {
                    SearchBox alef;
                    JComboBox<String> bet;
                    JTextField gimel;
                    switch (i) {
                        case 0 -> {
                            alef = A1;
                            bet = B1;
                            gimel = C1;
                        }
                        case 1 -> {
                            alef = A2;
                            bet = B2;
                            gimel = C2;
                        }
                        case 2 -> {
                            alef = A3;
                            bet = B3;
                            gimel = C3;
                        }
                        case 3 -> {
                            alef = A4;
                            bet = B4;
                            gimel = C4;
                        }
                        case 4 -> {
                            alef = A5;
                            bet = B5;
                            gimel = C5;
                        }
                        default -> {
                            alef = A6;
                            bet = B6;
                            gimel = C6;
                        }
                    }
                    AccountWrapper wrapper = set.get(i);
                    switch (wrapper.COLUMN) {
                        case DEBIT -> bet.setSelectedIndex(0);
                        case CREDIT -> bet.setSelectedIndex(1);
                        case GHOST -> bet.setSelectedIndex(2);
                        case TRACKER -> bet.setSelectedIndex(3);
                    }
                    alef.setSelectedIndex(wrapper.ACCOUNT.getName());
                    gimel.setText(wrapper.VALUE.toString());
                }
            }
            ADV_DATE.setText(entry.getDate().toString());
            ADV_ENT.setText(entry.getEntity());
            ADV_ITM.setText(entry.getItems());
            ADV_DESC.setText(entry.getDescription());
            ACC.setText(entry.getAccounts().toString());
            metaObject = entry.getMeta();
        }
        META.setText(metaObject.print());
        META_TEXT.setText(metaObject.print());
        META_TYPE.addItem("New NF Asset");
        META_TYPE.addItem("New Loan");
        META_TYPE.addItem("Trading Ledger");
        META_TYPE.addItem("NF Asset Change");
        META_TYPE.addItem("Loan Change");
        META_TYPE.addItem("Check");
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void bChanged(JComboBox<String> b, SearchBox a) {
        String column = (String) b.getSelectedItem();
        int index = a.getSelectedIndex();
        switch (AWColumn.fromString("" + column.charAt(0))) {
            case DEBIT, CREDIT -> a.setMaster(CURRENT_INSTANCE.getDCAccountsAsStrings());
            case GHOST -> a.setMaster(CURRENT_INSTANCE.getGhostAccountsAsStrings());
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
                        return true;
                    }
                });
                if (metaObject.containsKey("asset")) {
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
                        return true;
                    }
                });
                if (metaObject.containsKey("loan")) {
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
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("From", "To", "F Amount", "To Amount", "Main Amnt")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return true;
                    }
                });
                if (metaObject.containsKey("ledger")) {
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
                        return true;
                    }
                });
                if (metaObject.containsKey("asset-change")) {
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
                        return true;
                    }
                });
                if (metaObject.containsKey("loan-change")) {
                    for (JsonObject obj : metaObject.getArray("loan-change").getObjectArray()) {
                        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                obj.getString("name").getString(),
                                obj.getDecimal("change").decimal.toString()
                        });
                    }
                }
            }
            case (5) -> {
                TABLE.setModel(new DefaultTableModel(new Vector<>(Arrays.asList("Date Cashed", "Check Number", "Value")), 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return true;
                    }
                });
                if (metaObject.containsKey("check")) {
                    for (JsonObject obj : metaObject.getArray("check").getObjectArray()) {
                        if (obj.getDecimal("cashed").decimal.compareTo(BigDecimal.ZERO) == 0) {
                            ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                    "",
                                    obj.getString("number").getString(),
                                    CURRENT_INSTANCE.$(obj.getDecimal("value").decimal)
                            });
                        } else {
                            ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{
                                    new LDate(obj.getDecimal("cashed"), CURRENT_INSTANCE).toDateString(),
                                    obj.getString("number").getString(),
                                    CURRENT_INSTANCE.$(obj.getDecimal("value").decimal)
                            });
                        }
                    }
                }
            }
        }
    }

    public void simpleInsertAction() {
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
            AWColumn t1 = AWColumn.fromString("" + column.charAt(0));
            aArr.add(new JsonString(t1 + "!" + A1.getSelectedItem()));
            vArr.add(new JsonDecimal(Validation.validateDecimal(C1)));
            if (A2.getSelectedItem() != null) {
                column = (String) B2.getSelectedItem();
                if (column.equals("Tracking")) {
                    column = "B";
                }
                AWColumn t2 = AWColumn.fromString("" + column.charAt(0));
                aArr.add(new JsonString(t2 + "!" + A2.getSelectedItem()));
                vArr.add(new JsonDecimal(Validation.validateDecimal(C2)));
                if (A3.getSelectedItem() != null) {
                    column = (String) B3.getSelectedItem();
                    if (column.equals("Tracking")) {
                        column = "B";
                    }
                    AWColumn t3 = AWColumn.fromString("" + column.charAt(0));
                    aArr.add(new JsonString(t3 + "!" + A3.getSelectedItem()));
                    vArr.add(new JsonDecimal(Validation.validateDecimal(C3)));
                    if (A4.getSelectedItem() != null) {
                        column = (String) B4.getSelectedItem();
                        if (column.equals("Tracking")) {
                            column = "B";
                        }
                        AWColumn t4 = AWColumn.fromString("" + column.charAt(0));
                        aArr.add(new JsonString(t4 + "!" + A4.getSelectedItem()));
                        vArr.add(new JsonDecimal(Validation.validateDecimal(C4)));
                        if (A5.getSelectedItem() != null) {
                            column = (String) B5.getSelectedItem();
                            if (column.equals("Tracking")) {
                                column = "B";
                            }
                            AWColumn t5 = AWColumn.fromString("" + column.charAt(0));
                            aArr.add(new JsonString(t5 + "!" + A5.getSelectedItem()));
                            vArr.add(new JsonDecimal(Validation.validateDecimal(C5)));
                            if (A6.getSelectedItem() != null) {
                                column = (String) B6.getSelectedItem();
                                if (column.equals("Tracking")) {
                                    column = "B";
                                }
                                AWColumn t6 = AWColumn.fromString("" + column.charAt(0));
                                aArr.add(new JsonString(t6 + "!" + A6.getSelectedItem()));
                                vArr.add(new JsonDecimal(Validation.validateDecimal(C6)));
                            }
                        }
                    }
                }
            }
            entry.insert(
                    Validation.validateDate(SIMP_DATE, entry.getDate(), CURRENT_INSTANCE),
                    Validation.validateString(SIMP_ENT),
                    Validation.validateStringAllowEmpty(SIMP_ITM),
                    Validation.validateStringAllowEmpty(SIMP_DESC),
                    new LAccountSet(aArr, CURRENT_INSTANCE)
            );
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness!  " + ex.getMessage());
        } catch (JsonFormattingException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Somehow, the strings in account dropdowns are broken.\nThis should not be happening.  What did you do?");
        }
        if (UUID == 0) {
            CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
        }
        MAIN.updateTable();
        dispose();
    }

    public void advancedInsertAction() {
        try {
            TransactionEntry entry;
            if (UUID == 0) {
                entry = new TransactionEntry(CURRENT_INSTANCE);
            } else {
                entry = CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(UUID);
            }
            entry.insert(
                    Validation.validateDate(ADV_DATE, entry.getDate(), CURRENT_INSTANCE),
                    Validation.validateString(ADV_ENT),
                    Validation.validateStringAllowEmpty(ADV_ITM),
                    Validation.validateStringAllowEmpty(ADV_DESC),
                    new LAccountSet(Validation.validateString(ACC), CURRENT_INSTANCE)
            );
            entry.setMeta(Validation.validateJsonObject(META));
            if (UUID == 0) {
                CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
            }
            MAIN.updateTable();
            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "You did a badness!  " + ex.getMessage());
        }
    }

    public void mNewAction() {
        ((DefaultTableModel) TABLE.getModel()).addRow(new String[]{});
    }

    public void mUpdateAction() {
        try {
            metaObject = (JsonObject) JsonItem.digest(META_TEXT.getText());
            META.setText(metaObject.print());
        } catch (JsonFormattingException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Json on Json tab");
        }
    }

    public void mSaveAction() {
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
                                obj.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.put("desc", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.put("currency", new JsonString(tableAccess.getValueAt(i, 2).toString()));
                                obj.put("val", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.put("count", new JsonDecimal(tableAccess.getValueAt(i, 4).toString()));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("asset", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
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
                                obj.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.put("desc", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.put("cur", new JsonString(tableAccess.getValueAt(i, 2).toString()));
                                obj.put("princ", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.put("rate", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 4).toString())
                                ));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("loan", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
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
                                obj.put("from-cur", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.put("to-cur", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.put("from-amnt", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 2).toString())
                                ));
                                obj.put("to-amnt", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.put("main-amnt", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 4).toString())
                                ));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("ledger", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
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
                                obj.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.put("currency", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.put("change", new JsonDecimal(
                                        CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 2).toString())
                                ));
                                obj.put("count", new JsonDecimal(tableAccess.getValueAt(i, 3).toString()));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("asset-change", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
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
                                obj.put("name", new JsonString(tableAccess.getValueAt(i, 0).toString()));
                                obj.put("change", new JsonDecimal(tableAccess.getValueAt(i, 1).toString()));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("loan", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
                    }
                }
                case (5) -> {
                    try {
                        for (int i = 0; i < TABLE.getRowCount(); i++) {
                            for (int j = 0; j < 2; j++) {
                                if (tableAccess.getValueAt(i, j) == null) {
                                    flag = false;
                                }
                            }
                            if (flag) {
                                JsonObject obj = new JsonObject();
                                if (
                                        tableAccess.getValueAt(i, 0).toString().equals("")
                                                || tableAccess.getValueAt(i, 0).toString().equalsIgnoreCase("null")
                                                || tableAccess.getValueAt(i, 0).toString().equalsIgnoreCase("outstanding")
                                ) {
                                    obj.put("cashed", new JsonDecimal(new LDate(0, CURRENT_INSTANCE).getTime()));
                                } else {
                                    obj.put("cashed", new JsonDecimal(new LDate(tableAccess.getValueAt(i, 0).toString(), CURRENT_INSTANCE).getTime()));
                                }
                                obj.put("number", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.put("value", new JsonDecimal(CURRENT_INSTANCE.cleanNumber(tableAccess.getValueAt(i, 2).toString())));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("check", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
                    }
                }
            }
            META_TEXT.setText(metaObject.print());
            META.setText(metaObject.print());
            metaTypeChanged();
        }
    }

    public void mDeleteAction() {
        int x = TABLE.getSelectedRow();
        if (x > -1) {
            ((DefaultTableModel) TABLE.getModel()).removeRow(x);
        }
    }
}
