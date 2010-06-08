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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.joda.time.DateTime;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.persistence.OfficeDaoHibernate;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

/**
 * FIXME - rewrite holidayDaoHibernate integration tests.
 */
@Deprecated
public class HolidayDaoHibernateIntegrationTest extends MifosIntegrationTestCase {

    public HolidayDaoHibernateIntegrationTest() throws Exception {
        super();
    }

    private HolidayDaoHibernate holidayDao;
    private OfficeDaoHibernate officeDao;

    private GenericDao genericDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        genericDao = new GenericDaoHibernate();
        holidayDao = new HolidayDaoHibernate(genericDao);
        officeDao = new OfficeDaoHibernate(genericDao);

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

    public void ignore_testShouldSaveHoliday() throws Exception {

        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext((short) 1);
        assertTrue(holidays.isEmpty());

        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday Dao", new Date(), null, RepaymentRuleTypes
                .fromInt(1));

        HolidayBO holiday = HolidayBO.fromDto(holidayDetails);

        holidayDao.save(holiday);

        holidays = holidayDao.findAllHolidaysThisYearAndNext((short) 1);
        assertFalse(holidays.isEmpty());
    }

    public void ignore_testShouldFindAllHolidaysWithinThisAndNextYear() throws Exception {
        DateTime secondlastDayOfYear = new DateTime().withMonthOfYear(12).withDayOfMonth(30).toDateMidnight()
                .toDateTime();
        DateTime secondOfJanNextYear = new DateTime().plusYears(1).withMonthOfYear(1).withDayOfMonth(2)
                .toDateMidnight().toDateTime();
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

    public void ignore_testShouldFindAllHolidaysOrderedByFromDateAscending() throws Exception {
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

    public void testShouldfindCurrentAndFutureOfficeHolidaysEarliestFirst() throws OfficeException {

        DateTime yesterday = new DateTime().minusDays(1);
        Set<HolidayBO> holidays;
        OfficeBO headOffice = officeDao.findOfficeById((short) 1);

        OfficeBO myOffice = new OfficeBuilder().withParentOffice(headOffice).build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Second").from(yesterday.plusWeeks(3)).to(yesterday.plusWeeks(4)).build());
        holidays.add((HolidayBO) new HolidayBuilder().withName("First").from(yesterday).to(yesterday.plusWeeks(2)).build());
        myOffice.setHolidays(holidays);
        myOffice.save();

        OfficeBO anotherOffice = new OfficeBuilder().withParentOffice(headOffice).build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().from(yesterday.minusWeeks(3)).to(yesterday.plusWeeks(4)).build());
        anotherOffice.save();

        List<Holiday> myHolidays = holidayDao.findCurrentAndFutureOfficeHolidaysEarliestFirst(myOffice.getOfficeId());

        assertThat(myHolidays.size(), is(2));
        assertThat(myHolidays.get(0).getName(), is("First"));

    }

    private void insert(final Holiday holiday) throws ApplicationException {
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", holiday.getFromDate().toDate(), holiday
                .getThruDate().toDate(), holiday.getRepaymentRuleType());
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short) 1);
        DependencyInjectedServiceLocator.locateHolidayServiceFacade().createHoliday(holidayDetails, officeIds);
    }
}