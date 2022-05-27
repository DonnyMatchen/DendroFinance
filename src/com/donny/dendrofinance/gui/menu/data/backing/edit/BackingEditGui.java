package com.donny.dendrofinance.gui.menu.data.backing.edit;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.util.ExportableToJson;

public abstract class BackingEditGui<E extends ExportableToJson> extends ModalFrame {
    protected final BackingTableCore<E> TABLE;
    protected final int INDEX;

    public BackingEditGui(BackingTableGui<E> caller, BackingTableCore<E> core, int index, Instance curInst) {
        super(caller, (index >= 0 ? "Edit " : "New ") + core.getName(false), curInst);
        TABLE = core;
        INDEX = index;
        initComponents();
        pack();
        setVisible(true);
    }

    protected abstract void initComponents();

    protected abstract void saveAction();
}
