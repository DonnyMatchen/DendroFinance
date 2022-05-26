package com.donny.dendrofinance.gui.menu.util;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.math.BigDecimal;

public class ConversionGui extends RegisterFrame {
    private final JLabel A, B;
    private final JTextField DATE, AMOUNT;
    private final SearchBox CUR_A, CUR_B;
    private final JScrollPane PANE;
    private final JTextArea DISPLAY;
    private final JButton CONVERT;

    public ConversionGui(MainGui caller, Instance curInst) {
        super(caller, "Asset Conversion", curInst);

        //draw gui
        {
            A = new JLabel("Date");
            B = new JLabel("Amount");
            DATE = new JTextField();
            AMOUNT = new JTextField();
            CUR_A = new SearchBox("Currency A", CURRENT_INSTANCE.getAllAssetsAsStrings());
            CUR_B = new SearchBox("Currency B", CURRENT_INSTANCE.getAllAssetsAsStrings());
            PANE = DendroFactory.getScrollField();
            DISPLAY = (JTextArea) PANE.getViewport().getView();
            DISPLAY.setEditable(false);
            CONVERT = DendroFactory.getButton("Convert");
            CONVERT.addActionListener(event -> {
                if (!DATE.getText().equals("") && !new LDate(DATE.getText(), CURRENT_INSTANCE).toDateString().equals(LDate.now(CURRENT_INSTANCE).toDateString())) {
                    LCurrency a = CURRENT_INSTANCE.getLCurrency(CUR_A.getSelectedItem());
                    LCurrency b = CURRENT_INSTANCE.getLCurrency(CUR_B.getSelectedItem());
                    BigDecimal x = CURRENT_INSTANCE.cleanNumber(AMOUNT.getText());
                    if (x.compareTo(BigDecimal.ZERO) == 0) {
                        x = BigDecimal.ONE;
                    }
                    BigDecimal y = CURRENT_INSTANCE.convert(x, a, b, new LDate(DATE.getText(), CURRENT_INSTANCE));
                    DISPLAY.setText(a.encode(x) + "\n=\n" + b.encode(y));
                } else {
                    LCurrency a = CURRENT_INSTANCE.getLCurrency(CUR_A.getSelectedItem());
                    LCurrency b = CURRENT_INSTANCE.getLCurrency(CUR_B.getSelectedItem());
                    BigDecimal x = CURRENT_INSTANCE.cleanNumber(AMOUNT.getText());
                    if (x.compareTo(BigDecimal.ZERO) == 0) {
                        x = BigDecimal.ONE;
                    }
                    BigDecimal y = CURRENT_INSTANCE.convert(x, a, b);
                    DISPLAY.setText(a.encode(x) + "\n=\n" + b.encode(y));
                }
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
                                                )
                                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                                main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                                        DATE, 250, 250, Short.MAX_VALUE
                                                ).addComponent(
                                                        AMOUNT, 250, 250, Short.MAX_VALUE
                                                )
                                        )
                                ).addComponent(
                                        CUR_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        CUR_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                ).addComponent(
                                        CONVERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
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
                                        AMOUNT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                CUR_A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                CUR_B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                CONVERT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            pack();
        }
    }
}
