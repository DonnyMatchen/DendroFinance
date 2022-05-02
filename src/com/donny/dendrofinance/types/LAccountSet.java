package com.donny.dendrofinance.types;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class LAccountSet extends LType<ArrayList<AccountWrapper>> implements Iterable<AccountWrapper> {
    private final Instance CURRENT_INSTANCE;
    private final ArrayList<AccountWrapper> REGISTRY;

    public LAccountSet(Instance curInst) {
        REGISTRY = new ArrayList<>();
        CURRENT_INSTANCE = curInst;
    }

    public LAccountSet(JsonArray array, Instance curInst) {
        this(curInst);
        for (JsonObject obj : array.getObjectArray()) {
            if (obj.FIELDS.containsKey("acc") && obj.FIELDS.containsKey("col") && obj.FIELDS.containsKey("val")) {
                REGISTRY.add(new AccountWrapper(obj, CURRENT_INSTANCE));
            }
        }
    }

    public LAccountSet(String raw, Instance curInst) {
        this(curInst);
        for (String acc : raw.replace("{", "").replace("}", "").replace(" ", "").split(",")) {
            AccountWrapper wrapper = new AccountWrapper(acc, CURRENT_INSTANCE);
            if (wrapper.ACCOUNT == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Account not found: " + acc);
            } else {
                REGISTRY.add(wrapper);
            }
        }
    }

    @Override
    public boolean sameAs(ArrayList<AccountWrapper> b) {
        if (REGISTRY.size() == b.size()) {
            for (int i = 0; i < b.size(); i++) {
                if (!REGISTRY.get(i).equals(b.get(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compare(ArrayList<AccountWrapper> b) {
        return Integer.compare(REGISTRY.size(), b.size());
    }

    @Override
    public boolean isDefault() {
        return REGISTRY.isEmpty();
    }

    public AccountWrapper get(int i) {
        return REGISTRY.get(i);
    }

    public void replace(int i, AccountWrapper a) {
        REGISTRY.remove(i);
        REGISTRY.add(i, a);
    }

    public int indexOf(String string) {
        for (AccountWrapper a : REGISTRY) {
            if (a.ACCOUNT.getName().equalsIgnoreCase(string)) {
                return REGISTRY.indexOf(a);
            }
        }
        return -1;
    }

    @Override
    public JsonItem export() throws JsonFormattingException {
        JsonArray arr = new JsonArray();
        for (AccountWrapper a : REGISTRY) {
            arr.ARRAY.add(a.export());
        }
        return arr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (AccountWrapper a : REGISTRY) {
            sb.append(a.toString()).append(", ");
        }
        return sb.substring(0, sb.length() - 2) + "}";
    }

    public void add(AccountWrapper a) {
        REGISTRY.add(a);
    }

    public boolean remove(AccountWrapper a) {
        return REGISTRY.remove(a);
    }

    public AccountWrapper remove(int index) {
        return REGISTRY.remove(index);
    }

    public int getSize() {
        return REGISTRY.size();
    }

    public boolean containsBudget() {
        for (AccountWrapper a : REGISTRY) {
            if (a.ACCOUNT.getBudgetType() != null) {
                return true;
            }
        }
        return false;
    }

    public boolean containsGhost() {
        for (AccountWrapper a : REGISTRY) {
            if (a.COLUMN == AWColumn.GHOST) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<LCurrency> getCurrencies() {
        ArrayList<LCurrency> out = new ArrayList<>();
        for (AccountWrapper a : REGISTRY) {
            if (!out.contains(a.ACCOUNT.getCurrency())) {
                out.add(a.ACCOUNT.getCurrency());
            }
        }
        return out;
    }

    public void sort() {
        REGISTRY.sort((w1, w2) -> w1.ACCOUNT.compareTo(w2.ACCOUNT));
        REGISTRY.sort(Comparator.comparing(w -> w.COLUMN));
    }

    @Override
    public Iterator<AccountWrapper> iterator() {
        return REGISTRY.iterator();
    }
}
