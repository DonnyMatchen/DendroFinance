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
        for (String key : OBJECT.getFields()) {
            if (b.containsKey(key)) {
                if (!OBJECT.get(key).toString().equals(b.get(key).toString())) {
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
        return Integer.compare(OBJECT.getFields().size(), b.getFields().size());
    }

    @Override
    public JsonItem export() {
        return OBJECT;
    }

    @Override
    public boolean isDefault() {
        return OBJECT.getFields().isEmpty();
    }
}
