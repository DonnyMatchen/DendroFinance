package com.donny.dendroroot.util;

import java.math.BigDecimal;
import java.math.MathContext;

public class LMath {
    public static BigDecimal squareRoot(BigDecimal value, MathContext precision) {
        return value.sqrt(precision);
    }

    public static BigDecimal cubeRoot(BigDecimal value, MathContext precision) {
        BigDecimal x = new BigDecimal("1", precision);
        for (int i = 0; i < precision.getPrecision() + 1; i++) {
            x = x.subtract(
                    x.pow(3, precision)
                            .subtract(value, precision)
                            .divide(
                                    new BigDecimal("3", precision)
                                            .multiply(x.pow(2, precision), precision), precision
                            ), precision
            );
        }
        return x;
    }

    public static BigDecimal forthRoot(BigDecimal value, MathContext precision) {
        return value.sqrt(precision).sqrt(precision);
    }

    public static BigDecimal twelfthRoot(BigDecimal value, MathContext precision) {
        return forthRoot(cubeRoot(value, precision), precision);
    }
}
