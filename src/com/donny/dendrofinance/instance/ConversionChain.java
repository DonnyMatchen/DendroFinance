package com.donny.dendrofinance.instance;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.currency.LMarketApi;

import java.util.ArrayList;

public class ConversionChain {
    public final ArrayList<LCurrency> LIST = new ArrayList<>();
    public LMarketApi from;
    public LMarketApi to;
    public LCurrency middle;
}
