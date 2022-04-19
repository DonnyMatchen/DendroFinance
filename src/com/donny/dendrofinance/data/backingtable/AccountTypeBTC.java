package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.AccountTypeEditGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.BackingEditGui;
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
    public BackingEditGui<AccountType> getEditDialog(BackingTableGui<AccountType> caller, int index) {
        return new AccountTypeEditGui(caller, this, index, CURRENT_INSTANCE);
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
    public ArrayList<String[]> getContents() {
        ArrayList<String[]> out = new ArrayList<>();
        for (AccountType a : TABLE) {
            out.add(new String[]{
                    a.NAME, "" + a.TYPE, a.inUse(CURRENT_INSTANCE) ? "X" : ""
            });
        }
        return out;
    }

    @Override
    public String getIdentifier(int index) {
        return TABLE.get(index).NAME;
    }

    @Override
    public int getIndex(String identifier) {
        for (AccountType accTyp : TABLE) {
            if (accTyp.NAME.equalsIgnoreCase(identifier)) {
                return TABLE.indexOf(accTyp);
            }
        }
        return -1;
    }

    @Override
    public boolean canMove(int index) {
        return true;
    }

    @Override
    public boolean canEdit(int index) {
        return !TABLE.get(index).inUse(CURRENT_INSTANCE);
    }

    @Override
    public boolean canRemove(int index) {
        return !TABLE.get(index).inUse(CURRENT_INSTANCE);
    }

    @Override
    public void sort() {
    }

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (AccountType accTyp : TABLE) {
            try {
                array.ARRAY.add(accTyp.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Account Type: " + accTyp);
            }
        }
        return array;
    }
}
