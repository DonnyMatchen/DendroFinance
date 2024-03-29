package com.donny.dendrofinance.data.metatable;

import com.donny.dendrofinance.capsules.meta.AssetMetadata;
import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.types.LDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class AssetMTC extends MetaTableCore {

    public AssetMTC(ProgramInstance curInst) {
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
        ProcessReturn pReturn = new ProcessReturn(search);
        search = pReturn.reduced;
        ArrayList<String[]> out = new ArrayList<>();
        for (AssetMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.assetsAsOf(date)) {
            if (
                    (meta.isCurrent() && !(pReturn.all || pReturn.dead)) ||
                            (!meta.isCurrent() && pReturn.dead) || pReturn.all
            ) {
                if (meta.NAME.toLowerCase().contains(search.toLowerCase()) || meta.DESC.toLowerCase().contains(search.toLowerCase())) {
                    boolean first = true;
                    HashMap<LCurrency, BigDecimal> counts = meta.getCount(), values = meta.getValues();
                    for (LCurrency cur : meta.getCurrencies()) {
                        if (first) {
                            if (meta.isCurrent()) {
                                out.add(new String[]{
                                        Long.toUnsignedString(meta.ROOT_REF), meta.NAME, meta.DESC, cur.encode(values.get(cur)), counts.get(cur).toString(), meta.DATE.toString(), ""
                                });
                            } else {
                                out.add(new String[]{
                                        Long.toUnsignedString(meta.ROOT_REF), meta.NAME, meta.DESC, cur.encode(values.get(cur)), counts.get(cur).toString(), meta.DATE.toString(), meta.EVENTS.get(meta.EVENTS.size() - 1).DATE.toString()
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
            if (meta.ROOT_REF == Long.parseUnsignedLong(uuid) && meta.NAME.equals(name)) {
                return meta.toString();
            }
        }
        return "ERROR";
    }
}
