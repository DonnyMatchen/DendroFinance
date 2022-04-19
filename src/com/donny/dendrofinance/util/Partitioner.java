package com.donny.dendrofinance.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class Partitioner {
    public static ArrayList<String[]> partitionString(ArrayList<String> list, int size) {
        ArrayList<String> clone = new ArrayList<>(list);
        ArrayList<String[]> partitions = new ArrayList<>();
        String[] array;
        while (!clone.isEmpty()) {
            array = new String[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Integer[]> partitionInteger(ArrayList<Integer> list, int size) {
        ArrayList<Integer> clone = new ArrayList<>(list);
        ArrayList<Integer[]> partitions = new ArrayList<>();
        Integer[] array;
        while (!clone.isEmpty()) {
            array = new Integer[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Long[]> partitionLong(ArrayList<Long> list, int size) {
        ArrayList<Long> clone = new ArrayList<>(list);
        ArrayList<Long[]> partitions = new ArrayList<>();
        Long[] array;
        while (!clone.isEmpty()) {
            array = new Long[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Short[]> partitionShort(ArrayList<Short> list, int size) {
        ArrayList<Short> clone = new ArrayList<>(list);
        ArrayList<Short[]> partitions = new ArrayList<>();
        Short[] array;
        while (!clone.isEmpty()) {
            array = new Short[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Byte[]> partitionByte(ArrayList<Byte> list, int size) {
        ArrayList<Byte> clone = new ArrayList<>(list);
        ArrayList<Byte[]> partitions = new ArrayList<>();
        Byte[] array;
        while (!clone.isEmpty()) {
            array = new Byte[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Double[]> partitionDouble(ArrayList<Double> list, int size) {
        ArrayList<Double> clone = new ArrayList<>(list);
        ArrayList<Double[]> partitions = new ArrayList<>();
        Double[] array;
        while (!clone.isEmpty()) {
            array = new Double[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Float[]> partitionFloat(ArrayList<Float> list, int size) {
        ArrayList<Float> clone = new ArrayList<>(list);
        ArrayList<Float[]> partitions = new ArrayList<>();
        Float[] array;
        while (!clone.isEmpty()) {
            array = new Float[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Boolean[]> partitionBoolean(ArrayList<Boolean> list, int size) {
        ArrayList<Boolean> clone = new ArrayList<>(list);
        ArrayList<Boolean[]> partitions = new ArrayList<>();
        Boolean[] array;
        while (!clone.isEmpty()) {
            array = new Boolean[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Character[]> partitionCharacter(ArrayList<Character> list, int size) {
        ArrayList<Character> clone = new ArrayList<>(list);
        ArrayList<Character[]> partitions = new ArrayList<>();
        Character[] array;
        while (!clone.isEmpty()) {
            array = new Character[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<BigInteger[]> partitionBigInteger(ArrayList<BigInteger> list, int size) {
        ArrayList<BigInteger> clone = new ArrayList<>(list);
        ArrayList<BigInteger[]> partitions = new ArrayList<>();
        BigInteger[] array;
        while (!clone.isEmpty()) {
            array = new BigInteger[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<BigDecimal[]> partitionBigDecimal(ArrayList<BigDecimal> list, int size) {
        ArrayList<BigDecimal> clone = new ArrayList<>(list);
        ArrayList<BigDecimal[]> partitions = new ArrayList<>();
        BigDecimal[] array;
        while (!clone.isEmpty()) {
            array = new BigDecimal[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }

    public static ArrayList<Object[]> partitionObject(ArrayList<Object> list, int size) {
        ArrayList<Object> clone = new ArrayList<>(list);
        ArrayList<Object[]> partitions = new ArrayList<>();
        Object[] array;
        while (!clone.isEmpty()) {
            array = new Object[size];
            for (int i = 0; i < size; i++) {
                if (!clone.isEmpty()) {
                    array[i] = clone.get(0);
                    clone.remove(0);
                } else {
                    array[i] = null;
                }
            }
            partitions.add(array);
        }
        return partitions;
    }
}
