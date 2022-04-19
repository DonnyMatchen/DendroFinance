package com.donny.dendrofinance.types;

import com.donny.dendrofinance.account.Account;

import java.math.BigDecimal;

public class AccountWrapper {
    public final Account ACCOUNT;
    public final AWType COLUMN;
    public AccountWrapper(Account a, String column) {
        ACCOUNT = a;
        COLUMN = AWType.fromString(column);
    }

    @Override
    public String toString() {
        return COLUMN.toString() + "!" + ACCOUNT.getName();
    }

    /**
     * @param d the raw amount
     * @return the amount with correct sign
     */
    public BigDecimal alpha(BigDecimal d) {
        if (COLUMN == null) {
            return d;
        } else return switch (COLUMN) {
            case CREDIT -> ACCOUNT.alpha(true, d);
            case DEBIT -> ACCOUNT.alpha(false, d);
            default -> d;
        };
    }

    public enum AWType {
        DEBIT, CREDIT, TRACKER, TAX;

        public static AWType fromString(String s) {
            return switch (s) {
                case "D" -> DEBIT;
                case "C" -> CREDIT;
                case "B" -> TRACKER;
                default -> TAX;
            };
        }

        @Override
        public String toString() {
            return switch (this) {
                case DEBIT -> "D";
                case CREDIT -> "C";
                case TAX -> "T";
                case TRACKER -> "B";
            };
        }
    }
}
