package com.donny.dendrofinance.data;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.BroadAccountType;
import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.EntryType;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.entry.meta.*;
import com.donny.dendrofinance.entry.totals.OrderBookEntry;
import com.donny.dendrofinance.entry.totals.Position;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.types.*;
import com.donny.dendrofinance.util.Aggregation;
import com.donny.dendrofinance.util.Curation;
import com.donny.dendrofinance.util.Partitioner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataHandler {
    protected final Instance CURRENT_INSTANCE;
    protected final DataSet<TransactionEntry> TRANSACTIONS;
    protected final DataSet<BudgetEntry> BUDGETS;

    public DataHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        TRANSACTIONS = new DataSet<>("Transactions", EntryType.TRANSACTION, CURRENT_INSTANCE);
        BUDGETS = new DataSet<>("Budgets", EntryType.BUDGET, CURRENT_INSTANCE);
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "DataHandler Initiated");
    }

    public void init() {
        try {
            TRANSACTIONS.load();
            BUDGETS.load();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Error loading datasets: " + e);
        }
    }

    public void addTransaction(TransactionEntry entry) {
        readTransactions().add(entry);
    }

    public void addBudget(BudgetEntry entry) {
        readBudgets().add(entry);
    }

    public boolean deleteTransaction(long uuid) {
        TransactionEntry cand = null;
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getUUID() == uuid) {
                cand = entry;
                break;
            }
        }
        if (cand == null) {
            return false;
        } else {
            readTransactions().remove(cand);
            return true;
        }
    }

    public ArrayList<TransactionEntry> readTransactions() {
        try {
            TRANSACTIONS.read().sort(TransactionEntry::compareTo);
            return TRANSACTIONS.read();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Error loading datasets: " + e);
            return new ArrayList<>();
        }
    }

    public ArrayList<BudgetEntry> readBudgets() {
        try {
            return BUDGETS.read();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Error loading datasets: " + e);
            return new ArrayList<>();
        }
    }

    public TransactionEntry getPrior() {
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getEntity().equals("PRIOR")) {
                return entry;
            }
        }
        return null;
    }

    public BigDecimal accountAsOf(String name, LDate date) {
        if (CURRENT_INSTANCE.ACCOUNTS.getElement(name) != null) {
            return accountAsOf(CURRENT_INSTANCE.ACCOUNTS.getElement(name), date);
        } else {
            return null;
        }
    }

    public BigDecimal accountAsOf(Account acc, LDate date) {
        BigDecimal x = BigDecimal.ZERO;
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getAccounts().toString().contains(acc.getName()) && entry.getDate().compare(date) <= 0) {
                for (TransactionEntry.AVPair pair : entry.getAVPairs()) {
                    if (pair.WRAPPER.ACCOUNT.equals(acc)) {
                        x = x.add(pair.WRAPPER.alpha(pair.VALUE));
                    }
                }
            }
        }
        return x;
    }

    public HashMap<LCurrency, BigDecimal> pricesAsOf(int y, int m, int d) {
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "Price get started");
        HashMap<Account, BigDecimal> acc = accountsAsOf(y, m, d);
        HashMap<LCurrency, BigDecimal> out = new HashMap<>();
        boolean stock = false;
        HashMap<String, BigDecimal> stocks = new HashMap<>();
        if (CURRENT_INSTANCE.stockAPI.equals("twelve")) {
            ArrayList<String> twelve = new ArrayList<>();
            for (Account a : acc.keySet()) {
                if (a.getCurrency() instanceof LStock s) {
                    if (s.isPublic()) {
                        BigDecimal compare = BigDecimal.ONE;
                        if (a.getCurrency().getPlaces() > 0) {
                            compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
                        }
                        if (acc.get(a).compareTo(compare) >= 0 || acc.get(a).compareTo(compare.multiply(BigDecimal.valueOf(-1))) <= 0) {
                            stock = true;
                            twelve.add(a.getCurrency().getTicker());
                        }
                    }
                }
            }
            if (stock) {
                if (twelve.size() > 120) {
                    for (String[] partition : Partitioner.partitionString(twelve, 120)) {
                        stocks.putAll(CURRENT_INSTANCE.FILE_HANDLER.hitTwelveData(new ArrayList<>(Arrays.asList(partition)), new LDate(y, m, d, CURRENT_INSTANCE)));
                    }
                } else {
                    stocks = CURRENT_INSTANCE.FILE_HANDLER.hitTwelveData(twelve, new LDate(y, m, d, CURRENT_INSTANCE));
                }
            }
        }
        for (Account a : acc.keySet()) {
            BigDecimal compare = BigDecimal.ONE;
            if (a.getCurrency().getPlaces() > 0) {
                compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
            }
            if (acc.get(a).abs().compareTo(compare) >= 0) {
                if (!out.containsKey(a.getCurrency())) {
                    if (CURRENT_INSTANCE.stockAPI.equals("twelve") && a.getCurrency() instanceof LStock && ((LStock) a.getCurrency()).isPublic()) {
                        out.put(a.getCurrency(), stocks.get(a.getCurrency().getTicker()));
                    } else {
                        out.put(a.getCurrency(), a.getCurrency().getTotal(BigDecimal.ONE, new LDate(y, m, d, CURRENT_INSTANCE)));
                    }
                }
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "Price get finished");
        return out;
    }

    public HashMap<Account, BigDecimal> accountsAsOf(LDate date) {
        Aggregation<Account> accounts = new Aggregation<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compare(date) <= 0) {
                for (TransactionEntry.AVPair pair : entry.getAVPairs()) {
                    accounts.add(pair.WRAPPER.ACCOUNT, pair.WRAPPER.alpha(pair.VALUE));
                }
            }
        }
        return accounts;
    }

    public HashMap<Account, BigDecimal> accountsAsOf(int y, int m, int d) {
        Aggregation<Account> accounts = new Aggregation<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compare(y, m, d) <= 0) {
                for (TransactionEntry.AVPair pair : entry.getAVPairs()) {
                    accounts.add(pair.WRAPPER.ACCOUNT, pair.WRAPPER.alpha(pair.VALUE));
                }
            }
        }
        return accounts;
    }

    public BigDecimal cryptoAsOf(int y, int m, int d, LCurrency natural) {
        BigDecimal crypto = BigDecimal.ZERO;
        HashMap<Account, BigDecimal> vals = accountsAsOf(y, m, d);
        Aggregation<LCurrency> fin = new Aggregation<>();
        for (Account a : vals.keySet()) {
            if (a.getBroadAccountType() == BroadAccountType.TRACKING && !(a.getCurrency() instanceof LStock) && !(a.getCurrency() instanceof LInventory)) {
                fin.add(a.getCurrency(), vals.get(a));
            }
        }
        for (LCurrency c : fin.keySet()) {
            if (fin.get(c).compareTo(BigDecimal.ZERO) > 0) {
                if (!c.isFiat()) {
                    if (!c.getName().contains("v2")) {
                        if (c.getAltName().equals("")) {
                            crypto = crypto.add(fin.get(c).multiply(CURRENT_INSTANCE.FILE_HANDLER.hitCoinGeckoHistory(c.getName().toLowerCase().replace(" ", "-"), natural.getTicker(), y, m, d)));
                        } else {
                            crypto = crypto.add(fin.get(c).multiply(CURRENT_INSTANCE.FILE_HANDLER.hitCoinGeckoHistory(c.getAltName(), natural.getTicker(), y, m, d)));
                        }
                    } else {
                        CURRENT_INSTANCE.LOG_HANDLER.warn(this.getClass(), "REPv2: ɍ" + fin.get(c));
                    }
                }
            }
        }
        return crypto;
    }

    public ArrayList<AssetMetadata> assetTotals() {
        ArrayList<AssetMetadata> assets = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.hasMeta("asset")) {
                assets.addAll(entry.getAssetMeta());
            }
            if (entry.hasMeta("asset-change")) {
                for (AssetChangeMetadata meta : entry.getAssetChangeMeta()) {
                    for (AssetMetadata ass : assets) {
                        if (meta.NAME.equals(ass.NAME)) {
                            ass.EVENTS.add(meta);
                            break;
                        }
                    }
                }
            }
        }
        return assets;
    }

    public ArrayList<AssetMetadata> assetsAsOf(LDate date) {
        ArrayList<AssetMetadata> assets = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compare(date) <= 0) {
                if (entry.hasMeta("asset")) {
                    assets.addAll(entry.getAssetMeta());
                }
                if (entry.hasMeta("asset-change")) {
                    for (AssetChangeMetadata meta : entry.getAssetChangeMeta()) {
                        for (AssetMetadata ass : assets) {
                            if (meta.NAME.equals(ass.NAME)) {
                                ass.EVENTS.add(meta);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return assets;
    }

    public AssetMetadata assetAsOf(String name, LDate date) {
        for (AssetMetadata total : assetsAsOf(date)) {
            if (total.NAME.equals(name)) {
                return total;
            }
        }
        return null;
    }

    public ArrayList<LoanMetadata> loanTotals() {
        ArrayList<LoanMetadata> loans = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.hasMeta("loan")) {
                loans.addAll(entry.getLoanMeta());
            }
            if (entry.hasMeta("loan-change")) {
                for (LoanChangeMetadata meta : entry.getLoanChangeMeta()) {
                    for (LoanMetadata lon : loans) {
                        if (meta.NAME.equals(lon.NAME)) {
                            lon.EVENTS.add(meta);
                            break;
                        }
                    }
                }
            }
        }
        return loans;
    }

    public ArrayList<LoanMetadata> loansAsOf(LDate date) {
        ArrayList<LoanMetadata> loans = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compare(date) <= 0) {
                if (entry.hasMeta("loan")) {
                    loans.addAll(entry.getLoanMeta());
                }
                if (entry.hasMeta("loan-change")) {
                    for (LoanChangeMetadata meta : entry.getLoanChangeMeta()) {
                        for (LoanMetadata lon : loans) {
                            if (meta.NAME.equals(lon.NAME)) {
                                lon.EVENTS.add(meta);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return loans;
    }

    public LoanMetadata loanAsOf(String name, LDate date) {
        for (LoanMetadata total : loansAsOf(date)) {
            if (total.NAME.equals(name)) {
                return total;
            }
        }
        return null;
    }

    /**
     * The following is a list of implemented features of this search function
     * searching <code>a b c</code> will check separately for the presence of the character sequences <code>a</code>, <code>b</code>, and <code>c</code>.  Only entries with all three present will be included.
     * searching <code>"a b c"</code> will check for the character sequence <code>a b c</code> and only entries with that character sequence will be included
     * $T will return entries that have a tax account
     * $t will return entries that do not have a tax account
     * $B will return entries that have a budget account
     * $b will return entries that do not have a budget account
     * $C will return entries that have a tracking account
     * $c will return entries that do not have a tracking account
     * $L will return entries that have trading metadata
     * $l will return entries that do not have trading metadata
     * $A will return entries that have asset metadata
     * $a will return entries that do not have asset metadata
     * $D will return entries that have loan metadata
     * $d will return entries that do not have loan metadata
     * $@ will return entries that are unbalanced
     * $or(<code>[tokens]</code>) acts as an or test rather than the default and with the included tokens.
     *
     * @param search a search string, encoded as above
     * @return an <code>ArrayList</code> of transaction entries that met the search criteria
     */
    public ArrayList<TransactionEntry> huntTransactions(String search) {
        ArrayList<TransactionEntry> correct = new ArrayList<>();
        ArrayList<String> tokens = tokenize(search);
        for (TransactionEntry entry : readTransactions()) {
            boolean flag = true;
            OUTER:
            for (String token : tokens) {
                if (!token.equals("")) {
                    switch (token.charAt(0)) {
                        case '$' -> {
                            switch (token) {
                                case "$T" -> {
                                    if (!entry.hasTaxAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$t" -> {
                                    if (entry.hasTaxAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$B" -> {
                                    if (!entry.hasBudgetAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$b" -> {
                                    if (entry.hasBudgetAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$C" -> {
                                    if (!entry.hasTrackingAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$c" -> {
                                    if (entry.hasTrackingAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$L" -> {
                                    if (!entry.hasMeta("ledger")) {
                                        flag = false;
                                    }
                                }
                                case "$l" -> {
                                    if (entry.hasMeta("ledger")) {
                                        flag = false;
                                    }
                                }
                                case "$A" -> {
                                    if (!(entry.hasMeta("asset") || entry.hasMeta("asset-change"))) {
                                        flag = false;
                                    }
                                }
                                case "$a" -> {
                                    if (entry.hasMeta("asset") || entry.hasMeta("asset-change")) {
                                        flag = false;
                                    }
                                }
                                case "$D" -> {
                                    if (!(entry.hasMeta("loan") || entry.hasMeta("loan-change"))) {
                                        flag = false;
                                    }
                                }
                                case "$d" -> {
                                    if (entry.hasMeta("loan") || entry.hasMeta("loan-change")) {
                                        flag = false;
                                    }
                                }
                                case "$@" -> {
                                    if (entry.isBalanced()) {
                                        flag = false;
                                    }
                                }
                                default -> {
                                    if (token.length() > 5) {
                                        if (token.substring(0, 3).equalsIgnoreCase("$or")) {
                                            ArrayList<String> subTokens = tokenize(token.substring(4, token.length() - 1));
                                            boolean test = false;
                                            for (String sToken : subTokens) {
                                                if (entry.toString().contains(sToken)) {
                                                    test = true;
                                                }
                                            }
                                            if (!test) {
                                                flag = false;
                                            }
                                        }
                                    }
                                }
                            }
                            if (!flag) {
                                break OUTER;
                            }
                        }
                        case '-' -> {
                            if (entry.toFlatString().replace("\t", " ").toLowerCase().contains(token.replace("\"", "").replace("\\$", "$").replace("-", "").toLowerCase())) {
                                flag = false;
                                break OUTER;
                            }
                        }
                        default -> {
                            if (!entry.toFlatString().replace("\t", " ").toLowerCase().contains(token.replace("\"", "").replace("\\$", "$").toLowerCase())) {
                                flag = false;
                                break OUTER;
                            }
                        }
                    }
                }
            }
            if (flag) {
                correct.add(entry);
            }
        }
        correct.sort((t1, t2) -> t1.getDate().compare(t2.getDate()));
        return correct;
    }

    public ArrayList<String> getBudgetTypes() {
        Curation<String> out = new Curation<>();
        for (Account a : CURRENT_INSTANCE.ACCOUNTS) {
            if (a.getBroadAccountType() == BroadAccountType.EXPENSE) {
                out.add(a.getBudgetType());
            }
        }
        return out;
    }

    public ArrayList<String> tokenize(String raw) {
        ArrayList<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean quote = false, function = false;
        for (char c : raw.toCharArray()) {
            if (c == '"') {
                quote = !quote;
            }
            if (c == '(') {
                function = true;
            }
            if (c == ')') {
                function = false;
            }
            if (c == ' ' && !quote && !function) {
                tokens.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString());
        return tokens;
    }

    public TransactionEntry getTransactionEntry(long uuid) {
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getUUID() == uuid) {
                return entry;
            }
        }
        return null;
    }

    /**
     * @return Object[]{
     * ArrayList<Position> positions
     * ArrayList<OrderBookEntry> order book
     * }
     */
    public Object[] ledgerize(LDate date) {
        ArrayList<Position> positions = new ArrayList<>();
        ArrayList<OrderBookEntry> orderBook = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.hasMeta("ledger") && entry.getDate().compare(date) <= 0) {
                for (LedgerMetadata meta : entry.getLedgerMeta()) {
                    boolean flag = true, auxFlag = true;
                    if (meta.FROM.equals(CURRENT_INSTANCE.main)) {
                        LCurrency a = meta.TO;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                orderBook.addAll(p.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.FROM_AMNT));
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            Position newP = new Position(meta.TO, entry.CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.FROM_AMNT));
                            positions.add(newP);
                        }
                    } else if (meta.TO.equals(CURRENT_INSTANCE.main)) {
                        LCurrency a = meta.FROM;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                orderBook.addAll(p.change(entry.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.TO_AMNT));
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            Position newP = new Position(meta.FROM, entry.CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.TO_AMNT));
                            positions.add(newP);
                        }
                    } else {
                        LCurrency a = meta.TO, b = meta.FROM;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                orderBook.addAll(p.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                                flag = false;
                            }
                            if (p.ASSET.equals(b)) {
                                orderBook.addAll(p.change(entry.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
                                auxFlag = false;
                            }
                        }
                        if (flag && auxFlag) {
                            Position newP = new Position(meta.TO, entry.CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                            positions.add(newP);
                            newP = new Position(meta.FROM, entry.CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
                            positions.add(newP);
                        } else if (flag) {
                            Position newP = new Position(meta.TO, entry.CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                            positions.add(newP);
                        } else if (auxFlag) {
                            Position newP = new Position(meta.FROM, entry.CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
                            positions.add(newP);
                        }
                    }
                }
                ArrayList<Position> temp = new ArrayList<>(positions);
                for (Position t : temp) {
                    if (t.ELEMENTS.isEmpty()) {
                        positions.remove(t);
                    }
                }
            }
        }
        return new Object[]{positions, orderBook};
    }

    public Object[] ledgerize() {
        return ledgerize(LDate.now(CURRENT_INSTANCE));
    }

    public ArrayList<LedgerMetadata> getLedgerMeta(LCurrency currency) {
        ArrayList<LedgerMetadata> out = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.hasMeta("ledger")) {
                for (LedgerMetadata meta : entry.getLedgerMeta()) {
                    if (meta.FROM.equals(currency) || meta.TO.equals(currency)) {
                        out.add(meta);
                    }
                }
            }
        }
        return out;
    }

    public ArrayList<Position> getPositions() {
        return (ArrayList<Position>) ledgerize()[0];
    }

    public Position getPosition(LCurrency asset) {
        for (Position p : getPositions()) {
            if (p.ASSET.equals(asset)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<OrderBookEntry> getOrderBook() {
        return (ArrayList<OrderBookEntry>) ledgerize()[1];
    }

    public ArrayList<OrderBookEntry> getOrderBook(LCurrency asset) {
        ArrayList<OrderBookEntry> out = new ArrayList<>();
        for (OrderBookEntry entry : getOrderBook()) {
            if (entry.ASSET.equals(asset)) {
                out.add(entry);
            }
        }
        return out;
    }

    public ArrayList<LedgerMetadata> getLedgerMeta(LCurrency currency, LDate date) {
        ArrayList<LedgerMetadata> out = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.hasMeta("ledger") && entry.getDate().compare(date) <= 0) {
                for (LedgerMetadata meta : entry.getLedgerMeta()) {
                    if (meta.FROM.equals(currency) || meta.TO.equals(currency)) {
                        out.add(meta);
                    }
                }
            }
        }
        return out;
    }

    public ArrayList<Position> getPositions(LDate date) {
        return (ArrayList<Position>) ledgerize(date)[0];
    }

    public Position getPosition(LCurrency asset, LDate date) {
        for (Position p : getPositions(date)) {
            if (p.ASSET.equals(asset)) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<OrderBookEntry> getOrderBook(LDate date) {
        return (ArrayList<OrderBookEntry>) ledgerize(date)[1];
    }

    public ArrayList<OrderBookEntry> getOrderBook(LCurrency asset, LDate date) {
        ArrayList<OrderBookEntry> out = new ArrayList<>();
        for (OrderBookEntry entry : getOrderBook(date)) {
            if (entry.ASSET.equals(asset)) {
                out.add(entry);
            }
        }
        return out;
    }

    public boolean buySell(LDate date, BigDecimal amount, BigDecimal cost, String exchange, String currency) {
        try {
            Exchange e = CURRENT_INSTANCE.EXCHANGES.getElement(exchange);
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            if (e == null || c == null) {
                return false;
            }
            if (!e.supports(c)) {
                return false;
            }
            String acc = "Crypto";
            if (c instanceof LStock) {
                acc = "Stock";
            } else if (c instanceof LInventory) {
                acc = "Held_Inventory";
            } else if (c.isFiat()) {
                acc = "Other_Cash";
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                entry.insert(
                        date,
                        new LString(e.NAME),
                        new LString(c.toString()),
                        new LString(currency + " Purchase"),
                        new LAccountSet("D!" + acc + ", D!Trading_Expenses, C!" + e.NAME + "_USD, C!Portfolio, B!" + e.NAME + "_" + c.getTicker(), CURRENT_INSTANCE),
                        new LDecimalSet(cost.abs() + ", " + cost.abs() + ", " + cost.abs() + ", " + cost.abs() + ", " + amount)
                );
                entry.addLedgerMeta(CURRENT_INSTANCE.main, c, cost, amount, cost.abs());
                addTransaction(entry);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                Position temp = getPosition(c);
                ArrayList<OrderBookEntry> orders = temp.change(entry.getUUID(), date, c, amount, cost);
                BigDecimal profit = BigDecimal.ZERO;
                for (OrderBookEntry order : orders) {
                    profit = profit.add(order.profit());
                }
                String gl = "Gain";
                if (profit.compareTo(BigDecimal.ZERO) < 0) {
                    gl = "Loss";
                }
                entry.insert(
                        date,
                        new LString(e.NAME),
                        new LString(c.toString()),
                        new LString(currency + " Sale"),
                        new LAccountSet("D!" + e.NAME + "_USD, D!Portfolio, C!" + acc + ", C!Trading_Income, B!" + e.NAME + "_" + c.getTicker() + ", T!Tax_Cap" + gl, CURRENT_INSTANCE),
                        new LDecimalSet(cost + ", " + cost + ", " + cost + ", " + cost + ", " + amount + ", " + profit.abs())
                );
                entry.addLedgerMeta(c, CURRENT_INSTANCE.main, amount, cost, cost);
                addTransaction(entry);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean incPay(LDate date, String description, BigDecimal amount, BigDecimal unit, String exchange, String currency) {
        try {
            BigDecimal cost = amount.multiply(unit);
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            Exchange e = CURRENT_INSTANCE.EXCHANGES.getElement(exchange);
            if (c == null || e == null) {
                return false;
            }
            if (!e.supports(c)) {
                return false;
            }
            boolean s = e.stakes(c) > -1;
            String acc = "Crypto";
            if (c instanceof LStock) {
                acc = "Stock";
            } else if (c instanceof LInventory) {
                acc = "Held_Inventory";
            } else if (c.isFiat()) {
                acc = "Other_Cash";
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                String accSt;
                if (s) {
                    accSt = "D!" + acc + ", C!Portfolio, B!" + e.NAME + "_" + c.getTicker() + "_S";
                } else {
                    accSt = "D!" + acc + ", C!Portfolio, B!" + e.NAME + "_" + c.getTicker();
                }
                entry.insert(
                        date,
                        new LString(exchange),
                        new LString(currency),
                        new LString(description),
                        new LAccountSet(accSt, CURRENT_INSTANCE),
                        new LDecimalSet(cost + ", " + cost + ", " + amount)
                );
                entry.addLedgerMeta(CURRENT_INSTANCE.main, c, BigDecimal.ZERO, amount, cost.abs());
                addTransaction(entry);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                entry.insert(
                        date,
                        new LString(exchange),
                        new LString(currency),
                        new LString(description),
                        new LAccountSet("D!Portfolio, C!" + acc + ", B!" + e.NAME + "_" + c.getTicker(), CURRENT_INSTANCE),
                        new LDecimalSet(cost.abs() + ", " + cost.abs() + ", " + amount)
                );
                entry.addLedgerMeta(c, CURRENT_INSTANCE.main, amount, BigDecimal.ZERO, cost.abs());
                addTransaction(entry);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean coinTransfer(LDate date, BigDecimal fromAmnt, BigDecimal toAmnt, BigDecimal unit,
                                String fromExchange, String toExchange, String currency) {
        try {
            BigDecimal fee = toAmnt.subtract(fromAmnt);
            BigDecimal cost = fee.multiply(unit).abs();
            if (cost.compareTo(new BigDecimal("0.01")) < 0) {
                cost = BigDecimal.ZERO;
            }
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            Exchange ef = CURRENT_INSTANCE.EXCHANGES.getElement(fromExchange);
            Exchange et = CURRENT_INSTANCE.EXCHANGES.getElement(toExchange);
            if (c == null || ef == null || et == null) {
                return false;
            }
            if (!ef.supports(c) || !et.supports(c)) {
                return false;
            }
            String acc = "Crypto";
            if (c instanceof LStock) {
                acc = "Stock";
            } else if (c instanceof LInventory) {
                acc = "Held_Inventory";
            } else if (c.isFiat()) {
                acc = "Other_Cash";
            }
            String ent;
            if (ef.NAME.equalsIgnoreCase("Personal")) {
                ent = et.NAME;
            } else if (et.NAME.equalsIgnoreCase("Personal")) {
                ent = ef.NAME;
            } else {
                ent = ef.NAME + ", " + et.NAME;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            entry.insert(
                    date,
                    new LString(ent),
                    new LString(c.toString()),
                    new LString(c + " Transfer"),
                    new LAccountSet("D!Portfolio, C!" + acc + ", B!" + ef.NAME + "_" + c.getTicker() + ", B!" + et.NAME + "_" + c.getTicker(), CURRENT_INSTANCE),
                    new LDecimalSet(cost + ", " + cost + ", " + fromAmnt.multiply(BigDecimal.valueOf(-1)) + ", " + toAmnt)
            );
            entry.addLedgerMeta(c, CURRENT_INSTANCE.main, fee, BigDecimal.ZERO, cost);
            addTransaction(entry);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean tokenTransfer(LDate date, BigDecimal fromAmnt, BigDecimal toAmnt, BigDecimal fee, BigDecimal fromUnit,
                                 BigDecimal feeUnit, String fromExchange, String toExchange, String transCur, String costCur) {
        try {
            BigDecimal transitLoss = toAmnt.subtract(fromAmnt);
            BigDecimal cost1 = transitLoss.multiply(fromUnit);
            if (cost1.abs().compareTo(new BigDecimal("0.01")) < 0) {
                cost1 = BigDecimal.ZERO;
            }
            BigDecimal cost2 = fee.multiply(feeUnit);
            if (cost2.abs().compareTo(new BigDecimal("0.01")) < 0) {
                cost2 = BigDecimal.ZERO;
            }
            BigDecimal cost = cost1.abs().add(cost2.abs());
            LCurrency tc = CURRENT_INSTANCE.getLCurrency(transCur);
            LCurrency fc = CURRENT_INSTANCE.getLCurrency(costCur);
            Exchange ef = CURRENT_INSTANCE.EXCHANGES.getElement(fromExchange);
            Exchange et = CURRENT_INSTANCE.EXCHANGES.getElement(toExchange);
            if (tc == null || fc == null || ef == null || et == null) {
                return false;
            }
            if (!ef.supports(tc) || !ef.supports(fc) || !et.supports(tc)) {
                return false;
            }
            String ent;
            if (ef.NAME.equalsIgnoreCase("Personal")) {
                ent = et.NAME;
            } else if (et.NAME.equalsIgnoreCase("Personal")) {
                ent = ef.NAME;
            } else {
                ent = ef.NAME + ", " + et.NAME;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            entry.insert(
                    date,
                    new LString(ent),
                    new LString(tc + ", " + fc),
                    new LString(tc + " Transfer"),
                    new LAccountSet("D!Portfolio, C!Crypto, B!" + ef.NAME + "_" + tc.getTicker() + ", B!" + ef.NAME + "_" + fc.getTicker() +
                            ", B!" + et.NAME + "_" + tc.getTicker(), CURRENT_INSTANCE),
                    new LDecimalSet(cost.abs() + ", " + cost.abs() + ", " + fromAmnt.multiply(BigDecimal.valueOf(-1)) + ", " + fee + ", " + toAmnt)
            );
            if (transitLoss.compareTo(BigDecimal.ZERO) != 0) {
                entry.addLedgerMeta(tc, CURRENT_INSTANCE.main, transitLoss, BigDecimal.ZERO, cost1.abs());
            }
            entry.addLedgerMeta(fc, CURRENT_INSTANCE.main, fee, BigDecimal.ZERO, cost2.abs());
            addTransaction(entry);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean trade(LDate date, BigDecimal fromAmnt, BigDecimal toAmnt, BigDecimal unit,
                         String exchange, String fromCur, String toCur) {
        try {
            BigDecimal cost = fromAmnt.multiply(unit).abs();
            if (cost.compareTo(new BigDecimal("0.01")) < 0) {
                cost = BigDecimal.ZERO;
            }
            LCurrency fc = CURRENT_INSTANCE.getLCurrency(fromCur);
            LCurrency tc = CURRENT_INSTANCE.getLCurrency(toCur);
            Exchange e = CURRENT_INSTANCE.EXCHANGES.getElement(exchange);
            if (fc == null || tc == null || e == null) {
                return false;
            }
            if (fc.toString().equals(tc.toString())) {
                return false;
            }
            if (!e.supports(fc) || !e.supports(tc)) {
                return false;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            Position temp = getPosition(fc);
            ArrayList<OrderBookEntry> orders = temp.change(entry.getUUID(), date, fc, fromAmnt.multiply(BigDecimal.valueOf(-1)), cost);
            BigDecimal profit = BigDecimal.ZERO;
            for (OrderBookEntry order : orders) {
                profit = profit.add(order.profit());
            }
            String gl = "Gain";
            if (profit.compareTo(BigDecimal.ZERO) < 0) {
                gl = "Loss";
            }
            entry.insert(
                    date,
                    new LString(e.NAME),
                    new LString(fc + ", " + tc),
                    new LString("Trade (" + fc + " -> " + tc + ")"),
                    new LAccountSet("B!" + e.NAME + "_" + fc.getTicker() + ", B!" + e.NAME + "_" + tc.getTicker()
                            + ", T!Tax_Cap" + gl, CURRENT_INSTANCE),
                    new LDecimalSet(fromAmnt.multiply(BigDecimal.valueOf(-1)) + ", " + toAmnt + ", " + profit)
            );
            entry.addLedgerMeta(fc, tc, fromAmnt, toAmnt, cost);
            addTransaction(entry);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void save() {
        TRANSACTIONS.save();
        BUDGETS.save();
    }

    public void checkLedgers() {
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
            if (entry.hasMeta("ledger")) {
                Aggregation<String> curAg = new Aggregation<>();
                Aggregation<String> ledgAg = new Aggregation<>();
                for (TransactionEntry.AVPair pair : entry.getAVPairs()) {
                    if (pair.WRAPPER.COLUMN == AccountWrapper.AWType.TRACKER) {
                        String c = pair.WRAPPER.ACCOUNT.getCurrency().toUnifiedString();
                        curAg.add(c, pair.VALUE);
                    }
                }
                for (LedgerMetadata meta : entry.getLedgerMeta()) {
                    if (!meta.FROM.equals(CURRENT_INSTANCE.main)) {
                        ledgAg.add(meta.FROM.toString(), meta.FROM_AMNT);
                    }
                    if (!meta.TO.equals(CURRENT_INSTANCE.main)) {
                        ledgAg.add(meta.TO.toString(), meta.TO_AMNT);
                    }
                }
                for (String cur : curAg.keySet()) {
                    if (!ledgAg.containsKey(cur)) {
                        if (curAg.get(cur).compareTo(BigDecimal.ZERO) != 0) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Damaged or missing ledger metadata for entry: " + entry.getUUID());
                        }
                    } else if (curAg.get(cur).compareTo(ledgAg.get(cur)) != 0) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Damaged ledger metadata for entry: " + entry.getUUID());
                        CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Disparity: " + cur + ": " + curAg.get(cur) + " / " + ledgAg.get(cur));
                    }
                }
            }
        }
    }

    public void checkCG() {
        Aggregation<Long> map = new Aggregation<>();
        for (OrderBookEntry entry : getOrderBook()) {
            map.add(entry.END_REF, entry.profit());
        }
        for (Long uuid : map.keySet()) {
            TransactionEntry entry = getTransactionEntry(uuid);
            boolean flag = true;
            if (entry.hasTaxAccounts()) {
                for (TransactionEntry.AVPair pair : entry.getAVPairs()) {
                    if (pair.WRAPPER.COLUMN == AccountWrapper.AWType.TAX) {
                        if (pair.WRAPPER.ACCOUNT.getName().contains("CapGain") || pair.WRAPPER.ACCOUNT.getName().contains("CapLoss")) {
                            flag = false;
                            if (map.get(uuid).compareTo(BigDecimal.ZERO) < 0) {
                                if (pair.WRAPPER.ACCOUNT.getName().equals("Tax_CapGain") ||
                                        !CURRENT_INSTANCE.$(map.get(uuid).abs()).equals(CURRENT_INSTANCE.$(pair.VALUE))) {
                                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Mismatched Capital Loss: " + uuid);
                                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Disparity: " + CURRENT_INSTANCE.$(map.get(uuid)) + " : " +
                                            (pair.WRAPPER.ACCOUNT.getName().contains("CapGain") ? CURRENT_INSTANCE.$(pair.VALUE) :
                                                    CURRENT_INSTANCE.$(pair.VALUE.multiply(BigDecimal.valueOf(-1))))
                                    );
                                }
                            } else if (map.get(uuid).compareTo(BigDecimal.ZERO) > 0) {
                                if (pair.WRAPPER.ACCOUNT.getName().equals("Tax_CapLoss") ||
                                        !(CURRENT_INSTANCE.$(map.get(uuid).abs()).equals(CURRENT_INSTANCE.$(pair.VALUE)))
                                ) {
                                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Mismatched Capital Gain: " + uuid);
                                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Disparity: " + CURRENT_INSTANCE.$(map.get(uuid)) + " : " +
                                            (pair.WRAPPER.ACCOUNT.getName().contains("CapGain") ? CURRENT_INSTANCE.$(pair.VALUE) :
                                                    CURRENT_INSTANCE.$(pair.VALUE.multiply(BigDecimal.valueOf(-1))))
                                    );
                                }
                            }
                        }
                    }
                }
            }
            if (flag && !entry.equals(getPrior())) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Sale missing Capital Gain/Loss: " + entry.getUUID());
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Value should be: " + map.get(uuid));
            }
        }
    }
}
