package com.donny.dendrofinance.gui.menu.util.acc;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.gui.customswing.ProgramRegisterFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Cleaning;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;

public class AppDepGui extends ProgramRegisterFrame {
    private final JTextField DATE, STOCK, STOCK_CURRENT, STOCK_APP, CRYPTO, CRYPTO_CURRENT, CRYPTO_APP, INV, INV_CURRENT, INV_APP, FIAT, FIAT_CURRENT, FIAT_APP;

    public AppDepGui(ProgramMainGui caller, ProgramInstance curInst) {
        super(caller, "Appreciation Automator", curInst);

        //draw gui
        {
            int width = 100;

            JLabel a = new JLabel("Date");
            JLabel b = new JLabel("Stocks");
            JLabel c = new JLabel("Cryptocurrencies");
            JLabel d = new JLabel("Inventories");
            JLabel e = new JLabel("Non-main Fiat");
            DATE = new JTextField();
            STOCK_CURRENT = new JTextField();
            STOCK_CURRENT.setEditable(false);
            STOCK_APP = new JTextField();
            STOCK_APP.setEditable(false);
            STOCK = new JTextField();
            STOCK.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    STOCK_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(STOCK.getText()).subtract(Cleaning.cleanNumber(STOCK_CURRENT.getText()))));
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    STOCK_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(STOCK.getText()).subtract(Cleaning.cleanNumber(STOCK_CURRENT.getText()))));
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    STOCK_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(STOCK.getText()).subtract(Cleaning.cleanNumber(STOCK_CURRENT.getText()))));
                }
            });
            CRYPTO_CURRENT = new JTextField();
            CRYPTO_CURRENT.setEditable(false);
            CRYPTO_APP = new JTextField();
            CRYPTO_APP.setEditable(false);
            CRYPTO = new JTextField();
            CRYPTO.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    CRYPTO_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(CRYPTO.getText()).subtract(Cleaning.cleanNumber(CRYPTO_CURRENT.getText()))));
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    CRYPTO_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(CRYPTO.getText()).subtract(Cleaning.cleanNumber(CRYPTO_CURRENT.getText()))));
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    CRYPTO_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(CRYPTO.getText()).subtract(Cleaning.cleanNumber(CRYPTO_CURRENT.getText()))));
                }
            });
            INV_CURRENT = new JTextField();
            INV_CURRENT.setEditable(false);
            INV_APP = new JTextField();
            INV_APP.setEditable(false);
            INV = new JTextField();
            INV.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    INV_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(INV.getText()).subtract(Cleaning.cleanNumber(INV_CURRENT.getText()))));
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    INV_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(INV.getText()).subtract(Cleaning.cleanNumber(INV_CURRENT.getText()))));
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    INV_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(INV.getText()).subtract(Cleaning.cleanNumber(INV_CURRENT.getText()))));
                }
            });
            FIAT_CURRENT = new JTextField();
            FIAT_CURRENT.setEditable(false);
            FIAT_APP = new JTextField();
            FIAT_APP.setEditable(false);
            FIAT = new JTextField();
            FIAT.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    FIAT_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(FIAT.getText()).subtract(Cleaning.cleanNumber(FIAT_CURRENT.getText()))));
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    FIAT_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(FIAT.getText()).subtract(Cleaning.cleanNumber(FIAT_CURRENT.getText()))));
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    FIAT_APP.setText(CURRENT_INSTANCE.main.encode(Cleaning.cleanNumber(FIAT.getText()).subtract(Cleaning.cleanNumber(FIAT_CURRENT.getText()))));
                }
            });
            DATE.getDocument().addDocumentListener(new DocumentListener() {
                private void sharedAction() {
                    try {
                        if (!DATE.getText().isEmpty()) {
                            LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
                            STOCK_CURRENT.setText(CURRENT_INSTANCE.main.encode(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.stockName, date)));
                            CRYPTO_CURRENT.setText(CURRENT_INSTANCE.main.encode(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.cryptoName, date)));
                            INV_CURRENT.setText(CURRENT_INSTANCE.main.encode(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.inventoryName, date)));
                            FIAT_CURRENT.setText(CURRENT_INSTANCE.main.encode(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.fiatName, date)));
                        }
                    } catch (ValidationFailedException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Not a valid date: " + DATE.getText());
                    }
                }

                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    sharedAction();
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    sharedAction();
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    sharedAction();
                }
            });
            JButton save = DendroFactory.getButton("Save");
            save.addActionListener(event -> saveAction());
            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
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
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                                ).addGroup(
                                                        main.createSequentialGroup().addGroup(
                                                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                                        STOCK_CURRENT, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        CRYPTO_CURRENT, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        INV_CURRENT, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        FIAT_CURRENT, width, width, Short.MAX_VALUE
                                                                )
                                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                                        STOCK, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        CRYPTO, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        INV, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        FIAT, width, width, Short.MAX_VALUE
                                                                )
                                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                                                        STOCK_APP, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        CRYPTO_APP, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        INV_APP, width, width, Short.MAX_VALUE
                                                                ).addComponent(
                                                                        FIAT_APP, width, width, Short.MAX_VALUE
                                                                )
                                                        )
                                                )
                                        )
                                ).addComponent(
                                        save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        DATE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        STOCK_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        STOCK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        STOCK_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        CRYPTO_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        CRYPTO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        CRYPTO_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        INV_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        INV, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        INV_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        FIAT_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        FIAT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        FIAT_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            LDate date = LDate.now(CURRENT_INSTANCE);
            date = new LDate(date.getYear(), date.lastMonth(), LDate.lastDay(date.getYear(), date.lastMonth(), CURRENT_INSTANCE), 23, 59, 59, CURRENT_INSTANCE);
            DATE.setText(date.toString());
            STOCK.setText(STOCK_CURRENT.getText());
            CRYPTO.setText(CRYPTO_CURRENT.getText());
            INV.setText(INV_CURRENT.getText());
            FIAT.setText(FIAT_CURRENT.getText());
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void saveAction() {
        BigDecimal x = BigDecimal.ZERO;
        BigDecimal stock = Cleaning.cleanNumber(STOCK_APP.getText());
        BigDecimal crypto = Cleaning.cleanNumber(CRYPTO_APP.getText());
        BigDecimal inventory = Cleaning.cleanNumber(INV_APP.getText());
        BigDecimal fiat = Cleaning.cleanNumber(FIAT_APP.getText());
        x = x.add(stock).add(crypto).add(inventory).add(fiat);
        try {
            LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            String accs = (stock.compareTo(BigDecimal.ZERO) == 0 ? "" : stock.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.stockName + "(" + stock + "), " : "C!" + Account.stockName + "(" + stock.abs() + "), ")
                    + (crypto.compareTo(BigDecimal.ZERO) == 0 ? "" : crypto.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.cryptoName + "(" + crypto + "), " : "C!" + Account.cryptoName + "(" + crypto.abs() + "), ")
                    + (inventory.compareTo(BigDecimal.ZERO) == 0 ? "" : inventory.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.inventoryName + "(" + inventory + "), " : "C!" + Account.inventoryName + "(" + inventory.abs() + "), ")
                    + (fiat.compareTo(BigDecimal.ZERO) == 0 ? "" : fiat.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.fiatName + "(" + fiat + "), " : "C!" + Account.fiatName + "(" + fiat.abs() + "), ");
            if (x.compareTo(BigDecimal.ZERO) >= 0) {
                capsule.insert(
                        date,
                        "ACC",
                        "",
                        Account.appreciationName + " (" + date.getYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + ")",
                        new LAccountSet("C!" + Account.appreciationName + "(" + x + "), " + accs, CURRENT_INSTANCE)
                );
            } else {
                capsule.insert(
                        date,
                        "ACC",
                        "",
                        Account.depreciationName + " (" + date.getYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + ")",
                        new LAccountSet("D!" + Account.depreciationName + "(" + x.abs() + "), " + accs, CURRENT_INSTANCE)
                );
            }
            CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
            CALLER.updateTable();
            dispose();
        } catch (ValidationFailedException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Not a valid date: " + DATE.getText());
        }
    }
}
