/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class HolidayPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public HolidayPersistenceIntegrationTest() throws SystemException, ApplicationException {
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

    public void testGetHolidays() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short) 1, "RepaymentRule-SameDay");
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", repaymentRuleEntity);
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        List<HolidayBO> holidays = new HolidayPersistence().getHolidays(Calendar.getInstance().get(Calendar.YEAR));
        Assert.assertNotNull(holidays);
       Assert.assertEquals(1, holidays.size());

        TestObjectFactory.cleanUpHolidays(holidays);
        holidayEntity = null;

        holidays = new HolidayPersistence().getHolidays(Calendar.getInstance().get(Calendar.YEAR) - 1);
        Assert.assertNotNull(holidays);
       Assert.assertEquals(holidays.size(), 0);
    }

    public void testGetRepaymentRuleTypes() throws Exception {
        List<RepaymentRuleEntity> repaymentRules = new HolidayPersistence().getRepaymentRuleTypes();
        Assert.assertNotNull(repaymentRules);
       Assert.assertEquals(3, repaymentRules.size());
    }

    public void testGetUnAppliedHolidaysAgainstAppliedOnes() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short) 1, "RepaymentRule-SameDay");
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", repaymentRuleEntity);
        holidayEntity.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();

        // There should not be any UnappliedHolidays
        Assert.assertNotNull(holidays);
       Assert.assertEquals(holidays.size(), 0);

        TestObjectFactory.cleanUpHolidays(holidays);
        holidayEntity = null;
    }

    public void testGetUnAppliedHolidaysAgainst_Un_AppliedOnes() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity repaymentRuleEntity = new RepaymentRuleEntity((short) 1, "RepaymentRule-SameDay");
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", repaymentRuleEntity);
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();

        // There should be exactly one UnappliedHoliday
        Assert.assertNotNull(holidays);
       Assert.assertEquals(1, holidays.size());

        TestObjectFactory.cleanUpHolidays(holidays);
        holidayEntity = null;
    }

    public void testGetDistinctYears() throws Exception {
        List<HolidayBO> distinctYears = new HolidayPersistence().getDistinctYears();
        Assert.assertNotNull(distinctYears);
    }

}
