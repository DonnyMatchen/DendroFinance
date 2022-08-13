package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.instance.Instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptionOutputStream extends FileOutputStream {
    private final Instance CURRENT_INSTANCE;
    private final EncryptionHandler ENCRYPTION_HANDLER;

    private final byte[] BUFFER;
    private final int BLOCK_SIZE;
    private int cursor = 0;

    public EncryptionOutputStream(String name, EncryptionHandler handler, int blockSize, Instance curInst) throws FileNotFoundException {
        super(name);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
        BLOCK_SIZE = blockSize * 16;
        BUFFER = new byte[BLOCK_SIZE];
    }

    public EncryptionOutputStream(File file, EncryptionHandler handler, int blockSize, Instance curInst) throws FileNotFoundException {
        super(file);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
        BLOCK_SIZE = blockSize * 16;
        BUFFER = new byte[BLOCK_SIZE];
    }

    public EncryptionOutputStream(String name, int blockSize, Instance curInst) throws FileNotFoundException {
        this(name, curInst.ENCRYPTION_HANDLER, blockSize, curInst);
    }

    public EncryptionOutputStream(File file, int blockSize, Instance curInst) throws FileNotFoundException {
        this(file, curInst.ENCRYPTION_HANDLER, blockSize, curInst);
    }

    public EncryptionOutputStream(String name, EncryptionHandler handler, Instance curInst) throws FileNotFoundException {
        this(name, handler, curInst.blockSize, curInst);
    }

    public EncryptionOutputStream(File file, EncryptionHandler handler, Instance curInst) throws FileNotFoundException {
        this(file, handler, curInst.blockSize, curInst);
    }

    public EncryptionOutputStream(String name, Instance curInst) throws FileNotFoundException {
        this(name, curInst.ENCRYPTION_HANDLER, curInst.blockSize, curInst);
    }

    public EncryptionOutputStream(File file, Instance curInst) throws FileNotFoundException {
        this(file, curInst.ENCRYPTION_HANDLER, curInst.blockSize, curInst);
    }

    @Override
    public void write(int b) throws IOException {
        if (cursor == 0) {
            for (int i = 0; i < BLOCK_SIZE; i++) {
                BUFFER[i] = 0;
            }
        }
        BUFFER[cursor] = (byte) b;
        cursor++;
        if (cursor == BLOCK_SIZE) {
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
