package com.donny.dendrofinance.data.database;

import com.donny.dendrofinance.instance.Instance;

import java.io.File;
import java.sql.*;

public class DatabaseHandler {
    private final Instance CURRENT_INSTANCE;
    public Connection con;


    public DatabaseHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        try {
            con = DriverManager.getConnection(
                    "jdbc:hsqldb:file:"
                            + curInst.data.getPath() + File.separator + "Database" + File.separator + "ldb;shutdown=true;"
                            + curInst.ENCRYPTION_HANDLER.getDataBaseEncryptionString(),
                    "SA", "");
            con.setAutoCommit(false);
        } catch (SQLException e) {
            if (e.getMessage().equals("java.util.zip.ZipException: Not in GZIP format")) {
                curInst.LOG_HANDLER.fatal(getClass(), "Incorrect password entered or database corrupt!");
            } else {
                curInst.LOG_HANDLER.fatal(getClass(), "Database error!\n" + e + "\n" + e.getErrorCode() + ": " + e.getSQLState());
            }
            curInst.LOG_HANDLER.save();
            System.exit(1);
        }
    }

    public void init() throws SQLException {
        if (!tableExists("BUDGETS")) {
            con.createStatement().execute("""
                    CREATE CACHED TABLE BUDGETS (
                        name VARCHAR(255) NOT NULL PRIMARY KEY,
                        budg VARCHAR(8192) NOT NULL
                    );
                    """
            );
        }
        if (!tableExists("STATES")) {
            con.createStatement().execute("""
                    CREATE CACHED TABLE STATES (
                        dtm BIGINT NOT NULL PRIMARY KEY,
                        acc VARCHAR(16384) NOT NULL,
                        pos VARCHAR(16384) NOT NULL,
                        meta VARCHAR(16384) NOT NULL
                    );
                    """
            );
        }
        if (!tableExists("TRANSACTIONS")) {
            con.createStatement().execute("""
                    CREATE CACHED TABLE TRANSACTIONS (
                        uuid BIGINT NOT NULL UNIQUE,
                        dtm BIGINT NOT NULL,
                        ent VARCHAR(255),
                        itm VARCHAR(255),
                        des VARCHAR(255),
                        acc VARCHAR(1024),
                        meta VARCHAR(8192),
                        CONSTRAINT PK_Transaction PRIMARY KEY (uuid,dtm)
                    );
                    """
            );
        }
        if (!tableExists("TEMPLATES")) {
            con.createStatement().execute("""
                    CREATE CACHED TABLE TEMPLATES (
                        name VARCHAR(255) NOT NULL PRIMARY KEY,
                        ref BIGINT NOT NULL,
                        FOREIGN KEY (ref) REFERENCES TRANSACTIONS(uuid)
                    );
                    """
            );
        }
    }

    public boolean tableExists(String table) throws SQLException {
        DatabaseMetaData metaData = con.getMetaData();
        ResultSet tables = metaData.getTables(null, null, table, null);
        return tables.next();
    }

    public void revertDatabase() {
        try {
            con.rollback();
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to rollback transactions\n" + e);
        }
    }

    public void commitDatabase() {
        try {
            con.commit();
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to commit transactions\n" + e);
        }
    }

    public void closeDatabase() {
        try {
            con.close();
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to close database");
        }
    }
}
