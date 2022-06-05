package com.donny.dendrofinance.entry.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;
import com.donny.dendrofinance.util.Curation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetMetadata {
    public final String NAME, DESC;
    public final long ROOT_REF;
    public final LDate DATE;
    public final LCurrency CURRENCY;
    public final BigDecimal VAL, COUNT;
    public final ArrayList<AssetChangeMetadata> EVENTS;

    public AssetMetadata(long uuid, LDate date, String name, String description, LCurrency currency, BigDecimal value, BigDecimal count) {
        NAME = name;
        DESC = description;
        ROOT_REF = uuid;
        DATE = date;
        CURRENCY = currency;
        VAL = value;
        COUNT = count;
        EVENTS = new ArrayList<>();
    }

    public AssetMetadata(long uuid, LDate date, JsonObject obj, Instance curInst) {
        this(
                uuid,
                obj.containsKey("date") ? new LDate(obj.getDecimal("date"), curInst) : date,
                obj.getString("name").getString(),
                obj.getString("desc").getString(),
                curInst.getLCurrency(obj.getString("currency").getString()),
                obj.getDecimal("val").decimal,
                obj.getDecimal("count").decimal
        );

    }

    public ArrayList<Long> getRefs() {
        ArrayList<Long> out = new ArrayList<>();
        for (AssetChangeMetadata meta : EVENTS) {
            out.add(meta.UUID);
        }
        return out;
    }

    public ArrayList<LCurrency> getCurrencies() {
        Curation<LCurrency> out = new Curation<>();
        out.add(CURRENCY);
        for (AssetChangeMetadata meta : EVENTS) {
            out.add(meta.CURRENCY);
        }
        return out;
    }

    public HashMap<LCurrency, BigDecimal> getValues() {
        Aggregation<LCurrency> out = new Aggregation<>();
        out.add(CURRENCY, VAL);
        for (AssetChangeMetadata meta : EVENTS) {
            out.add(meta.CURRENCY, meta.CHANGE);
        }
        return out;
    }

    public HashMap<LCurrency, BigDecimal> getCount() {
        Aggregation<LCurrency> out = new Aggregation<>();
        out.add(CURRENCY, COUNT);
        for (AssetChangeMetadata meta : EVENTS) {
            out.add(meta.CURRENCY, meta.COUNT);
        }
        return out;
    }

    public BigDecimal getTotalCount() {
        BigDecimal total = COUNT;
        for (AssetChangeMetadata meta : EVENTS) {
            total = total.add(meta.COUNT);
        }
        return total;
    }

    public boolean isCurrent() {
        return getTotalCount().compareTo(BigDecimal.ZERO) != 0;
    }

    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("date", DATE.export());
        obj.put("name", new JsonString(NAME));
        obj.put("desc", new JsonString(DESC));
        obj.put("currency", new JsonString(CURRENCY.toString()));
        obj.put("val", new JsonDecimal(VAL));
        obj.put("count", new JsonDecimal(COUNT));
        return obj;
    }

    public String display() {
        return NAME + "(" + DESC + ") " + CURRENCY.encode(VAL) + " " + COUNT.abs();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(NAME + " (" + DESC + ") {");
        sb.append("\n\t").append("(").append(DATE.toDateString()).append(") ").append("+").append(CURRENCY.encode(VAL.abs())).append(" ").append("+").append(COUNT.abs());
        for (AssetChangeMetadata meta : EVENTS) {
            sb.append("\n\t").append(meta.print().replace(" " + NAME + " ", " "));
        }
        return sb.append("\n}").toString();
    }
}
