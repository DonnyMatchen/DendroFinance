package com.donny.dendrofinance.data.metatable;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import java.util.ArrayList;

public abstract class MetaTableCore {
    protected final Instance CURRENT_INSTANCE;

    public MetaTableCore(Instance curInst) {
        CURRENT_INSTANCE = curInst;
    }

    public abstract String[] getHeader();

    public abstract ArrayList<String[]> getContents(LDate date, String search);

    public abstract String getName();

    public abstract String print(String identifier, String name, LDate date);
}
