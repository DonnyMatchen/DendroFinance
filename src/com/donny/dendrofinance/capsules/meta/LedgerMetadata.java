package com.donny.dendrofinance.capsules.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

public class LedgerMetadata {
    public final long UUID;
    public final LDate DATE;
    public final LCurrency FROM, TO;
    // FROM_AMNT is always negative
    // TO_AMNT is always positive
    // MAIN_VALUE is always positive
    public final BigDecimal FROM_AMNT, TO_AMNT, MAIN_VALUE;
    private final Instance CURRENT_INSTANCE;

    public LedgerMetadata(long uuid, LDate date, LCurrency from, LCurrency to, BigDecimal cost, BigDecimal amount, BigDecimal mainValue, Instance curInst) {
        UUID = uuid;
        DATE = date;
        FROM = from;
        TO = to;
        FROM_AMNT = cost;
        TO_AMNT = amount;
        MAIN_VALUE = mainValue;
        CURRENT_INSTANCE = curInst;
    }

    public LedgerMetadata(long uuid, LDate date, JsonObject obj, Instance curInst) {
        this(uuid,
                obj.containsKey("date") ? new LDate(obj.getDecimal("date"), curInst) : date,
                curInst.getLCurrency(obj.getString("from-cur").getString()),
                curInst.getLCurrency(obj.getString("to-cur").getString()),
                obj.getDecimal("from-amnt").decimal,
                obj.getDecimal("to-amnt").decimal,
                obj.getDecimal("main-amnt").decimal,
                curInst);
    }

    public BigDecimal fromMain() {
        return MAIN_VALUE.divide(FROM_AMNT, CURRENT_INSTANCE.precision).abs();
    }

    public BigDecimal toMain() {
        if (TO_AMNT.compareTo(BigDecimal.ZERO) == 0) {
            return MAIN_VALUE;
        } else {
            return MAIN_VALUE.divide(TO_AMNT, CURRENT_INSTANCE.precision);
        }
    }

    public BigDecimal negativeMainValue() {
        if (FROM_AMNT.compareTo(BigDecimal.ZERO) == 0) {
            return MAIN_VALUE;
        } else {
            return MAIN_VALUE.multiply(BigDecimal.valueOf(-1));
        }
    }

    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("date", DATE.export());
        obj.put("from-cur", new JsonString(FROM.toString()));
        obj.put("to-cur", new JsonString(TO.toString()));
        obj.put("from-amnt", new JsonDecimal(FROM_AMNT));
        obj.put("to-amnt", new JsonDecimal(TO_AMNT));
        obj.put("main-amnt", new JsonDecimal(MAIN_VALUE));
        return obj;
    }

    @Override
    public String toString() {
        return "(" + FROM.toString() + " -> " + TO.toString() + ")\n" + FROM.encode(FROM_AMNT.abs()) + " -> " + TO.encode(TO_AMNT) + " (" + CURRENT_INSTANCE.$(MAIN_VALUE) + ")";
    }
}
