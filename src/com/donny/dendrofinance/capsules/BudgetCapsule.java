package com.donny.dendrofinance.capsules;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

import java.math.BigDecimal;
import java.sql.SQLException;

public class BudgetCapsule extends Capsule {
    private String name;
    private JsonObject contents;

    public BudgetCapsule(String name, Instance curInst) {
        this(name, getEmptyBudget(curInst), curInst);
    }

    public BudgetCapsule(String name, JsonObject contents, Instance curInst) {
        super(curInst);
        this.name = name;
        this.contents = contents;
    }

    public BudgetCapsule(JsonObject obj, ImportHandler.ImportMode mode, Instance curInst) throws SQLException {
        super(curInst);
        String candidate = obj.getString(new String[]{"n", "name"}).getString();
        boolean safe = CURRENT_INSTANCE.UNIQUE_HANDLER.checkName(candidate, "BUDGETS");
        if (!safe) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "Clashing name: " + candidate);
            if (mode == ImportHandler.ImportMode.OVERWRITE) {
                name = candidate;
            } else {
                name = CURRENT_INSTANCE.UNIQUE_HANDLER.generateName(candidate, "BUDGETS");
            }
        } else {
            name = candidate;
        }
        contents = obj.getObject(new String[]{"c", "contents"});
    }

    public BudgetCapsule(JsonObject obj, Instance curInst) {
        super(curInst);
        name = obj.getString(new String[]{"n", "name"}).getString();
        contents = obj.getObject(new String[]{"c", "contents"});
    }

    public BudgetCapsule(BudgetCapsule capsule, String name) {
        this(name, capsule.CURRENT_INSTANCE);
        contents = capsule.contents;
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
        JsonObject obj = new JsonObject();
        obj.put("n", new JsonString(name));
        obj.put("c", contents);
        return obj;
    }
}
