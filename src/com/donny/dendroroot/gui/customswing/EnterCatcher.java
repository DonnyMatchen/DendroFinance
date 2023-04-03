package com.donny.dendroroot.gui.customswing;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class EnterCatcher extends DocumentFilter {
    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
        super.insertString(fb, offs, str.replace("\r", "").replace("\n", ""), a);
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
        super.replace(fb, offs, length, str.replace("\r", "").replace("\n", ""), a);
    }
}
