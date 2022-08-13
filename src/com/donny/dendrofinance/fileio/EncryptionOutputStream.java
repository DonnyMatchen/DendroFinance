package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.instance.Instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptionOutputStream extends FileOutputStream {
    private final Instance CURRENT_INSTANCE;
    private final EncryptionHandler ENCRYPTION_HANDLER;

    private final byte[] BUFFER = new byte[16];
    private int cursor = 0;

    public EncryptionOutputStream(String name, EncryptionHandler handler, Instance curInst) throws FileNotFoundException {
        super(name);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
    }

    public EncryptionOutputStream(File file, EncryptionHandler handler, Instance curInst) throws FileNotFoundException {
        super(file);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
    }

    public EncryptionOutputStream(String name, Instance curInst) throws FileNotFoundException {
        this(name, curInst.ENCRYPTION_HANDLER, curInst);
    }

    public EncryptionOutputStream(File file, Instance curInst) throws FileNotFoundException {
        this(file, curInst.ENCRYPTION_HANDLER, curInst);
    }

    @Override
    public void write(int b) throws IOException {
        if (cursor == 0) {
            for (int i = 0; i < 16; i++) {
                BUFFER[i] = 0;
            }
        }
        BUFFER[cursor] = (byte) b;
        cursor++;
        if (cursor == 16) {
            cursor = 0;
            super.write((" " + ENCRYPTION_HANDLER.encrypt(BUFFER)).getBytes(Instance.CHARSET));
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (byte by : b) {
            write(by);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            write(b[off + i]);
        }
    }

    @Override
    public void close() throws IOException {
        boolean check = false;
        for (byte b : BUFFER) {
            if (b != 0) {
                check = true;
                break;
            }
        }
        if (check) {
            super.write((" " + ENCRYPTION_HANDLER.encrypt(BUFFER)).getBytes(Instance.CHARSET));
        }
        super.close();
    }
}
