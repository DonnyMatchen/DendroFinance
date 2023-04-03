package com.donny.dendroroot.util;

import java.math.BigDecimal;
import java.util.HashMap;

public class Aggregation<T> extends HashMap<T, BigDecimal> {
    public boolean add(T key, BigDecimal val) {
        if (containsKey(key)) {
            put(key, val.add(get(key)));
            if (get(key).compareTo(BigDecimal.ZERO) == 0) {
                remove(key);
            }
            return true;
        } else {
            put(key, val);
            return false;
        }
    }

    @Override
    public BigDecimal get(Object key) {
        return getOrDefault(key, BigDecimal.ZERO);
    }
}
