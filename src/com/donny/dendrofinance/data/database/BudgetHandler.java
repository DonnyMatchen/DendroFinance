package com.donny.dendrofinance.data.database;

import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.SqlEscape;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;
import com.donny.dendroroot.json.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class BudgetHandler extends TableHandler<String, BudgetCapsule> {
    public BudgetHandler(ProgramInstance curInst, DatabaseHandler database) {
        super(curInst, database);
    }

    public boolean add(String name, JsonObject budg, ImportHandler.ImportMode mode) {
        return add(new BudgetCapsule(name, budg, CURRENT_INSTANCE), mode);
    }

    @Override
    public boolean add(BudgetCapsule capsule, ImportHandler.ImportMode mode) {
        try {
            String name = capsule.getName();
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkName(capsule.getName(), "BUDGETS")) {
                switch (mode) {
                    case IGNORE -> {
                        return false;
                    }
                    case KEEP -> name = CURRENT_INSTANCE.UNIQUE_HANDLER.generateName(name, "BUDGETS");
                    case OVERWRITE -> {
                        try {
                            Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                            insert.execute("""
                                    UPDATE BUDGETS
                                    SET budg = '%s'
                                    WHERE name = '%s'
                                    """.formatted(
                                    SqlEscape.feed(capsule.getBudget().toString()),
                                    SqlEscape.feed(name)
                            ));
                            return true;
                        } catch (SQLException e) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to update budget:\n" + capsule.getName());
                            return false;
                        }
                    }
                }
            }
            try {
                Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                insert.execute("INSERT INTO BUDGETS VALUES('"
                        + SqlEscape.feed(name) + "', '"
                        + SqlEscape.feed(capsule.getBudget().toString()) + "')");
                return true;
            } catch (SQLException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to insert budget:\n" + capsule.getName());
                return false;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to query database:");
            return false;
        }
    }

    @Override
    public boolean add(JsonObject object, ImportHandler.ImportMode mode) {
        return add(new BudgetCapsule(object, CURRENT_INSTANCE), mode);
    }

    @Override
    public BudgetCapsule get(String name) {
        try {
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkName(name, "BUDGETS")) {
                Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                statement.execute("SELECT * FROM BUDGETS WHERE name = '" + name + "'");
                ResultSet set = statement.getResultSet();
                if (set.next()) {
                    return new BudgetCapsule(
                            SqlEscape.eat(set.getString(1)),
                            (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(2))),
                            CURRENT_INSTANCE
                    );
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve budget: " + name + "\n" + e);
            return null;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed contents for budget: " + name + "\n" + e);
            return null;
        }
    }

    public ArrayList<BudgetCapsule> getBudgets() {
        ArrayList<BudgetCapsule> output = new ArrayList<>();
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT * FROM BUDGETS");
            ResultSet set = statement.getResultSet();
            while (set.next()) {
                output.add(new BudgetCapsule(
                        SqlEscape.eat(set.getString(1)),
                        (JsonObject) JsonItem.digest(SqlEscape.eat(set.getString(2))),
                        CURRENT_INSTANCE
                ));
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve budgets\n" + e);
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed contents\n" + e);
        }
        return output;
    }

    @Override
    public boolean delete(String name) {
        try {
            Statement delete = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            delete.execute("DELETE FROM BUDGETS WHERE name = " + name);
            return true;
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to delete budget: " + name);
            return false;
        }
    }
}
