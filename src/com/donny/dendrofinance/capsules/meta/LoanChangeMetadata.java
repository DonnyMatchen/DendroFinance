package com.donny.dendrofinance.capsules.meta;

import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;
import com.donny.dendroroot.types.LDate;

import java.math.BigDecimal;

public class LoanChangeMetadata {
    public final long UUID;
    public final LDate DATE;
    public final String NAME;
    public final BigDecimal CHANGE;

    public LoanChangeMetadata(long uuid, LDate date, String name, BigDecimal change) {
        UUID = uuid;
        DATE = date;
        NAME = name;
        CHANGE = change;
    }

    public LoanChangeMetadata(long uuid, LDate date, JsonObject obj, ProgramInstance curInst) {
        this(
                obj.containsKey(new String[]{"u", "ref", "uuid"}) ? obj.getDecimal(new String[]{"u", "ref", "uuid"}).decimal.longValue() : uuid,
                obj.containsKey(new String[]{"t", "date", "timestamp"}) ? new LDate(obj.getDecimal(new String[]{"t", "date", "timestamp"}), curInst) : date,
                obj.getString(new String[]{"n", "name"}).getString(),
                obj.getDecimal(new String[]{"v", "val", "value", "change"}).decimal
        );
    }


    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("t", DATE.export());
        obj.put("n", new JsonString(NAME));
        obj.put("v", new JsonDecimal(CHANGE));
        return obj;
    }

    public JsonObject fullExport() throws JsonFormattingException {
        JsonObject obj = export();
        obj.put("u", new JsonDecimal(UUID));
        return obj;
    }

    public String print() {
        return "(" + DATE + ") " + CHANGE;
    }

    @Override
    public String toString() {
        return NAME + ": " + print();
    }
}
