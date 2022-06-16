package com.donny.dendrofinance.account;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.util.ExportableToJson;

import java.io.Serializable;
import java.math.BigDecimal;

public class Account implements ExportableToJson, Serializable {
    public static String portfolioName,
            stockName, inventoryName, fiatName, cryptoName,
            tradIncName, tradExpName,
            cgStockName, cgInventoryName, cgFiatName, cgCryptoName,
            clStockName, clInventoryName, clFiatName, clCryptoName,
            cgltStockName, cgltInventoryName, cgltFiatName, cgltCryptoName,
            clltStockName, clltInventoryName, clltFiatName, clltCryptoName,
            selfIncName,
            fixedAssetsTypeName, receiveTypeName,
            appreciationName, depreciationName;
    public static boolean specialAltered = false;

    public static void changeSpecial(String current, String replacement) {
        if (portfolioName.equals(current)) {
            portfolioName = replacement;
            specialAltered = true;
        }
        if (stockName.equals(current)) {
            stockName = replacement;
            specialAltered = true;
        }
        if (inventoryName.equals(current)) {
            inventoryName = replacement;
            specialAltered = true;
        }
        if (fiatName.equals(current)) {
            fiatName = replacement;
            specialAltered = true;
        }
        if (cryptoName.equals(current)) {
            cryptoName = replacement;
            specialAltered = true;
        }
        if (tradIncName.equals(current)) {
            tradIncName = replacement;
            specialAltered = true;
        }
        if (tradExpName.equals(current)) {
            tradExpName = replacement;
            specialAltered = true;
        }
        if (cgStockName.equals(current)) {
            cgStockName = replacement;
            specialAltered = true;
        }
        if (cgInventoryName.equals(current)) {
            cgInventoryName = replacement;
            specialAltered = true;
        }
        if (cgFiatName.equals(current)) {
            cgFiatName = replacement;
            specialAltered = true;
        }
        if (cgCryptoName.equals(current)) {
            cgCryptoName = replacement;
            specialAltered = true;
        }
        if (clStockName.equals(current)) {
            clStockName = replacement;
            specialAltered = true;
        }
        if (clInventoryName.equals(current)) {
            clInventoryName = replacement;
            specialAltered = true;
        }
        if (clFiatName.equals(current)) {
            clFiatName = replacement;
            specialAltered = true;
        }
        if (clCryptoName.equals(current)) {
            clCryptoName = replacement;
            specialAltered = true;
        }
        if (cgltStockName.equals(current)) {
            cgltStockName = replacement;
            specialAltered = true;
        }
        if (cgltInventoryName.equals(current)) {
            cgltInventoryName = replacement;
            specialAltered = true;
        }
        if (cgltFiatName.equals(current)) {
            cgltFiatName = replacement;
            specialAltered = true;
        }
        if (cgltCryptoName.equals(current)) {
            cgltCryptoName = replacement;
            specialAltered = true;
        }
        if (clltStockName.equals(current)) {
            clltStockName = replacement;
            specialAltered = true;
        }
        if (clltInventoryName.equals(current)) {
            clltInventoryName = replacement;
            specialAltered = true;
        }
        if (clltFiatName.equals(current)) {
            clltFiatName = replacement;
            specialAltered = true;
        }
        if (clltCryptoName.equals(current)) {
            clltCryptoName = replacement;
            specialAltered = true;
        }
        if (selfIncName.equals(current)) {
            selfIncName = replacement;
            specialAltered = true;
        }
        if (fixedAssetsTypeName.equals(current)) {
            fixedAssetsTypeName = replacement;
            specialAltered = true;
        }
        if (receiveTypeName.equals(current)) {
            receiveTypeName = replacement;
            specialAltered = true;
        }
        if (appreciationName.equals(current)) {
            appreciationName = replacement;
            specialAltered = true;
        }
        if (depreciationName.equals(current)) {
            depreciationName = replacement;
            specialAltered = true;
        }
    }

