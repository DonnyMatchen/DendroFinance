package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.StockEditGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;

public class StockBTC extends BackingTableCore<LStock> {
    public StockBTC(ProgramInstance curInst) {
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
            add(new LStock(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Ticker", "In Account"
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
            LStock stk = MAP.get(key);
            String check = search;
            boolean allow = true, flagA = stk.inAccount();
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
            String name = stk.getName();
            if (stk.isDead()) {
                name += " [Dead]";
            }
            if (!(name.toLowerCase().contains(check.toLowerCase())
                    || stk.getName().toLowerCase().contains(check.toLowerCase())
                    || stk.getTicker().toLowerCase().contains(check.toLowerCase()))) {
                allow = false;
            }
            if (allow) {
                out.add(new String[]{
                        name, stk.getTicker(), flagA ? "X" : ""
                });
            }
        }
        return out;
    }

    @Override
    public LStock getElement(String identifier) {
        for (LStock s : this) {
            if (
                    s.getName().equalsIgnoreCase(identifier) ||
                            s.getTicker().equalsIgnoreCase(identifier) ||
                            s.toString().equalsIgnoreCase(identifier)
            ) {
                return s;
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
        ArrayList<LStock> list = new ArrayList<>();
        for (LStock s : this) {
            list.add(s);
        }
        list.sort(Comparator.comparing(LStock::toString));
        KEYS.clear();
        for (LStock s : list) {
            KEYS.add(s.getName());
        }
        changed = true;
    }
}
