package com.donny.dendrofinance.data.database;

import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonObject;

public abstract class TableHandler<Identifier, Capsule> {
    protected final ProgramInstance CURRENT_INSTANCE;
    protected final DatabaseHandler DATABASE;

    public TableHandler(ProgramInstance curInst, DatabaseHandler database) {
        CURRENT_INSTANCE = curInst;
        DATABASE = database;
    }

    public abstract boolean add(Capsule capsule, ImportHandler.ImportMode mode);

    public abstract boolean add(JsonObject object, ImportHandler.ImportMode mode);

    public boolean update(Capsule capsule) {
        return add(capsule, ImportHandler.ImportMode.OVERWRITE);
    }

    public boolean update(JsonObject object) {
        return add(object, ImportHandler.ImportMode.OVERWRITE);
    }

    public abstract Capsule get(Identifier identifier);

    public abstract boolean delete(Identifier identifier);
}
