package com.donny.dendrofinance.entry.totals;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;

public class PositionElement {
    public final LDate DATE;
    public final long UUID;
    public final BigDecimal UNIT;
    public BigDecimal volume;

    public PositionElement(long uuid, LDate date, BigDecimal vol, BigDecimal unit) {
        UUID = uuid;
        DATE = date;
        volume = vol;
        UNIT = unit;
    }

    public BigDecimal cost() {
        return volume.multiply(UNIT);
    }

    public String[] view(LCurrency asset, LCurrency main__) {
        return new String[]{
                asset.encode(volume),
                main__.encode(cost()),
                main__.encode(UNIT)
        };
    }

    @Override
    public String toString() {
        return "{" + UUID + "} \\ (" + DATE + ") \\ volume=" + volume + ", cost=" + volume.multiply(UNIT) + ", unit=" + UNIT;
    }
}
