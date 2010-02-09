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

package org.mifos.application.holiday.util.helpers;

import static org.mifos.application.holiday.util.helpers.RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT;
import static org.mifos.application.holiday.util.helpers.RepaymentRuleTypes.NEXT_WORKING_DAY;
import static org.mifos.framework.util.helpers.DateUtils.getCalendar;
import static org.mifos.framework.util.helpers.DateUtils.getDateWithoutTimeStamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.LoanScheduleEntity;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.service.HolidayBusinessService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.DateTimeService;

/**
 * Helper methods related to holidays logic
 */
public class HolidayUtils {

    public static boolean isWorkingDay(Calendar day) throws RuntimeException {
        return FiscalCalendarRules.isWorkingDay(day);
    }

    static HolidayBO inHoliday(Calendar pday, List<HolidayBO> holidays) {
        Calendar day = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        day.setTimeInMillis(0);
        day.set(pday.get(Calendar.YEAR), pday.get(Calendar.MONTH), pday.get(Calendar.DAY_OF_MONTH));
        Date givenDate = getDateWithoutTimeStamp(day.getTimeInMillis());
        for (HolidayBO holiday : holidays) {
            if (holiday.encloses(givenDate))
                return holiday;
        }
        return null;
    }

    public static Calendar adjustDate(Calendar day, MeetingBO meeting) throws MeetingException {
        Calendar adjustedDate = isWorkingDay(day) ? day : getNextWorkingDay(day);
        HolidayBO holiday;
        try {
            holiday = inHoliday(adjustedDate, getAllHolidaysInCurrentAndNextYears(adjustedDate.get(Calendar.YEAR)));
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        }
        if (holiday == null)
            return adjustedDate;
        return adjustDateUsingRepaymentRule(holiday.getRepaymentRuleId(), adjustedDate, meeting);
    }

    public static Calendar getNextWorkingDay(Calendar day) {
        do {
            day.add(Calendar.DATE, 1);
        } while (!isWorkingDay(day));
        return day;
    }

    private static Calendar adjustDateUsingRepaymentRule(Short repaymentRuleId, Calendar adjustedDate, MeetingBO meeting)
            throws MeetingException {
        if (NEXT_WORKING_DAY.getValue().equals(repaymentRuleId)) {
            adjustedDate.add(Calendar.DATE, 1);
            return adjustDate(adjustedDate, meeting);
        } else if (NEXT_MEETING_OR_REPAYMENT.getValue().equals(repaymentRuleId)) {
            Date nextDate = meeting.getNextScheduleDateAfterRecurrenceWithoutAdjustment(adjustedDate.getTime());
            return adjustDate(getCalendar(nextDate), meeting);
        }
        return adjustedDate;
    }

    private static List<HolidayBO> getAllHolidaysInCurrentAndNextYears(int year) throws ServiceException {
        List<HolidayBO> holidays = new ArrayList<HolidayBO>();
        HolidayBusinessService service = new HolidayBusinessService();
        holidays.addAll(service.getHolidays(year));
        holidays.addAll(service.getHolidays(year + 1));
        return holidays;
    }

    public static void rescheduleLoanRepaymentDates(HolidayBO holiday) throws RuntimeException {
        try {
            List<LoanScheduleEntity> loanSchedulsList = new HolidayBusinessService().getAllLoanSchedule(holiday);
            for (LoanScheduleEntity loanScheduleEntity : loanSchedulsList) {
                LoanBO loan = new LoanBusinessService().getAccount(loanScheduleEntity.getAccount().getAccountId());
                MeetingBO loadedMeeting = loan.getLoanMeeting();

                Date adjustedDate = HolidayUtils.adjustDate(getCalendar(loanScheduleEntity.getActionDate()),
                        loadedMeeting).getTime();
                loanScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } catch (MeetingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rescheduleSavingDates(HolidayBO holiday) throws RuntimeException {
        try {
            List<SavingsScheduleEntity> savingSchedulsList = new HolidayBusinessService().getAllSavingSchedule(holiday);
            for (SavingsScheduleEntity savingScheduleEntity : savingSchedulsList) {
                SavingsBO saving = (SavingsBO) new AccountBusinessService().getAccount(savingScheduleEntity
                        .getAccount().getAccountId());
                MeetingBO loadedMeeting = saving.getCustomer().getCustomerMeeting().getMeeting();
                Date adjustedDate = HolidayUtils.adjustDate(getCalendar(savingScheduleEntity.getActionDate()),
                        loadedMeeting).getTime();
                savingScheduleEntity.setActionDate(new java.sql.Date(adjustedDate.getTime()));
            }
        } catch (ServiceException e) {
            throw new RuntimeException(e);
        } catch (MeetingException e) {
            throw new RuntimeException(e);
        }
    }
}