package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.InventoryEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class InventoryBTC extends BackingTableCore<LInventory> {
    public InventoryBTC(Instance curInst) {
        super(curInst, true);
    }

    @Override
    public void getEditDialog(BackingTableGui<LInventory> caller, int index) {
        new InventoryEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            TABLE.add(new LInventory(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Ticker", "Presentation", "In Account", "In Use"
        };
    }

    @Override
    public ArrayList<String[]> getContents() {
        ArrayList<String[]> out = new ArrayList<>();
        for (LInventory inv : TABLE) {
            out.add(new String[]{
                    inv.getName(), inv.getTicker(), inv.encode(BigDecimal.ZERO), inv.inAccount() ? "X" : "", inv.inUse() ? "X" : ""
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
        for (LInventory inv : TABLE) {
            if (inv.toString().equalsIgnoreCase(identifier)
                    || inv.getName().equalsIgnoreCase(identifier)
                    || inv.getTicker().equalsIgnoreCase(identifier)) {
                return TABLE.indexOf(inv);
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
        TABLE.sort(Comparator.comparing(LInventory::toString));
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray out = new JsonArray();
        for (LInventory inv : TABLE) {
            try {
                out.ARRAY.add(inv.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed Inventory: " + inv);
            }
        }
        return out;
    }
}
