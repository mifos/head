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

package org.mifos.application.holiday.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.Localization;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class HolidayDaoHibernateIntegrationTest {

    @Autowired
    private HolidayDao holidayDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        Locale locale = Localization.getInstance().getMainLocale();
        AuditConfigurtion.init(locale);

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();
    }

    @Test
    public void shouldSaveFutureHoliday() throws Exception {

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext((short) 1);
        assertTrue(holidays.isEmpty());

        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));
        Holiday futureHoliday = new HolidayBuilder().from(new DateTime()).to(new DateTime().plusDays(1)).appliesTo(headOffice).build();

        StaticHibernateUtil.startTransaction();
        holidayDao.save(futureHoliday);
        StaticHibernateUtil.commitTransaction();

        holidays = holidayDao.findAllHolidaysThisYearAndNext(headOffice.getOfficeId());
        assertFalse(holidays.isEmpty());
    }

    @Test
    public void shouldFindAllHolidaysWithinThisAndNextYear() throws Exception {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2).toDateMidnight().toDateTime();
        DateTime secondOfJanTwoYears = secondOfJanNextYear.plusYears(1);

        Holiday holidayThisYear = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holidayNextYear = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holidayTwoYearsAway = new HolidayBuilder().from(secondOfJanTwoYears).to(secondOfJanTwoYears).build();
        insert(holidayThisYear);
        insert(holidayNextYear);
        insert(holidayTwoYearsAway);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(new Short("1"));

        assertFalse(holidays.isEmpty());
        assertThat(holidays.size(), is(2));
    }

    @Test
    public void shouldFindAllHolidaysOrderedByFromDateAscending() throws Exception {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight()
                .toDateTime();
        DateTime lastDayOfYear = secondlastDayOfYear.plusDays(1);
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2)
                .toDateMidnight().toDateTime();
        DateTime thirdOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(3).toDateMidnight()
                .toDateTime();

        Holiday holiday1 = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holiday2 = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holiday3 = new HolidayBuilder().from(thirdOfJanNextYear).to(thirdOfJanNextYear).build();
        Holiday holiday4 = new HolidayBuilder().from(lastDayOfYear).to(lastDayOfYear).build();
        insert(holiday2);
        insert(holiday3);
        insert(holiday1);
        insert(holiday4);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(new Short("1"));

        assertThat(holidays.size(), is(4));

        assertTrue(holidays.get(0).encloses(secondlastDayOfYear.toDate()));
        assertTrue(holidays.get(1).encloses(lastDayOfYear.toDate()));
        assertTrue(holidays.get(2).encloses(secondOfJanNextYear.toDate()));
        assertTrue(holidays.get(3).encloses(thirdOfJanNextYear.toDate()));
    }

    @Test
    public void shouldfindCurrentAndFutureOfficeHolidaysEarliestFirst() throws Exception {

        DateTime yesterday = new DateTime().minusDays(1);
        Set<HolidayBO> holidays;
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Fourth").from(yesterday.plusWeeks(4)).to(yesterday.plusWeeks(5)).build());
        holidays.add((HolidayBO) new HolidayBuilder().withName("Second").from(yesterday.plusDays(1)).to(yesterday.plusWeeks(7)).build());
        headOffice.setHolidays(holidays);

        OfficeBO areaOffice = new OfficeBuilder().withParentOffice(headOffice).withGlobalOfficeNum("area56").build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Fifth").from(yesterday.plusWeeks(8)).to(yesterday.plusWeeks(9)).build());
        areaOffice.setHolidays(holidays);

        IntegrationTestObjectMother.createOffice(areaOffice);

        OfficeBO myOffice = new OfficeBuilder().withParentOffice(areaOffice).withGlobalOfficeNum("xxx234").build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Third").from(yesterday.plusWeeks(3)).to(yesterday.plusWeeks(4)).build());
        holidays.add((HolidayBO) new HolidayBuilder().withName("First").from(yesterday).to(yesterday.plusWeeks(2)).build());
        myOffice.setHolidays(holidays);

        IntegrationTestObjectMother.createOffice(myOffice);

        OfficeBO anotherOffice = new OfficeBuilder().withParentOffice(headOffice).build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("N/A").from(yesterday.minusWeeks(3)).to(yesterday.plusWeeks(4)).build());
        IntegrationTestObjectMother.createOffice(anotherOffice);

        List<Holiday> myHolidays = holidayDao.findCurrentAndFutureOfficeHolidaysEarliestFirst(myOffice.getOfficeId());

        assertThat(myHolidays.size(), is(5));
        assertThat(myHolidays.get(0).getName(), is("First"));
        assertThat(myHolidays.get(1).getName(), is("Second"));
        assertThat(myHolidays.get(2).getName(), is("Third"));
        assertThat(myHolidays.get(3).getName(), is("Fourth"));
        assertThat(myHolidays.get(4).getName(), is("Fifth"));
    }

    private void insert(final Holiday holiday) {
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        HolidayDetails holidayDetails = new HolidayDetails("HolidayDaoTest", holiday.getFromDate().toDate(), holiday.getThruDate().toDate(), holiday.getRepaymentRuleType());

        List<Short> officeIds = Arrays.asList(headOffice.getOfficeId());
        IntegrationTestObjectMother.createHoliday(holidayDetails, officeIds);
    }
}