package com.donny.dendroroot.xarc;

import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.instance.Instance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XarcOutputStream extends FileOutputStream {
    private final XarcWriter WRITER;

    public XarcOutputStream(File file, EncryptionHandler handler, Instance curInst) throws IOException {
        super(file);
        curInst.FILE_HANDLER.ensure(file);
        WRITER = new XarcWriter(file, handler, curInst);
    }

    public XarcOutputStream(File dir, String name, EncryptionHandler handler, Instance curInst) throws IOException {
        this(new File(dir.getAbsolutePath() + File.separator + name), handler, curInst);
    }

    @Override
    public void write(int b) {
        WRITER.write(b);
    }

    @Override
    public void write(byte[] bytes) {
        WRITER.write(bytes);
    }

    public void write(String str) {
        WRITER.write(str);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        WRITER.write(bytes, offset, length);
    }

    @Override
    public void close() throws IOException {
        WRITER.close();
        super.close();
    }
}
