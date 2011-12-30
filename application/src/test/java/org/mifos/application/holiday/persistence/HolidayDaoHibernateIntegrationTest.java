/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.*;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.Localization;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.dto.domain.HolidayDetails;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfiguration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class HolidayDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private HolidayDao holidayDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private OfficeBO areaOffice1;

    private OfficeBO branch1;

    private OfficeBO areaOffice2;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        Locale locale = Localization.getInstance().getConfiguredLocale();
        AuditConfiguration.init(locale);

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
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
    public void isNotFutureRepaymentHolidayWhenHolidayDoesNotExist() {
        DateTime secondLastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        Assert.assertFalse(holidayDao.isFutureRepaymentHoliday((short) 1, secondLastDayOfYear.toLocalDate().toString()));
    }

    @Test
    @Ignore
    public void sameDayHolidayIsNotFutureRepaymentHoliday() {
        DateTime secondLastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        Holiday sameDayRepaymentHoliday = new HolidayBuilder().from(secondLastDayOfYear).to(secondLastDayOfYear).withSameDayAsRule().build();
        insertHoliday(sameDayRepaymentHoliday);
        Assert.assertFalse(holidayDao.isFutureRepaymentHoliday((short) 1, secondLastDayOfYear.toLocalDate().toString()));
    }

    @Test
    @Ignore
    public void nextDayRepaymentIsFutureRepaymentHoliday() {
        DateTime secondLastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        Holiday nextDayRepaymentHoliday = new HolidayBuilder().from(secondLastDayOfYear).to(secondLastDayOfYear).withNextMeetingRule().build();
        insertHoliday(nextDayRepaymentHoliday);
        Assert.assertTrue(holidayDao.isFutureRepaymentHoliday((short) 1, secondLastDayOfYear.toLocalDate().toString()));
    }


    @Test
    public void shouldSaveFutureHoliday() throws Exception {

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext((short) 1);
        int initialCount = holidays.size();

        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));
        Holiday futureHoliday = new HolidayBuilder().from(new DateTime()).to(new DateTime().plusDays(1)).appliesTo(
                headOffice).build();

        StaticHibernateUtil.startTransaction();
        holidayDao.save(futureHoliday);
        StaticHibernateUtil.flushSession();

        holidays = holidayDao.findAllHolidaysThisYearAndNext(headOffice.getOfficeId());
        assertFalse(holidays.isEmpty());
        assertThat(holidays.size() - initialCount, is(1));
    }

    @Ignore
    @Test
    public void shouldFindAllHolidaysOrderedByFromDateAscending() throws Exception {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight()
                .toDateTime();
        DateTime lastDayOfYear = secondlastDayOfYear.plusDays(1);
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2)
                .toDateMidnight().toDateTime();
        DateTime thirdOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(3).toDateMidnight()
                .toDateTime();

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(new Short("1"));
        int initialCountOfHolidays = holidays.size();

        List<Holiday> futureHolidays = holidayDao.findAllHolidaysFromDateAndNext(new Short("1"), secondOfJanNextYear.toLocalDate().toString());
        int initialCountOfFutureHolidays = futureHolidays.size();

        Holiday holiday1 = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holiday2 = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holiday3 = new HolidayBuilder().from(thirdOfJanNextYear).to(thirdOfJanNextYear).build();
        Holiday holiday4 = new HolidayBuilder().from(lastDayOfYear).to(lastDayOfYear).build();
        insertHoliday(holiday2);
        insertHoliday(holiday3);
        insertHoliday(holiday1);
        insertHoliday(holiday4);

        holidays = holidayDao.findAllHolidaysThisYearAndNext(new Short("1"));

        assertThat(holidays.size() - initialCountOfHolidays, is(4));

        assertTrue(holidays.get(0).encloses(secondlastDayOfYear.toDate()));
        assertTrue(holidays.get(1).encloses(lastDayOfYear.toDate()));
        assertTrue(holidays.get(2).encloses(secondOfJanNextYear.toDate()));
        assertTrue(holidays.get(3).encloses(thirdOfJanNextYear.toDate()));

        futureHolidays = holidayDao.findAllHolidaysFromDateAndNext(new Short("1"), secondOfJanNextYear.toLocalDate().toString());

        assertThat(futureHolidays.size() - initialCountOfFutureHolidays, is(2));

        assertTrue(futureHolidays.get(0).encloses(secondOfJanNextYear.toDate()));
        assertTrue(futureHolidays.get(1).encloses(thirdOfJanNextYear.toDate()));
    }

    @Ignore
    @Test
    public void shouldFindWhetherGivenDateIsAHoliday() {
        Short officeId = Short.valueOf("1");
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        insertHoliday(new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build());
        assertThat(holidayDao.isHoliday(officeId, secondlastDayOfYear.toLocalDate().toString()), is(true));

        DateTime thirdOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(3).toDateMidnight().toDateTime();
        DateTime sixthOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(6).toDateMidnight().toDateTime();
        DateTime tenthOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(10).toDateMidnight().toDateTime();
        DateTime eleventhOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(11).toDateMidnight().toDateTime();
        insertHoliday(new HolidayBuilder().from(thirdOfJanNextYear).to(tenthOfJanNextYear).build());
        assertThat(holidayDao.isHoliday(officeId, thirdOfJanNextYear.toLocalDate().toString()), is(true));
        assertThat(holidayDao.isHoliday(officeId, sixthOfJanNextYear.toLocalDate().toString()), is(true));
        assertThat(holidayDao.isHoliday(officeId, tenthOfJanNextYear.toLocalDate().toString()), is(true));
        assertThat(holidayDao.isHoliday(officeId, eleventhOfJanNextYear.toLocalDate().toString()), is(false));
    }

    @Test
    public void shouldfindCurrentAndFutureOfficeHolidaysEarliestFirst() throws Exception {

        DateTime yesterday = new DateTime().minusDays(1);
        Set<HolidayBO> holidays;
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Fourth").from(yesterday.plusWeeks(4)).to(
                yesterday.plusWeeks(5)).build());
        holidays.add((HolidayBO) new HolidayBuilder().withName("Second").from(yesterday.plusDays(1)).to(
                yesterday.plusWeeks(7)).build());
        headOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(headOffice);

        // builder not setting searchId correctly due to not going thru office.save (which uses HierarchyManager)
        String headOfficeSearchId = headOffice.getSearchId();

        OfficeBO areaOffice = new OfficeBuilder().withName("Area Office").withSearchId(headOfficeSearchId + "25.")
                .withParentOffice(headOffice).withGlobalOfficeNum("area56").build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Fifth").from(yesterday.plusWeeks(8)).to(
                yesterday.plusWeeks(9)).build());
        areaOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(areaOffice);

        OfficeBO myOffice = new OfficeBuilder().withName("My Office").withSearchId(headOfficeSearchId + "25.1.")
                .withParentOffice(areaOffice).withGlobalOfficeNum("my001").build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Third").from(yesterday.plusWeeks(3)).to(
                yesterday.plusWeeks(4)).build());
        holidays.add((HolidayBO) new HolidayBuilder().withName("First").from(yesterday).to(yesterday.plusWeeks(2))
                .build());
        myOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(myOffice);

        OfficeBO anotherOffice = new OfficeBuilder().withName("Another Unconnected Office").withSearchId(
                headOfficeSearchId + "26.").withParentOffice(headOffice).withGlobalOfficeNum("n/a001").build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("N/A").from(yesterday.minusWeeks(3)).to(
                yesterday.plusWeeks(4)).build());
        anotherOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(anotherOffice);

        List<Holiday> myHolidays = holidayDao.findCurrentAndFutureOfficeHolidaysEarliestFirst(myOffice.getOfficeId());

        assertThat(myHolidays.size(), is(5));
        assertThat(myHolidays.get(0).getName(), is("First"));
        assertThat(myHolidays.get(1).getName(), is("Second"));
        assertThat(myHolidays.get(2).getName(), is("Third"));
        assertThat(myHolidays.get(3).getName(), is("Fourth"));
        assertThat(myHolidays.get(4).getName(), is("Fifth"));
    }

    @Test
    public void shouldThrowExceptionWhenFutureHolidaysApplicableToNewParentOfficeDifferFromPreviousParentOffice() throws Exception {

        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        // setup
        createOfficeHierarchyUnderHeadOffice(headOffice);

        DateTime tomorrow = new DateTime().plusDays(1);
        HolidayDetails holidayDetails = new HolidayBuilder().withName("areaOffice2Holiday").from(tomorrow).to(tomorrow).buildDto();
        IntegrationTestObjectMother.createHoliday(holidayDetails, Arrays.asList(areaOffice2.getOfficeId()));

        HolidayDetails branchOnlyHolidayDetails = new HolidayBuilder().withName("branchOnlyHoliday").from(tomorrow).to(tomorrow).buildDto();
        IntegrationTestObjectMother.createHoliday(branchOnlyHolidayDetails, Arrays.asList(branch1.getOfficeId()));

        // refetch
        branch1 = IntegrationTestObjectMother.findOfficeById(branch1.getOfficeId());

        // exercise test
        try {
            holidayDao.validateNoExtraFutureHolidaysApplicableOnParentOffice(branch1.getParentOffice().getOfficeId(), areaOffice2.getOfficeId());
            fail("shouldThrowExceptionWhenFutureHolidaysApplicableToNewParentOfficeDifferFromPreviousParentOffice");
        } catch (ApplicationException e) {
            assertThat(e.getKey(), is(OfficeConstants.ERROR_REPARENT_NOT_ALLOWED_AS_FUTURE_APPLICABLE_HOLIDAYS_ARE_DIFFERENT_ON_PREVIOUS_AND_NEW_PARENT));
        }
    }

    @Test
    public void shouldNotThrowExceptionWhenFutureHolidaysApplicableToNewParentOfficeDoNotDifferFromPreviousParentOffice() throws Exception {

        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        // setup
        createOfficeHierarchyUnderHeadOffice(headOffice);
        DateTime tomorrow = new DateTime().plusDays(1);

        HolidayDetails holidayDetails = new HolidayBuilder().withName("areaOffice2Holiday").from(tomorrow).to(tomorrow).buildDto();
        IntegrationTestObjectMother.createHoliday(holidayDetails, Arrays.asList(areaOffice2.getOfficeId(), areaOffice1.getOfficeId()));

        HolidayDetails branchOnlyHolidayDetails = new HolidayBuilder().withName("branchOnlyHoliday").from(tomorrow).to(tomorrow).buildDto();
        IntegrationTestObjectMother.createHoliday(branchOnlyHolidayDetails, Arrays.asList(branch1.getOfficeId()));

        // refetch
        branch1 = IntegrationTestObjectMother.findOfficeById(branch1.getOfficeId());

        // exercise test
        holidayDao.validateNoExtraFutureHolidaysApplicableOnParentOffice(branch1.getParentOffice().getOfficeId(), areaOffice2.getOfficeId());
    }

    @Test
    public void shouldRetrieveOfficeNames() throws Exception {

        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        // setup
        createOfficeHierarchyUnderHeadOffice(headOffice);

        List<Short> officeIds = Arrays.asList(branch1.getOfficeId());

        // exercise test
        List<String> officeNames = holidayDao.retrieveApplicableOfficeNames(officeIds);

        // verification
        assertThat(officeNames, hasItem(branch1.getOfficeName()));
    }

    private void insertHoliday(final Holiday holiday) {
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));

        HolidayDetails holidayDetails = new HolidayDetails("HolidayDaoTest", holiday.getFromDate().toDate(), holiday
                .getThruDate().toDate(), holiday.getRepaymentRuleType().getValue());

        List<Short> officeIds = Arrays.asList(headOffice.getOfficeId());
        IntegrationTestObjectMother.createHoliday(holidayDetails, officeIds);
    }

    private void createOfficeHierarchyUnderHeadOffice(OfficeBO headOffice) {
        areaOffice1 = new OfficeBuilder().areaOffice()
                                                  .withParentOffice(headOffice)
                                                  .withName("areaOffice1")
                                                  .withGlobalOfficeNum("x002")
                                                  .withSearchId("1.1.2")
                                                  .build();
        IntegrationTestObjectMother.createOffice(areaOffice1);

        branch1 = new OfficeBuilder().branchOffice()
                                              .withParentOffice(areaOffice1)
                                              .withName("branch1")
                                              .withGlobalOfficeNum("x005")
                                              .withSearchId("1.1.2.1")
                                              .build();
        IntegrationTestObjectMother.createOffice(branch1);

        areaOffice2 = new OfficeBuilder().areaOffice()
                                                  .withParentOffice(headOffice)
                                                  .withName("areaOffice2")
                                                  .withGlobalOfficeNum("x003")
                                                  .withSearchId("1.1.3")
                                                  .build();
        IntegrationTestObjectMother.createOffice(areaOffice2);
    }
}