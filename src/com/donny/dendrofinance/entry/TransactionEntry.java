package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.data.ImportHandler;
import com.donny.dendrofinance.entry.meta.*;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TransactionEntry extends Entry implements Comparable<TransactionEntry> {
    private LDate date;
    private String entity, items, description;
    private LAccountSet accounts;
    private JsonObject metadata;

    public TransactionEntry(Instance curInst) {
        super(curInst);
        date = LDate.now(curInst);
        entity = "UNKNOWN";
        items = "";
        description = "";
        accounts = new LAccountSet(curInst);
        metadata = new JsonObject();
    }

    public TransactionEntry(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) {
        super(obj, mode, curInst);
        date = new LDate(obj.getDecimal("date"), curInst);
        entity = obj.getString("entity").getString();
        items = obj.getString("items").getString();
        description = obj.getString("description").getString();
        accounts = new LAccountSet(obj.getArray("accounts"), curInst);
        metadata = obj.getObject("meta-data");
    }

    public void insert(LDate date, String ent, String itm, String desc,
                       LAccountSet accounts
    ) {
        accounts.sort();
        this.date = date;
        entity = ent;
        items = itm;
        description = desc;
        this.accounts = accounts;
    }

    public JsonItem getMeta(String key) {
        return metadata.get(key);
    }

    public JsonObject getMeta() {
        return metadata;
    }

    public void setMeta(JsonObject newMeta) {
        metadata = newMeta;
    }

    public ArrayList<String> metaList() {
        return new ArrayList<>(metadata.getFields());
    }

    public boolean hasMeta() {
        return !metadata.getFields().isEmpty();
    }

    public boolean hasMeta(String key) {
        return metadata.containsKey(key);
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
        addAssetChangeMeta(getUUID(), date, name, cur, change, count);
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
        addAssetMeta(getUUID(), date, name, desc, cur, value, count);
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
        addLoanChangeMeta(getUUID(), date, name, change);
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
        addLoanMeta(getUUID(), date, name, desc, cur, principal, rate);
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
        addLedgerMeta(getUUID(), date, from, to, fromAmount, toAmount, mainValue);
    }

    public void addCheckMeta(long uuid, LDate cashed, String checkNum, BigDecimal value) {
        ArrayList<CheckMetadata> meta = getCheckMetadata();
        meta.add(new CheckMetadata(uuid, date, cashed, checkNum, value, CURRENT_INSTANCE));
        setCheckMeta(meta);
    }

    public ArrayList<AssetMetadata> getAssetMeta() {
        if (hasMeta("asset")) {
            ArrayList<AssetMetadata> assets = new ArrayList<>();
            for (JsonObject obj : metadata.getArray("asset").getObjectArray()) {
                assets.add(new AssetMetadata(getUUID(), date, obj, CURRENT_INSTANCE));
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
        metadata.put("asset", array);
    }

    public ArrayList<AssetChangeMetadata> getAssetChangeMeta() {
        if (hasMeta("asset-change")) {
            ArrayList<AssetChangeMetadata> assets = new ArrayList<>();
            for (JsonObject obj : metadata.getArray("asset-change").getObjectArray()) {
                assets.add(new AssetChangeMetadata(getUUID(), date, obj, CURRENT_INSTANCE));
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
        metadata.put("asset-change", array);
    }

    public ArrayList<LoanMetadata> getLoanMeta() {
        if (hasMeta("loan")) {
            ArrayList<LoanMetadata> assets = new ArrayList<>();
            for (JsonObject obj : metadata.getArray("loan").getObjectArray()) {
                assets.add(new LoanMetadata(getUUID(), date, obj, CURRENT_INSTANCE));
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
        metadata.put("loan", array);
    }

    public ArrayList<LoanChangeMetadata> getLoanChangeMeta() {
        if (hasMeta("loan-change")) {
            ArrayList<LoanChangeMetadata> assets = new ArrayList<>();
            for (JsonObject obj : metadata.getArray("loan-change").getObjectArray()) {
                assets.add(new LoanChangeMetadata(getUUID(), date, obj, CURRENT_INSTANCE));
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
        metadata.put("loan-change", array);
    }

    public ArrayList<LedgerMetadata> getLedgerMeta() {
        if (hasMeta("ledger")) {
            ArrayList<LedgerMetadata> ledgers = new ArrayList<>();
            for (JsonObject obj : metadata.getArray("ledger").getObjectArray()) {
                ledgers.add(new LedgerMetadata(getUUID(), date, obj, CURRENT_INSTANCE));
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
        metadata.put("ledger", array);
    }

    public ArrayList<CheckMetadata> getCheckMetadata() {
        if (hasMeta("check")) {
            ArrayList<CheckMetadata> checks = new ArrayList<>();
            for (JsonObject obj : metadata.getArray("check").getObjectArray()) {
                checks.add(new CheckMetadata(getUUID(), date, obj, CURRENT_INSTANCE));
            }
            return checks;
        } else {
            return new ArrayList<>();
        }
    }

    public void setCheckMeta(ArrayList<CheckMetadata> list) {
        JsonArray array = new JsonArray();
        for (CheckMetadata check : list) {
            try {
                array.add(check.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The universe has broken\n" + check.REF + ": " + check);
            }
        }
        metadata.put("check", array);
    }

    public LDate getDate() {
        return date;
    }

    public String getEntity() {
        return entity;
    }

    public String getItems() {
        return items;
    }

    public String getDescription() {
        return description;
    }

    public LAccountSet getAccounts() {
        return accounts;
    }

    public void addAccount(String column, Account a, BigDecimal d) {
        accounts.add(new AccountWrapper(a, column, d));
    }

    public void deleteAccount(int i) {
        accounts.remove(i);
    }

    public boolean hasBudgetAccounts() {
        for (AccountWrapper aw : accounts) {
            if (aw.ACCOUNT.getBudgetType() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGhostAccounts() {
        for (AccountWrapper aw : accounts) {
            if (aw.ACCOUNT.getBroadAccountType() == BroadAccountType.GHOST) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTrackingAccounts() {
        for (AccountWrapper aw : accounts) {
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
        for (AccountWrapper wrapper : accounts) {
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

    private String getFlags() {
        String flags = "";
        if (hasMeta()) {
            if (hasMeta("loan")) {
                flags += "L";
            }
            if (hasMeta("loan-change")) {
                flags += "l";
            }
            if (hasMeta("asset")) {
                flags += "A";
            }
            if (hasMeta("asset-change")) {
                flags += "a";
            }
            if (hasMeta("ledger")) {
                flags += "T";
            }
            if (hasMeta("check")) {
                flags += "C";
            }
        }
        return flags;
    }

    public ArrayList<String[]> display() {
        ArrayList<String[]> layers = new ArrayList<>();
        for (int i = 0; i < accounts.getSize(); i++) {
            AccountWrapper wrapper = accounts.get(i);
            switch (wrapper.COLUMN) {
                case DEBIT -> {
                    if (i == 0) {
                        layers.add(new String[]{
                                "" + getUUID(), date.toString(), entity, items, description,
                                wrapper.ACCOUNT.getName(), wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE),
                                "", "", "", getFlags()
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
                                "" + getUUID(), date.toString(), entity, items, description,
                                wrapper.ACCOUNT.getName(), "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE),
                                "", "", getFlags()
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
                                "" + getUUID(), date.toString(), entity, items, description,
                                wrapper.ACCOUNT.getName(), "", "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE), "", getFlags()
                        });
                    } else {
                        layers.add(new String[]{
                                "", "", "", "", "",
                                wrapper.ACCOUNT.getName(), "", "", wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE), ""
                        });
                    }
                }
                case TRACKER -> {
                    if (i == 0) {
                        layers.add(new String[]{
                                "" + getUUID(), date.toString(), entity, items, description,
                                wrapper.ACCOUNT.getName(), "", "", "",
                                wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE), getFlags()
                        });
                    } else {
                        layers.add(new String[]{
                                "", "", "", "", "",
                                wrapper.ACCOUNT.getName(), "", "", "",
                                wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE)
                        });
                    }
                }
            }
        }
        return layers;
    }

    @Override
    public int compareTo(TransactionEntry entry) {
        return date.compareTo(entry.date);
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = super.export();
        obj.put("date", date.export());
        obj.put("entity", new JsonString(entity));
        obj.put("items", new JsonString(items));
        obj.put("description", new JsonString(description));
        obj.put("accounts", accounts.export());
        obj.put("meta-data", metadata);
        return obj;
    }

    @Override
    public String toFlatString() {
        return getUUID() + "\t" + date + "\t" + entity + "\t" + items + "\t" + description + "\t" +
                accounts.toString() + "\t" + metadata.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("UUID: ").append(getUUID())
                .append("\n\nDate: ").append(date)
                .append("\n\nEntity: ").append(entity)
                .append("\n\nItems: ").append(items)
                .append("\n\nDescription: ").append(description)
                .append("\n\nAccounts:");
        for (AccountWrapper wrapper : accounts) {
            sb.append("\n").append(wrapper.ACCOUNT.getName()).append(" (")
                    .append(wrapper.COLUMN.toString()).append("): ");
            sb.append(wrapper.ACCOUNT.getCurrency().encode(wrapper.VALUE));
        }
        sb.append("\n\nMetadata:");
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
        if (hasMeta("check")) {
            sb.append("\n\nCheck Metadata:");
            for (CheckMetadata meta : getCheckMetadata()) {
                sb.append("\n").append(meta.toString());
            }
        }
        return sb.toString();
    }
}
