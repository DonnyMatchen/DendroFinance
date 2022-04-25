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
    public void getEditDialog(BackingTableGui<TaxItem> caller, int index) {

    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            TABLE.add(new TaxItem(obj));
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
    public String getIdentifier(int index) {
        return TABLE.get(index).NAME;
    }

    @Override
    public int getIndex(String identifier) {
        for (int i = 0; i < TABLE.size(); i++) {
            TaxItem item = TABLE.get(i);
            if (item.NAME.equalsIgnoreCase(identifier)) {
                return i;
            }
        }
        return -1;
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
        TABLE.sort(Comparator.comparing(taxItem -> taxItem.NAME));
    }

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (TaxItem item : TABLE) {
            try {
                array.ARRAY.add(item.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed taxItem: " + item.NAME);
            }
        }
        return array;
    }
}
