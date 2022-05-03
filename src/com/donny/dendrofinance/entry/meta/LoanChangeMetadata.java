package com.donny.dendrofinance.entry.meta;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

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

    public LoanChangeMetadata(long uuid, LDate date, JsonObject obj, Instance curInst) {
        this(uuid,
                obj.containsKey("date") ? new LDate(obj.getDecimal("date"), curInst) : date,
                obj.getString("name").getString(),
                obj.getDecimal("change").decimal
        );
    }


    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("date", new JsonDecimal(BigDecimal.valueOf(DATE.getTime())));
        obj.put("name", new JsonString(NAME));
        obj.put("change", new JsonDecimal(CHANGE));
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
