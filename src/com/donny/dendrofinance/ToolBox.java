package com.donny.dendrofinance;

import com.donny.dendrofinance.account.Exchange;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;

import java.math.BigDecimal;
import java.util.Arrays;

public class ToolBox {

    public static void convert(LCurrency a, BigDecimal amnt, LCurrency b, Instance curInst) {
        System.out.println(a.encode(amnt) + " => " + b.encode(curInst.convert(a, amnt, b)));
    }

    public static BigDecimal cryptoOther(String cur, int y, int m, int d, Instance curInst) {
        return curInst.DATA_HANDLER.cryptoAsOf(y, m, d, curInst.getLCurrency(cur));
    }

    public static void checkExchanges(Instance curInst) {
        for (Exchange e : curInst.EXCHANGES) {
            System.out.println(e.NAME + ": " + Arrays.toString(e.aNamesInUse(curInst).toArray()));
        }
    }
}
