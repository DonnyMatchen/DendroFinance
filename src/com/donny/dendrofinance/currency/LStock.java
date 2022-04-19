package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

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
    public BigDecimal getTotal(BigDecimal amount) {
        try {
            if (isDead()) {
                return BigDecimal.ZERO;
            } else {
                if (isPublic()) {
                    if (CURRENT_INSTANCE.stockAPI.equals("twelve")) {
                        return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.hitTwelveData(getTicker()));
                    } else if (CURRENT_INSTANCE.stockAPI.equals("polygon")) {
                        return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.hitPolygon(getTicker()));
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else {
                    return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.getLatestPrivateStock(getName()));
                }
            }
        } catch (NumberFormatException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "NumberFormatException for string: " + ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public BigDecimal getTotal(BigDecimal amount, LDate date) {
        try {
            if (isDead()) {
                return BigDecimal.ZERO;
            } else {
                if (isPublic()) {
                    if (CURRENT_INSTANCE.stockAPI.equals("twelve")) {
                        return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.hitTwelveData(getTicker(), date));
                    } else if (CURRENT_INSTANCE.stockAPI.equals("polygon")) {
                        return amount.multiply(CURRENT_INSTANCE.FILE_HANDLER.hitPolygon(getTicker(), date));
                    } else {
                        return BigDecimal.ZERO;
                    }
                } else {
                    JsonArray arr = CURRENT_INSTANCE.FILE_HANDLER.getPrivateStock(getName());
                    BigDecimal ret = BigDecimal.ZERO;
                    for (JsonObject obj : arr.getObjectArray()) {
                        if (new LDate(obj.getString("date").getString(), CURRENT_INSTANCE).compare(date) <= 0) {
                            ret = amount.multiply(obj.getDecimal("price").decimal);
                        }
                    }
                    return ret;
                }
            }
        } catch (NumberFormatException ex) {
            CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "NumberFormatException for string: " + ex.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.FIELDS.put("name", new JsonString(getName()));
        obj.FIELDS.put("tic", new JsonString(getTicker()));
        String flags = "";
        if (isDead()) {
            flags += "D";
        }
        if (!PUBLIC) {
            flags += "P";
        }
        obj.FIELDS.put("flags", new JsonString(flags));
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
