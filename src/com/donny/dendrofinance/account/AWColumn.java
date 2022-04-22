package com.donny.dendrofinance.account;

public enum AWColumn {
    DEBIT, CREDIT, TRACKER, GHOST;

    public static AWColumn fromString(String s) {
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
