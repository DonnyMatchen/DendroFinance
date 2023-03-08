package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.InventoryEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class InventoryBTC extends BackingTableCore<LInventory> {
    public InventoryBTC(Instance curInst) {
        super(curInst, true);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Inventories";
        } else {
            return "Inventory";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<LInventory> caller, int index) {
        new InventoryEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            add(new LInventory(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Ticker", "Presentation", "In Account", "In Use"
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
            LInventory inv = MAP.get(key);
            String check = search;
            boolean allow = true, flagA = inv.inAccount();
            if (check.contains("$A")) {
                check = check.replace("$A", "").trim();
                if (!flagA) {
                    allow = false;
                }
            }
            if (check.contains("$a")) {
                check = check.replace("$a", "").trim();
                if (flagA) {
                    allow = false;
                }
            }
            if (!(inv.getName().toLowerCase().contains(check.toLowerCase())
                    || inv.getTicker().toLowerCase().contains(check.toLowerCase())
                    || inv.encode(BigDecimal.ZERO).contains(check)
                    || inv.toString().toLowerCase().contains(check.toLowerCase())
                    || inv.getName().toLowerCase().contains(check.toLowerCase()))) {
                allow = false;
            }
            if (allow) {
                out.add(new String[]{
                        inv.getName(), inv.getTicker(), inv.encode(BigDecimal.ZERO),
                        flagA ? "X" : ""
                });
            }
        }
        return out;
    }

    @Override
    public LInventory getElement(String identifier) {
        for (LInventory i : this) {
            if (
                    i.getName().equalsIgnoreCase(identifier) ||
                            i.getTicker().equalsIgnoreCase(identifier) ||
                            i.toString().equalsIgnoreCase(identifier)
            ) {
                return i;
            }
        }
        return null;
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
        return !getElement(identifier).inAccount();
    }

    @Override
    public void sort() {
        ArrayList<LInventory> list = new ArrayList<>();
        for (LInventory i : this) {
            list.add(i);
        }
        list.sort(Comparator.comparing(LInventory::toString));
        KEYS.clear();
        for (LInventory i : list) {
            KEYS.add(i.getName());
        }
        changed = true;
    }
}
