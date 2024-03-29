package com.donny.dendrofinance.gui.menu.transactions;

import com.donny.dendrofinance.capsules.TemplateCapsule;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.AccountsFrame;
import com.donny.dendrofinance.gui.customswing.ProgramModalFrame;
import com.donny.dendrofinance.gui.customswing.SizeFilter;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Cleaning;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.json.*;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Vector;

public class NewTransactionEntryGui extends ProgramModalFrame {
    public final ProgramMainGui MAIN;
    public final long UUID;
    public final boolean CLONE;
    private final JTextField DATE, ENT;
    private final JTextArea ITM, DESC;
    private final AccountsFrame ACC;

    private final JComboBox<String> TYPE;
    private final JTextArea META;
    private final JTable TABLE;
    public JsonObject metaObject;

    public NewTransactionEntryGui(ProgramMainGui caller, ProgramInstance curInst) {
        this(caller, 0, false, curInst);
    }

    public NewTransactionEntryGui(ProgramMainGui caller, long uuid, boolean clone, ProgramInstance curInst) {
        super(caller, (uuid == 0 ? "New" : clone ? "Clone" : "Edit") + " Transaction Entry", curInst);
        MAIN = caller;
        UUID = uuid;
        CLONE = clone;
        //draw gui
        {
            setBackground(DendroFactory.REGULAR);

            JTabbedPane back = new JTabbedPane();

            //transaction tab
            {
                JPanel tab = new JPanel();
                tab.setBorder(null);

                JLabel a = new JLabel("Date");
                JLabel b = new JLabel("Entity");
                JLabel c = new JLabel("Items");
                JLabel d = new JLabel("Description");

                DATE = new JTextField();
                SizeFilter.install(DATE, 255);
                ENT = new JTextField();
                SizeFilter.install(ENT, 255);
                JScrollPane itmScroll = DendroFactory.getLongField();
                ITM = (JTextArea) itmScroll.getViewport().getView();
                SizeFilter.install(ITM, 255);
                JScrollPane descScroll = DendroFactory.getLongField();
                DESC = (JTextArea) descScroll.getViewport().getView();
                SizeFilter.install(DESC, 255);

                ACC = new AccountsFrame(CURRENT_INSTANCE);

                JButton insert = DendroFactory.getButton("Save");
                insert.addActionListener(event -> insertAction());
                JButton cancel = DendroFactory.getButton("Cancel");
                cancel.addActionListener(event -> dispose());

                JComboBox<TemplateCapsule> templates = new JComboBox<>();
                CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.getTemplates().forEach(templates::addItem);
                JButton load = DendroFactory.getButton("Load Template");
                load.addActionListener(event -> {
                    if (templates.getSelectedIndex() >= 0) {
                        TransactionCapsule capsule = CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.get(((TemplateCapsule) templates.getSelectedItem()).getRef());
                        DATE.setText(capsule.getDate().toString());
                        ENT.setText(capsule.getEntity());
                        ITM.setText(capsule.getItems());
                        DESC.setText(capsule.getDescription());
                        ACC.load(capsule.getAccounts());
                        metaObject = capsule.getMeta();
                    }
                });

                //transaction tab
                {
                    GroupLayout main = new GroupLayout(tab);
                    tab.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                            main.createSequentialGroup().addComponent(
                                                    templates, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                    load, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGroup(
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
                                                            DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            itmScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                    ).addComponent(
                                                            descScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                                    DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    ENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    itmScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    descScroll, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    )
                            ).addGap(DendroFactory.LARGE_GAP).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            templates, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            load, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addGap(
                                    DendroFactory.LARGE_GAP, DendroFactory.LARGE_GAP, Short.MAX_VALUE
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                back.addTab("Transaction", tab);
            }

            //accounts tab
            {
                JPanel tab = new JPanel();
                tab.setBorder(null);

                JScrollPane accScroll = DendroFactory.getScrollPane(false, true);
                accScroll.setViewportView(ACC);

                JButton insert = DendroFactory.getButton("Save");
                insert.addActionListener(event -> insertAction());
                JButton cancel = DendroFactory.getButton("Cancel");
                cancel.addActionListener(event -> dispose());

                GroupLayout main = new GroupLayout(tab);
                tab.setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        accScroll
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
                        main.createSequentialGroup().addContainerGap().addComponent(
                                accScroll
                        ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );

                back.addTab("Accounts", tab);
            }


            //meta tab
            {
                JPanel tab = new JPanel();
                tab.setBorder(null);

                JLabel a = new JLabel("List");
                JLabel b = new JLabel("Json");

                JButton metaNew = DendroFactory.getButton("New Row");
                metaNew.addActionListener(event -> mNewAction());
                JButton update = DendroFactory.getButton("Update Table From Metadata");
                update.addActionListener(event -> mUpdateAction());
                JButton save = DendroFactory.getButton("Save Table to Metadata");
                save.addActionListener(event -> mSaveAction());
                JButton delete = DendroFactory.getButton("Delete Row");
                delete.addActionListener(event -> mDeleteAction());

                JScrollPane textPane = DendroFactory.getScrollField();
                META = (JTextArea) textPane.getViewport().getView();
                SizeFilter.install(META, 8192);
                textPane.setViewportView(META);
                TYPE = new JComboBox<>();
                TYPE.addItemListener(event -> metaTypeChanged());
                JScrollPane tablePane = DendroFactory.getTable(new String[]{}, new Object[][]{}, true);
                TABLE = (JTable) tablePane.getViewport().getView();
                tablePane.setViewportView(TABLE);

                JButton insert = DendroFactory.getButton("Save");
                insert.addActionListener(event -> insertAction());
                JButton cancel = DendroFactory.getButton("Cancel");
                cancel.addActionListener(event -> dispose());

                //meta tab
                {
                    GroupLayout main = new GroupLayout(tab);
                    tab.setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addContainerGap().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                            TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                            main.createSequentialGroup().addContainerGap().addComponent(
                                    TYPE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                            ).addGap(
                                    DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                            ).addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            insert, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            ).addContainerGap()
                    );
                }

                back.addTab("Metadata", tab);
            }

            add(back);
        }
        if (UUID == 0) {
            metaObject = new JsonObject();
        } else {
            TransactionCapsule capsule = CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.get(UUID);
            DATE.setText(capsule.getDate().toString());
            ENT.setText(capsule.getEntity());
            ITM.setText(capsule.getItems());
            DESC.setText(capsule.getDescription());
            ACC.load(capsule.getAccounts());
            metaObject = capsule.getMeta();
        }
        META.setText(metaObject.print());
        TYPE.addItem("New NF Asset");
        TYPE.addItem("New Loan");
        TYPE.addItem("Trading Ledger");
        TYPE.addItem("NF Asset Change");
        TYPE.addItem("Loan Change");
        TYPE.addItem("Check");
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    public void metaTypeChanged() {
        switch (TYPE.getSelectedIndex()) {
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

    public void insertAction() {
        try {
            TransactionCapsule capsule;
            if (UUID == 0 || CLONE) {
                capsule = new TransactionCapsule(CURRENT_INSTANCE);
            } else {
                capsule = CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.get(UUID);
            }
            capsule.insert(
                    Validation.validateDate(DATE, CURRENT_INSTANCE),
                    Validation.validateString(ENT),
                    Validation.validateStringAllowEmpty(ITM),
                    Validation.validateStringAllowEmpty(DESC),
                    ACC.export()
            );
            capsule.setMeta(Validation.validateJsonObject(META));
            CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(capsule, ImportHandler.ImportMode.OVERWRITE);
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
            metaObject = (JsonObject) JsonItem.digest(META.getText());
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
            switch (TYPE.getSelectedIndex()) {
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
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 3).toString())
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
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.put("rate", new JsonDecimal(
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 4).toString())
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
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 2).toString())
                                ));
                                obj.put("to-amnt", new JsonDecimal(
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 3).toString())
                                ));
                                obj.put("main-amnt", new JsonDecimal(
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 4).toString())
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
                                        Cleaning.cleanNumber(tableAccess.getValueAt(i, 2).toString())
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
                                        tableAccess.getValueAt(i, 0).toString().isEmpty()
                                                || tableAccess.getValueAt(i, 0).toString().equalsIgnoreCase("null")
                                                || tableAccess.getValueAt(i, 0).toString().equalsIgnoreCase("outstanding")
                                ) {
                                    obj.put("cashed", new JsonDecimal(new LDate(0, CURRENT_INSTANCE).getTime()));
                                } else {
                                    obj.put("cashed", new JsonDecimal(new LDate(tableAccess.getValueAt(i, 0).toString(), CURRENT_INSTANCE).getTime()));
                                }
                                obj.put("number", new JsonString(tableAccess.getValueAt(i, 1).toString()));
                                obj.put("value", new JsonDecimal(Cleaning.cleanNumber(tableAccess.getValueAt(i, 2).toString())));
                                arr.add(obj);
                            }
                        }
                        metaObject.put("check", arr);
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Entry in Meta Table");
                    } catch (ParseException e) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Date in Meta Table");
                    }
                }
            }
            META.setText(metaObject.print());
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
