package com.donny.dendrofinance.gui.customswing;

import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.ModalFrame;

import javax.swing.*;

public class ProgramModalFrame extends ModalFrame {
    protected final ProgramInstance CURRENT_INSTANCE;

    public ProgramModalFrame(JFrame caller, String name, ProgramInstance curInst) {
        super(caller, name, curInst);
        CURRENT_INSTANCE = curInst;
    }
}
