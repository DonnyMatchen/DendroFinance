package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonItem;

import java.math.BigDecimal;

public class LInt extends LType<LInt> {
    public final int VALUE;

    public LInt(int i) {
        VALUE = i;
    }

    @Override
    public boolean sameAs(LInt b) {
        return VALUE == b.VALUE;
    }

    @Override
    public int compare(LInt b) {
        return Integer.compare(VALUE, b.VALUE);
    }

    @Override
    public JsonItem export() {
        return new JsonDecimal(BigDecimal.valueOf(VALUE));
    }

    @Override
    public boolean isDefault() {
        return sameAs(new LInt(0));
    }

    @Override
    public String toString() {
        return "" + VALUE;
    }
}
