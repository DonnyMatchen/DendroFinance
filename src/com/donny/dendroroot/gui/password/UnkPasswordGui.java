package com.donny.dendroroot.gui.password;

import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.gui.customswing.DendroFactory;
import com.donny.dendroroot.gui.customswing.ModalFrame;
import com.donny.dendroroot.instance.Instance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UnkPasswordGui extends ModalFrame {
    private final EncryptionHandler ENC_HAND;

    private final JPasswordField PASSWORD;

    private boolean defaultPassword = false;

    public static EncryptionHandler getTestPassword(JFrame caller, String nameFor, Instance curInst) {
        UnkPasswordGui unk = new UnkPasswordGui(caller, nameFor, curInst);
        unk.setVisible(true);
        return unk.getEncryptionHandler();
    }

    private UnkPasswordGui(JFrame caller, String nameFor, Instance curInst) {
        super(caller, "Password for " + nameFor, curInst);
        ENC_HAND = new EncryptionHandler(CURRENT_INSTANCE);

        //draw gui
        {
            JLabel a = new JLabel("Password");
            PASSWORD = new JPasswordField();
            PASSWORD.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent event) {
                    if (event.getKeyCode() == 10) {
                        enterPressed();
                    }
                }
            });
            JButton enter = DendroFactory.getButton("Enter");
            enter.addActionListener(event -> enterPressed());
            JButton defaultButton = DendroFactory.getButton("Use Profile Password");
            defaultButton.addActionListener(event -> defaultPressed());

            //group layout
            {
                GroupLayout main = new GroupLayout(getContentPane());
                getContentPane().setLayout(main);
                main.setHorizontalGroup(
                        main.createSequentialGroup().addContainerGap().addComponent(
                                a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                PASSWORD, GroupLayout.PREFERRED_SIZE, 250, Short.MAX_VALUE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addGap(DendroFactory.SMALL_GAP).addComponent(
                                defaultButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                        ).addContainerGap()
                );
                main.setVerticalGroup(
                        main.createSequentialGroup().addContainerGap().addGroup(
                                main.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(
                                        a, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        PASSWORD, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        enter, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                ).addComponent(
                                        defaultButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE
                                )
                        ).addContainerGap()
                );
            }
        }
        pack();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(d.width / 2 - getWidth() / 2, d.height / 2 - getHeight() / 2);
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
