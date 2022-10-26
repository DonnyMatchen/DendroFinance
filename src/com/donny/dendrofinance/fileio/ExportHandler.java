package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.TemplateEntry;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.types.LDate;

import java.io.File;

public class ExportHandler {
    private final Instance CURRENT_INSTANCE;
    private final File DIR;

    public ExportHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        DIR = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Exports");
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "ExportHandler initiated");
    }

    public void export(String extension, String name) {
        LDate now = LDate.now(CURRENT_INSTANCE);
        File directory = new File(DIR.getPath() +
                File.separator + now.toDateString().replace("/", "-") +
                File.separator + now.toTimeString().replace(":", "-"));
        CURRENT_INSTANCE.FILE_HANDLER.ensure(directory);
        switch (extension) {
            case "JSON" -> {
                File transactions = new File(directory.getPath() + File.separator + name + "-Transactions.json");
                JsonArray array = new JsonArray();
                for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                    try {
                        array.add(entry.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + entry.getUUID());
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(transactions, array);

                File budgets = new File(directory.getPath() + File.separator + name + "-Budgets.json");
                array = new JsonArray();
                for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                    try {
                        array.add(entry.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + entry.getUUID());
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(budgets, array);

                File templates = new File(directory.getPath() + File.separator + name + "-Templates.json");
                array = new JsonArray();
                for (TemplateEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTemplates()) {
                    try {
                        array.add(entry.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + entry.getUUID());
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(templates, array);
            }
            case "XTBL" -> {
                File transactions = new File(directory.getPath() + File.separator + name + "-Transactions.xtbl");
                JsonArray array = new JsonArray();
                for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
                    try {
                        array.add(entry.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + entry.getUUID());
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(transactions, array);

                File budgets = new File(directory.getPath() + File.separator + name + "-Budgets.xtbl");
                array = new JsonArray();
                for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
                    try {
                        array.add(entry.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + entry.getUUID());
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(budgets, array);

                File templates = new File(directory.getPath() + File.separator + name + "-Templates.xtbl");
                array = new JsonArray();
                for (TemplateEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTemplates()) {
                    try {
                        array.add(entry.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + entry.getUUID());
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(templates, array);
            }
        }
    }
}
