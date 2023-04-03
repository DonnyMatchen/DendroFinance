package com.donny.dendrofinance.data.database;

import com.donny.dendrofinance.capsules.TemplateCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.SqlEscape;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TemplateHandler extends TableHandler<String, TemplateCapsule> {
    public TemplateHandler(ProgramInstance curInst, DatabaseHandler database) {
        super(curInst, database);
    }

    public boolean add(String name, long ref, ImportHandler.ImportMode mode) {
        return add(new TemplateCapsule(name, ref, CURRENT_INSTANCE), mode);
    }

    @Override
    public boolean add(TemplateCapsule capsule, ImportHandler.ImportMode mode) {
        try {
            String name = capsule.getName();
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkName(capsule.getName(), "TEMPLATES")) {
                switch (mode) {
                    case IGNORE -> {
                        return false;
                    }
                    case KEEP -> name = CURRENT_INSTANCE.UNIQUE_HANDLER.generateName(name, "TEMPLATES");
                    case OVERWRITE -> {
                        try {
                            Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                            insert.execute("""
                                    UPDATE TEMPLATES
                                    SET ref = %s
                                    WHERE name = '%s'
                                    """.formatted(
                                    capsule.getRef(),
                                    SqlEscape.feed(name)
                            ));
                            return true;
                        } catch (SQLException e) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to update template:\n" + capsule.getName());
                            return false;
                        }
                    }
                }
            }
            try {
                Statement insert = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                insert.execute("INSERT INTO TEMPLATES VALUES('"
                        + SqlEscape.feed(name) + "', "
                        + capsule.getRef() + ")");
                return true;
            } catch (SQLException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to insert template:\n" + capsule.getName());
                return false;
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to query database:");
            return false;
        }
    }

    @Override
    public boolean add(JsonObject object, ImportHandler.ImportMode mode) {
        return add(new TemplateCapsule(object, CURRENT_INSTANCE), mode);
    }

    @Override
    public TemplateCapsule get(String name) {
        try {
            if (!CURRENT_INSTANCE.UNIQUE_HANDLER.checkName(name, "TEMPLATES")) {
                Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
                statement.execute("SELECT * FROM TEMPLATES WHERE name = '" + name + "'");
                ResultSet set = statement.getResultSet();
                if (set.next()) {
                    return new TemplateCapsule(
                            SqlEscape.eat(set.getString(1)),
                            set.getLong(2),
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
        }
    }

    public ArrayList<TemplateCapsule> getTemplates() {
        ArrayList<TemplateCapsule> output = new ArrayList<>();
        try {
            Statement statement = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            statement.execute("SELECT * FROM TEMPLATES");
            ResultSet set = statement.getResultSet();
            while (set.next()) {
                output.add(new TemplateCapsule(
                        SqlEscape.eat(set.getString(1)),
                        set.getLong(2),
                        CURRENT_INSTANCE
                ));
            }
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to retrieve budgets\n" + e);
        }
        return output;
    }

    @Override
    public boolean delete(String name) {
        try {
            Statement delete = CURRENT_INSTANCE.DATA_HANDLER.DATABASE.con.createStatement();
            delete.execute("DELETE FROM TEMPLATES WHERE name = " + name);
            return true;
        } catch (SQLException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to delete template: " + name);
            return false;
        }
    }
}
