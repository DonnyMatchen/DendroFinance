package com.donny.dendrofinance.capsules.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;
import com.donny.dendrofinance.util.Curation;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetMetadata implements ExportableToJson {
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
                obj.containsKey(new String[]{"r", "ref", "root-ref"}) ? obj.getDecimal(new String[]{"r", "ref", "root-ref"}).decimal.longValue() : uuid,
                obj.containsKey(new String[]{"t", "date", "timestamp"}) ? new LDate(obj.getDecimal(new String[]{"t", "date", "timestamp"}), curInst) : date,
                obj.getString(new String[]{"n", "name"}).getString(),
                obj.getString(new String[]{"d", "desc"}).getString(),
                curInst.getLCurrency(obj.getString(new String[]{"c", "cur", "currency"}).getString()),
                obj.getDecimal(new String[]{"v", "val", "value"}).decimal,
                obj.getDecimal(new String[]{"z", "number", "count"}).decimal
        );
        if (obj.containsKey(new String[]{"e", "events"})) {
            for (JsonObject event : obj.getArray(new String[]{"e", "events"}).getObjectArray()) {
                EVENTS.add(new AssetChangeMetadata(0, null, event, curInst));
            }
        }
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

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("t", DATE.export());
        obj.put("n", new JsonString(NAME));
        obj.put("d", new JsonString(DESC));
        obj.put("c", new JsonString(CURRENCY.toString()));
        obj.put("v", new JsonDecimal(VAL));
        obj.put("z", new JsonDecimal(COUNT));
        return obj;
    }

    public JsonObject fullExport() throws JsonFormattingException {
        JsonObject obj = export();
        obj.put("r", new JsonDecimal(ROOT_REF));
        JsonArray array = new JsonArray();
        for (AssetChangeMetadata event : EVENTS) {
            array.add(event.fullExport());
        }
        obj.put("e", array);
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
