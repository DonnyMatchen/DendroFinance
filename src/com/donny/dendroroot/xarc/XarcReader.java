package com.donny.dendroroot.xarc;

import com.donny.dendroroot.fileio.EncryptionHandler;
import com.donny.dendroroot.instance.Instance;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class XarcReader extends Xarc {
    private final byte[] IN_BUFFER, BUFFER;
    private boolean eof;

    public XarcReader(File file, int blockSize, EncryptionHandler handler, Instance curInst) throws IOException {
        super(file, "r", blockSize, handler, curInst);
        BUFFER = new byte[blockSize * 16];
        IN_BUFFER = new byte[blockSize * 16 + 16];
        if (!test()) {
            throw new IOException("Unable to verify Xarc");
        }
        block = 1;
        cursor = 0;
    }

    public XarcReader(File file, EncryptionHandler handler, Instance curInst) throws IOException {
        this(file, curInst.blockSize, handler, curInst);
    }

    public XarcReader(XarcWriter writer) throws IOException {
        this(new File(writer.FILE_NAME), writer.blockSize, writer.HANDLER, writer.CURRENT_INSTANCE);
    }

    public boolean test() {
        try {
            FILE.seek(0);
            blockSize = FILE.read();
            loadBlock(0);
            return BUFFER[0] == 'p' && BUFFER[1] == 'a' && BUFFER[2] == 's' && BUFFER[3] == 's' && BUFFER[4] == 'w' && BUFFER[5] == 'd';
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to test xarc: " + FILE_NAME + "\n" + e);
            return false;
        }
    }

    public void goTo(long blockIndex, int blockOffset, boolean load) {
        cursor = blockOffset;
        if (block != blockIndex) {
            block = blockIndex;
            if (load) {
                loadBlock();
            }
        }
    }

    @Override
    public void goTo(long blockIndex, int blockOffset) {
        goTo(blockIndex, blockOffset, true);
    }

    public void goTo(long blockIndex, boolean load) {
        goTo(blockIndex, 0, load);
    }

    public int read() {
        boolean end = eof;
        if (!end) {
            if (cursor == 0) {
                boolean q = loadBlock();
                if (!q) {
                    end = true;
                }
            }
        }
        if (!end) {
            int read = BUFFER[cursor];
            if (read == 0) {
                end = true;
                for (int i = cursor; i < BUFFER.length; i++) {
                    if (BUFFER[i] != 0) {
                        end = false;
                        break;
                    }
                }
                if (end) {
                    read = -1;
                }
            }
            cursor++;
            if (cursor == BUFFER.length) {
                cursor = 0;
                block++;
            }
            return read;
        } else {
            return -1;
        }
    }

    public byte[] read(int n) {
        byte[] out = new byte[n];
        for (int i = 0; i < n; i++) {
            int x = read();
            if (x != -1) {
                out[i] = (byte) x;
            } else {
                break;
            }
        }
        return out;
    }

    public int read(byte[] buffer, int offset, int length) {
        for (int i = 0; i < length; i++) {
            int x = read();
            if (x != -1) {
                buffer[i + offset] = (byte) x;
            } else {
                return i;
            }
        }
        return length;
    }

    public long skip(long n) {
        long b = block;
        int c = cursor;
        goTo(XarcBlock.translate(block * blockSize + cursor + n, blockSize));
        if (eof) {
            goTo(b, c);
            for (long i = 0; i < n; i++) {
                int test = read();
                if (test == -1) {
                    return i;
                }
            }
        }
        return n;
    }

    protected boolean loadBlock(long blockIndex, byte[] buffer) {
        try {
            FILE.seek(XarcBlock.cipherOffset(blockIndex, blockSize));
            int n = FILE.read(IN_BUFFER, 0, IN_BUFFER.length);
            if (n <= 0) {
                eof = true;
            } else {
                Arrays.fill(buffer, (byte) 0);
                System.arraycopy(HANDLER.decrypt(IN_BUFFER), 0, buffer, 0, buffer.length);
                eof = false;
            }
        } catch (IOException e) {
            CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Failed to read block " + blockIndex + " from Xarc: " + FILE_NAME);
            eof = true;
        }
        return !eof;
    }

    /**
     * grabs a block of ciphertext and decrypts it, placing the plaintext in BUFFER
     *
     * @param blockIndex index of the block to be read
     */
    public boolean loadBlock(long blockIndex) {
        return loadBlock(blockIndex, BUFFER);
    }

    public boolean loadBlock() {
        return loadBlock(block);
    }
}
