package com.donny.dendrofinance.json;

public class JsonBool extends JsonItem {
    public boolean bool;

    public JsonBool(String raw) {
        super(JsonType.BOOL);
        bool = raw.equalsIgnoreCase("true");
    }

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    @Override
    public String toString() {
        return "" + bool;
    }

    @Override
    public String print(int scope) {
        return toString();
    }
}
