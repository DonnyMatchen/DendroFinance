package com.donny.dendrofinance.data.metatable;

import com.donny.dendrofinance.capsules.meta.LoanMetadata;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.types.LDate;

import java.util.ArrayList;

public class LoanMTC extends MetaTableCore {

    public LoanMTC(Instance curInst) {
        super(curInst);
    }

    @Override
    public String getId(boolean plural) {
        if (plural) {
            return "Loans";
        } else {
            return "Loan";
        }
    }

    @Override
    public String[] getHeader() {
        return new String[]{
                "Uuid", "Name", "Description", "Principal", "Rate", "Begun", "Ended"
        };
    }

    @Override
    public ArrayList<String[]> getContents(LDate date, String search) {
        ProcessReturn pReturn = new ProcessReturn(search);
        search = pReturn.reduced;
        ArrayList<String[]> out = new ArrayList<>();
        for (LoanMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.loansAsOf(date)) {
            if (
                    (meta.isCurrent() && !(pReturn.all || pReturn.dead)) ||
                            (meta.isCurrent() && !pReturn.dead) || pReturn.all
            ) {
                if (meta.NAME.toLowerCase().contains(search.toLowerCase()) || meta.DESC.toLowerCase().contains(search.toLowerCase())) {
                    if (meta.isCurrent()) {
                        out.add(new String[]{
                                Long.toUnsignedString(meta.ROOT_REF), meta.NAME, meta.DESC, meta.CUR.encode(meta.principalRemaining()), CURRENT_INSTANCE.p(meta.RATE), meta.DATE.toString(), ""
                        });
                    } else {
                        out.add(new String[]{
                                Long.toUnsignedString(meta.ROOT_REF), meta.NAME, meta.DESC, meta.CUR.encode(meta.principalRemaining()), CURRENT_INSTANCE.p(meta.RATE), meta.DATE.toString(), meta.EVENTS.get(meta.EVENTS.size() - 1).DATE.toString()
                        });
                    }
                }
            }
        }
        return out;
    }

    @Override
    public String getName() {
        return "Loan";
    }

    @Override
    public String print(String uuid, String name, LDate date) {
        for (LoanMetadata meta : CURRENT_INSTANCE.DATA_HANDLER.loansAsOf(date)) {
            if (meta.ROOT_REF == Long.parseUnsignedLong(uuid) && meta.NAME.equals(name)) {
                return meta.toString();
            }
        }
        return "ERROR";
    }
}
