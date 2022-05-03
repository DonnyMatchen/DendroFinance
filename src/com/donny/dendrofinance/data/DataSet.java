package com.donny.dendrofinance.data;

import com.donny.dendrofinance.entry.Entry;
import com.donny.dendrofinance.entry.EntryType;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;
import com.donny.dendrofinance.json.JsonObject;

import java.io.File;
import java.util.ArrayList;

public class DataSet<E extends Entry> {
    private final Instance CURRENT_INSTANCE;
    private final EntryType TYPE;
    private final File ARCHIVE;
    private final ArrayList<E> TABLE;
    private boolean emptyDueToDeletion = false;

    public DataSet(String name, EntryType type, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        TYPE = type;
        ARCHIVE = new File(curInst.data.getPath() + File.separator + "Entries" + File.separator + name + ".xtbl");
        TABLE = new ArrayList<>();
    }

    public void reload() throws JsonFormattingException {
        TABLE.clear();
        if (ARCHIVE.exists()) {
            String raw = CURRENT_INSTANCE.FILE_HANDLER.readDecrypt(ARCHIVE);
            if (raw.contains("passwd")) {
                raw = raw.substring(6);
            }
            JsonArray entries = (JsonArray) JsonItem.sanitizeDigest(raw);
            for (JsonObject obj : entries.getObjectArray()) {
                TABLE.add((E) Entry.get(TYPE, obj, CURRENT_INSTANCE));
            }
        }
    }

    public void save() {
        if (!TABLE.isEmpty()) {
            long uuid = 0;
            try {
                JsonArray array = new JsonArray();
                for (E entry : TABLE) {
                    uuid = entry.getUUID();
                    array.add(entry.export());
                }
                CURRENT_INSTANCE.FILE_HANDLER.writeEncrypt(ARCHIVE, "passwd" + array);
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Entry " + uuid + " Failed formatting!");
            }
        } else {
            if (ARCHIVE.exists()) {
                CURRENT_INSTANCE.FILE_HANDLER.delete(ARCHIVE);
            }
        }
    }

    public boolean add(E element) {
        emptyDueToDeletion = false;
        return TABLE.add(element);
    }

    public boolean remove(E element) {
        boolean removed = TABLE.remove(element);
        if (TABLE.isEmpty()) {
            emptyDueToDeletion = true;
        }
        return removed;
    }

    public ArrayList<E> read() throws JsonFormattingException {
        if (TABLE.isEmpty() && !emptyDueToDeletion) {
            reload();
        }
        return new ArrayList<>(TABLE);
    }
}
