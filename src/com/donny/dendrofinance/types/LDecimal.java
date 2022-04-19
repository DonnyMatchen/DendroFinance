package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonItem;

import java.math.BigDecimal;

public class LDecimal extends LType<LDecimal> {
    public final BigDecimal VALUE;

    public LDecimal(BigDecimal val) {
        VALUE = val;
    }

    public LDecimal(String raw) {
        VALUE = new BigDecimal(raw);
    }

    @Override
    public boolean sameAs(LDecimal b) {
        return compare(b) == 0;
    }

    @Override
    public int compare(LDecimal b) {
        return VALUE.compareTo(b.VALUE);
    }

    @Override
    public JsonItem export() {
        return new JsonDecimal(VALUE);
    }

    @Override
    public boolean isDefault() {
        return sameAs(new LDecimal("0"));
    }

    @Override
    public String toString() {
        return VALUE.toString();
    }
}
