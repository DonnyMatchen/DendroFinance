package com.donny.dendrofinance.gui.menu.util;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.entry.meta.AssetMetadata;
import com.donny.dendrofinance.entry.meta.LoanMetadata;
import com.donny.dendrofinance.entry.totals.Position;
import com.donny.dendrofinance.entry.totals.PositionElement;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.RegisterFrame;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;
import com.donny.dendrofinance.util.Curation;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;

public class ArchiveGui extends RegisterFrame {
    private final File DIR;

    private final JLabel A, B;
    private final JComboBox<String> YEAR, ARCHIVES;
    private final JButton GO, EXPORT;

    public ArchiveGui(MainGui caller, Instance curInst) {
        super(caller, "Archive", curInst);

        DIR = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Archives");

        //draw gui
        {
            A = new JLabel("Year");
            B = new JLabel("Archive");
            YEAR = new JComboBox<>();
            Curation<Integer> years = new Curation<>();
            for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                if (!entry.getEntity().equals("PRIOR")) {
                    years.add(entry.getDate().getYear());
                }
            }
            for (int i : years) {
                YEAR.addItem("" + i);
            }
            ARCHIVES = new JComboBox<>();
            File[] dirList = DIR.listFiles();
            if (dirList != null) {
                for (File f : dirList) {
                    if (f.getName().contains(".xarc")) {
                        ARCHIVES.addItem(f.getName().replace(".xarc", ""));
                    }
                }
            }
            GO = DendroFactory.getButton("Archive");
            GO.addActionListener(event -> archive());
            EXPORT = DendroFactory.getButton("Export");
            EXPORT.addActionListener(event -> export());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                                        )
                                ).addComponent(
                                        GO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addGroup(
                                        main.createSequentialGroup().addComponent(
                                                B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                                ARCHIVES, 300, 300, Short.MAX_VALUE
                                        )
                                ).addComponent(
                                        EXPORT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        YEAR, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                GO, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        B, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ARCHIVES, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                EXPORT, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
            }

            pack();
        }
    }

    private void archive() {
        if (YEAR.getSelectedItem() != null) {
            int year = Integer.parseInt((String) YEAR.getSelectedItem());
            TransactionEntry prior = new TransactionEntry(CURRENT_INSTANCE);
            ArrayList<TransactionEntry> entries = new ArrayList<>();
            for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                if (entry.getDate().getYear() <= year) {
                    entries.add(entry);
                }
            }
            Aggregation<Account> acc = new Aggregation<>();
            for (TransactionEntry entry : entries) {
                for (AccountWrapper wrapper : entry.getAccounts()) {
                    acc.add(wrapper.ACCOUNT, wrapper.getAlphaProcessed());
                }
            }
            LAccountSet aSet = new LAccountSet(CURRENT_INSTANCE);
            for (Account a : acc.keySet()) {
                aSet.add(new AccountWrapper(a, a.getDefaultColumn((acc.get(a).compareTo(BigDecimal.ZERO) >= 0)), acc.get(a)));
            }
            LDate end = new LDate(year, 12, 31, CURRENT_INSTANCE);
            ArrayList<Position> positions = CURRENT_INSTANCE.DATA_HANDLER.getPositions(end);
            prior.setLedgerMeta(new ArrayList<>());
            for (Position pos : positions) {
                for (PositionElement el : pos.ELEMENTS) {
                    prior.addLedgerMeta(el.DATE, CURRENT_INSTANCE.main, pos.ASSET, el.cost(), el.volume, el.cost());
                }
            }
            ArrayList<AssetMetadata> assets = CURRENT_INSTANCE.DATA_HANDLER.assetsAsOf(end);
            prior.setAssetMeta(new ArrayList<>());
            for (AssetMetadata ass : assets) {
                if (ass.isCurrent()) {
                    LCurrency first = ass.CURRENCY;
                    prior.addAssetMeta(ass.DATE, ass.NAME, ass.DESC, ass.CURRENCY, ass.getValues().get(ass.CURRENCY), ass.getCount().get(ass.CURRENCY));
                    for (LCurrency cur : ass.getCurrencies()) {
                        if (cur != first) {
                            prior.addAssetChangeMeta(ass.DATE, ass.NAME, cur, ass.getValues().get(cur), ass.getCount().get(cur));
                        }
                    }
                }
            }
            ArrayList<LoanMetadata> loans = CURRENT_INSTANCE.DATA_HANDLER.loansAsOf(end);
            for (LoanMetadata loan : loans) {
                if (loan.isCurrent()) {
                    prior.addLoanMeta(loan.DATE, loan.NAME, loan.DESC, loan.CUR, loan.principalRemaining(), loan.RATE);
                }
            }
            entries.remove(prior);
            LDate min = LDate.now(CURRENT_INSTANCE), max = prior.getDate();
            prior.insert(
                    new LDate(year, 12, 31, CURRENT_INSTANCE),
                    "PRIOR",
                    "",
                    "",
                    aSet
            );
            JsonArray arr = new JsonArray();
            for (TransactionEntry entry : entries) {
                CURRENT_INSTANCE.DATA_HANDLER.deleteTransaction(entry.getUUID());
                if (entry.getDate().compare(min) < 0) {
                    min = entry.getDate();
                }
                if (entry.getDate().compare(max) > 0) {
                    max = entry.getDate();
                }
                try {
                    arr.add(entry.export());
                } catch (JsonFormattingException ex) {
                    ex.printStackTrace();
                }
            }
            String name = min.toDateString().replace("/", "-") + " == " + max.toDateString().replace("/", "-") + ".xarc";
            CURRENT_INSTANCE.FILE_HANDLER.writeEncrypt(DIR, name, arr.toString());
            Curation<Integer> years = new Curation<>();
            for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                if (!entry.getEntity().equals("PRIOR")) {
                    years.add(entry.getDate().getYear());
                }
            }
            YEAR.removeAllItems();
            for (int i : years) {
                YEAR.addItem("" + i);
            }
            ARCHIVES.removeAllItems();
            File[] dirList = DIR.listFiles();
            if (dirList != null) {
                for (File f : dirList) {
                    if (f.getName().contains(".xarc")) {
                        ARCHIVES.addItem(f.getName().replace(".xarc", ""));
                    }
                }
            }
        }
    }

    private void export() {
        if (ARCHIVES.getSelectedItem() != null) {
            if (new File(DIR.getPath() + File.separator + ARCHIVES.getSelectedItem() + ".xarc").exists()) {
                try {
                    JsonItem temp = JsonItem.sanitizeDigest(CURRENT_INSTANCE.FILE_HANDLER.readDecrypt(DIR, ARCHIVES.getSelectedItem() + ".xarc"));
                    CURRENT_INSTANCE.FILE_HANDLER.write(
                            new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Exports"),
                            "Archive (" + ARCHIVES.getSelectedItem() + ").json",
                            temp.print());
                } catch (JsonFormattingException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Your archive is borked");
                    CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), ex.getMessage());
                }
            }
        }
    }
}
