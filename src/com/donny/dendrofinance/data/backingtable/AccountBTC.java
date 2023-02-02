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
            TABLE.add(new Account(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "AID", "Name", "Currency", "Type", "Column", "Budget", "Auto-Generated", "In Use"
        };
    }

    @Override
    public int contentIdentifierIndex() {
        return 1;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (Account a : TABLE) {
            String check = search;
            boolean flag = a.inUse(), allow = true;
            if (check.contains("$U")) {
                check = check.replace("$U", "").trim();
                if (!flag) {
                    allow = false;
                }
            }
            if (check.contains("$u")) {
                check = check.replace("$u", "").trim();
                if (flag) {
                    allow = false;
                }
            }
            if (!((a.getAid() + "").contains(check)
                    || a.getName().toLowerCase().contains(check.toLowerCase())
                    || a.getCurrency().getName().toLowerCase().contains(check.toLowerCase())
                    || a.getCurrency().toString().toLowerCase().contains(check.toLowerCase())
                    || a.getAccountType().NAME.toLowerCase().contains(check.toLowerCase())
                    || a.getBudgetType().toLowerCase().contains(check.toLowerCase())
                    || a.getBroadAccountType().toString().toLowerCase().contains(check.toLowerCase()))) {
                allow = false;
            }
            if (allow) {
                out.add(new String[]{
                        "" + a.getAid(), a.getName(), a.getCurrency().getName(), a.getAccountType().NAME,
                        a.getBroadAccountType().toString(), a.getBudgetType(), !a.EXPORT ? "X" : "",
                        flag ? "X" : ""
                });
            }
        }
        return out;
    }

    @Override
    public String getIdentifier(int index) {
        return TABLE.get(index).getName();
    }

    @Override
    public int getIndex(String identifier) {
        for (int i = 0; i < TABLE.size(); i++) {
            Account acc = TABLE.get(i);
            if (acc.getName().equalsIgnoreCase(identifier)) {
                return i;
            }
        }
        return -1;
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
        return canMove(identifier) && !getElement(identifier).inUse();
    }

    @Override
    public void sort() {
        TABLE.sort(Account::compareTo);
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (Account acc : TABLE) {
            if (acc.EXPORT) {
                try {
                    array.add(acc.export());
                } catch (JsonFormattingException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Account: " + acc);
                }
            }
        }
        return array;
    }
}
