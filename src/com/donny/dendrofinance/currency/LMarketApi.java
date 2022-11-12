package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.fileio.ApiLimitReachedException;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.*;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.ExportableToJson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LMarketApi implements ExportableToJson, Serializable {
    protected static LCurrency[] ListToArray(ArrayList<LCurrency> list) {
        LCurrency[] out = new LCurrency[list.size()];
        for (int i = 0; i < list.size(); i++) {
            out[i] = list.get(i);
        }
        return out;
    }

    protected static ArrayList<LCurrency[]> partition(ArrayList<LCurrency> list, int limit) {
        ArrayList<LCurrency[]> out = new ArrayList<>();
        int cursor = 0;
        LCurrency[] current = null;
        for (LCurrency c : list) {
            if (cursor == 0) {
                if (current != null) {
                    out.add(current);
                }
                current = new LCurrency[limit];
            }
            current[cursor] = c;
            cursor++;
            if (cursor == limit) {
                cursor = 0;
            }
        }
        boolean flag = false;
        for (LCurrency c : current) {
            if (c != null) {
                flag = true;
                break;
            }
        }
        if (flag) {
            out.add(current);
        }
        return out;
    }

    public final String NAME, TYPES, SEPARATOR, BASE_URL, BASE_URL_HISTORY, MULTI_URL, MULTI_URL_HISTORY, KEY;
    public final int ATTEMPT_LIMIT, DURATION, MULTI_LIMIT, MULTI_HIST_LIMIT;
    private int attempts = 0;
    private final Instance CURRENT_INSTANCE;
    private final ArrayList<String> BASE_PARSE, BASE_HIST_PARSE, MULTI_PARSE, MULTI_HIST_PARSE, NATS, EXCEPTS;

    public LMarketApi(String name, String types, String baseUrl, String baseUrlHist, String multiUrl,
                      String multiUrlHist, String sep, String apiKey, int mLimit, int mhLimit, int attemptLimit,
                      int duration, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        NAME = name;
        TYPES = types;
        BASE_URL = baseUrl;
        BASE_URL_HISTORY = baseUrlHist;
        MULTI_URL = multiUrl;
        MULTI_URL_HISTORY = multiUrlHist;
        SEPARATOR = sep;
        KEY = apiKey;
        BASE_PARSE = new ArrayList<>();
        BASE_HIST_PARSE = new ArrayList<>();
        MULTI_PARSE = new ArrayList<>();
        MULTI_HIST_PARSE = new ArrayList<>();
        MULTI_LIMIT = mLimit;
        MULTI_HIST_LIMIT = mhLimit;
        NATS = new ArrayList<>();
        EXCEPTS = new ArrayList<>();
        ATTEMPT_LIMIT = attemptLimit;
        DURATION = duration;
    }

    public LMarketApi(JsonObject object, Instance curInst) {
        this(
                object.getString("name").getString(),
                object.getString("types").getString(),
                object.getString("base-url").getString(),
                object.getString("base-url-hist").getString(),
                object.getString("multi-url").getString(),
                object.getString("multi-url-hist").getString(),
                object.getString("sep").getString(),
                object.getString("api-key").getString(),
                object.getDecimal("multi-bounds").decimal.intValue(),
                object.getDecimal("multi-bounds-hist").decimal.intValue(),
                object.getDecimal("attempt-limit").decimal.intValue(),
                object.getDecimal("duration").decimal.intValue(),
                curInst
        );
        for (JsonString string : object.getArray("base-parse").getStringArray()) {
            BASE_PARSE.add(string.getString());
        }
        for (JsonString string : object.getArray("base-hist-parse").getStringArray()) {
            BASE_HIST_PARSE.add(string.getString());
        }
        for (JsonString string : object.getArray("multi-parse").getStringArray()) {
            MULTI_PARSE.add(string.getString());
        }
        for (JsonString string : object.getArray("multi-hist-parse").getStringArray()) {
            MULTI_HIST_PARSE.add(string.getString());
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

    public void resetBaseParsePath(ArrayList<String> newPath) {
        BASE_PARSE.clear();
        BASE_PARSE.addAll(newPath);
    }

    public void resetBaseParsePathHistory(ArrayList<String> newPath) {
        BASE_HIST_PARSE.clear();
        BASE_HIST_PARSE.addAll(newPath);
    }

    public void resetMultiParsePath(ArrayList<String> newPath) {
        BASE_PARSE.clear();
        BASE_PARSE.addAll(newPath);
    }

    public void resetMultiParsePathHistory(ArrayList<String> newPath) {
        BASE_HIST_PARSE.clear();
        BASE_HIST_PARSE.addAll(newPath);
    }

    public void resetNats(ArrayList<String> newPath) {
        NATS.clear();
        NATS.addAll(newPath);
    }

    public void resetExcepts(ArrayList<String> newPath) {
        EXCEPTS.clear();
        EXCEPTS.addAll(newPath);
    }

    public String readBaseParsePath() {
        if (BASE_PARSE.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : BASE_PARSE) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public String readBaseParsePathHistory() {
        if (BASE_HIST_PARSE.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : BASE_HIST_PARSE) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public String readMultiParsePath() {
        if (MULTI_PARSE.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : MULTI_PARSE) {
                sb.append(", ").append(s);
            }
            return sb.substring(2);
        }
    }

    public String readMultiParsePathHistory() {
        if (MULTI_HIST_PARSE.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String s : MULTI_HIST_PARSE) {
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
     * @param a      the asset to be converted from
     * @param b      the asset to be converted to
     * @return the value of <code>amount</code> of <code>a</code> in <code>b</code>, or -1 if they cannot be converted using this api
     */
    public BigDecimal convert(BigDecimal amount, LCurrency a, LCurrency b) {
        BigDecimal returnValue;
        try {
            if (hasNat(b) && canSearch(a)) {
                returnValue = amount.multiply(getCurrentPrice(a, b)).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
                attempts = 0;
                return returnValue;
            } else if (hasNat(a) && canSearch(b)) {
                returnValue = amount.divide(getCurrentPrice(b, a), CURRENT_INSTANCE.precision).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
                attempts = 0;
                return returnValue;
            } else if (canSearch(a) && canSearch(b)) {
                LCurrency nat;
                if (hasNat(CURRENT_INSTANCE.main)) {
                    nat = CURRENT_INSTANCE.main;
                } else {
                    nat = CURRENT_INSTANCE.getLCurrency(NATS.get(0));
                }
                BigDecimal x = getCurrentPrice(a, nat).divide(getCurrentPrice(b, nat), CURRENT_INSTANCE.precision);
                returnValue = amount.multiply(x).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
                attempts = 0;
                return returnValue;
            } else {
                return BigDecimal.valueOf(-1);
            }
        } catch (ApiLimitReachedException e) {
            if (attempts < ATTEMPT_LIMIT) {
                try {
                    CURRENT_INSTANCE.LOG_HANDLER.info(getClass(), "Waiting " + DURATION + " milliseconds");
                    TimeUnit.MILLISECONDS.sleep(DURATION);
                    attempts++;
                    return convert(amount, a, b);
                } catch (InterruptedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The sleep timer was interrupted");
                    return BigDecimal.valueOf(-1);
                }
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Attempts exhausted.  Either your duration is incorrect, or you've been banned.");
                return BigDecimal.valueOf(-1);
            }
        }
    }

    /**
     * @param amount the amount of currency <code>a</code> to be converted
     * @param a      the asset to be converted from
     * @param b      the asset to be converted to
     * @param date   the date to get the conversion in
     * @return the value of <code>amount</code> of <code>a</code> in <code>b</code>, or -1 if they cannot be converted using this api
     */
    public BigDecimal convert(BigDecimal amount, LCurrency a, LCurrency b, LDate date) {
        BigDecimal returnValue;
        try {
            if (hasNat(b) && canSearch(a)) {
                returnValue = amount.multiply(getHistoricalPrice(a, b, date)).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
                attempts = 0;
                return returnValue;
            } else if (hasNat(a) && canSearch(b)) {
                returnValue = amount.divide(getHistoricalPrice(b, a, date), CURRENT_INSTANCE.precision).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
                attempts = 0;
                return returnValue;
            } else if (canSearch(a) && canSearch(b)) {
                LCurrency nat;
                if (hasNat(CURRENT_INSTANCE.main)) {
                    nat = CURRENT_INSTANCE.main;
                } else {
                    nat = CURRENT_INSTANCE.getLCurrency(NATS.get(0));
                }
                BigDecimal x = getHistoricalPrice(a, nat, date).divide(getHistoricalPrice(b, nat, date), CURRENT_INSTANCE.precision);
                returnValue = amount.multiply(x).multiply(b.getFactor().divide(a.getFactor(), CURRENT_INSTANCE.precision));
                attempts = 0;
                return returnValue;
            } else {
                return BigDecimal.valueOf(-1);
            }
        } catch (ApiLimitReachedException e) {
            if (attempts < 5) {
                try {
                    CURRENT_INSTANCE.LOG_HANDLER.info(getClass(), "Waiting " + DURATION + " milliseconds");
                    TimeUnit.MILLISECONDS.sleep(DURATION);
                    attempts++;
                    return convert(amount, a, b, date);
                } catch (InterruptedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The sleep timer was interrupted");
                    return BigDecimal.valueOf(-1);
                }
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Attempts exhausted.  Either your duration is incorrect, or you've been banned.");
                return BigDecimal.valueOf(-1);
            }
        }
    }

    /**
     * @param searches the assets to convert from
     * @param b        the asset to convert to
     * @return the values of <code>1</code> of each asset in <code>searches</code> converted to <code>b</code>, with only assets able to be converted included
     */
    public HashMap<LCurrency, BigDecimal> convert(ArrayList<LCurrency> searches, LCurrency b) {
        HashMap<LCurrency, BigDecimal> out = new HashMap<>();
        ArrayList<LCurrency> actual = new ArrayList<>();
        ArrayList<LCurrency> nats = new ArrayList<>();
        for (LCurrency c : searches) {
            if (canConvert(c, b)) {
                if (hasNat(c)) {
                    nats.add(c);
                } else {
                    actual.add(c);
                }
            }
        }
        for (LCurrency[] list : partition(actual, MULTI_LIMIT)) {
            int count = 0;
            for (LCurrency c : list) {
                if (c != null) {
                    count++;
                } else {
                    break;
                }
            }
            if (count > 1) {
                HashMap<LCurrency, BigDecimal> part = convert_(list, b);
                for (LCurrency c : part.keySet()) {
                    out.put(c, part.get(c).multiply(b.getFactor().divide(c.getFactor(), CURRENT_INSTANCE.precision)));
                }
            } else {
                out.put(list[0], convert(BigDecimal.ONE, list[0], b));
            }
        }
        for (LCurrency n : nats) {
            out.put(n, convert(BigDecimal.ONE, n, b));
        }
        return out;
    }

    private HashMap<LCurrency, BigDecimal> convert_(LCurrency[] searches, LCurrency b) {
        try {
            if (hasNat(b)) {
                return getCurrentPrices(searches, b);
            } else if (hasNat(CURRENT_INSTANCE.main)) {
                HashMap<LCurrency, BigDecimal> temp = getCurrentPrices(searches, CURRENT_INSTANCE.main);
                BigDecimal x = convert(BigDecimal.ONE, CURRENT_INSTANCE.main, b);
                HashMap<LCurrency, BigDecimal> out = new HashMap<>();
                for (LCurrency c : temp.keySet()) {
                    out.put(c, temp.get(c).multiply(x));
                }
                return out;
            } else {
                LCurrency nat = CURRENT_INSTANCE.getLCurrency(NATS.get(0));
                HashMap<LCurrency, BigDecimal> temp = getCurrentPrices(searches, nat);
                BigDecimal x = convert(BigDecimal.ONE, nat, b);
                HashMap<LCurrency, BigDecimal> out = new HashMap<>();
                for (LCurrency c : temp.keySet()) {
                    out.put(c, temp.get(c).multiply(x));
                }
                return out;
            }
        } catch (ApiLimitReachedException e) {
            if (attempts < ATTEMPT_LIMIT) {
                try {
                    CURRENT_INSTANCE.LOG_HANDLER.info(getClass(), "Waiting " + DURATION + " milliseconds");
                    TimeUnit.MILLISECONDS.sleep(DURATION);
                    attempts++;
                    return convert_(searches, b);
                } catch (InterruptedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The sleep timer was interrupted");
                    return new HashMap<>();
                }
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Attempts exhausted.  Either your duration is incorrect, or you've been banned.");
                return new HashMap<>();
            }
        }
    }

    /**
     * @param searches the assets to convert from
     * @param b        the asset to convert to
     * @return the values of <code>1</code> of each asset in <code>searches</code> converted to <code>b</code>, with only assets able to be converted included
     */
    public HashMap<LCurrency, BigDecimal> convert(ArrayList<LCurrency> searches, LCurrency b, LDate date) {
        HashMap<LCurrency, BigDecimal> out = new HashMap<>();
        ArrayList<LCurrency> actual = new ArrayList<>();
        ArrayList<LCurrency> nats = new ArrayList<>();
        for (LCurrency c : searches) {
            if (canConvert(c, b)) {
                if (hasNat(c)) {
                    nats.add(c);
                } else {
                    actual.add(c);
                }
            }
        }
        for (LCurrency[] list : partition(actual, MULTI_HIST_LIMIT)) {
            int count = 0;
            for (LCurrency c : list) {
                if (c != null) {
                    count++;
                } else {
                    break;
                }
            }
            if (count > 1) {
                HashMap<LCurrency, BigDecimal> part = convert_(list, b, date);
                for (LCurrency c : part.keySet()) {
                    out.put(c, part.get(c).multiply(b.getFactor().divide(c.getFactor(), CURRENT_INSTANCE.precision)));
                }
            } else {
                out.put(list[0], convert(BigDecimal.ONE, list[0], b, date));
            }
        }
        for (LCurrency n : nats) {
            out.put(n, convert(BigDecimal.ONE, n, b, date));
        }
        return out;
    }

    private HashMap<LCurrency, BigDecimal> convert_(LCurrency[] searches, LCurrency b, LDate date) {
        try {
            if (NATS.contains(b.toString())) {
                return getHistoricalPrices(searches, b, date);
            } else if (NATS.contains(CURRENT_INSTANCE.main.toString())) {
                HashMap<LCurrency, BigDecimal> temp = getHistoricalPrices(searches, CURRENT_INSTANCE.main, date);
                BigDecimal x = convert(BigDecimal.ONE, CURRENT_INSTANCE.main, b);
                HashMap<LCurrency, BigDecimal> out = new HashMap<>();
                for (LCurrency c : temp.keySet()) {
                    out.put(c, temp.get(c).multiply(x));
                }
                return out;
            } else {
                LCurrency nat = CURRENT_INSTANCE.getLCurrency(NATS.get(0));
                HashMap<LCurrency, BigDecimal> temp = getHistoricalPrices(searches, nat, date);
                BigDecimal x = convert(BigDecimal.ONE, nat, b);
                HashMap<LCurrency, BigDecimal> out = new HashMap<>();
                for (LCurrency c : temp.keySet()) {
                    out.put(c, temp.get(c).multiply(x));
                }
                return out;
            }
        } catch (ApiLimitReachedException e) {
            if (attempts < ATTEMPT_LIMIT) {
                try {
                    CURRENT_INSTANCE.LOG_HANDLER.info(getClass(), "Waiting " + DURATION + " milliseconds");
                    TimeUnit.MILLISECONDS.sleep(DURATION);
                    attempts++;
                    return convert_(searches, b);
                } catch (InterruptedException ex) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "The sleep timer was interrupted");
                    return new HashMap<>();
                }
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Attempts exhausted.  Either your duration is incorrect, or you've been banned.");
                return new HashMap<>();
            }
        }
    }

    private BigDecimal getCurrentPrice(LCurrency search, LCurrency nat) throws ApiLimitReachedException {
        ApiParseBundle bundle = new ApiParseBundle(search, nat, KEY);
        JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.hit(bundle.process(BASE_URL));
        JsonItem sup = null;
        for (String token : BASE_PARSE) {
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
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL) + "\n" + item.print());
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
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL) + "\n" + item.print());
                    return BigDecimal.ZERO;
                }
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL) + "\n" + item.print());
        return BigDecimal.ZERO;
    }

    private HashMap<LCurrency, BigDecimal> getCurrentPrices(LCurrency[] searches, LCurrency nat) throws ApiLimitReachedException {
        HashMap<LCurrency, BigDecimal> out = new HashMap<>();
        MultiApiParseBundle bundle = new MultiApiParseBundle(searches, nat, KEY, SEPARATOR);
        JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.hit(bundle.process(MULTI_URL, null));
        for (LCurrency c : searches) {
            if (c != null) {
                JsonItem sup = null;
                for (String token : MULTI_PARSE) {
                    token = bundle.process(token, c);
                    if (sup == null) {
                        sup = item;
                    }
                    switch (sup.getType()) {
                        case ARRAY -> sup = ((JsonArray) sup).get(Integer.parseInt(token));
                        case OBJECT -> sup = ((JsonObject) sup).get(token);
                        case DECIMAL -> out.put(c, ((JsonDecimal) sup).decimal);
                        case STRING -> out.put(c, new BigDecimal(((JsonString) sup).getString()));
                        case NULL, BOOL ->
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(MULTI_URL) + "\n" + item.print());
                    }
                }
                if (sup != null) {
                    switch (sup.getType()) {
                        case DECIMAL -> out.put(c, ((JsonDecimal) sup).decimal);
                        case STRING -> out.put(c, new BigDecimal(((JsonString) sup).getString()));
                        case ARRAY, BOOL, OBJECT, NULL ->
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(MULTI_URL) + "\n" + item.print());
                    }
                }
            } else {
                break;
            }
        }
        return out;
    }

    private HashMap<LCurrency, BigDecimal> getHistoricalPrices(LCurrency[] searches, LCurrency nat, LDate date) throws ApiLimitReachedException {
        HashMap<LCurrency, BigDecimal> out = new HashMap<>();
        MultiApiParseBundle bundle = new MultiApiParseBundle(searches, nat, KEY, SEPARATOR, date);
        JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.hit(bundle.process(MULTI_URL_HISTORY, null));
        for (LCurrency c : searches) {
            JsonItem sup = null;
            for (String token : MULTI_HIST_PARSE) {
                token = bundle.process(token, c);
                if (sup == null) {
                    sup = item;
                }
                switch (sup.getType()) {
                    case ARRAY -> sup = ((JsonArray) sup).get(Integer.parseInt(token));
                    case OBJECT -> sup = ((JsonObject) sup).get(token);
                    case DECIMAL -> out.put(c, ((JsonDecimal) sup).decimal);
                    case STRING -> out.put(c, new BigDecimal(((JsonString) sup).getString()));
                    case NULL, BOOL ->
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(MULTI_URL) + "\n" + item.print());
                }
            }
            if (sup != null) {
                switch (sup.getType()) {
                    case DECIMAL -> out.put(c, ((JsonDecimal) sup).decimal);
                    case STRING -> out.put(c, new BigDecimal(((JsonString) sup).getString()));
                    case ARRAY, BOOL, OBJECT, NULL ->
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(MULTI_URL) + "\n" + item.print());
                }
            }
        }
        return out;
    }

    private BigDecimal getHistoricalPrice(LCurrency search, LCurrency nat, LDate date) throws ApiLimitReachedException {
        ApiParseBundle bundle = new ApiParseBundle(search, nat, KEY, date);
        JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.hit(bundle.process(BASE_URL_HISTORY));
        JsonItem sup = null;
        for (String token : BASE_HIST_PARSE) {
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
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL_HISTORY) + "\n" + item.print());
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
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL_HISTORY) + "\n" + item.print());
                    return BigDecimal.ZERO;
                }
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "It looks like the api wasn't structured how you thought it was.\n" + bundle.process(BASE_URL_HISTORY) + "\n" + item.print());
        return BigDecimal.ZERO;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject object = new JsonObject();
        object.put("name", new JsonString(NAME));
        object.put("api-key", new JsonString(KEY));
        object.put("sep", new JsonString(SEPARATOR));
        object.put("base-url", new JsonString(BASE_URL));
        object.put("base-url-hist", new JsonString(BASE_URL_HISTORY));
        object.put("multi-url", new JsonString(MULTI_URL));
        object.put("multi-url-hist", new JsonString(MULTI_URL_HISTORY));
        object.put("multi-bounds", new JsonDecimal(MULTI_LIMIT));
        object.put("multi-bounds-hist", new JsonDecimal(MULTI_HIST_LIMIT));
        object.put("types", new JsonString(TYPES));
        JsonArray array = new JsonArray();
        for (String nat : NATS) {
            array.add(new JsonString(nat));
        }
        object.put("nats", array);
        array = new JsonArray();
        for (String except : EXCEPTS) {
            array.add(new JsonString(except));
        }
        object.put("excepts", array);
        array = new JsonArray();
        for (String s : BASE_PARSE) {
            array.add(new JsonString(s));
        }
        object.put("base-parse", array);
        array = new JsonArray();
        for (String s : BASE_HIST_PARSE) {
            array.add(new JsonString(s));
        }
        object.put("base-hist-parse", array);
        array = new JsonArray();
        for (String s : MULTI_PARSE) {
            array.add(new JsonString(s));
        }
        object.put("multi-parse", array);
        array = new JsonArray();
        for (String s : MULTI_HIST_PARSE) {
            array.add(new JsonString(s));
        }
        object.put("multi-hist-parse", array);
        object.put("attempt-limit", new JsonDecimal(ATTEMPT_LIMIT));
        object.put("duration", new JsonDecimal(DURATION));
        return object;
    }

    private static class ApiParseBundle {
        protected static final ArrayList<String> TOKENS = new ArrayList<>(Arrays.asList(
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

        protected final LCurrency SEARCH, NAT;
        protected final String KEY;
        protected final LDate DATE;

        public ApiParseBundle(LCurrency search, LCurrency nat, String key, LDate date) {
            SEARCH = search;
            NAT = nat;
            KEY = key;
            DATE = date;
        }

        public ApiParseBundle(LCurrency search, LCurrency nat, String key) {
            this(search, nat, key, null);
        }

        protected String getAlteredName(LCurrency currency) {
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
                case "@month@" ->
                        DATE == null ? "" : DATE.getMonth() < 10 ? "0" + DATE.getMonth() : "" + DATE.getMonth();
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
                if (raw.contains(token)) {
                    raw = raw.replace(token, getVariable(token));
                }
            }
            return raw;
        }
    }

    private static class MultiApiParseBundle extends ApiParseBundle {
        protected static final ArrayList<String> TOKENS_MULTI = new ArrayList<>(Arrays.asList(
                "@nat-tic@",
                "@nat-tic-l@",
                "@nat-name@",
                "@search-tic@",
                "@search-tic-l@",
                "@search-name@",
                "@searches-tic@",
                "@searches-tic-l@",
                "@searches-name@",
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
        protected final ArrayList<LCurrency> SEARCHES;
        protected final String SEPARATOR;

        public MultiApiParseBundle(LCurrency[] searches, LCurrency nat, String key, String separator) {
            super(null, nat, key);
            SEARCHES = new ArrayList<>();
            for (LCurrency c : searches) {
                if (c != null) {
                    SEARCHES.add(c);
                } else {
                    break;
                }
            }
            SEPARATOR = separator;
        }

        public MultiApiParseBundle(LCurrency[] searches, LCurrency nat, String key, String separator, LDate date) {
            super(null, nat, key, date);
            SEARCHES = new ArrayList<>();
            for (LCurrency c : searches) {
                if (c != null) {
                    SEARCHES.add(c);
                } else {
                    break;
                }
            }
            SEPARATOR = separator;
        }

        public String aggregateName() {
            StringBuilder out = new StringBuilder();
            for (LCurrency c : SEARCHES) {
                out.append(getAlteredName(c)).append(SEPARATOR);
            }
            return out.substring(0, out.length() - SEPARATOR.length());
        }

        public String aggregateTicker() {
            StringBuilder out = new StringBuilder();
            for (LCurrency c : SEARCHES) {
                out.append(c.getTicker()).append(SEPARATOR);
            }
            return out.substring(0, out.length() - SEPARATOR.length());
        }

        public String getVariable(String token, LCurrency search) {
            return switch (token) {
                case "@nat-tic@" -> NAT.getTicker().toUpperCase();
                case "@nat-tic-l@" -> NAT.getTicker().toLowerCase();
                case "@nat-name@" -> getAlteredName(NAT);
                case "@search-tic@" -> search.getTicker().toUpperCase();
                case "@search-tic-l@" -> search.getTicker().toLowerCase();
                case "@search-name@" -> getAlteredName(search);
                case "@searches-tic@" -> aggregateTicker();
                case "@searches-tic-l@" -> aggregateTicker().toLowerCase();
                case "@searches-name@" -> aggregateName();
                case "@year@" -> DATE == null ? "" : "" + DATE.getYear();
                case "@year-2@" -> DATE == null ? "" : "" + DATE.getYear() % 100;
                case "@month@" ->
                        DATE == null ? "" : DATE.getMonth() < 10 ? "0" + DATE.getMonth() : "" + DATE.getMonth();
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

        public String process(String raw, LCurrency search) {
            for (String token : TOKENS_MULTI) {
                if (raw.contains(token)) {
                    raw = raw.replace(token, getVariable(token, search));
                }
            }
            return raw;
        }
    }
}
