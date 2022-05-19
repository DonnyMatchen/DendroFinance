package com.donny.dendrofinance.types;

import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonItem;

public interface LType<E> {
    boolean sameAs(E b);

    int compare(E b);

    JsonItem export() throws JsonFormattingException;

    boolean isDefault();
}
