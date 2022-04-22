package com.donny.dendrofinance.entry.totals;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

public class OrderBookEntry {
    public final LCurrency ASSET;
    public final LDate START, END;
    public final long START_REF, END_REF;
    public final BigDecimal VOLUME, COST, SOLD;

    public OrderBookEntry(LCurrency cur, LDate start, LDate end, long sUUID, long eUUID, BigDecimal vol, BigDecimal cost, BigDecimal sold) {
        ASSET = cur;
        START = start;
        END = end;
        START_REF = sUUID;
        END_REF = eUUID;
        VOLUME = vol;
        COST = cost;
        SOLD = sold;
    }

    public BigDecimal profit() {
        return SOLD.add(COST);
    }

    @Override
    public String toString() {
        return "(" + START + ") -> (" + END + ") " + ASSET + " {" + COST + " : " + SOLD + " : " + profit() + "}";
    }
}
