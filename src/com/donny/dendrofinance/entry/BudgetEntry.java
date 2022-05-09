package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.header.BudgetHeader;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LJson;
import com.donny.dendrofinance.types.LString;

import java.math.BigDecimal;

public class BudgetEntry extends Entry<BudgetHeader> {
    public BudgetEntry(Instance curInst) {
        super(curInst, "BudgetHeader");
    }

    public BudgetEntry(String name, Instance curInst) {
        this(curInst);
        insert(new LString(name), new LJson(getEmptyBudget(curInst)));
    }

    public BudgetEntry(JsonObject obj, Instance curInst) {
        super(obj, curInst);
    }

    public BudgetEntry(BudgetEntry entry, String name) {
        this(entry.CURRENT_INSTANCE);
        try {
            insert(new LString(name), new LJson(new JsonObject(entry.getBudget().toString())));
        } catch (JsonFormattingException ex) {
            insert(new LString(name), new LJson(getEmptyBudget(CURRENT_INSTANCE)));
        }
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

    public void insert(LString name, LJson contents) {
        insertIntoField("name", name);
        insertIntoField("contents", contents);
    }

    public String getName() {
        return getString("name").VALUE;
    }

    public JsonObject getBudget() {
        return getJson("contents").OBJECT;
    }
}
