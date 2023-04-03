package com.donny.dendroroot.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Objects;

public class DoubleAggregation<E, T> extends HashMap<E, Aggregation<T>> {
    public boolean add(E outerKey, T innerKey, BigDecimal value) {
        if (!containsKey(outerKey)) {
            put(outerKey, new Aggregation<>());
        }
        return get(outerKey).add(innerKey, value);
    }

    @Override
    public Aggregation<T> get(Object key) {
        return Objects.requireNonNullElse(super.get(key), new Aggregation<>());
    }

    public Curation<T> innerKeySet() {
        Curation<T> keys = new Curation<>();
        for (E key : keySet()) {
            keys.addAll(get(key).keySet());
        }
        return keys;
    }
}
