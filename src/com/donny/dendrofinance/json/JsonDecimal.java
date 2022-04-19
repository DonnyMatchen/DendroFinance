package com.donny.dendrofinance.json;

import java.math.BigDecimal;

public class JsonDecimal extends JsonItem {
    public BigDecimal decimal;

    public JsonDecimal(String raw) throws JsonFormattingException {
        super(JsonType.DECIMAL);
        try {
            decimal = new BigDecimal(raw);
        } catch (NumberFormatException | ArithmeticException ex) {
            throw new JsonFormattingException("Bad Number: " + raw);
        }
    }

    public JsonDecimal(BigDecimal dec) {
        super(JsonType.DECIMAL);
        decimal = dec;
    }

    public JsonDecimal() {
        this(BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return decimal.toString();
    }

    @Override
    public String print(int scope) {
        return toString();
    }
}
