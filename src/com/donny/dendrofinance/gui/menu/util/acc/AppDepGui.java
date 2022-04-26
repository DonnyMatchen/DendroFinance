package com.donny.dendrofinance.gui.menu.util.acc;

import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.types.LString;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.math.BigDecimal;

public class AppDepGui extends RegisterFrame {
    private final JTextField DATE, STOCK, STOCK_CURRENT, STOCK_APP, CRYPTO, CRYPTO_CURRENT, CRYPTO_APP, INV, INV_CURRENT, INV_APP, FIAT, FIAT_CURRENT, FIAT_APP;
    private final JLabel A, B, C, D, E;
    private final JButton SAVE;

    public AppDepGui(MainGui caller, Instance curInst) {
        super(caller, curInst);

        //draw gui
        {
            int width = 100;

            A = new JLabel("Date");
            B = new JLabel("Stocks");
            C = new JLabel("Cryptocurrencies");
            D = new JLabel("Inventories");
            E = new JLabel("Non-main Fiat");
            DATE = new JTextField();
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
            SAVE = DendroFactory.getButton("Save");
            SAVE.addActionListener(event -> {
                BigDecimal x = BigDecimal.ZERO;
                BigDecimal stck = new BigDecimal(STOCK_APP.getText());
                BigDecimal cryp = new BigDecimal(CRYPTO_APP.getText());
                BigDecimal inv = new BigDecimal(INV_APP.getText());
                BigDecimal fiat = new BigDecimal(FIAT_APP.getText());
                x = x.add(stck).add(cryp).add(inv).add(fiat);
                LDate date = new LDate(DATE.getText(), CURRENT_INSTANCE);
                TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
                String accs = (stck.compareTo(BigDecimal.ZERO) == 0 ? "" : stck.compareTo(BigDecimal.ZERO) > 0 ? "D!Stock(" + stck + "), " : "C!Stock(" + stck.abs() + "), ")
                        + (cryp.compareTo(BigDecimal.ZERO) == 0 ? "" : cryp.compareTo(BigDecimal.ZERO) > 0 ? "D!Crypto(" + cryp + "), " : "C!Crypto(" + cryp.abs() + "), ")
                        + (inv.compareTo(BigDecimal.ZERO) == 0 ? "" : inv.compareTo(BigDecimal.ZERO) > 0 ? "D!Held_Inventory(" + inv + "), " : "C!Held_Inventory(" + inv.abs() + "), ")
                        + (fiat.compareTo(BigDecimal.ZERO) == 0 ? "" : fiat.compareTo(BigDecimal.ZERO) > 0 ? "D!Other_Cash(" + fiat + "), " : "C!Other_Cash(" + fiat.abs() + "), ");
                if (x.compareTo(BigDecimal.ZERO) >= 0) {
                    entry.insert(
                            date,
                            new LString("ACC"),
                            new LString(""),
                            new LString("Appreciation (" + date.getYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + ")"),
                            new LAccountSet("C!Appreciation(" + x + "), " + accs, CURRENT_INSTANCE)
                    );
                } else {
                    entry.insert(
                            date,
                            new LString("ACC"),
                            new LString(""),
                            new LString("Depreciation (" + date.getYear() + "-" + (date.getMonth() < 10 ? "0" : "") + date.getMonth() + ")"),
                            new LAccountSet("D!Depreciation(" + x.abs() + "), " + accs, CURRENT_INSTANCE)
                    );
                }
                CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
                caller.updateTable();
                dispose();
            });

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
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
                                        SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        STOCK_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        STOCK, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        STOCK_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        C, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        CRYPTO_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        CRYPTO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        CRYPTO_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        D, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        INV_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        INV, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        INV_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        E, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        FIAT_CURRENT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        FIAT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        FIAT_APP, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.MEDIUM_GAP).addComponent(
                                SAVE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            LDate date = LDate.now(CURRENT_INSTANCE);
            date = new LDate(date.getYear(), date.getMonth(), LDate.lastDay(date.getYear(), date.getMonth(), CURRENT_INSTANCE), 23, 59, 59, CURRENT_INSTANCE);
            DATE.setText(date.toString());
            STOCK_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf("Stock", date).toString());
            CRYPTO_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf("Crypto", date).toString());
            INV_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf("Held_Inventory", date).toString());
            FIAT_CURRENT.setText(CURRENT_INSTANCE.DATA_HANDLER.accountAsOf("Other_Cash", date).toString());

            pack();
        }
    }
}