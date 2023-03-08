package com.donny.dendrofinance.data;

import com.donny.dendrofinance.instance.Instance;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class UniqueHandler {
    private final Instance CURRENT_INSTANCE;

    public UniqueHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "UniqueHandler Initiated");
    }

    public long generateUUID() {
        SecureRandom rand = new SecureRandom();
        boolean flag = true;
        long candidate = 0;
        int failure = 0;
        while (flag) {
            candidate = rand.nextLong();
            try {
                flag = checkUuid(candidate);
            } catch (SQLException e) {
                failure++;
                if (failure > 100) {
                    CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Database Broken");
                    CURRENT_INSTANCE.LOG_HANDLER.save();
                    System.exit(1);
                }
            }
        }
        return candidate;
    }

    public String generateName(String base, String table) {
        int i = 0;
        boolean flag = true;
        int failure = 0;
        while (flag) {
            i++;
            try {
                flag = !checkName(base + "_" + i, table);
            } catch (SQLException e) {
                failure++;
                if (failure > 100) {
                    CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Database Broken");
                    CURRENT_INSTANCE.LOG_HANDLER.save();
                    System.exit(1);
                }
            }
        }
        return base + "_" + i;
    }

    public boolean checkUuid(long l) throws SQLException {
        if (l == 0) {
            return false;
        }
        Connection data = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con;
        Statement statement = data.createStatement();
        statement.execute("SELECT * FROM TRANSACTIONS WHERE uuid = " + l);
        return !statement.getResultSet().next();
    }

    public boolean checkTimestamp(long l) throws SQLException {
        Connection data = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con;
        Statement statement = data.createStatement();
        statement.execute("SELECT * FROM STATES WHERE dtm = " + l);
        return !statement.getResultSet().next();
    }

    public boolean checkName(String name, String table) throws SQLException {
        Connection data = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con;
        Statement statement = data.createStatement();
        statement.execute("SELECT * FROM " + table + " WHERE name = '" + name + "'");
        return !statement.getResultSet().next();
    }
}
