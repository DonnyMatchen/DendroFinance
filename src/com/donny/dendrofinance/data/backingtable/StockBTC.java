package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
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
    public String getName(boolean plural) {
        if (plural) {
            return "Stocks";
        } else {
            return "Stock";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<LStock> caller, int index) {
        new StockEditGui(caller, this, index, CURRENT_INSTANCE);
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
    public int contentIdentifierIndex() {
        return 1;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (LStock stk : TABLE) {
            String check = search;
            boolean flagU = false, rFlagU = false, flagA = false, rFlagA = false, allow = true;
            if (check.contains("$U")) {
                flagU = true;
                check = check.replace("$U", "").trim();
                if (!stk.inUse()) {
                    allow = false;
                }
            }
            if (check.contains("$u")) {
                rFlagU = true;
                check = check.replace("$u", "").trim();
                if (stk.inUse()) {
                    allow = false;
                }
            }
            if (check.contains("$A")) {
                flagA = true;
                check = check.replace("$A", "").trim();
                if (!stk.inAccount()) {
                    allow = false;
                }
            }
            if (check.contains("$a")) {
                rFlagA = true;
                check = check.replace("$a", "").trim();
                if (stk.inAccount()) {
                    allow = false;
                }
            }
            String name = stk.getName();
            if (stk.isDead()) {
                name = "[" + name + "]";
            }
            if (!(name.toLowerCase().contains(check.toLowerCase())
                    || stk.getName().toLowerCase().contains(check.toLowerCase())
                    || stk.getTicker().toLowerCase().contains(check.toLowerCase()))) {
                allow = false;
            }
            if (allow) {
                out.add(new String[]{
                        name, stk.getTicker(), (rFlagA && (flagA || stk.inAccount())) ? "X" : "", (rFlagU && (flagU || stk.inUse())) ? "X" : ""
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
        TABLE.sort(Comparator.comparing(LStock::toString));
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray out = new JsonArray();
        for (LStock stk : TABLE) {
            try {
                out.add(stk.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed LStock: " + stk);
            }
        }
        return out;
    }
}
