package com.donny.dendrofinance.capsules;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.data.Capsule;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;

import java.math.BigDecimal;
import java.sql.SQLException;

public class BudgetCapsule extends Capsule {
    private final ProgramInstance CURRENT_INSTANCE;

    private String name;
    private JsonObject contents;

    public BudgetCapsule(String name, ProgramInstance curInst) {
        this(name, getEmptyBudget(curInst), curInst);
    }

    public BudgetCapsule(String name, JsonObject contents, ProgramInstance curInst) {
        super(curInst);
        CURRENT_INSTANCE = curInst;
        this.name = name;
        this.contents = contents;
    }

    public BudgetCapsule(JsonObject obj, ImportHandler.ImportMode mode, ProgramInstance curInst) throws SQLException {
        super(curInst);
        CURRENT_INSTANCE = curInst;
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

    public BudgetCapsule(JsonObject obj, ProgramInstance curInst) {
        super(curInst);
        CURRENT_INSTANCE = curInst;
        name = obj.getString(new String[]{"n", "name"}).getString();
        contents = obj.getObject(new String[]{"c", "contents"});
    }

    public BudgetCapsule(BudgetCapsule capsule, String name) {
        this(name, capsule.CURRENT_INSTANCE);
        contents = capsule.contents;
    }

    public static JsonObject getEmptyBudget(ProgramInstance curInst) {
        JsonObject obj = new JsonObject();
        for (Account account : curInst.ACCOUNTS) {
            if (!account.getBudgetType().isEmpty()) {
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

    public boolean containsAccount(String accountName) {
        return contents.get(accountName) != null;
    }

    public BigDecimal getValue(String accountName) {
        if (containsAccount(accountName)) {
            return contents.getDecimal(accountName).decimal;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void update() {
        CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.update(this);
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("n", new JsonString(name));
        obj.put("c", contents);
        return obj;
    }
}
