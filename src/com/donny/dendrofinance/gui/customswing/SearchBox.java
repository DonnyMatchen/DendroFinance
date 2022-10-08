package com.donny.dendrofinance.gui.customswing;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

public class SearchBox extends JPanel {
    private final JLabel TITLE;
    private final JTextField SEARCH;
    private final JList<String> LIST;
    private ArrayList<String> master;

    public SearchBox(String name, ArrayList<String> master) {
        super();
        this.master = master;

        //gui setup
        {
            setBorder(null);
            TITLE = new JLabel(name);
            SEARCH = new JTextField();
            LIST = DendroFactory.getList();
            JScrollPane pane = DendroFactory.getScrollPane(false, true);
            pane.setViewportView(LIST);

            SEARCH.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateList(SEARCH.getText());
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateList(SEARCH.getText());
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateList(SEARCH.getText());
                }
            });

            //Group Layout
            {
                GroupLayout main = new GroupLayout(this);
                setLayout(main);
                main.setHorizontalGroup(
                        main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                TITLE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addComponent(
                                SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        ).addComponent(
                                pane, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE
                        )
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addComponent(
                                TITLE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                SEARCH, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                pane, 21, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        )
                );
            }
        }
        updateList("");
    }

    public void setMaster(ArrayList<String> newMaster) {
        master = newMaster;
        updateList(SEARCH.getText());
    }

    public void clear() {
        SEARCH.setText("");
    }

    public void setTitle(String title) {
        TITLE.setText(title);
    }

    public int getSelectedIndex() {
        return LIST.getMaxSelectionIndex();
    }

    public String getSelectedItem() {
        int x = LIST.getSelectedIndex();
        if (x < 0) {
            return null;
        } else {
            return LIST.getModel().getElementAt(x);
        }
    }

    public boolean setSelectedIndex(String item) {
        for (int i = 0; i < LIST.getModel().getSize(); i++) {
            if (LIST.getModel().getElementAt(i).equals(item)) {
                LIST.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    public boolean setSelectedIndex(int index) {
        if (index < 0 || index >= LIST.getModel().getSize()) {
            return false;
        } else {
            LIST.setSelectedIndex(index);
            return true;
        }
    }

    public int getListSize() {
        return LIST.getModel().getSize();
    }

    public void addListSelectionListener(ListSelectionListener listener) {
        LIST.addListSelectionListener(listener);
    }

    public ListSelectionListener[] getListSelectionListeners() {
        return LIST.getListSelectionListeners();
    }

    public void removeListSelectionListener(ListSelectionListener listener) {
        LIST.removeListSelectionListener(listener);
    }

    public void setListBackground(Color color) {
        LIST.setBackground(color);
    }

    private void updateList(String term) {
        ((DefaultListModel<String>) LIST.getModel()).removeAllElements();
        master.forEach(s -> {
            if (s.toLowerCase().contains(term.toLowerCase())) {
                ((DefaultListModel<String>) LIST.getModel()).add(LIST.getModel().getSize(), s);
            }
        });
    }
}