    public static JsonObject specialExport() throws JsonFormattingException {
        JsonObject object = new JsonObject();
        object.put("portfolio", new JsonString(portfolioName));
        object.put("stock", new JsonString(stockName));
        object.put("inventory", new JsonString(inventoryName));
        object.put("fiat", new JsonString(fiatName));
        object.put("crypto", new JsonString(cryptoName));
        object.put("trading-income", new JsonString(tradIncName));
        object.put("trading-expense", new JsonString(tradExpName));
        object.put("stock-gain", new JsonString(cgStockName));
        object.put("inventory-gain", new JsonString(cgInventoryName));
        object.put("fiat-gain", new JsonString(cgFiatName));
        object.put("crypto-gain", new JsonString(cgCryptoName));
        object.put("stock-loss", new JsonString(clStockName));
        object.put("inventory-loss", new JsonString(clInventoryName));
        object.put("fiat-loss", new JsonString(clFiatName));
        object.put("crypto-loss", new JsonString(clCryptoName));
        object.put("stock-gain-long-run", new JsonString(cgltStockName));
        object.put("inventory-gain-long-run", new JsonString(cgltInventoryName));
        object.put("fiat-gain-long-run", new JsonString(cgltFiatName));
        object.put("crypto-gain-long-run", new JsonString(cgltCryptoName));
        object.put("stock-loss-long-run", new JsonString(clltStockName));
        object.put("inventory-loss-long-run", new JsonString(clltInventoryName));
        object.put("fiat-loss-long-run", new JsonString(clltFiatName));
        object.put("crypto-loss-long-run", new JsonString(clltCryptoName));
        object.put("self-employment-income", new JsonString(selfIncName));
        object.put("fixed-assets-type", new JsonString(fixedAssetsTypeName));
        object.put("receivables-type", new JsonString(receiveTypeName));
        object.put("appreciation", new JsonString(appreciationName));
        object.put("depreciation", new JsonString(depreciationName));
        return object;
    }

    public final boolean EXPORT;
    public final Exchange EXCHANGE;
    private final Instance CURRENT_INSTANCE;
    private final String NAME;
    private final int AID;
    private final LCurrency CUR;
    private final AccountType TYPE;
    private final String BUDGET;

