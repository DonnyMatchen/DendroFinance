package com.donny.dendrofinance.data.backingtable;

import com.donny.dendrofinance.currency.LMarketApi;
import com.donny.dendrofinance.gui.menu.data.backing.BackingTableGui;
import com.donny.dendrofinance.gui.menu.data.backing.edit.MarketApiEditGui;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonArray;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;

import java.util.ArrayList;

public class MarketApiBTC extends BackingTableCore<LMarketApi> {
    public MarketApiBTC(Instance curInst) {
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
            TABLE.add(new LMarketApi(object, CURRENT_INSTANCE));
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
        for (LMarketApi item : TABLE) {
            out.add(new String[]{
                    item.NAME, item.KEY.equals("") ? "" : "X", item.stocks() ? "X" : "",
                    item.fiatCurrencies() ? "X" : "", item.cryptocurrencies() ? "X" : "",
                    item.inventories() ? "X" : "",
            });
        }
        return out;
    }

    @Override
    public String getIdentifier(int index) {
        return TABLE.get(index).NAME;
    }

    @Override
    public int getIndex(String identifier) {
        for (int i = 0; i < TABLE.size(); i++) {
            if (TABLE.get(i).NAME.equalsIgnoreCase(identifier)) {
                return i;
            }
        }
        return -1;
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

    @Override
    public JsonArray export() {
        JsonArray array = new JsonArray();
        for (LMarketApi item : TABLE) {
            try {
                array.add(item.export());
            } catch (JsonFormattingException ex) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Damaged LMarketAPI " + item.NAME);
            }
        }
        return array;
    }
}
