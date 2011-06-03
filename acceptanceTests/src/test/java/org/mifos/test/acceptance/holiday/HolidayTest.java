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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayConfirmationPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.HolidayTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"holiday","acceptance","ui","no_db_unit"})
public class HolidayTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;
    private HolidayTestHelper holidayTestHelper;

    private AppLauncher appLauncher;

    DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        holidayTestHelper = new HolidayTestHelper(selenium);
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(enabled = true)
    public void createHoliday() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2010,1,1,13,0,0,0));
    	
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();

        CreateHolidaySubmitParameters params = this.getHolidayParameters();
        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.submitAndNavigateToViewHolidaysPage();

        // try to create second holiday with the same date
        createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        params.setName("Test Holiday 2");
        confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        assertTextFoundOnPage("Holiday with the same date already exists:",
                "Text about duplicated holidays was not found.");
    }

    @Test(enabled = true)
    public void viewHolidays() {
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();
        adminPage.navigateToViewHolidays();
    }

    /*
     * FIXME: KEITHW - disabling these holiday tests as when they create a loan, they are expecting to go to a 
     * 'nextPayment_loanAccount' page to see 'review of installments'??? 
     */
    //http://mifosforge.jira.com/browse/MIFOSTEST-79
    @Test(enabled = true)
    public void holidaysRepaymentRule() throws Exception {
        //Given
            dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2031,1,1,13,0,0,0));
        //When
        createHolidayForInstallments(getHolidayParameters("2031"));

        CreateLoanAccountSearchParameters searchParameters=setSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = setSubmitParameters();

        //Then
        loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("10-Jan-2031","17-Jan-2031","24-Jan-2031","31-Jan-2031","07-Feb-2031","07-Mar-2031","07-Mar-2031","07-Mar-2031","07-Mar-2031","14-Mar-2031");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-78
    @Test(enabled = true)
    public void holidaysRepaymentRuleSameDay() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2032,1,1,13,0,0,0));
        //When
        CreateHolidaySubmitParameters param = getHolidayParameters("2032");
        param.setRepaymentRule(CreateHolidaySubmitParameters.SAME_DAY);

        createHolidayForInstallments(param);

        CreateLoanAccountSearchParameters searchParameters=setSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = setSubmitParameters();
        //Then
        loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("09-Jan-2032","16-Jan-2032","23-Jan-2032","30-Jan-2032","06-Feb-2032","13-Feb-2032","20-Feb-2032","27-Feb-2032","05-Mar-2032","12-Mar-2032");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-81
    @Test(enabled = true)
    public void holidaysRepaymentRuleNextWorkingDay() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2033,1,1,13,0,0,0));
        //When
        CreateHolidaySubmitParameters param = getHolidayParameters("2033");
        param.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        createHolidayForInstallments(param);

        CreateLoanAccountSearchParameters searchParameters=setSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = setSubmitParameters();

        //Then
        loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("14-Jan-2033","21-Jan-2033","28-Jan-2033","04-Feb-2033","11-Feb-2033","01-Mar-2033","01-Mar-2033","04-Mar-2033","11-Mar-2033","18-Mar-2033");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-75
    @Test(enabled = true)
    public void holidaysRepaymentRuleWithBatchJobs() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2034,1,1,13,0,0,0));
        //When
        CreateLoanAccountSearchParameters searchParameters=setSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = setSubmitParameters();

        LoanAccountPage page =  loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        String loanId = page.getAccountId();
        page.navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("13-Jan-2034","20-Jan-2034","27-Jan-2034","03-Feb-2034","10-Feb-2034","17-Feb-2034","24-Feb-2034","03-Mar-2034","10-Mar-2034","17-Mar-2034");

        createHolidayForInstallments(getHolidayParameters("2034"));

        //Then
        runBatchJobsForHoliday();


        navigationHelper.navigateToLoanAccountPage(loanId).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("13-Jan-2034","20-Jan-2034","27-Jan-2034","03-Feb-2034","10-Feb-2034","03-Mar-2034","03-Mar-2034","03-Mar-2034","10-Mar-2034","17-Mar-2034");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-74
    @Test(enabled = true)
    public void holidaysRepaymentRuleSameDayWithBatchJobs() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2035,1,1,13,0,0,0));
        //When
        CreateLoanAccountSearchParameters searchParameters=setSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = setSubmitParameters();

        LoanAccountPage page =  loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        String lid = page.getAccountId();
        page.navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("12-Jan-2035","19-Jan-2035","26-Jan-2035","02-Feb-2035","09-Feb-2035","16-Feb-2035","23-Feb-2035","02-Mar-2035","09-Mar-2035","16-Mar-2035");

        CreateHolidaySubmitParameters param = getHolidayParameters("2035");
        param.setRepaymentRule(CreateHolidaySubmitParameters.SAME_DAY);

        createHolidayForInstallments(param);
        //Then
        runBatchJobsForHoliday();

        navigationHelper.navigateToLoanAccountPage(lid).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("12-Jan-2035","19-Jan-2035","26-Jan-2035","02-Feb-2035","09-Feb-2035","16-Feb-2035","23-Feb-2035","02-Mar-2035","09-Mar-2035","16-Mar-2035");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-76
    @Test(enabled = true)
    public void holidaysRepaymentRuleNextWorkingDayWithBatchJobs() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2036,1,1,13,0,0,0));
        //When
        CreateLoanAccountSearchParameters searchParameters=setSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = setSubmitParameters();

        LoanAccountPage page =  loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        String lid = page.getAccountId();
        page.navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("11-Jan-2036","18-Jan-2036","25-Jan-2036","01-Feb-2036","08-Feb-2036","15-Feb-2036","22-Feb-2036","29-Feb-2036","07-Mar-2036","14-Mar-2036");

        CreateHolidaySubmitParameters param = getHolidayParameters("2036");
        param.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_WORKING_DAY);

        createHolidayForInstallments(param);
        //Then
        runBatchJobsForHoliday();

        navigationHelper.navigateToLoanAccountPage(lid).navigateToRepaymentSchedulePage();
        loanTestHelper.verifyRepaymentScheduleForHolidays("11-Jan-2036","18-Jan-2036","25-Jan-2036","01-Feb-2036","08-Feb-2036","29-Feb-2036","29-Feb-2036","29-Feb-2036","07-Mar-2036","14-Mar-2036");
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-72
    @Test(enabled = true)
    public void definedAndViewHoliday() throws Exception {
        //Given
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2037,1,1,13,0,0,0));
        //When / Then
        CreateHolidaySubmitParameters params = getHolidayParameters("2037");
        params.setRepaymentRule(CreateHolidaySubmitParameters.SAME_DAY);
        holidayTestHelper.createHoliday(params);
    }

    private AdminPage loginAndNavigateToAdminPage() {
        return appLauncher
         .launchMifos()
         .loginSuccessfullyUsingDefaultCredentials()
         .navigateToAdminPage();
     }

    private void createHolidayForInstallments(CreateHolidaySubmitParameters params){
        AdminPage adminPage = loginAndNavigateToAdminPage();
        adminPage.verifyPage();

        CreateHolidayEntryPage createHolidayEntryPage = adminPage.navigateToDefineHolidayPage();
        CreateHolidayConfirmationPage confirmationPage = createHolidayEntryPage.submitAndNavigateToHolidayConfirmationPage(params);
        confirmationPage.submitAndNavigateToViewHolidaysPage();
    }

    private CreateHolidaySubmitParameters getHolidayParameters(String year) {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday Test" + StringUtil.getRandomString(2));
        params.setFromDateDD("14");
        params.setFromDateMM("02");
        params.setFromDateYYYY(year);
        params.setThruDateDD("28");
        params.setThruDateMM("02");
        params.setThruDateYYYY(year);
        params.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);
        params.setSelectedOfficeIds("1");
        params.addOffice("Mifos HO");
        return params;
    }
    
    private CreateHolidaySubmitParameters getHolidayParameters() {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday Test" + StringUtil.getRandomString(2));
        params.setFromDateDD("14");
        params.setFromDateMM("02");
        params.setFromDateYYYY("2010");
        params.setThruDateDD("28");
        params.setThruDateMM("02");
        params.setThruDateYYYY("2010");
        params.setRepaymentRule(CreateHolidaySubmitParameters.NEXT_MEETING_OR_REPAYMENT);
        params.addOffice("Mifos HO");
        return params;
    }

    private void runBatchJobsForHoliday() throws SQLException {
        applicationDatabaseOperation.cleanBatchJobTables();
        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);
    }

    private CreateLoanAccountSearchParameters setSearchParameters() {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Holiday TestClient");
        searchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        return searchParameters;
    }

    private CreateLoanAccountSubmitParameters setSubmitParameters(){
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1423.0");
        submitAccountParameters.setGracePeriodTypeNone(true);
        return submitAccountParameters;
    }
}