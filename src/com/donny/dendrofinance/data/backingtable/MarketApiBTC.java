package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LMarketApi;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.MarketApiEditGui;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonObject;

import java.util.ArrayList;

public class MarketApiBTC extends BackingTableCore<LMarketApi> {
    public MarketApiBTC(ProgramInstance curInst) {
        super(curInst, false);
    }

    @Override
    public String getName(boolean plural) {
        if (plural) {
            return "Market Apis";
        } else {
            return "Market Api";
        }
    }

    @Override
    public void getEditDialog(BackingTableGui<LMarketApi> caller, int index) {
        new MarketApiEditGui(caller, this, index, CURRENT_INSTANCE);
    }

    @Override
    public void load(JsonArray array) {
        for (JsonObject object : array.getObjectArray()) {
            add(new LMarketApi(object, CURRENT_INSTANCE));
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Name", "Has Key", "Stocks", "Forex", "Cryptocurrencies", "Commodities"
        };
    }

    @Override
    public int contentIdentifierIndex() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getContents(String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (String key : KEYS) {
            LMarketApi item = MAP.get(key);
            out.add(new String[]{
                    item.NAME, item.KEY.isEmpty() ? "" : "X", item.stocks() ? "X" : "",
                    item.fiatCurrencies() ? "X" : "", item.cryptocurrencies() ? "X" : "",
                    item.inventories() ? "X" : "",
            });
        }
        return out;
    }

    @Override
    public boolean canMove(String identifier) {
        return true;
    }

    @Override
    public boolean canEdit(String identifier) {
        return true;
    }

    @Override
    public boolean canRemove(String identifier) {
        return true;
    }

    @Override
    public void sort() {
    }
}
