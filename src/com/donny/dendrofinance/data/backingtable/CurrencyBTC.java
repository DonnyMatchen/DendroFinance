package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.CurrencyEditGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class CurrencyBTC extends BackingTableCore<LCurrency> {
    public CurrencyBTC(ProgramInstance curInst) {
        super(curInst, true);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Currencies";
        } else {
            return "Currency";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<LCurrency> caller, int index) {
        new CurrencyEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            add(new LCurrency(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Ticker", "Presentation", "Type", "In Account"
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
            LCurrency cur = MAP.get(key);
            String check = search;
            boolean allow = true, flagA = cur.inAccount();
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
            StringBuilder name = new StringBuilder(cur.getName());
            if (!cur.getAltName().isEmpty()) {
                name.append(" [").append(cur.getAltName()).append("]");
            }
            if (cur.isDead()) {
                name.append(" [Dead]");
            }
            StringBuilder type = new StringBuilder(cur.isFiat() ? "Fiat" : "Cryptocurrency");
            if (cur.isToken()) {
                type.append(" (token)");
            }
            if (!(name.toString().toLowerCase().contains(check.toLowerCase())
                    || cur.getName().toLowerCase().contains(check.toLowerCase())
                    || cur.getTicker().toLowerCase().contains(check.toLowerCase())
                    || cur.encode(BigDecimal.ZERO).contains(check)
                    || type.toString().toLowerCase().contains(check.toLowerCase())
                    || cur.getName().toLowerCase().contains(check.toLowerCase()))) {
                allow = false;
            }
            if (allow) {
                out.add(new String[]{
                        name.toString(), cur.getTicker(), cur.encode(BigDecimal.ZERO), type.toString(),
                        flagA ? "X" : ""
                });
            }
        }
        return out;
    }

    @Override
    public LCurrency getElement(String identifier) {
        for (LCurrency c : this) {
            if (
                    c.getName().equalsIgnoreCase(identifier) ||
                            c.getTicker().equalsIgnoreCase(identifier) ||
                            c.getAltName().equalsIgnoreCase(identifier) ||
                            c.toString().equalsIgnoreCase(identifier)
            ) {
                return c;
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
        ArrayList<LCurrency> list = new ArrayList<>();
        for (LCurrency a : this) {
            list.add(a);
        }
        list.sort(Comparator.comparing(LCurrency::toString));
        KEYS.clear();
        for (LCurrency a : list) {
            KEYS.add(a.getName());
        }
        changed = true;
    }


    public ArrayList<LCurrency> getBaseline() {
        ArrayList<LCurrency> out = new ArrayList<>();
        KEYS.forEach(key -> {
            LCurrency c = MAP.get(key);
            if (c.first()) {
                out.add(c);
            }
        });
        return out;
    }
}
