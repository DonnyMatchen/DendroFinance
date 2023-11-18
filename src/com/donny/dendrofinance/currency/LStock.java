package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;

import java.math.BigDecimal;

public class LStock extends LCurrency {
    private final boolean PUBLIC;

    public LStock(String name, String ticker, boolean publik, BigDecimal factor, boolean dead, ProgramInstance curInst) {
        super(name, ticker, false, "§", true, 6, factor, "", false, dead, curInst);
        PUBLIC = publik;
    }

    public LStock(JsonObject obj, ProgramInstance curInst) {
        this(
                obj.getString("name").getString(),
                obj.getString("tic").getString(),
                !obj.getString("flags").getString().contains("P"),
                obj.containsKey("factor") ? obj.getDecimal("factor").decimal : BigDecimal.ONE,
                obj.getString("flags").getString().contains("D"),
                curInst
        );
    }

    public boolean isPublic() {
        return PUBLIC;
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(getName()));
        obj.put("tic", new JsonString(getTicker()));
        String flags = "";
        if (isDead()) {
            flags += "D";
        }
        if (!PUBLIC) {
            flags += "P";
        }
        obj.put("flags", new JsonString(flags));
        if (getFactor().compareTo(BigDecimal.ONE) != 0) {
            obj.put("factor", new JsonDecimal(getFactor()));
        }
        return obj;
    }

    @Override
    public boolean first() {
        for (LStock s : CURRENT_INSTANCE.STOCKS) {
            if (s.getTicker().equals(getTicker())) {
                return s == this;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (first()) {
            return "S!" + getTicker();
        } else {
            return getName();
        }
    }
}
