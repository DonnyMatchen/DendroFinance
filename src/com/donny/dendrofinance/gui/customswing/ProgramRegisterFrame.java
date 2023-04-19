package com.donny.dendrofinance.gui.customswing;

import com.donny.dendrofinance.gui.ProgramMainGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.RegisterFrame;

public abstract class ProgramRegisterFrame extends RegisterFrame {
    protected final ProgramInstance CURRENT_INSTANCE;
    protected final ProgramMainGui CALLER;

    public ProgramRegisterFrame(ProgramMainGui caller, String name, ProgramInstance curInst) {
        super(caller, name, curInst);
        CURRENT_INSTANCE = curInst;
        CALLER = caller;
    }
}
