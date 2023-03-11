package com.donny.dendrofinance.gui.menu.data.state;

import com.donny.dendrofinance.capsules.StateCapsule;
import com.donny.dendrofinance.fileio.ImportHandler;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.gui.form.Validation;
import com.donny.dendrofinance.gui.form.ValidationFailedException;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import javax.swing.*;

public class NewStateGui extends ModalFrame {
    private final StateGui CALLER;
    private final JTextField DATE;

    public NewStateGui(StateGui caller, Instance curInst) {
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
                    CURRENT_INSTANCE.DATA_HANDLER.DATABASE.STATES.add(new StateCapsule(date, CURRENT_INSTANCE), ImportHandler.ImportMode.OVERWRITE);
                    CALLER.updateDate();
                    dispose();
                } catch (ValidationFailedException e) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Bad Date\n" + e);
                }
            });
        }
    }
}
