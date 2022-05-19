package com.donny.dendrofinance.tax;

import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;
import java.util.ArrayList;

public class TaxItem implements ExportableToJson {
    public final String NAME;
    public final boolean BOUNDED;
    public final BigDecimal BOUND, EXEMPTION;
    private final ArrayList<BracketItem> BRACKETS;

    public TaxItem(JsonObject object) {
        NAME = object.getString("name").getString();
        BOUND = object.getDecimal("bound").decimal;
        BOUNDED = BOUND.compareTo(BigDecimal.ZERO) > 0;
        BRACKETS = new ArrayList<>();
        JsonArray arr = object.getArray("brackets");
        for (JsonObject obj : arr.getObjectArray()) {
            BRACKETS.add(new BracketItem(obj));
        }
        EXEMPTION = object.getDecimal("exempt").decimal;
    }

    public TaxItem(String name, BigDecimal exemption, BigDecimal bound) {
        NAME = name;
        BOUND = bound;
        BOUNDED = BOUND.compareTo(BigDecimal.ZERO) > 0;
        BRACKETS = new ArrayList<>();
        EXEMPTION = exemption;
    }

    public TaxItem(String name, BigDecimal exemption) {
        this(name, exemption, BigDecimal.ZERO);
    }

    public BigDecimal process(BigDecimal value, BigDecimal exemption) {
        value = value.subtract(exemption);
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        int x = BRACKETS.size() - 1;
        for (int i = 0; i < BRACKETS.size(); i++) {
            if (BRACKETS.get(i).LOWER_BOUND.compareTo(value) <= 0) {
                if (i == x || BRACKETS.get(i + 1).LOWER_BOUND.compareTo(value) > 0) {
                    total = total.add(value.subtract(BRACKETS.get(i).LOWER_BOUND).multiply(BRACKETS.get(i).RATE));
                } else {
                    total = total.add(BRACKETS.get(i + 1).LOWER_BOUND.subtract(BRACKETS.get(i).LOWER_BOUND).multiply(BRACKETS.get(i).RATE));
                }
            }
        }
        if (BOUNDED) {
            return total.compareTo(BOUND) >= 0 ? BOUND : total;
        } else {
            return total;
        }
    }

    public BigDecimal process(BigDecimal value) {
        return process(value, EXEMPTION);
    }

    public void setBrackets(ArrayList<BigDecimal[]> items) {
        BRACKETS.clear();
        for (BigDecimal[] item : items) {
            BRACKETS.add(new BracketItem(
                    item[0],
                    item[1]
            ));
        }
    }

    public void setFlatTax(BigDecimal rate) {
        BRACKETS.clear();
        BRACKETS.add(new BracketItem(
                BigDecimal.ZERO,
                rate
        ));
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(NAME));
        obj.put("bound", new JsonDecimal(BOUND));
        JsonArray arr = new JsonArray();
        for (BracketItem item : BRACKETS) {
            arr.add(item.export());
        }
        obj.put("brackets", arr);
        obj.put("exempt", new JsonDecimal(EXEMPTION));
        return obj;
    }

    public static class BracketItem implements ExportableToJson {
        public final BigDecimal LOWER_BOUND, RATE;

        public BracketItem(BigDecimal bound, BigDecimal rate) {
            LOWER_BOUND = bound;
            RATE = rate;
        }

        public BracketItem(JsonObject object) {
            this(
                    object.getDecimal("bound").decimal,
                    object.getDecimal("rate").decimal
            );
        }

        @Override
        public JsonObject export() {
            JsonObject obj = new JsonObject();
            obj.put("bound", new JsonDecimal(LOWER_BOUND));
            obj.put("rate", new JsonDecimal(RATE));
            return obj;
        }
    }
}
