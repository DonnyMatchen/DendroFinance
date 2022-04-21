package com.donny.dendrofinance.types;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;

public class AccountWrapper implements ExportableToJson {
    public final Account ACCOUNT;
    public final AWType COLUMN;
    public final BigDecimal VALUE;

    public AccountWrapper(Account account, AWType column, BigDecimal value) {
        ACCOUNT = account;
        COLUMN = column;
        VALUE = value;
    }
    public AccountWrapper(Account account, String column, BigDecimal value) {
        this(
                account,
                AWType.fromString(column),
                value
        );
    }
    public AccountWrapper(JsonObject obj, Instance curInst) {
        this(
                curInst.ACCOUNTS.getElement(obj.getString("acc").getString()),
                obj.getString("col").getString(),
                obj.getDecimal("val").decimal
        );
    }
    public AccountWrapper(String raw, Instance curInst) {
        String[] acv = raw.split("\\(");
        String[] ac = acv[0].split("!");
        ACCOUNT = curInst.ACCOUNTS.getElement(ac[1]);
        COLUMN = AWType.fromString(ac[0]);
        VALUE = new BigDecimal(acv[1].replace(")", ""));
    }

    public AccountWrapper modify(BigDecimal newVal){
        return new AccountWrapper(ACCOUNT, COLUMN, newVal);
    }

    @Override
    public String toString() {
        return COLUMN.toString() + "!" + ACCOUNT.getName() + "(" + VALUE + ")";
    }

    /**
     * @return the amount with corrected sign
     */
    public BigDecimal alpha() {
        if (COLUMN == null) {
            return VALUE;
        } else return switch (COLUMN) {
            case CREDIT -> ACCOUNT.alpha(true, VALUE);
            case DEBIT -> ACCOUNT.alpha(false, VALUE);
            default -> VALUE;
        };
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject object = new JsonObject();
        object.FIELDS.put("acc", new JsonString(ACCOUNT.toString()));
        object.FIELDS.put("col", new JsonString(COLUMN.toString()));
        object.FIELDS.put("val", new JsonDecimal(VALUE));
        return object;
    }

    public boolean equals(AccountWrapper b){
        return ACCOUNT.equals(b.ACCOUNT) && COLUMN == b.COLUMN && VALUE.compareTo(b.VALUE) == 0;
    }

    public enum AWType {
        DEBIT, CREDIT, TRACKER, GHOST;

        public static AWType fromString(String s) {
            return switch (s) {
                case "D" -> DEBIT;
                case "C" -> CREDIT;
                case "T" -> TRACKER;
                default -> GHOST;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case DEBIT -> "D";
                case CREDIT -> "C";
                case GHOST -> "G";
                case TRACKER -> "T";
            };
        }
    }
}
