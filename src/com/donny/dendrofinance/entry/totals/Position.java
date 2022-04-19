package com.donny.dendrofinance.entry.totals;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

public class Position {
    public final Instance CURRENT_INSTANCE;
    public final LCurrency ASSET;
    public final ArrayList<PositionElement> ELEMENTS;

    public Position(LCurrency asset, Instance curInst) {
        CURRENT_INSTANCE = curInst;
        ASSET = asset;
        ELEMENTS = new ArrayList<>();
    }

    public Position(Position base) {
        this(base.ASSET, base.CURRENT_INSTANCE);
        ELEMENTS.addAll(base.ELEMENTS);
    }

    public void sort() {
        ELEMENTS.sort((el1, el2) -> el1.DATE.compare(el2.DATE));
    }

    public ArrayList<OrderBookEntry> change(long uuid, LDate date, LCurrency cur, BigDecimal amnt, BigDecimal cost) {
        ArrayList<OrderBookEntry> blank = new ArrayList<>();
        if (cur.equals(ASSET)) {
            boolean ap = amnt.compareTo(BigDecimal.ZERO) > 0, az = amnt.compareTo(BigDecimal.ZERO) == 0, an = amnt.compareTo(BigDecimal.ZERO) < 0;
            boolean cp = cost.compareTo(BigDecimal.ZERO) > 0, cz = cost.compareTo(BigDecimal.ZERO) == 0;
            if (az) {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Somehow, a position with an amount of zero has been entered");
                return null;
            } else if (an && cz) {
                if (ELEMENTS.get(0).volume.compareTo(amnt.abs()) > 0) {
                    ELEMENTS.get(0).volume = ELEMENTS.get(0).volume.add(amnt);
                    return blank;
                } else if (ELEMENTS.get(0).volume.compareTo(amnt.abs()) == 0) {
                    ELEMENTS.remove(0);
                    return blank;
                } else {
                    BigDecimal vol = ELEMENTS.get(0).volume;
                    ELEMENTS.remove(0);
                    return change(uuid, date, ASSET, amnt.add(vol), BigDecimal.ZERO);
                }
            } else if (ap && !cp) {
                ELEMENTS.add(new PositionElement(uuid, date, amnt, cost.divide(amnt, CURRENT_INSTANCE.PRECISION)));
                sort();
                return blank;
            } else if (an && cp) {
                if (ELEMENTS.isEmpty()) {
                    CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Sell Out of Order: " + uuid);
                }
                if (ELEMENTS.get(0).volume.compareTo(amnt.abs()) > 0) {
                    ELEMENTS.get(0).volume = ELEMENTS.get(0).volume.add(amnt);
                    BigDecimal cst = amnt.abs().multiply(ELEMENTS.get(0).UNIT);
                    return new ArrayList<>(Arrays.asList(new OrderBookEntry(ASSET, ELEMENTS.get(0).DATE, date, ELEMENTS.get(0).UUID, uuid, amnt.abs(), cst, cost)));
                } else if (ELEMENTS.get(0).volume.compareTo(amnt.abs()) == 0) {
                    BigDecimal cst = amnt.abs().multiply(ELEMENTS.get(0).UNIT);
                    LDate fDate = ELEMENTS.get(0).DATE;
                    long fUUID = ELEMENTS.get(0).UUID;
                    ELEMENTS.remove(0);
                    return new ArrayList<>(Arrays.asList(new OrderBookEntry(ASSET, fDate, date, fUUID, uuid, amnt.abs(), cst, cost)));
                } else {
                    BigDecimal cst = ELEMENTS.get(0).volume.multiply(ELEMENTS.get(0).UNIT);
                    BigDecimal pUnit = cost.divide(amnt.abs(), CURRENT_INSTANCE.PRECISION);
                    BigDecimal newAmnt = amnt.add(ELEMENTS.get(0).volume);
                    blank.add(new OrderBookEntry(ASSET, ELEMENTS.get(0).DATE, date, ELEMENTS.get(0).UUID, uuid, ELEMENTS.get(0).volume.abs(), cst, ELEMENTS.get(0).volume.multiply(pUnit)));
                    ELEMENTS.remove(0);
                    blank.addAll(change(uuid, date, ASSET, newAmnt, newAmnt.abs().multiply(pUnit)));
                    return blank;
                }
            } else {
                CURRENT_INSTANCE.LOG_HANDLER.error(this.getClass(), "Somehow, a position with a positive amount and cost has been entered");
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * @return BigDecimal[volume, cost, unit price]
     */
    public BigDecimal[] collapse() {
        BigDecimal cost = BigDecimal.ZERO;
        BigDecimal vol = BigDecimal.ZERO;
        for (PositionElement el : ELEMENTS) {
            cost = cost.add(el.UNIT.multiply(el.volume));
            vol = vol.add(el.volume);
        }
        if (vol.compareTo(BigDecimal.ZERO) == 0) {
            return new BigDecimal[]{vol, cost, BigDecimal.ZERO};
        } else {
            return new BigDecimal[]{vol, cost, cost.divide(vol, CURRENT_INSTANCE.PRECISION)};
        }
    }

    public ArrayList<String[]> view() {
        ArrayList<String[]> brackets = new ArrayList<>();
        for (PositionElement el : ELEMENTS) {
            brackets.add(el.view(ASSET, CURRENT_INSTANCE.main__));
        }
        return brackets;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(ASSET.toString() + "{");
        for (PositionElement el : ELEMENTS) {
            out.append("\n\t").append(el);
        }
        out.append("\n}");
        return out.toString();
    }
}
