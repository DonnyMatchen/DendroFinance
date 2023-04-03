package com.donny.dendroroot.data;

import com.donny.dendroroot.instance.Instance;
import com.donny.dendroroot.util.ExportableToJson;

public abstract class Capsule implements ExportableToJson {
    protected final Instance CURRENT_INSTANCE;

    public Capsule(Instance curInst) {
        CURRENT_INSTANCE = curInst;
    }
}
