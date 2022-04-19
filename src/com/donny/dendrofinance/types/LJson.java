package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;

public class LJson extends LType<JsonObject> {
    public final JsonObject OBJECT;

    public LJson(JsonObject obj) {
        OBJECT = obj;
    }

    @Override
    public boolean sameAs(JsonObject b) {
        for (String key : OBJECT.FIELDS.keySet()) {
            if (b.FIELDS.containsKey(key)) {
                if (!OBJECT.FIELDS.get(key).toString().equals(b.FIELDS.get(key).toString())) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compare(JsonObject b) {
        return Integer.compare(OBJECT.FIELDS.keySet().size(), b.FIELDS.keySet().size());
    }

    @Override
    public JsonItem export() {
        return OBJECT;
    }

    @Override
    public boolean isDefault() {
        return OBJECT.FIELDS.keySet().isEmpty();
    }
}
