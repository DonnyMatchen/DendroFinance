package com.donny.dendrofinance.gui.password;

import com.donny.dendrofinance.data.EncryptionHandler;
import com.donny.dendrofinance.gui.customswing.DendroFactory;
import com.donny.dendrofinance.gui.customswing.ModalFrame;
import com.donny.dendrofinance.instance.Instance;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UnkPasswordGui extends ModalFrame {
    private final EncryptionHandler ENC_HAND;

    private final JPasswordField PASSWORD;
    private final JLabel A;
    private final JButton ENTER, DEFAULT;

    private boolean defaultPassword = false;

    public static EncryptionHandler getTestPassword(JFrame caller, String nameFor, Instance curInst) {
        UnkPasswordGui unk = new UnkPasswordGui(caller, nameFor, curInst);
        unk.setVisible(true);
        EncryptionHandler ret = unk.getEncryptionHandler();
        unk.dispose();
        return ret;
    }

    private UnkPasswordGui(JFrame caller, String nameFor, Instance curInst) {
        super(caller, "Password for " + nameFor, curInst);
        ENC_HAND = new EncryptionHandler(CURRENT_INSTANCE);

        //draw gui
        {
            A = new JLabel("Password");
            PASSWORD = new JPasswordField();
            PASSWORD.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    if (event.getKeyCode() == 10) {
                        enterPressed();
                    }
                }
            });
            ENTER = DendroFactory.getButton("Enter");
            ENTER.addActionListener(event -> enterPressed());
            DEFAULT = DendroFactory.getButton("Use Profile Password");
            DEFAULT.addActionListener(event -> defaultPressed());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PASSWORD, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        A, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PASSWORD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        ENTER, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
            pack();
        }
    }

    private EncryptionHandler getEncryptionHandler() {
        return defaultPassword ? CURRENT_INSTANCE.ENCRYPTION_HANDLER : ENC_HAND.keysInitiated() ? ENC_HAND : null;
    }

    private void enterPressed() {
        ENC_HAND.changeKey(PASSWORD.getPassword());
        PASSWORD.setText("");
        if (!ENC_HAND.keysInitiated()) {
            CURRENT_INSTANCE.LOG_HANDLER.fatal(getClass(), "Password failed to set");
        }
        dispose();
    }

    private void defaultPressed() {
        PASSWORD.setText("");
        defaultPassword = true;
        dispose();
    }
}
