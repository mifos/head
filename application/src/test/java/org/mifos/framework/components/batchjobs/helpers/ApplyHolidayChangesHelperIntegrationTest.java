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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ApplyHolidayChangesHelperIntegrationTest extends MifosIntegrationTestCase {
    public ApplyHolidayChangesHelperIntegrationTest() throws Exception {
        super();
    }

    private HolidayBO holidayEntity;
    private ApplyHolidayChangesHelper applyHolidayChangesHelper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ApplyHolidayChangesTask applyHolidayChangesTask = new ApplyHolidayChangesTask();
        applyHolidayChangesHelper = (ApplyHolidayChangesHelper) applyHolidayChangesTask.getTaskHelper();
    }

    @Override
    public void tearDown() throws Exception {
        applyHolidayChangesHelper = null;
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testExecuteAgainstAppliedHolidays() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
        holidayEntity.setHolidayChangesAppliedFlag(YesNoFlag.YES.getValue());
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // //////Meat&Potato//////////
        applyHolidayChangesHelper.execute(System.currentTimeMillis());
        StaticHibernateUtil.closeSession();
        // ////////////////

        List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();

        // There should not be any UnappliedHolidays
        Assert.assertNotNull(holidays);
       Assert.assertEquals(holidays.size(), 0);

        TestObjectFactory.cleanUpHolidays(holidays);
        holidayEntity = null;
    }

    public void testExecuteAgainst_Un_AppliedHolidays() throws Exception {
        HolidayPK holidayPK = new HolidayPK((short) 1, new Date());
        RepaymentRuleEntity entity = new HolidayPersistence().getRepaymentRule((short) 1);
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);

        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // //////Meat&Potato//////////
        applyHolidayChangesHelper.execute(System.currentTimeMillis());
        StaticHibernateUtil.closeSession();
        // ////////////////

        List<HolidayBO> holidays = new HolidayPersistence().getUnAppliedHolidays();

        // There should not be any UnappliedHolidays
        Assert.assertNotNull(holidays);
       Assert.assertEquals(holidays.size(), 0);

        TestObjectFactory.cleanUpHolidays(holidays);
        holidayEntity = null;
    }
}
