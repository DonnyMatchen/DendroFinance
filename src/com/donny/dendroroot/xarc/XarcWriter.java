package com.donny.dendroroot.xarc;

import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.instance.Instance;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;

public class XarcWriter extends Xarc {
    private final byte[] BUFFER;
    private final XarcReader READER;

    public XarcWriter(File file, int blockSize, EncryptionHandler handler, Instance curInst) throws IOException {
        super(file, "rw", blockSize, handler, curInst);
        BUFFER = new byte[blockSize * 16];
        create();
        READER = new XarcReader(this);
    }

    public XarcWriter(File file, EncryptionHandler handler, Instance curInst) throws IOException {
        this(file, curInst.blockSize, handler, curInst);
    }

    public void create() throws IOException {
        FILE.seek(0);
        FILE.write(blockSize);
        SecureRandom random = new SecureRandom();
        random.nextBytes(BUFFER);
        BUFFER[0] = (byte) 'p';
        BUFFER[1] = (byte) 'a';
        BUFFER[2] = (byte) 's';
        BUFFER[3] = (byte) 's';
        BUFFER[4] = (byte) 'w';
        BUFFER[5] = (byte) 'd';
        writeBlock(0);
        block = 1;
        cursor = 0;
    }

    public void goTo(long blockIndex, int blockOffset, boolean load) {
        if (block != blockIndex) {
            if (cursor != 0) {
                writeBlock();
            }
            block = blockIndex;
            cursor = blockOffset;
            if (load) {
                if (!READER.loadBlock(block, BUFFER)) {
                    Arrays.fill(BUFFER, (byte) 0);
                }
            }
        } else {
            cursor = blockOffset;
        }
    }

    @Override
    public void goTo(long blockIndex, int blockOffset) {
        goTo(blockIndex, blockOffset, true);
    }

    public void write(int b) {
        BUFFER[cursor] = (byte) b;
        cursor++;
        if (cursor == BUFFER.length) {
            cursor = 0;
            writeBlock();
            block++;
            if (!READER.loadBlock(block, BUFFER)) {
                Arrays.fill(BUFFER, (byte) 0);
            }
        }
    }

    public void write(String string) {
        write(string.getBytes(Instance.CHARSET));
    }

    public void write(byte[] array) {
        for (byte b : array) {
            write(b);
        }
    }

    public void write(byte[] array, int offset, int length) {
        for (int i = 0; i < length; i++) {
            write(array[i + offset]);
        }
    }

    public void skip(long n) {
        goTo(XarcBlock.translate(block * blockSize + cursor + n, blockSize));
    }

    public void writeBlock(long blockIndex) {
        try {
            FILE.seek(XarcBlock.cipherOffset(blockIndex, blockSize));
            byte[] encrypted = HANDLER.encrypt(BUFFER);
            FILE.write(encrypted, 0, encrypted.length);
            Arrays.fill(BUFFER, (byte) 0);
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to write block " + blockIndex + " to Xarc: " + FILE_NAME);
        }
    }

    public void writeBlock() {
        writeBlock(block);
    }

    @Override
    public void close() throws IOException {
        if (cursor != 0) {
            writeBlock();
        }
        super.close();
    }
}
