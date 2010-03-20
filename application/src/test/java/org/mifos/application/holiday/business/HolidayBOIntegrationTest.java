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

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HolidayBOIntegrationTest extends MifosIntegrationTestCase {

    public HolidayBOIntegrationTest() throws Exception {
        super();
    }

    private HolidayBO holidayEntity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(holidayEntity);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testAddHoliday() throws Exception {
        long fromDateMillis = new DateMidnight().getMillis();
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date(fromDateMillis));
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        holidayEntity = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidayEntity.getHolidayPK());

       Assert.assertEquals("Test Holiday", holidayEntity.getHolidayName());
       Assert.assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate().getTime());
       Assert.assertEquals(fromDateMillis, holidayEntity.getHolidayThruDate().getTime());
    }

    /**
     * test Holiday From Date Validations Failure.
     */
    public void testHolidayFromDateValidationFailure() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);

        try {
            holidayEntity.save();
            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();
            // If it succedded to create this holiday
            // Asssert false as it shouldn't by validations.
           Assert.assertTrue(false);
        } catch (ApplicationException e) {
            holidayEntity = null;
        }
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

        HolidayPK holidayPK = new HolidayPK((short) 1, new Date(fromDateMillis));
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        holidayEntity = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidayEntity.getHolidayPK());

       Assert.assertEquals("Test Holiday", holidayEntity.getHolidayName());
       Assert.assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate().getTime());
        // automatically Fromdate is copied into thruDate if thrudate is null.
       Assert.assertEquals(fromDateMillis, holidayEntity.getHolidayThruDate().getTime());
    }

    /**
     * test Holiday From Date Against Thru Date Failure.
     */
    public void testHolidayFromDateAgainstThruDateFailure() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, Calendar.getInstance().getTime());
        Calendar thruDate = Calendar.getInstance();
        thruDate.add(Calendar.DAY_OF_MONTH, -1);
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, thruDate.getTime(), "Test Holiday", entity);

        try {
            holidayEntity.save();

            StaticHibernateUtil.commitTransaction();
            StaticHibernateUtil.closeSession();

            // If it succedded to create this holiday
            // Asssert false as it shouldn't by validation rules.
           Assert.assertTrue(false);
        } catch (ApplicationException e) {
            holidayEntity = null;
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

        HolidayPK holidayPK = new HolidayPK((short) 1, new Date(fromDateMillis));

        Calendar thruDate = Calendar.getInstance();
        long thruDateMillis = new DateMidnight().getMillis();
        thruDate.setTimeInMillis(thruDateMillis);
        thruDate.add(Calendar.DAY_OF_MONTH, 1);
        thruDateMillis = thruDate.getTimeInMillis();
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, thruDate.getTime(), "Test Holiday", entity);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        holidayEntity = (HolidayBO) TestObjectFactory.getObject(HolidayBO.class, holidayEntity.getHolidayPK());

       Assert.assertEquals("Test Holiday", holidayEntity.getHolidayName());
       Assert.assertEquals(fromDateMillis, holidayEntity.getHolidayFromDate().getTime());
       Assert.assertEquals(thruDateMillis, holidayEntity.getHolidayThruDate().getTime());
    }

    /*
     * Do we need an update script like UPDATE HOLIDAY SET HOLIDAY_THRU_DATE =
     * HOLIDAY_FROM_DATE WHERE HOLIDAY_THRU_DATE is NULL; ? Doing this for save
     * and update will fix it for future cases.
     */
    public void testSaveSuppliesThruDate() throws Exception {
        long startDate = new DateMidnight(2003, 1, 26).getMillis();
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        HolidayBO holiday = new HolidayBO(new HolidayPK((short) 1, new Date(startDate)), null, "Test Day", entity);

        // Disable date Validation because startDate is less than today
        holiday.setValidationEnabled(false);

        holiday.save();
       Assert.assertEquals(startDate, holiday.getHolidayThruDate().getTime());
    }

    public void testUpdateSuppliesThruDate() throws Exception {
        long startDate = new DateMidnight(2003, 1, 26).getMillis();
        RepaymentRuleEntity entity = new RepaymentRuleEntity((short) 1, "RepaymentRule-SameDay");
        HolidayBO holiday = new HolidayBO(new HolidayPK((short) 1, new Date(startDate)), null, "Test Day", entity);

        // Disable date Validation because startDate is less than today
        holiday.setValidationEnabled(false);

        holiday.update(holiday.getHolidayPK(), null, "New Name");
       Assert.assertEquals(startDate, holiday.getHolidayThruDate().getTime());
    }
}
