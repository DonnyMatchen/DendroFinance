package com.donny.dendrofinance.capsules;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

import java.sql.SQLException;

public class TemplateCapsule extends Capsule {
    private String name;
    private final long REF;

    public TemplateCapsule(String name, long ref, Instance curInst) {
        super(curInst);
        this.name = name;
        REF = ref;
    }

    public TemplateCapsule(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) throws SQLException {
        super(curInst);
        String candidate = obj.getString(new String[]{"n", "name"}).getString();
        boolean safe = CURRENT_INSTANCE.UNIQUE_HANDLER.checkName(candidate, "TEMPLATES");
        if (!safe) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "Clashing Name: " + candidate);
            if (mode == ImportHandler.ImportMode.OVERWRITE) {
                name = candidate;
            } else {
                name = CURRENT_INSTANCE.UNIQUE_HANDLER.generateName(candidate, "TEMPLATES");
            }
        } else {
            name = candidate;
        }
        REF = obj.getDecimal(new String[]{"r", "ref"}).decimal.longValue();
    }

    public TemplateCapsule(JsonObject obj, Instance curInst) {
        super(curInst);
        name = obj.getString(new String[]{"n", "name"}).getString();
        REF = obj.getDecimal(new String[]{"r", "ref"}).decimal.longValue();
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

    public TransactionCapsule getTemplate() {
        return CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TRANSACTIONS.get(REF);
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("n", new JsonString(name));
        obj.put("r", new JsonDecimal(REF));
        return obj;
    }

    @Override
    public String toString() {
        return name + "\t" + Long.toUnsignedString(REF);
    }
}
