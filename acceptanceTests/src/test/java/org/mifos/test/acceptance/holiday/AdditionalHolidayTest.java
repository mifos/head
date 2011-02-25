/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.holiday;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.framework.holiday.ViewHolidaysPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.HolidayTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = { "holiday", "schedules", "acceptance", "ui" })
public class AdditionalHolidayTest extends UiTestCaseBase {

    private AppLauncher appLauncher;
    private HolidayTestHelper holidayTestHelper;
    private CenterTestHelper centerTestHelper;
    private GroupTestHelper groupTestHelper;
    private ClientTestHelper clientTestHelper;
    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;
    private SavingsAccountHelper savingsAccountHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private static final String LOAN_SCHEDULE = "LOAN_SCHEDULE";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(
                selenium);
        DateTime targetTime = new DateTime(2009, 3, 11, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        appLauncher = new AppLauncher(selenium);
        holidayTestHelper = new HolidayTestHelper(selenium);
        centerTestHelper = new CenterTestHelper(selenium);
        groupTestHelper = new GroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        savingsAccountHelper = new SavingsAccountHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        new MifosPage(selenium).logout();
    }

    /*
     * jhil comment: no db test is needed for this test since the actual test is just to click a link ? This is test
     * TC-07.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @Test(enabled = false)
    public void createHolidayFromViewHolidays() throws Exception {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        ViewHolidaysPage viewHolidayPage = adminPage.navigateToViewHolidaysPage();

        CreateHolidayEntryPage createHolidayPage = viewHolidayPage.navigateToDefineHolidayPage();

        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Test Holiday");
        params.setFromDateDD("5");
        params.setFromDateMM("09");
        params.setFromDateYYYY("2010");
        params.setRepaymentRule(CreateHolidaySubmitParameters.SAME_DAY);
        params.setSelectedOfficeIds("1");

        CreateHolidayConfirmationPage confirmHolidayPage = createHolidayPage
                .submitAndNavigateToHolidayConfirmationPage(params);
        confirmHolidayPage.submitAndNavigateToViewHolidaysPage();

        logOut();
    }

    // the following tests depend on being able to run batch jobs from acceptance tests.

    // TC08
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createHolidayOnAMeetingWithRepaymentSameDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        // create loan paid on the 1st of every month and then create a holiday on 1st July
        createMonthlyLoanScheduleAndAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);

        verifyLoanSchedule("AdditionalHolidayTest_010_result_dbunit.xml");
    }

    /*
     * TODO: missing test cases (commented out test shells) 1. Run batch jobs (how do I do this from the test?) 2.
     * Navigate to the repayment schedule for the loan (or check the db?). Perhaps DB is better and easier.
     */

    // TC09
    @Test(enabled = true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createHolidayOnAMeetingWithRepaymentNextWorkingDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleAndAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);

