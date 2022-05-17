package com.donny.dendrofinance.header;

import com.donny.dendrofinance.entry.Field;
import com.donny.dendrofinance.instance.Instance;

import java.util.ArrayList;

public abstract class Header {
    protected final ArrayList<Field> PROTOTYPE;

    public Header() {
        PROTOTYPE = new ArrayList<>();
    }

    public static Header getHeader(String type, Instance curInst) {
        return switch (type) {
            case "TransactionHeader" -> new TransactionHeader(curInst);
            case "BudgetHeader" -> new BudgetHeader();
            default -> null;
        };
    }

    public ArrayList<Field> getBlank() {
        return new ArrayList<>(PROTOTYPE);
    }

    public ArrayList<Field> getProto() {
        return new ArrayList<>(PROTOTYPE);
    }

    public abstract String getName();
}
