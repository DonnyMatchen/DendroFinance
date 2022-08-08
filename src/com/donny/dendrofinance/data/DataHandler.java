package com.donny.dendrofinance.data;

import com.donny.dendrofinance.account.*;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.EntryType;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.entry.meta.*;
import com.donny.dendrofinance.entry.totals.OrderBookEntry;
import com.donny.dendrofinance.entry.totals.Position;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.Aggregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class DataHandler {
    protected final Instance CURRENT_INSTANCE;
    protected final DataSet<TransactionEntry> TRANSACTIONS;
    protected final DataSet<BudgetEntry> BUDGETS;
    protected final ArrayList<String> BUDGET_TYPES;
    public boolean budgetTypesChanged = false;

    public DataHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        TRANSACTIONS = new DataSet<>("Transactions", EntryType.TRANSACTION, CURRENT_INSTANCE);
        BUDGETS = new DataSet<>("Budgets", EntryType.BUDGET, CURRENT_INSTANCE);
        BUDGET_TYPES = new ArrayList<>();
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "DataHandler Initiated");
    }

    public void reload() {
        CURRENT_INSTANCE.UUID_HANDLER.UUIDS.clear();
        try {
            TRANSACTIONS.reload();
            BUDGETS.reload();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Error loading datasets: " + e);
        }
    }

    public boolean addTransaction(TransactionEntry entry, ImportHandler.ImportMode mode) {
        if (entry.clashing) {
            switch (mode) {
                case IGNORE -> {
                    return false;
                }
                case KEEP -> {
                    return TRANSACTIONS.add(entry);
                }
                case OVERWRITE -> {
                    TransactionEntry orig = null;
                    for (TransactionEntry candidate : readTransactions()) {
                        if (candidate.getUUID() == entry.getUUID()) {
                            orig = candidate;
                            break;
                        }
                    }
                    if (orig != null) {
                        TRANSACTIONS.remove(orig);
                    }
                    return TRANSACTIONS.add(entry);
                }
            }
        }
        return TRANSACTIONS.add(entry);
    }

    public boolean addTransaction(TransactionEntry entry) {
        return addTransaction(entry, ImportHandler.ImportMode.KEEP);
    }

    public boolean addBudget(BudgetEntry entry, ImportHandler.ImportMode mode) {
        if (entry.clashing) {
            switch (mode) {
                case IGNORE -> {
                    return false;
                }
                case KEEP -> {
                    return BUDGETS.add(entry);
                }
                case OVERWRITE -> {
                    BudgetEntry orig = null;
                    for (BudgetEntry candidate : readBudgets()) {
                        if (candidate.getUUID() == entry.getUUID()) {
                            orig = candidate;
                            break;
                        }
                    }
                    if (orig != null) {
                        BUDGETS.remove(orig);
                    }
                    return BUDGETS.add(entry);
                }
            }
        }
        return BUDGETS.add(entry);
    }

    public boolean addBudget(BudgetEntry entry) {
        return addBudget(entry, ImportHandler.ImportMode.KEEP);
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
            return TRANSACTIONS.remove(cand);
        }
    }

    public boolean deleteBudget(long uuid) {
        BudgetEntry cand = null;
        for (BudgetEntry entry : readBudgets()) {
            if (entry.getUUID() == uuid) {
                cand = entry;
                break;
            }
        }
        if (cand == null) {
            return false;
        } else {
            return BUDGETS.remove(cand);
        }
    }

    public ArrayList<TransactionEntry> readTransactions() {
        try {
            return TRANSACTIONS.read();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Error loading datasets: " + e);
            return new ArrayList<>();
        }
    }

    public ArrayList<BudgetEntry> readBudgets() {
        try {
            return BUDGETS.read();
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Error loading datasets: " + e);
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
            if (entry.getAccounts().toString().contains(acc.getName()) && entry.getDate().compareTo(date) <= 0) {
                for (AccountWrapper wrapper : entry.getAccounts()) {
                    if (wrapper.ACCOUNT.equals(acc)) {
                        x = x.add(wrapper.getAlphaProcessed());
                    }
                }
            }
        }
        return x;
    }

    public HashMap<LCurrency, BigDecimal> pricesAsOf(int y, int m, int d) {
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "Price-get started");
        HashMap<Account, BigDecimal> acc = accountsAsOf(y, m, d);
        HashMap<LCurrency, BigDecimal> out = new HashMap<>();
        for (Account a : acc.keySet()) {
            BigDecimal compare = BigDecimal.ONE;
            if (a.getCurrency().getPlaces() > 0) {
                compare = new BigDecimal("0." + "0".repeat(a.getCurrency().getPlaces() - 1) + "1");
            }
            if (acc.get(a).abs().compareTo(compare) >= 0) {
                if (!out.containsKey(a.getCurrency())) {
                    LDate now = LDate.now(CURRENT_INSTANCE);
                    if (now.getYear() == y && now.getMonth() == m && now.getDay() == d) {
                        out.put(a.getCurrency(), a.getCurrency().getTotal(BigDecimal.ONE));
                    } else {
                        out.put(a.getCurrency(), a.getCurrency().getTotal(BigDecimal.ONE, new LDate(y, m, d, CURRENT_INSTANCE)));
                    }
                }
            }
        }
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "Price get finished");
        return out;
    }

    public HashMap<Account, BigDecimal> accountsAsOf(LDate date) {
        Aggregation<Account> accounts = new Aggregation<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compareTo(date) <= 0) {
                for (AccountWrapper wrapper : entry.getAccounts()) {
                    accounts.add(wrapper.ACCOUNT, wrapper.getAlphaProcessed());
                }
            }
        }
        return accounts;
    }

    public HashMap<Account, BigDecimal> accountsAsOf(int y, int m, int d) {
        Aggregation<Account> accounts = new Aggregation<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compareTo(y, m, d) <= 0) {
                for (AccountWrapper wrapper : entry.getAccounts()) {
                    accounts.add(wrapper.ACCOUNT, wrapper.getAlphaProcessed());
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
            crypto = crypto.add(CURRENT_INSTANCE.convert(fin.get(c), c, natural));
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
            if (entry.getDate().compareTo(date) <= 0) {
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
            if (entry.getDate().compareTo(date) <= 0) {
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
     * $G will return entries that have a ghost account
     * $g will return entries that do not have a ghost account
     * $B will return entries that have a budget account
     * $b will return entries that do not have a budget account
     * $T will return entries that have a tracking account
     * $t will return entries that do not have a tracking account
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
                                case "$G" -> {
                                    if (!entry.hasGhostAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$g" -> {
                                    if (entry.hasGhostAccounts()) {
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
                                case "$T" -> {
                                    if (!entry.hasTrackingAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$t" -> {
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
        correct.sort(Comparator.comparing(TransactionEntry::getDate));
        return correct;
    }

    public ArrayList<String> getBudgetTypes() {
        return new ArrayList<>(BUDGET_TYPES);
    }

    public void addBudgetType(String budgetType) {
        BUDGET_TYPES.add(budgetType);
    }

    public void resetBudgetTypes(ArrayList<String> newTypes) {
        BUDGET_TYPES.clear();
        BUDGET_TYPES.addAll(newTypes);
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
            if (entry.hasMeta("ledger") && entry.getDate().compareTo(date) <= 0) {
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
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
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
                            Position newP = new Position(meta.FROM, CURRENT_INSTANCE);
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
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                            positions.add(newP);
                            newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
                            positions.add(newP);
                        } else if (flag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(entry.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                            positions.add(newP);
                        } else if (auxFlag) {
                            Position newP = new Position(meta.FROM, CURRENT_INSTANCE);
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
            if (entry.hasMeta("ledger") && entry.getDate().compareTo(date) <= 0) {
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

    public ArrayList<CheckMetadata> getChecks(LDate date) {
        ArrayList<CheckMetadata> meta = new ArrayList<>();
        for (TransactionEntry entry : readTransactions()) {
            if (entry.getDate().compareTo(date) <= 0 && entry.hasMeta("check")) {
                meta.addAll(entry.getCheckMetadata());
            }
        }
        return meta;
    }

    public ArrayList<CheckMetadata> getChecks() {
        return getChecks(LDate.now(CURRENT_INSTANCE));
    }

    public ArrayList<CheckMetadata> getOutstandingChecks(LDate date) {
        ArrayList<CheckMetadata> out = new ArrayList<>();
        for (CheckMetadata check : getChecks(date)) {
            if (check.isOutstanding()) {
                out.add(check);
            }
        }
        return out;
    }

    public ArrayList<CheckMetadata> getOutstandingChecks() {
        return getOutstandingChecks(LDate.now(CURRENT_INSTANCE));
    }

    public boolean buySell(LDate date, BigDecimal amount, BigDecimal cost, String exchange, String currency) {
        try {
            cost = cost.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP);
            Exchange e = CURRENT_INSTANCE.EXCHANGES.getElement(exchange);
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            if (e == null || c == null) {
                return false;
            }
            if (!e.supports(c)) {
                return false;
            }
            amount = amount.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            String acc = Account.cryptoName;
            if (c instanceof LStock) {
                acc = Account.stockName;
            } else if (c instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (c.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                entry.insert(
                        date,
                        e.NAME,
                        c.toString(),
                        currency + " Purchase",
                        new LAccountSet(
                                "D!" + acc + "("
                                        + cost + "), D!" + Account.tradExpName + "("
                                        + cost + "), C!" + e.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                        + cost + "), C!" + Account.portfolioName + "("
                                        + cost + "), T!" + e.NAME + "_" + c.getTicker() + "("
                                        + amount + ")", CURRENT_INSTANCE)
                );
                entry.addLedgerMeta(CURRENT_INSTANCE.main, c, cost.multiply(BigDecimal.valueOf(-1)), amount, cost);
                addTransaction(entry);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                Position temp = getPosition(c);
                ArrayList<OrderBookEntry> orders = temp.change(entry.getUUID(), date, c, amount, cost);
                BigDecimal profitSR = BigDecimal.ZERO, profitLR = BigDecimal.ZERO;
                for (OrderBookEntry order : orders) {
                    if (order.longTerm()) {
                        profitLR = profitLR.add(order.profit());
                    } else {
                        profitSR = profitSR.add(order.profit());
                    }
                }
                String gl = "";
                if (profitSR.compareTo(BigDecimal.ZERO) != 0) {
                    gl += ", G!";
                    if (c instanceof LStock) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgStockName : Account.clStockName;
                    } else if (c instanceof LInventory) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgInventoryName : Account.clInventoryName;
                    } else {
                        if (c.isFiat()) {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgFiatName : Account.clFiatName;
                        } else {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgCryptoName : Account.clCryptoName;
                        }
                    }
                    gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                if (profitLR.compareTo(BigDecimal.ZERO) != 0) {
                    gl += ", G!";
                    if (c instanceof LStock) {
                        gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltStockName : Account.clltStockName;
                    } else if (c instanceof LInventory) {
                        gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltInventoryName : Account.clltInventoryName;
                    } else {
                        if (c.isFiat()) {
                            gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltFiatName : Account.clltFiatName;
                        } else {
                            gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltCryptoName : Account.clltCryptoName;
                        }
                    }
                    gl += "(" + profitLR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                entry.insert(
                        date,
                        e.NAME,
                        c.toString(),
                        currency + " Sale",
                        new LAccountSet("D!" + e.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                + cost + "), D!" + Account.portfolioName + "("
                                + cost + "), C!" + acc + "("
                                + cost + "), C!" + Account.tradIncName + "("
                                + cost + "), T!" + e.NAME + "_" + c.getTicker() + "("
                                + amount + ")" + gl, CURRENT_INSTANCE)
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

    public boolean buySellFee(LDate date, BigDecimal amount, BigDecimal cost, BigDecimal fee, BigDecimal feeUnit, String exchange, String currency, String feeCur) {
        try {
            cost = cost.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.abs();
            feeUnit = feeUnit.abs();
            BigDecimal feeVal = fee.multiply(feeUnit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP);
            Exchange e = CURRENT_INSTANCE.EXCHANGES.getElement(exchange);
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            LCurrency f = CURRENT_INSTANCE.getLCurrency(feeCur);
            if (e == null || c == null || f == null) {
                return false;
            }
            amount = amount.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.setScale(f.getPlaces(), RoundingMode.HALF_UP);
            if (!e.supports(c) || !e.supportsFee(f) || f == CURRENT_INSTANCE.main) {
                return false;
            }
            String acc = Account.cryptoName;
            if (c instanceof LStock) {
                acc = Account.stockName;
            } else if (c instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (c.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                //buy
                entry.insert(
                        date,
                        e.NAME,
                        c.toString(),
                        currency + " Purchase",
                        new LAccountSet(
                                "D!" + acc + "("
                                        + cost.subtract(feeVal) + "), D!" + Account.tradExpName + "("
                                        + cost + "), C!" + e.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                        + cost + "), C!" + Account.portfolioName + "("
                                        + cost.subtract(feeVal) + "), T!" + e.NAME + "_" + c.getTicker() + "("
                                        + amount + "), T!" + e.NAME + "_" + f.getTicker() + "("
                                        + fee.multiply(BigDecimal.valueOf(-1)) + ")", CURRENT_INSTANCE)
                );
                entry.addLedgerMeta(CURRENT_INSTANCE.main, c, cost.multiply(BigDecimal.valueOf(-1)), amount, cost);
                entry.addLedgerMeta(f, CURRENT_INSTANCE.main, fee.multiply(BigDecimal.valueOf(-1)), BigDecimal.ZERO, feeVal);
                addTransaction(entry);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                //sell
                Position temp = getPosition(c);
                ArrayList<OrderBookEntry> orders = temp.change(entry.getUUID(), date, c, amount, cost);
                BigDecimal profitSR = BigDecimal.ZERO, profitLR = BigDecimal.ZERO;
                for (OrderBookEntry order : orders) {
                    if (order.longTerm()) {
                        profitLR = profitLR.add(order.profit());
                    } else {
                        profitSR = profitSR.add(order.profit());
                    }
                }
                String gl = "";
                if (profitSR.compareTo(BigDecimal.ZERO) != 0) {
                    gl += ", G!";
                    if (c instanceof LStock) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgStockName : Account.clStockName;
                    } else if (c instanceof LInventory) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgInventoryName : Account.clInventoryName;
                    } else {
                        if (c.isFiat()) {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgFiatName : Account.clFiatName;
                        } else {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgCryptoName : Account.clCryptoName;
                        }
                    }
                    gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                if (profitLR.compareTo(BigDecimal.ZERO) != 0) {
                    gl += ", G!";
                    if (c instanceof LStock) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltStockName : Account.clltStockName;
                    } else if (c instanceof LInventory) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltInventoryName : Account.clltInventoryName;
                    } else {
                        if (c.isFiat()) {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltFiatName : Account.clltFiatName;
                        } else {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltCryptoName : Account.clltCryptoName;
                        }
                    }
                    gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                entry.insert(
                        date,
                        e.NAME,
                        c.toString(),
                        currency + " Sale",
                        new LAccountSet("D!" + e.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                + cost + "), D!" + Account.portfolioName + "("
                                + cost.add(feeVal) + "), C!" + acc + "("
                                + cost.add(feeVal) + "), C!" + Account.tradIncName + "("
                                + cost + "), T!" + e.NAME + "_" + c.getTicker() + "("
                                + amount + "), T!" + e.NAME + "_" + f.getTicker() + "("
                                + fee.multiply(BigDecimal.valueOf(-1)) + ")" + gl, CURRENT_INSTANCE)
                );
                entry.addLedgerMeta(c, CURRENT_INSTANCE.main, amount, cost, cost);
                entry.addLedgerMeta(f, CURRENT_INSTANCE.main, fee.multiply(BigDecimal.valueOf(-1)), BigDecimal.ZERO, feeVal);
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
            unit = unit.abs();
            BigDecimal cost = amount.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            Exchange e = CURRENT_INSTANCE.EXCHANGES.getElement(exchange);
            if (c == null || e == null) {
                return false;
            }
            if (!e.supports(c)) {
                return false;
            }
            amount = amount.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            boolean s = e.stakes(c) > -1;
            String acc = Account.cryptoName;
            if (c instanceof LStock) {
                acc = Account.stockName;
            } else if (c instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (c.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                entry.insert(
                        date,
                        exchange,
                        currency,
                        description,
                        new LAccountSet("D!" + acc + "("
                                + cost + "), C!" + Account.portfolioName + "("
                                + cost + "), T!" + e.NAME + "_" + c.getTicker() + (s ? "_S" : "") + "("
                                + amount + ")", CURRENT_INSTANCE)
                );
                entry.addLedgerMeta(CURRENT_INSTANCE.main, c, BigDecimal.ZERO, amount, cost);
                addTransaction(entry);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                entry.insert(
                        date,
                        exchange,
                        currency,
                        description,
                        new LAccountSet("D!" + Account.portfolioName + "("
                                + cost + "), C!" + acc + "("
                                + cost + "), T!" + e.NAME + "_" + c.getTicker() + "("
                                + amount + ")", CURRENT_INSTANCE)
                );
                entry.addLedgerMeta(c, CURRENT_INSTANCE.main, amount, BigDecimal.ZERO, cost);
                addTransaction(entry);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean minInc(LDate date, String description, BigDecimal amount, BigDecimal unit, String account) {
        try {
            unit = unit.abs();
            Account a = CURRENT_INSTANCE.ACCOUNTS.getElement(account);
            if (a == null) {
                return false;
            }
            LCurrency c = a.getCurrency();
            if (c == null) {
                return false;
            }
            LCurrency nw = c.getRoot();
            if (nw == null) {
                return false;
            }
            amount = amount.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            BigDecimal adjAmount = CURRENT_INSTANCE.convert(amount, c, nw);
            BigDecimal cost = adjAmount.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            String acc = Account.cryptoName;
            if (c instanceof LStock) {
                acc = Account.stockName;
            } else if (c instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (c.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                entry.insert(
                        date,
                        c.getName(),
                        c.toString(),
                        description,
                        new LAccountSet("D!" + acc + "("
                                + cost + "), C!" + Account.portfolioName + "("
                                + cost + "), T!" + a.getName() + "(" + amount + ")"
                                + ", G!" + Account.selfIncName + "(" + cost + ")", CURRENT_INSTANCE)
                );
                entry.addLedgerMeta(CURRENT_INSTANCE.main, nw, cost.multiply(BigDecimal.valueOf(-1)), adjAmount, cost);
                addTransaction(entry);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean coinTransfer(LDate date, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal unit,
                                String fromExchange, String toExchange, String currency) {
        try {
            fromAmount = fromAmount.abs().multiply(BigDecimal.valueOf(-1));
            toAmount = toAmount.abs();
            unit = unit.abs();
            BigDecimal fee = toAmount.add(fromAmount);
            BigDecimal cost = fee.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            LCurrency c = CURRENT_INSTANCE.getLCurrency(currency);
            Exchange ef = CURRENT_INSTANCE.EXCHANGES.getElement(fromExchange);
            Exchange et = CURRENT_INSTANCE.EXCHANGES.getElement(toExchange);
            if (c == null || ef == null || et == null) {
                return false;
            }
            if (!ef.supports(c) || !et.supports(c)) {
                return false;
            }
            fromAmount = fromAmount.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            toAmount = toAmount.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.setScale(c.getPlaces(), RoundingMode.HALF_UP);
            String acc = Account.cryptoName;
            if (c instanceof LStock) {
                acc = Account.stockName;
            } else if (c instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (c.isFiat()) {
                acc = Account.fiatName;
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
                    ent,
                    c.toString(),
                    c + " Transfer",
                    new LAccountSet("D!" + Account.portfolioName + "("
                            + cost + "), C!" + acc + "("
                            + cost + "), T!" + ef.NAME + "_" + c.getTicker() + "("
                            + fromAmount + "), T!" + et.NAME + "_" + c.getTicker() + "("
                            + toAmount + ")", CURRENT_INSTANCE)
            );
            entry.addLedgerMeta(c, CURRENT_INSTANCE.main, fee, BigDecimal.ZERO, cost);
            addTransaction(entry);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean tokenTransfer(LDate date, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal fee, BigDecimal fromUnit,
                                 BigDecimal feeUnit, String fromExchange, String toExchange, String transCur, String costCur) {
        try {
            fromAmount = fromAmount.abs().multiply(BigDecimal.valueOf(-1));
            fee = fee.abs().multiply(BigDecimal.valueOf(-1));
            toAmount = toAmount.abs();
            fromUnit = fromUnit.abs();
            feeUnit = feeUnit.abs();
            BigDecimal transitLoss = toAmount.add(fromAmount);
            BigDecimal cost1 = transitLoss.multiply(fromUnit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            BigDecimal cost2 = fee.multiply(feeUnit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            BigDecimal cost = cost1.add(cost2);
            LCurrency tc = CURRENT_INSTANCE.getLCurrency(transCur);
            LCurrency cc = CURRENT_INSTANCE.getLCurrency(costCur);
            Exchange ef = CURRENT_INSTANCE.EXCHANGES.getElement(fromExchange);
            Exchange et = CURRENT_INSTANCE.EXCHANGES.getElement(toExchange);
            if (tc == null || cc == null || ef == null || et == null) {
                return false;
            }
            if (!ef.supports(tc) || !ef.supports(cc) || !et.supports(tc)) {
                return false;
            }
            fromAmount = fromAmount.setScale(tc.getPlaces(), RoundingMode.HALF_UP);
            toAmount = toAmount.setScale(tc.getPlaces(), RoundingMode.HALF_UP);
            transitLoss = transitLoss.setScale(tc.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.setScale(cc.getPlaces(), RoundingMode.HALF_UP);
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
                    ent,
                    tc + ", " + cc,
                    tc + " Transfer",
                    new LAccountSet("D!" + Account.portfolioName + "("
                            + cost + "), C!" + Account.cryptoName + "("
                            + cost + "), T!" + ef.NAME + "_" + tc.getTicker() + "("
                            + fromAmount + "), T!" + ef.NAME + "_" + cc.getTicker() + "("
                            + fee + "), T!" + et.NAME + "_" + tc.getTicker() + "("
                            + toAmount + ")", CURRENT_INSTANCE)
            );
            if (transitLoss.compareTo(BigDecimal.ZERO) != 0) {
                entry.addLedgerMeta(tc, CURRENT_INSTANCE.main, transitLoss, BigDecimal.ZERO, cost1.abs());
            }
            entry.addLedgerMeta(cc, CURRENT_INSTANCE.main, fee, BigDecimal.ZERO, cost2.abs());
            addTransaction(entry);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean trade(LDate date, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal unit,
                         String exchange, String fromCur, String toCur) {
        try {
            fromAmount = fromAmount.abs().multiply(BigDecimal.valueOf(-1));
            toAmount = toAmount.abs();
            unit = unit.abs();
            BigDecimal cost = fromAmount.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
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
            fromAmount = fromAmount.setScale(fc.getPlaces(), RoundingMode.HALF_UP);
            toAmount = toAmount.setScale(tc.getPlaces(), RoundingMode.HALF_UP);
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            Position temp = getPosition(fc);
            ArrayList<OrderBookEntry> orders = temp.change(entry.getUUID(), date, fc, fromAmount, cost);
            BigDecimal profitSR = BigDecimal.ZERO, profitLR = BigDecimal.ZERO;
            for (OrderBookEntry order : orders) {
                if (order.longTerm()) {
                    profitLR = profitLR.add(order.profit());
                } else {
                    profitSR = profitSR.add(order.profit());
                }
            }
            String gl = "";
            if (profitSR.compareTo(BigDecimal.ZERO) != 0) {
                gl += ", G!";
                if (fc instanceof LStock) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgStockName : Account.clStockName;
                } else if (fc instanceof LInventory) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgInventoryName : Account.clInventoryName;
                } else {
                    if (fc.isFiat()) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgFiatName : Account.clFiatName;
                    } else {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgCryptoName : Account.clCryptoName;
                    }
                }
                gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
            }
            if (profitLR.compareTo(BigDecimal.ZERO) != 0) {
                gl += ", G!";
                if (fc instanceof LStock) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltStockName : Account.clltStockName;
                } else if (fc instanceof LInventory) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltInventoryName : Account.clltInventoryName;
                } else {
                    if (fc.isFiat()) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltFiatName : Account.clltFiatName;
                    } else {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltCryptoName : Account.clltCryptoName;
                    }
                }
                gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
            }
            entry.insert(
                    date,
                    e.NAME,
                    fc + ", " + tc,
                    "Trade (" + fc + " -> " + tc + ")",
                    new LAccountSet("T!" + e.NAME + "_" + fc.getTicker() + "("
                            + fromAmount + "), T!" + e.NAME + "_" + tc.getTicker() + "("
                            + toAmount + ")" + gl, CURRENT_INSTANCE)
            );
            entry.addLedgerMeta(fc, tc, fromAmount, toAmount, cost);
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
                for (AccountWrapper wrapper : entry.getAccounts()) {
                    if (wrapper.COLUMN == AWColumn.TRACKER) {
                        String c = wrapper.ACCOUNT.getCurrency().toUnifiedString();
                        curAg.add(c, wrapper.VALUE);
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
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged or missing ledger metadata for entry: " + Long.toUnsignedString(entry.getUUID()));
                        }
                    } else if (curAg.get(cur).compareTo(ledgAg.get(cur)) != 0) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged ledger metadata for entry: " + Long.toUnsignedString(entry.getUUID()));
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Disparity: " + cur + ": " + curAg.get(cur) + " / " + ledgAg.get(cur));
                    }
                }
            }
        }
    }

    public void checkCG() {
        ArrayList<String> gains = new ArrayList<>(Arrays.asList(
                Account.cgStockName, Account.cgInventoryName, Account.cgFiatName, Account.cgCryptoName,
                Account.cgltStockName, Account.cgltInventoryName, Account.cgltFiatName, Account.cgltCryptoName
        )), losses = new ArrayList<>(Arrays.asList(
                Account.clStockName, Account.clInventoryName, Account.clFiatName, Account.clCryptoName,
                Account.clltStockName, Account.clltInventoryName, Account.clltFiatName, Account.clltCryptoName
        ));
        Aggregation<Long> map = new Aggregation<>();
        for (OrderBookEntry entry : getOrderBook()) {
            map.add(entry.END_REF, entry.profit());
        }
        for (Long uuid : map.keySet()) {
            TransactionEntry entry = getTransactionEntry(uuid);
            boolean flag = false;
            BigDecimal total = BigDecimal.ZERO;
            if (entry.hasGhostAccounts()) {
                for (AccountWrapper wrapper : entry.getAccounts()) {
                    if (wrapper.COLUMN == AWColumn.GHOST) {
                        if (gains.contains(wrapper.ACCOUNT.getName())) {
                            total = total.add(wrapper.VALUE);
                            flag = true;
                        } else if (losses.contains(wrapper.ACCOUNT.getName())) {
                            total = total.subtract(wrapper.VALUE);
                            flag = true;
                        }
                    }
                }
            }
            if (flag) {
                if (CURRENT_INSTANCE.$(total).compareTo(CURRENT_INSTANCE.$(map.get(uuid))) != 0) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Mismatched Capital Gain/Loss: " + Long.toUnsignedString(uuid));
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Expected:" + CURRENT_INSTANCE.$(map.get(uuid))
                            + ", Found: " + CURRENT_INSTANCE.$(total)
                    );
                }
            }
            if (!flag && !entry.equals(getPrior())) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Missing Capital Gain/Loss: " + Long.toUnsignedString(entry.getUUID()));
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Expected: " + CURRENT_INSTANCE.$(map.get(uuid)));
            }
        }
    }
}
