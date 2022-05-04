package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.meta.*;
import com.donny.dendrofinance.header.TransactionHeader;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.types.LString;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TransactionEntry extends Entry<TransactionHeader> implements Comparable<TransactionEntry> {

    public TransactionEntry(Instance curInst) {
        super(curInst, "TransactionHeader");
    }

    public TransactionEntry(JsonObject obj, Instance curInst) {
        super(obj, curInst);
    }

    public void insert(LDate date, LString ent, LString itm, LString desc,
                       LAccountSet accounts
    ) {
        accounts.sort();
        insertIntoField("date", date);
        insertIntoField("entity", ent);
        insertIntoField("items", itm);
        insertIntoField("description", desc);
        insertIntoField("accounts", accounts);
    }

    public JsonItem getMeta(String key) {
        return getJson("meta-data").OBJECT.get(key);
    }

    public JsonObject getMeta() {
        return getJson("meta-data").OBJECT;
    }

    public ArrayList<String> metaList() {
        return new ArrayList<>(getJson("meta-data").OBJECT.getFields());
    }

    public boolean hasMeta() {
        return !getJson("meta-data").OBJECT.getFields().isEmpty();
    }

    public boolean hasMeta(String key) {
        return getJson("meta-data").OBJECT.containsKey(key);
    }

    public void addAssetChangeMeta(long uuid, LDate date, String name, LCurrency cur, BigDecimal change, BigDecimal count) {
        ArrayList<AssetChangeMetadata> meta = getAssetChangeMeta();
        meta.add(new AssetChangeMetadata(uuid, date, name, cur, change, count));
        setAssetChangeMeta(meta);
    }

    public void addAssetChangeMeta(LDate date, String name, LCurrency cur, BigDecimal change, BigDecimal count) {
        addAssetChangeMeta(getUUID(), date, name, cur, change, count);
    }

    public void addAssetChangeMeta(String name, LCurrency cur, BigDecimal change, BigDecimal count) {
        addAssetChangeMeta(getUUID(), getDate(), name, cur, change, count);
    }

    public void addAssetMeta(long uuid, LDate date, String name, String desc, LCurrency cur, BigDecimal value, BigDecimal count) {
        ArrayList<AssetMetadata> meta = getAssetMeta();
        meta.add(new AssetMetadata(uuid, date, name, desc, cur, value, count));
        setAssetMeta(meta);
    }

    public void addAssetMeta(LDate date, String name, String desc, LCurrency cur, BigDecimal value, BigDecimal count) {
        addAssetMeta(getUUID(), date, name, desc, cur, value, count);
    }

    public void addAssetMeta(String name, String desc, LCurrency cur, BigDecimal value, BigDecimal count) {
        addAssetMeta(getUUID(), getDate(), name, desc, cur, value, count);
    }

    public void addLoanChangeMeta(long uuid, LDate date, String name, BigDecimal change) {
        ArrayList<LoanChangeMetadata> meta = getLoanChangeMeta();
        meta.add(new LoanChangeMetadata(uuid, date, name, change));
        setLoanChangeMeta(meta);
    }

    public void addLoanChangeMeta(LDate date, String name, BigDecimal change) {
        addLoanChangeMeta(getUUID(), date, name, change);
    }

    public void addLoanChangeMeta(String name, BigDecimal change) {
        addLoanChangeMeta(getUUID(), getDate(), name, change);
    }

    public void addLoanMeta(long uuid, LDate date, String name, String desc, LCurrency cur, BigDecimal principal, BigDecimal rate) {
        ArrayList<LoanMetadata> meta = getLoanMeta();
        meta.add(new LoanMetadata(uuid, date, name, desc, cur, principal, rate, CURRENT_INSTANCE));
        setLoanMeta(meta);
    }

    public void addLoanMeta(LDate date, String name, String desc, LCurrency cur, BigDecimal principal, BigDecimal rate) {
        addLoanMeta(getUUID(), date, name, desc, cur, principal, rate);
    }

    public void addLoanMeta(String name, String desc, LCurrency cur, BigDecimal principal, BigDecimal rate) {
        addLoanMeta(getUUID(), getDate(), name, desc, cur, principal, rate);
    }

    public void addLedgerMeta(long uuid, LDate date, LCurrency from, LCurrency to, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal mainValue) {
        ArrayList<LedgerMetadata> meta = getLedgerMeta();
        meta.add(new LedgerMetadata(uuid, date, from, to, fromAmount, toAmount, mainValue, CURRENT_INSTANCE));
        setLedgerMeta(meta);
    }

    public void addLedgerMeta(LDate date, LCurrency from, LCurrency to, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal mainValue) {
        addLedgerMeta(getUUID(), date, from, to, fromAmount, toAmount, mainValue);
    }

    public void addLedgerMeta(LCurrency from, LCurrency to, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal mainValue) {
        addLedgerMeta(getUUID(), getDate(), from, to, fromAmount, toAmount, mainValue);
    }

    public ArrayList<AssetMetadata> getAssetMeta() {
        if (hasMeta("asset")) {
            ArrayList<AssetMetadata> assets = new ArrayList<>();
            JsonArray array = (JsonArray) getMeta("asset");
            for (JsonObject obj : array.getObjectArray()) {
                assets.add(new AssetMetadata(getUUID(), getDate(), obj, CURRENT_INSTANCE));
            }
            return assets;
        } else {
            return new ArrayList<>();
        }
    }

    public void setAssetMeta(ArrayList<AssetMetadata> list) {
        JsonArray array = new JsonArray();
        for (AssetMetadata asset : list) {
            try {
                array.add(asset.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The universe has broken\n" + asset.NAME);
            }
        }
        getJson("meta-data").OBJECT.put("asset", array);
    }

    public ArrayList<AssetChangeMetadata> getAssetChangeMeta() {
        if (hasMeta("asset-change")) {
            ArrayList<AssetChangeMetadata> assets = new ArrayList<>();
            JsonArray array = (JsonArray) getMeta("asset-change");
            for (JsonObject obj : array.getObjectArray()) {
                assets.add(new AssetChangeMetadata(getUUID(), getDate(), obj, CURRENT_INSTANCE));
            }
            return assets;
        } else {
            return new ArrayList<>();
        }
    }

    public void setAssetChangeMeta(ArrayList<AssetChangeMetadata> list) {
        JsonArray array = new JsonArray();
        for (AssetChangeMetadata asset : list) {
            try {
                array.add(asset.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The universe has broken\n" + asset.NAME);
            }
        }
        getJson("meta-data").OBJECT.put("asset-change", array);
    }

    public ArrayList<LoanMetadata> getLoanMeta() {
        if (hasMeta("loan")) {
            ArrayList<LoanMetadata> assets = new ArrayList<>();
            JsonArray array = (JsonArray) getMeta("loan");
            for (JsonObject obj : array.getObjectArray()) {
                assets.add(new LoanMetadata(getUUID(), getDate(), obj, CURRENT_INSTANCE));
            }
            return assets;
        } else {
            return new ArrayList<>();
        }
    }

    public void setLoanMeta(ArrayList<LoanMetadata> list) {
        JsonArray array = new JsonArray();
        for (LoanMetadata loan : list) {
            try {
                array.add(loan.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The universe has broken\n" + loan.NAME);
            }
        }
        getJson("meta-data").OBJECT.put("loan", array);
    }

    public ArrayList<LoanChangeMetadata> getLoanChangeMeta() {
        if (hasMeta("loan-change")) {
            ArrayList<LoanChangeMetadata> assets = new ArrayList<>();
            JsonArray array = (JsonArray) getMeta("loan-change");
            for (JsonObject obj : array.getObjectArray()) {
                assets.add(new LoanChangeMetadata(getUUID(), getDate(), obj, CURRENT_INSTANCE));
            }
            return assets;
        } else {
            return new ArrayList<>();
        }
    }

    public void setLoanChangeMeta(ArrayList<LoanChangeMetadata> list) {
        JsonArray array = new JsonArray();
        for (LoanChangeMetadata loan : list) {
            try {
                array.add(loan.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The universe has broken\n" + loan.NAME);
            }
        }
        getJson("meta-data").OBJECT.put("loan-change", array);
    }

    public ArrayList<LedgerMetadata> getLedgerMeta() {
        if (hasMeta("ledger")) {
            ArrayList<LedgerMetadata> ledgers = new ArrayList<>();
            JsonArray array = (JsonArray) getMeta("ledger");
            for (JsonObject obj : array.getObjectArray()) {
                ledgers.add(new LedgerMetadata(getUUID(), getDate(), obj, CURRENT_INSTANCE));
            }
            return ledgers;
        } else {
            return new ArrayList<>();
        }
    }

    public void setLedgerMeta(ArrayList<LedgerMetadata> list) {
        JsonArray array = new JsonArray();
        for (LedgerMetadata ledger : list) {
            try {
                array.add(ledger.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The universe has broken\n" + ledger.UUID + ": " + ledger);
            }
        }
        getJson("meta-data").OBJECT.put("ledger", array);
    }

    public LDate getDate() {
        return getDate("date");
    }

    public String getEntity() {
        return getString("entity").VALUE;
    }

    public String getItems() {
        return getString("items").VALUE;
    }

    public String getDescription() {
        return getString("description").VALUE;
    }

    public LAccountSet getAccounts() {
        return getAccountSet("accounts");
    }

    public void addAccount(String column, Account a, BigDecimal d) {
        getAccounts().add(new AccountWrapper(a, column, d));
    }

    public void deleteAccount(int i) {
        getAccounts().remove(i);
    }

    public boolean hasBudgetAccounts() {
        for (AccountWrapper aw : getAccounts()) {
            if (aw.ACCOUNT.getBudgetType() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGhostAccounts() {
        for (AccountWrapper aw : getAccounts()) {
            if (aw.ACCOUNT.getBroadAccountType() == BroadAccountType.GHOST) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTrackingAccounts() {
        for (AccountWrapper aw : getAccounts()) {
            if (aw.ACCOUNT.getBroadAccountType() == BroadAccountType.TRACKING) {
                return true;
            }
        }
        return false;
    }

    public boolean isBalanced() {
        BigDecimal cred = BigDecimal.ZERO, deb = BigDecimal.ZERO,
                ass = BigDecimal.ZERO, lia = BigDecimal.ZERO, equ = BigDecimal.ZERO;
        boolean corCol = true;
        for (AccountWrapper wrapper : getAccounts()) {
            if (wrapper.COLUMN == AWColumn.CREDIT) {
                cred = cred.add(wrapper.VALUE);
            } else if (wrapper.COLUMN == AWColumn.DEBIT) {
                deb = deb.add(wrapper.VALUE);
            }
            switch (wrapper.ACCOUNT.getBroadAccountType()) {
                case ASSET -> ass = ass.add(wrapper.getAlphaProcessed());
                case LIABILITY -> lia = lia.add(wrapper.getAlphaProcessed());
                case EQUITY_MINUS, EXPENSE -> equ = equ.subtract(wrapper.getAlphaProcessed());
                case EQUITY_PLUS, REVENUE -> equ = equ.add(wrapper.getAlphaProcessed());
            }
            switch (wrapper.COLUMN) {
                case TRACKER -> {
                    if (wrapper.ACCOUNT.getBroadAccountType() != BroadAccountType.TRACKING) {
                        corCol = false;
                    }
                }
                case GHOST -> {
                    if (wrapper.ACCOUNT.getBroadAccountType() != BroadAccountType.GHOST) {
                        corCol = false;
                    }
                }
                case CREDIT, DEBIT -> {
                    if (wrapper.ACCOUNT.getBroadAccountType() == BroadAccountType.GHOST || wrapper.ACCOUNT.getBroadAccountType() == BroadAccountType.TRACKING) {
                        corCol = false;
                    }
                }
            }
        }
        return cred.compareTo(deb) == 0 && ass.compareTo(lia.add(equ)) == 0 && corCol;
    }

    public ArrayList<String[]> display() {
        ArrayList<String[]> layers = new ArrayList<>();
        for (int i = 0; i < getAccounts().getSize(); i++) {
            AccountWrapper wrapper = getAccounts().get(i);
            switch (wrapper.COLUMN) {
                case DEBIT -> {
                    if (i == 0) {
                        layers.add(new String[]{
                                getUUID() + "", getDate().toString(), getEntity(), getItems(), getDescription(),
                                wrapper.ACCOUNT.getName(), wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE),
                                "", "", ""
                        });
                    } else {
                        layers.add(new String[]{
                                "", "", "", "", "",
                                wrapper.ACCOUNT.getName(), wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE),
                                "", "", ""
                        });
                    }
                }
                case CREDIT -> {
                    if (i == 0) {
                        layers.add(new String[]{
                                getUUID() + "", getDate().toString(), getEntity(), getItems(), getDescription(),
                                wrapper.ACCOUNT.getName(), "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE),
                                "", ""
                        });
                    } else {
                        layers.add(new String[]{
                                "", "", "", "", "",
                                wrapper.ACCOUNT.getName(), "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE),
                                "", ""
                        });
                    }
                }
                case GHOST -> {
                    if (i == 0) {
                        layers.add(new String[]{
                                getUUID() + "", getDate().toString(), getEntity(), getItems(), getDescription(),
                                wrapper.ACCOUNT.getName(), "", "", "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE)
                        });
                    } else {
                        layers.add(new String[]{
                                "", "", "", "", "",
                                wrapper.ACCOUNT.getName(), "", "", "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE)
                        });
                    }
                }
                case TRACKER -> {
                    if (i == 0) {
                        layers.add(new String[]{
                                getUUID() + "", getDate().toString(), getEntity(), getItems(), getDescription(),
                                wrapper.ACCOUNT.getName(), "", "",
                                wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE), ""
                        });
                    } else {
                        layers.add(new String[]{
                                "", "", "", "", "",
                                wrapper.ACCOUNT.getName(), "", "",
                                wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE), ""
                        });
                    }
                }
            }
        }
        return layers;
    }

    @Override
    public int compareTo(TransactionEntry entry) {
        return getDate().compare(entry.getDate());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UUID: ").append(getUUID())
                .append("\n\nDate: ").append(getDate())
                .append("\n\nEntity: ").append(getEntity())
                .append("\n\nItems: ").append(getItems())
                .append("\n\nDescription: ").append(getDescription())
                .append("\n\nAccounts:");
        for (AccountWrapper wrapper : getAccounts()) {
            sb.append("\n").append(wrapper.ACCOUNT.getName()).append(" (")
                    .append(wrapper.COLUMN.toString()).append("): ");
            sb.append(wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE));
        }
        sb.append("\n\nMetaData:");
        if (hasMeta("loan")) {
            sb.append("\n\nLoan Metadata:");
            for (LoanMetadata meta : getLoanMeta()) {
                sb.append("\n").append(meta.display());
            }
        }
        if (hasMeta("loan-change")) {
            sb.append("\n\nLoan Change Metadata:");
            for (LoanChangeMetadata meta : getLoanChangeMeta()) {
                sb.append("\n").append(meta.print());
            }
        }
        if (hasMeta("asset")) {
            sb.append("\n\nAsset Metadata:");
            for (AssetMetadata meta : getAssetMeta()) {
                sb.append("\n").append(meta.display());
            }
        }
        if (hasMeta("asset-change")) {
            sb.append("\n\nAsset Change Metadata:");
            for (AssetChangeMetadata meta : getAssetChangeMeta()) {
                sb.append("\n").append(meta.print());
            }
        }
        if (hasMeta("ledger")) {
            sb.append("\n\nTrading Metadata:");
            for (LedgerMetadata meta : getLedgerMeta()) {
                sb.append("\n").append(meta.toString());
            }
        }
        return sb.toString();
    }
}
