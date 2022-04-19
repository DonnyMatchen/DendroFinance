package com.donny.dendrofinance.data;

import com.donny.dendrofinance.entry.BudgetEntry;
import com.donny.dendrofinance.entry.TransactionEntry;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;

import java.io.File;

public class ExportHandler {
    private final Instance CURRENT_INSTANCE;
    private final File DIR;

    public ExportHandler(Instance curInst) {
        CURRENT_INSTANCE = curInst;
        DIR = new File(CURRENT_INSTANCE.data.getPath() + File.separator + "Exports");
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), "ExportHandler initiated");
    }

    public void export() {
        if (!DIR.exists()) {
            DIR.mkdir();
        }
        File transactions = new File(DIR.getPath() + File.separator + "Transactions.json");
        JsonArray array = new JsonArray();
        for (TransactionEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readTransactions()) {
            try {
                array.ARRAY.add(entry.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Damaged Transaction Entry: " + entry.getUUID());
            }
        }
        CURRENT_INSTANCE.FILE_HANDLER.write(transactions, array.toString());

        File budgets = new File(DIR.getPath() + File.separator + "Budgets.json");
        array = new JsonArray();
        for (BudgetEntry entry : CURRENT_INSTANCE.DATA_HANDLER.readBudgets()) {
            try {
                array.ARRAY.add(entry.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Damaged Budget Entry: " + entry.getUUID());
            }
        }
        CURRENT_INSTANCE.FILE_HANDLER.write(budgets, array.toString());
    }
}
