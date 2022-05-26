package com.donny.dendrofinance.data;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.gui.MainGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.io.File;
import java.util.Base64;

public class ImportHandler {
    private final Instance CURRENT_INSTANCE;

    public ImportHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "ImportHandler initiated");
    }

    /*
     * It is expected that JSON imports might be encrypted, but CSVs never will be
     */
    public final void load(String path, JFrame caller) {
        File file = new File(path);
        if (file.exists()) {
            if (path.toLowerCase().contains(".csv")) {
                loadCSV(file);
            } else if (path.toLowerCase().contains(".json")) {
                loadJSON(file);
            } else if (path.toLowerCase().contains(".xtbl")) {
                loadXTBL(file, caller);
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

    public void loadJSON(File file) {
        boolean imported = false;
        if (file.getName().toLowerCase().contains("transaction")) {
            try {
                JsonArray array = (JsonArray) JsonItem.sanitizeDigest(CURRENT_INSTANCE.FILE_HANDLER.read(file));
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.addTransaction(new TransactionEntry(obj, CURRENT_INSTANCE));
                }
                imported = true;
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed Import File:\n" + file.getPath());
            }
        } else if (file.getName().toLowerCase().contains("budget")) {
            try {
                JsonArray array = (JsonArray) JsonItem.sanitizeDigest(CURRENT_INSTANCE.FILE_HANDLER.read(file));
                for (JsonObject obj : array.getObjectArray()) {
                    CURRENT_INSTANCE.DATA_HANDLER.addBudget(new BudgetEntry(obj, CURRENT_INSTANCE));
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

    public void loadXTBL(File file, JFrame caller) {
        boolean imported = false;
        if (file.getName().toLowerCase().contains("transaction")) {
            String raw = CURRENT_INSTANCE.FILE_HANDLER.readDecryptUnknownPassword(file, caller);
            if (raw == null) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Incorrect password for file: " + file.getPath());
            } else {
                try {
                    JsonArray array = (JsonArray) JsonItem.sanitizeDigest(raw.replace("passwd", ""));
                    for (JsonObject obj : array.getObjectArray()) {
                        CURRENT_INSTANCE.DATA_HANDLER.addTransaction(new TransactionEntry(obj, CURRENT_INSTANCE));
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
                    CURRENT_INSTANCE.DATA_HANDLER.addBudget(new BudgetEntry(obj, CURRENT_INSTANCE));
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
