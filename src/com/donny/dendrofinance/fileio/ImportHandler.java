package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.capsules.TemplateCapsule;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.gui.password.UnkPasswordGui;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.xarc.XarcInputStream;
import com.fasterxml.jackson.core.JsonFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

public class ImportHandler {
    private final ProgramInstance CURRENT_INSTANCE;

    public enum ImportMode {
        /*
         * IGNORE: if there is a UUID clash, don't add the new one
         * KEEP: if there is a UUID clash, add the new one with a new UUID
         * OVERWRITE: if there is a UUID clash, discard the old one and add the new one
         */
        IGNORE, KEEP, OVERWRITE;

        public static String[] getArray() {
            return new String[]{
                    "IGNORE", "KEEP", "OVERWRITE"
            };
        }

        public static ImportMode fromString(String string) {
            return switch (string.toUpperCase()) {
                case "IGNORE", "IGNORE NEW" -> IGNORE;
                case "OVERWRITE", "REPLACE OLD" -> OVERWRITE;
                default -> KEEP;
            };
        }

        public String toString() {
            return switch (this) {
                case IGNORE -> "IGNORE";
                case KEEP -> "KEEP";
                case OVERWRITE -> "OVERWRITE";
            };
        }
    }

    public ImportHandler(ProgramInstance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "ImportHandler initiated");
    }

    /*
     * It is expected that JSON imports might be encrypted, but CSVs never will be
     */
    public final void load(String path, JFrame caller, ImportMode mode) throws SQLException {
        File file = new File(path);
        if (file.exists()) {
            if (path.toLowerCase().contains(".csv")) {
                loadCSV(file);
            } else if (path.toLowerCase().contains(".json")) {
                loadJSON(file, mode);
            } else if (path.toLowerCase().contains(".xtbl")) {
                loadXTBL(file, caller, mode);
            } else if (path.toLowerCase().contains(".xarc")) {
                loadXARC(file, caller, mode);
            }
        }
    }

    public void loadCSV(File file) {
        String raw = CURRENT_INSTANCE.FILE_HANDLER.read(file);
        for (String line : raw.replace("\r", "").split("\n")) {
            String[] fields = line.split("\t");
            TransactionCapsule capsule = new TransactionCapsule(CURRENT_INSTANCE);
            try {
                capsule.insert(
                        new LDate(fields[0], CURRENT_INSTANCE),
                        fields[1],
                        fields[2],
                        fields[3],
                        new LAccountSet(fields[4], CURRENT_INSTANCE)
                );
                if (!fields[5].equals("{}")) {
                    capsule.setMeta((JsonObject) JsonItem.digest(fields[6]));
                }
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Metadata: " + fields[6]);
            } catch (ParseException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Date: " + fields[0]);
            }
            CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(capsule, ImportMode.KEEP);
        }
        CURRENT_INSTANCE.FILE_HANDLER.delete(file);
    }

    public void loadJSON(File file, ImportMode mode) throws SQLException {
        boolean imported = false;
        if (file.getName().toLowerCase().contains("transaction")) {
            JsonArray array = (JsonArray) CURRENT_INSTANCE.FILE_HANDLER.readJson(file);
            for (JsonObject obj : array.getObjectArray()) {
                CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(new TransactionCapsule(obj, mode, CURRENT_INSTANCE), mode);
            }
            imported = true;
        } else if (file.getName().toLowerCase().contains("budget")) {
            JsonArray array = (JsonArray) CURRENT_INSTANCE.FILE_HANDLER.readJson(file);
            for (JsonObject obj : array.getObjectArray()) {
                CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.add(new BudgetCapsule(obj, mode, CURRENT_INSTANCE), mode);
            }
            imported = true;
        } else if (file.getName().toLowerCase().contains("template")) {
            JsonArray array = (JsonArray) CURRENT_INSTANCE.FILE_HANDLER.readJson(file);
            for (JsonObject obj : array.getObjectArray()) {
                CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.add(new TemplateCapsule(obj, mode, CURRENT_INSTANCE), mode);
            }
            imported = true;
        } else if (file.getName().toLowerCase().contains("state")) {
            JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.readJson(file);
            JsonArray array = (JsonArray) item;
            for (JsonObject obj : array.getObjectArray()) {
                CURRENT_INSTANCE.DATA_HANDLER.STATES.add(new StateCapsule(obj, CURRENT_INSTANCE), mode);
            }
            imported = true;
        }
        if (imported) {
            CURRENT_INSTANCE.FILE_HANDLER.delete(file);
        }
    }

    public void loadXTBL(File file, JFrame caller, ImportMode mode) throws SQLException {
        boolean imported = false;
        if (file.getName().toLowerCase().contains("transaction") || file.getName().toLowerCase().contains(".xarc")) {
            JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.readDecryptJsonUnknownPassword(file, caller);
            if (item == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                JsonArray array = (JsonArray) item;
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(new TransactionCapsule(obj, mode, CURRENT_INSTANCE), mode);
                }
                imported = true;
            }
        } else if (file.getName().toLowerCase().contains("budget")) {
            JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.readDecryptJsonUnknownPassword(file, caller);
            if (item == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                JsonArray array = (JsonArray) item;
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.add(new BudgetCapsule(obj, mode, CURRENT_INSTANCE), mode);
                }
                imported = true;
            }
        } else if (file.getName().toLowerCase().contains("template")) {
            JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.readDecryptJsonUnknownPassword(file, caller);
            if (item == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                JsonArray array = (JsonArray) item;
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.add(new TemplateCapsule(obj, mode, CURRENT_INSTANCE), mode);
                }
                imported = true;
            }
        } else if (file.getName().toLowerCase().contains("state")) {
            JsonItem item = CURRENT_INSTANCE.FILE_HANDLER.readDecryptJsonUnknownPassword(file, caller);
            if (item == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                JsonArray array = (JsonArray) item;
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.STATES.add(new StateCapsule(obj, CURRENT_INSTANCE), mode);
                }
                imported = true;
            }
        }
        if (imported) {
            CURRENT_INSTANCE.FILE_HANDLER.delete(file);
        }
    }

    public void loadXARC(File file, JFrame caller, ImportMode mode) throws SQLException {
        boolean imported = false;
        EncryptionHandler handler = UnkPasswordGui.getTestPassword(caller, file.getName(), CURRENT_INSTANCE);
        try {
            JsonItem item = JsonItem.digest(new JsonFactory().createParser(new XarcInputStream(file, handler, CURRENT_INSTANCE)));
            if (item == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                if (file.getName().toLowerCase().contains("transaction")) {
                    JsonArray array = (JsonArray) item;
                    for (JsonObject obj : array.getObjectArray()) {
                        CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.add(new TransactionCapsule(obj, mode, CURRENT_INSTANCE), mode);
                    }
                    imported = true;
                } else if (file.getName().toLowerCase().contains("budget")) {
                    JsonArray array = (JsonArray) item;
                    for (JsonObject obj : array.getObjectArray()) {
                        CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.add(new BudgetCapsule(obj, mode, CURRENT_INSTANCE), mode);
                    }
                    imported = true;
                } else if (file.getName().toLowerCase().contains("template")) {
                    JsonArray array = (JsonArray) item;
                    for (JsonObject obj : array.getObjectArray()) {
                        CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.add(new TemplateCapsule(obj, mode, CURRENT_INSTANCE), mode);
                    }
                    imported = true;
                } else if (file.getName().toLowerCase().contains("state")) {
                    JsonArray array = (JsonArray) item;
                    for (JsonObject obj : array.getObjectArray()) {
                        CURRENT_INSTANCE.DATA_HANDLER.STATES.add(new StateCapsule(obj, CURRENT_INSTANCE), mode);
                    }
                    imported = true;
                }
            }
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong importing " + file.getPath() + "\n" + e);
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed json: " + file.getPath() + "\n" + e);
        }
        if (imported) {
            CURRENT_INSTANCE.FILE_HANDLER.delete(file);
        }
    }
}
