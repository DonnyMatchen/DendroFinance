package com.donny.dendrofinance.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DecryptionInputStream extends FileInputStream {
    public DecryptionInputStream(String name) throws FileNotFoundException {
        super(name);
    }

    public DecryptionInputStream(File file) throws FileNotFoundException {
        super(file);
    }
}
