package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

public class TemplateEntry extends Entry {
    private String name;
    private final long REF;

    public TemplateEntry(String name, long ref, Instance curInst) {
        super(curInst);
        this.name = name;
        REF = ref;
    }

    public TemplateEntry(long ref, Instance curInst) {
        this("UNKNOWN", ref, curInst);
    }

    public TemplateEntry(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) {
        super(obj, mode, curInst);
        name = obj.getString("name").getString();
        REF = obj.getDecimal("ref").decimal.longValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        obj.put("name", new JsonString(name));
        obj.put("ref", new JsonDecimal(REF));
        return obj;
    }

    @Override
    public String toFlatString() {
        return Long.toUnsignedString(getUUID()) + "\t" + name + "\n" + getTemplate().toFlatString();
    }

    @Override
    public String toString() {
        return name;
    }
}
