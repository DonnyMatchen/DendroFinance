package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.util.ExportableToJson;

import javax.swing.*;

public abstract class BackingEditGui<E extends ExportableToJson> extends JDialog {
    protected final Instance CURRENT_INSTANCE;
    protected final BackingTableCore<E> TABLE;
    protected final int INDEX;

    protected final JPanel BACK;

    public BackingEditGui(BackingTableGui<E> caller, BackingTableCore<E> core, int index, Instance curInst) {
        super(caller, true);
        TABLE = core;
        INDEX = index;
        CURRENT_INSTANCE = curInst;

        BACK = new JPanel();
        initComponents();
        add(BACK);
        pack();
        setVisible(true);
    }

    protected abstract void initComponents();
}
