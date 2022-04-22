package com.donny.dendrofinance.gui.customswing;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ItemField extends JPanel {
    private final JScrollPane TEXT_PANE, LIST_PANE;
    private final JTextArea TEXT;
    private final JList<String> LIST;
    private final ArrayList<String> MASTER;

    public ItemField(ArrayList<String> master) {
        super();
        MASTER = master;

        //gui setup
        {
            this.setBorder(null);
            TEXT_PANE = DendroFactory.getLongField();
            TEXT = (JTextArea) TEXT_PANE.getViewport().getView();
            LIST_PANE = DendroFactory.getScrollPane(false, true);
            LIST = new JList<>(new DefaultListModel<>());
            LIST_PANE.setViewportView(LIST);
            LIST.addMouseListener(new MouseInputAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    super.mouseClicked(event);
                    if (!TEXT.getText().equals("")) {
                        String check = getToken(TEXT.getText());
                        TEXT.setText(TEXT.getText().substring(0, TEXT.getText().lastIndexOf(check)) + LIST.getSelectedValue() + "(), ");
                    }
                }
            });
            TEXT.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateList(TEXT.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateList(TEXT.getText());
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateList(TEXT.getText());
                }
            });

            //group layout
            {
                GroupLayout main = new GroupLayout(this);
                setLayout(main);
                main.setHorizontalGroup(
                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                TEXT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addComponent(
                                LIST_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        )
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addComponent(
                                TEXT_PANE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                LIST_PANE, 0, GroupLayout.PREFERRED_SIZE, 65
                        )
                );
            }
        }
    }

    public void updateList(String text) {
        ((DefaultListModel<String>) LIST.getModel()).removeAllElements();
        if (!text.equals("")) {
            MASTER.forEach(string -> {
                if (string.toLowerCase().contains(getToken(text).toLowerCase())) {
                    ((DefaultListModel<String>) LIST.getModel()).addElement(string);
                }
            });
        }
    }

    public String getText() {
        return TEXT.getText();
    }

    public void setText(String text) {
        TEXT.setText(text);
    }

    private String getToken(String text) {
        StringBuilder sb = new StringBuilder();
        boolean ignore = false;
        for (char c : text.toCharArray()) {
            if (c == '(') {
                ignore = true;
            }
            if (!ignore) {
                sb.append(c);
            }
            if (c == ')') {
                ignore = false;
            }
        }
        String[] tokens = sb.toString().replace(" ", "").split(",");
        if (tokens[tokens.length - 1].split("!").length == 2) {
            return tokens[tokens.length - 1].split("!")[1];
        } else {
            return tokens[tokens.length - 1];
        }
    }

    public void setTextBackground(Color color) {
        TEXT.setBackground(color);
    }
}
