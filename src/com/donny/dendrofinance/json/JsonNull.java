package com.donny.dendrofinance.json;

public class JsonNull extends JsonItem {
    public JsonNull() {
        super(JsonType.NULL);
    }

    @Override
    public String toString() {
        return "null";
    }

    @Override
    public String print(int scope) {
        return toString();
    }
}
