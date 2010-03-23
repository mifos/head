/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.calendar;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.schedule.ScheduledEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HolidayAdjustmentRuleFactoryTest {

    private HolidayAdjustmentRuleFactory holidayAdjustmentRuleFactory;

    @Mock
    private ScheduledEvent scheduledEvent;

    private List<Days> workingDays;

    private final DateTime originalScheduledDate = new DateTime();

    @Test
    public void factoryShouldReturnNextWorkingDayStrategy() {

        RepaymentRuleTypes holidayAdjustmentRule = RepaymentRuleTypes.NEXT_WORKING_DAY;

        // exercise test
        DateAdjustmentStrategy adjustmentStrategy  = holidayAdjustmentRuleFactory.createStrategy(originalScheduledDate, workingDays, scheduledEvent, holidayAdjustmentRule);

        assertThat(adjustmentStrategy, is(instanceOf(NextWorkingDayStrategy.class)));
    }

    @Test
    public void factoryShouldReturnNearestScheduledEventBeginningOnStrategy() {

        RepaymentRuleTypes holidayAdjustmentRule = RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT;

        // exercise test
        DateAdjustmentStrategy adjustmentStrategy  = holidayAdjustmentRuleFactory.createStrategy(originalScheduledDate, workingDays, scheduledEvent, holidayAdjustmentRule);

        assertThat(adjustmentStrategy, is(instanceOf(NearestScheduledEventBeginningOnStrategy.class)));
    }

    @Test
    public void factoryShouldReturnNextWorkingDayStrategyForSameDayRule() {

        RepaymentRuleTypes holidayAdjustmentRule = RepaymentRuleTypes.SAME_DAY;

        // exercise test
        DateAdjustmentStrategy adjustmentStrategy  = holidayAdjustmentRuleFactory.createStrategy(originalScheduledDate, workingDays, scheduledEvent, holidayAdjustmentRule);

        assertThat(adjustmentStrategy, is(instanceOf(SameDayStrategy.class)));
    }

    @Test
    public void givenRepaymentMoratoriumRuleFactoryShouldReturnNearestNearestScheduledEventBeginningOnStrategy() {

        RepaymentRuleTypes holidayAdjustmentRule = RepaymentRuleTypes.REPAYMENT_MORATORIUM;

        // exercise test
        DateAdjustmentStrategy adjustmentStrategy  = holidayAdjustmentRuleFactory.createStrategy(originalScheduledDate, workingDays, scheduledEvent, holidayAdjustmentRule);

        assertThat(adjustmentStrategy, is(instanceOf(NearestScheduledEventBeginningOnStrategy.class)));
    }
}
