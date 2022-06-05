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