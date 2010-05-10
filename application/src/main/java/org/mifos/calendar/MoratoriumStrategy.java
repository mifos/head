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

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.schedule.ScheduledEvent;

public class MoratoriumStrategy implements ListOfDatesAdjustmentStrategy {

    /**
     * should be ordered by date ascending to avoid problems with overlapping holidays
     * TODO: Fix to allow moratorium periods to overlap holidays. THIS IS IMPORTANT.
     */
    private final List<Holiday> upcomingHolidays;
    private final List<Days> workingDays;
    private final ScheduledEvent scheduledEvent;

    public MoratoriumStrategy(final List<Holiday> upcomingHolidays, final List<Days> workingDays,
            final ScheduledEvent scheduledEvent) {

        this.upcomingHolidays = upcomingHolidays;
        this.workingDays = workingDays;
        this.scheduledEvent = scheduledEvent;
    }

    @Override
    public List<DateTime> adjust (List<DateTime> dates) {

        if (dates == null) {throw new IllegalArgumentException("dates cannot be null.");}

        List<DateTime> adjustedDates = null;

        if (dates.isEmpty()) {
            adjustedDates = dates;
        } else {
           DateTime firstDate = dates.get(0);
           if ( isEnclosedByAHolidayWithRepaymentRule(firstDate, RepaymentRuleTypes.REPAYMENT_MORATORIUM) ) {
               adjustedDates = adjust (shiftSchedulePastMoratorium(dates));
           } else if (isEnclosedByAHoliday(firstDate)) { //enclosed by a non-moratorium holiday
               adjustedDates = makeList(shiftDatePastNonMoratoriumHoliday(firstDate), adjust (rest(dates)));
           } else {
               adjustedDates = makeList (firstDate, adjust (rest (dates)));
           }
        }
        return adjustedDates;
    }

    private List<DateTime> shiftSchedulePastMoratorium (List<DateTime> dates) {

        assert (dates != null) && isEnclosedByAHolidayWithRepaymentRule(dates.get(0), RepaymentRuleTypes.REPAYMENT_MORATORIUM);

        List<DateTime> shiftedDates = dates;
        do {
            shiftedDates = shiftByOneScheduledEventRecurrence(shiftedDates);
        } while (isEnclosedByAHolidayWithRepaymentRule(shiftedDates.get(0), RepaymentRuleTypes.REPAYMENT_MORATORIUM));
        return shiftedDates;

    }

    private List<DateTime> shiftByOneScheduledEventRecurrence (List<DateTime> dates) {

        assert dates != null;

        List<DateTime> pushedOutSchedule = new ArrayList<DateTime>();
        for (DateTime date: dates) {
            pushedOutSchedule.add(WorkingDay.nearestWorkingDayOnOrAfter(scheduledEvent.nextEventDateAfter(date),
                                                                        workingDays));
        }
        return pushedOutSchedule;
    }

    /**
     * Given that the date is in a non-moratorium holiday, shift it past the holiday until either it is no longer
     * in a holiday or moratorium, or until it no longer moves (e.g., lands in a same-day holiday).
     *
     * <p> If the date shifts into a moratorium period, then shift it out using the RepaymentRuleType of
     * the most recent non-moratorium holiday that the date was shifted out of. For example, if shifting
     * the date out of a next-working-day holiday lands it in a moratorium period, then use the
     * next-working-day repayment rule to shift it past the moratorium period.</p>
     *
     * @param date the DateTime to be shifted
     * @return the shifted date
     */
    private DateTime shiftDatePastNonMoratoriumHoliday (DateTime date) {

        assert date != null;
        assert isEnclosedByAHoliday(date);
        assert ! isEnclosedByAHolidayWithRepaymentRule(date, RepaymentRuleTypes.REPAYMENT_MORATORIUM);

        Holiday currentlyEnclosingHoliday = getHolidayEnclosing(date);
        RepaymentRuleTypes mostRecentNonMoratoriumRepaymentRule
                                = currentlyEnclosingHoliday.getRepaymentRuleType(); //never REPAYMENT_MORATORIUM
        DateTime previousDate = null;
        DateTime adjustedDate = date;

        do {
            previousDate = adjustedDate;
            if (currentlyEnclosingHoliday.getRepaymentRuleType() == RepaymentRuleTypes.REPAYMENT_MORATORIUM) {
                adjustedDate = buildHolidayFromCurrentHolidayWithRepaymentRule (currentlyEnclosingHoliday,
                                                              mostRecentNonMoratoriumRepaymentRule)
                                    .adjust(previousDate, workingDays, scheduledEvent);
            } else {
                adjustedDate = (new BasicWorkingDayStrategy(workingDays))
                                    .adjust(currentlyEnclosingHoliday.adjust(previousDate, workingDays, scheduledEvent));
                mostRecentNonMoratoriumRepaymentRule = currentlyEnclosingHoliday.getRepaymentRuleType();
            }
            if (isEnclosedByAHoliday(adjustedDate)) {
                currentlyEnclosingHoliday = getHolidayEnclosing(adjustedDate);
            }
        } while (isEnclosedByAHoliday(adjustedDate) && (! adjustedDate.equals(previousDate)));

        return adjustedDate;
    }

    private Holiday buildHolidayFromCurrentHolidayWithRepaymentRule (Holiday originalHoliday, RepaymentRuleTypes rule) {
        HolidayPK holidayPK = new HolidayPK((short)1, originalHoliday.getFromDate().toDate());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity(rule.getValue(), "lookup.value.key");
        try {
            return new HolidayBO(holidayPK, originalHoliday.getThruDate().toDate(), "temporaryHoliday", repaymentRuleEntity);
        } catch (ApplicationException e) {
            throw new IllegalStateException("Could not create temporary holiday", e);
        }
    }

    private boolean isEnclosedByAHolidayWithRepaymentRule (DateTime date, RepaymentRuleTypes rule) {
        for (Holiday holiday : this.upcomingHolidays) {
            if (holiday.encloses(date.toDate()) && (holiday.getRepaymentRuleType() == rule)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEnclosedByAHoliday (DateTime date) {
        for (Holiday holiday : this.upcomingHolidays) {
            if (holiday.encloses(date.toDate())) {
                return true;
            }
        }
        return false;
    }

    private Holiday getHolidayEnclosing (DateTime date) {

        assert isEnclosedByAHoliday(date);

        Holiday holidayEnclosingDate = null;
        for (Holiday holiday : upcomingHolidays) {
            if (holiday.encloses(date.toDate())) {
                holidayEnclosingDate = holiday;
            }
        }
        return holidayEnclosingDate;
    }

    /*
     * TODO KeithP: These methods should be generic and moved to a ListUtils class
     */
    private List<DateTime> rest (List<DateTime> dates) {

        assert dates != null;

        List<DateTime> rest = new ArrayList<DateTime>();
        for (int i = 1; i < dates.size(); i++) {
            rest.add(dates.get(i));
        }
        return rest;
    }

    private List<DateTime> makeList (DateTime first, List<DateTime> rest) {

        assert first != null;
        assert rest != null;

        List<DateTime> newList = rest;
        newList.add(0, first);
        return newList;
    }
}
