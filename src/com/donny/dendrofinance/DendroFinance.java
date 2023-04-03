package com.donny.dendrofinance;

import com.donny.dendrofinance.instance.ProgramInstance;

import java.util.ArrayList;
import java.util.Random;

public class DendroFinance {
    public static final ArrayList<ProgramInstance> INSTANCES = new ArrayList<>();

    public static String newIid() {
        ArrayList<String> iids = new ArrayList<>();
        for (ProgramInstance i : INSTANCES) {
            iids.add(i.IID);
        }
        while (true) {
            byte[] attempt = new byte[64];
            new Random().nextBytes(attempt);
            String candidate = new String(attempt, ProgramInstance.CHARSET);
            if (!iids.contains(candidate)) {
                return candidate;
            }
        }
    }

    public static void main(String[] args) {
        ProgramInstance curInst = getInstance(args);
    }

    public static ProgramInstance getInstance(String[] args) {
        return new ProgramInstance(args);
    }
}
