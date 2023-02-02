package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.currency.LCurrency;
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
    private JTextField name, sep, mBound, mhBound, attemptLimit, duration;
    private JTextArea url_, urlH_, urlM_, urlMH_, key_, path_, pathH_, pathM_, pathMH_, nats_, excluded_, included_;
    private JCheckBox multi, fiat, crypto, stock, inventory;

    public MarketApiEditGui(BackingTableGui<LMarketApi> caller, BackingTableCore<LMarketApi> core, int index, Instance curInst) {
        super(caller, core, index, curInst);
    }

    @Override
    protected void initComponents() {
        JPanel flags = new JPanel();

        multi = new JCheckBox("Multiple Search Supported");
        multi.addChangeListener(event -> {
            if (multi.isSelected()) {
                urlM_.setEditable(true);
                urlM_.setBackground(DendroFactory.CONTENT);
                urlMH_.setEditable(true);
                urlMH_.setBackground(DendroFactory.CONTENT);
                pathM_.setEditable(true);
                pathM_.setBackground(DendroFactory.CONTENT);
                pathMH_.setEditable(true);
                pathMH_.setBackground(DendroFactory.CONTENT);
                sep.setEditable(true);
                sep.setBackground(DendroFactory.CONTENT);
                mBound.setEditable(true);
                mBound.setBackground(DendroFactory.CONTENT);
                mhBound.setEditable(true);
                mhBound.setBackground(DendroFactory.CONTENT);
            } else {
                urlM_.setEditable(false);
                urlM_.setBackground(DendroFactory.DISABLED);
                urlMH_.setEditable(false);
                urlMH_.setBackground(DendroFactory.DISABLED);
                pathM_.setEditable(false);
                pathM_.setBackground(DendroFactory.DISABLED);
                pathMH_.setEditable(false);
                pathMH_.setBackground(DendroFactory.DISABLED);
                sep.setEditable(false);
                sep.setBackground(DendroFactory.DISABLED);
                mBound.setEditable(false);
                mBound.setBackground(DendroFactory.DISABLED);
                mhBound.setEditable(false);
                mhBound.setBackground(DendroFactory.DISABLED);
            }
        });

        name = new JTextField();

        JScrollPane url = DendroFactory.getLongField();
        url_ = (JTextArea) url.getViewport().getView();
        JScrollPane urlH = DendroFactory.getLongField();
        urlH_ = (JTextArea) urlH.getViewport().getView();
        JScrollPane urlM = DendroFactory.getLongField();
        urlM_ = (JTextArea) urlM.getViewport().getView();
        JScrollPane urlMH = DendroFactory.getLongField();
        urlMH_ = (JTextArea) urlMH.getViewport().getView();
        JScrollPane key = DendroFactory.getLongField();
        key_ = (JTextArea) key.getViewport().getView();
        JScrollPane path = DendroFactory.getLongField();
        path_ = (JTextArea) path.getViewport().getView();
        JScrollPane pathH = DendroFactory.getLongField();
        pathH_ = (JTextArea) pathH.getViewport().getView();
        JScrollPane pathM = DendroFactory.getLongField();
        pathM_ = (JTextArea) pathM.getViewport().getView();
        JScrollPane pathMH = DendroFactory.getLongField();
        pathMH_ = (JTextArea) pathMH.getViewport().getView();
        JScrollPane nats = DendroFactory.getLongField();
        nats_ = (JTextArea) nats.getViewport().getView();
        JScrollPane excluded = DendroFactory.getLongField();
        excluded_ = (JTextArea) excluded.getViewport().getView();
        JScrollPane included = DendroFactory.getLongField();
        included_ = (JTextArea) included.getViewport().getView();

        sep = new JTextField();
        mBound = new JTextField();
        mhBound = new JTextField();
        attemptLimit = new JTextField();
        duration = new JTextField();

        fiat = new JCheckBox("Forex");
        crypto = new JCheckBox("Cryptocurrencies");
        stock = new JCheckBox("Stocks");
        inventory = new JCheckBox("Commodity");

        JLabel a = new JLabel("Name");
        JLabel b = new JLabel("URL");
        JLabel c = new JLabel("History URL");
        JLabel d = new JLabel("Multisearch URL");
        JLabel e = new JLabel("Multisearch History URL");
        JLabel f = new JLabel("Available types");
        JLabel g = new JLabel("Parse Path");
        JLabel h = new JLabel("History Parse Path");
        JLabel i = new JLabel("Multisearch Parse Path");
        JLabel j = new JLabel("Multisearch History Parse Path");
        JLabel k = new JLabel("Naturals");
        JLabel l = new JLabel("API Key");
        JLabel m = new JLabel("Specifically Included");
        JLabel n = new JLabel("Specifically Excluded");
        JLabel o = new JLabel("Separator");
        JLabel p = new JLabel("Multisearch Bound");
        JLabel q = new JLabel("Multisearch History Bound");
        JLabel r = new JLabel("API Call Attempt Limit");
        JLabel s = new JLabel("API Wait Duration");

        JButton cancel = DendroFactory.getButton("Cancel");
        cancel.addActionListener(event -> dispose());
        JButton save = DendroFactory.getButton("Save");
        save.addActionListener(event -> saveAction());

        if (INDEX >= 0) {
            LMarketApi root = TABLE.getElement(INDEX);
            multi.setSelected(root.MULTIPLE);
            name.setText(root.NAME);
            url_.setText(root.BASE_URL);
            urlH_.setText(root.BASE_URL_HISTORY);
            urlM_.setText(root.MULTI_URL);
            urlMH_.setText(root.MULTI_URL_HISTORY);
            key_.setText(root.KEY);
            path_.setText(root.readBaseParsePath());
            pathH_.setText(root.readBaseParsePathHistory());
            pathM_.setText(root.readMultiParsePath());
            pathMH_.setText(root.readMultiParsePathHistory());
            nats_.setText(root.readNats());
            included_.setText(root.readIncluded());
            excluded_.setText(root.readExcluded());
            fiat.setSelected(root.fiatCurrencies());
            stock.setSelected(root.stocks());
            crypto.setSelected(root.cryptocurrencies());
            inventory.setSelected(root.inventories());
            sep.setText(root.SEPARATOR);
            mBound.setText("" + root.MULTI_LIMIT);
            mhBound.setText("" + root.MULTI_HIST_LIMIT);
            attemptLimit.setText("" + root.ATTEMPT_LIMIT);
            duration.setText("" + root.DURATION);
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
                            main.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
                                    multi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
                                            ).addComponent(
                                                    j, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    k, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    l, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    m, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    n, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    o, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    p, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    q, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    r, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            ).addComponent(
                                                    s, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                            )
                                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                                    name, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    url, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    urlH, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    urlM, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    urlMH, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                            ).addComponent(
                                                    path, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    pathH, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    pathM, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    pathMH, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    nats, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    key, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    included, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    excluded, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    sep, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    mBound, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    mhBound, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    attemptLimit, 250, 250, Short.MAX_VALUE
                                            ).addComponent(
                                                    duration, 250, 250, Short.MAX_VALUE
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
                    main.createSequentialGroup().addContainerGap().addComponent(
                            multi, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                    ).addGap(DendroFactory.MEDIUM_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    name, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    url, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    c, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    urlH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    d, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    urlM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    e, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    urlMH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    f, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    flags, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    g, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    path, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    h, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    pathH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    i, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    pathM, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    j, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    pathMH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    k, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    nats, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    l, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    key, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    m, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    included, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    n, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    excluded, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    o, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    sep, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    p, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    mBound, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    q, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    mhBound, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    r, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    attemptLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            )
                    ).addGap(DendroFactory.SMALL_GAP).addGroup(
                            main.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(
                                    s, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addComponent(
                                    duration, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
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
            /*
             * String name, String types, String baseUrl, String baseUrlHist, String multiUrl,
                      String multiUrlHist, String sep, String apiKey, int mLimit, int mhLimit, int attemptLimit,
                      int duration, Instance curInst
             */
            LMarketApi temp;
            if (multi.isSelected()) {
                temp = new LMarketApi(
                        Validation.validateString(name),
                        type,
                        Validation.validateString(url_),
                        Validation.validateString(urlH_),
                        Validation.validateString(urlM_),
                        Validation.validateString(urlMH_),
                        Validation.validateString(sep),
                        Validation.validateStringAllowEmpty(key_),
                        Validation.validateInteger(mBound).intValue(),
                        Validation.validateInteger(mhBound).intValue(),
                        Validation.validateInteger(attemptLimit).intValue(),
                        Validation.validateInteger(duration).intValue(),
                        true,
                        CURRENT_INSTANCE
                );
                if (!pathM_.getText().equals("")) {
                    temp.resetMultiParsePath(new ArrayList<>(Arrays.asList(Validation.validateString(pathM_).replace(" ", "").split(","))));
                }
                if (!pathMH_.getText().equals("")) {
                    temp.resetMultiParsePathHistory(new ArrayList<>(Arrays.asList(Validation.validateString(pathMH_).replace(" ", "").split(","))));
                }
            } else {
                temp = new LMarketApi(
                        Validation.validateString(name),
                        type,
                        Validation.validateString(url_),
                        Validation.validateString(urlH_),
                        Validation.validateStringAllowEmpty(key_),
                        Validation.validateInteger(attemptLimit).intValue(),
                        Validation.validateInteger(duration).intValue(),
                        CURRENT_INSTANCE
                );
            }
            if (!path_.getText().equals("")) {
                temp.resetBaseParsePath(new ArrayList<>(Arrays.asList(Validation.validateString(path_).replace(" ", "").split(","))));
            }
            if (!pathH_.getText().equals("")) {
                temp.resetBaseParsePathHistory(new ArrayList<>(Arrays.asList(Validation.validateString(pathH_).replace(" ", "").split(","))));
            }
            ArrayList<LCurrency> list;
            if (!nats_.getText().equals("")) {
                list = new ArrayList<>();
                for (String s : Validation.validateString(nats_).replace(" ", "").split(",")) {
                    LCurrency c = CURRENT_INSTANCE.getLCurrency(s);
                    if (c != null) {
                        list.add(c);
                    }
                }
                temp.resetNats(new ArrayList<>(list));
            }
            if (!included_.getText().equals("")) {
                list = new ArrayList<>();
                for (String s : Validation.validateString(included_).replace(" ", "").split(",")) {
                    LCurrency c = CURRENT_INSTANCE.getLCurrency(s);
                    if (c != null) {
                        list.add(c);
                    }
                }
                temp.resetIncluded(new ArrayList<>(list));
            }
            if (!excluded_.getText().equals("")) {
                list = new ArrayList<>();
                for (String s : Validation.validateString(excluded_).replace(" ", "").split(",")) {
                    LCurrency c = CURRENT_INSTANCE.getLCurrency(s);
                    if (c != null) {
                        list.add(c);
                    }
                }
                temp.resetExcluded(list);
            }
            if (INDEX >= 0) {
                TABLE.replace(INDEX, temp);
            } else {
                TABLE.add(temp);
            }
            dispose();
        } catch (ValidationFailedException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "You did a badness!  " + ex.getMessage());
        }
    }
}
