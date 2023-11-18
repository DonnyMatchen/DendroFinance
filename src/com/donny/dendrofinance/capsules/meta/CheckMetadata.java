package com.donny.dendrofinance.capsules.meta;

import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.util.ExportableToJson;

import java.math.BigDecimal;

public class CheckMetadata implements ExportableToJson {
    public final long REF;
    public final LDate ISSUED, CASHED;
    public final String CHECK_NUMBER;
    public final BigDecimal VALUE;
    private final ProgramInstance CURRENT_INSTANCE;

    public CheckMetadata(long uuid, LDate issued, LDate cashed, String number, BigDecimal value, ProgramInstance curInst) {
        REF = uuid;
        ISSUED = issued;
        CASHED = cashed;
        CHECK_NUMBER = number;
        VALUE = value;
        CURRENT_INSTANCE = curInst;
    }

    public CheckMetadata(long uuid, LDate issued, JsonObject obj, ProgramInstance curInst) {
        this(
                obj.containsKey(new String[]{"r", "ref"}) ? obj.getDecimal(new String[]{"r", "ref"}).decimal.longValue() : uuid,
                obj.containsKey(new String[]{"i", "start", "issued"}) ? new LDate(obj.getDecimal(new String[]{"i", "start", "issued"}), curInst) : issued,
                new LDate(obj.getDecimal(new String[]{"c", "end", "cashed"}), curInst),
                obj.getString(new String[]{"n", "count", "number"}).getString(),
                obj.getDecimal(new String[]{"v", "value"}).decimal,
                curInst
        );
    }

    public boolean isOutstanding() {
        return CASHED.getTime() == 0;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("i", ISSUED.export());
        obj.put("c", CASHED.export());
        obj.put("n", new JsonString(CHECK_NUMBER));
        obj.put("v", new JsonDecimal(VALUE));
        return obj;
    }

    public JsonObject fullExport() throws JsonFormattingException {
        JsonObject obj = export();
        obj.put("r", new JsonDecimal(REF));
        return obj;
    }

    @Override
    public String toString() {
        if (isOutstanding()) {
            return "(" + ISSUED.toDateString() + " -- ???)\n(" + CHECK_NUMBER + ") " + CURRENT_INSTANCE.main.encode(VALUE);
        } else {
            return "(" + ISSUED.toDateString() + " -- " + CASHED.toDateString() + ")\n(" + CHECK_NUMBER + ") " + CURRENT_INSTANCE.main.encode(VALUE);
        }
    }
}