    public Account(String name, int aid, LCurrency cur, AccountType type, String budget, Exchange exchange, Instance curInst, boolean export) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        AID = aid;
        CUR = cur;
        TYPE = type;
        BUDGET = budget;
        EXPORT = export;
        EXCHANGE = exchange;
    }

    public Account(String name, int aid, LCurrency cur, AccountType type, Exchange exchange, Instance curInst, boolean export) {
        this(name, aid, cur, type, "", exchange, curInst, export);
    }

    public Account(JsonObject obj, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        NAME = obj.getString("name").getString();
        AID = obj.getDecimal("id").decimal.intValue();
        CUR = curInst.getLCurrency(obj.getString("currency").getString());
        TYPE = curInst.ACCOUNT_TYPES.getElement(obj.getString("type").getString());
        EXPORT = true;
        if (obj.containsKey("exchange")) {
            EXCHANGE = CURRENT_INSTANCE.EXCHANGES.getElement(obj.getString("exchange").getString());
        } else {
            EXCHANGE = null;
        }
        if (obj.containsKey("budget") && (TYPE.TYPE == BroadAccountType.REVENUE || TYPE.TYPE == BroadAccountType.EXPENSE)) {
            String candidate = obj.getString("budget").getString();
            if (CURRENT_INSTANCE.DATA_HANDLER.getBudgetTypes().contains(candidate)) {
                BUDGET = candidate;
            } else {
                BUDGET = "";
            }
        } else {
            BUDGET = "";
        }
    }

    public AWColumn getDefaultColumn(boolean positive) {
        if (positive) {
            return switch (getBroadAccountType()) {
                case TRACKING -> AWColumn.fromString("T");
                case GHOST -> AWColumn.fromString("G");
                case ASSET, EQUITY_MINUS, EXPENSE -> AWColumn.fromString("D");
                case LIABILITY, EQUITY_PLUS, REVENUE -> AWColumn.fromString("C");
            };
        } else {
            return switch (getBroadAccountType()) {
                case TRACKING -> AWColumn.fromString("T");
                case GHOST -> AWColumn.fromString("G");
                case ASSET, EQUITY_MINUS, EXPENSE -> AWColumn.fromString("C");
                case LIABILITY, EQUITY_PLUS, REVENUE -> AWColumn.fromString("D");
            };
        }
    }

    public BroadAccountType getBroadAccountType() {
        if (TYPE == null) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Broken Type: " + NAME);
            return null;
        } else {
            return TYPE.TYPE;
        }
    }

    public AccountType getAccountType() {
        return TYPE;
    }

    public String getBudgetType() {
        return BUDGET;
    }

    public LCurrency getCurrency() {
        return CUR;
    }

    public String getName() {
        return NAME;
    }

    public int getAid() {
        return AID;
    }

    public boolean inUse() {
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
            for (AccountWrapper aw : entry.getAccounts()) {
                if (aw.ACCOUNT.equals(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean inSpecial() {
        if (NAME.equalsIgnoreCase(portfolioName)) return true;
        if (NAME.equalsIgnoreCase(stockName)) return true;
        if (NAME.equalsIgnoreCase(inventoryName)) return true;
        if (NAME.equalsIgnoreCase(fiatName)) return true;
        if (NAME.equalsIgnoreCase(cryptoName)) return true;
        if (NAME.equalsIgnoreCase(tradIncName)) return true;
        if (NAME.equalsIgnoreCase(tradExpName)) return true;
        if (NAME.equalsIgnoreCase(cgStockName)) return true;
        if (NAME.equalsIgnoreCase(cgInventoryName)) return true;
        if (NAME.equalsIgnoreCase(cgFiatName)) return true;
        if (NAME.equalsIgnoreCase(cgCryptoName)) return true;
        if (NAME.equalsIgnoreCase(clStockName)) return true;
        if (NAME.equalsIgnoreCase(clInventoryName)) return true;
        if (NAME.equalsIgnoreCase(clFiatName)) return true;
        if (NAME.equalsIgnoreCase(clCryptoName)) return true;
        if (NAME.equalsIgnoreCase(cgltStockName)) return true;
        if (NAME.equalsIgnoreCase(cgltInventoryName)) return true;
        if (NAME.equalsIgnoreCase(cgltFiatName)) return true;
        if (NAME.equalsIgnoreCase(cgltCryptoName)) return true;
        if (NAME.equalsIgnoreCase(clltStockName)) return true;
        if (NAME.equalsIgnoreCase(clltInventoryName)) return true;
        if (NAME.equalsIgnoreCase(clltFiatName)) return true;
        if (NAME.equalsIgnoreCase(clltCryptoName)) return true;
        if (NAME.equalsIgnoreCase(selfIncName)) return true;
        if (NAME.equalsIgnoreCase(fixedAssetsTypeName)) return true;
        if (NAME.equalsIgnoreCase(receiveTypeName)) return true;
        if (NAME.equalsIgnoreCase(appreciationName)) return true;
        if (NAME.equalsIgnoreCase(depreciationName)) return true;
        return false;
    }

    public BigDecimal getAlpha(boolean credit, BigDecimal input) {
        return TYPE.TYPE.getAlpha(credit).multiply(input);
    }

    public int compareTo(Account b) {
        return Integer.compare(AID, b.AID);
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(NAME));
        obj.put("id", new JsonDecimal(AID));
        obj.put("currency", new JsonString(CUR.toString()));
        obj.put("type", new JsonString(TYPE.NAME));
        if (EXCHANGE != null) {
            obj.put("exchange", new JsonString(EXCHANGE.NAME));
        }
        if (!BUDGET.equals("")) {
            obj.put("budget", new JsonString(BUDGET));
        }
        return obj;
    }

    @Override
    public String toString() {
        return NAME;
    }
}