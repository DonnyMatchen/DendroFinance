package com.donny.dendroroot.gui.form;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Cleaning {
    public static BigDecimal cleanNumber(String raw) {
        raw = raw.replace("(", "-");
        String allowed = "0123456789.-";
        StringBuilder out = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (allowed.contains(String.valueOf(c))) {
                out.append(c);
            }
        }
        if (out.length() == 0) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(out.toString());
        }
    }

    public static BigDecimal cleanNumberAllowPercent(String raw, MathContext precision) {
        boolean flag = raw.contains("%");
        raw = raw.replace("(", "-");
        String allowed = "0123456789.-";
        StringBuilder out = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (allowed.contains(String.valueOf(c))) {
                out.append(c);
            }
        }
        if (out.length() == 0) {
            return BigDecimal.ZERO;
        } else {
            if (flag) {
                return new BigDecimal(out.toString()).divide(BigDecimal.TEN.pow(2), precision);
            } else {
                return new BigDecimal(out.toString());
            }
        }
    }

    public static BigInteger cleanInteger(String raw) {
        raw = raw.replace("(", "-");
        String allowed = "0123456789-";
        StringBuilder out = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (allowed.contains(String.valueOf(c))) {
                out.append(c);
            }
        }
        if (out.length() == 0) {
            return BigInteger.ZERO;
        } else {
            return new BigInteger(out.toString());
        }
    }
}
