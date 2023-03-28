package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.AccountEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.util.ArrayList;

public class AccountBTC extends BackingTableCore<Account> {
    public AccountBTC(Instance curInst) {
        super(curInst, true);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Accounts";
        } else {
            return "Account";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<Account> caller, int index) {
        new AccountEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            add(new Account(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "AID", "Name", "Currency", "Type", "Column", "Budget", "Auto-Generated"
        };
    }

    @Override
    public int contentIdentifierIndex() {
        return 1;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (String key : KEYS) {
            Account a = MAP.get(key);
            boolean allow = ((String.valueOf(a.getAid())).contains(search)
                    || a.getName().toLowerCase().contains(search.toLowerCase())
                    || a.getCurrency().getName().toLowerCase().contains(search.toLowerCase())
                    || a.getCurrency().toString().toLowerCase().contains(search.toLowerCase())
                    || a.getAccountType().NAME.toLowerCase().contains(search.toLowerCase())
                    || a.getBudgetType().toLowerCase().contains(search.toLowerCase())
                    || a.getBroadAccountType().toString().toLowerCase().contains(search.toLowerCase()));
            if (allow) {
                out.add(new String[]{
                        String.valueOf(a.getAid()), a.getName(), a.getCurrency().getName(), a.getAccountType().NAME,
                        a.getBroadAccountType().toString(), a.getBudgetType(), !a.EXPORT ? "X" : ""
                });
            }
        }
        return out;
    }

    @Override
    public boolean canMove(String identifier) {
        return getElement(identifier).EXPORT;
    }

    @Override
    public boolean canEdit(String identifier) {
        return getElement(identifier).EXPORT;
    }

    @Override
    public boolean canRemove(String identifier) {
        return canMove(identifier);
    }

    @Override
    public void sort() {
        ArrayList<Account> list = new ArrayList<>();
        for (Account a : this) {
            list.add(a);
        }
        list.sort(Account::compareTo);
        KEYS.clear();
        for (Account a : list) {
            KEYS.add(a.getName());
        }
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray out = new JsonArray();
        for (String key : KEYS) {
            Account acc = MAP.get(key);
            if (acc.EXPORT) {
                try {
                    out.add(MAP.get(key).export());
                } catch (JsonFormattingException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to export: " + key + "\n" + e);
                }
            }
        }
        return out;
    }
}
