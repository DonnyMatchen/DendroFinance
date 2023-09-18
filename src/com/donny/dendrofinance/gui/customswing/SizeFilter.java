package com.donny.dendrofinance.gui.customswing;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class SizeFilter extends DocumentFilter {
    public static void install(JTextField field, int size) {
        AbstractDocument doc = (AbstractDocument) field.getDocument();
        doc.setDocumentFilter(new SizeFilter(size));
    }

    public static void install(JTextArea area, int size) {
        AbstractDocument doc = (AbstractDocument) area.getDocument();
        doc.setDocumentFilter(new SizeFilter(size));
    }

    private final int LIMIT;

    public SizeFilter(int limit) {
        super();
        LIMIT = limit;
    }

    @Override
    public void insertString(FilterBypass fb, int offs, String str, AttributeSet a) throws BadLocationException {
        int length = LIMIT - fb.getDocument().getLength();
        if (length > 0) {
            if (length < str.length()) {
                str = str.substring(0, length);
            }
            super.insertString(fb, offs, str, a);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    @Override
    public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet a) throws BadLocationException {
        int len = LIMIT - (fb.getDocument().getLength() - length);
        if (len > 0) {
            if (len < str.length()) {
                str = str.substring(0, length);
            }
            super.replace(fb, offs, length, str, a);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
