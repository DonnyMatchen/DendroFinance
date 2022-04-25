package com.donny.dendrofinance.instance;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountType;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.data.*;
import com.donny.dendrofinance.data.backingtable.*;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.PasswordGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.json.*;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class Instance {
    private static final ArrayList<String> root = new ArrayList<>(Arrays.asList("USD", "EUR", "GBP", "CHF", "JPY", "CNY"));

    //Major managing objects and handling lists
    public final String IID;
    public final MathContext PRECISION = new MathContext(20);
    public final LogHandler LOG_HANDLER;
    public final PasswordGui ENCRYPTION_HANDLER;
    public final FileHandler FILE_HANDLER;
    public final UuidHandler UUID_HANDLER;
    public final CurrencyBTC CURRENCIES;
    public final StockBTC STOCKS;
    public final InventoryBTC INVENTORIES;
    public final AccountBTC ACCOUNTS;
    public final ExchangeBTC EXCHANGES;
    public final AccountTypeBTC ACCOUNT_TYPES;
    public final TaxItemBTC TAX_ITEMS;
    public final ArrayList<LCurrency> VS = new ArrayList<>();
    public final ImportHandler IMPORT_HANDLER;
    public final ExportHandler EXPORT_HANDLER;
    public final DataHandler DATA_HANDLER;
    public File data = new File(System.getProperty("user.dir") + File.separator + "data");

    //flags, api keys, and other minor alterable things
    public boolean log = false, export = false, american = true, day = false;
    public String twelveDataApiKey = "ADD KEY HERE", polygonApiKey = "ADD KEY HERE", stockAPI = "twelve", mainTicker = "USD", main__Ticker = "USD Extra";
    public LogHandler.LogLevel logLevel;
    public LCurrency main;
    public LCurrency main__;

    public Instance(String iid, String[] args) {
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                if (arg.equalsIgnoreCase("-X")) {
                    export = true;
                } else if (arg.equalsIgnoreCase("-L")) {
                    log = true;
                    if (args.length > i + 1) {
                        logLevel = new LogHandler.LogLevel(args[i + 1].toLowerCase());
                    }
                }
            }
        }
        if (logLevel == null) {
            logLevel = new LogHandler.LogLevel("info");
        }
        IID = iid;
        CURRENCIES = new CurrencyBTC(this);
        STOCKS = new StockBTC(this);
        INVENTORIES = new InventoryBTC(this);
        ACCOUNTS = new AccountBTC(this);
        EXCHANGES = new ExchangeBTC(this);
        ACCOUNT_TYPES = new AccountTypeBTC(this);
        TAX_ITEMS = new TaxItemBTC(this);
        LOG_HANDLER = new LogHandler(logLevel.getLevel(), this);
        FILE_HANDLER = new FileHandler(this);
        DendroFactory.init(this);
        ENCRYPTION_HANDLER = new PasswordGui(args, this);
        UUID_HANDLER = new UuidHandler(this);
        ENCRYPTION_HANDLER.setVisible(true);
        do {
            System.out.print("");
        } while (!ENCRYPTION_HANDLER.done);
        try {
            loadStuff();
        } catch (JsonFormattingException e) {
            LOG_HANDLER.fatal(this.getClass(), "Mis-formatted data!\n" + e);
            LOG_HANDLER.save();
            System.exit(1);
        }

        //Data
        DATA_HANDLER = new DataHandler(this);
        DATA_HANDLER.init();
        for (TransactionEntry entry : DATA_HANDLER.readTransactions()) {
            if (!entry.isBalanced()) {
                LOG_HANDLER.error(this.getClass(), "Unbalanced entry: " + entry.getUUID());
            }
        }
        DATA_HANDLER.checkLedgers();
        DATA_HANDLER.checkCG();
        IMPORT_HANDLER = new ImportHandler(this);
        EXPORT_HANDLER = new ExportHandler(this);
        new MainGui(this).setVisible(true);
    }

    public Instance(String[] args) {
        this(DendroFinance.newIid(), args);
    }

    public final void loadStuff() throws JsonFormattingException {
        LOG_HANDLER.trace(this.getClass(), "Instance.loadStuff() run");
        //establishing data files
        File currencies = new File(data.getPath() + File.separator + "Currencies" + File.separator + "currencies.json"),
                stocks = new File(data.getPath() + File.separator + "Currencies" + File.separator + "stocks.json"),
                inventories = new File(data.getPath() + File.separator + "Currencies" + File.separator + "inventories.json"),
                special = new File(data.getPath() + File.separator + "Currencies" + File.separator + "special.json"),
                exchanges = new File(data.getPath() + File.separator + "Accounts" + File.separator + "exchanges.json"),
                accounts = new File(data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json"),
                extran = new File(data.getPath() + File.separator + "Accounts" + File.separator + "extranious.json"),
                accTyp = new File(data.getPath() + File.separator + "Accounts" + File.separator + "account-types.json"),
                taxItm = new File(data.getPath() + File.separator + "Accounts" + File.separator + "tax-items.json");
        //Create Defaults for new profiles
        {
            //Folders
            {
                File archive = new File(data.getPath() + File.separator + "Archives"),
                        pstock = new File(data.getPath() + File.separator + "P_Stock"),
                        exp = new File(data.getPath() + File.separator + "Exports");
                if (!archive.exists()) {
                    archive.mkdir();
                }
                if (!pstock.exists()) {
                    pstock.mkdir();
                }
                if (!exp.exists()) {
                    exp.mkdir();
                }
            }
            //Currencies
            {
                if (!currencies.exists()) {
                    FILE_HANDLER.write(currencies, new String(FILE_HANDLER.getTemplate("Currencies/currencies.json"), Charset.forName("unicode")));
                }
                if (!stocks.exists()) {
                    FILE_HANDLER.write(stocks, new String(FILE_HANDLER.getTemplate("Currencies/stocks.json"), Charset.forName("unicode")));
                }
                if (!inventories.exists()) {
                    FILE_HANDLER.write(inventories, new String(FILE_HANDLER.getTemplate("Currencies/inventories.json"), Charset.forName("unicode")));
                }
                if (!special.exists()) {
                    FILE_HANDLER.write(special, new String(FILE_HANDLER.getTemplate("Currencies/special.json"), Charset.forName("unicode")));
                }
            }
            //Accounts
            {
                if (!exchanges.exists()) {
                    FILE_HANDLER.write(exchanges, new String(FILE_HANDLER.getTemplate("Accounts/exchanges.json"), Charset.forName("unicode")));
                }
                if (!accounts.exists()) {
                    FILE_HANDLER.write(accounts, new String(FILE_HANDLER.getTemplate("Accounts/accounts.json"), Charset.forName("unicode")));
                }
                if (!extran.exists()) {
                    FILE_HANDLER.write(extran, new String(FILE_HANDLER.getTemplate("Accounts/extranious.json"), Charset.forName("unicode")));
                }
                if (!accTyp.exists()) {
                    FILE_HANDLER.write(accTyp, new String(FILE_HANDLER.getTemplate("Accounts/account-types.json"), Charset.forName("unicode")));
                }
                if (!taxItm.exists()) {
                    FILE_HANDLER.write(taxItm, new String(FILE_HANDLER.getTemplate("Accounts/tax-items.json"), Charset.forName("unicode")));
                }
            }
            LOG_HANDLER.trace(this.getClass(), "Defaults set where necessary");
        }
        //LCurrency
        {
            CURRENCIES.clear();
            JsonArray array = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(currencies));
            JsonArray spec = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(special));
            for (JsonObject obj : array.getObjectArray()) {
                LCurrency cur = new LCurrency(obj, this);
                for (JsonObject obk : spec.getObjectArray()) {
                    if (obk.getString("asset").getString().equals(cur.toString())) {
                        cur.setAltApi(obk.getString("api").getString());
                    }
                }
                CURRENCIES.add(cur);
            }
            main = getLCurrency(mainTicker);
            main__ = getLCurrency(main__Ticker);
            if (main__ == null) {
                LCurrency __ = new LCurrency(main, main.getTicker() + " Extra", 2);
                main__ = __;
                CURRENCIES.add(__);
            }
            CURRENCIES.changed = false;
            LOG_HANDLER.trace(this.getClass(), "Currencies loaded");
            JsonArray tickers = (JsonArray) FILE_HANDLER.hit("https://api.coingecko.com/api/v3/simple/supported_vs_currencies");
            for (JsonString s : tickers.getStringArray()) {
                LCurrency cur = getLCurrency(s.getString());
                if (cur != null) {
                    VS.add(cur);
                }
            }
        }
        //LStock
        {
            STOCKS.clear();
            JsonArray array = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(stocks));
            for (JsonObject obj : array.getObjectArray()) {
                STOCKS.add(new LStock(obj, this));
            }
            STOCKS.changed = false;
            LOG_HANDLER.trace(this.getClass(), "Stocks loaded");
        }
        //Inventory
        {
            INVENTORIES.clear();
            JsonArray array = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(inventories));
            for (JsonObject obj : array.getObjectArray()) {
                INVENTORIES.add(new LInventory(obj, this));
            }
            INVENTORIES.changed = false;
            LOG_HANDLER.trace(this.getClass(), "Inventories loaded");
        }
        //Account Types
        {
            ACCOUNT_TYPES.clear();
            JsonArray array = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(accTyp));
            for (JsonObject obj : array.getObjectArray()) {
                ACCOUNT_TYPES.add(new AccountType(obj));
            }
            ACCOUNT_TYPES.changed = false;
            LOG_HANDLER.trace(this.getClass(), "Account Types loaded");
        }
        //Exchanges
        {
            EXCHANGES.clear();
            EXCHANGES.add(new Exchange("Personal", "", this, false));
            EXCHANGES.add(new Exchange("Cash", "", this, false));
            JsonArray array = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(exchanges));
            for (JsonObject obj : array.getObjectArray()) {
                EXCHANGES.add(new Exchange(obj, this, true));
            }
            EXCHANGES.changed = false;
            LOG_HANDLER.trace(this.getClass(), "Exchanges loaded");
        }
        //Accounts
        {
            ACCOUNTS.clear();
            JsonObject accountObj = (JsonObject) JsonItem.sanitizeDigest(FILE_HANDLER.read(accounts));
            JsonArray array = accountObj.getArray("accounts");
            for (JsonObject obj : array.getObjectArray()) {
                ACCOUNTS.add(new Account(obj, this));
            }
            int x = accountObj.getDecimal("tracking-prefix").decimal.intValue(), y = accountObj.getDecimal("asset-prefix").decimal.intValue();
            for (Account a : ACCOUNTS) {
                if (a.getBroadAccountType() == BroadAccountType.ASSET && a.getAid() > y) {
                    y = a.getAid();
                }
                if (a.getBroadAccountType() == BroadAccountType.TRACKING && a.getAid() > x) {
                    x = a.getAid();
                }
            }
            x++;
            y++;
            JsonObject extranious = (JsonObject) JsonItem.sanitizeDigest(FILE_HANDLER.read(extran));
            if (extranious.FIELDS.containsKey("gift-cards")) {
                for (JsonString item : extranious.getArray("gift-cards").getStringArray()) {
                    ACCOUNTS.add(new Account(item.getString() + "_GC", y, main,
                            ACCOUNT_TYPES.getElement("Gift_Card"), this, false));
                    y++;
                }
            }
            for (Exchange e : EXCHANGES) {
                for (LStock s : STOCKS) {
                    if (e.supports(s)) {
                        ACCOUNTS.add(new Account(e.NAME + "_" + s.getTicker(), x, s,
                                ACCOUNT_TYPES.getElement("Tracking"), this, false));
                        x++;
                    }
                }
                for (LCurrency j : CURRENCIES.getBaseline()) {
                    if (e.supports(j)) {
                        if (!e.NAME.equals("Personal") && !e.NAME.equals("Cash")) {
                            if (j.equals(main)) {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), y, j,
                                        ACCOUNT_TYPES.getElement("Portfolio_Cash"), this, false));
                                y++;
                            } else {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), x, j,
                                        ACCOUNT_TYPES.getElement("Tracking"), this, false));
                                x++;
                            }
                        } else {
                            if (!j.equals(getLCurrency("main"))) {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), x, j,
                                        ACCOUNT_TYPES.getElement("Tracking"), this, false));
                                x++;
                            } else {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), y, main,
                                        ACCOUNT_TYPES.getElement("Portfolio_Cash"), this, false));
                                y++;
                            }
                        }
                        int places = e.stakes(j);
                        if (places != -1) {
                            ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker() + "_S",
                                    x, new LCurrency(j, j.getName(), places - j.getPlaces()),
                                    ACCOUNT_TYPES.getElement("Tracking"), this, false));
                            x++;
                        }
                    }
                }
                for (LInventory i : INVENTORIES) {
                    if (e.supports(i)) {
                        ACCOUNTS.add(new Account(
                                e.NAME + "_" + i.getTicker().replace(" ", "_"),
                                x, i, ACCOUNT_TYPES.getElement("Tracking"), this, false));
                        x++;
                    }
                }
            }
            if (extranious.FIELDS.containsKey("brave-mobile")) {
                for (JsonString item : extranious.getArray("brave-mobile").getStringArray()) {
                    ACCOUNTS.add(new Account(item.getString() + "_BAT", x, getLCurrency("C!BAT"),
                            ACCOUNT_TYPES.getElement("Tracking"), this, false));
                    x++;
                }
            }
            ACCOUNTS.sort();
            ACCOUNTS.changed = false;
            LOG_HANDLER.trace(this.getClass(), "Accounts loaded");
        }
        //Tax
        {
            TAX_ITEMS.clear();
            JsonArray taxArray = (JsonArray) JsonItem.sanitizeDigest(FILE_HANDLER.read(taxItm));
            TAX_ITEMS.load(taxArray);
        }
    }

    public void conclude(boolean save) {
        if (save) {
            DATA_HANDLER.save();
            File currencies = new File(data.getPath() + File.separator + "Currencies" + File.separator + "currencies.json"),
                    stocks = new File(data.getPath() + File.separator + "Currencies" + File.separator + "stocks.json"),
                    inventories = new File(data.getPath() + File.separator + "Currencies" + File.separator + "inventories.json"),
                    exchanges = new File(data.getPath() + File.separator + "Accounts" + File.separator + "exchanges.json"),
                    accounts = new File(data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json"),
                    accTyp = new File(data.getPath() + File.separator + "Accounts" + File.separator + "account-types.json"),
                    taxItm = new File(data.getPath() + File.separator + "Accounts" + File.separator + "tax-items.json");
            if (CURRENCIES.changed) {
                FILE_HANDLER.write(currencies, CURRENCIES.export().toString());
            }
            if (STOCKS.changed) {
                FILE_HANDLER.write(stocks, STOCKS.export().toString());
            }
            if (INVENTORIES.changed) {
                FILE_HANDLER.write(inventories, INVENTORIES.export().toString());
            }
            if (ACCOUNTS.changed) {
                FILE_HANDLER.write(accounts, ACCOUNTS.export().toString());
            }
            if (EXCHANGES.changed) {
                FILE_HANDLER.write(exchanges, EXCHANGES.export().toString());
            }
            if (ACCOUNT_TYPES.changed) {
                FILE_HANDLER.write(accTyp, ACCOUNT_TYPES.export().toString());
            }
            if (TAX_ITEMS.changed) {
                FILE_HANDLER.write(taxItm, TAX_ITEMS.export().toString());
            }
        }
        ENCRYPTION_HANDLER.dispose();
        if (export) {
            EXPORT_HANDLER.export();
        }
        if (log) {
            LOG_HANDLER.save();
        }
    }

    public String p(BigDecimal d) {
        return (new DecimalFormat("#,##0.00%")).format(d);
    }

    public String $(BigDecimal d) {
        return main.encode(d);
    }

    public String $$(BigDecimal d) {
        return main__.encode(d);
    }

    public BigDecimal convert(LCurrency a, BigDecimal amnt, LCurrency b) {
        if (a.getTicker().equalsIgnoreCase(b.getTicker()) && a.getName().equalsIgnoreCase(b.getName())) {
            return amnt;
        } else {
            if (a.isFiat() && b.isFiat()) {
                if (root.contains(a.getTicker())) {
                    return amnt.multiply(FILE_HANDLER.hitPolygonForex(a.getTicker(), b.getTicker()));
                } else if (root.contains(b.getTicker())) {
                    return amnt.multiply(BigDecimal.ONE.divide(FILE_HANDLER.hitPolygonForex(b.getTicker(), a.getTicker()), PRECISION));
                }
            }
            boolean vs = false, f = true;
            if (!a.isFiat()) {
                for (LCurrency cur : VS) {
                    if (b.equals(cur)) {
                        vs = true;
                        break;
                    }
                }
            }
            if (!vs) {
                for (LCurrency cur : VS) {
                    if (cur.equals(a)) {
                        vs = true;
                        f = false;
                        break;
                    }
                }
            }
            if (vs) {
                String name;
                if (f) {
                    if (a.getAltName().equals("")) {
                        name = a.getName().toLowerCase().replace(" ", "-");
                    } else {
                        name = a.getAltName();
                    }
                    return FILE_HANDLER.hitCoinGecko(name, b.getTicker()).multiply(amnt);
                } else {
                    if (b.getAltName().equals("")) {
                        name = b.getName().toLowerCase().replace(" ", "-");
                    } else {
                        name = b.getAltName();
                    }
                    return amnt.divide(FILE_HANDLER.hitCoinGecko(name, a.getTicker().toLowerCase()), PRECISION);
                }
            } else {
                return b.reverseTotal(a.getTotal(amnt));
            }
        }
    }

    public LCurrency getLCurrency(String name) {
        if (name.equalsIgnoreCase("Main")) {
            return main;
        } else if (name.equalsIgnoreCase("Main_")) {
            return main__;
        } else {
            if (name.contains("!")) {
                if (name.contains("S!")) {
                    return STOCKS.getElement(name);
                } else if (name.contains("I!")) {
                    return INVENTORIES.getElement(name);
                } else {
                    return CURRENCIES.getElement(name);
                }
            }
            if (name.contains(" a")) {
                name = name.replace(" a", "") + " Max";
            }
            LCurrency a = CURRENCIES.getElement(name);
            LCurrency b = STOCKS.getElement(name);
            LCurrency c = INVENTORIES.getElement(name);
            if (a != null) {
                return a;
            }
            if (b != null) {
                return b;
            }
            return c;
        }
    }

    public void installPeriod(JComboBox<String> box) {
        box.removeAllItems();
        box.addItem("Year");
        box.addItem("S1");
        box.addItem("S2");
        box.addItem("Q1");
        box.addItem("Q2");
        box.addItem("Q3");
        box.addItem("Q4");
        box.addItem("January");
        box.addItem("February");
        box.addItem("March");
        box.addItem("April");
        box.addItem("May");
        box.addItem("June");
        box.addItem("July");
        box.addItem("August");
        box.addItem("September");
        box.addItem("October");
        box.addItem("November");
        box.addItem("December");
    }

    public BigDecimal cleanNumber(String raw) {
        boolean flag = raw.contains("%");
        raw = raw.replace("(", "-");
        String allowed = "0123456789.-";
        StringBuilder out = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (allowed.contains("" + c)) {
                out.append(c);
            }
        }
        if (out.length() == 0) {
            return BigDecimal.ZERO;
        } else {
            if (flag) {
                return new BigDecimal(out.toString()).divide(BigDecimal.TEN.pow(2), PRECISION);
            } else {
                return new BigDecimal(out.toString());
            }
        }
    }

    public ArrayList<String> getAllTaxItemsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        TAX_ITEMS.forEach(item -> out.add(item.NAME));
        return out;
    }

    public ArrayList<String> getAllAssetsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        CURRENCIES.forEach(c -> out.add(c.toString()));
        STOCKS.forEach((s -> out.add(s.toString())));
        INVENTORIES.forEach(i -> out.add(i.toString()));
        return out;
    }

    public ArrayList<String> getAllAssetsAsStrings(Exchange e) {
        if (e == null) {
            return getAllUniqueAssetsAsStrings();
        } else {
            ArrayList<String> out = new ArrayList<>();
            CURRENCIES.getBaseline().forEach(c -> {
                if (e.supports(c) && c != main) {
                    out.add(c.toString());
                }
            });
            STOCKS.forEach((s -> {
                if (e.supports(s)) {
                    out.add(s.toString());
                }
            }));
            INVENTORIES.forEach((i -> {
                if (e.supports(i)) {
                    out.add(i.toString());
                }
            }));
            return out;
        }
    }

    public ArrayList<String> getAllAssetsAsStrings(SearchBox e) {
        return getAllAssetsAsStrings(EXCHANGES.getElement(e.getSelectedItem()));
    }

    public ArrayList<String> getAllAssetsAsStrings(Exchange e1, Exchange e2) {
        if (e1 == null || e2 == null) {
            if (e2 == null) {
                return getAllAssetsAsStrings(e1);
            } else {
                return getAllAssetsAsStrings(e2);
            }
        } else {
            ArrayList<String> out = new ArrayList<>();
            CURRENCIES.getBaseline().forEach(c -> {
                if (e1.supports(c) && e2.supports(c) && c != main) {
                    out.add(c.toString());
                }
            });
            STOCKS.forEach((s -> {
                if (e1.supports(s) && e2.supports(s)) {
                    out.add(s.toString());
                }
            }));
            INVENTORIES.forEach((i -> {
                if (e1.supports(i) && e2.supports(i)) {
                    out.add(i.toString());
                }
            }));
            return out;
        }
    }

    public ArrayList<String> getAllAssetsAsStrings(SearchBox e1, SearchBox e2) {
        return getAllAssetsAsStrings(
                EXCHANGES.getElement(e1.getSelectedItem()),
                EXCHANGES.getElement(e2.getSelectedItem())
        );
    }

    public ArrayList<String> getAllTokensAsStrings(Exchange e1, Exchange e2) {
        ArrayList<String> out = new ArrayList<>();
        if (e1 == null || e2 == null) {
            CURRENCIES.getBaseline().forEach(c -> {
                if (c.isToken()) {
                    out.add(c.toString());
                }
            });
        } else {
            CURRENCIES.getBaseline().forEach(c -> {
                if (e1.supports(c) && e2.supports(c) && c.isToken()) {
                    out.add(c.toString());
                }
            });
        }
        return out;
    }

    public ArrayList<String> getAllTokensAsStrings(SearchBox e1, SearchBox e2) {
        return getAllTokensAsStrings(
                EXCHANGES.getElement(e1.getSelectedItem()),
                EXCHANGES.getElement(e2.getSelectedItem())
        );
    }

    public ArrayList<String> getAllUniqueAssetsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        CURRENCIES.forEach(c -> {
            if (c.first() && c != main) {
                out.add(c.toString());
            }
        });
        STOCKS.forEach(s -> {
            if (s.first()) {
                out.add(s.toString());
            }
        });
        INVENTORIES.forEach(i -> {
            if (i.first()) {
                out.add(i.toString());
            }
        });
        return out;
    }

    public ArrayList<String> getAccountTypesAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        ACCOUNT_TYPES.forEach(at -> out.add(at.NAME));
        return out;
    }

    public ArrayList<String> getExchangesAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        EXCHANGES.forEach(e -> out.add(e.NAME));
        return out;
    }

    public ArrayList<String> getAccountsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        ACCOUNTS.forEach(a -> out.add(a.getName()));
        return out;
    }

    public ArrayList<String> getAccountsInUseAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        ACCOUNTS.forEach(a -> {
            if (a.inUse()) {
                out.add(a.getName());
            }
        });
        return out;
    }

    public ArrayList<String> getDCAccountsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        ACCOUNTS.forEach(a -> {
            if (a.getBroadAccountType() != BroadAccountType.GHOST && a.getBroadAccountType() != BroadAccountType.TRACKING) {
                out.add(a.getName());
            }
        });
        return out;
    }

    public ArrayList<String> getTaxAccountsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        ACCOUNTS.forEach(a -> {
            if (a.getBroadAccountType() == BroadAccountType.GHOST) {
                out.add(a.getName());
            }
        });
        return out;
    }

    public ArrayList<String> getTrackingAccountsAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        ACCOUNTS.forEach(a -> {
            if (a.getBroadAccountType() == BroadAccountType.TRACKING) {
                out.add(a.getName());
            }
        });
        return out;
    }
}
