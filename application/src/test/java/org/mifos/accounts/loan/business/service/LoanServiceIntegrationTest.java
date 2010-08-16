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

package org.mifos.accounts.loan.business.service;

import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.calendar.DayOfWeek;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class LoanServiceIntegrationTest {

    private FiscalCalendarRules fiscalCalendarRules = new FiscalCalendarRules();
    private List<WeekDay> savedWorkingDays = fiscalCalendarRules.getWorkingDays();


    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();
        fiscalCalendarRules.setWorkingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY");
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
        fiscalCalendarRules.setWorkingDays(weekDaysToPropertyString(savedWorkingDays));
        new DateTimeService().resetToCurrentSystemDateTime();
    }

    @Test
    public void validateDisbursementDateShouldReturnNormally() throws Exception {
        new LoanService().validateDisbursementDateForNewLoan((short)1,new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday()));
    }

    @Test(expected=ApplicationException.class)
    public void validateDisbursementDateOnWeekendShouldThrowException () throws Exception {
        new LoanService().validateDisbursementDateForNewLoan((short)1, new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.sunday()));
    }

    @Test(expected=ApplicationException.class)
    public void validateDisbursementDateInHolidayShouldThrowException () throws Exception {
        DateTime disbursementDate = new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.monday());
        buildAndPersistHoliday(disbursementDate.minusDays(1), disbursementDate.plusDays(1), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        new LoanService().validateDisbursementDateForNewLoan((short)1, new DateMidnight().toDateTime().withDayOfWeek(DayOfWeek.sunday()));
    }

    private void buildAndPersistHoliday (DateTime start, DateTime through, RepaymentRuleTypes rule) {
        HolidayBO holiday = (HolidayBO) new HolidayBuilder().from(start)
                                                               .to(through)
                                                               .withRepaymentRule(rule).build();
        IntegrationTestObjectMother.saveHoliday(holiday);
    }


    private String weekDaysToPropertyString(List<WeekDay> weekDays) {
        String propertyString = "";
        Boolean first = true;
        for (WeekDay day : weekDays) {
            if (!first) {
                propertyString = propertyString + ",";
            } else {
                first = false;
            }
            propertyString = propertyString + day.toString().toUpperCase();
        }
        return propertyString;
    }

}
