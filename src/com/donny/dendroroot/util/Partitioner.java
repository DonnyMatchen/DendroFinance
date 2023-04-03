package com.donny.dendroroot.util;

import java.util.ArrayList;

public class Partitioner {
    public static ArrayList<byte[]> partition(byte[] array, int partitionSize) {
        if (partitionSize < 1) {
            throw new IllegalArgumentException("Partition Size must be a positive number!");
        }
        ArrayList<byte[]> out = new ArrayList<>();
        int cursor = 0;
        byte[] current = null;
        for (byte b : array) {
            if (cursor == 0) {
                if (current != null) {
                    out.add(current);
                }
                current = new byte[partitionSize];
            }
            current[cursor] = b;
            cursor++;
            if (cursor == partitionSize) {
                cursor = 0;
            }
        }
        boolean flag = false;
        for (byte b : current) {
            if (b != 0) {
                flag = true;
                break;
            }
        }
        if (flag) {
            out.add(current);
        }
        return out;
    }

    public static ArrayList<int[]> partition(int[] array, int partitionSize) {
        if (partitionSize < 1) {
            throw new IllegalArgumentException("Partition Size must be a positive number!");
        }
        ArrayList<int[]> out = new ArrayList<>();
        int cursor = 0;
        int[] current = null;
        for (int i : array) {
            if (cursor == 0) {
                if (current != null) {
                    out.add(current);
                }
                current = new int[partitionSize];
            }
            current[cursor] = i;
            cursor++;
            if (cursor == partitionSize) {
                cursor = 0;
            }
        }
        boolean flag = false;
        for (int i : current) {
            if (i != 0) {
                flag = true;
                break;
            }
        }
        if (flag) {
            out.add(current);
        }
        return out;
    }

    public static ArrayList<long[]> partition(long[] array, int partitionSize) {
        if (partitionSize < 1) {
            throw new IllegalArgumentException("Partition Size must be a positive number!");
        }
        ArrayList<long[]> out = new ArrayList<>();
        int cursor = 0;
        long[] current = null;
        for (long l : array) {
            if (cursor == 0) {
                if (current != null) {
                    out.add(current);
                }
                current = new long[partitionSize];
            }
            current[cursor] = l;
            cursor++;
            if (cursor == partitionSize) {
                cursor = 0;
            }
        }
        boolean flag = false;
        for (long l : current) {
            if (l != 0) {
                flag = true;
                break;
            }
        }
        if (flag) {
            out.add(current);
        }
        return out;
    }

    public static ArrayList<double[]> partition(double[] array, int partitionSize) {
        if (partitionSize < 1) {
            throw new IllegalArgumentException("Partition Size must be a positive number!");
        }
        ArrayList<double[]> out = new ArrayList<>();
        int cursor = 0;
        double[] current = null;
        for (double d : array) {
            if (cursor == 0) {
                if (current != null) {
                    out.add(current);
                }
                current = new double[partitionSize];
            }
            current[cursor] = d;
            cursor++;
            if (cursor == partitionSize) {
                cursor = 0;
            }
        }
        boolean flag = false;
        for (double d : current) {
            if (d != 0) {
                flag = true;
                break;
            }
        }
        if (flag) {
            out.add(current);
        }
        return out;
    }
}
