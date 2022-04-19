package com.donny.dendrofinance.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

public class Aggregation<T> extends HashMap<T, BigDecimal> {
    public boolean add(T key, BigDecimal val) {
        if (containsKey(key)) {
            put(key, val.add(get(key)));
            return true;
        } else {
            put(key, val);
            return false;
        }
    }

    @Override
    public BigDecimal get(Object key) {
        return Objects.requireNonNullElse(super.get(key), BigDecimal.ZERO);
    }
}
