package com.umbrella.budgetapp.Extensions;

import androidx.annotation.NonNull;

import com.google.common.util.concurrent.AtomicDouble;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Functions {

    /**
     * Calculate percentage difference between two Doubles.
     * @param Old The old value.
     * @param New The new value.
     * @return The %v2 change of v1 in Double.
     */
    public static Double calculatePercentage(@NonNull Double Old, @NonNull Double New) {
        if (Old == 0.00) return 0.00;
        return ((New - Old) / Old) * 100;
    }

    /**
     * Calculate percentage difference between two AtomicDoubles.
     * @param Old The old value.
     * @param New The new value.
     * @return The %v2 change of v1 in Double.
     */
    public static Double calculatePercentage(@NonNull AtomicDouble Old, @NonNull AtomicDouble New) {
        if (Old.doubleValue() == 0.00) return 0.00;
        return new BigDecimal(((New.doubleValue() - Old.doubleValue()) / Old.doubleValue()) * 100).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calculate the occupation value of one double in contrast with the total value.
     * @param current The value that represents the percentage of a total value.
     * @param total The total value.
     * @return The %current of total.
     */
    public static Double calculatePercentageOfTotal(@NonNull Double current, @NonNull Double total) {
        return (total != 0.00) ? new BigDecimal((current / total) * 100).doubleValue() : 0.00;
    }

    /**
     * Calculate the occupation value of one double in contrast with the total value.
     * @param current The value that represents the percentage of a total value.
     * @param total The total value.
     * @return The %current of total.
     */
    public static Double calculatePercentageOfTotal(@NonNull AtomicDouble current, @NonNull AtomicDouble total) {
        return (total.doubleValue() != 0.00) ? new BigDecimal((current.doubleValue() / total.doubleValue()) * 100).doubleValue() : 0.00;
    }
}
