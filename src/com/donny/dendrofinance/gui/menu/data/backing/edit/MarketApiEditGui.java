package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.currency.LMarketApi;
import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MarketApiEditGui extends BackingEditGui<LMarketApi> {
    private JTextField name;
    private JScrollPane url, urlH, key, path, pathH, nats, excepts;
    private JTextArea url_, urlH_, key_, path_, pathH_, nats_, excepts_;
    private JPanel flags;
    private JCheckBox fiat, crypto, stock, inventory;
    private JLabel a, b, c, d, e, f, g, h, i;
    private JButton cancel, save;

    public MarketApiEditGui(BackingTableGui<LMarketApi> caller, BackingTableCore<LMarketApi> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        flags = new JPanel();

        name = new JTextField();

        url = DendroFactory.getLongField();
        url_ = (JTextArea) url.getViewport().getView();
        urlH = DendroFactory.getLongField();
        urlH_ = (JTextArea) urlH.getViewport().getView();
        key = DendroFactory.getLongField();
        key_ = (JTextArea) key.getViewport().getView();
        path = DendroFactory.getLongField();
        path_ = (JTextArea) path.getViewport().getView();
        pathH = DendroFactory.getLongField();
        pathH_ = (JTextArea) pathH.getViewport().getView();
        nats = DendroFactory.getLongField();
        nats_ = (JTextArea) nats.getViewport().getView();
        excepts = DendroFactory.getLongField();
        excepts_ = (JTextArea) excepts.getViewport().getView();

        fiat = new JCheckBox("Forex");
        crypto = new JCheckBox("Cryptocurrencies");
        stock = new JCheckBox("Stocks");
        inventory = new JCheckBox("Commodity");

        a = new JLabel("Name");
        b = new JLabel("URL");
        c = new JLabel("History URL");
        d = new JLabel("Available types");
        e = new JLabel("Parse Path");
        f = new JLabel("History Parse Path");
        g = new JLabel("Naturals");
        h = new JLabel("API Key");
        i = new JLabel("Exceptions");

        cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        save = DendroFactory.getButton("Save");
        save.addActionListener(event -> saveAction());

        if (INDEX >= 0) {
            LMarketApi root = TABLE.getElement(INDEX);
            name.setText(root.NAME);
            url_.setText(root.BASE_URL);
            urlH_.setText(root.BASE_URL_HISTORY);
            key_.setText(root.KEY);
            path_.setText(root.readParsePath());
            pathH_.setText(root.readParsePathHistory());
            nats_.setText(root.readNats());
            excepts_.setText(root.readExcepts());
            fiat.setSelected(root.fiatCurrencies());
            stock.setSelected(root.stocks());
            crypto.setSelected(root.cryptocurrencies());
            inventory.setSelected(root.inventories());
        }

        //group layout
        {
            //flags
            {
                GroupLayout side = new GroupLayout(flags);
                flags.setLayout(side);
                side.setHorizontalGroup(
                        side.createSequentialGroup().addGroup(
                                side.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        stock, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        fiat, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                side.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                        crypto, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        inventory, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        )
                );
                side.setVerticalGroup(
                        side.createSequentialGroup().addGroup(
                                side.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        stock, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        crypto, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                side.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        fiat, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        inventory, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        )
                );
            }

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
                                            ).addComponent(
                                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    g, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    h, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    i, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    url, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    urlH, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addComponent(
                                                    path, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    pathH, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    nats, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    key, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    excepts, 250, 250, Short.MAX_VALUE
                                            )
                                    )
                            ).addGroup(
                                    main.createSequentialGroup().addComponent(
                                            cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addGap(
                                            DendroFactory.SMALL_GAP, DendroFactory.SMALL_GAP, Short.MAX_VALUE
                                    ).addComponent(
                                            save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    ).addContainerGap()
            );
            main.setVerticalGroup(
                    main.createSequentialGroup().addContainerGap().addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    name, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    url, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    urlH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    path, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    pathH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    g, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    nats, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    h, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    key, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    i, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    excepts, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                    cancel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    save, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addContainerGap()
            );
        }
    }

    @Override
    protected void saveAction() {
        try {
            String type = "";
            if (fiat.isSelected()) {
                type += "F";
            }
            if (stock.isSelected()) {
                type += "S";
            }
            if (crypto.isSelected()) {
                type += "C";
            }
            if (inventory.isSelected()) {
                type += "I";
            }
            LMarketApi temp = new LMarketApi(
                    Validation.validateString(name),
                    type,
                    Validation.validateString(url_),
                    Validation.validateString(urlH_),
                    Validation.validateString(key_),
                    CURRENT_INSTANCE
            );
            if (!path_.getText().equals("")) {
                temp.resetParsePath(new ArrayList<>(Arrays.asList(Validation.validateString(path_).replace(" ", "").split(","))));
            }
            if (!pathH_.getText().equals("")) {
                temp.resetParsePathHistory(new ArrayList<>(Arrays.asList(Validation.validateString(pathH_).replace(" ", "").split(","))));
            }
            if (!nats_.getText().equals("")) {
                temp.resetNats(new ArrayList<>(Arrays.asList(Validation.validateString(nats_).replace(" ", "").split(","))));
            }
            if (!excepts_.getText().equals("")) {
                temp.resetExcepts(new ArrayList<>(Arrays.asList(Validation.validateString(excepts_).replace(" ", "").split(","))));
            }
            if (INDEX >= 0) {
                TABLE.replace(INDEX, temp);
            } else {
                TABLE.add(temp);
            }

            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!");
        }
    }
}
