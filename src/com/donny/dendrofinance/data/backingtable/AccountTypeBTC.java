package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.AccountTypeEditGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonObject;

import java.util.ArrayList;

public class AccountTypeBTC extends BackingTableCore<AccountType> {
    public AccountTypeBTC(ProgramInstance curInst) {
        super(curInst, false);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Account Types";
        } else {
            return "Account Type";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<AccountType> caller, int index) {
        new AccountTypeEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            add(new AccountType(obj));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Broad Type"
        };
    }

    @Override
    public int contentIdentifierIndex() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (String key : KEYS) {
            AccountType a = MAP.get(key);
            if (a.NAME.toLowerCase().contains(search.toLowerCase())
                    || a.TYPE.toString().toLowerCase().contains(search.toLowerCase())) {
                out.add(new String[]{
                        a.NAME, String.valueOf(a.TYPE)
                });
            }
        }
        return out;
    }

    @Override
    public boolean canMove(String identifier) {
        return true;
    }

    @Override
    public boolean canEdit(String identifier) {
        return true;
    }

    @Override
    public boolean canRemove(String identifier) {
        return true;
    }

    @Override
    public void sort() {
    }
}
