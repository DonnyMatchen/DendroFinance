package com.donny.dendrofinance.capsules.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.ProgramInstance;
import com.donny.dendroroot.json.*;
import com.donny.dendroroot.types.LDate;
import com.donny.dendroroot.util.LMath;

import java.math.BigDecimal;
import java.util.ArrayList;

public class LoanMetadata {
    public final String NAME, DESC;
    public final long ROOT_REF;
    public final LDate DATE;
    public final LCurrency CUR;
    public final BigDecimal PRINC, RATE;
    public final ArrayList<LoanChangeMetadata> EVENTS;
    private final ProgramInstance CURRENT_INSTANCE;

    public LoanMetadata(long uuid, LDate date, String name, String description, LCurrency currency, BigDecimal principal, BigDecimal rate, ProgramInstance curInst) {
        NAME = name;
        DESC = description;
        ROOT_REF = uuid;
        DATE = date;
        CUR = currency;
        PRINC = principal;
        RATE = rate;
        EVENTS = new ArrayList<>();
        CURRENT_INSTANCE = curInst;
    }

    public LoanMetadata(long uuid, LDate date, JsonObject obj, ProgramInstance curInst) {
        this(
                obj.containsKey(new String[]{"r", "ref", "root-ref"}) ? obj.getDecimal(new String[]{"r", "ref", "root-ref"}).decimal.longValue() : uuid,
                obj.containsKey(new String[]{"t", "date", "timestamp"}) ? new LDate(obj.getDecimal(new String[]{"t", "date", "timestamp"}), curInst) : date,
                obj.getString(new String[]{"n", "name"}).getString(),
                obj.getString(new String[]{"d", "desc"}).toString(),
                curInst.getLCurrency(obj.getString(new String[]{"c", "cur", "currency"}).getString()),
                obj.getDecimal(new String[]{"p", "princ", "principle"}).decimal,
                obj.getDecimal(new String[]{"i", "rate", "interest"}).decimal,
                curInst
        );
        if (obj.containsKey(new String[]{"e", "events"})) {
            for (JsonObject event : obj.getArray("events").getObjectArray()) {
                EVENTS.add(new LoanChangeMetadata(0, null, event, CURRENT_INSTANCE));
            }
        }
    }

    public ArrayList<Long> getRefs() {
        ArrayList<Long> out = new ArrayList<>();
        for (LoanChangeMetadata meta : EVENTS) {
            out.add(meta.UUID);
        }
        return out;
    }

    public BigDecimal principalRemaining() {
        BigDecimal sum = PRINC;
        for (LoanChangeMetadata meta : EVENTS) {
            sum = sum.add(meta.CHANGE);
        }
        return sum;
    }

    public BigDecimal principalReduction(BigDecimal payment, String period) {
        return switch (period) {
            case "S" ->
                    payment.subtract(principalRemaining().multiply(LMath.squareRoot(BigDecimal.ONE.add(RATE), CURRENT_INSTANCE.precision)));
            case "Q" ->
                    payment.subtract(principalRemaining().multiply(LMath.forthRoot(BigDecimal.ONE.add(RATE), CURRENT_INSTANCE.precision)));
            case "M" ->
                    payment.subtract(principalRemaining().multiply(LMath.twelfthRoot(BigDecimal.ONE.add(RATE), CURRENT_INSTANCE.precision)));
            default -> payment.subtract(principalRemaining().multiply(BigDecimal.ONE.add(RATE)));
        };
    }

    public boolean isCurrent() {
        return PRINC.compareTo(BigDecimal.ZERO) > 0;
    }

    public JsonObject export() throws JsonFormattingException {
        JsonObject obj = new JsonObject();
        obj.put("t", DATE.export());
        obj.put("n", new JsonString(NAME));
        obj.put("d", new JsonString(DESC));
        obj.put("c", new JsonString(CUR.toString()));
        obj.put("p", new JsonDecimal(PRINC));
        obj.put("i", new JsonDecimal(RATE));
        return obj;
    }

    public JsonObject fullExport() throws JsonFormattingException {
        JsonObject obj = export();
        obj.put("r", new JsonDecimal(ROOT_REF));
        JsonArray array = new JsonArray();
        for (LoanChangeMetadata event : EVENTS) {
            array.add(event.fullExport());
        }
        obj.put("e", array);
        return obj;
    }

    public String display() {
        return NAME + "(" + DESC + ") " + CUR.encode(PRINC) + " @" + CURRENT_INSTANCE.p(RATE);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(NAME + " {");
        for (LoanChangeMetadata meta : EVENTS) {
            sb.append("\n\t").append(meta.print());
        }
        return sb.append("\n}").toString();
    }
}
