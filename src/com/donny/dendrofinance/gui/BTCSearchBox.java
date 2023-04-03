package com.donny.dendrofinance.gui;

import com.donny.dendrofinance.data.backingtable.BackingTableCore;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.SearchBox;
import com.donny.dendroroot.util.UniqueName;

import java.util.ArrayList;

public class BTCSearchBox<E extends UniqueName> extends SearchBox<E> {

    public BTCSearchBox(String name, ArrayList<E> list, ProgramInstance curInst) {
        super(name, list, curInst);
    }

    public BTCSearchBox(String name, BackingTableCore<E> core, ProgramInstance curInst) {
        super(name, core.getMaster(), curInst);
    }

    public void setMaster(BackingTableCore<E> core) {
        setMaster(core.getMaster());
    }
}
