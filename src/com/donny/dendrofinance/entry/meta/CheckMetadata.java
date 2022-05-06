package com.donny.dendrofinance.entry.meta;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

public class CheckMetadata {
    public final int REF;
    public final LDate ISSUED, CASHED;
    public final String CHECK_NUMBER;
    public final BigDecimal VALUE;
    private final Instance CURRENT_INSTANCE;

    public CheckMetadata(int uuid, LDate issued, LDate cashed, String number, BigDecimal value, Instance curInst) {
        REF = uuid;
        ISSUED = issued;
        CASHED = cashed;
        CHECK_NUMBER = number;
        VALUE = value;
        CURRENT_INSTANCE = curInst;
    }

    public CheckMetadata(int uuid, LDate issued, JsonObject obj, Instance curInst) {
        this(
                uuid,
                obj.containsKey("issued") ? new LDate(obj.getDecimal("issued"), curInst) : issued,
                new LDate(obj.getDecimal("cashed"), curInst),
                obj.getString("number").getString(),
                obj.getDecimal("value").decimal,
                curInst
        );
    }

    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("issued", new JsonDecimal(BigDecimal.valueOf(ISSUED.getTime())));
        obj.put("cashed", new JsonDecimal(BigDecimal.valueOf(CASHED.getTime())));
        obj.put("number", new JsonString(CHECK_NUMBER));
        obj.put("value", new JsonDecimal(VALUE));
        return obj;
    }

    @Override
    public String toString() {
        return "(" + ISSUED.toString() + " -- " + CASHED.toString() + ")\n(" + CHECK_NUMBER + ") " + CURRENT_INSTANCE.main.encode(VALUE);
    }
}