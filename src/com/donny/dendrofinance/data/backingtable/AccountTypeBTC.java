package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.AccountTypeEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.util.ArrayList;

public class AccountTypeBTC extends BackingTableCore<AccountType> {
    public AccountTypeBTC(Instance curInst) {
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
            TABLE.add(new AccountType(obj));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Broad Type", "In Use"
        };
    }

    @Override
    public int contentIdentifierIndex() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (AccountType a : TABLE) {
            if (a.NAME.toLowerCase().contains(search.toLowerCase())
                    || a.TYPE.toString().toLowerCase().contains(search.toLowerCase())) {
                out.add(new String[]{
                        a.NAME, "" + a.TYPE, a.inUse(CURRENT_INSTANCE) ? "X" : ""
                });
            }
        }
        return out;
    }

    @Override
    public String getIdentifier(int index) {
        return TABLE.get(index).NAME;
    }

    @Override
    public int getIndex(String identifier) {
        for (int i = 0; i < TABLE.size(); i++) {
            AccountType accTyp = TABLE.get(i);
            if (accTyp.NAME.equalsIgnoreCase(identifier)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean canMove(String identifier) {
        return true;
    }

    @Override
    public boolean canEdit(String identifier) {
        return !getElement(identifier).inUse(CURRENT_INSTANCE);
    }

    @Override
    public boolean canRemove(String identifier) {
        return !getElement(identifier).inUse(CURRENT_INSTANCE);
    }

    @Override
    public void sort() {
    }

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (AccountType accTyp : TABLE) {
            try {
                array.add(accTyp.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Account Type: " + accTyp);
            }
        }
        return array;
    }
}
