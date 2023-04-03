package com.donny.dendrofinance.capsules;

import com.donny.dendrofinance.account.Account;
import com.donny.dendrofinance.capsules.meta.AssetMetadata;
import com.donny.dendrofinance.capsules.meta.LoanMetadata;
import com.donny.dendrofinance.capsules.totals.Position;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.data.Capsule;
import com.donny.dendroroot.json.JsonArray;
import com.donny.dendroroot.json.JsonDecimal;
import com.donny.dendroroot.json.JsonFormattingException;
import com.donny.dendroroot.json.JsonObject;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.util.Aggregation;

import java.util.ArrayList;

public class StateCapsule extends Capsule {
    private ProgramInstance CURRENT_INSTANCE;
    private final LDate DATE;
    private final Aggregation<Account> ACCOUNTS;
    private final ArrayList<Position> POSITIONS;
    private final ArrayList<AssetMetadata> ASSETS;
    private final ArrayList<LoanMetadata> LOANS;

    public StateCapsule(LDate date, ProgramInstance curInst) {
        super(curInst);
        CURRENT_INSTANCE = curInst;
        DATE = date;
        ACCOUNTS = CURRENT_INSTANCE.DATA_HANDLER.accountsAsOf(date);
        POSITIONS = CURRENT_INSTANCE.DATA_HANDLER.getPositions(date);
        ASSETS = CURRENT_INSTANCE.DATA_HANDLER.assetsAsOf(date);
        LOANS = CURRENT_INSTANCE.DATA_HANDLER.loansAsOf(date);
    }

    public StateCapsule(LDate date, JsonObject acc, JsonArray pos, JsonObject meta, ProgramInstance curInst) {
        super(curInst);
        DATE = date;
        ACCOUNTS = new Aggregation<>();
        for (String key : acc.getFields()) {
            ACCOUNTS.put(CURRENT_INSTANCE.ACCOUNTS.getElement(key), acc.getDecimal(key).decimal);
        }
        POSITIONS = new ArrayList<>();
        for (JsonObject p : pos.getObjectArray()) {
            POSITIONS.add(new Position(p, curInst));
        }
        ASSETS = new ArrayList<>();
        for (JsonObject asset : meta.getArray(new String[]{"a", "ass", "assets"}).getObjectArray()) {
            ASSETS.add(new AssetMetadata(0, null, asset, curInst));
        }
        LOANS = new ArrayList<>();
        for (JsonObject loans : meta.getArray(new String[]{"l", "loans"}).getObjectArray()) {
            LOANS.add(new LoanMetadata(0, null, loans, curInst));
        }
    }

    public StateCapsule(JsonObject obj, ProgramInstance curInst) {
        super(curInst);
        DATE = new LDate(obj.getDecimal(new String[]{"t", "timestamp", "date"}).decimal.longValue(), curInst);
        ACCOUNTS = new Aggregation<>();
        JsonObject acc = obj.getObject(new String[]{"a", "acc", "accounts"});
        for (String key : acc.getFields()) {
            ACCOUNTS.put(CURRENT_INSTANCE.ACCOUNTS.getElement(key), acc.getDecimal(key).decimal);
        }
        POSITIONS = new ArrayList<>();
        for (JsonObject pos : obj.getArray(new String[]{"p", "pos", "positions"}).getObjectArray()) {
            POSITIONS.add(new Position(pos, curInst));
        }
        JsonObject meta = obj.getObject(new String[]{"m", "meta"});
        ASSETS = new ArrayList<>();
        for (JsonObject asset : meta.getArray(new String[]{"a", "ass", "assets"}).getObjectArray()) {
            ASSETS.add(new AssetMetadata(0, null, asset, curInst));
        }
        LOANS = new ArrayList<>();
        for (JsonObject loans : meta.getArray(new String[]{"l", "loans"}).getObjectArray()) {
            LOANS.add(new LoanMetadata(0, null, loans, curInst));
        }
    }

    public LDate getDate() {
        return DATE;
    }

    public Aggregation<Account> getAccounts() {
        return ACCOUNTS;
    }

    public ArrayList<Position> getPositions() {
        return POSITIONS;
    }

    public ArrayList<AssetMetadata> getAssets() {
        return ASSETS;
    }

    public ArrayList<LoanMetadata> getLoans() {
        return LOANS;
    }

    public boolean hasContents() {
        return !(ACCOUNTS.size() == 0 && POSITIONS.size() == 0 && ASSETS.size() == 0 && LOANS.size() == 0);
    }

    public JsonObject exportAccounts() {
        JsonObject out = new JsonObject();
        for (Account a : ACCOUNTS.keySet()) {
            out.put(a.getName(), new JsonDecimal(ACCOUNTS.get(a)));
        }
        return out;
    }

    public JsonArray exportPositions() {
        JsonArray array = new JsonArray();
        for (Position p : POSITIONS) {
            try {
                array.add(p.export());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to export Position: " + p.ASSET + "\n" + e);
            }
        }
        return array;
    }

    public JsonObject exportMeta() {
        JsonObject meta = new JsonObject();
        JsonArray array = new JsonArray();
        for (AssetMetadata asset : ASSETS) {
            try {
                array.add(asset.fullExport());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to export Asset: " + asset.NAME + "\n" + e);
            }
        }
        meta.put("a", array);
        array = new JsonArray();
        for (LoanMetadata loan : LOANS) {
            try {
                array.add(loan.fullExport());
            } catch (JsonFormattingException e) {
                CURRENT_INSTANCE.LOG_HANDLER.error(getClass(), "Unable to export Asset: " + loan.NAME + "\n" + e);
            }
        }
        meta.put("l", array);
        return meta;
    }

    @Override
    public JsonObject export() {
        JsonObject object = new JsonObject();
        object.put("t", new JsonDecimal(DATE.getTime()));
        object.put("a", exportAccounts());
        object.put("p", exportPositions());
        object.put("m", exportMeta());
        return object;
    }
}
