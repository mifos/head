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

package org.mifos.application.holiday.business;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.joda.time.DateMidnight;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.persistence.HolidayServiceFacadeWebTier;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class HolidayBOIntegrationTest extends MifosIntegrationTestCase {

    public HolidayBOIntegrationTest() throws Exception {
        super();
    }

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

    /**
     * test Holiday From Date Validations Failure.
     */
    public void testHolidayFromDateValidationFailure(){
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", new Date(), null, RepaymentRuleTypes.fromInt(1));
        try {
            createHolidayForHeadOffice(holidayDetails);
            Assert.fail("Did not raise ServiceException");
        } catch (ServiceException e) {
        }
    }

    public void testHolidayRepaymentRuleTypeEntity() throws ServiceException, PersistenceException {
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DAY_OF_MONTH, 1);
        Date holidayStartDate = startDate.getTime();
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", holidayStartDate, null,
                RepaymentRuleTypes.SAME_DAY);
        createHolidayForHeadOffice(holidayDetails);
        List<HolidayBO> holidays = new HolidayPersistence().getHolidays(holidayStartDate.getYear()+1900);
        assertEquals(1, holidays.size());
        assertNotNull(holidays.get(0).getRepaymentRuleType().getPropertiesKey());
    }

    /**
     * test Holiday From Date Validations Success.
     */
    public void testHolidayFromDateValidationSuccess() throws Exception {
        long fromDateMillis = new DateMidnight().getMillis();
        Calendar fromDate = Calendar.getInstance();
        fromDate.setTimeInMillis(fromDateMillis);
        fromDate.add(Calendar.DAY_OF_MONTH, 1);
        fromDateMillis = fromDate.getTimeInMillis();
        Date startDate = new Date(fromDateMillis);

        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", startDate, null, RepaymentRuleTypes
                .fromInt(1));
        createHolidayForHeadOffice(holidayDetails);

        List<HolidayBO> holidays = new HolidayPersistence().getHolidays(startDate.getYear()+1900);
        Assert.assertEquals(1, holidays.size());

        HolidayBO holiday = holidays.get(0);
        Assert.assertEquals("Test Holiday", holiday.getHolidayName());
        Assert.assertEquals(fromDateMillis, holiday.getHolidayFromDate().getTime());
        // automatically Fromdate is copied into thruDate if thrudate is null.
        Assert.assertEquals(fromDateMillis, holiday.getHolidayThruDate().getTime());
    }

    /**
     * test Holiday From Date Against Thru Date Failure.
     */
    public void testHolidayFromDateAgainstThruDateFailure(){
        Calendar thruDate = Calendar.getInstance();
        thruDate.add(Calendar.DAY_OF_MONTH, -1);
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", Calendar.getInstance().getTime(), thruDate.getTime(), RepaymentRuleTypes
                .fromInt(1));
        try {
            createHolidayForHeadOffice(holidayDetails);
            Assert.fail("Did not raise ServiceException");
        } catch (ServiceException e) {
        }
    }

    /**
     * test Holiday From Date Against Thru Date Success.
     */
    public void testHolidayFromDateAgainstThruDateSucces() throws Exception {
        long fromDateMillis = new DateMidnight().getMillis();
        Calendar fromDate = Calendar.getInstance();
        fromDate.setTimeInMillis(fromDateMillis);
        fromDate.add(Calendar.DAY_OF_MONTH, 1);
        fromDateMillis = fromDate.getTimeInMillis();

        Calendar thruDate = Calendar.getInstance();
        long thruDateMillis = new DateMidnight().getMillis();
        thruDate.setTimeInMillis(thruDateMillis);
        thruDate.add(Calendar.DAY_OF_MONTH, 1);
        thruDateMillis = thruDate.getTimeInMillis();

        Date startDate = new Date(fromDateMillis);
        HolidayDetails holidayDetails = new HolidayDetails("Test Holiday", startDate, thruDate.getTime(),
                RepaymentRuleTypes.fromInt(1));
        createHolidayForHeadOffice(holidayDetails);

        List<HolidayBO> holidays = new HolidayPersistence().getHolidays(startDate.getYear()+1900);
        Assert.assertEquals(1, holidays.size());

        HolidayBO holiday = holidays.get(0);
        Assert.assertEquals("Test Holiday", holiday.getHolidayName());
        Assert.assertEquals(fromDateMillis, holiday.getHolidayFromDate().getTime());
        Assert.assertEquals(thruDateMillis, holiday.getHolidayThruDate().getTime());
    }

    /*
     * Do we need an update script like UPDATE HOLIDAY SET HOLIDAY_THRU_DATE =
     * HOLIDAY_FROM_DATE WHERE HOLIDAY_THRU_DATE is NULL; ? Doing this for save
     * and update will fix it for future cases.
     */
    public void testSaveSuppliesThruDate() throws Exception {
        long startDate = new DateMidnight(2003, 1, 26).getMillis();
        Date fromDate = new Date(startDate);
        HolidayDetails holidayDetails = new HolidayDetails("Test Day", fromDate, null,
                RepaymentRuleTypes.fromInt(1));
        holidayDetails.disableValidation(true);
        createHolidayForHeadOffice(holidayDetails);
        List<HolidayBO> holidays = new HolidayPersistence().getHolidays(fromDate.getYear()+1900);
        Assert.assertEquals(1, holidays.size());
        Assert.assertEquals(startDate, holidays.get(0).getHolidayThruDate().getTime());
    }
}
