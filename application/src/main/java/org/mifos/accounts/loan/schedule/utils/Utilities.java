package org.mifos.accounts.loan.schedule.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Utilities {
    private static final int MILLI_SECONDS_IN_DAY = 86400000;

    public static boolean isGreaterThanZero(BigDecimal balance) {
        return balance.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isLesserThanOrEqualToZero(BigDecimal balance) {
        return !isGreaterThanZero(balance);
    }

    // (first - second) gap in days
    public static long getDaysInBetween(Date first, Date second) {
        long firstInMillis = first.getTime();
        long secondInMillis = second.getTime();
        return (firstInMillis - secondInMillis) / MILLI_SECONDS_IN_DAY;
    }

    public static BigDecimal round(BigDecimal computedInterest) {
        return computedInterest.setScale(2, RoundingMode.HALF_UP);
    }
}
