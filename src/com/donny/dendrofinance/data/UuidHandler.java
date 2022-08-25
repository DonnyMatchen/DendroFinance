package com.donny.dendrofinance.data;

import com.donny.dendrofinance.instance.Instance;

import java.security.SecureRandom;
import java.util.ArrayList;

public class UuidHandler {
    public final ArrayList<Long> UUIDS;

    public UuidHandler(Instance curInst) {
        UUIDS = new ArrayList<>();
        curInst.LOG_HANDLER.trace(getClass(), "UuidHandler Initiated");
    }

    public long generateUUID() {
        SecureRandom rand = new SecureRandom();
        boolean flag = true;
        long candidate = 0;
        while (flag) {
            candidate = rand.nextLong();
            flag = check(candidate);
        }
        UUIDS.add(candidate);
        return candidate;
    }

    public boolean check(long l) {
        if (l == 0) {
            return true;
        }
        return UUIDS.contains(l);
    }
}
