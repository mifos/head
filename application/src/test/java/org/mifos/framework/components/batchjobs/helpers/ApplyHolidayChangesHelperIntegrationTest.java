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

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.servicefacade.TestCollectionSheetRetrieveSavingsAccountsUtils;
import org.mifos.application.servicefacade.TestSaveCollectionSheetUtils;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApplyHolidayChangesHelperIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private HolidayDao holidayDao;

    private static MifosCurrency oldDefaultCurrency;

    private ApplyHolidayChangesHelper applyHolidayChangesHelper;

    private DateTimeService dateTimeService = new DateTimeService();

    // john w - Collection Sheet Util Classes just used to create center hierarchies
    private TestSaveCollectionSheetUtils testSaveCollectionSheetUtils;

    private TestCollectionSheetRetrieveSavingsAccountsUtils testCollectionSheetRetrieveSavingsAccountsUtils;

    private SavingsBO newCenterSavingsAccount;

    private SavingsBO newGroupSavingsAccount;

    @BeforeClass
    public static void initialiseHibernateUtilAndSetDefaultCurrency() {
        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
    }

    @AfterClass
    public static void resetDefaultCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAndResetCurrentDateTimeAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
        dateTimeService.resetToCurrentSystemDateTime();
        StaticHibernateUtil.rollbackTransaction();
        StaticHibernateUtil.flushAndClearSession();
    }

    @Before
    public void cleanDatabaseTablesAndSetupCustomerTestDataBeforeTest() throws Exception {
        databaseCleaner.clean();
        dateTimeService.setCurrentDateTime(new DateTime().withYear(2010).withMonthOfYear(DateTimeConstants.FEBRUARY)
                .withDayOfMonth(23));
        ApplyHolidayChangesTask applyHolidayChangesTask = new ApplyHolidayChangesTask();
        applyHolidayChangesHelper = (ApplyHolidayChangesHelper) applyHolidayChangesTask.getTaskHelper();

        testSaveCollectionSheetUtils = new TestSaveCollectionSheetUtils();
        testCollectionSheetRetrieveSavingsAccountsUtils = new TestCollectionSheetRetrieveSavingsAccountsUtils();

        // create center hierarchy with loan and savings and customer accounts.
        createCenterHierarchy(dateTimeService.getCurrentJavaDateTime());
        StaticHibernateUtil.flushAndClearSession();
    }

    @Test
    public void testThatNoSchedulesAreUpdatedWhenHolidaysDontAffectThem() throws Exception {

        DateTime nextYear = new DateTime().plusYears(1);
        createOfficeHolidayTestData(nextYear);

        //expected results
        LocalDate[] expectedDateResultsCustomerAndSavings = noChangeExpectedForCustomerAndSavings();
        LocalDate[] expectedDateResultsLoan = noChangeExpectedForLoan();

        // run the batch job
        StaticHibernateUtil.startTransaction();
        applyHolidayChangesHelper.execute(dateTimeService.getCurrentJavaDateTime().getTime());

        //
        verify_results(expectedDateResultsCustomerAndSavings, expectedDateResultsLoan);
    }

    @Test @Ignore("Convert to unit test")
    public void testThatAllTypesofSchedulesAreUpdatedGivenAMixOfUnappliedHolidays() throws Exception {
        DateTime yesterday = new DateTime().minusDays(1);
        createOfficeHolidayTestData(yesterday);

        //expected results
        LocalDate[] expectedDateResultsCustomerAndSavings = changeExpectedForCustomerAndSavings();
        LocalDate[] expectedDateResultsLoan = changeExpectedForLoan();

        // run the batch job
        StaticHibernateUtil.startTransaction();
        applyHolidayChangesHelper.execute(dateTimeService.getCurrentJavaDateTime().getTime());

        //
        verify_results(expectedDateResultsCustomerAndSavings, expectedDateResultsLoan);

    }

    @Test
    public void testThatTaskRunsSuccessfullyWhenNoHolidaysNeedApplying() throws Exception {

        List<Holiday> unappliedHolidays = holidayDao.getUnAppliedHolidays();
        assertThat(unappliedHolidays.size(), is(0));

        //expected results
        LocalDate[] expectedDateResultsCustomerAndSavings = noChangeExpectedForCustomerAndSavings();
        LocalDate[] expectedDateResultsLoan = noChangeExpectedForLoan();

        // run the batch job
        StaticHibernateUtil.startTransaction();
        applyHolidayChangesHelper.execute(dateTimeService.getCurrentJavaDateTime().getTime());

        //
        verify_results(expectedDateResultsCustomerAndSavings, expectedDateResultsLoan);
    }

    private void verify_results(LocalDate[] expectedDateResultsCustomerAndSavings, LocalDate[] expectedDateResultsLoan) {
        // verify results by refreshing data and comparing with expected results
        getCustomerAccountAndVerifyDates(testSaveCollectionSheetUtils.getCenter(),
                expectedDateResultsCustomerAndSavings);
        getCustomerAccountAndVerifyDates(testSaveCollectionSheetUtils.getGroup(), expectedDateResultsCustomerAndSavings);
        getCustomerAccountAndVerifyDates(testSaveCollectionSheetUtils.getClientLoan().getCustomer(),
                expectedDateResultsCustomerAndSavings);

        getSavingsAccountAndVerifyDates(newCenterSavingsAccount, expectedDateResultsCustomerAndSavings);
        getSavingsAccountAndVerifyDates(newGroupSavingsAccount, expectedDateResultsCustomerAndSavings);

        getLoanAccountAndVerifyDates(testSaveCollectionSheetUtils.getClientLoan(), expectedDateResultsLoan);

    }

    private void getCustomerAccountAndVerifyDates(CustomerBO customer, LocalDate[] expectedResultDates) {

        CustomerAccountBO refreshedCustomerAccount = (CustomerAccountBO) StaticHibernateUtil.getSessionTL().get(
                CustomerAccountBO.class, customer.getCustomerAccount().getAccountId());
        verifyAccountActionDates(refreshedCustomerAccount.getAccountActionDates(), expectedResultDates);
    }

    private void getSavingsAccountAndVerifyDates(SavingsBO savings, LocalDate[] expectedResultDates) {

        SavingsBO refreshedSavingsAccount = (SavingsBO) StaticHibernateUtil.getSessionTL().get(SavingsBO.class,
                savings.getAccountId());
        verifyAccountActionDates(refreshedSavingsAccount.getAccountActionDates(), expectedResultDates);
    }

    private void getLoanAccountAndVerifyDates(LoanBO loan, LocalDate[] expectedResultDates) {

        LoanBO refreshedLoanAccount = (LoanBO) StaticHibernateUtil.getSessionTL()
                .get(LoanBO.class, loan.getAccountId());
        verifyAccountActionDates(refreshedLoanAccount.getAccountActionDates(), expectedResultDates);
    }

    private void verifyAccountActionDates(Set<AccountActionDateEntity> accountActionDates,
            LocalDate[] expectedResultDates) {

        int i = 0;
        for (AccountActionDateEntity date : accountActionDates) {
            assertThat(expectedResultDates[i].toString(), is(date.getActionDate().toString()));
            i++;
        }

    }

    private LocalDate[] noChangeExpectedForCustomerAndSavings() {

        LocalDate[] expectedDates = new LocalDate[10];
        expectedDates[0] = new LocalDate(2010, 2, 23);
        expectedDates[1] = new LocalDate(2010, 3, 2);
        expectedDates[2] = new LocalDate(2010, 3, 9);
        expectedDates[3] = new LocalDate(2010, 3, 16);
        expectedDates[4] = new LocalDate(2010, 3, 23);
        expectedDates[5] = new LocalDate(2010, 3, 30);
        expectedDates[6] = new LocalDate(2010, 4, 6);
        expectedDates[7] = new LocalDate(2010, 4, 13);
        expectedDates[8] = new LocalDate(2010, 4, 20);
        expectedDates[9] = new LocalDate(2010, 4, 27);
        return expectedDates;
    }

    private LocalDate[] noChangeExpectedForLoan() {

        LocalDate[] expectedDates = new LocalDate[12];
        expectedDates[0] = new LocalDate(2010, 3, 2);
        expectedDates[1] = new LocalDate(2010, 3, 9);
        expectedDates[2] = new LocalDate(2010, 3, 16);
        expectedDates[3] = new LocalDate(2010, 3, 23);
        expectedDates[4] = new LocalDate(2010, 3, 30);
        expectedDates[5] = new LocalDate(2010, 4, 6);
        expectedDates[6] = new LocalDate(2010, 4, 13);
        expectedDates[7] = new LocalDate(2010, 4, 20);
        expectedDates[8] = new LocalDate(2010, 4, 27);
        expectedDates[9] = new LocalDate(2010, 5, 4);
        expectedDates[10] = new LocalDate(2010, 5, 11);
        expectedDates[11] = new LocalDate(2010, 5, 18);
        return expectedDates;
    }

    private LocalDate[] changeExpectedForCustomerAndSavings() {

        LocalDate[] expectedDates = new LocalDate[10];
        expectedDates[0] = new LocalDate(2010, 3, 2);
        expectedDates[1] = new LocalDate(2010, 3, 2);
        expectedDates[2] = new LocalDate(2010, 3, 23);
        expectedDates[3] = new LocalDate(2010, 3, 30);
        expectedDates[4] = new LocalDate(2010, 4, 6);
        expectedDates[5] = new LocalDate(2010, 4, 13);
        expectedDates[6] = new LocalDate(2010, 4, 20);
        expectedDates[7] = new LocalDate(2010, 4, 27);
        expectedDates[8] = new LocalDate(2010, 5, 4);
        expectedDates[9] = new LocalDate(2010, 5, 11);
        return expectedDates;
    }

    private LocalDate[] changeExpectedForLoan() {

        LocalDate[] expectedDates = new LocalDate[12];
        expectedDates[0] = new LocalDate(2010, 3, 2);
        expectedDates[1] = new LocalDate(2010, 3, 23);
        expectedDates[2] = new LocalDate(2010, 3, 30);
        expectedDates[3] = new LocalDate(2010, 4, 6);
        expectedDates[4] = new LocalDate(2010, 4, 13);
        expectedDates[5] = new LocalDate(2010, 4, 20);
        expectedDates[6] = new LocalDate(2010, 4, 27);
        expectedDates[7] = new LocalDate(2010, 5, 4);
        expectedDates[8] = new LocalDate(2010, 5, 11);
        expectedDates[9] = new LocalDate(2010, 5, 18);
        expectedDates[10] = new LocalDate(2010, 5, 25);
        expectedDates[11] = new LocalDate(2010, 6, 1);
        return expectedDates;
    }

    private void createOfficeHolidayTestData(DateTime startDate) throws Exception {

        /*
         * When startDate is 'yesterday' Head Office Holiday: from 2010-02-22 thru 2010-03-01 ... repayment rule is next
         * meeting date Branch Holiday:
         *
         * from 2010-03-08 thru 2010-03-22 ... moratorium
         *
         * One more holiday that should not affect any schedules
         */

        // Creating Office Holidays
        Set<HolidayBO> holidays;
        OfficeBO headOffice = IntegrationTestObjectMother.findOfficeById(Short.valueOf("1"));
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("HO Holiday").from(startDate).to(startDate.plusWeeks(1))
                .withRepaymentRule(RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT).build());
        headOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(headOffice);

        OfficeBO centerOffice = testSaveCollectionSheetUtils.getCenter().getOffice();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("Center Hierarchy Holiday").from(startDate.plusWeeks(2))
                .to(startDate.plusWeeks(4)).withRepaymentMoratoriumRule().build());
        centerOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(centerOffice);

        // builder not setting searchId correctly due to not going thru office.save (which uses HierarchyManager)
        String headOfficeSearchId = headOffice.getSearchId();
        OfficeBO anotherOffice = new OfficeBuilder().withParentOffice(headOffice).withName("Another Office")
                .withSearchId(headOfficeSearchId + "26.").withGlobalOfficeNum("n/a001").build();
        holidays = new HashSet<HolidayBO>();
        holidays.add((HolidayBO) new HolidayBuilder().withName("N/A").from(startDate.minusWeeks(3)).to(
                startDate.plusWeeks(8)).withRepaymentMoratoriumRule().build());
        anotherOffice.setHolidays(holidays);
        IntegrationTestObjectMother.createOffice(anotherOffice);
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
