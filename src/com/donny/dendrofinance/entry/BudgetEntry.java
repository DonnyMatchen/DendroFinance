package com.donny.dendrofinance.entry;

import com.donny.dendrofinance.header.BudgetHeader;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LJson;
import com.donny.dendrofinance.types.LString;

import java.math.BigDecimal;

public class BudgetEntry extends Entry<BudgetHeader> {
    public BudgetEntry(Instance curInst) {
        super(curInst, "BudgetHeader");
    }

    public BudgetEntry(JsonObject obj, Instance curInst) {
        super(obj, curInst);
    }

    public static BudgetEntry defaultBudget(String name, Instance curInst) {
        JsonObject obj = new JsonObject();
        if (!curInst.DATA_HANDLER.getBudgetTypes().isEmpty()) {
            for (String s : curInst.DATA_HANDLER.getBudgetTypes()) {
                obj.FIELDS.put(s, new JsonDecimal(BigDecimal.ZERO));
            }
        }
        BudgetEntry entry = new BudgetEntry(curInst);
        entry.insert(new LString(name), new LJson(obj));
        return entry;
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
