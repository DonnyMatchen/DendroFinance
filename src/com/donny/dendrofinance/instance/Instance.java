package com.donny.dendrofinance.instance;

import com.donny.dendrofinance.DendroFinance;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LMarketApi;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.data.DataHandler;
import com.donny.dendrofinance.data.LogHandler;
import com.donny.dendrofinance.data.UuidHandler;
import com.donny.dendrofinance.data.backingtable.*;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.fileio.ExportHandler;
import com.donny.dendrofinance.fileio.FileHandler;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.fileio.encryption.EncryptionHandler;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.SearchBox;
import com.donny.dendrofinance.gui.password.PasswordGui;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Instance {
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    //Major managing objects and handling lists
    public final String IID;
    public final String[] ARGS;
    public final LogHandler LOG_HANDLER;
    public final EncryptionHandler ENCRYPTION_HANDLER;
    public final FileHandler FILE_HANDLER;
    public final UuidHandler UUID_HANDLER;
    public final CurrencyBTC CURRENCIES;
    public final StockBTC STOCKS;
    public final InventoryBTC INVENTORIES;
    public final AccountBTC ACCOUNTS;
    public final ExchangeBTC EXCHANGES;
    public final AccountTypeBTC ACCOUNT_TYPES;
    public final TaxItemBTC TAX_ITEMS;
    public final MarketApiBTC MARKET_APIS;
    public final ImportHandler IMPORT_HANDLER;
    public final ExportHandler EXPORT_HANDLER;
    public final DataHandler DATA_HANDLER;
    public File data = new File(System.getProperty("user.dir") + File.separator + "data");

    //flags, api keys, and other minor alterable things
    public MathContext precision;
    public boolean log, american, day, large;
    public String mainTicker, main__Ticker;
    public LogHandler.LogLevel logLevel;
    public LCurrency main;
    public LCurrency main__;
    public int blockSize;

    public Instance(String iid, String[] args) {
        IID = iid;
        ARGS = args;
        CURRENCIES = new CurrencyBTC(this);
        STOCKS = new StockBTC(this);
        INVENTORIES = new InventoryBTC(this);
        ACCOUNTS = new AccountBTC(this);
        EXCHANGES = new ExchangeBTC(this);
        ACCOUNT_TYPES = new AccountTypeBTC(this);
        TAX_ITEMS = new TaxItemBTC(this);
        MARKET_APIS = new MarketApiBTC(this);
        LOG_HANDLER = new LogHandler(this);
        FILE_HANDLER = new FileHandler(this);
        DendroFactory.init(this);
        ENCRYPTION_HANDLER = new EncryptionHandler(this);
        PasswordGui password = new PasswordGui(this);
        UUID_HANDLER = new UuidHandler(this);
        password.setVisible(true);
        while (!password.done) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                LOG_HANDLER.warn(getClass(), "The timer was interrupted.  This could cause damage.  Check integrity of data before saving.");
            }
        }

        //data handler
        DATA_HANDLER = new DataHandler(this);

        //ensure folders and data files are set up
        ensureFolders();
        ensureBackingFiles();
        reloadBackingElements();
        checkCurToMain();

        //Data
        reloadEntries();
        IMPORT_HANDLER = new ImportHandler(this);
        EXPORT_HANDLER = new ExportHandler(this);
        new MainGui(this).setVisible(true);
        password.dispose();
    }

    public Instance(String[] args) {
        this(DendroFinance.newIid(), args);
    }

    public void ensureFolders() {
        File archive = new File(data.getPath() + File.separator + "Archives"),
                pStock = new File(data.getPath() + File.separator + "P_Stock"),
                pInv = new File(data.getPath() + File.separator + "P_Inventory"),
                exp = new File(data.getPath() + File.separator + "Exports"),
                imp = new File(data.getPath() + File.separator + "Imports");
        while (!archive.exists() || !pStock.exists() || !pInv.exists() || !exp.exists() || !imp.exists()) {
            if (!data.exists()) {
                data.mkdir();
            }
            if (!archive.exists()) {
                archive.mkdir();
            }
            if (!pStock.exists()) {
                pStock.mkdir();
            }
            if (!pInv.exists()) {
                pInv.mkdir();
            }
            if (!exp.exists()) {
                exp.mkdir();
            }
            if (!imp.exists()) {
                imp.mkdir();
            }
        }
    }

    public void ensureBackingFiles() {
        File currencies = new File(data.getPath() + File.separator + "Currencies" + File.separator + "currencies.json"),
                stocks = new File(data.getPath() + File.separator + "Currencies" + File.separator + "stocks.json"),
                inventories = new File(data.getPath() + File.separator + "Currencies" + File.separator + "inventories.json"),
                exchanges = new File(data.getPath() + File.separator + "Accounts" + File.separator + "exchanges.json"),
                accounts = new File(data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json"),
                accTyp = new File(data.getPath() + File.separator + "Accounts" + File.separator + "account-types.json"),
                taxItm = new File(data.getPath() + File.separator + "Accounts" + File.separator + "tax-items.json"),
                marApi = new File(data.getPath() + File.separator + "Currencies" + File.separator + "market-apis.json"),
                spec = new File(data.getPath() + File.separator + "Accounts" + File.separator + "special.json"),
                budg = new File(data.getPath() + File.separator + "Accounts" + File.separator + "budget.json");
        //Hang to prevent File IO problems
        boolean loaded = false;
        while (!loaded) {
            loaded = currencies.exists() && stocks.exists() && inventories.exists() && exchanges.exists() &&
                    accounts.exists() && accTyp.exists() && taxItm.exists() && marApi.exists();
            //Currencies
            {
                if (!currencies.exists()) {
                    FILE_HANDLER.writeJson(currencies, FILE_HANDLER.getTemplate("Currencies/currencies.json"));
                }
                if (!stocks.exists()) {
                    FILE_HANDLER.writeJson(stocks, FILE_HANDLER.getTemplate("Currencies/stocks.json"));
                }
                if (!inventories.exists()) {
                    FILE_HANDLER.writeJson(inventories, FILE_HANDLER.getTemplate("Currencies/inventories.json"));
                }
                if (!marApi.exists()) {
                    FILE_HANDLER.writeJson(marApi, FILE_HANDLER.getTemplate("Currencies/market-apis.json"));
                }
            }
            //Accounts
            {
                if (!exchanges.exists()) {
                    FILE_HANDLER.writeJson(exchanges, FILE_HANDLER.getTemplate("Accounts/exchanges.json"));
                }
                if (!accounts.exists()) {
                    FILE_HANDLER.writeJson(accounts, FILE_HANDLER.getTemplate("Accounts/accounts.json"));
                }
                if (!accTyp.exists()) {
                    FILE_HANDLER.writeJson(accTyp, FILE_HANDLER.getTemplate("Accounts/account-types.json"));
                }
                if (!taxItm.exists()) {
                    FILE_HANDLER.writeJson(taxItm, FILE_HANDLER.getTemplate("Accounts/tax-items.json"));
                }
                if (!spec.exists()) {
                    FILE_HANDLER.writeJson(spec, FILE_HANDLER.getTemplate("Accounts/special.json"));
                }
                if (!budg.exists()) {
                    FILE_HANDLER.writeJson(budg, FILE_HANDLER.getTemplate("Accounts/budget.json"));
                }
            }
        }
        LOG_HANDLER.trace(getClass(), "Defaults set where necessary");
    }

    public void reloadBackingElements() {
        //establishing data files
        File currencies = new File(data.getPath() + File.separator + "Currencies" + File.separator + "currencies.json"),
                stocks = new File(data.getPath() + File.separator + "Currencies" + File.separator + "stocks.json"),
                inventories = new File(data.getPath() + File.separator + "Currencies" + File.separator + "inventories.json"),
                exchanges = new File(data.getPath() + File.separator + "Accounts" + File.separator + "exchanges.json"),
                accounts = new File(data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json"),
                accTyp = new File(data.getPath() + File.separator + "Accounts" + File.separator + "account-types.json"),
                taxItm = new File(data.getPath() + File.separator + "Accounts" + File.separator + "tax-items.json"),
                marApi = new File(data.getPath() + File.separator + "Currencies" + File.separator + "market-apis.json"),
                spec = new File(data.getPath() + File.separator + "Accounts" + File.separator + "special.json"),
                budg = new File(data.getPath() + File.separator + "Accounts" + File.separator + "budget.json");
        //LCurrency
        {
            CURRENCIES.clear();
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(currencies);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "currencies.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            CURRENCIES.load(array);
            main = getLCurrency(mainTicker);
            main__ = getLCurrency(main__Ticker);
            if (main__ == null) {
                LCurrency __ = new LCurrency(main, main.getTicker() + " Extra", 2);
                main__ = __;
                CURRENCIES.add(__);
            }
            CURRENCIES.changed = false;
            LOG_HANDLER.trace(getClass(), "Currencies loaded");
        }
        //LStock
        {
            STOCKS.clear();
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(stocks);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "stocks.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            STOCKS.load(array);
            STOCKS.changed = false;
            LOG_HANDLER.trace(getClass(), "Stocks loaded");
        }
        //Inventory
        {
            INVENTORIES.clear();
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(inventories);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "inventories.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            INVENTORIES.load(array);
            INVENTORIES.changed = false;
            LOG_HANDLER.trace(getClass(), "Inventories loaded");
        }
        //Market APIs
        {
            MARKET_APIS.clear();
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(marApi);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "market-apis.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            MARKET_APIS.load(array);
            MARKET_APIS.changed = false;
            LOG_HANDLER.trace(getClass(), "Market APIs loaded");
        }
        //Account Types
        {
            ACCOUNT_TYPES.clear();
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(accTyp);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "account-types.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            ACCOUNT_TYPES.load(array);
            ACCOUNT_TYPES.changed = false;
            LOG_HANDLER.trace(getClass(), "Account Types loaded");
        }
        //Exchanges
        {
            EXCHANGES.clear();
            EXCHANGES.add(new Exchange("Personal", "", this, false));
            EXCHANGES.add(new Exchange("Cash", "", this, false));
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(exchanges);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "exchanges.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            for (JsonObject obj : array.getObjectArray()) {
                EXCHANGES.add(new Exchange(obj, this, true));
            }
            EXCHANGES.changed = false;
            LOG_HANDLER.trace(getClass(), "Exchanges loaded");
        }
        //Accounts
        {
            ACCOUNTS.clear();
            JsonArray budgets = (JsonArray) FILE_HANDLER.readJson(budg);
            if (budgets == null) {
                LOG_HANDLER.fatal(getClass(), "budget.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            for (JsonString string : budgets.getStringArray()) {
                DATA_HANDLER.addBudgetType(string.getString());
            }
            JsonObject accountObj = (JsonObject) FILE_HANDLER.readJson(accounts);
            if (accountObj == null) {
                LOG_HANDLER.fatal(getClass(), "accounts.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            JsonArray array = accountObj.getArray("accounts");
            for (JsonObject obj : array.getObjectArray()) {
                ACCOUNTS.add(new Account(obj, this));
            }

            //adding special account identifiers
            {
                JsonObject notAccObj = (JsonObject) FILE_HANDLER.readJson(spec);
                if (notAccObj == null) {
                    LOG_HANDLER.fatal(getClass(), "special.json couldn't be loaded");
                    LOG_HANDLER.save();
                    System.exit(1);
                }

                Account.portfolioName = notAccObj.getString("portfolio").getString();

                Account.stockName = notAccObj.getString("stock").getString();
                Account.inventoryName = notAccObj.getString("inventory").getString();
                Account.fiatName = notAccObj.getString("fiat").getString();
                Account.cryptoName = notAccObj.getString("crypto").getString();

                Account.tradIncName = notAccObj.getString("trading-income").getString();
                Account.tradExpName = notAccObj.getString("trading-expense").getString();

                Account.cgStockName = notAccObj.getString("stock-gain").getString();
                Account.cgInventoryName = notAccObj.getString("inventory-gain").getString();
                Account.cgFiatName = notAccObj.getString("fiat-gain").getString();
                Account.cgCryptoName = notAccObj.getString("crypto-gain").getString();

                Account.clStockName = notAccObj.getString("stock-loss").getString();
                Account.clInventoryName = notAccObj.getString("inventory-loss").getString();
                Account.clFiatName = notAccObj.getString("fiat-loss").getString();
                Account.clCryptoName = notAccObj.getString("crypto-loss").getString();

                if (notAccObj.containsKey("stock-gain-long-run")) {
                    Account.cgltStockName = notAccObj.getString("stock-gain-long-run").getString();
                } else {
                    Account.cgltStockName = Account.cgStockName;
                }
                if (notAccObj.containsKey("inventory-gain-long-run")) {
                    Account.cgltInventoryName = notAccObj.getString("inventory-gain-long-run").getString();
                } else {
                    Account.cgltInventoryName = Account.cgInventoryName;
                }
                if (notAccObj.containsKey("fiat-gain-long-run")) {
                    Account.cgltFiatName = notAccObj.getString("fiat-gain-long-run").getString();
                } else {
                    Account.cgltFiatName = Account.cgFiatName;
                }
                if (notAccObj.containsKey("crypto-gain-long-run")) {
                    Account.cgltCryptoName = notAccObj.getString("crypto-gain-long-run").getString();
                } else {
                    Account.cgltCryptoName = Account.cgCryptoName;
                }
                if (notAccObj.containsKey("stock-loss-long-run")) {
                    Account.clltStockName = notAccObj.getString("stock-loss-long-run").getString();
                } else {
                    Account.clltStockName = Account.clStockName;
                }
                if (notAccObj.containsKey("inventory-loss-long-run")) {
                    Account.clltInventoryName = notAccObj.getString("inventory-loss-long-run").getString();
                } else {
                    Account.clltInventoryName = Account.clInventoryName;
                }
                if (notAccObj.containsKey("fiat-loss-long-run")) {
                    Account.clltFiatName = notAccObj.getString("fiat-loss-long-run").getString();
                } else {
                    Account.clltFiatName = Account.clFiatName;
                }
                if (notAccObj.containsKey("crypto-loss-long-run")) {
                    Account.clltCryptoName = notAccObj.getString("crypto-loss-long-run").getString();
                } else {
                    Account.clltCryptoName = Account.clCryptoName;
                }

                Account.selfIncName = notAccObj.getString("self-employment-income").getString();

                Account.fixedAssetsTypeName = notAccObj.getString("fixed-assets-type").getString();
                Account.receiveTypeName = notAccObj.getString("receivables-type").getString();

                Account.appreciationName = notAccObj.getString("appreciation").getString();
                Account.depreciationName = notAccObj.getString("depreciation").getString();
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
            if (accountObj.containsKey("gift-cards")) {
                for (JsonString card : accountObj.getArray("gift-cards").getStringArray()) {
                    ACCOUNTS.add(new Account(card.getString() + "_GC", y, main,
                            ACCOUNT_TYPES.getElement("Gift_Card"), null, this, false));
                    y++;
                }
            }
            for (Exchange e : EXCHANGES) {
                for (LStock s : STOCKS) {
                    if (e.supports(s)) {
                        ACCOUNTS.add(new Account(e.NAME + "_" + s.getTicker(), x, s,
                                ACCOUNT_TYPES.getElement("Tracking"), e, this, false));
                        x++;
                    }
                }
                for (LCurrency j : CURRENCIES.getBaseline()) {
                    if (e.supports(j)) {
                        if (!e.NAME.equals("Personal") && !e.NAME.equals("Cash")) {
                            if (j.equals(main)) {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), y, j,
                                        ACCOUNT_TYPES.getElement("Portfolio_Cash"), e, this, false));
                                y++;
                            } else {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), x, j,
                                        ACCOUNT_TYPES.getElement("Tracking"), e, this, false));
                                x++;
                            }
                        } else {
                            if (!j.equals(getLCurrency("main"))) {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), x, j,
                                        ACCOUNT_TYPES.getElement("Tracking"), e, this, false));
                                x++;
                            } else {
                                ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker(), y, main,
                                        ACCOUNT_TYPES.getElement("Portfolio_Cash"), e, this, false));
                                y++;
                            }
                        }
                        int places = e.stakes(j);
                        if (places != -1) {
                            ACCOUNTS.add(new Account(e.NAME + "_" + j.getTicker() + "_S",
                                    x, new LCurrency(j, j.getName(), places - j.getPlaces()),
                                    ACCOUNT_TYPES.getElement("Tracking"), e, this, false));
                            x++;
                        }
                    }
                }
                for (LInventory i : INVENTORIES) {
                    if (e.supports(i)) {
                        ACCOUNTS.add(new Account(
                                e.NAME + "_" + i.getTicker().replace(" ", "_"),
                                x, i, ACCOUNT_TYPES.getElement("Tracking"), e, this, false));
                        x++;
                    }
                }
            }
            if (accountObj.containsKey("brave-mobile")) {
                for (JsonString device : accountObj.getArray("brave-mobile").getStringArray()) {
                    ACCOUNTS.add(new Account(device.getString() + "_BAT", x, getLCurrency("C!BAT"),
                            ACCOUNT_TYPES.getElement("Tracking"), null, this, false));
                    x++;
                }
            }
            ACCOUNTS.sort();
            ACCOUNTS.changed = false;
            LOG_HANDLER.trace(getClass(), "Accounts loaded");
        }
        //Tax
        {
            TAX_ITEMS.clear();
            JsonArray array = (JsonArray) FILE_HANDLER.readJson(taxItm);
            if (array == null) {
                LOG_HANDLER.fatal(getClass(), "tax-items.json couldn't be loaded");
                LOG_HANDLER.save();
                System.exit(1);
            }
            TAX_ITEMS.load(array);
            TAX_ITEMS.changed = false;
            LOG_HANDLER.trace(getClass(), "Tax Items loaded");
        }
    }

    public void reloadEntries() {
        DATA_HANDLER.reload();
        for (TransactionEntry entry : DATA_HANDLER.readTransactions()) {
            if (!entry.isBalanced()) {
                LOG_HANDLER.error(getClass(), "Unbalanced entry: " + Long.toUnsignedString(entry.getUUID()));
            }
        }
        DATA_HANDLER.checkLedgers();
        DATA_HANDLER.checkCG();
    }

    public void checkCurToMain() {
        for (LCurrency currency : CURRENCIES) {
            boolean flag = true;
            for (LMarketApi marketApi : MARKET_APIS) {
                if (marketApi.canConvert(currency, main)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                LOG_HANDLER.error(getClass(), "Currency can't be converted to main: " + currency);
            }
        }
        for (LStock stock : STOCKS) {
            boolean flag = true;
            for (LMarketApi marketApi : MARKET_APIS) {
                if (marketApi.canConvert(stock, main)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                LOG_HANDLER.error(getClass(), "Stock can't be converted to main: " + stock);
            }
        }
        for (LInventory inventory : INVENTORIES) {
            if (inventory.isCommodity()) {
                boolean flag = true;
                for (LMarketApi marketApi : MARKET_APIS) {
                    if (marketApi.canConvert(inventory, main)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    LOG_HANDLER.error(getClass(), "Commodity can't be converted to main: " + inventory);
                }
            }
        }
    }

    public void save() {
        DATA_HANDLER.save();
        File currencies = new File(data.getPath() + File.separator + "Currencies" + File.separator + "currencies.json"),
                stocks = new File(data.getPath() + File.separator + "Currencies" + File.separator + "stocks.json"),
                inventories = new File(data.getPath() + File.separator + "Currencies" + File.separator + "inventories.json"),
                exchanges = new File(data.getPath() + File.separator + "Accounts" + File.separator + "exchanges.json"),
                accounts = new File(data.getPath() + File.separator + "Accounts" + File.separator + "accounts.json"),
                accTyp = new File(data.getPath() + File.separator + "Accounts" + File.separator + "account-types.json"),
                taxItm = new File(data.getPath() + File.separator + "Accounts" + File.separator + "tax-items.json"),
                spec = new File(data.getPath() + File.separator + "Accounts" + File.separator + "special.json"),
                budg = new File(data.getPath() + File.separator + "Accounts" + File.separator + "budget.json");
        if (CURRENCIES.changed) {
            FILE_HANDLER.write(currencies, CURRENCIES.export().print());
        }
        if (STOCKS.changed) {
            FILE_HANDLER.write(stocks, STOCKS.export().print());
        }
        if (INVENTORIES.changed) {
            FILE_HANDLER.write(inventories, INVENTORIES.export().print());
        }
        if (ACCOUNTS.changed) {
            FILE_HANDLER.write(accounts, ACCOUNTS.export().print());
        }
        if (EXCHANGES.changed) {
            FILE_HANDLER.write(exchanges, EXCHANGES.export().print());
        }
        if (ACCOUNT_TYPES.changed) {
            FILE_HANDLER.write(accTyp, ACCOUNT_TYPES.export().print());
        }
        if (TAX_ITEMS.changed) {
            FILE_HANDLER.write(taxItm, TAX_ITEMS.export().print());
        }
        if (Account.specialAltered) {
            try {
                FILE_HANDLER.write(spec, Account.specialExport().print());
            } catch (JsonFormattingException ex) {
                LOG_HANDLER.error(getClass(), "Malformed Special Account Name.  " + ex.getMessage());
            }
        }
        if (DATA_HANDLER.budgetTypesChanged) {
            try {
                JsonArray array = new JsonArray();
                for (String budget : DATA_HANDLER.getBudgetTypes()) {
                    array.add(new JsonString(budget));
                }
                FILE_HANDLER.write(budg, array.print());
            } catch (JsonFormattingException ex) {
                LOG_HANDLER.error(getClass(), ex.getMessage());
            }
        }
    }

    public void conclude(boolean save) {
        if (save) {
            save();
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

    public BigDecimal convert(BigDecimal amount, LCurrency a, LCurrency b) {
        LStock aStock = null, bStock = null;
        LInventory aInv = null, bInv = null;
        boolean aPrivate = false, bPrivate = false;
        if (a instanceof LStock) {
            aStock = (LStock) a;
        }
        if (b instanceof LStock) {
            bStock = (LStock) b;
        }
        if (a instanceof LInventory) {
            aInv = (LInventory) a;
        }
        if (b instanceof LInventory) {
            bInv = (LInventory) b;
        }
        if ((aStock != null && !aStock.isPublic()) || (aInv != null && !aInv.isPublic())) {
            aPrivate = true;
        }
        if ((bStock != null && !bStock.isPublic()) || (bInv != null && !bInv.isPublic())) {
            bPrivate = true;
        }
        if (a.getTicker().equalsIgnoreCase(b.getTicker()) && a.getClass() == b.getClass() && a.isFiat() == b.isFiat()) {
            return amount.multiply(b.getFactor().divide(a.getFactor(), precision));
        } else if (aPrivate || bPrivate) {
            if (aPrivate) {
                JsonObject history;
                if (aStock != null) {
                    history = FILE_HANDLER.getPrivateStock(a.getTicker());
                } else {
                    history = FILE_HANDLER.getPrivateInventory(a.getTicker());
                }
                LCurrency cur = getLCurrency(history.getString("currency").getString());
                JsonArray arr = history.getArray("history");
                BigDecimal price = BigDecimal.ZERO;
                for (JsonObject obj : arr.getObjectArray()) {
                    price = obj.getDecimal("price").decimal;
                }
                if (cur.equals(b)) {
                    return price.multiply(amount);
                } else {
                    return convert(price.multiply(amount), cur, b);
                }
            } else {
                JsonObject history;
                if (aStock != null) {
                    history = FILE_HANDLER.getPrivateStock(b.getTicker());
                } else {
                    history = FILE_HANDLER.getPrivateInventory(b.getTicker());
                }
                LCurrency cur = getLCurrency(history.getString("currency").getString());
                JsonArray arr = history.getArray("history");
                BigDecimal price = BigDecimal.ZERO;
                for (JsonObject obj : arr.getObjectArray()) {
                    price = obj.getDecimal("price").decimal;
                }
                if (cur.equals(a)) {
                    return price.multiply(amount);
                } else {
                    return convert(price.multiply(amount), cur, a);
                }
            }
        } else {
            for (LMarketApi marketApi : MARKET_APIS) {
                if (marketApi.canConvert(a, b)) {
                    return marketApi.convert(amount, a, b);
                }
            }
            LMarketApi aa = null, bb = null;
            for (LMarketApi marketApi : MARKET_APIS) {
                if (aa == null && marketApi.canConvert(a, main)) {
                    aa = marketApi;
                }
                if (bb == null && marketApi.canConvert(b, main)) {
                    bb = marketApi;
                }
            }
            if (aa == null || bb == null) {
                if (aa == null && bb == null) {
                    LOG_HANDLER.error(getClass(), "The following assets are not presently convertible: " + a + ", " + b);
                } else if (aa == null) {
                    LOG_HANDLER.error(getClass(), "The following asset is not presently convertible: " + a);
                } else {
                    LOG_HANDLER.error(getClass(), "The following asset is not presently convertible: " + b);
                }
                return BigDecimal.ZERO;
            } else {
                BigDecimal x = aa.convert(BigDecimal.ONE, a, main).divide(bb.convert(BigDecimal.ONE, b, main), precision);
                return amount.multiply(x).multiply(b.getFactor().divide(a.getFactor(), precision));
            }
        }
    }

    public BigDecimal convert(BigDecimal amount, LCurrency a, LCurrency b, LDate date) {
        LStock aStock = null, bStock = null;
        LInventory aInv = null, bInv = null;
        boolean aPrivate = false, bPrivate = false;
        if (a instanceof LStock) {
            aStock = (LStock) a;
        }
        if (b instanceof LStock) {
            bStock = (LStock) b;
        }
        if (a instanceof LInventory) {
            aInv = (LInventory) a;
        }
        if (b instanceof LInventory) {
            bInv = (LInventory) b;
        }
        if ((aStock != null && !aStock.isPublic()) || (aInv != null && !aInv.isPublic())) {
            aPrivate = true;
        }
        if ((bStock != null && !bStock.isPublic()) || (bInv != null && !bInv.isPublic())) {
            bPrivate = true;
        }
        if (a.getTicker().equalsIgnoreCase(b.getTicker()) && a.getClass() == b.getClass() && a.isFiat() == b.isFiat()) {
            return amount.multiply(b.getFactor().divide(a.getFactor(), precision));
        } else if (aPrivate || bPrivate) {
            if (aPrivate) {
                JsonObject history;
                if (aStock != null) {
                    history = FILE_HANDLER.getPrivateStock(a.getTicker());
                } else {
                    history = FILE_HANDLER.getPrivateInventory(a.getTicker());
                }
                LCurrency cur = getLCurrency(history.getString("currency").getString());
                JsonArray arr = history.getArray("history");
                BigDecimal price = BigDecimal.ZERO;
                if (new LDate(arr.getObject(0).getString("date").getString(), this).compareTo(date) > 0) {
                    price = arr.getObject(0).getDecimal("price").decimal;
                } else {
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject obj = arr.getObject(i);
                        LDate dateI = new LDate(obj.getString("date").getString(), this);
                        if (i < arr.size() - 1) {
                            LDate dateII = new LDate(arr.getObject(i + 1).getString("date").getString(), this);
                            if (date.compareTo(dateI) >= 0 && date.compareTo(dateII) <= 0) {
                                price = obj.getDecimal("price").decimal;
                                break;
                            }
                        }
                        if (i == arr.size() - 1) {
                            price = obj.getDecimal("price").decimal;
                        }
                    }
                }
                if (cur.equals(b)) {
                    return price.multiply(amount);
                } else {
                    return convert(price.multiply(amount), cur, b, date);
                }
            } else {
                JsonObject history;
                if (aStock != null) {
                    history = FILE_HANDLER.getPrivateStock(b.getTicker());
                } else {
                    history = FILE_HANDLER.getPrivateInventory(b.getTicker());
                }
                LCurrency cur = getLCurrency(history.getString("currency").getString());
                JsonArray arr = history.getArray("history");
                BigDecimal price = BigDecimal.ZERO;
                if (new LDate(arr.getObject(0).getString("date").getString(), this).compareTo(date) > 0) {
                    price = arr.getObject(0).getDecimal("price").decimal;
                } else {
                    for (int i = 0; i < arr.size(); i++) {
                        JsonObject obj = arr.getObject(i);
                        LDate dateI = new LDate(obj.getString("date").getString(), this);
                        if (i < arr.size() - 1) {
                            LDate dateII = new LDate(arr.getObject(i + 1).getString("date").getString(), this);
                            if (date.compareTo(dateI) >= 0 && date.compareTo(dateII) <= 0) {
                                price = obj.getDecimal("price").decimal;
                                break;
                            }
                        }
                        if (i == arr.size() - 1) {
                            price = obj.getDecimal("price").decimal;
                        }
                    }
                }
                if (cur.equals(a)) {
                    return price.multiply(amount);
                } else {
                    return convert(price.multiply(amount), cur, a, date);
                }
            }
        } else {
            for (LMarketApi marketApi : MARKET_APIS) {
                if (marketApi.canConvert(a, b)) {
                    return marketApi.convert(amount, a, b, date);
                }
            }
            LMarketApi aa = null, bb = null;
            for (LMarketApi marketApi : MARKET_APIS) {
                if (aa == null && marketApi.canConvert(a, main)) {
                    aa = marketApi;
                }
                if (bb == null && marketApi.canConvert(b, main)) {
                    bb = marketApi;
                }
            }
            if (aa == null || bb == null) {
                if (aa == null && bb == null) {
                    LOG_HANDLER.error(getClass(), "The following assets are not presently convertible: " + a + ", " + b);
                } else if (aa == null) {
                    LOG_HANDLER.error(getClass(), "The following asset is not presently convertible: " + a);
                } else {
                    LOG_HANDLER.error(getClass(), "The following asset is not presently convertible: " + b);
                }
                return BigDecimal.ZERO;
            } else {
                BigDecimal x = aa.convert(BigDecimal.ONE, a, main, date).divide(bb.convert(BigDecimal.ONE, b, main, date), precision);
                return amount.multiply(x).multiply(b.getFactor().divide(a.getFactor(), precision));
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
                return new BigDecimal(out.toString()).divide(BigDecimal.TEN.pow(2), precision);
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

    public ArrayList<String> getFeeExchangesAsStrings() {
        ArrayList<String> out = new ArrayList<>();
        EXCHANGES.forEach(e -> {
            if (e.hasFees()) {
                out.add(e.NAME);
            }
        });
        return out;
    }

    public ArrayList<String> getAllFeesAsStrings(Exchange e) {
        if (e == null) {
            return new ArrayList<>();
        } else {
            ArrayList<String> out = new ArrayList<>();
            CURRENCIES.getBaseline().forEach(c -> {
                if (e.supportsFee(c)) {
                    out.add(c.toString());
                }
            });
            STOCKS.forEach((s -> {
                if (e.supportsFee(s)) {
                    out.add(s.toString());
                }
            }));
            INVENTORIES.forEach((i -> {
                if (e.supportsFee(i)) {
                    out.add(i.toString());
                }
            }));
            return out;
        }
    }

    public ArrayList<String> getAllFeesAsStrings(SearchBox e) {
        return getAllFeesAsStrings(EXCHANGES.getElement(e.getSelectedItem()));
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

    public ArrayList<String> getGhostAccountsAsStrings() {
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
