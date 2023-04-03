package com.donny.dendroroot.gui.customswing;

import com.donny.dendroroot.gui.form.Validation;
import com.donny.dendroroot.gui.form.ValidationFailedException;
import com.donny.dendroroot.instance.Instance;
import com.donny.dendroroot.types.LDate;

import javax.swing.*;
import java.awt.event.KeyListener;

public class DateRange extends JPanel {
    private final JTextField START, END;

    public DateRange(boolean defaultLabel) {
        super();

        //gui
        {
            setBorder(null);
            JLabel a = new JLabel("Date Range"), b = new JLabel("to");
            START = new JTextField();
            END = new JTextField();

            //group layout
            {
                if (defaultLabel) {
                    GroupLayout main = new GroupLayout(this);
                    setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addComponent(
                                    a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    START, GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    END, GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE
                            )
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            START, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            END, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    );
                } else {
                    GroupLayout main = new GroupLayout(this);
                    setLayout(main);
                    main.setHorizontalGroup(
                            main.createSequentialGroup().addComponent(
                                    START, GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                            ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                    END, GroupLayout.PREFERRED_SIZE, 300, Short.MAX_VALUE
                            )
                    );
                    main.setVerticalGroup(
                            main.createSequentialGroup().addGroup(
                                    main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                            START, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            b, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    ).addComponent(
                                            END, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                    )
                            )
                    );
                }

            }
        }
    }

    public void init(LDate start, LDate end) {
        START.setText(start.toDateString());
        END.setText(end.toDateString());
    }

    public void initRange(int range, Instance curInst) {
        LDate now = LDate.now(curInst);
        LDate temp = LDate.getRange(range, now, curInst);
        init(temp, now);
    }

    public LDate[] getRange(Instance curInst) {
        LDate start = null, end = null;
        try {
            start = Validation.validateDate(START, curInst);
            START.setText(start.toDateString());
        } catch (ValidationFailedException e) {
            curInst.LOG_HANDLER.error(getClass(), "Bad date: " + START.getText());
        }
        try {
            end = Validation.validateDate(END, curInst);
            END.setText(end.toDateString());
        } catch (ValidationFailedException e) {
            curInst.LOG_HANDLER.error(getClass(), "Bad date: " + END.getText());
        }
        if (start != null && end != null) {
            return new LDate[]{
                    LDate.startDay(start), LDate.endDay(end)
            };
        } else {
            return null;
        }
    }

    @Override
    public void addKeyListener(KeyListener l) {
        START.addKeyListener(l);
        END.addKeyListener(l);
    }

    public void setEditable(boolean editable) {
        START.setEditable(editable);
        START.setBackground(editable ? DendroFactory.CONTENT : DendroFactory.DISABLED);
        END.setEditable(editable);
        END.setBackground(editable ? DendroFactory.CONTENT : DendroFactory.DISABLED);
    }
}
