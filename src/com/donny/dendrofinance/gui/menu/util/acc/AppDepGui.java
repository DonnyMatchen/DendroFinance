package com.donny.dendrofinance.gui.menu.util.acc;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;

public class AppDepGui extends RegisterFrame {
    private final JTextField DATE, STOCK, STOCK_CURRENT, STOCK_APP, CRYPTO, CRYPTO_CURRENT, CRYPTO_APP, INV, INV_CURRENT, INV_APP, FIAT, FIAT_CURRENT, FIAT_APP;

    public AppDepGui(MainGui caller, Instance curInst) {
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
            DATE.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    try {
                        LDate date = new LDate(DATE.getText(), CURRENT_INSTANCE);
                        STOCK_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.stockName, date).toString());
                        CRYPTO_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.cryptoName, date).toString());
                        INV_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.inventoryName, date).toString());
                        FIAT_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf(Account.fiatName, date).toString());
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {

                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {

                }
            });
            STOCK = new JTextField();
            STOCK.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(STOCK.getText());
                        STOCK_APP.setText(x.subtract(new BigDecimal(STOCK_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(STOCK.getText());
                        STOCK_APP.setText(x.subtract(new BigDecimal(STOCK_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(STOCK.getText());
                        STOCK_APP.setText(x.subtract(new BigDecimal(STOCK_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }
            });
            STOCK_CURRENT = new JTextField();
            STOCK_CURRENT.setEditable(false);
            STOCK_APP = new JTextField();
            STOCK_APP.setEditable(false);
            CRYPTO = new JTextField();
            CRYPTO.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(CRYPTO.getText());
                        CRYPTO_APP.setText(x.subtract(new BigDecimal(CRYPTO_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(CRYPTO.getText());
                        CRYPTO_APP.setText(x.subtract(new BigDecimal(CRYPTO_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(CRYPTO.getText());
                        CRYPTO_APP.setText(x.subtract(new BigDecimal(CRYPTO_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }
            });
            CRYPTO_CURRENT = new JTextField();
            CRYPTO_CURRENT.setEditable(false);
            CRYPTO_APP = new JTextField();
            CRYPTO_APP.setEditable(false);
            INV = new JTextField();
            INV.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(INV.getText());
                        INV_APP.setText(x.subtract(new BigDecimal(INV_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(INV.getText());
                        INV_APP.setText(x.subtract(new BigDecimal(INV_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(INV.getText());
                        INV_APP.setText(x.subtract(new BigDecimal(INV_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }
            });
            INV_CURRENT = new JTextField();
            INV_CURRENT.setEditable(false);
            INV_APP = new JTextField();
            INV_APP.setEditable(false);
            FIAT = new JTextField();
            FIAT.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(FIAT.getText());
                        FIAT_APP.setText(x.subtract(new BigDecimal(FIAT_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(FIAT.getText());
                        FIAT_APP.setText(x.subtract(new BigDecimal(FIAT_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent documentEvent) {
                    try {
                        BigDecimal x = new BigDecimal(FIAT.getText());
                        FIAT_APP.setText(x.subtract(new BigDecimal(FIAT_CURRENT.getText())).toString());
                    } catch (NumberFormatException ex) {
                    }
                }
            });
            FIAT_CURRENT = new JTextField();
            FIAT_CURRENT.setEditable(false);
            FIAT_APP = new JTextField();
            FIAT_APP.setEditable(false);
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
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
    }

    private void saveAction() {
        BigDecimal x = BigDecimal.ZERO;
        BigDecimal stock = new BigDecimal(STOCK_APP.getText());
        BigDecimal crypto = new BigDecimal(CRYPTO_APP.getText());
        BigDecimal inventory = new BigDecimal(INV_APP.getText());
        BigDecimal fiat = new BigDecimal(FIAT_APP.getText());
        x = x.add(stock).add(crypto).add(inventory).add(fiat);
        LDate date = new LDate(DATE.getText(), CURRENT_INSTANCE);
        TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
        String accs = (stock.compareTo(BigDecimal.ZERO) == 0 ? "" : stock.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.stockName + "(" + stock + "), " : "C!" + Account.stockName + "(" + stock.abs() + "), ")
                + (crypto.compareTo(BigDecimal.ZERO) == 0 ? "" : crypto.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.cryptoName + "(" + crypto + "), " : "C!" + Account.cryptoName + "(" + crypto.abs() + "), ")
                + (inventory.compareTo(BigDecimal.ZERO) == 0 ? "" : inventory.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.inventoryName + "(" + inventory + "), " : "C!" + Account.inventoryName + "(" + inventory.abs() + "), ")
                + (fiat.compareTo(BigDecimal.ZERO) == 0 ? "" : fiat.compareTo(BigDecimal.ZERO) > 0 ? "D!" + Account.fiatName + "(" + fiat + "), " : "C!" + Account.fiatName + "(" + fiat.abs() + "), ");
        if (x.compareTo(BigDecimal.ZERO) >= 0) {
            entry.insert(
                    date,
                    "ACC",
                    "",
                    Account.appreciationName + " (" + date.getYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + ")",
                    new LAccountSet("C!" + Account.appreciationName + "(" + x + "), " + accs, CURRENT_INSTANCE)
            );
        } else {
            entry.insert(
                    date,
                    "ACC",
                    "",
                    Account.depreciationName + " (" + date.getYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + ")",
                    new LAccountSet("D!" + Account.depreciationName + "(" + x.abs() + "), " + accs, CURRENT_INSTANCE)
            );
        }
        CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
        CALLER.updateTable();
        dispose();
    }
}
