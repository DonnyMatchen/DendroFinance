package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.data.ImportHandler;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.util.ExportableToJson;

import java.io.Serializable;

public abstract class Entry implements ExportableToJson, Serializable {
    protected final Instance CURRENT_INSTANCE;
    private final long UUID;
    public boolean clashing;

    public Entry(Instance curInst) {
        UUID = curInst.UUID_HANDLER.generateUUID();
        CURRENT_INSTANCE = curInst;
    }

    public Entry(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        if (obj.containsKey("_uuid")) {
            long candidate = obj.getDecimal("_uuid").decimal.longValue();
            if (CURRENT_INSTANCE.UUID_HANDLER.UUIDS.contains(candidate)) {
                CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "Clashing UUID: " + candidate);
                clashing = true;
                if (mode == ImportHandler.ImportMode.OVERWRITE) {
                    UUID = candidate;
                } else {
                    UUID = CURRENT_INSTANCE.UUID_HANDLER.generateUUID();
                }
            } else {
                UUID = candidate;
                CURRENT_INSTANCE.UUID_HANDLER.UUIDS.add(UUID);
            }
        } else {
            UUID = curInst.UUID_HANDLER.generateUUID();
        }
    }

    public static Entry get(EntryType type, JsonObject obj, Instance curInst) {
        return switch (type) {
            case TRANSACTION -> new TransactionEntry(obj, ImportHandler.ImportMode.KEEP, curInst);
            case BUDGET -> new BudgetEntry(obj, ImportHandler.ImportMode.KEEP, curInst);
        };
    }

    public long getUUID() {
        return UUID;
    }

    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("_uuid", new JsonDecimal(UUID));
        return obj;
    }

    public abstract String toFlatString();
}
