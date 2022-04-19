package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonItem;

import java.math.BigDecimal;
import java.util.ArrayList;

public class LDecimalSet extends LType<LDecimalSet> {
    private final ArrayList<BigDecimal> REGISTRY;

    public LDecimalSet() {
        REGISTRY = new ArrayList<>();
    }

    public LDecimalSet(JsonArray array) {
        this();
        for (JsonDecimal decimal : array.getDecimalArray()) {
            REGISTRY.add(decimal.decimal);
        }
    }

    public LDecimalSet(String raw) {
        this();
        for (String val : raw.replace("{", "").replace("}", "").replace(" ", "").split(",")) {
            REGISTRY.add(new BigDecimal(val));
        }
    }

    @Override
    public boolean sameAs(LDecimalSet b) {
        boolean flag = true;
        if (REGISTRY.size() == b.REGISTRY.size()) {
            for (int i = 0; i < b.REGISTRY.size(); i++) {
                if (!get(i).equals(b.get(i))) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }
        return false;
    }

    @Override
    public int compare(LDecimalSet b) {
        return Integer.compare(REGISTRY.size(), b.REGISTRY.size());
    }

    @Override
    public JsonItem export() {
        JsonArray arr = new JsonArray();
        for (BigDecimal d : REGISTRY) {
            arr.ARRAY.add(new JsonDecimal(d));
        }
        return arr;
    }

    @Override
    public boolean isDefault() {
        return REGISTRY.isEmpty();
    }

    public BigDecimal get(int i) {
        if (i < REGISTRY.size()) {
            return REGISTRY.get(i);
        } else {
            return null;
        }
    }

    public int getSize() {
        return REGISTRY.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (BigDecimal d : REGISTRY) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(d);
        }
        sb.append("}");
        return sb.toString();
    }

    public void replace(int index, BigDecimal d) {
        if (index < REGISTRY.size()) {
            REGISTRY.remove(index);
            REGISTRY.add(index, d);
        }
    }

    public BigDecimal remove(int index) {
        return REGISTRY.remove(index);
    }

    public LDecimalSet add(BigDecimal d) {
        REGISTRY.add(d);
        return this;
    }
}
