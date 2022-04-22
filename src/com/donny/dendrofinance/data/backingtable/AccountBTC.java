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
    public ArrayList<String[]> getContents() {
        ArrayList<String[]> out = new ArrayList<>();
        for (Account a : TABLE) {
            out.add(new String[]{
                    "" + a.getAid(), a.getName(), a.getCurrency().getName(), a.getAccountType().NAME,
                    a.getBroadAccountType().toString(), a.getBudgetType(), !a.EXPORT ? "X" : "",
                    a.inUse() ? "X" : ""
            });
        }
        return out;
    }

    @Override
    public String getIdentifier(int index) {
        return TABLE.get(index).getName();
    }

    @Override
    public int getIndex(String identifier) {
        for (Account acc : TABLE) {
            if (acc.getName().equalsIgnoreCase(identifier)) {
                return TABLE.indexOf(acc);
            }
        }
        return -1;
    }

    @Override
    public boolean canMove(int index) {
        return TABLE.get(index).EXPORT;
    }

    @Override
    public boolean canEdit(int index) {
        return TABLE.get(index).EXPORT;
    }

    @Override
    public boolean canRemove(int index) {
        return !TABLE.get(index).inUse();
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
                    array.ARRAY.add(acc.export());
                } catch (JsonFormattingException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Account: " + acc);
                }
            }
        }
        return array;
    }
}
