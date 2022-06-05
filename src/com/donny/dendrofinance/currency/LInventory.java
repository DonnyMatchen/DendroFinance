package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.entry.totals.Position;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

public class LInventory extends LCurrency {
    private final BigDecimal STATIC_VALUE;
    private final boolean COMMODITY, MERCHANDISE;

    public LInventory(String name, String ticker, String symbol, int places, BigDecimal val, boolean merch, Instance curInst) {
        super(name, ticker, false, symbol + "§", true, places, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = false;
        STATIC_VALUE = val;
    }

    public LInventory(String name, String ticker, String symbol, int places, boolean merch, Instance curInst) {
        super(name, ticker, false, symbol + "§", true, places, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = true;
        STATIC_VALUE = BigDecimal.ZERO;
    }

    public LInventory(String name, BigDecimal val, boolean merch, Instance curInst) {
        super(name, "", false, "§", true, 0, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = false;
        STATIC_VALUE = val;
    }

    public LInventory(JsonObject obj, Instance curInst) {
        super(
                obj.getString("name").getString(),
                obj.getString("tic").getString(),
                false,
                obj.getString("symbol").getString() + "§", true,
                obj.getDecimal("places").decimal.intValue(), "", false, false, curInst
        );
        MERCHANDISE = obj.getString("flags").getString().contains("M");
        COMMODITY = obj.getString("flags").getString().contains("C");
        STATIC_VALUE = COMMODITY || MERCHANDISE ? BigDecimal.ZERO : obj.getDecimal("value").decimal;
    }

    public BigDecimal getUnitValue() {
        return STATIC_VALUE;
    }

    public boolean isCommodity() {
        return COMMODITY;
    }

    public boolean isMerchandise() {
        return MERCHANDISE;
    }

    @Override
    public BigDecimal getTotal(BigDecimal amount) {
        if (COMMODITY) {
            return super.getTotal(amount);
        } else if (MERCHANDISE) {
            Position p = CURRENT_INSTANCE.DATA_HANDLER.getPosition(this);
            if (p == null) {
                return BigDecimal.ZERO;
            } else {
                return amount.multiply(p.collapse()[2]);
            }
        } else {
            return amount.multiply(STATIC_VALUE);
        }
    }

    @Override
    public BigDecimal getTotal(BigDecimal amount, LDate date) {
        if (COMMODITY) {
            return super.getTotal(amount, date);
        } else if (MERCHANDISE) {
            Position p = CURRENT_INSTANCE.DATA_HANDLER.getPosition(this, date);
            if (p == null) {
                return BigDecimal.ZERO;
            } else {
                return amount.multiply(p.collapse()[2]);
            }
        } else {
            return amount.multiply(STATIC_VALUE);
        }
    }

    @Override
    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("name", new JsonString(getName()));
        obj.put("tic", new JsonString(getTicker()));
        obj.put("symbol", new JsonString(getSymbol().replace("§", "")));
        obj.put("places", new JsonDecimal(getPlaces()));
        if (COMMODITY || MERCHANDISE) {
            String q = "";
            if (COMMODITY) {
                q += "C";
            }
            if (MERCHANDISE) {
                q += "M";
            }
            obj.put("flags", new JsonString(q));
        } else {
            obj.put("flags", new JsonString("c"));
            obj.put("value", new JsonDecimal(STATIC_VALUE));
        }
        return obj;
    }

    @Override
    public boolean first() {
        for (LInventory i : CURRENT_INSTANCE.INVENTORIES) {
            if (i.getTicker().equals(getTicker())) {
                return i == this;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (first()) {
            return "I!" + getTicker();
        } else {
            return getName();
        }
    }
}
