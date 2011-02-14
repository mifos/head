/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.application.holiday.business;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.domain.builders.HolidayBuilder;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HolidayBOTest {
    @Test
    public void testIsFutureRepayment() throws Exception {
        Holiday sameDayRepaymentHoliday =  new HolidayBuilder().withName("holiday").from(new DateTime()).to(new DateTime()).
                withSameDayAsRule().build();
        Holiday nextMeetingDayRepaymentHoliday =  new HolidayBuilder().withName("holiday").from(new DateTime()).to(new DateTime()).
                withNextMeetingRule().build();
        Holiday nextWorkingDayRepaymentHoliday =  new HolidayBuilder().withName("holiday").from(new DateTime()).to(new DateTime()).
                withNextWorkingDayRule().build();
        Holiday moratoriumRepaymentHoliday =  new HolidayBuilder().withName("holiday").from(new DateTime()).to(new DateTime()).
                withRepaymentMoratoriumRule().build();
        Assert.assertFalse(sameDayRepaymentHoliday.isFutureRepayment());
        Assert.assertTrue(nextMeetingDayRepaymentHoliday.isFutureRepayment());
        Assert.assertTrue(nextWorkingDayRepaymentHoliday.isFutureRepayment());
        Assert.assertTrue(moratoriumRepaymentHoliday.isFutureRepayment());
    }
}
