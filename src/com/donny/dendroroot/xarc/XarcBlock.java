package com.donny.dendroroot.xarc;

public class XarcBlock {
    /**
     * this takes the offset in the plaintext and converts it to block index and block offset
     *
     * @param offset    offset in plaintext
     * @param blockSize size of blocks defined by profile in bytes
     * @return int[]{blockIndex, blockOffset}
     */
    public static long[] translate(long offset, int blockSize) {
        return new long[]{offset / ((long) blockSize * 16), offset % ((long) blockSize * 16)};
    }

    /**
     * this returns the ciphertext offset of the beginning of the given block
     *
     * @param blockIndex index of the block
     * @param blockSize  size of blocks defined by profile in bytes
     * @return ciphertext file index
     */
    public static long cipherOffset(long blockIndex, int blockSize) {
        return 1 + blockIndex * ((long) blockSize * 16 + 16);
    }
}
