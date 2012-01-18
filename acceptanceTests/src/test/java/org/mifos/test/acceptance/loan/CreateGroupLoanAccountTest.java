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

import java.io.UnsupportedEncodingException;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineHiddenMandatoryFieldsPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.QuestionGroupTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit", "smoke"})
public class CreateGroupLoanAccountTest extends UiTestCaseBase {

    private QuestionGroupTestHelper questionGroupTestHelper;
    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    private HomePage homePage;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        questionGroupTestHelper = new QuestionGroupTestHelper(selenium);
        questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateLoan1");
        questionGroupTestHelper.markQuestionGroupAsInactive("QGForCreateLoan2");
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
        DateTime targetTime = new DateTime(2011, 2, 25, 1, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        //When
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
        CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
        formParameters.setSearchString("groupWithoutLoan");
        formParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);
        createLoanAccountEntryPage.setAmount("3000.0");
        createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 2, 25, 15, 0, 0, 0));
        selectAdditionalFees();
        CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = createLoanAccountEntryPage.navigateToReviewInstallmentsPage();
        verifyFirstInstallmentDateAndDisbursalDateOnReviewPage();
        verifyAdditionalFeesOnReviewPage();
        CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
        verifyFirstInstallmentDateAndDisbursalDateOnPreviewPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        String loanId = loanAccountPage.getAccountId();
        loanAccountPage.verifyLoanIsPendingApproval();
        loanAccountPage.verifyNumberOfInstallments("4");
        loanAccountPage.verifyDisbursalDate("25/02/2011");
        loanAccountPage.verifyPrincipalOriginal("3,000");
        loanAccountPage.verifyLoanTotalBalance("3,466");
        loanAccountPage.verifyFeesOriginal("410");
        loanAccountPage.verifyInterestOriginal("56");
        verifyFees();
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(3, 1, "04-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(4, 1, "11-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(5, 1, "18-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(6, 1, "25-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(3, 3, "750.2");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(4, 3, "750.2");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(5, 3, "750.2");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(6, 3, "749.4");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(3, 5, "110.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(4, 5, "100.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(5, 5, "100.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(6, 5, "100.0");
        viewRepaymentSchedulePage.navigateToLoanAccountPage();
        
        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        editLoanAccountStatusParameters.setNote("test");
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);

        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setPaymentType(DisburseLoanParameters.CASH);
        disburseParameters.setDisbursalDateDD("25");
        disburseParameters.setDisbursalDateMM("02");
        disburseParameters.setDisbursalDateYYYY("2011");
        loanTestHelper.disburseLoan(loanId, disburseParameters);
    }

    private void selectAdditionalFees() {
        selenium.select("selectedFeeId0", "label=loanWeeklyFee");
        selenium.type("selectedFeeId0Amount", "100");
    }
            
    private void verifyFirstInstallmentDateAndDisbursalDateOnReviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary']/div[3]/div[2]"), ("25-Feb-2011"));
        Assert.assertEquals(selenium.getTable("installments.1.1"), ("04/03/11"));
    }
            
    private void verifyAdditionalFeesOnReviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[1]/div[1]"), ("oneTimeFee"));
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[1]/div[2]"), ("10 Periodicity: One Time Frequency: Upfront"));
    }
            
    private void verifyFirstInstallmentDateAndDisbursalDateOnPreviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[4]/div[2]"), ("25-Feb-2011"));
        Assert.assertEquals(selenium.getTable("installments.1.1"), ("04-Mar-2011"));
    }
            
    private void verifyFees() {
        Assert.assertTrue(selenium.isTextPresent("loanWeeklyFee: 100 ( Recur every 1 week(s))"));
        Assert.assertTrue(selenium.isTextPresent("oneTimeFee: 10"));
    }
    
    /**
    * Create a new Group Loan with GLIM and LSIM enabled
    * http://mifosforge.jira.com/browse/MIFOSTEST-1179
    *
    * @throws Exception
    */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void CreateLoanWithGLIMandLSIMenabled() throws Exception {
        applicationDatabaseOperation.updateGLIM(1);
        applicationDatabaseOperation.updateLSIM(1);
        setAppDate(new DateTime(2011, 4, 29, 15, 0, 0, 0));
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
        CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
        formParameters.setSearchString("Default Group");
        formParameters.setLoanProduct("GroupEmergencyLoan");
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);
        verifyDisbursalDateOnLoanEntryPage();
        createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 4, 30, 15, 0, 0, 0));
        verifyDisbursalDateErrorMessage();
        createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 4, 21, 15, 0, 0, 0));
        verifyDisbursalDateErrorMessage();
        createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 4, 29, 15, 0, 0, 0));
        createLoanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "500", "0000-Animal Husbandry");
        createLoanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "1000", "0001-Cow Purchase");
        CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = createLoanAccountEntryPage.navigateToReviewInstallmentsPage();
        verifyFirstInstallmentAndDisbursalDateOnReviewPage();
        CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
        verifyFirstInstallmentAndDisbursalDateOnPreviewPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        loanAccountPage.verifyLoanIsPendingApproval();
        loanAccountPage.verifyNumberOfInstallments("10");
        loanAccountPage.verifyDisbursalDate("29/04/2011");
        loanAccountPage.verifyPrincipalOriginal("1,500");
        loanAccountPage.verifyLoanTotalBalance("1,500");
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(3, 1, "06-May-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(4, 1, "13-May-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(5, 1, "20-May-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(6, 1, "27-May-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(7, 1, "03-Jun-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(8, 1, "10-Jun-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(9, 1, "17-Jun-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(10, 1, "24-Jun-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(11, 1, "01-Jul-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(12, 1, "08-Jul-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(3, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(4, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(5, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(6, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(7, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(8, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(9, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(10, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(11, 3, "150.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(12, 3, "150.0");
        viewRepaymentSchedulePage.navigateToLoanAccountPage();
        applicationDatabaseOperation.updateGLIM(0);
        applicationDatabaseOperation.updateLSIM(0);
    }
        
    private void verifyFirstInstallmentAndDisbursalDateOnReviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary']/div[3]/div[2]"), ("29-Apr-2011"));
        Assert.assertEquals(selenium.getTable("installments.1.1"), ("06/05/11"));
    }
        
    private void verifyFirstInstallmentAndDisbursalDateOnPreviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[4]/div[2]"), ("29-Apr-2011"));
        Assert.assertEquals(selenium.getTable("installments.1.1"), ("06-May-2011"));
    }
        
    private void setAppDate(DateTime dateTime) throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        dateTimeUpdaterRemoteTestingService.setDateTime(dateTime);
    }
       
    private void verifyDisbursalDateOnLoanEntryPage() {
        Assert.assertEquals(selenium.getValue("disbursementDateDD"), ("29"));
        Assert.assertEquals(selenium.getValue("disbursementDateMM"), ("4"));
        Assert.assertEquals(selenium.getValue("disbursementDateYY"), ("2011"));
    }
        
    private void verifyDisbursalDateErrorMessage() {
        Assert.assertFalse(selenium.isTextPresent("This is an invalid Disbursement date."));
    }
    
    @Test(enabled=true)
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

    @Test(enabled=true)
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
    
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void tryCreateGroupLoanWithGlimEnabledWithoutMandatoryPurposeOfLoan() throws Exception {
        applicationDatabaseOperation.updateGLIM(1);
        try {
            CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
            searchParameters.setSearchString("Default Group");
            searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

            CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

            loanAccountEntryPage.selectTwoClientsForGlim();
            loanAccountEntryPage.navigateToReviewInstallmentsPage();
        } finally {
            applicationDatabaseOperation.updateGLIM(0);
        }
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void tryCreateGroupLoanWithMandatoryPurposeOfLoan() throws Exception {
        applicationDatabaseOperation.updateGLIM(1);
        try {
        	AdminPage adminPage = navigationHelper.navigateToAdminPage();
        	DefineHiddenMandatoryFieldsPage defineHiddenMandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();
            defineHiddenMandatoryFieldsPage.checkMandatoryLoanAccountPurpose();
            defineHiddenMandatoryFieldsPage.submit();
            adminPage.navigateToClientsAndAccountsPageUsingHeaderTab();
            
            CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
            searchParameters.setSearchString("Default Group");
            searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

            CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);

            loanAccountEntryPage.selectTwoClientsForGlim();
            loanAccountEntryPage = loanAccountEntryPage.clickContinueButExpectValidationFailure();
            loanAccountEntryPage.verifyError("Please specify loan purpose for member 1.");
            loanAccountEntryPage.selectPurposeForGlim();
            loanAccountEntryPage.clickContinue();
        } finally {
        	AdminPage adminPage = navigationHelper.navigateToAdminPage();
        	DefineHiddenMandatoryFieldsPage defineHiddenMandatoryFieldsPage = adminPage.navigateToDefineHiddenMandatoryFields();
            defineHiddenMandatoryFieldsPage.uncheckMandatoryLoanAccountPurpose();
            defineHiddenMandatoryFieldsPage.submit();
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