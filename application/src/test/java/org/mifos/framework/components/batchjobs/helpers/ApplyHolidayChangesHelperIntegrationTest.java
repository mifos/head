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

package org.mifos.framework.components.batchjobs.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ApplyHolidayChangesHelperIntegrationTest extends MifosIntegrationTestCase {
    public ApplyHolidayChangesHelperIntegrationTest() throws Exception {
        super();
    }

    private List<HolidayBO> holidays = null;
    private CustomerBO center;
    private HolidayBO holidayEntity;
    private ApplyHolidayChangesHelper applyHolidayChangesHelper;
    DateTimeService dateTimeService = new DateTimeService();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService.setCurrentDateTime(new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.FEBRUARY)
                .withDayOfMonth(23));
        ApplyHolidayChangesTask applyHolidayChangesTask = new ApplyHolidayChangesTask();
        applyHolidayChangesHelper = (ApplyHolidayChangesHelper) applyHolidayChangesTask.getTaskHelper();
    }

    @Override
    public void tearDown() throws Exception {
        holidays = new HolidayPersistence().getUnAppliedHolidays();
        Assert.assertEquals(0, holidays.size());
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.cleanUpHolidays(holidays);
        applyHolidayChangesHelper = null;
        holidayEntity = null;
        StaticHibernateUtil.closeSession();
        dateTimeService.resetToCurrentSystemDateTime();
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
    }

    public void testRecurringFeeScheduleIsAdjusted() throws Exception {
        // create center (includes recurring fee)
        StaticHibernateUtil.startTransaction();
        final List<FeeView> feeView = new ArrayList<FeeView>();
        final MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createCenter("Center_Active_test", meeting, feeView);
        center.update();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        // create new holiday
        StaticHibernateUtil.startTransaction();
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        Assert.assertEquals(10, center.getCustomerAccount().getAccountActionDates().size());
        long fromDateMillis = new DateMidnight().getMillis();
        final Date holidayStartDate = new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.MARCH)
                .withDayOfMonth(8).toDate();
        final HolidayPK holidayPK = new HolidayPK((short) 1, holidayStartDate);
        final RepaymentRuleEntity entity = new HolidayPersistence()
                .getRepaymentRule(RepaymentRuleTypes.NEXT_WORKING_DAY.getValue());
        final Date holidayEndDate = new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.MARCH)
                .withDayOfMonth(8).toDate();
        holidayEntity = new HolidayBO(holidayPK, holidayEndDate, "Test Holiday", entity);
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);
        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Set<AccountActionDateEntity> accountActionDates = center.getCustomerAccount().getAccountActionDates();
        Assert.assertEquals("Customer schedule unadjusted",
                DateUtils.truncate(holidayStartDate, Calendar.DAY_OF_MONTH), DateUtils.truncate(accountActionDates
                        .toArray(new AccountActionDateEntity[] {})[1].getActionDate(), Calendar.DAY_OF_MONTH));

        // run the batch job
        StaticHibernateUtil.startTransaction();
        new ApplyHolidayChangesTask().getTaskHelper().execute(dateTimeService.getCurrentJavaDateTime().getTime());
        // commit done in ApplyHolidayChangesHelper

        // make sure schedule changed
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        accountActionDates = center.getCustomerAccount().getAccountActionDates();
        final Date displacedPaybackDate = new DateTime(holidayEndDate.getTime()).withDayOfMonth(9).toDate();
        Assert
                .assertEquals("customer schedule adjusted", DateUtils.truncate(displacedPaybackDate,
                        Calendar.DAY_OF_MONTH), DateUtils.truncate(accountActionDates
                        .toArray(new AccountActionDateEntity[] {})[1].getActionDate(), Calendar.DAY_OF_MONTH));
    }
}
