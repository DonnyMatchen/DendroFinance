package com.donny.dendrofinance.capsules;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.util.ExportableToJson;

public abstract class Capsule implements ExportableToJson {
    protected final Instance CURRENT_INSTANCE;

    public Capsule(Instance curInst) {
        CURRENT_INSTANCE = curInst;
    }
}
