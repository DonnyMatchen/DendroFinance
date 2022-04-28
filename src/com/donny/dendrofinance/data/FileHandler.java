package com.donny.dendrofinance.data;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LDate;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class FileHandler {
    private final Instance CURRENT_INSTANCE;

    public FileHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "FileHandler Initiated");
    }

    public String read(File file) {
        ensure(file.getParentFile());
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, Charset.forName("unicode")))) {
            boolean flag = true;
            while (flag) {
                int x = reader.read();
                if (x != -1) {
                    output.append((char) x);
                } else {
                    flag = false;
                }
            }
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file read: " + file.getAbsolutePath());
            return output.toString().replace("\r", "");
        } catch (IOException e) {
            if (file.exists()) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " could not be read from");
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " does not exist");
            }
            return "";
        }
    }

    public String read(File dir, String file) {
        return read(new File(dir.getPath() + File.separator + file));
    }

    public String readPlain(File file) {
        ensure(file.getParentFile());
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.US_ASCII))) {
            boolean flag = true;
            while (flag) {
                int x = reader.read();
                if (x != -1) {
                    output.append((char) x);
                } else {
                    flag = false;
                }
            }
            reader.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file read: " + file.getAbsolutePath());
            return output.toString().replace("\r", "");
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " could not be read from");
            return "";
        }
    }

    public String readPlain(File dir, String file) {
        return readPlain(new File(dir.getPath() + File.separator + file));
    }

    public String readDecrypt(File file) {
        ensure(file.getParentFile());
        return CURRENT_INSTANCE.ENCRYPTION_HANDLER.decrypt(read(file)).replace("\r", "");
    }

    public String readDecrypt(File dir, String file) {
        return readDecrypt(new File(dir.getPath() + File.separator + file));
    }

    public byte[] getTemplate(String path) {
        return getResource("templates/" + path);
    }

    public byte[] getResource(String path) {
        InputStream stream = this.getClass().getResourceAsStream("/com/donny/dendrofinance/resources/" + path);
        try {
            return stream.readAllBytes();
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Resource not located: " + path);
            return null;
        }
    }

    public void write(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Charset.forName("unicode")))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), output);
        }
    }

    public void write(File dir, String file, String output) {
        write(new File(dir.getPath() + File.separator + file), output);
    }

    public void writePlain(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.US_ASCII))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file written: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), output);
        }
    }

    public void writePlain(File dir, String file, String output) {
        writePlain(new File(dir.getPath() + File.separator + file), output);
    }

    public void writeEncrypt(File file, String output) {
        ensure(file.getParentFile());
        write(file, CURRENT_INSTANCE.ENCRYPTION_HANDLER.encrypt(output));
    }

    public void writeEncrypt(File dir, String file, String output) {
        writeEncrypt(new File(dir.getPath() + File.separator + file), output);
    }

    public void append(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, Charset.forName("unicode"), true))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file appended: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), output);
        }
    }

    public void append(File dir, String file, String output) {
        append(new File(dir.getPath() + File.separator + file), output);
    }

    public void appendPlain(File file, String output) {
        ensure(file.getParentFile());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.US_ASCII, true))) {
            writer.write(output);
            writer.close();
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file appended: " + file.getAbsolutePath());
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), file.getPath() + " could not be written to");
            CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), output);
        }
    }

    public void appendPlain(File dir, String file, String output) {
        appendPlain(new File(dir.getPath() + File.separator + file), output);
    }

    public void delete(File file) {
        ensure(file.getParentFile());
        file.delete();
        CURRENT_INSTANCE.LOG_HANDLER.debug(this.getClass(), "file deleted: " + file.getAbsolutePath());
    }

    public void delete(File dir, String file) {
        delete(new File(dir.getPath() + File.separator + file));
    }

    public void deleteR(File root) {
        File[] rootList = root.listFiles();
        if (root.isDirectory() && rootList != null) {
            for (File f : rootList) {
                deleteR(f);
            }
        }
        delete(root);
    }

    public HashMap<String, BigDecimal> hitTwelveData(ArrayList<String> tickers, LDate date) {
        String day = new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
        HashMap<String, BigDecimal> out = new HashMap<>();
        StringBuilder ticker = new StringBuilder();
        for (String tick : tickers) {
            ticker.append(",").append(tick);
        }
        JsonObject obj = (JsonObject) hit("https://api.twelvedata.com/time_series?symbol=" + ticker.substring(1) + "&interval=1day&outputsize=1&end_date=" + day + "&apikey=" + CURRENT_INSTANCE.twelveDataApiKey);
        for (String tick : tickers) {
            JsonObject object = obj.getObject(tick);
            if (object.FIELDS.containsKey("values")) {
                JsonArray res = object.getArray("values");
                if (!res.ARRAY.isEmpty()) {
                    if (tick.contains("/")) {
                        out.put(tick, BigDecimal.ONE.divide(new BigDecimal(res.getObject(0).getString("close").getString()), CURRENT_INSTANCE.PRECISION));
                    } else {
                        out.put(tick, new BigDecimal(res.getObject(0).getString("close").getString()));
                    }
                }
            }
        }
        return out;
    }

    public BigDecimal hitTwelveData(String ticker, LDate date) {
        String day = new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
        JsonObject obj = (JsonObject) hit("https://api.twelvedata.com/time_series?symbol=" + ticker + "&interval=1day&outputsize=1&end_date=" + day + "&apikey=" + CURRENT_INSTANCE.twelveDataApiKey);
        if (obj.FIELDS.containsKey("values")) {
            JsonArray res = obj.getArray("values");
            if (!res.ARRAY.isEmpty()) {
                return new BigDecimal(res.getObject(0).getString("close").getString());
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal hitTwelveData(String ticker) {
        return hitTwelveData(ticker, LDate.now(CURRENT_INSTANCE).yesterday());
    }

    public BigDecimal hitTwelveDataForex(String from, String to, LDate date) {
        BigDecimal first = hitTwelveData(from + "/" + to, date);
        if (first.compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal second = hitTwelveData(to + "/" + from, date);
            if (second.compareTo(BigDecimal.ZERO) == 0) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Incompatible TwelveData pair: " + from + ", " + to);
                return BigDecimal.ZERO;
            } else {
                return BigDecimal.ONE.divide(second, CURRENT_INSTANCE.PRECISION);
            }
        } else {
            return first;
        }

    }

    public BigDecimal hitTwelveDataForex(String from, String to) {
        return hitTwelveDataForex(from, to, LDate.now(CURRENT_INSTANCE));
    }

    public BigDecimal hitTwelveDataForex(String ticker, LDate date) {
        return hitTwelveDataForex(ticker, CURRENT_INSTANCE.main.getTicker(), date);
    }

    public BigDecimal hitTwelveDataForex(String ticker) {
        return hitTwelveDataForex(ticker, CURRENT_INSTANCE.main.getTicker());
    }

    public BigDecimal hitPolygon(String ticker) {
        String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/prev?adjusted=true&apiKey=" + CURRENT_INSTANCE.polygonApiKey;
        JsonObject obj = (JsonObject) hit(url);
        if (obj.FIELDS.containsKey("results")) {
            JsonObject resp = obj.getArray("results").getObject(0);
            return resp.getDecimal("c").decimal;
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal hitPolygon(String ticker, LDate date) {
        return hitPolygon(ticker, date, false);
    }

    private BigDecimal hitPolygon(String ticker, LDate date, boolean tr) {
        String day = new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
        String url = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day/" + day + "/" + day + "?adjusted=true&sort=asc&limit=120&apiKey=" + CURRENT_INSTANCE.polygonApiKey;
        JsonObject obj = (JsonObject) hit(url);
        if (obj.FIELDS.containsKey("results")) {
            JsonObject resp = obj.getArray("results").getObject(0);
            return resp.getDecimal("c").decimal;
        } else {
            if (tr) {
                return BigDecimal.ZERO;
            } else {
                return hitPolygon(ticker, date.yesterday(), true);
            }
        }
    }

    public BigDecimal hitPolygonForex(String ticker, String nat) {
        return hitPolygon("C:" + ticker + nat);
    }

    public BigDecimal hitPolygonForex(String ticker) {
        return hitPolygonForex(ticker, CURRENT_INSTANCE.main.getTicker());
    }

    public BigDecimal hitPolygonForex(String ticker, String nat, LDate date) {
        return hitPolygon("C:" + ticker + nat, date);
    }

    public BigDecimal hitPolygonForex(String ticker, LDate date) {
        return hitPolygonForex(ticker, CURRENT_INSTANCE.main.getTicker(), date);
    }

    public BigDecimal hitKraken(String ticker, String vs) {
        JsonObject obj = (JsonObject) hit("https://api.kraken.com/0/public/Ticker?pair=" + ticker + vs);
        JsonObject result = obj.getObject("result");
        if (result.FIELDS.keySet().size() == 1) {
            String token = new ArrayList<>(result.FIELDS.keySet()).get(0);
            return new BigDecimal(result.getObject(token).getArray("c").getString(0).getString());
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal hitKraken(String ticker) {
        return hitKraken(ticker, CURRENT_INSTANCE.main.getTicker());
    }

    public BigDecimal hitKrakenHistory(String ticker, String vs, LDate date) {
        JsonObject obj = (JsonObject) hit("https://api.kraken.com/0/public/OHLC?pair=" + ticker + vs + "&since=" + (date.getTime() / 1000 - 1) + "&interval=1440");
        if (obj.FIELDS.containsKey("result")) {
            JsonObject resp = obj.getObject("result");
            for (String key : resp.FIELDS.keySet()) {
                if (!key.equals("last")) {
                    return new BigDecimal(resp.getArray(key).getArray(0).getString(4).getString());
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal hitKrakenHistory(String ticker, LDate date) {
        return hitKrakenHistory(ticker, CURRENT_INSTANCE.main.getTicker(), date);
    }

    public BigDecimal hitCoinGecko(String name, String vs) {
        name = name.replace(" ", "-").toLowerCase();
        vs = vs.toLowerCase();
        JsonObject obj = (JsonObject) hit("https://api.coingecko.com/api/v3/simple/price?ids=" + name + "&vs_currencies=" + vs);
        if (obj.FIELDS.containsKey(name)) {
            return obj.getObject(name).getDecimal(vs).decimal;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal hitCoinGecko(String name) {
        return hitCoinGecko(name, CURRENT_INSTANCE.main.getTicker());
    }

    public BigDecimal hitCoinGeckoHistory(String name, String vs, LDate date) {
        return hitCoinGeckoHistory(name, vs, date.getYear(), date.getMonth(), date.getDay());
    }

    public BigDecimal hitCoinGeckoHistory(String name, String vs, int y, int m, int d) {
        name = name.replace(" ", "-").toLowerCase();
        vs = vs.toLowerCase();
        String dat = d + "-" + m + "-" + y;
        JsonObject obj = (JsonObject) hit("https://api.coingecko.com/api/v3/coins/" + name + "/history?date=" + dat);
        if (obj.FIELDS.containsKey("market_data")) {
            return obj.getObject("market_data").getObject("current_price").getDecimal(vs).decimal;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal hitCoinGeckoHistory(String name, LDate date) {
        return hitCoinGeckoHistory(name, CURRENT_INSTANCE.main.getTicker(), date);
    }

    public BigDecimal getLatestPrivateStock(String name) {
        JsonArray brackets = getPrivateStock(name);
        return brackets.getObject(brackets.ARRAY.size() - 1).getDecimal("price").decimal;
    }

    public JsonArray getPrivateStock(String name) {
        File directory = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "P_Stock");
        JsonArray history = null;
        File[] directoryList = directory.listFiles();
        if (directory.exists() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(name)) {
                    try {
                        history = (JsonArray) JsonItem.sanitizeDigest(readPlain(f));
                    } catch (JsonFormattingException e) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Bad Private Stock File: " + f.getPath());
                    }
                }
            }
        }
        if (history != null) {
            return history;
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "No History File found!: " + name);
            return null;
        }
    }

    public JsonItem hit(String url) {
        System.out.println(url);
        try {
            return JsonItem.sanitizeDigest(streamRaw(url));
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Bad Json at:" + url + "\n" + e);
            return null;
        }
    }

    public String streamRaw(String url) {
        int c = 0;
        try (
                InputStream stream = new URL(url).openStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
        ) {
            StringBuilder sb = new StringBuilder();
            while (c != -1) {
                c = reader.read();
                if (c != -1) {
                    sb.append((char) c);
                }
            }
            stream.close();
            return sb.toString();
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Could not connect to " + url + "\n" + e);
            return "";
        }
    }

    public void ensure(File file) {
        if (!file.exists()) {
            if (file.getParentFile().exists()) {
                file.mkdir();
            } else {
                ensure(file.getParentFile());
            }
        }
    }
}
