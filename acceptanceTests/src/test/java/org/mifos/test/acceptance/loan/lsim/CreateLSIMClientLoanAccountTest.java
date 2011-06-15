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

package org.mifos.test.acceptance.loan.lsim;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.account.AccountStatus;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateLSIMClientLoanAccountTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private LoanTestHelper loanTestHelper;
    private LoanProductTestHelper loanProductTestHelper;
    private String expectedDate;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        applicationDatabaseOperation.updateLSIM(1);
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010, 1, 22, 10, 55, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() throws SQLException {
        applicationDatabaseOperation.updateLSIM(0);
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled=false, groups = {"loan", "acceptance", "ui"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-127
    public void newWeeklyLSIMClientLoanAccount() throws Exception {

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        expectedDate = "29-Jan-2010";
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("9012.0");
        submitAccountParameters.setLsimFrequencyWeeks("on");
        submitAccountParameters.setLsimWeekFrequency("1");
        submitAccountParameters.setLsimWeekDay("Friday");

        createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void newMonthlyClientLoanAccountWithMeetingOnSpecificDayOfMonth() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Client - Mary Monthly1");
        searchParameters.setLoanProduct("MonthlyClientFlatLoan1stOfMonth");
        expectedDate = "05-Feb-2010";
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("1234.0");
        // create LSIM loan that has repayments on 5th of every month
        submitAccountParameters.setLsimFrequencyMonths("on");
        submitAccountParameters.setLsimMonthTypeDayOfMonth("on");
        submitAccountParameters.setLsimDayOfMonth("5");

        createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void newMonthlyClientLoanAccountWithMeetingOnSameWeekAndWeekday() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Monthly3rdFriday");
        searchParameters.setLoanProduct("MonthlyClientFlatLoanThirdFridayOfMonth");
        expectedDate = "11-Mar-2010";

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setAmount("2765.0");
        // create LSIM loan that has repayments on 2nd Thursday of each month
        submitAccountParameters.setLsimFrequencyMonths("on");
        submitAccountParameters.setLsimMonthTypeNthWeekdayOfMonth("on");
        submitAccountParameters.setLsimMonthRank("Second");
        submitAccountParameters.setLsimWeekDay("Thursday");

        createLSIMLoanAndCheckAmountAndInstallmentDate(searchParameters, submitAccountParameters, expectedDate);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-123
    @Test(enabled=false)
    public void createLoanAccountWithNonMeetingDatesForDisburseAndRepay() throws Exception {
        //Given
        setTime(2011, 03, 24);
        String errorMessage = "";
        CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
        formParameters.setLoanProduct("ClientEmergencyLoan");
        formParameters.setSearchString("UpdateCustomProperties TestClient");

        for(int i=0;i<2;i++)
        {
            CreateLoanAccountSearchPage createLoanAccountSearchPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu();
            formParameters.setSearchString("1");
            createLoanAccountSearchPage = createLoanAccountSearchPage.navigateToCreateLoanAccountEntryPage(formParameters);
            errorMessage = "No text <Stu12332659912419 Client12332659912419:ID0002-000000030> present on the page";
            createLoanAccountSearchPage.verifyTextPresent("Stu12332659912419 Client12332659912419:ID0002-000000030", errorMessage);
            
            createLoanAccountSearchPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu();
            formParameters.setSearchString(" ");
            createLoanAccountSearchPage = createLoanAccountSearchPage.navigateToCreateLoanAccountEntryPage(formParameters);
            errorMessage = "No text <Stu1233266063395 Client1233266063395:ID0002-000000003> present on the page";
            createLoanAccountSearchPage.verifyTextPresent("Stu1233266063395 Client1233266063395:ID0002-000000003", errorMessage);
            
            createLoanAccountSearchPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu();
            formParameters.setSearchString("%");
            createLoanAccountSearchPage = createLoanAccountSearchPage.navigateToCreateLoanAccountEntryPage(formParameters);

            verify10SearchResults();
            verify25SearchResults();
            verify50SearchResults();
            verify100SearchResults();
            
            formParameters.setSearchString("UpdateCustomProperties TestClient");
            navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu().verifyNoSelectLoanProduct(formParameters, "Please select a Loan product name");
            
            createLoanAccountSearchPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu();
            formParameters.setSearchString("UpdateCustomProperties TestClient");
            CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);
            
            createLoanAccountEntryPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu().searchAndNavigateToCreateLoanAccountPage(formParameters); 
            
            createLoanAccountEntryPage.verifyAllowedAmounts("1000.0", "10000.0", "1000.0");
            createLoanAccountEntryPage.verifyDisbsursalDate("25", "3", "2011");
            
            createLoanAccountEntryPage.setAmount("");
            createLoanAccountEntryPage.setInstallments("");
            createLoanAccountEntryPage.setDisbursalDate("", "", "");
            createLoanAccountEntryPage.setInterestRate("");
            
            createLoanAccountEntryPage = createLoanAccountEntryPage.clickContinueButExpectValidationFailure();
            
            createLoanAccountEntryPage.verifyError("Please specify valid Amount. Amount should be a value between 1,000 and 10,000, inclusive.");
            createLoanAccountEntryPage.verifyError("Please specify valid Interest rate. Interest rate should be a value between 0 and 0, inclusive.");
            createLoanAccountEntryPage.verifyError("Please specify valid No. of installments. No. of installments should be a value between 1 and 10, inclusive.");
            createLoanAccountEntryPage.verifyError("You have entered an invalid disbursal date. Please check the date format.");
            
            createLoanAccountEntryPage.setDisbursalDate("26", "02", "2011");
            
            createLoanAccountEntryPage = createLoanAccountEntryPage.clickContinueButExpectValidationFailure();
            
            createLoanAccountEntryPage.verifyError("The disbursement date is invalid. Disbursement date must be on or after todays date.");
            
            createLoanAccountEntryPage.setDisbursalDate("21", "01", "2012");
            
            createLoanAccountEntryPage = createLoanAccountEntryPage.clickContinueButExpectValidationFailure();
            if(i%2 == 0) {
                createLoanAccountEntryPage.verifyNoError("The disbursement date is invalid. It must fall on a valid customer meeting schedule.");
            } 
            else {
                createLoanAccountEntryPage.verifyError("The disbursement date is invalid. It must fall on a valid customer meeting schedule.");
            }
            createLoanAccountEntryPage.setDisbursalDate("25", "3", "2011");
            createLoanAccountEntryPage.setAmount("999999999999999999.88888888");
            createLoanAccountEntryPage.setInterestRate("999999999999.88888888");
            
            createLoanAccountEntryPage = createLoanAccountEntryPage.clickContinueButExpectValidationFailure();

            createLoanAccountEntryPage.verifyError("The interest rate is invalid as the number of digits after the decimal separator exceeds the allowed number of 5.");
            createLoanAccountEntryPage.verifyError("The amount is invalid because the number of digits before the decimal separator exceeds the allowed number of 14.");
            
            createLoanAccountEntryPage.setAmount("12345678901234");
            createLoanAccountEntryPage.setInterestRate("999999999999.88888888");
            
            createLoanAccountEntryPage = createLoanAccountEntryPage.clickContinueButExpectValidationFailure();
            
            createLoanAccountEntryPage.verifyError("The interest rate is invalid as the number of digits after the decimal separator exceeds the allowed number of 5.");

            CreateLoanAccountSubmitParameters formParametersFees = new CreateLoanAccountSubmitParameters();
            formParametersFees.setAdditionalFee1("loanWeeklyFee");
            formParametersFees.setAdditionalFee2("oneTimeFee");
            
            createLoanAccountEntryPage.setAmount("5000");
            createLoanAccountEntryPage.setInterestRate("0.0");
            createLoanAccountEntryPage.setInstallments("10");
            createLoanAccountEntryPage.fillAdditionalFee(formParametersFees);
            
            CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = createLoanAccountEntryPage.clickContinue();
            
            createLoanAccountReviewInstallmentPage.verifyLoanAmount("5,000");
            createLoanAccountReviewInstallmentPage.verifyDueDate(1, "01-Apr-2011");
            createLoanAccountReviewInstallmentPage.verifyDueDate(2, "08-Apr-2011");
            createLoanAccountReviewInstallmentPage.verifyDueDate(10, "03-Jun-2011");
            
            CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndNavigateToPreviewPage();
            
            createLoanAccountPreviewPage.verifyLoanAmount("5,000");
            createLoanAccountPreviewPage.verifyInterestTypeInLoanPreview("Flat");
            createLoanAccountPreviewPage.verifyDueDate(1, "01-Apr-2011");
            createLoanAccountPreviewPage.verifyDueDate(2, "08-Apr-2011");
            createLoanAccountPreviewPage.verifyDueDate(10, "03-Jun-2011");
            
            createLoanAccountEntryPage = createLoanAccountPreviewPage.editAccountInformation();
            
            createLoanAccountEntryPage.setAmount("2000.0");
            
            createLoanAccountPreviewPage = createLoanAccountEntryPage.clickContinue().clickPreviewAndNavigateToPreviewPage();
            createLoanAccountPreviewPage.verifyLoanAmount("2,000");
            CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
            
            errorMessage = "No text <View loan account details now> present on the page";
            createLoanAccountConfirmationPage.verifyTextPresent("View loan account details now", errorMessage);
            LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
            
            loanAccountPage.verifyStatus("Application Pending Approval");
            loanAccountPage.verifyDisbursalDate("25/03/2011");
            
            String[][] accountSummaryTable = {{"", "Original Loan", "Amount paid", "Loan balance"},
                    {"Principal", "2000.0", "0.0", "2000.0"},
                    {"Interest", "0.0", "0.0", "0.0"},
                    {"Fees", "1010.0", "0.0", "1010.0"},
                    {"Penalty", "0.0", "0.0", "0.0"},
                    {"Total", "3010.0", "0.0", "3010.0"}};
            loanAccountPage.verifyAccountSummary(accountSummaryTable);
            loanAccountPage.verifyInterestRate("0.0");
            
            ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();
            
            viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(3, 1, "01-Apr-2011");
            viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(4, 1, "08-Apr-2011");
            viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(6, 1, "22-Apr-2011");
            
            loanAccountPage = viewRepaymentSchedulePage.navigateToLoanAccountPage();
            EditLoanAccountInformationPage editLoanAccountInformationPage = loanAccountPage.navigateToEditAccountInformation();
            
            editLoanAccountInformationPage.setAmount("3000.0");
            loanAccountPage = editLoanAccountInformationPage.submitAndNavigateToAccountInformationPreviewPage().submitAndNavigateToLoanAccountPage();
            
            loanAccountPage.verifyLoanAmount("3000.0");
            applicationDatabaseOperation.updateLSIM(0);
        }
    }

    private void verify10SearchResults() {
        selenium.select("name=customerSearchResults_length", "value=10");
        Assert.assertEquals(selenium.getXpathCount("//table[@id='customerSearchResults']/tbody/tr").intValue(), 10);
    }

    private void verify25SearchResults() {
        selenium.select("name=customerSearchResults_length", "value=25");
        Assert.assertEquals(selenium.getXpathCount("//table[@id='customerSearchResults']/tbody/tr").intValue(), 25);
    }

    private void verify50SearchResults() {
        selenium.select("name=customerSearchResults_length", "value=50");
        Assert.assertTrue(selenium.getXpathCount("//table[@id='customerSearchResults']/tbody/tr").intValue() > 30);
    }

    private void verify100SearchResults() {
        selenium.select("name=customerSearchResults_length", "value=100");
        Assert.assertTrue(selenium.getXpathCount("//table[@id='customerSearchResults']/tbody/tr").intValue() > 30);
    }

    private void setTime(int year, int monthOfYear, int dayOfMonth) throws UnsupportedEncodingException {

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime systemTime = new DateTime(year, monthOfYear, dayOfMonth, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(systemTime);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-121
    @Test(enabled=false)
    public void createWeeklyLoanAccountWithNonMeetingDatesForDisburseAndRepay() throws Exception {
        //Given
        setTime(2011, 02, 23);

        //When
        DefineNewLoanProductPage.SubmitFormParameters defineNewLoanProductformParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        defineNewLoanProductformParameters.setOfferingName("ProdTest121");

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233171716380 Client1233171716380");
        searchParameters.setLoanProduct(defineNewLoanProductformParameters.getOfferingName());

        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters = createSearchParameters("23","02","2011");

        EditLoanAccountStatusParameters editLoanAccountStatusParameters = new EditLoanAccountStatusParameters();
        editLoanAccountStatusParameters.setStatus(AccountStatus.LOAN_APPROVED.getStatusText());
        editLoanAccountStatusParameters.setNote("activate account");

        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters=createDisubreseLoanParameters("24","02","2011");

        loanProductTestHelper.defineNewLoanProduct(defineNewLoanProductformParameters);
        String loanId = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId();
        loanTestHelper.changeLoanAccountStatus(loanId, editLoanAccountStatusParameters);

        //Then
        loanTestHelper.disburseLoanWithWrongParams(loanId, disburseLoanParameters,"Date of transaction can not be a future date.");
        disburseLoanParameters.setDisbursalDateDD("23");
        loanTestHelper.disburseLoan(loanId, disburseLoanParameters);
        //loanTestHelper.disburseLoan(loanId, disburseLoanParameters);
        loanTestHelper.repayLoan(loanId);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-124
    @Test(enabled=false)
    public void verifyGracePeriodEffectOnLoanSchedule() throws Exception{
        //Given
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.setGracePeriodType(DefineNewLoanProductPage.SubmitFormParameters.PRINCIPAL_ONLY_GRACE);
        formParameters.setGracePeriodDuration("3");
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233266063395 Client1233266063395");
        searchParameters.setLoanProduct(formParameters.getOfferingName());

        //When / Then
        loanProductTestHelper
            .navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters)
            .verifyVariableInstalmentOptionsDefaults()
            .checkConfigureVariableInstalmentsCheckbox()
            .submitAndGotoNewLoanProductPreviewPage()
            .submit();
        
        CreateLoanAccountSubmitParameters createLoanAccountSubmitParameters = new CreateLoanAccountSubmitParameters();
        
        createLoanAccountSubmitParameters.setDd("22");
        createLoanAccountSubmitParameters.setMm("2");
        createLoanAccountSubmitParameters.setYy("2011");

        //Then
        loanTestHelper.createLoanAccount(searchParameters, createLoanAccountSubmitParameters)
            .navigateToRepaymentSchedulePage()
            .verifySchedulePrincipalWithGrace(Integer.parseInt(formParameters.getGracePeriodDuration()));
    }

    private CreateLoanAccountSubmitParameters createSearchParameters(String d, String m, String y){
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        submitAccountParameters.setDd(d);
        submitAccountParameters.setMm(m);
        submitAccountParameters.setYy(y);
        return submitAccountParameters;
    }

    private DisburseLoanParameters createDisubreseLoanParameters(String d, String m, String y){
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters.setDisbursalDateDD(d);
        disburseLoanParameters.setDisbursalDateMM(m);
        disburseLoanParameters.setDisbursalDateYYYY(y);
        disburseLoanParameters.setPaymentType(PaymentParameters.CASH);
        return disburseLoanParameters;
    }
    
    private String createLSIMLoanAndCheckAmountAndInstallmentDate(CreateLoanAccountSearchParameters searchParameters,
                                                                  CreateLoanAccountSubmitParameters submitAccountParameters, String expectedDate) {

        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        loanAccountPage.verifyLoanAmount(submitAccountParameters.getAmount());
        String loanId = loanAccountPage.getAccountId();
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        viewRepaymentSchedulePage.verifyFirstInstallmentDate(4, 2, expectedDate);
        return loanId;
    }
}