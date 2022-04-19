package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.BackingEditGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.StockEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;

public class StockBTC extends BackingTableCore<LStock> {
    public StockBTC(Instance curInst) {
        super(curInst, true);
    }

    @Override
    public BackingEditGui<LStock> getEditDialog(BackingTableGui<LStock> caller, int index) {
        return new StockEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            TABLE.add(new LStock(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Ticker", "In Account", "In Use"
        };
    }

    @Override
    public ArrayList<String[]> getContents() {
        ArrayList<String[]> out = new ArrayList<>();
        for (LStock stk : TABLE) {
            String name = stk.getName();
            if (stk.isDead()) {
                name = "[" + name + "]";
            }
            out.add(new String[]{
                    name, stk.getTicker(), stk.inAccount() ? "X" : "", stk.inUse() ? "X" : ""
            });
        }
        return out;
    }

    @Override
    public String getIdentifier(int index) {
        return TABLE.get(index).toString();
    }

    @Override
    public int getIndex(String identifier) {
        for (LStock stk : TABLE) {
            if (stk.toString().equalsIgnoreCase(identifier)
                    || stk.getName().equalsIgnoreCase(identifier)
                    || stk.getTicker().equalsIgnoreCase(identifier)) {
                return TABLE.indexOf(stk);
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
        return true;
    }

    @Override
    public boolean canRemove(int index) {
        return !TABLE.get(index).inAccount();
    }

    @Override
    public void sort() {
        TABLE.sort(Comparator.comparing(LStock::toString));
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray out = new JsonArray();
        for (LStock stk : TABLE) {
            try {
                out.ARRAY.add(stk.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed LStock: " + stk);
            }
        }
        return out;
    }
}
