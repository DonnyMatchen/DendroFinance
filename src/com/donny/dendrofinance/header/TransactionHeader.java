package com.donny.dendrofinance.header;

import com.donny.dendrofinance.entry.Field;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.types.LJson;
import com.donny.dendrofinance.types.LString;

import java.util.Arrays;

public class TransactionHeader extends Header {
    public TransactionHeader(Instance curInst) {
        super();
        PROTOTYPE.addAll(Arrays.asList(
                new Field("date", new LDate(0, curInst)),
                new Field("entity", new LString("")),
                new Field("items", new LString("")),
                new Field("description", new LString("")),
                new Field("accounts", new LAccountSet(curInst)),
                new Field("meta-data", new LJson(new JsonObject()))
        ));
    }

    @Override
    public String getName() {
        return "TransactionHeader";
    }
}
