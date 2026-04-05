package com.spiritlight.mobkilltracker.v3.utils.math;

import java.math.BigDecimal;

/** Utility class to do some maths. */
public class StrictMath {

    public static double decimalPointOf(double val) {
        return BigDecimal.valueOf(val).subtract(BigDecimal.valueOf((int) val)).doubleValue();
    }

    public static double subtract(double a, double b) {
        return BigDecimal.valueOf(a).subtract(BigDecimal.valueOf(b)).doubleValue();
    }

    public static double add(double a, double b) {
        return BigDecimal.valueOf(a).add(BigDecimal.valueOf(b)).doubleValue();
    }
}
