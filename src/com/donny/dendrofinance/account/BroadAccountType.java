package com.donny.dendrofinance.account;

import java.math.BigDecimal;
import java.util.HashMap;

public enum BroadAccountType {
    ASSET, LIABILITY, EQUITY_PLUS, EQUITY_MINUS, REVENUE, EXPENSE, GHOST, TRACKING;

    private static final HashMap<BroadAccountType, String> CODEX = new HashMap<>();

    static {
        CODEX.put(ASSET, "ASSET");
        CODEX.put(LIABILITY, "LIABILITY");
        CODEX.put(EQUITY_PLUS, "EQUITY_PLUS");
        CODEX.put(EQUITY_MINUS, "EQUITY_MINUS");
        CODEX.put(REVENUE, "REVENUE");
        CODEX.put(EXPENSE, "EXPENSE");
        CODEX.put(GHOST, "GHOST");
        CODEX.put(TRACKING, "TRACKING");
    }

    public static BroadAccountType fromString(String name) {
        for (BroadAccountType a : CODEX.keySet()) {
            if (CODEX.get(a).equals(name)) {
                return a;
            }
        }
        return null;
    }

    public BigDecimal alpha(boolean credit) {
        BigDecimal p = BigDecimal.ONE, n = BigDecimal.valueOf(-1);
        if (this == GHOST || this == TRACKING) {
            return p;
        } else {
            if (this == ASSET || this == EXPENSE || this == EQUITY_MINUS) {
                return credit ? n : p;
            } else {
                return credit ? p : n;
            }
        }
    }

    @Override
    public String toString() {
        return switch (this) {
            case ASSET -> "ASSET";
            case LIABILITY -> "LIABILITY";
            case EQUITY_PLUS -> "EQUITY_PLUS";
            case EQUITY_MINUS -> "EQUITY_MINUS";
            case REVENUE -> "REVENUE";
            case EXPENSE -> "EXPENSE";
            case GHOST -> "GHOST";
            case TRACKING -> "TRACKING";
        };
    }
}
