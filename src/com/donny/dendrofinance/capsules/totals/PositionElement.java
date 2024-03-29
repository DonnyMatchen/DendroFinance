package com.donny.dendrofinance.capsules.totals;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.util.ExportableToJson;

import java.math.BigDecimal;

public class PositionElement implements ExportableToJson {
    public final LDate DATE;
    public final long UUID;
    public final BigDecimal UNIT;
    public BigDecimal volume;

    public PositionElement(long uuid, LDate date, BigDecimal vol, BigDecimal unit) {
        UUID = uuid;
        DATE = date;
        volume = vol;
        UNIT = unit;
    }

    public PositionElement(JsonObject object, ProgramInstance curInst) {
        this(
                object.getDecimal("ref").decimal.longValue(),
                new LDate(object.getDecimal("date"), curInst),
                object.getDecimal("vol").decimal,
                object.getDecimal("unit").decimal
        );
    }

    public BigDecimal cost() {
        return volume.multiply(UNIT);
    }

    public String[] view(LCurrency asset, LCurrency main__) {
        return new String[]{
                asset.encode(volume),
                main__.encode(cost()),
                main__.encode(UNIT)
        };
    }

    @Override
    public JsonObject export() {
        JsonObject object = new JsonObject();
        object.put("ref", new JsonDecimal(UUID));
        object.put("date", DATE.export());
        object.put("vol", new JsonDecimal(volume));
        object.put("unit", new JsonDecimal(UNIT));
        return object;
    }

    @Override
    public String toString() {
        return "{" + UUID + "} \\ (" + DATE + ") \\ volume=" + volume + ", cost=" + volume.multiply(UNIT) + ", unit=" + UNIT;
    }
}
