package com.donny.dendrofinance.data;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.account.AccountWrapper;
import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.capsules.meta.*;
import com.donny.dendrofinance.capsules.totals.OrderBookEntry;
import com.donny.dendrofinance.capsules.totals.Position;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LInventory;
import com.donny.dendrofinance.currency.LStock;
import com.donny.dendrofinance.data.database.*;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.util.Aggregation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataHandler {
    protected final ProgramInstance CURRENT_INSTANCE;
    protected final ArrayList<String> BUDGET_TYPES;
    public final DatabaseHandler DATABASE;
    public final BudgetHandler BUDGETS;
    public final StatesHandler STATES;
    public final TransactionHandler TRANSACTIONS;
    public final TemplateHandler TEMPLATES;
    public boolean budgetTypesChanged = false;

    public DataHandler(ProgramInstance curInst) {
        CURRENT_INSTANCE = curInst;
        DATABASE = new DatabaseHandler(CURRENT_INSTANCE);
        BUDGETS = new BudgetHandler(CURRENT_INSTANCE, DATABASE);
        TEMPLATES = new TemplateHandler(CURRENT_INSTANCE, DATABASE);
        STATES = new StatesHandler(CURRENT_INSTANCE, DATABASE);
        TRANSACTIONS = new TransactionHandler(CURRENT_INSTANCE, DATABASE);
        BUDGET_TYPES = new ArrayList<>();
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "DataHandler Initiated");
    }

    //Budget type operations
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

    //state formation
    public void createStates() {
        LDate now = LDate.now(CURRENT_INSTANCE);
        LDate dawn = new LDate(TRANSACTIONS.getMinDate(), CURRENT_INSTANCE);
        switch (CURRENT_INSTANCE.freq) {
            case NEVER -> {
            }
            case ANNUAL -> {
                for (int y = dawn.getYear() + 1; y < now.getYear() + 1; y++) {
                    LDate check = new LDate(y, 1, 1, 0, 0, 0, 0, CURRENT_INSTANCE);
                    long c = check.getTime() - 1;
                    if (STATES.get(check.getTime() - 1) == null) {
                        StateCapsule capsule = new StateCapsule(new LDate(c, CURRENT_INSTANCE), CURRENT_INSTANCE);
                        if (capsule.hasContents()) {
                            STATES.add(capsule, ImportHandler.ImportMode.OVERWRITE);
                            CURRENT_INSTANCE.LOG_HANDLER.info(getClass(), "New State added automatically: " + new LDate(c, CURRENT_INSTANCE));
                        }
                    }
                }
            }
            case QUARTERLY -> {
                int y = dawn.getYear(), m = dawn.getMonth();
                String q = dawn.getQuarter();
                switch (q) {
                    case "Q1" -> q = "Q2";
                    case "Q2" -> q = "Q3";
                    case "Q3" -> q = "Q4";
                    case "Q4" -> {
                        q = "Q1";
                        y++;
                    }
                }
                switch (q) {
                    case "Q1" -> m = 1;
                    case "Q2" -> m = 4;
                    case "Q3" -> m = 7;
                    case "Q4" -> m = 10;
                }
                LDate check = new LDate(y, m, 1, 0, 0, 0, 0, CURRENT_INSTANCE);
                while (check.compareTo(now) < 0) {
                    long c = check.getTime() - 1;
                    if (STATES.get(check.getTime() - 1) == null) {
                        StateCapsule capsule = new StateCapsule(new LDate(c, CURRENT_INSTANCE), CURRENT_INSTANCE);
                        if (capsule.hasContents()) {
                            STATES.add(capsule, ImportHandler.ImportMode.OVERWRITE);
                        }
                    }
                    m += 3;
                    if (m > 12) {
                        m -= 12;
                        y++;
                    }
                    check = new LDate(y, m, 1, 0, 0, 0, 0, CURRENT_INSTANCE);
                }
            }
            case MONTHLY -> {
                int y = dawn.getYear(), m = dawn.getMonth();
                m++;
                if (m == 13) {
                    m = 1;
                    y++;
                }
                LDate check = new LDate(y, m, 1, 0, 0, 0, 0, CURRENT_INSTANCE);
                while (check.compareTo(now) < 0) {
                    long c = check.getTime() - 1;
                    if (STATES.get(check.getTime() - 1) == null) {
                        StateCapsule capsule = new StateCapsule(new LDate(c, CURRENT_INSTANCE), CURRENT_INSTANCE);
                        if (capsule.hasContents()) {
                            STATES.add(capsule, ImportHandler.ImportMode.OVERWRITE);
                        }
                    }
                    m++;
                    if (m == 13) {
                        m = 1;
                        y++;
                    }
                    check = new LDate(y, m, 1, 0, 0, 0, 0, CURRENT_INSTANCE);
                }
            }
        }
    }

    //meta aggregation
    public ArrayList<CheckMetadata> getChecks(LDate start, LDate end) {
        ArrayList<CheckMetadata> meta = new ArrayList<>();
        for (TransactionCapsule capsule : TRANSACTIONS.getRange(start, end)) {
            if (capsule.hasMeta("check")) {
                meta.addAll(capsule.getCheckMetadata());
            }
        }
        return meta;
    }

    public ArrayList<CheckMetadata> getOutstandingChecks(LDate start, LDate end) {
        ArrayList<CheckMetadata> out = new ArrayList<>();
        for (CheckMetadata check : getChecks(start, end)) {
            if (check.isOutstanding()) {
                out.add(check);
            }
        }
        return out;
    }

    public ArrayList<LedgerMetadata> getLedgers(LDate start, LDate end) {
        ArrayList<LedgerMetadata> meta = new ArrayList<>();
        for (TransactionCapsule capsule : TRANSACTIONS.getRange(start, end)) {
            if (capsule.hasMeta("ledger")) {
                meta.addAll(capsule.getLedgerMeta());
            }
        }
        return meta;
    }

    public ArrayList<LedgerMetadata> getLedgers(LDate start, LDate end, LCurrency cur) {
        ArrayList<LedgerMetadata> range = getLedgers(start, end);
        ArrayList<LedgerMetadata> out = new ArrayList<>();
        for (LedgerMetadata meta : range) {
            if (meta.FROM.matches(cur) || meta.TO.matches(cur)) {
                out.add(meta);
            }
        }
        return out;
    }

    //transactions + states
    public Aggregation<Account> accountsAsOf(LDate date) {
        StateCapsule baseline = STATES.getBefore(date.getTime());
        Aggregation<Account> accounts = new Aggregation<>();
        if (baseline != null) {
            JsonObject acc = baseline.exportAccounts();
            for (String key : acc.getFields()) {
                accounts.add(CURRENT_INSTANCE.ACCOUNTS.getElement(key), acc.getDecimal(key).decimal);
            }
            for (TransactionCapsule capsule : TRANSACTIONS.getRange(baseline.getDate(), date)) {
                for (AccountWrapper wrapper : capsule.getAccounts()) {
                    accounts.add(wrapper.ACCOUNT, wrapper.getAlphaProcessed());
                }
            }
        } else {
            for (TransactionCapsule capsule : TRANSACTIONS.getRange(date)) {
                for (AccountWrapper wrapper : capsule.getAccounts()) {
                    accounts.add(wrapper.ACCOUNT, wrapper.getAlphaProcessed());
                }
            }
        }
        return accounts;
    }

    public BigDecimal accountAsOf(Account account, LDate date) {
        return accountsAsOf(date).get(account);
    }

    public BigDecimal accountAsOf(String name, LDate date) {
        return accountAsOf(CURRENT_INSTANCE.ACCOUNTS.getElement(name), date);
    }

    public HashMap<LCurrency, BigDecimal> pricesAsOf(LCurrency cur, LDate date) {
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "Price-get started");
        HashMap<Account, BigDecimal> acc = accountsAsOf(LDate.endDay(date));
        Aggregation<LCurrency> assets = new Aggregation<>();
        ArrayList<LCurrency> significant = new ArrayList<>();
        for (Account a : acc.keySet()) {
            assets.add(a.getCurrency(), acc.get(a));
        }
        for (LCurrency c : assets.keySet()) {
            if (c.significant(assets.get(c))) {
                significant.add(c);
            }
        }
        LDate now = LDate.now(CURRENT_INSTANCE);
        HashMap<LCurrency, BigDecimal> out;
        if (date.getYear() == now.getYear() && date.getMonth() == now.getMonth() && date.getDay() == now.getDay()) {
            out = CURRENT_INSTANCE.getAllConversions(significant, cur);
        } else {
            out = CURRENT_INSTANCE.getAllConversions(significant, cur, date);
        }
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "Price get finished");
        return out;
    }

    public ArrayList<AssetMetadata> assetsAsOf(LDate date) {
        StateCapsule baseline = STATES.getBefore(date.getTime());
        ArrayList<AssetMetadata> assets = new ArrayList<>();
        ArrayList<TransactionCapsule> range;
        if (baseline != null) {
            assets.addAll(baseline.getAssets());
            range = TRANSACTIONS.getRange(baseline.getDate(), date);
        } else {
            range = TRANSACTIONS.getRange(date);
        }
        for (TransactionCapsule capsule : range) {
            if (capsule.hasMeta("asset")) {
                assets.addAll(capsule.getAssetMeta());
            }
            if (capsule.hasMeta("asset-change")) {
                for (AssetChangeMetadata change : capsule.getAssetChangeMeta()) {
                    for (AssetMetadata sup : assets) {
                        if (sup.NAME.equals(change.NAME)) {
                            sup.EVENTS.add(change);
                        }
                    }
                }
            }
        }
        ArrayList<AssetMetadata> out = new ArrayList<>();
        for (AssetMetadata meta : assets) {
            if (meta.getTotalCount().compareTo(BigDecimal.ZERO) >= 0) {
                out.add(meta);
            }
        }
        return out;
    }

    public AssetMetadata assetAsOf(String name, LDate date) {
        for (AssetMetadata total : assetsAsOf(date)) {
            if (total.NAME.equals(name)) {
                return total;
            }
        }
        return null;
    }

    public ArrayList<LoanMetadata> loansAsOf(LDate date) {
        StateCapsule baseline = STATES.getBefore(date.getTime());
        ArrayList<LoanMetadata> loans = new ArrayList<>();
        ArrayList<TransactionCapsule> range;
        if (baseline != null) {
            loans.addAll(baseline.getLoans());
            range = TRANSACTIONS.getRange(baseline.getDate(), date);
        } else {
            range = TRANSACTIONS.getRange(date);
        }
        for (TransactionCapsule capsule : range) {
            if (capsule.hasMeta("loan")) {
                loans.addAll(capsule.getLoanMeta());
            }
            if (capsule.hasMeta("loan-change")) {
                for (LoanChangeMetadata change : capsule.getLoanChangeMeta()) {
                    for (LoanMetadata sup : loans) {
                        if (sup.NAME.equals(change.NAME)) {
                            sup.EVENTS.add(change);
                        }
                    }
                }
            }
        }
        ArrayList<LoanMetadata> out = new ArrayList<>();
        for (LoanMetadata meta : loans) {
            if (meta.isCurrent()) {
                out.add(meta);
            }
        }
        return out;
    }

    public LoanMetadata loanAsOf(String name, LDate date) {
        for (LoanMetadata total : loansAsOf(date)) {
            if (total.NAME.equals(name)) {
                return total;
            }
        }
        return null;
    }

    public ArrayList<OrderBookEntry> getOrderBook(LDate start, LDate end) {
        ArrayList<OrderBookEntry> orderBook = new ArrayList<>();
        ArrayList<Position> positions = getPositions(start);
        for (TransactionCapsule capsule : TRANSACTIONS.getRange(start, end)) {
            if (capsule.hasMeta("ledger")) {
                for (LedgerMetadata meta : capsule.getLedgerMeta()) {
                    boolean flag = true, auxFlag = true;
                    if (meta.FROM.equals(CURRENT_INSTANCE.main)) {
                        LCurrency a = meta.TO;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                orderBook.addAll(p.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.FROM_AMNT));
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.FROM_AMNT));
                            positions.add(newP);
                        }
                    } else if (meta.TO.equals(CURRENT_INSTANCE.main)) {
                        LCurrency a = meta.FROM;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                orderBook.addAll(p.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.TO_AMNT));
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            Position newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.TO_AMNT));
                            positions.add(newP);
                        }
                    } else {
                        LCurrency a = meta.TO, b = meta.FROM;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                orderBook.addAll(p.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                                flag = false;
                            }
                            if (p.ASSET.equals(b)) {
                                orderBook.addAll(p.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
                                auxFlag = false;
                            }
                        }
                        if (flag && auxFlag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                            positions.add(newP);
                            newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
                            positions.add(newP);
                        } else if (flag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue()));
                            positions.add(newP);
                        } else if (auxFlag) {
                            Position newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            orderBook.addAll(newP.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE));
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
        return orderBook;
    }

    public ArrayList<OrderBookEntry> getOrderBook(LDate start, LDate end, LCurrency asset) {
        ArrayList<OrderBookEntry> out = new ArrayList<>();
        for (OrderBookEntry entry : getOrderBook(start, end)) {
            if (entry.ASSET.equals(asset)) {
                out.add(entry);
            }
        }
        return out;
    }

    public ArrayList<Position> getPositions(LDate date) {
        ArrayList<Position> positions = new ArrayList<>();
        StateCapsule baseline = STATES.getBefore(date.getTime());
        ArrayList<TransactionCapsule> range;
        if (baseline != null) {
            positions.addAll(baseline.getPositions());
            range = TRANSACTIONS.getRange(baseline.getDate(), date);
        } else {
            range = TRANSACTIONS.getRange(date);
        }
        for (TransactionCapsule capsule : range) {
            if (capsule.hasMeta("ledger")) {
                for (LedgerMetadata meta : capsule.getLedgerMeta()) {
                    boolean flag = true, auxFlag = true;
                    if (meta.FROM.equals(CURRENT_INSTANCE.main)) {
                        LCurrency a = meta.TO;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                p.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.FROM_AMNT);
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            newP.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.FROM_AMNT);
                            positions.add(newP);
                        }
                    } else if (meta.TO.equals(CURRENT_INSTANCE.main)) {
                        LCurrency a = meta.FROM;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                p.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.TO_AMNT);
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            Position newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            newP.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.TO_AMNT);
                            positions.add(newP);
                        }
                    } else {
                        LCurrency a = meta.TO, b = meta.FROM;
                        for (Position p : positions) {
                            if (p.ASSET.equals(a)) {
                                p.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue());
                                flag = false;
                            }
                            if (p.ASSET.equals(b)) {
                                p.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE);
                                auxFlag = false;
                            }
                        }
                        if (flag && auxFlag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            newP.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue());
                            positions.add(newP);
                            newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            newP.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE);
                            positions.add(newP);
                        } else if (flag) {
                            Position newP = new Position(meta.TO, CURRENT_INSTANCE);
                            newP.change(capsule.getUUID(), meta.DATE, meta.TO, meta.TO_AMNT, meta.negativeMainValue());
                            positions.add(newP);
                        } else if (auxFlag) {
                            Position newP = new Position(meta.FROM, CURRENT_INSTANCE);
                            newP.change(capsule.getUUID(), meta.DATE, meta.FROM, meta.FROM_AMNT, meta.MAIN_VALUE);
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
        return positions;
    }

    public Position getPosition(LCurrency asset, LDate date) {
        for (Position p : getPositions(date)) {
            if (p.ASSET.equals(asset)) {
                return p;
            }
        }
        return null;
    }

    public boolean purchaseSale(LDate date, BigDecimal amount, BigDecimal cost, Exchange exchange, LCurrency currency) {
        try {
            cost = cost.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP);
            if (exchange == null || currency == null) {
                return false;
            }
            if (!exchange.supports(currency)) {
                return false;
            }
            amount = amount.setScale(currency.getPlaces(), RoundingMode.HALF_UP);
            String acc = Account.cryptoName;
            if (currency instanceof LStock) {
                acc = Account.stockName;
            } else if (currency instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (currency.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                capsule.insert(
                        date,
                        exchange.NAME,
                        currency.toString(),
                        currency + " Purchase",
                        new LAccountSet(
                                "D!" + acc + "("
                                        + cost + "), D!" + Account.tradExpName + "("
                                        + cost + "), C!" + exchange.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                        + cost + "), C!" + Account.portfolioName + "("
                                        + cost + "), T!" + exchange.NAME + "_" + currency.getTicker() + "("
                                        + amount + ")", CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(CURRENT_INSTANCE.main, currency, cost.multiply(BigDecimal.valueOf(-1)), amount, cost);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                Position temp = getPosition(currency, date);
                ArrayList<OrderBookEntry> orders = temp.change(capsule.getUUID(), date, currency, amount, cost);
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
                    if (currency instanceof LStock) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgStockName : Account.clStockName;
                    } else if (currency instanceof LInventory) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgInventoryName : Account.clInventoryName;
                    } else {
                        if (currency.isFiat()) {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgFiatName : Account.clFiatName;
                        } else {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgCryptoName : Account.clCryptoName;
                        }
                    }
                    gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                if (profitLR.compareTo(BigDecimal.ZERO) != 0) {
                    gl += ", G!";
                    if (currency instanceof LStock) {
                        gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltStockName : Account.clltStockName;
                    } else if (currency instanceof LInventory) {
                        gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltInventoryName : Account.clltInventoryName;
                    } else {
                        if (currency.isFiat()) {
                            gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltFiatName : Account.clltFiatName;
                        } else {
                            gl += profitLR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltCryptoName : Account.clltCryptoName;
                        }
                    }
                    gl += "(" + profitLR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                capsule.insert(
                        date,
                        exchange.NAME,
                        currency.toString(),
                        currency + " Sale",
                        new LAccountSet("D!" + exchange.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                + cost + "), D!" + Account.portfolioName + "("
                                + cost + "), C!" + acc + "("
                                + cost + "), C!" + Account.tradIncName + "("
                                + cost + "), T!" + exchange.NAME + "_" + currency.getTicker() + "("
                                + amount + ")" + gl, CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(currency, CURRENT_INSTANCE.main, amount, cost, cost);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean purchaseSaleFee(LDate date, BigDecimal amount, BigDecimal cost, BigDecimal fee, BigDecimal feeUnit, Exchange exchange, LCurrency currency, LCurrency feeCur) {
        try {
            cost = cost.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.abs();
            feeUnit = feeUnit.abs();
            BigDecimal feeVal = fee.multiply(feeUnit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP);
            if (exchange == null || currency == null || feeCur == null) {
                return false;
            }
            amount = amount.setScale(currency.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.setScale(feeCur.getPlaces(), RoundingMode.HALF_UP);
            if (!exchange.supports(currency) || !exchange.supportsFee(feeCur) || feeCur == CURRENT_INSTANCE.main) {
                return false;
            }
            String acc = Account.cryptoName;
            if (currency instanceof LStock) {
                acc = Account.stockName;
            } else if (currency instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (currency.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                //buy
                capsule.insert(
                        date,
                        exchange.NAME,
                        currency.toString(),
                        currency + " Purchase",
                        new LAccountSet(
                                "D!" + acc + "("
                                        + cost.subtract(feeVal) + "), D!" + Account.tradExpName + "("
                                        + cost + "), C!" + exchange.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                        + cost + "), C!" + Account.portfolioName + "("
                                        + cost.subtract(feeVal) + "), T!" + exchange.NAME + "_" + currency.getTicker() + "("
                                        + amount + "), T!" + exchange.NAME + "_" + feeCur.getTicker() + "("
                                        + fee.multiply(BigDecimal.valueOf(-1)) + ")", CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(CURRENT_INSTANCE.main, currency, cost.multiply(BigDecimal.valueOf(-1)), amount, cost);
                capsule.addLedgerMeta(feeCur, CURRENT_INSTANCE.main, fee.multiply(BigDecimal.valueOf(-1)), BigDecimal.ZERO, feeVal);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                //sell
                Position temp = getPosition(currency, date);
                ArrayList<OrderBookEntry> orders = temp.change(capsule.getUUID(), date, currency, amount, cost);
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
                    if (currency instanceof LStock) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgStockName : Account.clStockName;
                    } else if (currency instanceof LInventory) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgInventoryName : Account.clInventoryName;
                    } else {
                        if (currency.isFiat()) {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgFiatName : Account.clFiatName;
                        } else {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgCryptoName : Account.clCryptoName;
                        }
                    }
                    gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                if (profitLR.compareTo(BigDecimal.ZERO) != 0) {
                    gl += ", G!";
                    if (currency instanceof LStock) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltStockName : Account.clltStockName;
                    } else if (currency instanceof LInventory) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltInventoryName : Account.clltInventoryName;
                    } else {
                        if (currency.isFiat()) {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltFiatName : Account.clltFiatName;
                        } else {
                            gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltCryptoName : Account.clltCryptoName;
                        }
                    }
                    gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
                }
                capsule.insert(
                        date,
                        exchange.NAME,
                        currency.toString(),
                        currency + " Sale",
                        new LAccountSet("D!" + exchange.NAME + "_" + CURRENT_INSTANCE.main.getTicker() + "("
                                + cost + "), D!" + Account.portfolioName + "("
                                + cost.add(feeVal) + "), C!" + acc + "("
                                + cost.add(feeVal) + "), C!" + Account.tradIncName + "("
                                + cost + "), T!" + exchange.NAME + "_" + currency.getTicker() + "("
                                + amount + "), T!" + exchange.NAME + "_" + feeCur.getTicker() + "("
                                + fee.multiply(BigDecimal.valueOf(-1)) + ")" + gl, CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(currency, CURRENT_INSTANCE.main, amount, cost, cost);
                capsule.addLedgerMeta(feeCur, CURRENT_INSTANCE.main, fee.multiply(BigDecimal.valueOf(-1)), BigDecimal.ZERO, feeVal);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean incomePayment(LDate date, String description, BigDecimal amount, BigDecimal unit, Exchange exchange, LCurrency currency) {
        try {
            unit = unit.abs();
            BigDecimal cost = amount.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            if (currency == null || exchange == null) {
                return false;
            }
            if (!exchange.supports(currency)) {
                return false;
            }
            amount = amount.setScale(currency.getPlaces(), RoundingMode.HALF_UP);
            boolean s = exchange.stakes(currency) > -1;
            String acc = Account.cryptoName;
            if (currency instanceof LStock) {
                acc = Account.stockName;
            } else if (currency instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (currency.isFiat()) {
                acc = Account.fiatName;
            }
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                capsule.insert(
                        date,
                        exchange.NAME,
                        currency.toString(),
                        description,
                        new LAccountSet("D!" + acc + "("
                                + cost + "), C!" + Account.portfolioName + "("
                                + cost + "), T!" + exchange.NAME + "_" + currency.getTicker() + (s ? "_S" : "") + "("
                                + amount + ")", CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(CURRENT_INSTANCE.main, currency, BigDecimal.ZERO, amount, cost);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                capsule.insert(
                        date,
                        exchange.NAME,
                        currency.toString(),
                        description,
                        new LAccountSet("D!" + Account.portfolioName + "("
                                + cost + "), C!" + acc + "("
                                + cost + "), T!" + exchange.NAME + "_" + currency.getTicker() + "("
                                + amount + ")", CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(currency, CURRENT_INSTANCE.main, amount, BigDecimal.ZERO, cost);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean miningIncome(LDate date, String description, BigDecimal amount, BigDecimal unit, Account account) {
        try {
            unit = unit.abs();
            if (account == null) {
                return false;
            }
            LCurrency c = account.getCurrency();
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
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            if (amount.compareTo(BigDecimal.ZERO) > 0) {
                capsule.insert(
                        date,
                        c.getName(),
                        c.toString(),
                        description,
                        new LAccountSet("D!" + acc + "("
                                + cost + "), C!" + Account.portfolioName + "("
                                + cost + "), T!" + account.getName() + "(" + amount + ")"
                                + ", G!" + Account.selfIncName + "(" + cost + ")", CURRENT_INSTANCE)
                );
                capsule.addLedgerMeta(CURRENT_INSTANCE.main, nw, cost.multiply(BigDecimal.valueOf(-1)), adjAmount, cost);
                TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean coinTransfer(LDate date, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal unit,
                                Exchange fromExchange, Exchange toExchange, LCurrency currency) {
        try {
            fromAmount = fromAmount.abs().multiply(BigDecimal.valueOf(-1));
            toAmount = toAmount.abs();
            unit = unit.abs();
            BigDecimal fee = toAmount.add(fromAmount);
            BigDecimal cost = fee.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            if (currency == null || fromExchange == null || toExchange == null) {
                return false;
            }
            if (!fromExchange.supports(currency) || !toExchange.supports(currency)) {
                return false;
            }
            fromAmount = fromAmount.setScale(currency.getPlaces(), RoundingMode.HALF_UP);
            toAmount = toAmount.setScale(currency.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.setScale(currency.getPlaces(), RoundingMode.HALF_UP);
            String acc = Account.cryptoName;
            if (currency instanceof LStock) {
                acc = Account.stockName;
            } else if (currency instanceof LInventory) {
                acc = Account.inventoryName;
            } else if (currency.isFiat()) {
                acc = Account.fiatName;
            }
            String ent;
            if (fromExchange.NAME.equalsIgnoreCase("Personal")) {
                ent = toExchange.NAME;
            } else if (toExchange.NAME.equalsIgnoreCase("Personal")) {
                ent = fromExchange.NAME;
            } else {
                ent = fromExchange.NAME + ", " + toExchange.NAME;
            }
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            capsule.insert(
                    date,
                    ent,
                    currency.toString(),
                    currency + " Transfer",
                    new LAccountSet("D!" + Account.portfolioName + "("
                            + cost + "), C!" + acc + "("
                            + cost + "), T!" + fromExchange.NAME + "_" + currency.getTicker() + "("
                            + fromAmount + "), T!" + toExchange.NAME + "_" + currency.getTicker() + "("
                            + toAmount + ")", CURRENT_INSTANCE)
            );
            capsule.addLedgerMeta(currency, CURRENT_INSTANCE.main, fee, BigDecimal.ZERO, cost);
            TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean tokenTransfer(LDate date, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal fee, BigDecimal fromUnit,
                                 BigDecimal feeUnit, Exchange fromExchange, Exchange toExchange, LCurrency transCur, LCurrency costCur) {
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
            if (transCur == null || costCur == null || fromExchange == null || toExchange == null) {
                return false;
            }
            if (!fromExchange.supports(transCur) || !fromExchange.supports(costCur) || !toExchange.supports(transCur)) {
                return false;
            }
            fromAmount = fromAmount.setScale(transCur.getPlaces(), RoundingMode.HALF_UP);
            toAmount = toAmount.setScale(transCur.getPlaces(), RoundingMode.HALF_UP);
            transitLoss = transitLoss.setScale(transCur.getPlaces(), RoundingMode.HALF_UP);
            fee = fee.setScale(costCur.getPlaces(), RoundingMode.HALF_UP);
            String ent;
            if (fromExchange.NAME.equalsIgnoreCase("Personal")) {
                ent = toExchange.NAME;
            } else if (toExchange.NAME.equalsIgnoreCase("Personal")) {
                ent = fromExchange.NAME;
            } else {
                ent = fromExchange.NAME + ", " + toExchange.NAME;
            }
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            capsule.insert(
                    date,
                    ent,
                    transCur + ", " + costCur,
                    transCur + " Transfer",
                    new LAccountSet("D!" + Account.portfolioName + "("
                            + cost + "), C!" + Account.cryptoName + "("
                            + cost + "), T!" + fromExchange.NAME + "_" + transCur.getTicker() + "("
                            + fromAmount + "), T!" + fromExchange.NAME + "_" + costCur.getTicker() + "("
                            + fee + "), T!" + toExchange.NAME + "_" + transCur.getTicker() + "("
                            + toAmount + ")", CURRENT_INSTANCE)
            );
            if (transitLoss.compareTo(BigDecimal.ZERO) != 0) {
                capsule.addLedgerMeta(transCur, CURRENT_INSTANCE.main, transitLoss, BigDecimal.ZERO, cost1.abs());
            }
            capsule.addLedgerMeta(costCur, CURRENT_INSTANCE.main, fee, BigDecimal.ZERO, cost2.abs());
            TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public boolean trade(LDate date, BigDecimal fromAmount, BigDecimal toAmount, BigDecimal unit,
                         Exchange exchange, LCurrency fromCur, LCurrency toCur) {
        try {
            fromAmount = fromAmount.abs().multiply(BigDecimal.valueOf(-1));
            toAmount = toAmount.abs();
            unit = unit.abs();
            BigDecimal cost = fromAmount.multiply(unit).setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP).abs();
            if (fromCur == null || toCur == null || exchange == null) {
                return false;
            }
            if (fromCur.toString().equals(toCur.toString())) {
                return false;
            }
            if (!exchange.supports(fromCur) || !exchange.supports(toCur)) {
                return false;
            }
            fromAmount = fromAmount.setScale(fromCur.getPlaces(), RoundingMode.HALF_UP);
            toAmount = toAmount.setScale(toCur.getPlaces(), RoundingMode.HALF_UP);
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            Position temp = getPosition(fromCur, date);
            ArrayList<OrderBookEntry> orders = temp.change(capsule.getUUID(), date, fromCur, fromAmount, cost);
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
                if (fromCur instanceof LStock) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgStockName : Account.clStockName;
                } else if (fromCur instanceof LInventory) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgInventoryName : Account.clInventoryName;
                } else {
                    if (fromCur.isFiat()) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgFiatName : Account.clFiatName;
                    } else {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgCryptoName : Account.clCryptoName;
                    }
                }
                gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
            }
            if (profitLR.compareTo(BigDecimal.ZERO) != 0) {
                gl += ", G!";
                if (fromCur instanceof LStock) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltStockName : Account.clltStockName;
                } else if (fromCur instanceof LInventory) {
                    gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltInventoryName : Account.clltInventoryName;
                } else {
                    if (fromCur.isFiat()) {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltFiatName : Account.clltFiatName;
                    } else {
                        gl += profitSR.compareTo(BigDecimal.ZERO) > 0 ? Account.cgltCryptoName : Account.clltCryptoName;
                    }
                }
                gl += "(" + profitSR.abs().setScale(CURRENT_INSTANCE.main.getPlaces(), RoundingMode.HALF_UP) + ")";
            }
            capsule.insert(
                    date,
                    exchange.NAME,
                    fromCur + ", " + toCur,
                    "Trade (" + fromCur + " -> " + toCur + ")",
                    new LAccountSet("T!" + exchange.NAME + "_" + fromCur.getTicker() + "("
                            + fromAmount + "), T!" + exchange.NAME + "_" + toCur.getTicker() + "("
                            + toAmount + ")" + gl, CURRENT_INSTANCE)
            );
            capsule.addLedgerMeta(fromCur, toCur, fromAmount, toAmount, cost);
            TRANSACTIONS.add(capsule, ImportHandler.ImportMode.KEEP);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    //checks
    public void checkLedgers(LDate start, LDate end) {
        for (TransactionCapsule capsule : TRANSACTIONS.getRange(start, end)) {
            if (capsule.hasMeta("ledger")) {
                Aggregation<String> curAg = new Aggregation<>();
                Aggregation<String> ledgAg = new Aggregation<>();
                for (AccountWrapper wrapper : capsule.getAccounts()) {
                    if (wrapper.COLUMN == AWColumn.TRACKER) {
                        String c = wrapper.ACCOUNT.getCurrency().toUnifiedString();
                        curAg.add(c, wrapper.VALUE);
                    }
                }
                for (LedgerMetadata meta : capsule.getLedgerMeta()) {
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
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged or missing ledger metadata for capsule: " + Long.toUnsignedString(capsule.getUUID()));
                        }
                    } else if (curAg.get(cur).compareTo(ledgAg.get(cur)) != 0) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged ledger metadata for capsule: " + Long.toUnsignedString(capsule.getUUID()));
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Disparity: " + cur + ": " + curAg.get(cur) + " / " + ledgAg.get(cur));
                    }
                }
            }
        }
    }

    public void checkCG(LDate start, LDate end) {
        ArrayList<String> gains = new ArrayList<>(Arrays.asList(
                Account.cgStockName, Account.cgInventoryName, Account.cgFiatName, Account.cgCryptoName,
                Account.cgltStockName, Account.cgltInventoryName, Account.cgltFiatName, Account.cgltCryptoName
        )), losses = new ArrayList<>(Arrays.asList(
                Account.clStockName, Account.clInventoryName, Account.clFiatName, Account.clCryptoName,
                Account.clltStockName, Account.clltInventoryName, Account.clltFiatName, Account.clltCryptoName
        ));
        Aggregation<Long> map = new Aggregation<>();
        for (OrderBookEntry entry : getOrderBook(start, end)) {
            map.add(entry.END_REF, entry.profit());
        }
        for (Long uuid : map.keySet()) {
            TransactionCapsule capsule = TRANSACTIONS.get(uuid);
            boolean flag = false;
            BigDecimal total = BigDecimal.ZERO;
            if (capsule.hasGhostAccounts()) {
                for (AccountWrapper wrapper : capsule.getAccounts()) {
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
            if (!flag) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Missing Capital Gain/Loss: " + Long.toUnsignedString(capsule.getUUID()));
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Expected: " + CURRENT_INSTANCE.$(map.get(uuid)));
            }
        }
    }
}
