package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.header.Header;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.*;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Entry<T extends Header> {
    public final ArrayList<Field> VALUES;
    protected final T HEADER;
    protected final Instance CURRENT_INSTANCE;
    private final long UUID;

    public Entry(Instance curInst, String header) {
        UUID = curInst.UUID_HANDLER.generateUUID();
        HEADER = (T) Header.getHeader(header, curInst);
        VALUES = HEADER.getBlank();
        CURRENT_INSTANCE = curInst;
    }

    public Entry(JsonObject obj, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        HEADER = (T) Header.getHeader(obj.getString("_header").getString(), curInst);
        VALUES = HEADER.getBlank();
        JsonObject types = obj.getObject("_types");
        if (obj.containsKey("_uuid")) {
            long candidate = obj.getDecimal("_uuid").decimal.longValue();
            if (CURRENT_INSTANCE.UUID_HANDLER.UUIDS.contains(candidate)) {
                CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "Clashing UUID: " + candidate);
                UUID = CURRENT_INSTANCE.UUID_HANDLER.generateUUID();
            } else {
                UUID = candidate;
                CURRENT_INSTANCE.UUID_HANDLER.UUIDS.add(UUID);
            }
        } else {
            UUID = curInst.UUID_HANDLER.generateUUID();
        }
        for (String key : types.getFields()) {
            insertIntoField(new Field(key, types.getString(key), obj.get(key), curInst));
        }

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

    public LType get(String fieldName) {
        for (Field field : VALUES) {
            if (field.getName().equals(fieldName)) {
                return field.getValue();
            }
        }
        for (Field field : HEADER.getBlank()) {
            if (field.getName().equals(fieldName)) {
                return field.getValue();
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "(Get) Field \"" + fieldName + "\" not found in: " + UUID);
        return null;
    }

    public LString getString(String fieldName) {
        return (LString) get(fieldName);
    }

    public LDecimal getDecimal(String fieldName) {
        return (LDecimal) get(fieldName);
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

    public final void insertIntoField(Field container) {
        for (Field field : VALUES) {
            if (field.getType() == container.getType() && field.getName().equals(container.getName())) {
                field.setValue(container.getValue());
                break;
            }
        }
        for (Field field : HEADER.getBlank()) {
            if (field.getType() == container.getType() && field.getName().equals(container.getName())) {
                field.setValue(container.getValue());
                break;
            }
        }
    }

    public void insertIntoField(String name, LType value) {
        boolean flag = true;
        for (Field field : VALUES) {
            if (field.getType() == FieldType.resolve(value) && field.getName().equals(name)) {
                flag = !field.setValue(value);
                break;
            }
        }
        for (Field field : HEADER.getBlank()) {
            if (field.getType() == FieldType.resolve(value) && field.getName().equals(name)) {
                flag = !field.setValue(value);
                break;
            }
        }
        if (flag) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "(Insert) Field \"" + name + "\" not found in: " + UUID);
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
        obj.put("_header", new JsonString(HEADER.getName()));
        obj.put("_uuid", new JsonDecimal(BigDecimal.valueOf(UUID)));
        obj.put("_types", new JsonObject());
        for (Field field : VALUES) {
            obj.getObject("_types").put(field.getName(), new JsonString(field.getType().toString()));
            obj.put(field.getName(), field.getValue().export());
        }
        return obj;
    }
}
