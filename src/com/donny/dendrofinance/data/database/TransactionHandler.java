package com.donny.dendrofinance.data.database;

import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.SqlEscape;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.types.LDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;

public class TransactionHandler extends TableHandler<Long, TransactionCapsule> {

    public TransactionHandler(ProgramInstance curInst, DatabaseHandler database) {
        super(curInst, database);
    }

    public boolean add(long uuid, LDate dtm, String ent, String itm, String des,
                       LAccountSet acc, JsonObject meta, ImportHandler.ImportMode mode) {
        return add(new TransactionCapsule(uuid, dtm, ent, itm, des, acc, meta, CURRENT_INSTANCE), mode);
    }

    public boolean add(LDate dtm, String ent, String itm, String des,
                       LAccountSet acc, JsonObject meta) {
        TransactionCapsule capsule = new TransactionCapsule(dtm, ent, itm, des, acc, meta, CURRENT_INSTANCE);
        try {
            Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            insert.execute("INSERT INTO TRANSACTIONS VALUES("
                    + capsule.getUUID() + ", "
                    + dtm.getTime() + ", '"
                    + SqlEscape.feed(capsule.getEntity()) + "', '"
                    + SqlEscape.feed(capsule.getItems()) + "', '"
                    + SqlEscape.feed(capsule.getDescription()) + "', '"
                    + SqlEscape.feed(capsule.getAccounts().toString()) + "', '"
                    + SqlEscape.feed(capsule.getMeta().toString()) + "')");
            return true;
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to add transaction to database:\n" + capsule.toFlatString());
            return false;
        }
    }

