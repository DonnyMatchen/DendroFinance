package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.header.Header;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.*;

import java.math.BigDecimal;
import java.util.HashMap;

public class Entry<T extends Header> {
    public final Instance CURRENT_INSTANCE;
    public final HashMap<String, Field> VALUES;
    protected final T HEADER;
    private final long UUID;

    public Entry(Instance curInst, String header) {
        UUID = curInst.UUID_HANDLER.generateUUID();
        HEADER = (T) Header.getHeader(header, curInst);
        VALUES = HEADER.getBlank();
        CURRENT_INSTANCE = curInst;
    }

    public Entry(JsonObject obj, Instance curInst) {
        HEADER = (T) Header.getHeader(obj.getString("_header").getString(), curInst);
        VALUES = HEADER.getBlank();
        JsonObject types = obj.getObject("_types");
        if (obj.FIELDS.containsKey("_uuid")) {
            UUID = obj.getDecimal("_uuid").decimal.longValue();
        } else {
            UUID = curInst.UUID_HANDLER.generateUUID();
        }
        for (String key : types.FIELDS.keySet()) {
            insertIntoField(key, new Field(key, types.getString(key), obj.FIELDS.get(key), curInst));
        }
        CURRENT_INSTANCE = curInst;
    }

    public static Entry get(EntryType type, JsonObject obj, Instance curInst) {
        return switch (type) {
            case TRANSACTION -> new TransactionEntry(obj, curInst);
            case BUDGET -> new BudgetEntry(obj, curInst);
        };
    }

    public long getUUID() {
        return UUID;
    }

    public void clean() {
        HashMap<String, Field> fields = new HashMap<>(VALUES);
        for (String key : fields.keySet()) {
            boolean flag = true;
            for (Field g : HEADER.getProto()) {
                if (g.getName().equals(fields.get(key).getName())) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                VALUES.remove(key);
            }
        }
    }

    public LType get(String fieldName) {
        if (VALUES.containsKey(fieldName)) {
            return VALUES.get(fieldName).getValue();
        }
        if (HEADER.getBlank().containsKey(fieldName)) {
            return HEADER.getBlank().get(fieldName).getValue();
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "(Get) Field \"" + fieldName + "\" not found in: " + UUID);
        return null;
    }

    public LString getString(String fieldName) {
        return (LString) get(fieldName);
    }

    public LDecimal getDecimal(String fieldName) {
        return (LDecimal) get(fieldName);
    }

    public LDecimalSet getDecimalSet(String fieldName) {
        return (LDecimalSet) get(fieldName);
    }

    public LDate getDate(String fieldName) {
        return (LDate) get(fieldName);
    }

    public LInt getInt(String fieldName) {
        return (LInt) get(fieldName);
    }

    public LAccountSet getAccountSet(String fieldName) {
        return (LAccountSet) get(fieldName);
    }

    public LJson getJson(String fieldName) {
        return (LJson) get(fieldName);
    }

    public final void insertIntoField(String name, Field field) {
        if (VALUES.containsKey(name)) {
            if (VALUES.get(name).getType() == field.getType() && VALUES.get(name).getName().equals(field.getName())) {
                insertIntoField(name, field.getValue());
            }
        }
    }

    public void insertIntoField(String name, LType value) {
        if (VALUES.containsKey(name)) {
            if (VALUES.get(name).getValue().getClass() == value.getClass()) {
                VALUES.get(name).setValue(value);
            }
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "(Insert) Field \"" + name + "\" not found in: " + UUID);
        }
    }

    @Override
    public String toString() {
        return toFlatString();
    }

    public String toFlatString() {
        StringBuilder sb = new StringBuilder(UUID + "\t");
        for (Field f : HEADER.getProto()) {
            String ths = f.getName();
            sb.append(get(ths).toString()).append("\t");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.FIELDS.put("_header", new JsonString(HEADER.getName()));
        obj.FIELDS.put("_uuid", new JsonDecimal(BigDecimal.valueOf(UUID)));
        obj.FIELDS.put("_types", new JsonObject());
        for (String key : VALUES.keySet()) {
            Field f = VALUES.get(key);
            obj.getObject("_types").FIELDS.put(key, new JsonString(f.getType().toString()));
            obj.FIELDS.put(key, f.getValue().export());
        }
        return obj;
    }

    public int fieldsSize() {
        return VALUES.keySet().size();
    }
}
