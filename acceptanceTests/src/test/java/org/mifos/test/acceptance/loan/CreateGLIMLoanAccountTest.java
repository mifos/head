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

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationPage;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountInformationParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.EditPreviewLoanAccountPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewOriginalSchedulePage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
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
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateGLIMLoanAccountTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        applicationDatabaseOperation.updateGLIM(1);
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void logOut() throws SQLException {
        applicationDatabaseOperation.updateGLIM(0);
        (new MifosPage(selenium)).logout();
    }

    /*
     * This test is to verify that you can edit a GLIM loan account after it has been
     * dibursed without getting an invalid disbursal date error. See MIFOS-2597.
     */
    // http://mifosforge.jira.com/browse/MIFOSTEST-132
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=false) //TODO http://mifosforge.jira.com/browse/MIFOS-5081
    public void checkGLIMLoanCreatedBySubmitForApproval() throws Exception {
        //Given
        applicationDatabaseOperation.updateGLIM(1);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        //When
        ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
        CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
        CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
        formParameters.setSearchString("Default Group");
        formParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);
        createLoanAccountEntryPage.navigateToReviewInstallmentsPage();
        verifyGLIMErrorMessage();
        createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 3, 04, 15, 0, 0, 0));
        createLoanAccountEntryPage.selectGLIMClients(0,"Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "9999.9", "0009-Horse");
        createLoanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "9999.9", "0001-Cow Purchase");
        createLoanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "9999.9", "0003-Goat Purchase");
        createLoanAccountEntryPage.selectGLIMClients(3, "Holiday TestClient Client Id: 0002-000000023", "9999.9", "0003-Goat Purchase");
        selectAdditionalFees();
        CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = createLoanAccountEntryPage.navigateToReviewInstallmentsPage();
        verifyFirstInstallmentAndDisbursalDateOnReviewPage();
        verifyAdditionalFeesOnReviewPage();
        CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
        verifyFirstInstallmentAndDisbursalDateOnPreviewPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
        String loanId = loanAccountPage.getAccountId();
        loanAccountPage.verifyLoanIsPendingApproval();
        loanAccountPage.verifyNumberOfInstallments("4");
        loanAccountPage.verifyDisbursalDate("04/03/2011");
        loanAccountPage.verifyPrincipalOriginal("39999.6");
        loanAccountPage.verifyLoanTotalBalance("41147.0");
        loanAccountPage.verifyFeesOriginal("410.0");
        loanAccountPage.verifyInterestOriginal("737.4");
        verifyFees();
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(3, 1, "11-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(4, 1, "18-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(5, 1, "25-Mar-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableDueDate(6, 1, "01-Apr-2011");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(3, 3, "9999.9");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(4, 3, "9999.9");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(5, 3, "9999.9");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(6, 3, "9999.9");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(3, 5, "110.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(4, 5, "100.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(5, 5, "100.0");
        viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(6, 5, "100.0");
        viewRepaymentSchedulePage.navigateToLoanAccountPage();
        loanId = loanAccountPage.getAccountId();
        dateTimeUpdaterRemoteTestingService.setDateTime(new LocalDate(2011, 3, 8).toDateTimeAtStartOfDay());
        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Test");
        loanTestHelper.changeLoanAccountStatus(loanId, statusParameters);
        DisburseLoanParameters params = new DisburseLoanParameters();
        params.setDisbursalDateDD("8");
        params.setDisbursalDateMM("3");
        params.setDisbursalDateYYYY("2011");
        params.setPaymentType(DisburseLoanParameters.CASH);
        loanTestHelper.disburseLoan(loanId, params);
        dateTimeUpdaterRemoteTestingService.setDateTime(new LocalDate(2011, 3, 15).toDateTimeAtStartOfDay());
        EditLoanAccountInformationParameters editLoanAccountInformationParameters = new EditLoanAccountInformationParameters();
        editLoanAccountInformationParameters.setExternalID("ID2323ID");
        loanTestHelper.changeLoanAccountInformation(loanId, new CreateLoanAccountSubmitParameters(), editLoanAccountInformationParameters);
        applicationDatabaseOperation.updateGLIM(0);
    }

    private void verifyFirstInstallmentAndDisbursalDateOnReviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary']/div[3]/div[2]"), ("04-Mar-2011"));
        Assert.assertEquals(selenium.getTable("installments.1.1"), ("11/03/11"));
    }
            
    private void verifyFirstInstallmentAndDisbursalDateOnPreviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[4]/div[2]"), ("04-Mar-2011"));
        Assert.assertEquals(selenium.getTable("installments.1.1"), ("11-Mar-2011"));
    }
         
    private void verifyFees() {
        Assert.assertTrue(selenium.isTextPresent("loanWeeklyFee: 100.0 ( Recur every 1 week(s))"));
        Assert.assertTrue(selenium.isTextPresent("oneTimeFee: 10.0"));
    }
            
    private void verifyAdditionalFeesOnReviewPage(){
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[1]/div[1]"), ("oneTimeFee"));
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[1]/div[2]"), ("10 Periodicity: One Time Frequency: Upfront"));
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[2]/div[1]"), ("loanWeeklyFee"));
        Assert.assertEquals(selenium.getText("xpath=//div[@class='product-summary'][2]/div[2]/div[2]"), ("100 Periodicity: 1 week(s)"));
    }
            
    private void selectAdditionalFees() {
        selenium.select("selectedFeeId0", "label=loanWeeklyFee");
        selenium.type("selectedFeeId0Amount", "100");
    }
            
    private void verifyGLIMErrorMessage() {
        Assert.assertTrue(selenium.isTextPresent("You must select at least two individual members in the \"Individual Details\" section in order to successfully create a loan."));
    }
    
    // http://mifosforge.jira.com/browse/MIFOSTEST-133
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkGLIMLoanCreatedBySaveForLater() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        loanTestHelper.createLoanAccountForMultipleClientsInGroup(false);
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-134
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyLoanAccountCreationPipelineWhenGlimIsEnabled() throws Exception {
        //Given
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Stu1233266063395 Client1233266063395");
        searchParameters.setLoanProduct("ClientEmergencyLoan");
        String[] locators = {   "name=prdOfferingId",
                                "loancreationdetails.input.sumLoanAmount",
                                "loancreationdetails.input.interestRate",
                                "loancreationdetails.input.numberOfInstallments",
                                "disbursementDateDD",
                                "disbursementDateMM",
                                "disbursementDateYY",
                                "name=loanOfferingFund",
                                "name=businessActivityId",
                                "name=collateralTypeId",
                                "name=collateralNote",
                                "name=externalId",
                                "name=selectedFee[0].feeId",
                                "name=selectedFee[0].amount",
                                "name=selectedFee[1].feeId",
                                "name=selectedFee[1].amount",
                                "name=selectedFee[2].feeId",
                                "name=selectedFee[2].amount",
                                "loancreationdetails.button.continue",
                                "loancreationdetails.button.cancel"
                            };
        //When / Then
        loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters).verifyAllElementsArePresent(locators);
   }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = true)
    public void newWeeklyGLIMAccount() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "301", "0012-Sheep Purchase");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "401", "0012-Sheep Purchase");
        
        //MIFOS-5703
        applicationDatabaseOperation.updateLSIM(1);
        EditLoanAccountInformationPage editPage = loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPage()
                .navigateToLoanAccountDetailsPage().navigateToEditAccountInformation();
        editPage.verifyRepaymentDayAccessibility(true);
        editPage.setWeekFrequency("5");
        
            
        LoanAccountPage loanPage = editPage.submitAndNavigateToAccountInformationPreviewPage().submitAndNavigateToLoanAccountPage();
        loanPage.verifyFrequencyInstallments("5");
        
        EditLoanAccountStatusParameters statusParams = new EditLoanAccountStatusParameters();
        statusParams.setNote("GLIM test");
        statusParams.setStatus(EditLoanAccountStatusParameters.APPROVED);
        loanPage.changeAccountStatus(statusParams);
        editPage = loanPage.navigateToEditAccountInformation();
        editPage.verifyRepaymentDayAccessibility(false);
        editPage.navigateBack();
        
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setDisbursalDateDD("1");
        disburseParams.setDisbursalDateMM("03");
        disburseParams.setDisbursalDateYYYY("2011");
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        loanPage.disburseLoan(disburseParams);
        
        editPage = loanPage.navigateToEditAccountInformation();
        editPage.verifyRepaymentDayAccessibility(false);
        applicationDatabaseOperation.updateLSIM(0);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = true)
    public void newMonthlyGLIMAccount() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("MonthlyGroupFlatLoan1stOfMonth");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "3010", "0012-Sheep Purchase");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "4010", "0012-Sheep Purchase");
        loanAccountEntryPage.setDisbursalDate("1", "3","2011");
        EditLoanAccountInformationPage editPage = loanAccountEntryPage.submitAndNavigateToGLIMLoanAccountConfirmationPage()
                .navigateToLoanAccountDetailsPage().navigateToEditAccountInformation();
        editPage.setDayOfMonth("56");
        editPage.submitWithErrors();
        editPage.verifyErrorInForm("Repayment day format is not valid");
        applicationDatabaseOperation.updateLSIM(0);
    }

    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkGLIMAccountTotalCalculationWithDecimal() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "9999.9");
        loanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "99999.9");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "99999.9");
        loanAccountEntryPage.checkTotalAmount("209999.7");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkGLIMAccountTotalCalculationWholeNumber() throws Exception {
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "9999");
        loanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "99999");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "99999");
        loanAccountEntryPage.checkTotalAmount("209997.0");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void checkGLIMAccountEmptyPurpose() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "9999", "0012-Sheep Purchase");
        loanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "99999");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "99999");
                
        CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = loanAccountEntryPage.navigateToReviewInstallmentsPage();
        CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
        CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
        LoanAccountPage loanAccountPage = createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
                
        String[] purposes = {"0012-Sheep Purchase", "-", "-"};
        loanAccountPage.verifyGLIMPurpose(purposes);
        
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, true);
        
        EditPreviewLoanAccountPage previewLoanAccountPage = loanAccountPage.navigateToEditAccountInformation().submitAndNavigateToAccountInformationPreviewPage();
        previewLoanAccountPage.verifyGLIMPurpose(purposes);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void checkGLIMIndividualSchedule() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 1, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        searchParameters.setSearchString("Default Group");
        searchParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");
        CreateLoanAccountEntryPage loanAccountEntryPage = loanTestHelper.navigateToCreateLoanAccountEntryPage(searchParameters);
        loanAccountEntryPage.selectGLIMClients(0, "Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "9999", "0012-Sheep Purchase");
        loanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "99999");
        loanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "99999");
                
        LoanAccountPage loanAccountPage = loanAccountEntryPage.navigateToReviewInstallmentsPage()
                .clickPreviewAndGoToReviewLoanAccountPage()
                .submitForApprovalAndNavigateToConfirmationPage()
                .navigateToLoanAccountDetailsPage();

        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, true);

        EditLoanAccountStatusParameters statusParams = new EditLoanAccountStatusParameters();
        statusParams.setNote("GLIM test");
        statusParams.setStatus(EditLoanAccountStatusParameters.APPROVED);
        
        loanAccountPage.changeAccountStatus(statusParams);
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, true);
        String loanId = loanAccountPage.getAccountId();
        
        dateTimeUpdaterRemoteTestingService.setDateTime(new LocalDate(2011, 3, 8).toDateTimeAtStartOfDay());
        
        loanAccountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setAmount("209997.0");
        disburseParams.setDisbursalDateDD("8");
        disburseParams.setDisbursalDateMM("3");
        disburseParams.setDisbursalDateYYYY("2011");
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        
        loanAccountPage.disburseLoan(disburseParams);
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, false);
        totalGroupLoanEqualSumOfindividual(loanAccountPage, 3, 1.0);
        
        PaymentParameters paymentParams = new PaymentParameters();
        paymentParams.setAmount("1500.0");
        paymentParams.setTransactionDateDD("8");
        paymentParams.setTransactionDateMM("3");
        paymentParams.setTransactionDateYYYY("2011");
        paymentParams.setPaymentType(PaymentParameters.CASH);
        
        loanTestHelper.applyPayment(loanId, paymentParams);
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, false);
        totalGroupLoanEqualSumOfindividual(loanAccountPage, 3, 1.0);
        
        String[] expectedPrincipalAmounts = new String[] { "24.9", "249.2", "249.2" };
        String[] expectedInterestAmounts = new String[] { "46", "460.3", "460.3" };
        String[] expectedFeeAmounts = new String[] { "0.5", "4.8", "4.8" };
        verifyProRatedGLIMPayments(loanAccountPage, 3, expectedPrincipalAmounts, expectedInterestAmounts, expectedFeeAmounts);

        loanTestHelper.repayLoan(loanId);
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, false);
        totalGroupLoanEqualSumOfindividual(loanAccountPage, 3, 1.0);
        
        loanAccountPage.navigateToApplyAdjustment().submitAdjustment();
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, false);
        totalGroupLoanEqualSumOfindividual(loanAccountPage, 3, 1.0);
        
        statusParams.setNote("GLIM test");
        statusParams.setStatus(EditLoanAccountStatusParameters.CLOSED_WRITTEN_OFF);
        
        loanAccountPage.changeAccountStatus(statusParams);
        loanAccountPage.verifyGLIMIndividualScheduleLinks(3, false);
        totalGroupLoanEqualSumOfindividual(loanAccountPage, 3, 1.0);
    }
    
    private void totalGroupLoanEqualSumOfindividual(LoanAccountPage page, int count, double delta) {
        ViewOriginalSchedulePage originalSchedulePage = page.navigateToRepaymentSchedulePage().navigateToViewOriginalSchedulePage();
        double[][] installments = originalSchedulePage.getInstallments(delta);
        originalSchedulePage.returnToRepaymentSchedule().navigateToLoanAccountPage();
        
        for(int i = 0; i < count; ++i) {
            ViewOriginalSchedulePage individualSchedulePage = page.navigateToIndividualOriginalSchedulePage(i + 2);
            double[][] individual = individualSchedulePage.getInstallments(delta);
            individualSchedulePage.returnToLoanAccountDetail();
            
            for(int j = 0; j < installments.length; ++j) {
                installments[j][0] -= individual[j][0];
                installments[j][1] -= individual[j][1];
                installments[j][2] -= individual[j][2];
                installments[j][3] -= individual[j][3];
            }
        }
        
        for (int i = 0; i < installments.length; ++i) {
            Assert.assertEquals(installments[i][0], 0, delta);
            Assert.assertEquals(installments[i][1], 0, delta);
            Assert.assertEquals(installments[i][2], 0, delta);
            Assert.assertEquals(installments[i][3], 0, delta);
        }
    }

    private void verifyProRatedGLIMPayments(LoanAccountPage loanAccountPage, int count, String[] expectedPrincipalAmounts, String[] expectedInterestAmounts,
            String[] expectedFeeAmounts) {
        for (int i = 0; i < count; i++) {
            ViewRepaymentSchedulePage repaymentSchedulePage = loanAccountPage.navigateToIndividualRepaymentSchedulePage(i);
            repaymentSchedulePage.verifyInstallmentAmount(ViewRepaymentSchedulePage.FIRST_ROW, ViewRepaymentSchedulePage.PRINCIPAL_COLUMM, expectedPrincipalAmounts[i]);
            repaymentSchedulePage.verifyInstallmentAmount(ViewRepaymentSchedulePage.FIRST_ROW, ViewRepaymentSchedulePage.INTEREST_COLUMN, expectedInterestAmounts[i]);
            repaymentSchedulePage.verifyInstallmentAmount(ViewRepaymentSchedulePage.FIRST_ROW, ViewRepaymentSchedulePage.FEE_COLUMN, expectedFeeAmounts[i]);
            loanAccountPage = repaymentSchedulePage.navigateToLoanAccountPage();
        }
    }
}
