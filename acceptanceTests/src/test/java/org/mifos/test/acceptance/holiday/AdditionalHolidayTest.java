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

package org.mifos.test.acceptance.holiday;

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.ViewHolidaysPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"holiday","acceptance","ui"})
public class AdditionalHolidayTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private static final String LOAN_SCHEDULE = "LOAN_SCHEDULE";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,3,11,0,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        new MifosPage(selenium).logout();
    }

    /*
     * jhil comment:
     * no db test is needed for this test since the actual test is just to click a link ?
     * This is test TC-07.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void createHolidayFromViewHolidays() throws Exception {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        ViewHolidaysPage viewHolidayPage = adminPage.navigateToViewHolidaysPage();
        viewHolidayPage.verifyPage();

        CreateHolidayEntryPage createHolidayPage = viewHolidayPage.navigateToDefineHolidayPage();
        createHolidayPage.verifyPage();

        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Test Holiday");
        params.setFromDateDD("5");
        params.setFromDateMM("09");
        params.setFromDateYYYY("2010");
        params.setRepaymentRule(CreateHolidaySubmitParameters.SAME_DAY);

        CreateHolidayConfirmationPage confirmHolidayPage = createHolidayPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmHolidayPage.verifyPage();
        confirmHolidayPage.submitAndNavigateToViewHolidaysPage();

        logOut();
    }

    // the following tests depend on being able to run batch jobs from acceptance tests.
    /*
     * // TC08 public void createHolidayOnAMeetingWithRepaymentSameDay() {
     * CreateHolidaySubmitParameters sameDayRepaymentParameters =
     * getParametersForHolidayOnAMeeting(SAME_DAY);
     * this.createHoliday(sameDayRepaymentParameters);
     *
     * TODO: missing test cases (commented out test shells) 1. Run batch jobs
     * (how do I do this from the test?) 2. Navigate to the repayment schedule
     * for the loan (or check the db?). Perhaps DB is better and easier.
     *
     *
     * }
     *
     * // TC09 public void createHolidayOnAMeetingWithRepaymentNextWorkingDay()
     * { CreateHolidaySubmitParameters nextWorkingDayRepaymentParameters =
     * getParametersForHolidayOnAMeeting(NEXT_WORKING_DAY);
     * this.createHoliday(nextWorkingDayRepaymentParameters);
     *
     *
     * }
     *
     * // TC10 public void createHolidayOnAMeetingWithRepaymentNextMeeting() {
     * CreateHolidaySubmitParameters nextMeetingRepaymentParameters =
     * getParametersForHolidayOnAMeeting(NEXT_MEETING_OR_REPAYMENT);
     * this.createHoliday(nextMeetingRepaymentParameters);
     *
     *
     * }
     */

    // TC11
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithMeetingOnAHolidayWithRepaymentSameDay() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        // create a holiday on 1st of july and a loan with a meeeting on 1st of july.
        createWeeklyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        // verify against db to make sure that the schedule is correct.
        verifyLoanSchedule("AdditionalHolidayTest_004_result_dbunit.xml.zip");
    }

    // TC12
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createMonthlyLoanScheduleWithMeetingOnAHolidayWithRepaymentSameDay() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        // create a holiday on 1st of july and a loan with a meeeting on 1st of july.
        createMonthlyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_001_result_dbunit.xml.zip");

    }

    // TC13
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextMeeting() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        createWeeklyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        verifyLoanSchedule("AdditionalHolidayTest_005_result_dbunit.xml.zip");
    }

    // TC14
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createMonthlyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextMeeting() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createMonthlyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        verifyLoanSchedule("AdditionalHolidayTest_002_result_dbunit.xml.zip");
    }

    // TC15
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextDay() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        createWeeklyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_006_result_dbunit.xml.zip");
    }

    // TC16
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createMonthlyLoanScheduleWithMeetingOnAHolidayWithRepaymentNextDay() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createMonthlyLoanScheduleWithMeetingOnAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_003_result_dbunit.xml.zip");
    }

    // TC19
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithTwoMeetingsDuringAHolidayWithRepaymentNextMeeting() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);

        verifyLoanSchedule("AdditionalHolidayTest_007_result_dbunit.xml.zip");
    }

    // TC20
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithTwoMeetingsDuringAHolidayWithRepaymentNextDay() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_008_result_dbunit.xml.zip");
    }

    // TC21
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createWeeklyLoanScheduleWithTwoMeetingsDuringAHolidayWithRepaymentSameDay() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_004_dbunit.xml.zip", dataSource, selenium);

        createWeeklyLoanScheduleWithTwoMeetingsDuringAHoliday(CreateHolidaySubmitParameters.SAME_DAY);

        verifyLoanSchedule("AdditionalHolidayTest_009_result_dbunit.xml.zip");
    }

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

    private void createHoliday(final CreateHolidaySubmitParameters params)
    {
        logOut();
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        createHolidayEntryPage.verifyPage();

        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.verifyPage();
        confirmationPage.submitAndNavigateToViewHolidaysPage();
    }

    private void createHolidayOn1stJuly(final String repaymentRule) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();

        params.setName("Canada Day");
        params.setFromDateDD("1");
        params.setFromDateMM("07");
        params.setFromDateYYYY("2009");
        params.setRepaymentRule(repaymentRule);

        createHoliday(params);
    }

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

        createHoliday(params);
    }

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
    private void verifyLoanSchedule (final String resultDataSet) throws Exception
    {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSet);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { LOAN_SCHEDULE });

        dbUnitUtilities.verifyTable(LOAN_SCHEDULE, databaseDataSet, expectedDataSet);
    }

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
    }
}
