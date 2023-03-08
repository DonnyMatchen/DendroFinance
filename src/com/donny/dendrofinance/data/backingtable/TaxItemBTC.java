package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.tax.TaxItem;

import java.util.ArrayList;
import java.util.Comparator;

public class TaxItemBTC extends BackingTableCore<TaxItem> {
    public TaxItemBTC(Instance curInst) {
        super(curInst, true);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Tax Items";
        } else {
            return "Tax Item";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<TaxItem> caller, int index) {

    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            add(new TaxItem(obj));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[0];
    }

    @Override
    public int contentIdentifierIndex() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        return null;
    }

    @Override
    public boolean canMove(String identifier) {
        return false;
    }

    @Override
    public boolean canEdit(String identifier) {
        return false;
    }

    @Override
    public boolean canRemove(String identifier) {
        return false;
    }

    @Override
    public void sort() {
        ArrayList<TaxItem> list = new ArrayList<>();
        for (TaxItem t : this) {
            list.add(t);
        }
        list.sort(Comparator.comparing(taxItem -> taxItem.NAME));
        KEYS.clear();
        for (TaxItem t : list) {
            KEYS.add(t.getName());
        }
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (String key : MAP.keySet()) {
            try {
                array.add(MAP.get(key).export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed taxItem: " + MAP.get(key).NAME);
            }
        }
        return array;
    }
}
