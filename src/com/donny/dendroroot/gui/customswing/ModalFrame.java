package com.donny.dendroroot.gui.customswing;

import com.donny.dendroroot.instance.Instance;

import javax.swing.*;

public class ModalFrame extends JDialog {
    protected final Instance CURRENT_INSTANCE;

    public ModalFrame(JFrame caller, String name, Instance curInst) {
        super(caller, name, true);
        CURRENT_INSTANCE = curInst;
    }
}
