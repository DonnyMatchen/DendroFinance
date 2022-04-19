package com.donny.dendrofinance.util;

import java.util.ArrayList;
import java.util.Collection;

public class Curation<E> extends ArrayList<E> {
    @Override
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        } else {
            return super.add(e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E obj : c) {
            if (this.add(obj)) {
                modified = true;
            }
        }
        return modified;
    }
}
