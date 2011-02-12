/*
 * Copyright Grameen Foundation USA
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
package org.mifos.application.holiday.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.LoanExceptionConstants;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.service.HolidayService;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.calendar.DayOfWeek;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.service.BusinessRuleException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HolidayServiceFacadeWebTierTest {

    // class under test
    private HolidayServiceFacade holidayServiceFacade;

    @Mock
    private HolidayService holidayService;

    @Mock
    private HolidayDao holidayDao;

    private HolidayDetails holidayDetails;

    private Locale locale;

    private String dateFormat;

    private Short officeId;

    @Before
    public void setupAndInjectDependencies() {
        String name = "testHoliday";
        DateTime dateTime = new DateTime();
        Date fromDate = dateTime.plusDays(10).toDate();
        Date thruDate = dateTime.plusDays(20).toDate();
        RepaymentRuleTypes repaymentRule = RepaymentRuleTypes.SAME_DAY;
        holidayDetails = new HolidayDetails(name, fromDate, thruDate, repaymentRule.getValue());
        holidayServiceFacade =  new HolidayServiceFacadeWebTier(holidayService, holidayDao);
        locale = new Locale("en", "GB");
        dateFormat = computeDateFormat(locale);
        officeId = Short.valueOf("1");
    }

    private String computeDateFormat(Locale locale) {
        String dateSeparator = DateUtils.getDateSeparatorByLocale(locale, DateFormat.MEDIUM);
        return String.format("dd%sMMM%syyyy", dateSeparator, dateSeparator);
    }

    @Test
    public void validateDisbursementDateIsWorkingDayShouldThrowException () throws Exception {

        try {
            DateTime sundayDisbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.sunday());

            holidayServiceFacade.validateDisbursementDateForNewLoan(officeId, sundayDisbursementDate);
            fail("Should have thrown BusinessRuleException");
        } catch (BusinessRuleException e) {
            assertThat(e.getMessageKey(), is(LoanExceptionConstants.DISBURSEMENTDATE_MUST_BE_A_WORKING_DAY));
        }
    }

    @Test
    public void validateDisbursementDateIsNotInHolidayShouldThrowExceptionWhenDateIsInAHoliday () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        List<Holiday> holidays = new ArrayList<Holiday>();
        Holiday holiday = new HolidayBuilder().from(disbursementDate.minusDays(1)).to(disbursementDate.plusDays(6)).build();
        holidays.add(holiday);
        try {

            when(holidayDao.findAllHolidaysThisYearAndNext(officeId)).thenReturn(holidays);

            holidayServiceFacade.validateDisbursementDateForNewLoan(officeId, disbursementDate);
            fail("Should have thrown BusinessRuleException");
        } catch (BusinessRuleException e) {
            assertThat(e.getMessageKey(), is(LoanExceptionConstants.DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY));
        }
    }

    @Test
    public void validateDisbursementDateIsWorkingDayAndNotAHolidayShouldReturnNormally () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());

        when(holidayDao.findAllHolidaysThisYearAndNext(officeId)).thenReturn(new ArrayList<Holiday>());

        holidayServiceFacade.validateDisbursementDateForNewLoan(officeId, disbursementDate);
    }

    @Ignore
    @Test
    public void shouldCreateHoliday() throws Exception {

        List<Short> officeIds = Arrays.asList(Short.valueOf("1"), Short.valueOf("2"));

        holidayServiceFacade.createHoliday(holidayDetails, officeIds);

//        verify(officeDao).addHoliday(eq(Short.valueOf("1")), Mockito.any(HolidayBO.class));
//        verify(officeDao).addHoliday(eq(Short.valueOf("2")), Mockito.any(HolidayBO.class));
    }

    @Test
    public void isFutureRepaymentHoliday() {
        Calendar dueDate = toCalendar("01-Nov-2010");
        when(holidayService.isFutureRepaymentHoliday(dueDate, officeId)).thenReturn(true);
        Assert.assertTrue(holidayServiceFacade.isFutureRepaymentHoliday(officeId, dueDate));
        verify(holidayService, times(1)).isFutureRepaymentHoliday(dueDate, officeId);
    }

    @Test
    public void shouldDetermineIfGivenDayIsWorkingDay() {
        Calendar holiday = toCalendar("01-Nov-2010");
        when(holidayService.isWorkingDay(holiday, officeId)).thenReturn(false);
        assertThat(holidayServiceFacade.isWorkingDay(holiday, officeId), is(false));
        verify(holidayService, times(1)).isWorkingDay(holiday, officeId);
    }

    @Test
    public void shouldGetNextWorkingDay() {
        Calendar holiday = toCalendar("01-Nov-2010");
        Calendar workingDay = toCalendar("02-Nov-2010");
        when(holidayService.getNextWorkingDay(holiday, officeId)).thenReturn(workingDay);
        assertThat(holidayServiceFacade.getNextWorkingDay(holiday, officeId), is(workingDay));
        verify(holidayService, times(1)).getNextWorkingDay(holiday, officeId);
    }

    private Date toDate(String dateString) {
        return DateUtils.getDate(dateString, locale, dateFormat);
    }

    private Calendar toCalendar(String dateString) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(toDate(dateString));
        return calendar;
    }
}
