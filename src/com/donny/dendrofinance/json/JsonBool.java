package com.donny.dendrofinance.json;

public class JsonBool extends JsonItem {
    public boolean bool;

    public JsonBool(String raw) {
        this(raw.equalsIgnoreCase("true"));
    }

    public JsonBool(boolean bool) {
        super(JsonType.BOOL);
        this.bool = bool;
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
