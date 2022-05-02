package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.ExportableToJsonObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class LMarketApi implements ExportableToJsonObject {
    public final String NAME, TYPES, BASE_URL, BASE_URL_HISTORY, KEY;
    private final Instance CURRENT_INSTANCE;
    private final ArrayList<String> PARSE_PATH, PARSE_PATH_HISTORY, NATS, EXCEPTS;

    public LMarketApi(String name, String types, String baseUrl, String baseUrlHist, String apiKey, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        TYPES = types;
        BASE_URL = baseUrl;
        BASE_URL_HISTORY = baseUrlHist;
        KEY = apiKey;
        PARSE_PATH = new ArrayList<>();
        PARSE_PATH_HISTORY = new ArrayList<>();
        NATS = new ArrayList<>();
        EXCEPTS = new ArrayList<>();
    }

    public LMarketApi(JsonObject object, Instance curInst) {
        this(
                object.getString("name").getString(),
                object.getString("types").getString(),
                object.getString("base-url").getString(),
                object.getString("base-url-hist").getString(),
                object.getString("api-key").getString(),
                curInst
        );
        for (JsonString string : object.getArray("parse-path").getStringArray()) {
            PARSE_PATH.add(string.getString());
        }
        for (JsonString string : object.getArray("parse-path-hist").getStringArray()) {
            PARSE_PATH_HISTORY.add(string.getString());
        }
        for (JsonString str : object.getArray("nats").getStringArray()) {
            NATS.add(str.getString());
        }
        for (JsonString str : object.getArray("excepts").getStringArray()) {
            EXCEPTS.add(str.getString());
        }
    }

    public boolean hasNat(LCurrency currency) {
        for (String nat : NATS) {
            LCurrency cur = CURRENT_INSTANCE.getLCurrency(nat);
            if (cur != null) {
                if (cur.getTicker().equals(currency.getTicker()) && cur.getClass() == currency.getClass() && cur.isFiat() == currency.isFiat()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canSearch(LCurrency c) {
        if (EXCEPTS.contains(c.toString())) {
            return false;
        } else {
            if (c instanceof LStock) {
                return TYPES.contains("S");
            } else if (c instanceof LInventory) {
                return TYPES.contains("I");
            } else {
                if (c.isFiat()) {
                    return TYPES.contains("F");
                } else {
                    return TYPES.contains("C");
                }
            }
        }
    }

    public boolean cryptocurrencies() {
        return TYPES.contains("C");
    }

    public boolean fiatCurrencies() {
        return TYPES.contains("F");
    }

    public boolean stocks() {
        return TYPES.contains("S");
    }

    public boolean inventories() {
        return TYPES.contains("I");
    }

    public void resetParsePath(ArrayList<String> newPath) {
        PARSE_PATH.clear();
        PARSE_PATH.addAll(newPath);
    }

    public void resetParsePathHistory(ArrayList<String> newPath) {
        PARSE_PATH_HISTORY.clear();
        PARSE_PATH_HISTORY.addAll(newPath);
    }

    public void resetNats(ArrayList<String> newPath) {
        NATS.clear();
        NATS.addAll(newPath);
    }

    public void resetExcepts(ArrayList<String> newPath) {
        EXCEPTS.clear();
        EXCEPTS.addAll(newPath);
    }

    public String readParsePath() {
        if (PARSE_PATH.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : PARSE_PATH) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public String readParsePathHistory() {
        if (PARSE_PATH_HISTORY.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : PARSE_PATH_HISTORY) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public String readNats() {
        if (NATS.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : NATS) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public String readExcepts() {
        if (EXCEPTS.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : EXCEPTS) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public boolean canConvert(LCurrency a, LCurrency b) {
        return (canSearch(a) && hasNat(b)) ||
                (canSearch(b) && hasNat(a)) ||
                (canSearch(a) && canSearch(b)) ||
                (a.getTicker().equals(b.getTicker()) && a.isFiat() == b.isFiat() && a.getClass() == b.getClass());
    }

    /**
     * @param amount the amount of currency <code>a</code> to be converted
     * @param a      the currency to be converted from
     * @param b      the currency to be converted to
     * @return the value of <code>amount</code> of <code>a</code> in <code>b</code>, or -1 if they cannot be converted using this api
     */
    public BigDecimal convert(BigDecimal amount, LCurrency a, LCurrency b) {
        if (hasNat(b) && canSearch(a)) {
            return amount.multiply(getCurrentPrice(a, b)).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
        } else if (hasNat(a) && canSearch(b)) {
            return amount.divide(getCurrentPrice(b, a), CURRENT_INSTANCE.precision).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
        } else if (canSearch(a) && canSearch(b)) {
            LCurrency nat;
            if (hasNat(CURRENT_INSTANCE.main)) {
                nat = CURRENT_INSTANCE.main;
            } else {
                nat = CURRENT_INSTANCE.getLCurrency(NATS.get(0));
            }
            BigDecimal x = getCurrentPrice(a, nat).divide(getCurrentPrice(b, nat), CURRENT_INSTANCE.precision);
            return amount.multiply(x).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
        } else {
            return BigDecimal.valueOf(-1);
        }
    }

    /**
     * @param amount the amount of currency <code>a</code> to be converted
     * @param a      the currency to be converted from
     * @param b      the currency to be converted to
     * @return the value of <code>amount</code> of <code>a</code> in <code>b</code>, or -1 if they cannot be converted using this api
     */
    public BigDecimal convert(BigDecimal amount, LCurrency a, LCurrency b, LDate date) {
        if (hasNat(b) && canSearch(a)) {
            return amount.multiply(getHistoricalPrice(a, b, date)).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
        } else if (hasNat(a) && canSearch(b)) {
            return amount.divide(getHistoricalPrice(b, a, date), CURRENT_INSTANCE.precision).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
        } else if (canSearch(a) && canSearch(b)) {
            LCurrency nat;
            if (hasNat(CURRENT_INSTANCE.main)) {
                nat = CURRENT_INSTANCE.main;
            } else {
                nat = CURRENT_INSTANCE.getLCurrency(NATS.get(0));
            }
            BigDecimal x = getHistoricalPrice(a, nat, date).divide(getHistoricalPrice(b, nat, date), CURRENT_INSTANCE.precision);
            return amount.multiply(x).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
        } else {
            return BigDecimal.valueOf(-1);
        }
    }

    public BigDecimal getCurrentPrice(LCurrency search, LCurrency nat) {
        ApiParseBundle bundle = new ApiParseBundle(search, nat, KEY);
        JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.hit(bundle.process(BASE_URL));
        JsonItem sup = null;
        for (String token : PARSE_PATH) {
            token = bundle.process(token);
            if (sup == null) {
                sup = item;
            }
            switch (sup.getType()) {
                case ARRAY -> sup = ((JsonArray) sup).get(Integer.parseInt(token));
                case OBJECT -> sup = ((JsonObject) sup).get(token);
                case DECIMAL -> {
                    return ((JsonDecimal) sup).decimal;
                }
                case STRING -> {
                    return new BigDecimal(((JsonString) sup).getString());
                }
                case NULL, BOOL -> {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL));
                    return BigDecimal.ZERO;
                }
            }
        }
        if (sup != null) {
            switch (sup.getType()) {
                case DECIMAL -> {
                    return ((JsonDecimal) sup).decimal;
                }
                case STRING -> {
                    return new BigDecimal(((JsonString) sup).getString());
                }
                case ARRAY, BOOL, OBJECT, NULL -> {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL));
                    return BigDecimal.ZERO;
                }
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL));
        return BigDecimal.ZERO;
    }

    public BigDecimal getHistoricalPrice(LCurrency search, LCurrency nat, LDate date) {
        ApiParseBundle bundle = new ApiParseBundle(search, nat, KEY, date);
        JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.hit(bundle.process(BASE_URL_HISTORY));
        JsonItem sup = null;
        for (String token : PARSE_PATH_HISTORY) {
            token = bundle.process(token);
            if (sup == null) {
                sup = item;
            }
            switch (sup.getType()) {
                case ARRAY -> sup = ((JsonArray) sup).get(Integer.parseInt(token));
                case OBJECT -> sup = ((JsonObject) sup).get(token);
                case DECIMAL -> {
                    return ((JsonDecimal) sup).decimal;
                }
                case STRING -> {
                    return new BigDecimal(((JsonString) sup).getString());
                }
                case BOOL, NULL -> {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL_HISTORY));
                    return BigDecimal.ZERO;
                }
            }
        }
        if (sup != null) {
            switch (sup.getType()) {
                case DECIMAL -> {
                    return ((JsonDecimal) sup).decimal;
                }
                case STRING -> {
                    return new BigDecimal(((JsonString) sup).getString());
                }
                case ARRAY, BOOL, OBJECT, NULL -> {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL));
                    return BigDecimal.ZERO;
                }
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL));
        return BigDecimal.ZERO;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject object = new JsonObject();
        object.FIELDS.put("name", new JsonString(NAME));
        object.FIELDS.put("api-key", new JsonString(KEY));
        object.FIELDS.put("base-url", new JsonString(BASE_URL));
        object.FIELDS.put("base-url-hist", new JsonString(BASE_URL_HISTORY));
        object.FIELDS.put("types", new JsonString(TYPES));
        JsonArray array = new JsonArray();
        for (String nat : NATS) {
            array.ARRAY.add(new JsonString(nat));
        }
        object.FIELDS.put("nats", array);
        array = new JsonArray();
        for (String except : EXCEPTS) {
            array.ARRAY.add(new JsonString(except));
        }
        object.FIELDS.put("excepts", array);
        array = new JsonArray();
        for (String s : PARSE_PATH) {
            array.ARRAY.add(new JsonString(s));
        }
        object.FIELDS.put("parse-path", array);
        array = new JsonArray();
        for (String s : PARSE_PATH_HISTORY) {
            array.ARRAY.add(new JsonString(s));
        }
        object.FIELDS.put("parse-path-hist", array);
        return object;
    }

    private static class ApiParseBundle {
        private static final ArrayList<String> TOKENS = new ArrayList<>(Arrays.asList(
                "@nat-tic@",
                "@nat-tic-l@",
                "@nat-name@",
                "@search-tic@",
                "@search-tic-l@",
                "@search-name@",
                "@year@",
                "@year-2@",
                "@month@",
                "@month-str@",
                "@month-long@",
                "@day@",
                "@epoch@",
                "@epoch-second@",
                "@epoch-second-m@",
                "@epoch-day@",
                "@epoch-day-m@",
                "@key@"
        ));

        final LCurrency SEARCH, NAT;
        final String KEY;
        final LDate DATE;

        public ApiParseBundle(LCurrency search, LCurrency nat, String key, LDate date) {
            SEARCH = search;
            NAT = nat;
            KEY = key;
            DATE = date;
        }

        public ApiParseBundle(LCurrency search, LCurrency nat, String key) {
            this(search, nat, key, null);
        }

        private String getAlteredName(LCurrency currency) {
            if (currency.getAltName().equals("")) {
                return currency.getName().toLowerCase().replace(" ", "-");
            } else {
                return currency.getAltName();
            }
        }

        public String getVariable(String token) {
            return switch (token) {
                case "@nat-tic@" -> NAT.getTicker().toUpperCase();
                case "@nat-tic-l@" -> NAT.getTicker().toLowerCase();
                case "@nat-name@" -> getAlteredName(NAT);
                case "@search-tic@" -> SEARCH.getTicker().toUpperCase();
                case "@search-tic-l@" -> SEARCH.getTicker().toLowerCase();
                case "@search-name@" -> getAlteredName(SEARCH);
                case "@year@" -> DATE == null ? "" : "" + DATE.getYear();
                case "@year-2@" -> DATE == null ? "" : "" + DATE.getYear() % 100;
                case "@month@" -> DATE == null ? "" : DATE.getMonth() < 10 ? "0" + DATE.getMonth() : "" + DATE.getMonth();
                case "@month-str@" -> DATE == null ? "" : DATE.getMonthStringShort();
                case "@month-long@" -> DATE == null ? "" : DATE.getMonthString();
                case "@day@" -> DATE == null ? "" : DATE.getDay() < 10 ? "0" + DATE.getDay() : "" + DATE.getDay();
                case "@epoch@" -> DATE == null ? "" : "" + DATE.getTime();
                case "@epoch-second@" -> DATE == null ? "" : "" + (DATE.getTime() / 1000);
                case "@epoch-second-m@" -> DATE == null ? "" : "" + (DATE.getTime() / 1000 - 1);
                case "@epoch-day@" -> DATE == null ? "" : "" + (DATE.getTime() / 86400000);
                case "@epoch-day-m@" -> DATE == null ? "" : "" + (DATE.getTime() / 86400000 - 1);
                case "@key@" -> KEY;
                default -> "";
            };
        }

        public String process(String raw) {
            for (String token : TOKENS) {
                raw = raw.replace(token, getVariable(token));
            }
            return raw;
        }
    }
}
