package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class LCurrency implements ExportableToJson {
    protected final Instance CURRENT_INSTANCE;
    private final String NAME, TIC, SYMBOL, ALT_NAME;
    private final int PLACES;
    private final BigDecimal FACTOR;
    private final boolean FIAT, EXTINCT, TOKEN, FORWARDS;

    public LCurrency(String name, String tic, boolean fiat, String symbol, boolean forwards, int places,
                     BigDecimal factor, String alt, boolean token, boolean dead, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        TIC = tic;
        FIAT = fiat;
        SYMBOL = symbol;
        PLACES = places;
        EXTINCT = dead;
        TOKEN = token;
        FACTOR = factor;
        ALT_NAME = alt;
        FORWARDS = forwards;
    }

    public LCurrency(String name, String tic, boolean fiat, String symbol, boolean forwards, int places,
                     String alt, boolean token, boolean dead, Instance curInst) {
        this(name, tic, fiat, symbol, forwards, places, BigDecimal.ONE, alt, token, dead, curInst);
    }

    public LCurrency(LCurrency old, String name, int addPlace) {
        CURRENT_INSTANCE = old.CURRENT_INSTANCE;
        NAME = name;
        TIC = old.TIC;
        FIAT = old.FIAT;
        SYMBOL = old.SYMBOL;
        PLACES = old.PLACES + addPlace;
        EXTINCT = old.EXTINCT;
        TOKEN = old.TOKEN;
        FACTOR = old.FACTOR;
        ALT_NAME = old.ALT_NAME;
        FORWARDS = old.FORWARDS;
    }

    public LCurrency(JsonObject obj, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        String flags = obj.getString("flags").getString();
        if (!obj.containsKey("name")) {
            CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "Nameless Currency:\n" + obj);
        }
        if (obj.containsKey("factor")) {
            FACTOR = obj.getDecimal("factor").decimal;
        } else {
            FACTOR = BigDecimal.ONE;
        }
        NAME = obj.getString("name").getString();
        TIC = obj.getString("tic").getString();
        FIAT = flags.contains("F");
        SYMBOL = obj.getString("symbol").getString();
        PLACES = obj.getDecimal("places").decimal.intValue();
        EXTINCT = flags.contains("D");
        TOKEN = flags.contains("T");
        ALT_NAME = obj.getString("alt").getString();
        FORWARDS = !flags.contains(">");
    }

    public String encode(BigDecimal val) {
        BigDecimal num = val.abs();
        DecimalFormat format;
        if (PLACES > 0) {
            format = new DecimalFormat("#,##0." + "0".repeat(PLACES));
        } else {
            format = new DecimalFormat("#,##0");
        }
        if (val.compareTo(BigDecimal.ZERO) >= 0) {
            if (FORWARDS) {
                return SYMBOL + format.format(num);
            } else {
                return format.format(num) + SYMBOL;
            }
        } else {
            if (FORWARDS) {
                return "(" + SYMBOL + format.format(num) + ")";
            } else {
                return "(" + format.format(num) + " " + SYMBOL + ")";
            }
        }
    }

    public String getName() {
        return NAME;
    }

    public String getTicker() {
        return TIC;
    }

    public boolean isFiat() {
        return FIAT;
    }

    public int getPlaces() {
        return PLACES;
    }

    public BigDecimal getFactor() {
        return FACTOR;
    }

    public String getSymbol() {
        return SYMBOL;
    }

    public boolean isDead() {
        return EXTINCT;
    }

    public boolean isToken() {
        return TOKEN;
    }

    public boolean isForwards() {
        return FORWARDS;
    }

    public String getAltName() {
        return ALT_NAME;
    }

    public BigDecimal reverseTotal(BigDecimal amount) {
        BigDecimal temp = getTotal(BigDecimal.ONE);
        return !temp.equals(BigDecimal.ZERO) ? amount.divide(temp, CURRENT_INSTANCE.precision) : BigDecimal.ZERO;
    }

    public BigDecimal getTotal(BigDecimal amount) {
        if (EXTINCT) {
            return BigDecimal.ZERO;
        } else {
            return CURRENT_INSTANCE.convert(amount, this, CURRENT_INSTANCE.main);
        }
    }

    public BigDecimal getTotal(BigDecimal amount, LDate date) {
        if (EXTINCT) {
            return BigDecimal.ZERO;
        } else {
            return CURRENT_INSTANCE.convert(amount, this, CURRENT_INSTANCE.main, date);
        }
    }

    public boolean inUse() {
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
            for (AccountWrapper aw : entry.getAccounts()) {
                if (aw.ACCOUNT.getCurrency().equals(this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean inAccount() {
        for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
            if (a.getCurrency().equals(this)) {
                if (a.EXPORT || a.inUse()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(NAME));
        obj.put("tic", new JsonString(TIC));
        obj.put("symbol", new JsonString(SYMBOL));
        obj.put("places", new JsonDecimal(BigDecimal.valueOf(PLACES)));
        obj.put("alt", new JsonString(ALT_NAME));
        obj.put("factor", new JsonDecimal(FACTOR));
        String flags = "";
        if (FIAT) {
            flags += "F";
        }
        if (EXTINCT) {
            flags += "D";
        }
        if (TOKEN) {
            flags += "T";
        }
        if (!FORWARDS) {
            flags += ">";
        }
        obj.put("flags", new JsonString(flags));
        return obj;
    }

    public boolean first() {
        for (LCurrency c : CURRENT_INSTANCE.CURRENCIES) {
            if (c.getTicker().equals(getTicker())) {
                return c == this;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (first()) {
            if (isFiat()) {
                return "F!" + getTicker();
            } else {
                return "C!" + getTicker();
            }
        } else {
            return getName();
        }
    }

    public String toUnifiedString() {
        if (isFiat()) {
            return "F!" + getTicker();
        } else {
            if (this instanceof LStock) {
                return "S!" + getTicker();
            } else if (this instanceof LInventory) {
                return "I!" + getTicker();
            } else {
                return "C!" + getTicker();
            }
        }
    }
}