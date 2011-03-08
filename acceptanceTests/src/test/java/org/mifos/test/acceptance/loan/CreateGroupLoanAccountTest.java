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

package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateGroupLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;

    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private HomePage homePage;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-303
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newWeeklyGroupLoanAccount() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 18, 1, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        //When
        homePage = loginSuccessfully();

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("groupWithoutLoan");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("3000.0");

        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        String loanId = loanAccountPage.getAccountId();

        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        editLoanAccountStatusParameters.setNote("test");
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);

        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setPaymentType(DisburseLoanParameters.CASH);
        disburseParameters.setDisbursalDateDD("18");
        disburseParameters.setDisbursalDateMM("02");
        disburseParameters.setDisbursalDateYYYY("2011");
        loanTestHelper.disburseLoan(loanId, disburseParameters);
    }

    @SuppressWarnings({"PMD.SignatureDeclareThrowsException"})
    public void newMonthlyGroupLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010, 8, 13, 1, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        //When
        homePage = loginSuccessfully();

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("MonthlyGroup");
        searchParameters.setLoanProduct("MonthlyGroupFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1000.0");

        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();

        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage.submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters);

        createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newMonthlyGroupLoanAccountWithMeetingOnSameWeekAndWeekdayOfMonth() throws Exception {
        homePage = loginSuccessfully();

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("GroupMeetsOn3rdFriday");
        searchParameters.setLoanProduct("MonthlyGroupFlatLoan1stOfMonth");

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1000.0");

        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsPage.navigateToCreateLoanAccountUsingLeftMenu();

        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(searchParameters);
        createLoanAccountEntryPage.verifyPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountEntryPage.submitAndNavigateToLoanAccountConfirmationPage(submitAccountParameters);

        createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void tryCreateGroupLoanWithoutMandatoryPurposeOfLoan() throws Exception {
        applicationDatabaseOperation.updateGLIM(1);
        try {
            CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
            searchParameters.setSearchString("Default Group");
            searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

            CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

            loanAccountEntryPage.selectTwoClientsForGlim();

            CreateLoanAccountConfirmationPage confirmationPage = loanAccountEntryPage.clickContinueAndNavigateToLoanAccountConfirmationPage();

            LoanAccountPage loanAccountPage = confirmationPage.navigateToLoanAccountDetailsPage();

            loanAccountPage.navigateToEditAccountInformation();
        } finally {
            applicationDatabaseOperation.updateGLIM(0);
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void tryCreateGroupLoanWithMandatoryPurposeOfLoan() throws Exception {
        applicationDatabaseOperation.updateGLIM(1);
        try {
            CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
            searchParameters.setSearchString("Default Group");
            searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

            CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

            loanAccountEntryPage.selectTwoClientsForGlim();
            loanAccountEntryPage.selectPurposeForGlim();
            loanAccountEntryPage.clickContinue();
        } finally {
            applicationDatabaseOperation.updateGLIM(0);
        }
    }

    private HomePage loginSuccessfully() {
        (new MifosPage(selenium)).logout();
        LoginPage loginPage = new AppLauncher(selenium).launchMifos();
        loginPage.verifyPage();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        homePage.verifyPage();

        return homePage;
    }
}