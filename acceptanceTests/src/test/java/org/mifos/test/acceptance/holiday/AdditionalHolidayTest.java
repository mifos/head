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

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
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
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchParameters;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSubmitParameters;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.CenterTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.ClientTestHelper;
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

import java.util.ArrayList;
import java.util.List;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"holiday", "schedules", "acceptance", "ui"})
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

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
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

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void createTwoWeeklyLoansInDifferentOffices() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
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
    //http://mifosforge.jira.com/browse/MIFOSTEST-280
    public void testBranchSpecificMoratorium() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);
        CreateHolidaySubmitParameters param = getCreateHolidaySubmitParameters();
        //When / Then
        holidayTestHelper.createHoliday(param);
    }

    public CreateHolidaySubmitParameters getCreateHolidaySubmitParameters() {
        CreateHolidaySubmitParameters params = new CreateHolidayEntryPage.CreateHolidaySubmitParameters();
        params.setName("Holiday" + StringUtil.getRandomString(8));
        params.setFromDateDD("01");
        params.setFromDateMM("02");
        params.setFromDateYYYY("2011");
        params.setThruDateDD("14");
        params.setThruDateMM("02");
        params.setThruDateYYYY("2011");
        params.setRepaymentRule(CreateHolidaySubmitParameters.MORATORIUM);
        params.addOffice("MyOffice1233266206574");
        return params;
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
