package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

public class TemplateEntry extends Entry {
    private final String NAME;
    private final long REF;

    public TemplateEntry(String name, long ref, Instance curInst) {
        super(curInst);
        NAME = name;
        REF = ref;
    }

    public TemplateEntry(long ref, Instance curInst) {
        this("UNKNOWN", ref, curInst);
    }

    public TemplateEntry(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) {
        super(obj, mode, curInst);
        NAME = obj.getString("name").getString();
        REF = obj.getDecimal("ref").decimal.longValue();
    }

    public String getName() {
        return NAME;
    }

    public long getRef() {
        return REF;
    }

    public TransactionEntry getTemplate() {
        return CURRENT_INSTANCE.DATA_HANDLER.getTransactionEntry(REF);
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = super.export();
        obj.put("name", new JsonString(NAME));
        obj.put("ref", new JsonDecimal(REF));
        return obj;
    }

    @Override
    public String toFlatString() {
        return Long.toUnsignedString(getUUID()) + "\t" + NAME + "\n" + getTemplate().toFlatString();
    }

    @Override
    public String toString() {
        return NAME;
    }
}
