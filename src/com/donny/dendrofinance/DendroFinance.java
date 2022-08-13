package com.donny.dendrofinance;

import com.donny.dendrofinance.instance.Instance;

import java.util.ArrayList;
import java.util.Random;

public class DendroFinance {
    public static final ArrayList<Instance> INSTANCES = new ArrayList<>();

    public static String newIid() {
        ArrayList<String> iids = new ArrayList<>();
        for (Instance i : INSTANCES) {
            iids.add(i.IID);
        }
        while (true) {
            byte[] attempt = new byte[64];
            new Random().nextBytes(attempt);
            String candidate = new String(attempt, Instance.CHARSET);
            if (!iids.contains(candidate)) {
                return candidate;
            }
        }
    }

    public static void main(String[] args) {
        Instance curInst = getInstance(args);
    }

    public static Instance getInstance(String[] args) {
        return new Instance(args);
    }
}
