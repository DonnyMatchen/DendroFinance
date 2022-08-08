package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.data.ImportHandler;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

import java.math.BigDecimal;

public class BudgetEntry extends Entry {
    private String name;
    private JsonObject contents;

    public BudgetEntry(Instance curInst) {
        super(curInst);
        name = "DEFAULT";
        contents = new JsonObject();
    }

    public BudgetEntry(String name, Instance curInst) {
        this(curInst);
        this.name = name;
        contents = getEmptyBudget(curInst);
    }

    public BudgetEntry(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) {
        super(obj, mode, curInst);
        name = obj.getString("name").getString();
        contents = obj.getObject("contents");
    }

    public BudgetEntry(BudgetEntry entry, String name) {
        this(entry.CURRENT_INSTANCE);
        this.name = name;
        contents = entry.contents;
    }

    public static JsonObject getEmptyBudget(Instance curInst) {
        JsonObject obj = new JsonObject();
        for (Account account : curInst.ACCOUNTS) {
            if (!account.getBudgetType().equals("")) {
                obj.put(account.getName(), new JsonDecimal(BigDecimal.ZERO));
            }
        }
        return obj;
    }

    public void insert(String name, JsonObject contents) {
        this.name = name;
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public JsonObject getBudget() {
        return contents;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = super.export();
        obj.put("name", new JsonString(name));
        obj.put("contents", contents);
        return obj;
    }

    @Override
    public String toFlatString() {
        return Long.toUnsignedString(getUUID()) + "\t" + name + "\t" + contents.toString();
    }
}
