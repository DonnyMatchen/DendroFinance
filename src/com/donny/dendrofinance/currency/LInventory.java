package com.donny.dendrofinance.currency;

import com.donny.dendrofinance.capsules.totals.Position;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.json.JsonString;
import com.donny.dendroroot.types.LDate;

import java.math.BigDecimal;

public class LInventory extends LCurrency {
    private final BigDecimal STATIC_VALUE;
    private final boolean COMMODITY, MERCHANDISE, PUBLIC;

    public LInventory(String name, String ticker, String symbol, int places, BigDecimal val, boolean merch, ProgramInstance curInst) {
        super(name, ticker, false, symbol + "§", true, places, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = false;
        PUBLIC = false;
        STATIC_VALUE = val;
    }

    public LInventory(String name, String ticker, String symbol, int places, boolean merch, boolean pub, ProgramInstance curInst) {
        super(name, ticker, false, symbol + "§", true, places, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = true;
        PUBLIC = pub;
        STATIC_VALUE = BigDecimal.ZERO;
    }

    public LInventory(String name, String ticker, String symbol, int places, BigDecimal factor, boolean merch, boolean pub, ProgramInstance curInst) {
        super(name, ticker, false, symbol + "§", true, places, factor, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = true;
        PUBLIC = pub;
        STATIC_VALUE = BigDecimal.ZERO;
    }

    public LInventory(String name, BigDecimal val, boolean merch, ProgramInstance curInst) {
        super(name, "", false, "§", true, 0, "", false, false, curInst);
        MERCHANDISE = merch;
        COMMODITY = false;
        PUBLIC = false;
        STATIC_VALUE = val;
    }

    public LInventory(JsonObject obj, ProgramInstance curInst) {
        super(
                obj.getString("name").getString(),
                obj.getString("tic").getString(),
                obj.getString("symbol").getString() + "§",
                obj.getDecimal("places").decimal.intValue(),
                obj,
                curInst
        );
        MERCHANDISE = obj.getString("flags").getString().contains("M");
        COMMODITY = obj.getString("flags").getString().contains("C");
        if (COMMODITY) {
            PUBLIC = obj.getString("flags").getString().contains("P");
        } else {
            PUBLIC = false;
        }
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

    public boolean isPublic() {
        return PUBLIC;
    }

    @Override
    public BigDecimal getTotal(BigDecimal amount) {
        if (COMMODITY) {
            return super.getTotal(amount);
        } else if (MERCHANDISE) {
            Position p = CURRENT_INSTANCE.DATA_HANDLER.getPosition(this, LDate.now(CURRENT_INSTANCE));
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
                if (PUBLIC) {
                    q += "P";
                }
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
        if (getFactor().compareTo(BigDecimal.ONE) != 0) {
            obj.put("factor", new JsonDecimal(getFactor()));
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
