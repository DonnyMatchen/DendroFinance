package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.AccountWrapper;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.ExportableToJson;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class LCurrency implements ExportableToJson {
    public final Instance CURRENT_INSTANCE;
    protected final String TIC;
    private final String NAME, SYMBOL, ALT_NAME;
    private final int PLACES;
    private final boolean FIAT, EXTINCT, TOKEN, FORWARDS;
    private String altApi = "";

    public LCurrency(String name, String tic, boolean fiat, String symbol, boolean forwards, int places,
                     String alt, boolean token, boolean dead, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        TIC = tic;
        FIAT = fiat;
        SYMBOL = symbol;
        PLACES = places;
        EXTINCT = dead;
        TOKEN = token;
        ALT_NAME = alt;
        FORWARDS = forwards;
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
        ALT_NAME = old.ALT_NAME;
        FORWARDS = old.FORWARDS;
    }

    public LCurrency(JsonObject obj, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        String flags = obj.getString("flags").getString();
        if (!obj.FIELDS.containsKey("name")) {
            System.out.println(obj);
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
                return format.format(num) + " " + SYMBOL;
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

    public void setAltApi(String alt) {
        altApi = alt;
    }

    public BigDecimal reverseTotal(BigDecimal amount) {
        BigDecimal temp = getTotal(BigDecimal.ONE);
        return !temp.equals(BigDecimal.ZERO) ? amount.divide(temp, CURRENT_INSTANCE.PRECISION) : BigDecimal.ZERO;
    }

    public BigDecimal getTotal(BigDecimal amount) {
        try {
            if (EXTINCT) {
                return BigDecimal.ZERO;
            } else {
                if (FIAT) {
                    if (TIC.equals(CURRENT_INSTANCE.main.getTicker())) {
                        return amount;
                    } else {
                        return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.hitPolygonForex(TIC));
                    }
                } else {
                    BigDecimal alpha;
                    if (altApi.equals("kraken")) {
                        alpha = CURRENT_INSTANCE.FILE_HANDLER.hitKraken(TIC);
                    } else {
                        if (ALT_NAME.equals("")) {
                            alpha = CURRENT_INSTANCE.FILE_HANDLER.hitCoinGecko(NAME);
                        } else {
                            alpha = CURRENT_INSTANCE.FILE_HANDLER.hitCoinGecko(ALT_NAME);
                        }
                    }
                    if (!first()) {
                        if (NAME.contains("Mili")) {
                            alpha = alpha.divide(new BigDecimal("1000"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Centi")) {
                            alpha = alpha.divide(new BigDecimal("100"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Deci")) {
                            alpha = alpha.divide(new BigDecimal("10"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Hecta")) {
                            alpha = alpha.multiply(new BigDecimal("100"));
                        } else if (NAME.contains("Nano")) {
                            alpha = alpha.divide(new BigDecimal("1000000000"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Micro")) {
                            alpha = alpha.divide(new BigDecimal("1000000"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Satoshi")) {
                            alpha = alpha.divide(new BigDecimal("100000000"), CURRENT_INSTANCE.PRECISION);
                        }
                    }
                    return alpha.multiply(amount);
                }
            }
        } catch (NumberFormatException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "{" + this + "} NumberFormatException for string: " + ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotal(BigDecimal amount, LCurrency nat) {
        return CURRENT_INSTANCE.convert(this, amount, nat);
    }

    public BigDecimal getTotal(BigDecimal amount, LDate date) {
        try {
            if (EXTINCT) {
                return BigDecimal.ZERO;
            } else {
                if (FIAT) {
                    if (TIC.equals(CURRENT_INSTANCE.main.getTicker())) {
                        return amount;
                    } else {
                        return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.hitPolygonForex(TIC));
                    }
                } else {
                    BigDecimal alpha;
                    if (TIC.equals("REPV2")) {
                        alpha = CURRENT_INSTANCE.FILE_HANDLER.hitKrakenHistory("REPV2", date);
                    } else {
                        if (ALT_NAME.equals("")) {
                            alpha = CURRENT_INSTANCE.FILE_HANDLER.hitCoinGeckoHistory(NAME, date);
                        } else {
                            alpha = CURRENT_INSTANCE.FILE_HANDLER.hitCoinGeckoHistory(ALT_NAME, date);
                        }
                    }
                    if (!first()) {
                        if (NAME.contains("Mili")) {
                            alpha = alpha.divide(new BigDecimal("1000"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Centi")) {
                            alpha = alpha.divide(new BigDecimal("100"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Deci")) {
                            alpha = alpha.divide(new BigDecimal("10"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Hecta")) {
                            alpha = alpha.multiply(new BigDecimal("100"));
                        } else if (NAME.contains("Nano")) {
                            alpha = alpha.divide(new BigDecimal("1000000000"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Micro")) {
                            alpha = alpha.divide(new BigDecimal("1000000"), CURRENT_INSTANCE.PRECISION);
                        } else if (NAME.contains("Satoshi")) {
                            alpha = alpha.divide(new BigDecimal("100000000"), CURRENT_INSTANCE.PRECISION);
                        }
                    }
                    return alpha.multiply(amount);
                }
            }
        } catch (NumberFormatException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "{" + this + "} NumberFormatException for string: " + ex.getMessage());
            return BigDecimal.ZERO;
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
        obj.FIELDS.put("name", new JsonString(NAME));
        obj.FIELDS.put("tic", new JsonString(TIC));
        obj.FIELDS.put("symbol", new JsonString(SYMBOL));
        obj.FIELDS.put("places", new JsonDecimal(BigDecimal.valueOf(PLACES)));
        obj.FIELDS.put("alt", new JsonString(ALT_NAME));
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
        obj.FIELDS.put("flags", new JsonString(flags));
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
            } else if (this instanceof Inventory) {
                return "I!" + getTicker();
            } else {
                return "C!" + getTicker();
            }
        }
    }
}