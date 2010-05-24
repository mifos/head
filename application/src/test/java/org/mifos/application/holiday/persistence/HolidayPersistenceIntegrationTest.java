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

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HolidayPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public HolidayPersistenceIntegrationTest() throws Exception {
        super();
    }

    private HolidayBO holidayEntity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        rollback();
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    private void rollback() {
        Transaction transaction = StaticHibernateUtil.getSessionTL().getTransaction();
        if(transaction.isActive()){
            transaction.rollback();
        }
    }

    private void createHolidayForHeadOffice(HolidayDetails holidayDetails) throws ServiceException {
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short) 1);
        new HolidayServiceFacadeWebTier(new OfficePersistence()).createHoliday(holidayDetails, officeIds);
    }

    public void testGetHolidays() throws Exception {
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", new Date(), null,
                RepaymentRuleTypes.SAME_DAY);
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);

        List<HolidayBO> holidays = new HolidayPersistence().getHolidays(Calendar.getInstance().get(Calendar.YEAR));
        Assert.assertNotNull(holidays);
        Assert.assertEquals(1, holidays.size());

        rollback();

        holidays = new HolidayPersistence().getHolidays(Calendar.getInstance().get(Calendar.YEAR) - 1);
        Assert.assertNotNull(holidays);
        Assert.assertEquals(holidays.size(), 0);
    }

    public void testGetUnAppliedHolidaysAgainstAppliedOnes() throws Exception {
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", new Date(), null,
                RepaymentRuleTypes.SAME_DAY, YesNoFlag.YES);
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);

        List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();

        // There should not be any UnappliedHolidays
        Assert.assertNotNull(holidays);
        Assert.assertEquals(holidays.size(), 0);
    }

    public void testGetUnAppliedHolidaysAgainst_Un_AppliedOnes() throws Exception {
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", new Date(), null,
                RepaymentRuleTypes.SAME_DAY);
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);

        List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();
        // There should be exactly one UnappliedHoliday
        Assert.assertNotNull(holidays);
        Assert.assertEquals(1, holidays.size());
    }

    public void testGetDistinctYears() throws Exception {
        List<HolidayBO> distinctYears = new HolidayPersistence().getDistinctYears();
        Assert.assertNotNull(distinctYears);
    }

}
