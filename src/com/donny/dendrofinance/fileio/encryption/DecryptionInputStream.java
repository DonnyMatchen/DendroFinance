package com.donny.dendrofinance.fileio.encryption;

import com.donny.dendrofinance.instance.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DecryptionInputStream extends FileInputStream {
    private final Instance CURRENT_INSTANCE;
    private final EncryptionHandler ENCRYPTION_HANDLER;
    private final byte[] BUFFER, IN_BUFFER;
    private final int BLOCK_SIZE;
    private int cursor = 0;
    private boolean end = false;
    /*
     * 0: not initiated
     * 1: initial space passed
     * 2: incorrect password or other error
     * 3: everything is correct
     */
    private int status = 0;

    public DecryptionInputStream(String name, EncryptionHandler handler, Instance curInst) throws IOException {
        super(name);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
        BLOCK_SIZE = super.read() * 16;
        BUFFER = new byte[BLOCK_SIZE];
        IN_BUFFER = new byte[BLOCK_SIZE + 16];
    }

    public DecryptionInputStream(File file, EncryptionHandler handler, Instance curInst) throws IOException {
        super(file);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
        BLOCK_SIZE = super.read() * 16;
        BUFFER = new byte[BLOCK_SIZE];
        IN_BUFFER = new byte[BLOCK_SIZE + 16];
    }

    public DecryptionInputStream(String name, Instance curInst) throws IOException {
        this(name, curInst.ENCRYPTION_HANDLER, curInst);
    }

    public DecryptionInputStream(File file, Instance curInst) throws IOException {
        this(file, curInst.ENCRYPTION_HANDLER, curInst);
    }

    public String getStatus() {
        return switch (status) {
            case 0 -> "AWAITING_TEST";
            case 1 -> "FAILED_TEST";
            case 2 -> "PASSED_TEST";
            default -> "UNKNOWN";
        };
    }

    public boolean check() {
        if (status == 0) {
            try {
                read();
                cursor--;
            } catch (IOException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong checking status\n" + e);
                return false;
            }
        }
        return status >= 2;
    }

    @Override
    public int read() throws IOException {
        if (cursor == 0) {
            if (end) {
                return -1;
            }
            Arrays.fill(IN_BUFFER, (byte) 0);
            for (int i = 0; i < BLOCK_SIZE + 16; i++) {
                int x = super.read();
                if (x == -1) {
                    end = true;
                    break;
                } else {
                    IN_BUFFER[i] = (byte) x;
                }
            }
            byte[] decod = ENCRYPTION_HANDLER.decrypt(IN_BUFFER);
            if (decod == null || decod.length < 6) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong: " + getStatus());
                return -1;
            }
            System.arraycopy(decod, 0, BUFFER, 0, BLOCK_SIZE);
            if (status == 0) {
                status++;
                // "passwd" = 112 97 115 115 119 100
                if (
                        BUFFER[0] == (byte) 112
                                && BUFFER[1] == (byte) 97
                                && BUFFER[2] == (byte) 115
                                && BUFFER[3] == (byte) 115
                                && BUFFER[4] == (byte) 119
                                && BUFFER[5] == (byte) 100
                ) {
                    status++;
                    cursor = 6;
                } else {
                    CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Decryption failed\n" + Arrays.toString(BUFFER));
                }
            } else if (status == 1) {
                return -1;
            }
        }
        int read = cursor;
        cursor++;
        if (cursor == BLOCK_SIZE) {
            cursor = 0;
        }
        if (BUFFER[read] == 0 && end) {
            boolean padding = true;
            for (int i = read; i < BLOCK_SIZE; i++) {
                if (BUFFER[i] != 0) {
                    padding = false;
                    break;
                }
            }
            if (padding) {
                return -1;
            }
        }
        return BUFFER[read];
    }

    @Override
    public int read(byte[] b) throws IOException {
        int count = 0;
        for (int i = 0; i < b.length; i++) {
            int x = read();
            if (x == -1) {
                if (i == 0) {
                    return -1;
                } else {
                    return count;
                }
            }
            b[i] = (byte) read();
            count++;
        }
        return count;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count = 0;
        for (int i = 0; i < len; i++) {
            int x = read();
            if (x == -1) {
                if (i == 0) {
                    return -1;
                } else {
                    return count;
                }
            }
            b[off + i] = (byte) x;
            count++;
        }
        return count;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        int count = 0;
        ArrayList<Byte> temp = new ArrayList<>();
        int x = 0;
        while (x != -1) {
            x = read();
            if (x != -1) {
                temp.add((byte) x);
                count++;
            }
        }
        byte[] out = new byte[count];
        for (int i = 0; i < count; i++) {
            out[i] = temp.get(i);
        }
        return out;
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        byte[] out = new byte[len];
        for (int i = 0; i < len; i++) {
            int x = read();
            if (x == -1) {
                return out;
            }
            out[i] = (byte) x;
        }
        return out;
    }

    @Override
    public long skip(long n) throws IOException {
        for (int i = 0; i < n; i++) {
            int x = read();
            if (x == -1) {
                return i;
            }
        }
        return n;
    }

    @Override
    public int available() {
        CURRENT_INSTANCE.LOG_HANDLER.warn(getClass(), "Projection of remaining bytes is not supported");
        throw new UnsupportedOperationException();
    }
}
