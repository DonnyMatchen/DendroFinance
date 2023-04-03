package com.donny.dendroroot.gui;

import com.donny.dendroroot.gui.customswing.RegisterFrame;
import com.donny.dendroroot.instance.Instance;

import javax.swing.*;
import java.util.ArrayList;

public class MainGui extends JFrame {
    public final ArrayList<RegisterFrame> FRAME_REGISTRY;
    private final Instance CURRENT_INSTANCE;

    public MainGui(String name, Instance curInst) {
        super(name);
        CURRENT_INSTANCE = curInst;
        FRAME_REGISTRY = new ArrayList<>();
    }

    public void conclude(boolean save, boolean exit) {
        CURRENT_INSTANCE.conclude(save);
        ArrayList<RegisterFrame> temp = new ArrayList<>(FRAME_REGISTRY);
        for (RegisterFrame frame : temp) {
            frame.dispose();
        }
        super.dispose();
        if (exit) {
            System.exit(0);
        }
    }

    @Override
    public void dispose() {
        new ClosePrompt(this, true).setVisible(true);
    }
}
