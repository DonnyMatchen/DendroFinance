package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;

public abstract class RegisterFrame extends JFrame {
    protected final MainGui CALLER;
    protected final Instance CURRENT_INSTANCE;

    public RegisterFrame(MainGui caller, Instance curInst) {
        CALLER = caller;
        CURRENT_INSTANCE = curInst;
        CALLER.FRAME_REGISTRY.add(this);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        String[] clss = getClass().toString().split("\\.");
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), clss[clss.length - 1] + " created");
    }

    @Override
    public void dispose() {
        CALLER.FRAME_REGISTRY.remove(this);
        String[] clss = getClass().toString().split("\\.");
        CURRENT_INSTANCE.LOG_HANDLER.trace(this.getClass(), clss[clss.length - 1] + " destroyed");
        super.dispose();
    }
}
