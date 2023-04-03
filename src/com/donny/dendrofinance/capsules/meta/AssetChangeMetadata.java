package com.donny.dendrofinance.capsules.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.util.ExportableToJson;

import java.math.BigDecimal;

public class AssetChangeMetadata implements ExportableToJson {
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

    public AssetChangeMetadata(long uuid, LDate date, JsonObject obj, ProgramInstance curInst) {
        this(
                obj.containsKey(new String[]{"u", "ref", "uuid"}) ? obj.getDecimal(new String[]{"u", "ref", "uuid"}).decimal.longValue() : uuid,
                obj.containsKey(new String[]{"t", "date", "timestamp"}) ? new LDate(obj.getDecimal(new String[]{"t", "date", "timestamp"}), curInst) : date,
                obj.getString(new String[]{"n", "name"}).getString(),
                curInst.getLCurrency(obj.getString(new String[]{"c", "cur", "currency"}).getString()),
                obj.getDecimal(new String[]{"v", "val", "value", "change"}).decimal,
                obj.getDecimal(new String[]{"z", "number", "count"}).decimal
        );
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("t", DATE.export());
        obj.put("n", new JsonString(NAME));
        obj.put("c", new JsonString(CURRENCY.getTicker()));
        obj.put("v", new JsonDecimal(CHANGE));
        obj.put("z", new JsonDecimal(COUNT));
        return obj;
    }

    public JsonObject fullExport() throws JsonFormattingException {
        JsonObject obj = export();
        obj.put("u", new JsonDecimal(UUID));
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
