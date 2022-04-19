package com.donny.dendrofinance.header;

import com.donny.dendrofinance.entry.Field;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LJson;
import com.donny.dendrofinance.types.LString;

import java.util.Arrays;

public class BudgetHeader extends Header {
    public BudgetHeader() {
        super();
        PROTOTYPE.addAll(Arrays.asList(
                new Field("name", new LString("")),
                new Field("contents", new LJson(new JsonObject()))
        ));
    }

    @Override
    public String getName() {
        return "BudgetHeader";
    }
}