        verifyLoanSchedule("AdditionalHolidayTest_011_result_dbunit.xml");

    }

    // TC10
    @Test(enabled = true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createHolidayOnAMeetingWithRepaymentNextMeeting() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsAndAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);

        verifyLoanSchedule("AdditionalHolidayTest_012_result_dbunit.xml");

    }

    // TC11
    @Test(enabled = true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithMeetingOnAHolidayWithRepaymentSameDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        // create a holiday on 1st of July and a loan with a meeting on 1st of July.
        createWeeklyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        // verify against db to make sure that the schedule is correct.
        verifyLoanSchedule("AdditionalHolidayTest_004_result_dbunit.xml");
    }

    // TC12
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = true)
    public void createMonthlyLoanScheduleWithMeetingOnAHolidayWithRepaymentSameDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        // create a holiday on 1st of july and a loan with a meeting on 1st of July.
        createMonthlyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_001_result_dbunit.xml");

    }

    // TC13
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createWeeklyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextMeeting() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        verifyLoanSchedule("AdditionalHolidayTest_005_result_dbunit.xml");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createWeeklyLoanScheduleNoFeesWithSecondInstallmentInAMoratorium() throws Exception {

        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        // April 1st 2009 is a Wednesday
        this.createHolidayOn("April Fools Day", CreateHolidaySubmitParameters.MORATORIUM, "01", "04", "2009");

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        // This client meets weekly on Wednesdays
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        // This loan product is a weekly flat-interest loan without fees that defaults to 11 installments.
        searchParameters.setLoanProduct("MyLoanProduct1232993826860");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2000");

        this.createLoan(searchParameters, submitAccountParameters);

        /*
         * Expected result: Without moratorium, 11 installments scheduled every Wednesday from 2010-03-18 through
         * 2010-5-27. The moratorium on the date of the third installment, 2010-04-01, causes it and all future
         * installments to be pushed out to 2010-04-8 through 2010-6-3. Principal and interest payments should be the
         * same as if there were no moratorium.
         */
        verifyLoanSchedule("CreateLoanScheduleWithMoratorium_001_result_dbunit.xml");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createTwoWeeklyLoansInDifferentOffices() throws Exception {
        CreateLoanAccountSearchParameters searchParameters1 = new CreateLoanAccountSearchParameters();
        // This client meets weekly on Wednesdays
        searchParameters1.setSearchString("Stu1233171716380 Client1233171716380");

        // This loan product is a weekly flat-interest loan without fees that defaults to 11 installments.
        searchParameters1.setLoanProduct("MyLoanProduct1232993826860");

        CreateLoanAccountSubmitParameters submitAccountParameters1 = new CreateLoanAccountSubmitParameters();
        submitAccountParameters1.setAmount("2000");

        this.createLoan(searchParameters1, submitAccountParameters1);

        // create second loan account
        CreateLoanAccountSearchParameters searchParameters2 = new CreateLoanAccountSearchParameters();
        searchParameters2.setSearchString("Stu1232993852651 Client1232993852651");
        searchParameters2.setLoanProduct("MyLoanProduct1232993826860");

        CreateLoanAccountSubmitParameters submitAccountParameters2 = new CreateLoanAccountSubmitParameters();
        submitAccountParameters2.setAmount("2000");

        this.createLoan(searchParameters2, submitAccountParameters2);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createMonthlyLoanScheduleNoFeesWithFirstInstallmentOnAMoratorium() throws Exception {

        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);
        this.createHolidayOn("May Day", CreateHolidaySubmitParameters.MORATORIUM, "01", "05", "2009");
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");

        this.createLoan(searchParameters, submitAccountParameters);
        verifyLoanSchedule("CreateLoanScheduleWithMoratorium_002_result_dbunit.xml");
    }

    // TC14
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createMonthlyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextMeeting() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createMonthlyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        verifyLoanSchedule("AdditionalHolidayTest_002_result_dbunit.xml");
    }

    // TC15
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createWeeklyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_006_result_dbunit.xml");
    }

    // TC16
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createMonthlyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createMonthlyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_003_result_dbunit.xml");
    }

    // TC19
    @Test(enabled = false)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithTwoMeetingsDuringAHolidayWithRepaymentNextMeeting() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        verifyLoanSchedule("AdditionalHolidayTest_007_result_dbunit.xml");
    }

    // TC20
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createWeeklyLoanScheduleWithTwoMeetingsDuringAHolidayWithRepaymentNextDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_008_result_dbunit.xml");
    }

    // TC21
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void createWeeklyLoanScheduleWithTwoMeetingsDuringAHolidayWithRepaymentSameDay() throws Exception {
        initRemote
                .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_009_result_dbunit.xml");
    }

    @Test(enabled = false)
    private void createMonthlyLoanScheduleWithMeetingOnAHoliday(final String repaymentRule) {
        // create a holiday on 1st of july
        this.createHolidayOn1stJuly(repaymentRule);

        // create a loan that has its repayment on the 1st of every month
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");

        this.createLoan(searchParameters, submitAccountParameters);
    }

    @Test(enabled = false)
    private void createMonthlyLoanScheduleAndAHoliday(final String repaymentRule) {

        // create a loan that has its repayment on the 1st of every month
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");

        this.createLoan(searchParameters, submitAccountParameters);

        // create a holiday on 1st of july
        this.createHolidayOn1stJuly(repaymentRule);
    }

    @Test(enabled = false)
    private void createWeeklyLoanScheduleWithMeetingOnAHoliday(final String repaymentRule) {
        // create a holiday on July 1st 2009, a Wednesday
        this.createHolidayOn1stJuly(repaymentRule);

        // create a loan for a client who meet at wednesdays, client with system ID 0003-000000006
        // in the acceptance_small_003 data set.
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyClientDeclinetLoanWithPeriodicFee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("4321.0");

        this.createLoan(searchParameters, submitAccountParameters);
    }

    @Test(enabled = false)
    private void createWeeklyLoanScheduleAndAHoliday(final String repaymentRule) {
        // create a loan for a client who meet at wednesdays, client with system ID 0003-000000006
        // in the acceptance_small_003 data set.
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyClientDeclinetLoanWithPeriodicFee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("4321.0");

        this.createLoan(searchParameters, submitAccountParameters);

        // create a holiday on July 1st 2009, a Wednesday
        this.createHolidayOn1stJuly(repaymentRule);
    }

    @Test(enabled = false)
    private void createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(final String repaymentRule) {
        // create a holiday that overlaps two Wednesdays
        this.createHolidayFrom14thJulyThru23rd(repaymentRule);

        // create a loan for a client who meet at Wednesdays, client with system ID 0003-000000006
        // in the acceptance_small_003 data set.
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyClientDeclinetLoanWithPeriodicFee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2000.0");

        this.createLoan(searchParameters, submitAccountParameters);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-280
    public void testBranchSpecificMoratorium() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
            CreateHolidaySubmitParameters param = FormParametersHelper.getCreateHolidaySubmitParameters();
        //When / Then
        holidayTestHelper.createHoliday(param);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-281
    @Test(enabled = false) // TODO js - investigate why this fails on master
    public void testHolidayAffectsFeeSchedule() throws Exception {
        // Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_015_dbunit.xml", dataSource, selenium);

        ChargeParameters chargeParameters = new ChargeParameters();
        String officeName = "MyOffice1232993831593";
        String centerName = "MyCenter1232993841778";
        String groupName = "MyGroup1232993846342";
        String clientName = "Stu1232993852651 Client1232993852651";
        String loanId = "000100000000004";
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday" + StringUtil.getRandomString(8));
        params.setFromDateDD("16");
        params.setFromDateMM("03");
        params.setFromDateYYYY("2009");
        params.setRepaymentRule(CreateHolidaySubmitParameters.MORATORIUM);
        params.addOffice(officeName);
        CreateSavingsAccountSearchParameters searchParameters = new CreateSavingsAccountSearchParameters();
        searchParameters.setSearchString(clientName);
        searchParameters.setSavingsProduct("MandClientSavings3MoPostMinBal");
        CreateSavingsAccountSubmitParameters submitAccountParameters = new CreateSavingsAccountSubmitParameters();
        submitAccountParameters.setAmount("100.0");

        // When
        chargeParameters.setType("Weekly Center Fee");
        centerTestHelper.applyCharge(centerName, chargeParameters);
        String centerAmount = navigationHelper.navigateToCenterViewDetailsPage(centerName).getAmountDue();

        chargeParameters.setType("Weekly Group Fee");
        groupTestHelper.applyCharge(groupName, chargeParameters);
        String groupAmount = navigationHelper.navigateToGroupViewDetailsPage(groupName).getAmountDue();

        chargeParameters.setType("Weekly Client Fee");
        clientTestHelper.applyCharge(clientName, chargeParameters);
        String clientAmount = navigationHelper.navigateToClientViewDetailsPage(clientName).getAmountDue();

        chargeParameters.setType("PeriodicWeeklyLoanFee8");
        loanTestHelper.applyChargeUsingFeeLabel(loanId, chargeParameters);

        String savingsId = savingsAccountHelper.createSavingsAccountWithQG(searchParameters, submitAccountParameters).getAccountId();
        String savingsAmount = savingsAccountHelper.activateSavingsAccount(savingsId).getTotalAmountDue();

        holidayTestHelper.createHoliday(params);

        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        navigationHelper.navigateToAdminPage();
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(
                selenium);
        DateTime targetTime = new DateTime(2009, 3, 17, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToCenterViewDetailsPage(centerName).verifyAmountDue(centerAmount);
        navigationHelper.navigateToGroupViewDetailsPage(groupName).verifyAmountDue(groupAmount);
        navigationHelper.navigateToClientViewDetailsPage(clientName).verifyAmountDue(clientAmount);
        navigationHelper.navigateToLoanAccountPage(loanId).navigateToRepaymentSchedulePage().verifyScheduleNotContainDate("16-Mar-2009");
        navigationHelper.navigateToSavingsAccountDetailPage(savingsId).verifyTotalAmountDue(savingsAmount);
    }

    @Test(enabled = false)
    private void createWeeklyLoanScheduleWithTwoMeetingsAndAHoliday(final String repaymentRule) {
        // create a loan for a client who meet at Wednesdays, client with system ID 0003-000000006
        // in the acceptance_small_003 data set.
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyClientDeclinetLoanWithPeriodicFee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2000.0");

        this.createLoan(searchParameters, submitAccountParameters);

        // create a holiday that overlaps two Wednesdays
        this.createHolidayFrom14thJulyThru23rd(repaymentRule);
    }

    @Test(enabled = false)
    private void createHoliday(final CreateHolidaySubmitParameters params) {
        logOut();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();

        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage
                .submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.submitAndNavigateToViewHolidaysPage();
    }

    @Test(enabled = false)
    private void createHolidayOn1stJuly(final String repaymentRule) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Canada Day");
        params.setFromDateDD("1");
        params.setFromDateMM("07");
        params.setFromDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);
        params.setSelectedOfficeIds("1");

        createHoliday(params);
    }

    @Test(enabled = false)
    private void createHolidayOn(final String name, final String repaymentRule, final String day, final String month,
            final String year) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName(name);
        params.setFromDateDD(day);
        params.setFromDateMM(month);
        params.setFromDateYYYY(year);
        params.setRepaymentRule(repaymentRule);
        params.setSelectedOfficeIds("1");

        createHoliday(params);
    }

    @Test(enabled = false)
    private void createHolidayFrom14thJulyThru23rd(final String repaymentRule) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Long holiday");
        params.setFromDateDD("14");
        params.setFromDateMM("07");
        params.setFromDateYYYY("2009");
        params.setThruDateDD("23");
        params.setThruDateMM("07");
        params.setThruDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);
        params.setSelectedOfficeIds("1");

        createHoliday(params);
    }

    @Test(enabled = false)
    private void createLoan(final CreateLoanAccountSearchParameters searchParameters,
            final CreateLoanAccountSubmitParameters submitAccountParameters) {
        logOut();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = navigateToCreateLoanAccountSearchPage();
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage
                .searchAndNavigateToCreateLoanAccountPage(searchParameters);
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage
                .submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters);
        createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Ignore
    @Test(enabled = false)
    private void verifyLoanSchedule(final String resultDataSet) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSet);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { LOAN_SCHEDULE });

        dbUnitUtilities.verifyTable(LOAN_SCHEDULE, databaseDataSet, expectedDataSet);
    }

    @Test(enabled = false)
    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher.launchMifos().loginSuccessfullyUsingDefaultCredentials().navigateToAdminPage();

    }
}
