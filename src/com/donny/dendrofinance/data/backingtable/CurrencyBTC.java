package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.CurrencyEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;

public class CurrencyBTC extends BackingTableCore<LCurrency> {
    public CurrencyBTC(Instance curInst) {
        super(curInst, true);
    }

    @Override
    public void getEditDialog(BackingTableGui<LCurrency> caller, int index) {
        new CurrencyEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject obj : array.getObjectArray()) {
            TABLE.add(new LCurrency(obj, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Ticker", "Presentation", "Type", "In Account", "In Use"
        };
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (LCurrency cur : TABLE) {
            String check = search;
            boolean flagU = false, rFlagU = false, flagA = false, rFlagA = false, allow = true;
            if (check.contains("$U")) {
                flagU = true;
                check = check.replace("$U", "").trim();
                if (!cur.inUse()) {
                    allow = false;
                }
            }
            if (check.contains("$u")) {
                rFlagU = true;
                check = check.replace("$u", "").trim();
                if (cur.inUse()) {
                    allow = false;
                }
            }
            if (check.contains("$A")) {
                flagA = true;
                check = check.replace("$A", "").trim();
                if (!cur.inAccount()) {
                    allow = false;
                }
            }
            if (check.contains("$a")) {
                rFlagA = true;
                check = check.replace("$a", "").trim();
                if (cur.inAccount()) {
                    allow = false;
                }
            }
            StringBuilder name = new StringBuilder(cur.getName());
            if (!cur.getAltName().equals("")) {
                name.append(" (").append(cur.getAltName()).append(")");
            }
            if (cur.isDead()) {
                name = new StringBuilder("[" + name + "]");
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
                        (!rFlagA && (flagA || cur.inAccount())) ? "X" : "",
                        (!rFlagU && (flagU || cur.inUse())) ? "X" : ""
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
        for (LCurrency cur : TABLE) {
            if (cur.toString().equalsIgnoreCase(identifier)
                    || cur.getName().equalsIgnoreCase(identifier)
                    || cur.getTicker().equalsIgnoreCase(identifier)) {
                return TABLE.indexOf(cur);
            }
            String ticker = cur.getTicker();
            if (cur.getName().contains("Mili")) {
                ticker = " m" + ticker;
            } else if (cur.getName().contains("Centi")) {
                ticker = " c" + ticker;
            } else if (cur.getName().contains("Deci")) {
                ticker = " d" + ticker;
            } else if (cur.getName().contains("Hecta")) {
                ticker = " h" + ticker;
            } else if (cur.getName().contains("NanoEther")) {
                ticker = " Gwei";
            } else if (cur.getName().contains("Nano")) {
                ticker = " n" + ticker;
            } else if (cur.getName().contains("Micro")) {
                ticker = " μ" + ticker;
            } else if (cur.getName().contains("Satoshi")) {
                ticker = " Satoshi";
            }
            if (ticker.equals(identifier)) {
                return TABLE.indexOf(cur);
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
        TABLE.sort(Comparator.comparing(LCurrency::toString));
        changed = true;
    }

    @Override
    public JsonArray export() {
        JsonArray out = new JsonArray();
        for (LCurrency cur : TABLE) {
            try {
                out.ARRAY.add(cur.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Malformed LCurrency: " + cur);
            }
        }
        return out;
    }

    public ArrayList<LCurrency> getBaseline() {
        ArrayList<LCurrency> out = new ArrayList<>();
        TABLE.forEach(c -> {
            if (c.first()) {
                out.add(c);
            }
        });
        return out;
    }
}
