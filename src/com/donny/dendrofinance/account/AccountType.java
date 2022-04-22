package com.donny.dendrofinance.account;

import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.util.ExportableToJsonObject;

public class AccountType implements ExportableToJsonObject {
    public final String NAME;
    public final BroadAccountType TYPE;

    public AccountType(String name, String type) {
        NAME = name;
        TYPE = BroadAccountType.fromString(type);
    }

    public AccountType(JsonObject obj) {
        NAME = obj.getString("name").getString();
        TYPE = BroadAccountType.fromString(obj.getString("type").getString());
    }

    public boolean inUse(Instance curInst) {
        for (TransactionEntry entry : curInst.DATA_HANDLER.readTransactions()) {
            for (AccountWrapper aw : entry.getAccounts()) {
                if (aw.ACCOUNT.getAccountType() == this) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.FIELDS.put("name", new JsonString(NAME));
        obj.FIELDS.put("type", new JsonString(TYPE.toString()));
        return obj;
    }
}
