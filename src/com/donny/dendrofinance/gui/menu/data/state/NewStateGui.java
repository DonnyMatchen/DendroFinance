package com.donny.dendrofinance.gui.menu.data.state;

import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.customswing.ProgramModalFrame;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;

public class NewStateGui extends ProgramModalFrame {
    private final StateGui CALLER;
    private final JTextField DATE;

    public NewStateGui(StateGui caller, ProgramInstance curInst) {
        super(caller, "New State", curInst);
        CALLER = caller;

        //draw gui
        {
            DATE = new JTextField();

            JLabel a = new JLabel("Date and Time");
            JButton cancel = DendroFactory.getButton("Cancel");
            cancel.addActionListener(event -> dispose());
            JButton add = DendroFactory.getButton("Add");
            add.addActionListener(event -> {
                try {
                    LDate date = Validation.validateDate(DATE, CURRENT_INSTANCE);
                    CURRENT_INSTANCE.DATA_HANDLER.STATES.add(new StateCapsule(date, CURRENT_INSTANCE), ImportHandler.ImportMode.OVERWRITE);
                    CALLER.updateDate();
                    dispose();
                } catch (ValidationFailedException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Date\n" + e);
                }
            });
        }
    }
}
