package com.donny.dendrofinance.entry;

public enum FieldType {
    SET_ACCOUNT, SET_DECIMAL, STRING, DATE, INT, DECIMAL, JSON, NULL;

    public static FieldType resolve(Object o) {
        String clss = o.getClass().toString();
        if (clss.contains("LJson")) {
            return JSON;
        }
        if (clss.contains("LAccountSet")) {
            return SET_ACCOUNT;
        }
        if (clss.contains("LDecimalSet")) {
            return SET_DECIMAL;
        }
        if (clss.contains("LString")) {
            return STRING;
        }
        if (clss.contains("LDate")) {
            return DATE;
        }
        if (clss.contains("LInt")) {
            return INT;
        }
        if (clss.contains("LDecimal")) {
            return DECIMAL;
        }
        return NULL;
    }

    public static FieldType fromString(String str) {
        return switch (str) {
            case "S" -> STRING;
            case "D" -> DATE;
            case "F" -> DECIMAL;
            case "I" -> INT;
            case "J" -> JSON;
            case "A" -> SET_ACCOUNT;
            case "$" -> SET_DECIMAL;
            default -> NULL;
        };
    }

    @Override
    public String toString() {
        return switch (this) {
            case STRING -> "S";
            case DATE -> "D";
            case INT -> "I";
            case JSON -> "J";
            case SET_DECIMAL -> "$";
            case DECIMAL -> "F";
            case SET_ACCOUNT -> "A";
            default -> "0";
        };
    }
}
