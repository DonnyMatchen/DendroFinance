package com.donny.dendrofinance.account;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.util.ExportableToJsonObject;

import java.math.BigDecimal;

public class Account implements ExportableToJsonObject {
    public final Instance CURRENT_INSTANCE;
    public final boolean EXPORT;
    private final String NAME;
    private final int AID;
    private final LCurrency CUR;
    private final AccountType TYPE;
    private final String BUDGET;

    public Account(String name, int aid, LCurrency cur, AccountType type, String budget, Instance curInst, boolean export) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        AID = aid;
        CUR = cur;
        TYPE = type;
        BUDGET = budget;
        EXPORT = export;
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "Account " + NAME + " Created");
    }

    public Account(String name, int aid, LCurrency cur, AccountType type, Instance curInst, boolean export) {
        this(name, aid, cur, type, "", curInst, export);
    }

    public Account(JsonObject obj, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        NAME = obj.getString("name").getString();
        AID = obj.getDecimal("id").decimal.intValue();
        CUR = curInst.getLCurrency(obj.getString("currency").getString());
        TYPE = curInst.ACCOUNT_TYPES.getElement(obj.getString("type").getString());
        EXPORT = true;
        if (obj.FIELDS.containsKey("budget")) {
            BUDGET = obj.getString("budget").getString();
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
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Broken Type: " + NAME);
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

    public BigDecimal alpha(boolean credit, BigDecimal input) {
        return TYPE.TYPE.alpha(credit).multiply(input);
    }

    public int compareTo(Account b) {
        return Integer.compare(AID, b.AID);
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.FIELDS.put("name", new JsonString(NAME));
        obj.FIELDS.put("id", new JsonDecimal(BigDecimal.valueOf(AID)));
        if (CUR.getName().equals(CURRENT_INSTANCE.getLCurrency(CUR.toString()).getName())) {
            obj.FIELDS.put("currency", new JsonString(CUR.toString()));
        } else {
            obj.FIELDS.put("currency", new JsonString(CUR.getName()));
        }
        obj.FIELDS.put("type", new JsonString(TYPE.NAME));
        obj.FIELDS.put("budget", new JsonString(BUDGET));
        return obj;
    }

    @Override
    public String toString() {
        return NAME;
    }
}