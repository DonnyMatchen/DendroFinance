package com.donny.dendrofinance.account;

import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;
import com.donny.dendroroot.util.UniqueName;

public class AccountType implements UniqueName {
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

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(NAME));
        obj.put("type", new JsonString(TYPE.toString()));
        return obj;
    }

    @Override
    public String toString() {
        return NAME;
    }
}
