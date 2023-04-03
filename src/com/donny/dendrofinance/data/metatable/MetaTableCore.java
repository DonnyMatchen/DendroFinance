package com.donny.dendrofinance.data.metatable;

import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.types.LDate;

import java.util.ArrayList;

public abstract class MetaTableCore {
    protected final ProgramInstance CURRENT_INSTANCE;

    public MetaTableCore(ProgramInstance curInst) {
        CURRENT_INSTANCE = curInst;
    }

    public abstract String getId(boolean plural);

    public abstract String[] getHeader();

    public abstract ArrayList<String[]> getContents(LDate date, String search);

    public abstract String getName();

    public abstract String print(String identifier, String name, LDate date);

    protected static class ProcessReturn {
        public boolean all, dead;
        public String reduced;

        public ProcessReturn(String raw) {
            all = false;
            dead = false;
            reduced = raw;
            if (reduced.contains("$")) {
                if (reduced.toLowerCase().contains("$a")) {
                    all = true;
                    reduced = reduced.replace("$a", "").trim();
                }
                if (reduced.toLowerCase().contains("$d")) {
                    dead = true;
                    reduced = reduced.replace("$d", "").trim();
                }
            }
        }
    }
}
