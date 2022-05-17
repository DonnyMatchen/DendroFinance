package com.donny.dendrofinance.entry.meta;

import com.donny.dendrofinance.currency.LCurrency;
import com.donny.dendrofinance.instance.Instance;
import com.donny.dendrofinance.json.JsonDecimal;
import com.donny.dendrofinance.json.JsonFormattingException;
import com.donny.dendrofinance.json.JsonObject;
import com.donny.dendrofinance.json.JsonString;
import com.donny.dendrofinance.types.LDate;
import com.donny.dendrofinance.util.LMath;

import java.math.BigDecimal;
import java.util.ArrayList;

public class LoanMetadata {
    public final String NAME, DESC;
    public final long ROOT_REF;
    public final LDate DATE;
    public final LCurrency CUR;
    public final BigDecimal PRINC, RATE;
    public final ArrayList<LoanChangeMetadata> EVENTS;
    private final Instance CURRENT_INSTANCE;

    public LoanMetadata(long uuid, LDate date, String name, String description, LCurrency currency, BigDecimal principal, BigDecimal rate, Instance curInst) {
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

    public LoanMetadata(long uuid, LDate date, JsonObject obj, Instance curInst) {
        this(
                uuid,
                obj.containsKey("date") ? new LDate(obj.getDecimal("date"), curInst) : date,
                obj.getString("name").getString(),
                obj.getString("desc").toString(),
                curInst.getLCurrency(obj.getString("cur").getString()),
                obj.getDecimal("princ").decimal,
                obj.getDecimal("rate").decimal,
                curInst
        );
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
        obj.put("date", new JsonDecimal(BigDecimal.valueOf(DATE.getTime())));
        obj.put("name", new JsonString(NAME));
        obj.put("desc", new JsonString(DESC));
        obj.put("cur", new JsonString(CUR.toString()));
        obj.put("princ", new JsonDecimal(PRINC));
        obj.put("rate", new JsonDecimal(RATE));
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
