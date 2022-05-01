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
import com.donny.dendrofinance.types.LJson;
import com.donny.dendrofinance.types.LString;

import java.io.File;

public class ImportHandler {
    private final Instance CURRENT_INSTANCE;

    public ImportHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        load();
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "ImportHandler initiated");
    }

    public final void load() {
        File[] dataList = CURRENT_INSTANCE.data.listFiles();
        if (dataList != null) {
            for (File f : dataList) {
                if (!f.isDirectory()) {
                    if (f.getName().contains(".csv")) {
                        loadCSV(f);
                    } else if (f.getName().contains(".json")) {
                        loadJSON(f);
                    }
                }
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
                    new LString(fields[1]),
                    new LString(fields[2]),
                    new LString(fields[3]),
                    new LAccountSet(fields[4], CURRENT_INSTANCE)
            );
            try {
                if (!fields[5].equals("{}")) {
                    entry.insertIntoField("meta-data", new LJson((JsonObject) JsonItem.sanitizeDigest(fields[6])));
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
}
