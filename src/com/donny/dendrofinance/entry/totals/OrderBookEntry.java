package com.donny.dendrofinance.entry.totals;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;

public class OrderBookEntry implements ExportableToJson {
    public final LCurrency ASSET;
    public final LDate START, END;
    public final long START_REF, END_REF;
    public final BigDecimal VOLUME, COST, SOLD;

    public OrderBookEntry(LCurrency cur, LDate start, LDate end, long sUUID, long eUUID, BigDecimal vol, BigDecimal cost, BigDecimal sold) {
        ASSET = cur;
        START = start;
        END = end;
        START_REF = sUUID;
        END_REF = eUUID;
        VOLUME = vol;
        COST = cost;
        SOLD = sold;
    }

    public OrderBookEntry(JsonObject object, Instance curInst) {
        this(
                curInst.getLCurrency(object.getString("asset").getString()),
                new LDate(object.getDecimal("start"), curInst),
                new LDate(object.getDecimal("end"), curInst),
                object.getDecimal("start-ref").decimal.longValue(),
                object.getDecimal("end-ref").decimal.longValue(),
                object.getDecimal("volume").decimal,
                object.getDecimal("cost").decimal,
                object.getDecimal("sold").decimal
        );
    }

    public BigDecimal profit() {
        return SOLD.add(COST);
    }

    public boolean longTerm() {
        return END.getTime() - START.getTime() >= 31622400000L;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject object = new JsonObject();
        object.put("asset", new JsonString(ASSET.toString()));
        object.put("start", START.export());
        object.put("end", END.export());
        object.put("start-ref", new JsonDecimal(START_REF));
        object.put("end-ref", new JsonDecimal(END_REF));
        object.put("volume", new JsonDecimal(VOLUME));
        object.put("cost", new JsonDecimal(COST));
        object.put("sold", new JsonDecimal(SOLD));
        return object;
    }

    @Override
    public String toString() {
        return "(" + START + ") -> (" + END + ") " + ASSET + " {" + COST + " : " + SOLD + " : " + profit() + "}";
    }
}
