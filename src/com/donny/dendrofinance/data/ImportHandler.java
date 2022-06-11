package com.donny.dendrofinance.data;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.io.File;

public class ImportHandler {
    private final Instance CURRENT_INSTANCE;

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

    public ImportHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "ImportHandler initiated");
    }

    /*
     * It is expected that JSON imports might be encrypted, but CSVs never will be
     */
    public final void load(String path, JFrame caller, ImportMode mode) {
        File file = new File(path);
        if (file.exists()) {
            if (path.toLowerCase().contains(".csv")) {
                loadCSV(file);
            } else if (path.toLowerCase().contains(".json")) {
                loadJSON(file, mode);
            } else if (path.toLowerCase().contains(".xtbl") || path.toLowerCase().contains(".xarc")) {
                loadXTBL(file, caller, mode);
            }
        }
    }

    public void loadCSV(File file) {
        String raw = CURRENT_INSTANCE.FILE_HANDLER.readPlain(file);
        for (String line : raw.replace("\r", "").split("\n")) {
            String[] fields = line.split("\t");
            TransactionEntry entry = new TransactionEntry(CURRENT_INSTANCE);
            entry.insert(
                    new LDate(fields[0], CURRENT_INSTANCE),
                    fields[1],
                    fields[2],
                    fields[3],
                    new LAccountSet(fields[4], CURRENT_INSTANCE)
            );
            try {
                if (!fields[5].equals("{}")) {
                    entry.setMeta((JsonObject) JsonItem.sanitizeDigest(fields[6]));
                }
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Metadata: " + fields[6]);
            }
            CURRENT_INSTANCE.DATA_HANDLER.addTransaction(entry);
        }
        CURRENT_INSTANCE.FILE_HANDLER.delete(file);
    }

    public void loadJSON(File file, ImportMode mode) {
        boolean imported = false;
        if (file.getName().toLowerCase().contains("transaction")) {
            try {
                JsonArray array = (JsonArray) JsonItem.sanitizeDigest(CURRENT_INSTANCE.FILE_HANDLER.read(file));
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.addTransaction(new TransactionEntry(obj, mode, CURRENT_INSTANCE), mode);
                }
                imported = true;
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Import File:\n" + file.getPath());
            }
        } else if (file.getName().toLowerCase().contains("budget")) {
            try {
                JsonArray array = (JsonArray) JsonItem.sanitizeDigest(CURRENT_INSTANCE.FILE_HANDLER.read(file));
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.addBudget(new BudgetEntry(obj, mode, CURRENT_INSTANCE), mode);
                }
                imported = true;
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Import File:\n" + file.getPath());
            }
        }
        if (imported) {
            CURRENT_INSTANCE.FILE_HANDLER.delete(file);
        }
    }

    public void loadXTBL(File file, JFrame caller, ImportMode mode) {
        boolean imported = false;
        if (file.getName().toLowerCase().contains("transaction") || file.getName().toLowerCase().contains(".xarc")) {
            String raw = CURRENT_INSTANCE.FILE_HANDLER.readDecryptUnknownPassword(file, caller);
            if (raw == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                try {
                    JsonArray array = (JsonArray) JsonItem.sanitizeDigest(raw.replace("passwd", ""));
                    for (JsonObject obj : array.getObjectArray()) {
                        CURRENT_INSTANCE.DATA_HANDLER.addTransaction(new TransactionEntry(obj, mode, CURRENT_INSTANCE));
                    }
                    imported = true;
                } catch (JsonFormattingException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Import File:\n" + file.getPath());
                }
            }
        } else if (file.getName().toLowerCase().contains("budget")) {
            try {
                String raw = CURRENT_INSTANCE.FILE_HANDLER.readDecryptUnknownPassword(file, caller);
                JsonArray array = (JsonArray) JsonItem.sanitizeDigest(raw.replace("passwd", ""));
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.addBudget(new BudgetEntry(obj, mode, CURRENT_INSTANCE));
                }
                imported = true;
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Import File:\n" + file.getPath());
            }
        }
        if (imported) {
            CURRENT_INSTANCE.FILE_HANDLER.delete(file);
        }
    }
}
