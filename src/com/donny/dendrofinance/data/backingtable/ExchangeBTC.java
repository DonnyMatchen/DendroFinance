package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.ExchangeEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.util.ArrayList;

public class ExchangeBTC extends BackingTableCore<Exchange> {
    public ExchangeBTC(Instance curInst) {
        super(curInst, false);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Exchanges";
        } else {
            return "Exchange";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<Exchange> caller, int index) {
        new ExchangeEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            TABLE.add(new Exchange(obj, CURRENT_INSTANCE, true));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Alternate Name", "# Fiat", "# Stock", "# Crypto", "# Inventory", "In Use"
        };
    }

    @Override
    public int contentIdentifierIndex() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (Exchange exch : TABLE) {
            if (exch.NAME.toLowerCase().contains(search.toLowerCase())
                    || exch.ALT.toLowerCase().contains(search.toLowerCase())) {
                out.add(exch.print(CURRENT_INSTANCE));
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
        for (Exchange exch : TABLE) {
            if (exch.NAME.equalsIgnoreCase(identifier)) {
                return TABLE.indexOf(exch);
            }
        }
        return -1;
    }

    @Override
    public boolean canMove(String identifier) {
        return canEdit(identifier);
    }

    @Override
    public boolean canEdit(String identifier) {
        return getElement(identifier).EXPORT;
    }

    @Override
    public boolean canRemove(String identifier) {
        return canEdit(identifier) && !getElement(identifier).inUse(CURRENT_INSTANCE);
    }

    @Override
    public void sort() {
    }

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (Exchange exch : TABLE) {
            try {
                array.ARRAY.add(exch.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Exchange: " + exch);
            }
        }
        return array;
    }
}
