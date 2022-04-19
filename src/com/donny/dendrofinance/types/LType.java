package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;

public abstract class LType<E> {
    public abstract boolean sameAs(E b);

    public abstract int compare(E b);

    public abstract JsonItem export() throws JsonFormattingException;

    public abstract boolean isDefault();
}
