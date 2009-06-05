/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.branchreport;

import static org.junit.Assert.assertEquals;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.FIVE_TO_EIGHT_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.FOUR_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.MORE_THAN_TWELVE;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.NINE_TO_TWELVE_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.ONE_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.THREE_WEEK;
import static org.mifos.application.branchreport.LoanArrearsAgingPeriod.TWO_WEEK;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class LoanArrearsAgingPeriodTest {

    private static final int TOTAL_WEEK_DAYS = 7;

    @Test
    public void testLoanArrearsAgingPeriodValues() throws Exception {
        assertEquals(1, (int) ONE_WEEK.getMinDays());
        assertEquals(TOTAL_WEEK_DAYS, (int) ONE_WEEK.getMaxDays());
        assertEquals(TOTAL_WEEK_DAYS + 1, (int) ONE_WEEK.getNotLessThanDays());

        assertEquals(TOTAL_WEEK_DAYS + 1, (int) TWO_WEEK.getMinDays());
        assertEquals(TOTAL_WEEK_DAYS * 2, (int) TWO_WEEK.getMaxDays());
        assertEquals(TOTAL_WEEK_DAYS * 2 + 1, (int) TWO_WEEK.getNotLessThanDays());

        assertEquals(TOTAL_WEEK_DAYS * 2 + 1, (int) THREE_WEEK.getMinDays());
        assertEquals(TOTAL_WEEK_DAYS * 3, (int) THREE_WEEK.getMaxDays());
        assertEquals(TOTAL_WEEK_DAYS * 3 + 1, (int) THREE_WEEK.getNotLessThanDays());

        assertEquals(TOTAL_WEEK_DAYS * 3 + 1, (int) FOUR_WEEK.getMinDays());
        assertEquals(TOTAL_WEEK_DAYS * 4, (int) FOUR_WEEK.getMaxDays());
        assertEquals(TOTAL_WEEK_DAYS * 4 + 1, (int) FOUR_WEEK.getNotLessThanDays());

        assertEquals(TOTAL_WEEK_DAYS * 4 + 1, (int) FIVE_TO_EIGHT_WEEK.getMinDays());
        assertEquals(TOTAL_WEEK_DAYS * 8, (int) FIVE_TO_EIGHT_WEEK.getMaxDays());
        assertEquals(TOTAL_WEEK_DAYS * 8 + 1, (int) FIVE_TO_EIGHT_WEEK.getNotLessThanDays());

        assertEquals(TOTAL_WEEK_DAYS * 8 + 1, (int) NINE_TO_TWELVE_WEEK.getMinDays());
        assertEquals(TOTAL_WEEK_DAYS * 12, (int) NINE_TO_TWELVE_WEEK.getMaxDays());
        assertEquals(TOTAL_WEEK_DAYS * 12 + 1, (int) NINE_TO_TWELVE_WEEK.getNotLessThanDays());

        assertEquals(TOTAL_WEEK_DAYS * 12 + 1, (int) MORE_THAN_TWELVE.getMinDays());
        assertEquals(Integer.MAX_VALUE - 1, (int) MORE_THAN_TWELVE.getMaxDays());
        assertEquals(Integer.MAX_VALUE, (int) MORE_THAN_TWELVE.getNotLessThanDays());
    }

    @Test
    public void testNumberOfArrearsPeriods() throws Exception {
        assertEquals(7, LoanArrearsAgingPeriod.values().length);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(LoanArrearsAgingPeriodTest.class);
    }

}
