package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonString;

public class LString extends LType<LString> {
    public final String VALUE;

    public LString(String str) {
        VALUE = str.trim();
    }

    @Override
    public boolean sameAs(LString b) {
        return VALUE.equals(b.VALUE);
    }

    @Override
    public int compare(LString b) {
        return VALUE.compareTo(b.VALUE);
    }

    @Override
    public JsonItem export() throws JsonFormattingException {
        return new JsonString(VALUE);
    }

    @Override
    public boolean isDefault() {
        return sameAs(new LString(""));
    }

    @Override
    public String toString() {
        return VALUE;
    }
}