    @Override
    public boolean add(TransactionCapsule capsule, ImportHandler.ImportMode mode) {
        long uuid = capsule.getUUID();
        long dtm = capsule.getDate().getTime();
        try {
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkUuid(uuid)) {
                switch (mode) {
                    case IGNORE -> {
                        return false;
                    }
                    case KEEP -> uuid = CURRENT_INSTANCE.UNIQUE_HANDLER.generateUUID();
                    case OVERWRITE -> {
                        TransactionCapsule old = get(uuid);
                        if (old.getDate().getTime() == dtm) {
                            try {
                                Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                                insert.execute("""
                                        UPDATE TRANSACTIONS
                                        SET ent = '%s', itm = '%s', des = '%s', acc = '%s', meta = '%s'
                                        WHERE uuid = %s
                                        """.formatted(
                                        SqlEscape.feed(capsule.getEntity()),
                                        SqlEscape.feed(capsule.getItems()),
                                        SqlEscape.feed(capsule.getDescription()),
                                        SqlEscape.feed(capsule.getAccounts().toString()),
                                        SqlEscape.feed(capsule.getMeta().toString()),
                                        uuid
                                ));
                            } catch (SQLException e) {
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to update transaction:\n" + capsule.toFlatString());
                                return false;
                            }
                            return true;
                        } else {
                            int counter = 0;
                            boolean deleted = false;
                            while (counter < 10 && !deleted) {
                                if (delete(uuid)) {
                                    deleted = true;
                                }
                                counter++;
                            }
                            if (!deleted) {
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to query database:");
            return false;
        }
        try {
            Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            insert.execute("INSERT INTO TRANSACTIONS VALUES("
                    + uuid + ", "
                    + dtm + ", '"
                    + SqlEscape.feed(capsule.getEntity()) + "', '"
                    + SqlEscape.feed(capsule.getItems()) + "', '"
                    + SqlEscape.feed(capsule.getDescription()) + "', '"
                    + SqlEscape.feed(capsule.getAccounts().toString()) + "', '"
                    + SqlEscape.feed(capsule.getMeta().toString()) + "')");
            return true;
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to add transaction to database:\n" + capsule.toFlatString());
            return false;
        }
    }

    @Override
    public boolean add(JsonObject object, ImportHandler.ImportMode mode) {
        return add(new TransactionCapsule(object, CURRENT_INSTANCE), mode);
    }

    @Override
    public boolean delete(Long uuid) {
        try {
            Statement delete = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            delete.execute("DELETE FROM TRANSACTIONS WHERE uuid = " + uuid);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteRange(LDate start, LDate end) {
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("DELETE FROM TRANSACTIONS WHERE dtm > " + start.getTime() + " AND dtm < " + end.getTime() + " ORDER BY dtm ASC");
            return true;
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to delete transactions\n" + e);
        }
        return false;
    }

    @Override
    public TransactionCapsule get(Long uuid) {
        try {
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkUuid(uuid)) {
                Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                statement.execute("SELECT * FROM TRANSACTIONS WHERE uuid = " + uuid);
                ResultSet set = statement.getResultSet();
                if (set.next()) {
                    return new TransactionCapsule(
                            set.getLong(1),
                            new LDate(set.getLong(2), CURRENT_INSTANCE),
                            SqlEscape.eat(set.getString(3)),
                            SqlEscape.eat(set.getString(4)),
                            SqlEscape.eat(set.getString(5)),
                            new LAccountSet(SqlEscape.eat(set.getString(6)), CURRENT_INSTANCE),
                            (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(7))),
                            CURRENT_INSTANCE
                    );
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve transaction: " + uuid + "\n" + e);
            return null;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed metadata for transaction: " + uuid + "\n" + e);
            return null;
        }
    }

    public long getMaxDate() {
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT TOP 1 dtm FROM TRANSACTIONS ORDER BY dtm DESC");
            ResultSet set = statement.getResultSet();
            if (set.next()) {
                return set.getLong(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve maximum date:\n" + e);
            return 0;
        }
    }

    public long getMinDate() {
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT TOP 1 dtm FROM TRANSACTIONS ORDER BY dtm ASC");
            ResultSet set = statement.getResultSet();
            if (set.next()) {
                return set.getLong(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve minimum date:\n" + e);
            return 0;
        }
    }

    public ArrayList<TransactionCapsule> getRange(LDate start, LDate end) {
        ArrayList<TransactionCapsule> output = new ArrayList<>();
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT * FROM TRANSACTIONS WHERE dtm > " + start.getTime() + " AND dtm < " + end.getTime() + " ORDER BY dtm ASC");
            ResultSet set = statement.getResultSet();
            while (set.next()) {
                output.add(new TransactionCapsule(
                        set.getLong(1),
                        new LDate(set.getLong(2), CURRENT_INSTANCE),
                        SqlEscape.eat(set.getString(3)),
                        SqlEscape.eat(set.getString(4)),
                        SqlEscape.eat(set.getString(5)),
                        new LAccountSet(SqlEscape.eat(set.getString(6)), CURRENT_INSTANCE),
                        (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(7))),
                        CURRENT_INSTANCE
                ));
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve transactions\n" + e);
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed metadata\n" + e);
        }
        return output;
    }

    public ArrayList<TransactionCapsule> getRange(LDate[] range) {
        return getRange(range[0], range[1]);
    }

    //WARNING!  BE SURE YOU WANT TO DO THIS!
    public ArrayList<TransactionCapsule> getRange(LDate end) {
        ArrayList<TransactionCapsule> output = new ArrayList<>();
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT * FROM TRANSACTIONS WHERE dtm < " + end.getTime() + " ORDER BY dtm ASC");
            ResultSet set = statement.getResultSet();
            while (set.next()) {
                output.add(new TransactionCapsule(
                        set.getLong(1),
                        new LDate(set.getLong(2), CURRENT_INSTANCE),
                        SqlEscape.eat(set.getString(3)),
                        SqlEscape.eat(set.getString(4)),
                        SqlEscape.eat(set.getString(5)),
                        new LAccountSet(SqlEscape.eat(set.getString(6)), CURRENT_INSTANCE),
                        (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(7))),
                        CURRENT_INSTANCE
                ));
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve transactions\n" + e);
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed metadata\n" + e);
        }
        return output;
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
    public ArrayList<TransactionCapsule> hunt(LDate start, LDate end, String search) {
        ArrayList<TransactionCapsule> range = getRange(start, end);
        ArrayList<TransactionCapsule> correct = new ArrayList<>();
        ArrayList<String> tokens = tokenize(search);
        for (TransactionCapsule capsule : range) {
            boolean flag = true;
            OUTER:
            for (String token : tokens) {
                if (!token.equals("")) {
                    switch (token.charAt(0)) {
                        case '$' -> {
                            switch (token) {
                                case "$G" -> {
                                    if (!capsule.hasGhostAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$g" -> {
                                    if (capsule.hasGhostAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$B" -> {
                                    if (!capsule.hasBudgetAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$b" -> {
                                    if (capsule.hasBudgetAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$T" -> {
                                    if (!capsule.hasTrackingAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$t" -> {
                                    if (capsule.hasTrackingAccounts()) {
                                        flag = false;
                                    }
                                }
                                case "$L" -> {
                                    if (!capsule.hasMeta("ledger")) {
                                        flag = false;
                                    }
                                }
                                case "$l" -> {
                                    if (capsule.hasMeta("ledger")) {
                                        flag = false;
                                    }
                                }
                                case "$A" -> {
                                    if (!(capsule.hasMeta("asset") || capsule.hasMeta("asset-change"))) {
                                        flag = false;
                                    }
                                }
                                case "$a" -> {
                                    if (capsule.hasMeta("asset") || capsule.hasMeta("asset-change")) {
                                        flag = false;
                                    }
                                }
                                case "$D" -> {
                                    if (!(capsule.hasMeta("loan") || capsule.hasMeta("loan-change"))) {
                                        flag = false;
                                    }
                                }
                                case "$d" -> {
                                    if (capsule.hasMeta("loan") || capsule.hasMeta("loan-change")) {
                                        flag = false;
                                    }
                                }
                                case "$@" -> {
                                    if (capsule.isBalanced()) {
                                        flag = false;
                                    }
                                }
                                default -> {
                                    if (token.length() > 5) {
                                        if (token.substring(0, 3).equalsIgnoreCase("$or")) {
                                            ArrayList<String> subTokens = tokenize(token.substring(4, token.length() - 1));
                                            boolean test = false;
                                            for (String sToken : subTokens) {
                                                if (capsule.toString().contains(sToken)) {
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
                            if (capsule.toFlatString().replace("\t", " ").toLowerCase().contains(token.replace("\"", "").replace("\\$", "$").replace("-", "").toLowerCase())) {
                                flag = false;
                                break OUTER;
                            }
                        }
                        default -> {
                            if (!capsule.toFlatString().replace("\t", " ").toLowerCase().contains(token.replace("\"", "").replace("\\$", "$").toLowerCase())) {
                                flag = false;
                                break OUTER;
                            }
                        }
                    }
                }
            }
            if (flag) {
                correct.add(capsule);
            }
        }
        correct.sort(Comparator.comparing(TransactionCapsule::getDate));
        return correct;
    }

    private ArrayList<String> tokenize(String raw) {
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
}
