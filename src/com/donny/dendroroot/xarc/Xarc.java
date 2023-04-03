package com.donny.dendroroot.xarc;

import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.instance.Instance;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public abstract class Xarc {
    protected final Instance CURRENT_INSTANCE;
    protected final EncryptionHandler HANDLER;
    protected final RandomAccessFile FILE;
    protected final String FILE_NAME;
    protected int blockSize;

    protected int cursor = 0;
    protected long block = 0;

    public Xarc(File file, String mode, int blockSize, EncryptionHandler handler, Instance curInst) throws IOException {
        FILE = new RandomAccessFile(file, mode);
        FILE_NAME = file.getAbsolutePath();
        HANDLER = handler;
        CURRENT_INSTANCE = curInst;
        this.blockSize = blockSize;
    }

    public Xarc(File file, String mode, EncryptionHandler handler, Instance curInst) throws IOException {
        this(file, mode, curInst.blockSize, handler, curInst);
    }

    public abstract void goTo(long blockIndex, int blockOffset);

    public void goTo(long blockIndex) {
        goTo(blockIndex, 0);
    }

    public void goTo(long[] location) {
        goTo(location[0], (int) location[1]);
    }

    public void close() throws IOException {
        FILE.close();
    }


}
