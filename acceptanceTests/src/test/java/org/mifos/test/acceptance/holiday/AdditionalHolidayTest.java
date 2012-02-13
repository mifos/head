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

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage;
import org.mifos.test.acceptance.framework.holiday.CreateHolidayEntryPage.CreateHolidaySubmitParameters;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.GroupTestHelper;
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

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"holiday", "schedules", "acceptance", "ui", "no_db_unit"})
public class AdditionalHolidayTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private AppLauncher appLauncher;
    private HolidayTestHelper holidayTestHelper;
    private CenterTestHelper centerTestHelper;
    private GroupTestHelper groupTestHelper;
    private ClientTestHelper clientTestHelper;
    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;

    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        appLauncher = new AppLauncher(selenium);
        holidayTestHelper = new HolidayTestHelper(selenium);
        centerTestHelper = new CenterTestHelper(selenium);
        groupTestHelper = new GroupTestHelper(selenium);
        clientTestHelper = new ClientTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        new MifosPage(selenium).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    /*
     * loan creation for setup is stepping into questionnaire page when it shouldnt
     */
    @Test(enabled=true)
    public void createTwoWeeklyLoansInDifferentOffices() throws Exception {
        DateTime targetTime = new DateTime(2011, 3, 9, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParameters1 = new CreateLoanAccountSearchParameters();
        searchParameters1.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters1.setLoanProduct("WeeklyClientFlatLoanWithNoFee");

        CreateLoanAccountSubmitParameters submitAccountParameters1 = new CreateLoanAccountSubmitParameters();
        submitAccountParameters1.setAmount("2000");

        this.createLoan(searchParameters1, submitAccountParameters1);

        // create second loan account
        CreateLoanAccountSearchParameters searchParameters2 = new CreateLoanAccountSearchParameters();
        searchParameters2.setSearchString("ClientInBranch1 ClientInBranch1");
        searchParameters2.setLoanProduct("WeeklyClientFlatLoanWithNoFee");

        CreateLoanAccountSubmitParameters submitAccountParameters2 = new CreateLoanAccountSubmitParameters();
        submitAccountParameters2.setAmount("2000");

        this.createLoan(searchParameters2, submitAccountParameters2);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    //http://mifosforge.jira.com/browse/MIFOSTEST-280
    public void testBranchSpecificMoratorium() throws Exception {
        DateTime targetTime = new DateTime(2009, 3, 11, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        //Given
        CreateHolidaySubmitParameters param = getCreateHolidaySubmitParameters();
        //When / Then
        holidayTestHelper.createHoliday(param);
    }

    private CreateHolidaySubmitParameters getCreateHolidaySubmitParameters() {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday" + StringUtil.getRandomString(8));
        params.setFromDateDD("01");
        params.setFromDateMM("02");
        params.setFromDateYYYY("2011");
        params.setThruDateDD("14");
        params.setThruDateMM("02");
        params.setThruDateYYYY("2011");
        params.setRepaymentRule(CreateHolidaySubmitParameters.MORATORIUM);
        params.addOffice("MyOfficeDHMFT");
        return params;
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-281
    public void testHolidayAffectsFeeSchedule() throws Exception {
        DateTime targetTime = new DateTime(2009, 3, 11, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        // Given

        ChargeParameters chargeParameters = new ChargeParameters();
        String officeName = "MyOfficeDHMFT";
        String centerName = "Default Center";
        String groupName = "Default Group";
        String clientName = "Holiday TestClient";
        String loanId = "000100000000035";
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday" + StringUtil.getRandomString(8));
        params.setFromDateDD("16");
        params.setFromDateMM("03");
        params.setFromDateYYYY("2009");
        params.setRepaymentRule(CreateHolidaySubmitParameters.MORATORIUM);
        params.addOffice(officeName);

        // When
        chargeParameters.setType("Misc Fees");
        chargeParameters.setAmount("100");
        centerTestHelper.applyCharge(centerName, chargeParameters);
        String centerAmount = navigationHelper.navigateToCenterViewDetailsPage(centerName).getAmountDue();

        chargeParameters.setType("Misc Fees");
        chargeParameters.setAmount("100");
        groupTestHelper.applyCharge(groupName, chargeParameters);
        String groupAmount = navigationHelper.navigateToGroupViewDetailsPage(groupName).getAmountDue();

        chargeParameters.setType("Misc Fees");
        chargeParameters.setAmount("100");
        clientTestHelper.applyCharge(clientName, chargeParameters);
        String clientAmount = navigationHelper.navigateToClientViewDetailsPage(clientName).getAmountDue();

        chargeParameters.setType("loanWeeklyFee");
        chargeParameters.setAmount("100");
        loanTestHelper.applyChargeUsingFeeLabel(loanId, chargeParameters);

        holidayTestHelper.createHoliday(params);

        navigationHelper.navigateToAdminPage();

        applicationDatabaseOperation.cleanBatchJobTables();
        List<String> jobsToRun = new ArrayList<String>();
        jobsToRun.add("ApplyHolidayChangesTaskJob");
        new BatchJobHelper(selenium).runSomeBatchJobs(jobsToRun);

        targetTime = new DateTime(2009, 3, 17, 0, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        navigationHelper.navigateToCenterViewDetailsPage(centerName).verifyAmountDue(centerAmount);
        navigationHelper.navigateToGroupViewDetailsPage(groupName).verifyAmountDue(groupAmount);
        navigationHelper.navigateToClientViewDetailsPage(clientName).verifyAmountDue(clientAmount);
        navigationHelper.navigateToLoanAccountPage(loanId).navigateToRepaymentSchedulePage().verifyScheduleNotContainDate("16-Mar-2009");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void testHolidayLoanDisbursement() throws Exception {
    	DateTime targetTime = new DateTime(2020, 8, 9, 0, 0, 0, 0);
    	dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

    	String officeName = "MyOfficeDHMFT";
    	String loanId = "000100000000035";
 
    	CreateHolidaySubmitParameters holidayParams = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
    	holidayParams.setName("Holiday" + StringUtil.getRandomString(8));
    	holidayParams.setFromDateDD("16");
    	holidayParams.setFromDateMM("08");
    	holidayParams.setFromDateYYYY("2020");
    	holidayParams.setRepaymentRule(CreateHolidaySubmitParameters.MORATORIUM);
    	holidayParams.addOffice(officeName);

    	holidayTestHelper.createHoliday(holidayParams);
    	DateTime targetTime1 = new DateTime(2020, 8, 16, 0, 0, 0, 0);
    	dateTimeUpdaterRemoteTestingService.setDateTime(targetTime1);
    	
    	DisburseLoanParameters disburseParams = new DisburseLoanParameters();
    	disburseParams.setDisbursalDateDD("16");
    	disburseParams.setDisbursalDateMM("08");
    	disburseParams.setDisbursalDateYYYY("2020");
    	disburseParams.setPaymentType(DisburseLoanParameters.CASH);
    	
    	loanTestHelper.prepareToDisburseLoan(loanId)
    			.submitAndNavigateToDisburseLoanConfirmationPage(disburseParams)
    			.submitButDisbursalFailed("Disbursement date must be on a working day.");
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

    private CreateLoanAccountSearchPage navigateToCreateLoanAccountSearchPage() {
        LoginPage loginPage = appLauncher.launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        return clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();
    }
}
