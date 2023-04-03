package com.donny.dendrofinance.account;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;
import com.donny.dendroroot.util.UniqueName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class Exchange implements UniqueName, Serializable {
    private final ProgramInstance CURRENT_INSTANCE;
    public final String NAME, ALT;
    public final ArrayList<String> SUPPORTED;
    public final ArrayList<String> FEE;
    public final ArrayList<JsonObject> STAKING;
    public final boolean EXPORT;

    public Exchange(String name, String alt, ArrayList<String> sup, ProgramInstance curInst, boolean export) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        ALT = alt;
        SUPPORTED = new ArrayList<>(sup);
        FEE = new ArrayList<>();
        STAKING = new ArrayList<>();
        EXPORT = export;
    }

    public Exchange(String name, String alt, ProgramInstance curInst, boolean export) {
        this(name, alt, new ArrayList<>(), curInst, export);
    }

    public Exchange(String name, String alt, ArrayList<String> sup, ArrayList<JsonObject> stak, ProgramInstance curInst, boolean export) {
        this(name, alt, sup, curInst, export);
        STAKING.addAll(stak);
    }

    public Exchange(JsonObject obj, ProgramInstance curInst, boolean export) {
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

    @Override
    public String getName() {
        return NAME;
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

    public String[] print() {
        int fiat = 0, stock = 0, crypto = 0, inv = 0;
        for (String str : SUPPORTED) {
            LCurrency c = CURRENT_INSTANCE.getLCurrency(str);
            if (c.isFiat()) {
                fiat++;
            } else if (c instanceof LStock) {
                stock++;
            } else if (c instanceof LInventory) {
                inv++;
            } else {
                crypto++;
            }
        }
        return new String[]{
                NAME, ALT, String.valueOf(fiat), String.valueOf(stock), String.valueOf(crypto), String.valueOf(inv)
        };
    }

    public ArrayList<String> aNames() {
        ArrayList<String> out = new ArrayList<>();
        SUPPORTED.forEach(cur -> out.add(NAME + "_" + cur.split("!")[1]));
        STAKING.forEach(pair -> out.add(NAME + "_" + pair.getString("cur").getString().split("!")[1] + "_S"));
        return out;
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

    @Override
    public String toString() {
        return NAME;
    }
}
