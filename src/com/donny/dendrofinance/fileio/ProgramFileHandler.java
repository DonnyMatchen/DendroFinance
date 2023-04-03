package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.fileio.FileHandler;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonItem;
import com.donny.dendroroot.json.JsonObject;
import com.fasterxml.jackson.core.JsonFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProgramFileHandler extends FileHandler {

    public ProgramFileHandler(ProgramInstance curInst) {
        super(curInst);
    }

    /*
     *  RESOURCES
     */

    public JsonItem getTemplate(String path) {
        try (InputStream stream = getClass().getResourceAsStream("/com/donny/dendrofinance/templates/" + path)) {
            JsonItem item = JsonItem.digest(new JsonFactory().createParser(stream));
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Resource loaded: " + path);
            return item;
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Resource not located: " + path);
            return null;
        } catch (NullPointerException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No such resource: " + path);
            return null;
        } catch (JsonFormattingException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Malformed resource: " + path);
            return null;
        }
    }

    /*
     *  PRIVATE PRICE HISTORIES
     */

    public JsonObject getPrivateStock(String name) {
        File directory = new File(CURRENT_INSTANCE.data.getAbsoluteFile() + File.separator + "P_Stock");
        JsonObject history = null;
        File[] directoryList = directory.listFiles();
        if (directory.exists() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(name)) {
                    history = (JsonObject) readJson(f);
                }
            }
        }
        if (history != null) {
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Private stock history read: " + name);
            return history;
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No Stock History File found: " + name);
            return null;
        }
    }

    public JsonObject getPrivateInventory(String id) {
        File directory = new File(CURRENT_INSTANCE.data.getAbsoluteFile() + File.separator + "P_Inventory");
        JsonObject history = null;
        File[] directoryList = directory.listFiles();
        if (directory.exists() && directoryList != null) {
            for (File f : directoryList) {
                if (f.getName().contains(id)) {
                    history = (JsonObject) readJson(f);
                }
            }
        }
        if (history != null) {
            CURRENT_INSTANCE.LOG_HANDLER.debug(getClass(), "Private inventory history read: " + id);
            return history;
        } else {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "No Inventory History File found: " + id);
            return null;
        }
    }
}
