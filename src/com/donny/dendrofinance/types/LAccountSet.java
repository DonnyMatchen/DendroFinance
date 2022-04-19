package com.donny.dendrofinance.types;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonString;

import java.util.ArrayList;
import java.util.Iterator;

public class LAccountSet extends LType<ArrayList<AccountWrapper>> implements Iterable<AccountWrapper> {
    public final Instance CURRENT_INSTANCE;
    private final ArrayList<AccountWrapper> REGISTRY;

    public LAccountSet(Instance curInst) {
        REGISTRY = new ArrayList<>();
        CURRENT_INSTANCE = curInst;
    }

    public LAccountSet(JsonArray array, Instance curInst) {
        this(curInst);
        for (JsonString item : array.getStringArray()) {
            String[] parts = item.getString().split("!");
            for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
                if (a.getName().equalsIgnoreCase(parts[1])) {
                    REGISTRY.add(new AccountWrapper(a, parts[0]));
                    break;
                }
            }
        }
    }

    public LAccountSet(String raw, Instance curInst) {
        this(curInst);
        for (String acc : raw.replace("{", "").replace("}", "").replace(" ", "").split(",")) {
            String[] parts = acc.split("!");
            boolean flag = true;
            for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
                if (a.getName().equalsIgnoreCase(parts[1])) {
                    REGISTRY.add(new AccountWrapper(a, parts[0]));
                    flag = false;
                    break;
                }
            }
            if (flag) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Account not found: " + acc);
            }
        }
    }

    @Override
    public boolean sameAs(ArrayList<AccountWrapper> b) {
        boolean flag = true;
        if (REGISTRY.size() == b.size()) {
            for (int i = 0; i < b.size(); i++) {
                if (!REGISTRY.get(i).equals(b.get(i))) {
                    flag = false;
                    break;
                }
            }
            return flag;
        }
        return false;
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
            arr.ARRAY.add(new JsonString(a.toString()));
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

    public boolean containsTax() {
        for (AccountWrapper a : REGISTRY) {
            if (a.COLUMN == AccountWrapper.AWType.TAX) {
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

    @Override
    public Iterator<AccountWrapper> iterator() {
        return REGISTRY.iterator();
    }
}
