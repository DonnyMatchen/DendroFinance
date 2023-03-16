package com.donny.dendrofinance.data.database;

import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.form.SqlEscape;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LDate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class StatesHandler extends TableHandler<Long, StateCapsule> {
    public StatesHandler(Instance curInst, DatabaseHandler database) {
        super(curInst, database);
    }

    @Override
    public boolean add(StateCapsule capsule, ImportHandler.ImportMode mode) {
        try {
            long timestamp = capsule.getDate().getTime();
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkTimestamp(timestamp)) {
                switch (mode) {
                    case IGNORE -> {
                        return false;
                    }
                    case KEEP, OVERWRITE -> {
                        try {
                            Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                            insert.execute("""
                                    UPDATE STATES
                                    SET acc = '%s', pos = '%s', meta = '%s'
                                    WHERE dtm = '%s'
                                    """.formatted(
                                    SqlEscape.feed(capsule.exportAccounts().toString()),
                                    SqlEscape.feed(capsule.exportPositions().toString()),
                                    SqlEscape.feed(capsule.exportMeta().toString()),
                                    timestamp
                            ));
                            return true;
                        } catch (SQLException e) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to update states:\n" + capsule.getDate() + "\n" + e);
                            return false;
                        }
                    }
                }
            }
            try {
                Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                insert.execute("INSERT INTO STATES VALUES ("
                        + timestamp + ", '"
                        + SqlEscape.feed(capsule.exportAccounts().toString()) + "', '"
                        + SqlEscape.feed(capsule.exportPositions().toString()) + "', '"
                        + SqlEscape.feed(capsule.exportMeta().toString()) + "')");
                return true;
            } catch (SQLException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to insert state:\n" + capsule.getDate() + "\n" + e);
                return false;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to query database:");
            return false;
        }
    }

    @Override
    public boolean add(JsonObject object, ImportHandler.ImportMode mode) {
        return add(new StateCapsule(object, CURRENT_INSTANCE), mode);
    }

    @Override
    public StateCapsule get(Long timestamp) {
        try {
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkTimestamp(timestamp)) {
                Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                statement.execute("SELECT * FROM STATES WHERE dtm = " + timestamp);
                ResultSet set = statement.getResultSet();
                if (set.next()) {
                    return new StateCapsule(
                            new LDate(set.getLong(1), CURRENT_INSTANCE),
                            (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(2))),
                            (JsonArray) JsonItem.digest(SqlEscape.eat(set.getString(3))),
                            (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(4))),
                            CURRENT_INSTANCE
                    );
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve state: " + Long.toUnsignedString(timestamp) + "\n" + e);
            return null;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed contents for state: " + Long.toUnsignedString(timestamp) + "\n" + e);
            return null;
        }
    }

    public StateCapsule getBefore(Long timestamp) {
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT TOP 1 dtm FROM STATES WHERE dtm <= " + timestamp + " ORDER BY dtm DESC");
            ResultSet set = statement.getResultSet();
            if (set.next()) {
                return get(set.getLong(1));
            } else {
                return null;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve a state before: " + Long.toUnsignedString(timestamp) + "\n" + e);
            return null;
        }
    }

    public long getMaxDate() {
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT TOP 1 dtm FROM STATES ORDER BY dtm DESC");
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
            statement.execute("SELECT TOP 1 dtm FROM STATES ORDER BY dtm ASC");
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

    public ArrayList<StateCapsule> getRange(LDate start, LDate end) {
        ArrayList<StateCapsule> output = new ArrayList<>();
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT * FROM STATES WHERE dtm > " + start.getTime() + " AND dtm < " + end.getTime() + " ORDER BY dtm ASC");
            ResultSet set = statement.getResultSet();
            while (set.next()) {
                output.add(new StateCapsule(
                        new LDate(set.getLong(1), CURRENT_INSTANCE),
                        (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(2))),
                        (JsonArray) JsonItem.digest(SqlEscape.eat(set.getString(3))),
                        (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(4))),
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

    @Override
    public boolean delete(Long timestamp) {
        try {
            Statement delete = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            delete.execute("DELETE FROM STATES WHERE dtm = " + timestamp);
            return true;
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to delete state: " + timestamp);
            return false;
        }
    }
}
