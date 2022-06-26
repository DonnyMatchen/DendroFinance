package com.donny.dendrofinance.account;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.util.ExportableToJson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Exchange implements ExportableToJson, Serializable {
    private final Instance CURRENT_INSTANCE;
    public final String NAME, ALT;
    public final ArrayList<String> SUPPORTED;
    public final ArrayList<String> FEE;
    public final ArrayList<JsonObject> STAKING;
    public final boolean EXPORT;

    public Exchange(String name, String alt, ArrayList<String> sup, Instance curInst, boolean export) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        ALT = alt;
        SUPPORTED = new ArrayList<>(sup);
        FEE = new ArrayList<>();
        STAKING = new ArrayList<>();
        EXPORT = export;
    }

    public Exchange(String name, String alt, Instance curInst, boolean export) {
        this(name, alt, new ArrayList<>(), curInst, export);
    }

    public Exchange(String name, String alt, ArrayList<String> sup, ArrayList<JsonObject> stak, Instance curInst, boolean export) {
        this(name, alt, sup, curInst, export);
        STAKING.addAll(stak);
    }

    public Exchange(JsonObject obj, Instance curInst, boolean export) {
        this(obj.getString("name").getString(), obj.getString("alt").getString(), curInst, export);
        for (JsonString string : obj.getArray("supported").getStringArray()) {
            SUPPORTED.add(string.getString());
        }
        if (obj.containsKey("fees")) {
            for (JsonString string : obj.getArray("fees").getStringArray()) {
                FEE.add(string.getString());
            }
        }
        if (obj.containsKey("staking")) {
            STAKING.addAll(obj.getArray("staking").getObjectArray());
        }
    }

    public boolean supports(LCurrency currency) {
        switch (NAME) {
            case "Personal" -> {
                return !(currency instanceof LStock);
            }
            case "Cash" -> {
                return currency.isFiat();
            }
            default -> {
                if (currency instanceof LStock) {
                    if (SUPPORTED.contains("All Stock")) {
                        return true;
                    }
                } else if (currency instanceof LInventory) {
                    if (SUPPORTED.contains("All Inventory")) {
                        return true;
                    }
                } else {
                    if (currency.isFiat()) {
                        if (SUPPORTED.contains("All Fiat")) {
                            return true;
                        }
                    } else {
                        if (SUPPORTED.contains("All Crypto")) {
                            return true;
                        }
                    }
                }
                String check = currency.toString();
                for (String ticker : SUPPORTED) {
                    if (check.equalsIgnoreCase(ticker)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public boolean supportsFee(LCurrency currency) {
        String check = currency.toString();
        for (String ticker : FEE) {
            if (check.equalsIgnoreCase(ticker)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFees() {
        return !FEE.isEmpty();
    }

    public int stakes(LCurrency currency) {
        String check = currency.toString();
        for (JsonObject obj : STAKING) {
            if (check.equalsIgnoreCase(obj.getString("cur").getString())) {
                return obj.getDecimal("places").decimal.intValue();
            }
        }
        return -1;
    }

    public String[] print(Instance curInst) {
        int fiat = 0, stock = 0, crypto = 0, inv = 0;
        for (String str : SUPPORTED) {
            if (str.contains("F!")) {
                fiat++;
            }
            if (str.contains("S!")) {
                stock++;
            }
            if (str.contains("C!")) {
                crypto++;
            }
            if (str.contains("I!")) {
                inv++;
            }
        }
        return new String[]{
                NAME, ALT, "" + fiat, "" + stock, "" + crypto, "" + inv, inUse(curInst) ? "X" : ""
        };
    }

    public boolean inUse(Instance curInst) {
        for (TransactionEntry entry : curInst.DATA_HANDLER.readTransactions()) {
            for (AccountWrapper aw : entry.getAccounts()) {
                if (aw.ACCOUNT.EXCHANGE == this) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<String> aNames() {
        ArrayList<String> out = new ArrayList<>();
        SUPPORTED.forEach(cur -> out.add(NAME + "_" + cur.split("!")[1]));
        STAKING.forEach(pair -> out.add(NAME + "_" + pair.getString("cur").getString().split("!")[1] + "_S"));
        return out;
    }

    public ArrayList<String> aNamesInUse(Instance curInst) {
        ArrayList<String> reduced = new ArrayList<>();
        for (String name : aNames()) {
            Account a = curInst.ACCOUNTS.getElement(name);
            if (a == null) {
                curInst.LOG_HANDLER.info(getClass(), "You might have a missing currency (" + name + ")");
            } else {
                if (a.inUse()) {
                    reduced.add(name);
                }
            }
        }
        return reduced;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(NAME));
        obj.put("alt", new JsonString(ALT));
        JsonArray array = new JsonArray();
        SUPPORTED.sort(Comparator.naturalOrder());
        for (String ticker : SUPPORTED) {
            array.add(new JsonString(ticker));
        }
        obj.put("supported", array);
        if (!FEE.isEmpty()) {
            array = new JsonArray();
            for (String ticker : FEE) {
                array.add(new JsonString(ticker));
            }
            obj.put("fees", array);
        }
        if (!STAKING.isEmpty()) {
            obj.put("staking", new JsonArray(STAKING));
        }
        return obj;
    }
}
