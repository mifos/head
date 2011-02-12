/*
 * Copyright Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */
package org.mifos.accounts.loan.schedule.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Utilities {
    private static final int MILLI_SECONDS_IN_DAY = 86400000;

    public static boolean isGreaterThanZero(BigDecimal balance) {
        return balance.compareTo(BigDecimal.ZERO) > 0;
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
