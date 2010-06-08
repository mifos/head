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
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HolidayDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public HolidayDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    private HolidayDaoHibernate holidayDao;

    private GenericDao genericDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        genericDao = new GenericDaoHibernate();
        holidayDao = new HolidayDaoHibernate(genericDao);

    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.getSessionTL().clear();
        rollback();
    }

    private void rollback() {
        Transaction transaction = StaticHibernateUtil.getSessionTL().getTransaction();
        if (transaction.isActive()) {
            transaction.rollback();
        }
    }

    public void testShouldSaveHoliday() throws Exception {

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext((short) 1);
        assertTrue(holidays.isEmpty());

        DateTime today = new DateTime();
        insert("TestHoliday", today, today, RepaymentRuleTypes.SAME_DAY);

        holidays = holidayDao.findAllHolidaysThisYearAndNext((short) 1);
        assertFalse(holidays.isEmpty());
    }

    public void testShouldRetunUnappliedHolidays() throws Exception {
        DateTime today = new DateTime();
        insert("TestHoliday", today, today, RepaymentRuleTypes.SAME_DAY);

        List<Short> officeIds = new LinkedList<Short>();
        short officeId1 = (short) 1;
        short officeId2 = (short) 2;
        officeIds.add(officeId1);
        officeIds.add(officeId2);

        Map<Short, List<HolidayBO>> unappliedOfficeHolidays = holidayDao.unappliedOfficeHolidays(officeIds);
        assertEquals(2, unappliedOfficeHolidays.size());

        List<HolidayBO> unappliedholidaysOfOffice1 = unappliedOfficeHolidays.get(officeId1);
        assertNotNull(unappliedholidaysOfOffice1);
        assertEquals(1, unappliedholidaysOfOffice1.size());
        assertEquals("TestHoliday", (unappliedholidaysOfOffice1.get(0)).getHolidayName());

        List<HolidayBO> unappliedholidaysOfOffice2 = unappliedOfficeHolidays.get(officeId2);
        assertNotNull(unappliedholidaysOfOffice2);
        assertEquals(1, unappliedholidaysOfOffice2.size());
        assertEquals("TestHoliday", (unappliedholidaysOfOffice2.get(0)).getHolidayName());
    }

    public void testShouldRetunHolidaysForOffices() throws Exception {
        DateTime today = new DateTime();
        DateTime thisDayNextYear = today.plusYears(1);
        DateTime thisDayYearAfterNext = thisDayNextYear.plusYears(1);
        insert("holiday1", today, today, RepaymentRuleTypes.SAME_DAY);
        insert("holiday2", thisDayNextYear, thisDayNextYear, RepaymentRuleTypes.SAME_DAY);
        insert("holiday3", thisDayYearAfterNext, thisDayYearAfterNext, RepaymentRuleTypes.SAME_DAY);

        List<Short> officeIds = new LinkedList<Short>();
        short officeId1 = (short) 1;
        short officeId2 = (short) 2;
        officeIds.add(officeId1);
        officeIds.add(officeId2);

        Map<Short, List<HolidayBO>> holidaysForOffices = holidayDao.holidaysForOffices(officeIds, thisDayNextYear
                .getYear(), thisDayYearAfterNext.getYear());
        assertEquals(2, holidaysForOffices.size());

        List<HolidayBO> holidaysForOffices1 = holidaysForOffices.get(officeId1);
        assertNotNull(holidaysForOffices1);
        assertEquals(2, holidaysForOffices1.size());
        assertThat(holidaysForOffices1.get(0).getHolidayName(), is(not("holiday1")));
        assertThat(holidaysForOffices1.get(1).getHolidayName(), is(not("holiday1")));

        List<HolidayBO> holidaysForOffices2 = holidaysForOffices.get(officeId2);
        assertNotNull(holidaysForOffices2);
        assertEquals(2, holidaysForOffices2.size());
        assertThat(holidaysForOffices2.get(0).getHolidayName(), is(not("holiday1")));
        assertThat(holidaysForOffices2.get(1).getHolidayName(), is(not("holiday1")));
    }

    public void testShouldFindAllHolidaysWithinThisAndNextYear() throws ServiceException {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight()
                .toDateTime();
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2)
                .toDateMidnight().toDateTime();
        DateTime secondOfJanTwoYears = secondOfJanNextYear.plusYears(1);

        insert("holiday1", secondlastDayOfYear, secondlastDayOfYear, RepaymentRuleTypes.SAME_DAY);
        insert("holiday2", secondOfJanNextYear, secondOfJanNextYear, RepaymentRuleTypes.SAME_DAY);
        insert("holiday3", secondOfJanTwoYears, secondOfJanTwoYears, RepaymentRuleTypes.SAME_DAY);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(new Short("1"));

        assertFalse(holidays.isEmpty());
        assertThat(holidays.size(), is(2));
    }

    public void testShouldFindAllHolidaysOrderedByFromDateAscending() throws ServiceException {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight()
                .toDateTime();
        DateTime lastDayOfYear = secondlastDayOfYear.plusDays(1);
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2)
                .toDateMidnight().toDateTime();
        DateTime thirdOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(3).toDateMidnight()
                .toDateTime();

        insert("holiday1", secondlastDayOfYear, secondlastDayOfYear, RepaymentRuleTypes.SAME_DAY);
        insert("holiday2", secondOfJanNextYear, secondOfJanNextYear, RepaymentRuleTypes.SAME_DAY);
        insert("holiday3", thirdOfJanNextYear, thirdOfJanNextYear, RepaymentRuleTypes.SAME_DAY);
        insert("holiday4", lastDayOfYear, lastDayOfYear, RepaymentRuleTypes.SAME_DAY);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(new Short("1"));

        assertThat(holidays.size(), is(4));

        assertTrue(holidays.get(0).encloses(secondlastDayOfYear.toDate()));
        assertTrue(holidays.get(1).encloses(lastDayOfYear.toDate()));
        assertTrue(holidays.get(2).encloses(secondOfJanNextYear.toDate()));
        assertTrue(holidays.get(3).encloses(thirdOfJanNextYear.toDate()));
    }

    private void insert(String name, DateTime fromDate, DateTime toDate, RepaymentRuleTypes repaymentRuleType)
            throws ServiceException {
        HolidayDetails holidayDetails = new HolidayDetails(name, fromDate.toDate(), toDate.toDate(), repaymentRuleType);
        holidayDetails.disableValidation(true);
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short) 1);
        new HolidayServiceFacadeWebTier(new OfficePersistence()).createHoliday(holidayDetails, officeIds);
    }
}
