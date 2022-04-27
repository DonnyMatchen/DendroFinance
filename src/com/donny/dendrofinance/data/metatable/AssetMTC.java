package com.donny.dendrofinance.data.metatable;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.entry.meta.AssetMetadata;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetMTC extends MetaTableCore {

    public AssetMTC(Instance curInst) {
        super(curInst);
    }

    @Override
    public String getId(boolean plural) {
        if (plural) {
            return "Assets";
        } else {
            return "Asset";
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Uuid", "Name", "Description", "Value", "Count", "Acquired", "Disposed"
        };
    }

    @Override
    public ArrayList<String[]> getContents(LDate date, String search) {
        ArrayList<String[]> out = new ArrayList<>();
        for (AssetMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.assetsAsOf(date)) {
            if (meta.NAME.toLowerCase().contains(search.toLowerCase()) || meta.DESC.toLowerCase().contains(search.toLowerCase())) {
                boolean first = true;
                HashMap<LCurrency, BigDecimal> counts = meta.getCount(), values = meta.getValues();
                for (LCurrency cur : meta.getCurrencies()) {
                    if (first) {
                        if (meta.isCurrent()) {
                            out.add(new String[]{
                                    "" + meta.ROOT_REF, meta.NAME, meta.DESC, cur.encode(values.get(cur)), counts.get(cur).toString(), meta.DATE.toString(), ""
                            });
                        } else {
                            out.add(new String[]{
                                    "" + meta.ROOT_REF, meta.NAME, meta.DESC, cur.encode(values.get(cur)), counts.get(cur).toString(), meta.DATE.toString(), meta.EVENTS.get(meta.EVENTS.size() - 1).DATE.toString()
                            });
                        }
                    } else {
                        out.add(new String[]{
                                "", "", "", cur.encode(values.get(cur)), counts.get(cur).toString(), "", ""
                        });
                    }
                    first = false;
                }
            }
        }
        return out;
    }

    @Override
    public String getName() {
        return "Asset";
    }

    @Override
    public String print(String uuid, String name, LDate date) {
        for (AssetMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.assetsAsOf(date)) {
            if (meta.ROOT_REF == Long.parseLong(uuid) && meta.NAME.equals(name)) {
                return meta.toString();
            }
        }
        return "ERROR";
    }
}
