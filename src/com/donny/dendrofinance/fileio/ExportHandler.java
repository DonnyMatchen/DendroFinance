package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.capsules.TemplateCapsule;
import com.donny.dendrofinance.capsules.TransactionCapsule;
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

    public void export(LDate start, LDate end, String extension, String name) {
        LDate now = LDate.now(CURRENT_INSTANCE);
        File directory = new File(DIR.getPath() +
                File.separator + now.toDateString().replace("/", "-") +
                File.separator + now.toTimeString().replace(":", "-"));
        CURRENT_INSTANCE.FILE_HANDLER.ensure(directory);
        switch (extension) {
            case "JSON" -> {
                File transactions = new File(directory.getPath() + File.separator + name + "(" + start.toFileSafeDateString() + "_to_" + end.toFileSafeDateString() + ")-Transactions.json");
                JsonArray array = new JsonArray();
                for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TRANSACTIONS.getRange(start, end)) {
                    try {
                        array.add(capsule.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + capsule.getUUID() + "\n" + ex);
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(transactions, array);

                File budgets = new File(directory.getPath() + File.separator + name + "-Budgets.json");
                array = new JsonArray();
                for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.BUDGETS.getBudgets()) {
                    try {
                        array.add(capsule.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + capsule.getName() + "\n" + ex);
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(budgets, array);

                File templates = new File(directory.getPath() + File.separator + name + "-Templates.json");
                array = new JsonArray();
                for (TemplateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TEMPLATES.getTemplates()) {
                    try {
                        array.add(capsule.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + capsule.getName() + "\n" + ex);
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(templates, array);

                File states = new File(directory.getPath() + File.separator + name + "-States.json");
                array = new JsonArray();
                for (StateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.STATES.getStates()) {
                    array.add(capsule.export());
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeJson(states, array);
            }
            case "XTBL" -> {
                File transactions = new File(directory.getPath() + File.separator + name + "(" + start.toFileSafeDateString() + "_to_" + end.toFileSafeDateString() + ")-Transactions.xtbl");
                JsonArray array = new JsonArray();
                for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TRANSACTIONS.getRange(start, end)) {
                    try {
                        array.add(capsule.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + capsule.getUUID() + "\n" + ex);
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(transactions, array);

                File budgets = new File(directory.getPath() + File.separator + name + "-Budgets.xtbl");
                array = new JsonArray();
                for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.BUDGETS.getBudgets()) {
                    try {
                        array.add(capsule.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + capsule.getName() + "\n" + ex);
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(budgets, array);

                File templates = new File(directory.getPath() + File.separator + name + "-Templates.xtbl");
                array = new JsonArray();
                for (TemplateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.TEMPLATES.getTemplates()) {
                    try {
                        array.add(capsule.export());
                    } catch (JsonFormattingException ex) {
                        CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + capsule.getName() + "\n" + ex);
                    }
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(templates, array);

                File states = new File(directory.getPath() + File.separator + name + "-States.xtbl");
                array = new JsonArray();
                for (StateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.DATABASE.STATES.getStates()) {
                    array.add(capsule.export());
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(states, array);
            }
        }
    }
}
