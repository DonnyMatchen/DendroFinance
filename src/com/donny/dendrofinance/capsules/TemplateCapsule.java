package com.donny.dendrofinance.capsules;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.data.Capsule;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;

import java.sql.SQLException;

public class TemplateCapsule extends Capsule {
    private final ProgramInstance CURRENT_INSTANCE;
    private String name;
    private final long REF;

    public TemplateCapsule(String name, long ref, ProgramInstance curInst) {
        super(curInst);
        CURRENT_INSTANCE = curInst;
        this.name = name;
        REF = ref;
    }

    public TemplateCapsule(JsonObject obj, ImportHandler.ImportMode mode, ProgramInstance curInst) throws SQLException {
        super(curInst);
        CURRENT_INSTANCE = curInst;
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

    public TemplateCapsule(JsonObject obj, ProgramInstance curInst) {
        super(curInst);
        CURRENT_INSTANCE = curInst;
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
        return CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.get(REF);
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
