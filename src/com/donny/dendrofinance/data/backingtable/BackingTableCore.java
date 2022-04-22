package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.util.ExportableToJsonObject;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class BackingTableCore<E extends ExportableToJsonObject> implements Iterable<E> {
    public final boolean SORTABLE;
    protected final Instance CURRENT_INSTANCE;
    protected final ArrayList<E> TABLE;
    public boolean changed;

    public BackingTableCore(Instance curInst, boolean sort) {
        CURRENT_INSTANCE = curInst;
        TABLE = new ArrayList<>();
        SORTABLE = sort;
        changed = false;
    }

    public abstract void getEditDialog(BackingTableGui<E> caller, int index);

    public abstract void load(JsonArray array);

    public abstract String[] getHeader();

    public abstract ArrayList<String[]> getContents();

    public boolean add(E element) {
        if (TABLE.add(element)) {
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public int indexOf(E element) {
        return TABLE.indexOf(element);
    }

    public void clear() {
        TABLE.clear();
    }

    public boolean contains(E element) {
        return TABLE.contains(element);
    }

    public int size() {
        return TABLE.size();
    }

    public abstract String getIdentifier(int index);

    public abstract int getIndex(String identifier);

    public E getElement(String identifier) {
        int index = getIndex(identifier);
        if (index == -1) {
            return null;
        } else {
            return getElement(index);
        }
    }

    public E getElement(int index) {
        if (index >= 0 && index < TABLE.size()) {
            return TABLE.get(index);
        } else {
            return null;
        }
    }

    public boolean deleteElement(String identifier) {
        return deleteElement(getIndex(identifier));
    }

    public boolean deleteElement(int index) {
        if (TABLE.size() > index && index >= 0) {
            TABLE.remove(index);
            changed = true;
            return true;
        } else {
            return false;
        }
    }

    public abstract boolean canMove(int index);

    public abstract boolean canEdit(int index);

    public abstract boolean canRemove(int index);

    public boolean move(String identifier, boolean up) {
        return move(getIndex(identifier), up);
    }

    public boolean move(int index, boolean up) {
        if (index < 0) {
            return false;
        } else if (index == 0 && !up) {
            return false;
        } else if (index == TABLE.size() - 1 && up) {
            return false;
        } else {
            if (canMove(index + (up ? 1 : -1))) {
                E temp = TABLE.get(index);
                if (up) {
                    TABLE.remove(index);
                    TABLE.add(index + 1, temp);
                } else {
                    TABLE.remove(index);
                    TABLE.add(index - 1, temp);
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
            E x = getElement(i), y = getElement(j);
            deleteElement(i);
            deleteElement(j);
            TABLE.add(j, x);
            TABLE.add(i, y);
            return true;
        } else {
            return false;
        }
    }

    public abstract void sort();

    public abstract JsonArray export();

    public void replace(int index, E element) {
        TABLE.remove(index);
        TABLE.add(index, element);
    }

    @Override
    public Iterator<E> iterator() {
        return TABLE.iterator();
    }
}
