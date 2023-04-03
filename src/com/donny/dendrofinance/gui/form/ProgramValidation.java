package com.donny.dendrofinance.gui.form;

import com.donny.dendrofinance.account.AWColumn;
import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendrofinance.types.LAccountSet;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.form.ValidationFailedException;

import javax.swing.*;

public class ProgramValidation {
    public static LAccountSet validateAccountSet(JTextArea field, ProgramInstance curInst) throws ValidationFailedException {
        String raw = field.getText();
        raw = raw.replace("{", "").replace("}", "").replace(" ", "");
        for (String seg : raw.split(",")) {
            if (!seg.contains("!") || !seg.contains("(") || !seg.contains(")") ||
                    seg.split("!").length != 2 || seg.split("\\(").length != 2) {
                field.setBackground(DendroFactory.WRONG);
                field.select(field.getText().indexOf(seg), field.getText().indexOf(seg) + seg.length());
                throw new ValidationFailedException("Segment \"" + seg + "\" is not a valid ACV");
            }
            String[] first = seg.split("!");
            if (AWColumn.fromString(first[0]) == AWColumn.TRACKER) {
                if (!first[0].equals("T")) {
                    field.select(field.getText().indexOf(seg), field.getText().indexOf(seg) + first[0].length());
                    field.setBackground(DendroFactory.WRONG);
                    throw new ValidationFailedException("Segment \"" + seg + "\" is not in a valid Column");
                }
            }
            String[] second = first[1].replace(")", "").split("\\(");
            Account a = curInst.ACCOUNTS.getElement(second[0]);
            if (a == null) {
                field.setBackground(DendroFactory.WRONG);
                field.select(field.getText().indexOf(second[0]), field.getText().indexOf(second[0]) + second[0].length());
                throw new ValidationFailedException("Segment \"" + seg + "\" is not in a valid account");
            }
            try {
                Double.parseDouble(second[1]);
            } catch (NumberFormatException ex) {
                field.setBackground(DendroFactory.WRONG);
                field.select(field.getText().indexOf(second[1]), field.getText().indexOf(second[1]) + second[1].length());
                throw new ValidationFailedException("Segment \"" + seg + "\" does not have a valid value");
            }
        }
        field.setBackground(DendroFactory.CONTENT);
        return new LAccountSet(field.getText(), curInst);
    }
}
