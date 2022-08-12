package com.donny.dendrofinance.fileio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class EncryptionOutputStream extends FileOutputStream {
    public EncryptionOutputStream(String name) throws FileNotFoundException {
        super(name);
    }

    public EncryptionOutputStream(String name, boolean append) throws FileNotFoundException {
        super(name, append);
    }

    public EncryptionOutputStream(File file) throws FileNotFoundException {
        super(file);
    }

    public EncryptionOutputStream(File file, boolean append) throws FileNotFoundException {
        super(file, append);
    }
}
