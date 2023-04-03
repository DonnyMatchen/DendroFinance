package com.donny.dendroroot.xarc;

import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.instance.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class XarcInputStream extends FileInputStream {
    private final XarcReader READER;

    public XarcInputStream(File file, EncryptionHandler handler, Instance curInst) throws IOException {
        super(file);
        READER = new XarcReader(file, handler, curInst);
    }

    public XarcInputStream(File file, Instance curInst) throws IOException {
        super(file);
        READER = new XarcReader(file, curInst.ENCRYPTION_HANDLER, curInst);
    }

    @Override
    public int read() {
        return READER.read();
    }

    @Override
    public int read(byte[] bytes) {
        return read(bytes, 0, bytes.length);
    }

    @Override
    public int read(byte[] bytes, int offs, int len) {
        return READER.read(bytes, offs, len);
    }

    @Override
    public byte[] readNBytes(int n) {
        return READER.read(n);
    }

    @Override
    public int readNBytes(byte[] bytes, int offs, int len) {
        return READER.read(bytes, offs, len);
    }

    @Override
    public byte[] readAllBytes() {
        ArrayList<Byte> bytes = new ArrayList<>();
        boolean flag = true;
        while (flag) {
            int x = READER.read();
            if (x != -1) {
                bytes.add((byte) x);
            } else {
                flag = false;
            }
        }
        byte[] out = new byte[bytes.size()];
        for (int i = 0; i < out.length; i++) {
            out[i] = bytes.get(i);
        }
        return out;
    }

    @Override
    public long skip(long n) {
        return READER.skip(n);
    }

    @Override
    public void close() throws IOException {
        READER.close();
        super.close();
    }
}
