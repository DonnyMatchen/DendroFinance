package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;

public class LStock extends LCurrency {
    private final boolean PUBLIC;

    public LStock(String name, String ticker, boolean publik, boolean dead, Instance curInst) {
        super(name, ticker, false, "§", true, 6, "", false, dead, curInst);
        PUBLIC = publik;
    }

    public LStock(JsonObject obj, Instance curInst) {
        this(
                obj.getString("name").getString(),
                obj.getString("tic").getString(),
                !obj.getString("flags").getString().contains("P"),
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
