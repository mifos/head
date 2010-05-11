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
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;

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
        holidayDao.getSession().getTransaction().rollback();
    }

    public void testShouldSaveHoliday() throws Exception {

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();
        assertTrue(holidays.isEmpty());

        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday Dao", new Date(),null,RepaymentRuleTypes.fromInt(1));
        holidayDetails.disableValidation(true);
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short)1);
        new HolidayServiceFacadeWebTier(new OfficePersistence()).createHoliday(holidayDetails, officeIds);

        holidays = holidayDao.findAllHolidaysThisYearAndNext();
        assertFalse(holidays.isEmpty());
    }

    public void testShouldFindAllHolidaysWithinThisAndNextYear() throws PersistenceException {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2).toDateMidnight().toDateTime();
        DateTime secondOfJanTwoYears = secondOfJanNextYear.plusYears(1);

        Holiday holidayThisYear = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holidayNextYear = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holidayTwoYearsAway = new HolidayBuilder().from(secondOfJanTwoYears).to(secondOfJanTwoYears).build();
        insert(holidayThisYear);
        insert(holidayNextYear);
        insert(holidayTwoYearsAway);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();

        assertFalse(holidays.isEmpty());
        assertThat(holidays.size(), is(2));
    }

    public void testShouldFindAllHolidaysOrderedByFromDateAscending() throws PersistenceException {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight().toDateTime();
        DateTime lastDayOfYear = secondlastDayOfYear.plusDays(1);
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2).toDateMidnight().toDateTime();
        DateTime thirdOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(3).toDateMidnight().toDateTime();

        Holiday holiday1 = new HolidayBuilder().from(secondlastDayOfYear).to(secondlastDayOfYear).build();
        Holiday holiday2 = new HolidayBuilder().from(secondOfJanNextYear).to(secondOfJanNextYear).build();
        Holiday holiday3 = new HolidayBuilder().from(thirdOfJanNextYear).to(thirdOfJanNextYear).build();
        Holiday holiday4 = new HolidayBuilder().from(lastDayOfYear).to(lastDayOfYear).build();
        insert(holiday2);
        insert(holiday3);
        insert(holiday1);
        insert(holiday4);

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext();

        assertThat(holidays.size(), is(4));

        assertTrue(holidays.get(0).encloses(secondlastDayOfYear.toDate()));
        assertTrue(holidays.get(1).encloses(lastDayOfYear.toDate()));
        assertTrue(holidays.get(2).encloses(secondOfJanNextYear.toDate()));
        assertTrue(holidays.get(3).encloses(thirdOfJanNextYear.toDate()));
    }

    private void insert(final Holiday holiday) throws PersistenceException {
        holidayDao.save(holiday);
    }
}
