package com.donny.dendrofinance.fileio;

import com.donny.dendrofinance.instance.Instance;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DecryptionInputStream extends FileInputStream {
    private final Instance CURRENT_INSTANCE;
    private final EncryptionHandler ENCRYPTION_HANDLER;
    private final StringBuilder INTAKE = new StringBuilder();
    private byte[] buffer = null;
    private int cursor = 0;
    private boolean end = false;
    /*
     * 0: not initiated
     * 1: initial space passed
     * 2: incorrect password or other error
     * 3: everything is correct
     */
    private int status = 0;

    public DecryptionInputStream(String name, EncryptionHandler handler, Instance curInst) throws FileNotFoundException {
        super(name);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
    }

    public DecryptionInputStream(File file, EncryptionHandler handler, Instance curInst) throws FileNotFoundException {
        super(file);
        CURRENT_INSTANCE = curInst;
        ENCRYPTION_HANDLER = handler;
    }

    public DecryptionInputStream(String name, Instance curInst) throws FileNotFoundException {
        this(name, curInst.ENCRYPTION_HANDLER, curInst);
    }

    public DecryptionInputStream(File file, Instance curInst) throws FileNotFoundException {
        this(file, curInst.ENCRYPTION_HANDLER, curInst);
    }

    public String getStatus() {
        return switch (status) {
            case 0 -> "NOT_INITIATED";
            case 1 -> "INITIATED_AWAITING_TEST";
            case 2 -> "INITIATED_FAILED_TEST";
            case 3 -> "INITIATED_PASSED_TEST";
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
        return status > 2;
    }

    @Override
    public int read() throws IOException {
        if (cursor == 0) {
            if (end) {
                return -1;
            }
            boolean flag = true;
            while (flag) {
                int x = super.read();
                if (x == -1) {
                    end = true;
                    x = ' ';
                }
                if (x == ' ') {
                    if (status == 0) {
                        status = 1;
                    } else {
                        byte[] decod = ENCRYPTION_HANDLER.decrypt(INTAKE.toString());
                        if (decod == null || decod.length < 6) {
                            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Something went wrong: " + getStatus());
                            return -1;
                        }
                        buffer = decod;
                        INTAKE.setLength(0);
                        if (status == 1) {
                            status = 2;
                            // "passwd" = 112 97 115 115 119 100
                            if (
                                    buffer[0] == (byte) 112
                                            && buffer[1] == (byte) 97
                                            && buffer[2] == (byte) 115
                                            && buffer[3] == (byte) 115
                                            && buffer[4] == (byte) 119
                                            && buffer[5] == (byte) 100
                            ) {
                                status = 3;
                                cursor = 6;
                            } else {
                                System.out.println(Arrays.toString(buffer));
                            }
                        }
                        flag = false;
                    }
                } else {
                    INTAKE.append((char) x);
                }
            }
        }
        int read = cursor;
        cursor++;
        if (cursor == buffer.length) {
            cursor = 0;
        }
        if (buffer[read] == 0 && end) {
            boolean padding = true;
            for (int i = read; i < buffer.length; i++) {
                if (buffer[i] != 0) {
                    padding = false;
                    break;
                }
            }
            if (padding) {
                return -1;
            }
        }
        return buffer[read];
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
