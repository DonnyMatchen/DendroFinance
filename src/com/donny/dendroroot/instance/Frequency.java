package com.donny.dendroroot.instance;

public enum Frequency {
    NEVER, ANNUAL, QUARTERLY, MONTHLY;

    public static Frequency fromString(String str) {
        return switch (str.toUpperCase()) {
            case "ANNUAL" -> ANNUAL;
            case "QUARTERLY" -> QUARTERLY;
            case "MONTHLY" -> MONTHLY;
            default -> NEVER;
        };
    }

    public String toString() {
        return switch (this) {
            case NEVER -> "NEVER";
            case ANNUAL -> "ANNUAL";
            case QUARTERLY -> "QUARTERLY";
            case MONTHLY -> "MONTHLY";
        };
    }
}