package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.capsules.BudgetCapsule;
import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.capsules.TemplateCapsule;
import com.donny.dendrofinance.capsules.TransactionCapsule;
import com.donny.dendrofinance.fileio.xarc.XarcOutputStream;
import com.donny.dendrofinance.gui.password.UnkPasswordGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class ExportHandler {
    private final Instance CURRENT_INSTANCE;
    private final File DIR;

    public ExportHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        DIR = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Exports");
        CURRENT_INSTANCE.LOG_HANDLER.trace(getClass(), "ExportHandler initiated");
    }

    public void export(LDate start, LDate end, String extension, String name, JFrame caller, boolean[] flags) {
        LDate now = LDate.now(CURRENT_INSTANCE);
        File directory = new File(DIR.getPath() +
                File.separator + now.toDateString().replace("/", "-") +
                File.separator + now.toTimeString().replace(":", "-"));
        CURRENT_INSTANCE.FILE_HANDLER.ensure(directory);
        switch (extension) {
            case "JSON" -> {
                if(flags[0]) {
                    JsonArray array = new JsonArray();
                    for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getRange(start, end)) {
                        try {
                            array.add(capsule.export());
                        } catch (JsonFormattingException ex) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + capsule.getUUID() + "\n" + ex);
                        }
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeJson(directory, name + "(" + start.toFileSafeDateString() + "_to_" + end.toFileSafeDateString() + ")-Transactions.json", array);
                }

                if(flags[1]) {
                    JsonArray array = new JsonArray();
                    for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                        try {
                            array.add(capsule.export());
                        } catch (JsonFormattingException ex) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + capsule.getName() + "\n" + ex);
                        }
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeJson(directory, name + "-Budgets.json", array);
                }

                if(flags[2]) {
                    JsonArray array = new JsonArray();
                    for (TemplateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.getTemplates()) {
                        try {
                            array.add(capsule.export());
                        } catch (JsonFormattingException ex) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + capsule.getName() + "\n" + ex);
                        }
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeJson(directory, name + "-Templates.json", array);
                }

                if(flags[3]) {
                    JsonArray array = new JsonArray();
                    for (StateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.STATES.getRange(start, end)) {
                        array.add(capsule.export());
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeJson(directory, name + "-States.json", array);
                }
            }
            case "XTBL" -> {
                EncryptionHandler encrypt = UnkPasswordGui.getTestPassword(caller, "exports", CURRENT_INSTANCE);
                if(flags[0]) {
                    JsonArray array = new JsonArray();
                    for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getRange(start, end)) {
                        try {
                            array.add(capsule.export());
                        } catch (JsonFormattingException ex) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + capsule.getUUID() + "\n" + ex);
                        }
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(directory, name + "(" + start.toFileSafeDateString() + "_to_" + end.toFileSafeDateString() + ")-Transactions.xtbl", array, encrypt);
                }

                if(flags[1]) {
                    JsonArray array = new JsonArray();
                    for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                        try {
                            array.add(capsule.export());
                        } catch (JsonFormattingException ex) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + capsule.getName() + "\n" + ex);
                        }
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(directory, name + "-Budgets.xtbl", array, encrypt);
                }

                if(flags[2]) {
                    JsonArray array = new JsonArray();
                    for (TemplateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.getTemplates()) {
                        try {
                            array.add(capsule.export());
                        } catch (JsonFormattingException ex) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + capsule.getName() + "\n" + ex);
                        }
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(directory, name + "-Templates.xtbl", array, encrypt);
                }

                if(flags[3]) {
                    JsonArray array = new JsonArray();
                    for (StateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.STATES.getRange(start, end)) {
                        array.add(capsule.export());
                    }
                    CURRENT_INSTANCE.FILE_HANDLER.writeEncryptJson(directory, name + "-States.xtbl", array, encrypt);
                }
            }
            case "XARC" -> {
                try{
                    EncryptionHandler encrypt = UnkPasswordGui.getTestPassword(caller, "exports", CURRENT_INSTANCE);
                    if(flags[0]) {
                        JsonArray array = new JsonArray();
                        for (TransactionCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TRANSACTIONS.getRange(start, end)) {
                            try {
                                array.add(capsule.export());
                            } catch (JsonFormattingException ex) {
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Transaction Entry: " + capsule.getUUID() + "\n" + ex);
                            }
                        }
                        JsonItem.save(array, new XarcOutputStream(directory, name + "(" + start.toFileSafeDateString() + "_to_" + end.toFileSafeDateString() + ")-Transactions.xtbl", encrypt, CURRENT_INSTANCE));
                    }

                    if(flags[1]) {
                        JsonArray array = new JsonArray();
                        for (BudgetCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.BUDGETS.getBudgets()) {
                            try {
                                array.add(capsule.export());
                            } catch (JsonFormattingException ex) {
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Budget Entry: " + capsule.getName() + "\n" + ex);
                            }
                        }
                        JsonItem.save(array, new XarcOutputStream(directory, name + "-Budgets.xtbl", encrypt, CURRENT_INSTANCE));
                    }

                    if(flags[2]) {
                        JsonArray array = new JsonArray();
                        for (TemplateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.TEMPLATES.getTemplates()) {
                            try {
                                array.add(capsule.export());
                            } catch (JsonFormattingException ex) {
                                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged Template Entry: " + capsule.getName() + "\n" + ex);
                            }
                        }
                        JsonItem.save(array, new XarcOutputStream(directory, name + "-Templates.xtbl", encrypt, CURRENT_INSTANCE));
                    }

                    if(flags[3]) {
                        JsonArray array = new JsonArray();
                        for (StateCapsule capsule : CURRENT_INSTANCE.DATA_HANDLER.STATES.getRange(start, end)) {
                            array.add(capsule.export());
                        }
                        JsonItem.save(array, new XarcOutputStream(directory, name + "-States.xtbl", encrypt, CURRENT_INSTANCE));
                    }
                } catch (IOException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Error in output\n" + e);
                }
            }
        }
    }
}
