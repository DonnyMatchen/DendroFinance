package com.donny.dendrofinance.entry.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

public class AssetChangeMetadata {
    public final long UUID;
    public final LDate DATE;
    public final String NAME;
    public final LCurrency CURRENCY;
    public final BigDecimal CHANGE, COUNT;

    public AssetChangeMetadata(long uuid, LDate date, String name, LCurrency cur, BigDecimal change, BigDecimal count) {
        UUID = uuid;
        DATE = date;
        NAME = name;
        CURRENCY = cur;
        CHANGE = change;
        COUNT = count;
    }

    public AssetChangeMetadata(long uuid, LDate date, JsonObject obj, Instance curInst) {
        this(uuid,
                obj.containsKey("date") ? new LDate(obj.getDecimal("date"), curInst) : date,
                obj.getString("name").getString(),
                curInst.getLCurrency(obj.getString("currency").getString()),
                obj.getDecimal("change").decimal,
                obj.getDecimal("count").decimal);
    }


    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("date", DATE.export());
        obj.put("name", new JsonString(NAME));
        obj.put("currency", new JsonString(CURRENCY.getTicker()));
        obj.put("change", new JsonDecimal(CHANGE));
        obj.put("count", new JsonDecimal(COUNT));
        return obj;
    }

    public String print() {
        return "(" + DATE.toDateString() + ") " + NAME + " " + (CHANGE.compareTo(BigDecimal.ZERO) < 0 ? "-" : "+") + CURRENCY.encode(CHANGE.abs()) + " " +
                (COUNT.compareTo(BigDecimal.ZERO) < 0 ? "-" : "+") + COUNT.abs();
    }

    @Override
    public String toString() {
        return NAME + ": " + print();
    }
}
