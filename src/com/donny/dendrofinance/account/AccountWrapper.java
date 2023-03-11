package com.donny.dendrofinance.account;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;

public class AccountWrapper implements ExportableToJson {
    public final Account ACCOUNT;
    public final AWColumn COLUMN;
    public final BigDecimal VALUE;

    public AccountWrapper(Account account, AWColumn column, BigDecimal value) {
        ACCOUNT = account;
        COLUMN = column;
        VALUE = value;
    }

    public AccountWrapper(Account account, String column, BigDecimal value) {
        this(
                account,
                AWColumn.fromString(column),
                value
        );
    }

    public AccountWrapper(JsonObject obj, Instance curInst) {
        this(
                curInst.ACCOUNTS.getElement(obj.getString(new String[]{"a", "acc"}).getString()),
                obj.getString(new String[]{"c", "col"}).getString(),
                obj.getDecimal(new String[]{"v", "val"}).decimal
        );
    }

    public AccountWrapper(String raw, Instance curInst) {
        String[] acv = raw.split("\\(");
        String[] ac = acv[0].split("!");
        ACCOUNT = curInst.ACCOUNTS.getElement(ac[1]);
        COLUMN = AWColumn.fromString(ac[0]);
        VALUE = new BigDecimal(acv[1].replace(")", ""));
    }

    public AccountWrapper modify(BigDecimal newVal) {
        return new AccountWrapper(ACCOUNT, COLUMN, newVal);
    }

    @Override
    public String toString() {
        return COLUMN.toString() + "!" + ACCOUNT.getName() + "(" + VALUE + ")";
    }

    /**
     * @return the amount with corrected sign
     */
    public BigDecimal getAlphaProcessed() {
        if (COLUMN == null) {
            return VALUE;
        } else return switch (COLUMN) {
            case CREDIT -> ACCOUNT.getAlpha(true, VALUE);
            case DEBIT -> ACCOUNT.getAlpha(false, VALUE);
            default -> VALUE;
        };
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject object = new JsonObject();
        object.put("a", new JsonString(ACCOUNT.toString()));
        object.put("c", new JsonString(COLUMN.toString()));
        object.put("v", new JsonDecimal(VALUE));
        return object;
    }

    public boolean equals(AccountWrapper b) {
        return ACCOUNT.equals(b.ACCOUNT) && COLUMN == b.COLUMN && VALUE.compareTo(b.VALUE) == 0;
    }
}
