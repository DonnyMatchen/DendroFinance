package com.donny.dendrofinance.account;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.AccountWrapper;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;

public class Account implements ExportableToJson {
    public final Instance CURRENT_INSTANCE;
    public final boolean EXPORT;
    private final String NAME;
    private final int AID;
    private final LCurrency CUR;
    private final AccountType TYPE;
    private final String B_TYPE;

    public Account(String name, int aid, LCurrency cur, AccountType type, String btype, Instance curInst, boolean export) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        AID = aid;
        CUR = cur;
        TYPE = type;
        B_TYPE = btype;
        EXPORT = export;
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "Account " + NAME + " Created");
    }

    public Account(String name, int aid, LCurrency cur, AccountType type, Instance curInst, boolean export) {
        this(name, aid, cur, type, "", curInst, export);
    }

    public Account(JsonObject obj, Instance curInst) {
        this(
                obj.getString("name").getString(),
                obj.getDecimal("id").decimal.intValue(),
                curInst.getLCurrency(obj.getString("currency").getString()),
                curInst.ACCOUNT_TYPES.getElement(obj.getString("type").getString()),
                obj.getString("budget").getString(),
                curInst,
                true
        );
    }

    public AccountWrapper getDefaultWrapper() {
        return switch (getBroadAccountType()) {
            case TRACKING -> new AccountWrapper(this, "B");
            case TAX -> new AccountWrapper(this, "T");
            case ASSET, EQUITY_MINUS, EXPENSE -> new AccountWrapper(this, "D");
            case LIABILITY, EQUITY_PLUS, REVENUE -> new AccountWrapper(this, "C");
        };
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

    public boolean counts(BigDecimal value) {
        if (CUR.getPlaces() > 0) {
            BigDecimal check = new BigDecimal("0." + "0".repeat(CUR.getPlaces() - 1) + "1");
            return value.abs().compareTo(check) >= 0;
        } else {
            return value.compareTo(BigDecimal.ZERO) != 0;
        }
    }

    public String getBudgetType() {
        return B_TYPE;
    }

    public String encode(BigDecimal amnt) {
        return CUR.encode(amnt);
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
        obj.FIELDS.put("budget", new JsonString(B_TYPE));
        return obj;
    }

    @Override
    public String toString() {
        return NAME;
    }
}