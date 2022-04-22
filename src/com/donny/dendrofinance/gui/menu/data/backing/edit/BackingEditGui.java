package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.util.ExportableToJsonObject;

import javax.swing.*;

public abstract class BackingEditGui<E extends ExportableToJsonObject> extends JDialog {
    protected final Instance CURRENT_INSTANCE;
    protected final BackingTableCore<E> TABLE;
    protected final int INDEX;

    public BackingEditGui(BackingTableGui<E> caller, BackingTableCore<E> core, int index, Instance curInst) {
        super(caller, true);
        TABLE = core;
        INDEX = index;
        CURRENT_INSTANCE = curInst;
        initComponents();
        pack();
        setVisible(true);
    }

    protected abstract void initComponents();
}
