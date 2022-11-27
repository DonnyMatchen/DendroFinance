package com.donny.dendrofinance.instance;

import com.donny.dendrofinance.currency.LMarketApi;

import java.util.ArrayList;

public class ChainList {
    public final ArrayList<ConversionChain> CHAINS = new ArrayList<>();

    public ConversionChain contains(LMarketApi from) {
        for (ConversionChain chain : CHAINS) {
            if (chain.from == from) {
                return chain;
            }
        }
        return null;
    }
}
