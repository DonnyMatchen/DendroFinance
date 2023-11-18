package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.util.ExportableToJson;
import com.donny.dendroroot.util.UniqueName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class BackingTableCore<E extends UniqueName> implements Iterable<E>, ExportableToJson {
    public final boolean SORTABLE;
    protected final ProgramInstance CURRENT_INSTANCE;
    protected final ArrayList<String> KEYS;
    protected final HashMap<String, E> MAP;
    public boolean changed;

    public BackingTableCore(ProgramInstance curInst, boolean sort) {
        CURRENT_INSTANCE = curInst;
        KEYS = new ArrayList<>();
        MAP = new HashMap<>();
        SORTABLE = sort;
        changed = false;
    }

    public abstract String getName(boolean plural);

    public abstract void getEditDialog(BackingTableGui<E> caller, int index);

    public abstract void load(JsonArray array);

    public abstract String[] getHeader();

    public abstract int contentIdentifierIndex();

    public abstract ArrayList<String[]> getContents(String search);

    public boolean add(E element) {
        if (MAP.containsKey(element.getName())) {
            return false;
        }
        if (KEYS.add(element.getName())) {
            MAP.put(element.getName(), element);
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public int indexOf(E element) {
        return KEYS.indexOf(element.getName());
    }

    public void clear() {
        KEYS.clear();
        MAP.clear();
    }

    public boolean contains(E element) {
        return KEYS.contains(element.getName()) && MAP.containsKey(element.getName());
    }

    public int size() {
        return KEYS.size();
    }

    public String getIdentifier(int index) {
        return KEYS.get(index);
    }

    public int getIndex(String identifier) {
        return KEYS.indexOf(identifier);
    }

    public E getElement(String identifier) {
        if (KEYS.contains(identifier)) {
            return MAP.get(identifier);
        } else {
            return null;
        }
    }

    public E getElement(int index) {
        if (index >= 0 && index < KEYS.size()) {
            return MAP.get(KEYS.get(index));
        } else {
            return null;
        }
    }

    public boolean deleteElement(String identifier) {
        if (KEYS.contains(identifier)) {
            KEYS.remove(identifier);
            MAP.remove(identifier);
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteElement(int index) {
        if (KEYS.size() > index && index >= 0) {
            String key = KEYS.get(index);
            KEYS.remove(index);
            MAP.remove(key);
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public abstract boolean canMove(String identifier);

    public abstract boolean canEdit(String identifier);

    public abstract boolean canRemove(String identifier);

    public boolean move(String identifier, boolean up) {
        int index = getIndex(identifier);
        if (index < 0) {
            return false;
        } else if (index == 0 && !up) {
            return false;
        } else if (index == KEYS.size() - 1 && up) {
            return false;
        } else {
            if (canMove(getIdentifier(index + (up ? 1 : -1)))) {
                String key = KEYS.get(index);
                if (up) {
                    KEYS.remove(index);
                    KEYS.add(index + 1, key);
                } else {
                    KEYS.remove(index);
                    KEYS.add(index - 1, key);
                }
                changed = true;
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean swap(String id1, String id2) {
        int i = getIndex(id1);
        int j = getIndex(id2);
        if (i >= 0 && j >= 0 && i != j) {
            String x = KEYS.get(i), y = KEYS.get(j);
            KEYS.remove(i);
            KEYS.remove(j);
            KEYS.add(j, x);
            KEYS.add(i, y);
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public abstract void sort();

    @Override
    public JsonArray export() {
        JsonArray out = new JsonArray();
        for (String key : KEYS) {
            try {
                out.add(MAP.get(key).export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to export: " + key + "\n" + e);
            }
        }
        return out;
    }

    public void replace(int index, E element) {
        String key = KEYS.get(index);
        KEYS.remove(key);
        MAP.remove(key);
        KEYS.add(index, element.getName());
        MAP.put(element.getName(), element);
        changed = true;
    }

    @Override
    public Iterator<E> iterator() {
        return getMaster().iterator();
    }

    public ArrayList<E> getMaster() {
        ArrayList<E> out = new ArrayList<>();
        for (String key : KEYS) {
            out.add(MAP.get(key));
        }
        return out;
    }
}
