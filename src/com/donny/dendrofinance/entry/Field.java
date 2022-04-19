package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.*;

import static com.donny.dendrofinance.entry.FieldType.fromString;
import static com.donny.dendrofinance.entry.FieldType.resolve;

public class Field {
    private final String NAME;
    private final FieldType TYPE;
    private LType value;

    public Field(String name, LType value) {
        NAME = name;
        this.value = value;
        TYPE = resolve(value);
    }

    public Field(String name, JsonString type, JsonItem item, Instance curInst) {
        NAME = name;
        TYPE = fromString(type.getString());
        switch (TYPE) {
            case DATE -> value = new LDate((JsonDecimal) item, curInst);
            case SET_ACCOUNT -> value = new LAccountSet((JsonArray) item, curInst);
            case INT -> value = new LInt(((JsonDecimal) item).decimal.intValue());
            case JSON -> value = new LJson((JsonObject) item);
            case STRING -> value = new LString(((JsonString) item).getString());
            case DECIMAL -> value = new LDecimal(((JsonDecimal) item).decimal);
            case SET_DECIMAL -> value = new LDecimalSet((JsonArray) item);
            case NULL -> value = null;
        }
    }

    public String getName() {
        return NAME;
    }

    public LType getValue() {
        return value;
    }

    public boolean setValue(LType value) {
        if (FieldType.resolve(value).equals(TYPE)) {
            this.value = value;
            return true;
        } else {
            return false;
        }
    }

    public FieldType getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
