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
    public int contentIdentifierIndex() {
        return 1;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (LInventory inv : TABLE) {
            String check = search;
            boolean flagU = false, rFlagU = false, flagA = false, rFlagA = false, allow = true;
            if (check.contains("$U")) {
                flagU = true;
                check = check.replace("$U", "").trim();
                if (!inv.inUse()) {
                    allow = false;
                }
            }
            if (check.contains("$u")) {
                rFlagU = true;
                check = check.replace("$u", "").trim();
                if (inv.inUse()) {
                    allow = false;
                }
            }
            if (check.contains("$A")) {
                flagA = true;
                check = check.replace("$A", "").trim();
                if (!inv.inAccount()) {
                    allow = false;
                }
            }
            if (check.contains("$a")) {
                flagA = true;
                check = check.replace("$a", "").trim();
                if (inv.inAccount()) {
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
                        (!rFlagA && (flagA || inv.inAccount())) ? "X" : "",
                        (!rFlagU && (flagU || inv.inUse())) ? "X" : ""
                });
            }
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
