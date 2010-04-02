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
import java.util.Date;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.business.HolidayPK;
import org.mifos.application.holiday.business.RepaymentRuleEntity;
import org.mifos.application.holiday.persistence.HolidayPersistence;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.TestCollectionSheetRetrieveSavingsAccountsUtils;
import org.mifos.application.servicefacade.TestSaveCollectionSheetUtils;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ApplyHolidayChangesHelperIntegrationTest extends MifosIntegrationTestCase {
    public ApplyHolidayChangesHelperIntegrationTest() throws Exception {
        super();
    }

    private List<HolidayBO> holidays = null;
    private CustomerBO center;
    private HolidayBO holidayEntity;
    private ApplyHolidayChangesHelper applyHolidayChangesHelper;
    private DateTimeService dateTimeService = new DateTimeService();

    // john w - Collection Sheet Util Classes just used to create center hierarchies
    private TestSaveCollectionSheetUtils testSaveCollectionSheetUtils;
    private TestCollectionSheetRetrieveSavingsAccountsUtils testCollectionSheetRetrieveSavingsAccountsUtils;
    private SavingsBO newCenterSavingsAccount;
    private SavingsBO newGroupSavingsAccount;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService.setCurrentDateTime(new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.FEBRUARY)
                .withDayOfMonth(23));
        ApplyHolidayChangesTask applyHolidayChangesTask = new ApplyHolidayChangesTask();
        applyHolidayChangesHelper = (ApplyHolidayChangesHelper) applyHolidayChangesTask.getTaskHelper();

        testSaveCollectionSheetUtils = new TestSaveCollectionSheetUtils();
        testCollectionSheetRetrieveSavingsAccountsUtils = new TestCollectionSheetRetrieveSavingsAccountsUtils();

    }

    @Override
    public void tearDown() throws Exception {
        holidays = new HolidayPersistence().getUnAppliedHolidays();
        Assert.assertEquals(0, holidays.size());
        TestObjectFactory.cleanUp(center);
        TestObjectFactory.deleteHoliday(holidayEntity);
        applyHolidayChangesHelper = null;
        holidayEntity = null;

        // John W - clean up data related to newer tests
        if (newCenterSavingsAccount != null) {
            newCenterSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                    newCenterSavingsAccount.getAccountId());
        }
        if (newGroupSavingsAccount != null) {
            newGroupSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                    newGroupSavingsAccount.getAccountId());
        }
        TestObjectFactory.cleanUp(newCenterSavingsAccount);
        TestObjectFactory.cleanUp(newGroupSavingsAccount);
        testSaveCollectionSheetUtils.clearObjects();

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

    public void testRecurringFeeScheduleIsAdjustedForNewHoliday() throws Exception {
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
        holidayEntity = new HolidayBO(holidayPK, null, "Test Holiday", entity);
        // Disable date Validation because startDate is less than today
        holidayEntity.setValidationEnabled(false);
        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Set<AccountActionDateEntity> accountActionDates = center.getCustomerAccount().getAccountActionDates();
        Assert.assertEquals("Customer schedule unadjusted", LocalDate.fromDateFields(holidayStartDate), LocalDate
                .fromDateFields(accountActionDates.toArray(new AccountActionDateEntity[] {})[1].getActionDate()));

        // run the batch job
        StaticHibernateUtil.startTransaction();
        new ApplyHolidayChangesTask().getTaskHelper().execute(dateTimeService.getCurrentJavaDateTime().getTime());
        // commit done in ApplyHolidayChangesHelper

        // make sure schedule changed
        center = TestObjectFactory.getCustomer(center.getCustomerId());
        accountActionDates = center.getCustomerAccount().getAccountActionDates();
        final Date displacedPaybackDate = new DateTime(holidayStartDate.getTime()).withDayOfMonth(9).toDate();
        Assert.assertEquals("customer schedule adjusted", LocalDate.fromDateFields(displacedPaybackDate), LocalDate
                .fromDateFields(accountActionDates.toArray(new AccountActionDateEntity[] {})[1].getActionDate()));
    }

    public void testThatAllTypesofSchedulesAreUpdatedGivenALengthyHoliday() throws Exception {

        // create center hierarchy with loan and savings and customer accounts.
        final Date today = dateTimeService.getCurrentJavaDateTime();
        createCenterHierarchy(today);

        // Creating Holiday
        final LocalDate holidayFromDate = new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.MARCH)
                .withDayOfMonth(9).toLocalDate();
        final LocalDate holidayThruDate = new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.MARCH)
                .withDayOfMonth(28).toLocalDate();
        final HolidayPK holidayPK = new HolidayPK((short) 1, DateUtils.getDateFromLocalDate(holidayFromDate));
        final RepaymentRuleEntity entity = new HolidayPersistence()
                .getRepaymentRule(RepaymentRuleTypes.NEXT_WORKING_DAY.getValue());
        holidayEntity = new HolidayBO(holidayPK, DateUtils.getDateFromLocalDate(holidayThruDate), "Brand New Holiday",
                entity);
        holidayEntity.save();
        StaticHibernateUtil.commitTransaction();

        // Generate expected results
        final LocalDate adjustedLocalDate = new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.MARCH)
                .withDayOfMonth(29).toLocalDate();
        final java.sql.Date adjustedDate = new java.sql.Date(DateUtils.getDateFromLocalDate(adjustedLocalDate)
                .getTime());
        List<java.sql.Date> resultDates = createResultDates(new java.sql.Date(today.getTime()), adjustedDate);

        // run the batch job
        StaticHibernateUtil.startTransaction();
        applyHolidayChangesHelper.execute(today.getTime());
        System.out.println("finished");

        // verify results by refreshing data and comparing with expected results
        CustomerAccountBO refreshedCenterCustomerAccount = (CustomerAccountBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                testSaveCollectionSheetUtils.getCenter().getCustomerAccount().getAccountId());
        verifyAccountActionDates(refreshedCenterCustomerAccount.getAccountActionDates(), resultDates);

        newGroupSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                newGroupSavingsAccount.getAccountId());
        verifyAccountActionDates(newGroupSavingsAccount.getAccountActionDates(), resultDates);

        LoanBO refreshedClientLoanAccount = (LoanBO) StaticHibernateUtil.getSessionTL().get(AccountBO.class,
                testSaveCollectionSheetUtils.getClientLoan().getAccountId());
        //loan schedules start a week later so taking the first 'week' out of resultDates
        resultDates.remove(0);
        verifyAccountActionDates(refreshedClientLoanAccount.getAccountActionDates(), resultDates);

    }

    private void verifyAccountActionDates(Set<AccountActionDateEntity> accountActionDates,
            List<java.sql.Date> resultDates) {

        for (AccountActionDateEntity date : accountActionDates) {
            java.sql.Date expectedDate = resultDates.get(date.getInstallmentId() - 1);
            assertEquals(expectedDate.toString(), date.getActionDate().toString());
        }

    }

    private List<java.sql.Date> createResultDates(java.sql.Date startDate, java.sql.Date adjustedDate) {

        //assuming that 50 is more than enough dates to cater for (to compare against scheduled dates)
        List<java.sql.Date> dates = new ArrayList<java.sql.Date>(50);
        dates.add(startDate);
        for (Integer i = 0; i < 49; i++) {
            LocalDate nextLocalDate = DateUtils.getLocalDateFromDate(dates.get(i)).plusDays(7);
            java.sql.Date nextDate = new java.sql.Date(DateUtils.getDateFromLocalDate(nextLocalDate).getTime());
            dates.add(nextDate);
        }
        // Adjust Dates for holiday as per expected
        dates.set(2, adjustedDate);
        dates.set(3, adjustedDate);
        dates.set(4, adjustedDate);
        return dates;
    }

    private void createCenterHierarchy(Date today) throws Exception {
        // John W - apologies for ugliness of reusing Collection Sheet test utility class here.
        testSaveCollectionSheetUtils.createSampleCenterHierarchy(today);
        newCenterSavingsAccount = testCollectionSheetRetrieveSavingsAccountsUtils.createSavingsAccount(
                testSaveCollectionSheetUtils.getCenter(), "cvi", "6.6", true, true);
        newGroupSavingsAccount = testCollectionSheetRetrieveSavingsAccountsUtils.createSavingsAccount(
                testSaveCollectionSheetUtils.getGroup(), "gm", "2.5", false, false);
    }

}
