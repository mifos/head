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
import java.sql.SQLException;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loanproduct", "acceptance", "ui", "no_db_unit"})
public class DecliningPrincipleLoanTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    private final static String clientName = "Client WeeklyTue";
    LoanProductTestHelper loanProductTestHelper;
    LoanTestHelper loanTestHelper;
    CustomPropertiesHelper propertiesHelper;
    DateTime systemDateTime;
    NavigationHelper navigationHelper;
    String interestTypeName = "Declining Balance-Interest Recalculation";
    int interestType = DefineNewLoanProductPage.SubmitFormParameters.DECLINING_BALANCE_INTEREST_RECALCULATION;
    boolean isLoanProductCreatedAndVerified = false;
    String feeName = "loanWeeklyFee";
    private final static String LOAN_CLOSED = "Closed- Obligation met";
    private final static String LOAN_ACTIVE_GOOD = "Active in Good Standing";
    private final static String LOAN_ACTIVE_BAD = "Active in Bad Standing";
    static final int WEEKLY_RECURRENCE_TYPE_ID = 1;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @AfterMethod
    public void logOut() throws SQLException {
        (new MifosPage(selenium)).logout();
        applicationDatabaseOperation.updateLSIM(0);
        applicationDatabaseOperation.updateGapBetweenDisbursementAndFirstMeetingDate(1);
        propertiesHelper.setOverdueInterestPaidFirst("false");
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        propertiesHelper = new CustomPropertiesHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        loanTestHelper.setApplicationTime(systemDateTime);
        dataSetup.addDecliningPrincipalBalance();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    @Test(enabled=true)
    public void verifyDecliningPrincipleLoan() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        DefineNewLoanProductPage.SubmitFormParameters formParameters = loanProductTestHelper.defineLoanProductParameters(3, 1000, 20, interestType, WEEKLY_RECURRENCE_TYPE_ID);
        loanProductTestHelper.
                navigateToDefineNewLoanPageAndFillMandatoryFields(formParameters).
                submitAndGotoNewLoanProductPreviewPage().
                verifyInterestTypeInPreview(interestTypeName).
                submit().navigateToViewLoanDetails().
                verifyInterestTypeInSummary(interestTypeName);
        verifyDecliningPrincipalLoanAccount(3, interestTypeName, systemDateTime.plusDays(1), formParameters.getOfferingName());
    }

    @Test(enabled=true, groups={"loan"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void verifyLoanPaymentAndAdjustment() throws Exception {
        applicationDatabaseOperation.updateLSIM(1);
        applicationDatabaseOperation.updateGapBetweenDisbursementAndFirstMeetingDate(2);
        verifyEarlyExcessPayment("000100000000025");
        verifyEarlyLessPayment("000100000000026");
        verifyLateExcessPayment("000100000000027");
        verifyLateLessPayment("000100000000028");
        verifyMultipleDue("000100000000029");
        verifyOverdue();
    }

//    @Test(enabled = true)
//    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
//    public void verifyLoanPaymentAndAdjustmentWithWaiveInterest() throws Exception {
//        new FeeTestHelper(dataSetup).createPeriodicFee(feeName, FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.WEEKLY_FEE_RECURRENCE, 1, 100);
//        int noOfInstallments = 4;
//        DefineNewLoanProductPage.SubmitFormParameters formParameters = loanProductTestHelper.defineLoanProductParameters(noOfInstallments, 1000, 24, interestType);
//        createLoanProduct(formParameters);
//        verifyMultipleDue(5, formParameters.getOfferingName());
//    }


    private void verifyMultipleDue(String accountId) throws UnsupportedEncodingException {
        navigationHelper.navigateToLoanAccountPage(accountId);
        verifyPayment(accountId);
        verifyAdjustment();
    }

    private void verifyPayment(String accountId) throws UnsupportedEncodingException {
        verifyRepaymentAndAdjustment(systemDateTime.plusDays(0), systemDateTime.plusDays(5), "1,100", RepaymentScheduleData.ACCOUNT_SUMMARY_REPAYMENT_ONE, RepaymentScheduleData.ACCOUNT_SUMMARY_ADJUSTMENT_ONE, LOAN_ACTIVE_GOOD);
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(24), "403", RepaymentScheduleData.MULTIPLE_DUE_FIRST_PAYMENT);//verify first the due fee is knocked
        verifyRepaymentAndAdjustment(systemDateTime.plusDays(24), systemDateTime.plusDays(25), "1,012.8", RepaymentScheduleData.ACCOUNT_SUMMARY_REPAYMENT_TWO, RepaymentScheduleData.ACCOUNT_SUMMARY_ADJUSTMENT_TWO, LOAN_ACTIVE_BAD);
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(26), "305.1", RepaymentScheduleData.MULTIPLE_DUE_SECOND_PAYMENT);//verify first the due fee is knocked
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(29), "104.4", RepaymentScheduleData.MULTIPLE_DUE_THIRD_PAYMENT);//verify first the due fee is knocked
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(29), "200", RepaymentScheduleData.MULTIPLE_DUE_FORTH_PAYMENT);//verify first the due fee is knocked
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(29), "102.8", RepaymentScheduleData.MULTIPLE_DUE_FIFTH_PAYMENT);//same date, less payment
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(35), "112.3", RepaymentScheduleData.MULTIPLE_DUE_SIXTH_PAYMENT);//verify first the due fee is knocked
        makePaymentAndVerifyPayment(accountId, systemDateTime.plusDays(38), "291.9", RepaymentScheduleData.MULTIPLE_DUE_SEVENTH_PAYMENT);//verify first the due fee is knocked
        verifyRepaymentAndAdjustment(systemDateTime.plusDays(38), systemDateTime.plusDays(38), "0.2", null, null, LOAN_ACTIVE_BAD);
    }

    private void verifyAdjustment() throws UnsupportedEncodingException {
        verifyAdjustmentFromLoanAccountPage("291.9", RepaymentScheduleData.MULTIPLE_DUE_FIRST_ADJUSTMENT);
        verifyAdjustmentFromRepaymentSchedule("112.3", RepaymentScheduleData.MULTIPLE_DUE_SECOND_ADJUSTMENT);
        verifyAdjustmentFromInstallmentsDetails("102.8", RepaymentScheduleData.MULTIPLE_DUE_THIRD_ADJUSTMENT);
        verifyAdjustmentFromLoanAccountPage("200", RepaymentScheduleData.MULTIPLE_DUE_FORTH_ADJUSTMENT);
        verifyAdjustmentFromRepaymentSchedule("104.4", RepaymentScheduleData.MULTIPLE_DUE_FIFTH_ADJUSTMENT);
        verifyAdjustmentFromInstallmentsDetails("305.1", RepaymentScheduleData.MULTIPLE_DUE_SIXTH_ADJUSTMENT);
        verifyAdjustmentFromRepaymentSchedule("403", RepaymentScheduleData.MULTIPLE_DUE_SEVENTH_ADJUSTMENT);
    }

    private void verifyRepaymentAndAdjustment(DateTime repaymentDate, DateTime adjustmentDate, String loanAmount, String[][] repaymentAccountSummery, String[][] adjustedAccountSummery, String loanStatus) throws UnsupportedEncodingException {
        loanTestHelper.repayLoan(repaymentDate).
                verifyLoanStatus(LOAN_CLOSED).verifyAccountSummary(repaymentAccountSummery);
        loanTestHelper.setApplicationTime(adjustmentDate).navigateBack();
        new LoanAccountPage(selenium).navigateToApplyAdjustment().
                verifyRepayAdjustment(loanAmount).verifyLoanStatus(loanStatus).verifyAccountSummary(adjustedAccountSummery);
    }

    private void verifyAdjustmentFromInstallmentsDetails(String adjustedAmount, String[][] adjustedSchedule) {
        new LoanAccountPage(selenium).
                navigateToViewNextInstallmentDetails().
                navigateToApplyAdjustment().
                verifyAdjustment(adjustedAmount).
                navigateToRepaymentSchedulePage().
                verifyScheduleTable(adjustedSchedule).navigateToLoanAccountPage();
    }

    private void verifyAdjustmentFromRepaymentSchedule(String adjustedAmount, String[][] adjustedSchedule) {
        new LoanAccountPage(selenium).
                navigateToRepaymentSchedulePage().
                navigateToApplyAdjustment().
                verifyAdjustment(adjustedAmount).
                navigateToRepaymentSchedulePage().
                verifyScheduleTable(adjustedSchedule).navigateToLoanAccountPage();
    }

    private void verifyAdjustmentFromLoanAccountPage(String adjustedAmount, String[][] adjustedSchedule) {
        new LoanAccountPage(selenium).
                navigateToApplyAdjustment().
                verifyAdjustment(adjustedAmount).
                navigateToRepaymentSchedulePage().
                verifyScheduleTable(adjustedSchedule).navigateToLoanAccountPage();
    }

    private void verifyLateLessPayment(String accountId) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(12);
        navigationHelper.navigateToLoanAccountPage(accountId);
        makePaymentAndVerifyPayment(accountId, paymentDate, "100", RepaymentScheduleData.LATE_LESS_FIRST_PAYMENT);//verify first the due fee is knocked
        makePaymentAndVerifyPayment(accountId, paymentDate, "5.3", RepaymentScheduleData.LATE_LESS_SECOND_PAYMENT);//verify due interest is knocked next
        makePaymentAndVerifyPayment(accountId, paymentDate, "100", RepaymentScheduleData.LATE_LESS_THIRD_PAYMENT);//verify the due principle is knocked next
        new LoanAccountPage(selenium).navigateToApplyPayment().verifyPaymentPriorLastPaymentDate(loanTestHelper.setPaymentParams("10", paymentDate));
    }

    private void verifyLateExcessPayment(String accountId) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(12);
        navigationHelper.navigateToLoanAccountPage(accountId);
        makePaymentAndVerifyPayment(accountId, paymentDate, "354", RepaymentScheduleData.LATE_EXCESS_PAYMENT);
        makePaymentAndVerifyPayment(accountId, paymentDate, "2.5", RepaymentScheduleData.LATE_EXCESS_SECOND_PAYMENT);//verifying only overdue interest in knocked
        makePaymentAndVerifyPayment(accountId, paymentDate, "100", RepaymentScheduleData.LATE_EXCESS_THIRD_PAYMENT);//verify if future interest in reduced as the future principle is paid
    }

    private void verifyEarlyLessPayment(String accountId) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(1);
        navigationHelper.navigateToLoanAccountPage(accountId);
        makePaymentAndVerifyPayment(accountId, paymentDate, "100", RepaymentScheduleData.EARLY_LESS_FIRST_PAYMENT); //verifying interest till date
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")    // one of the dependent methods throws Exception
    public void createLoan() throws Exception {
        DateTime testDateTime = new DateTime(2011, 02, 22, 10, 0, 0, 0);
        loanTestHelper.setApplicationTime(testDateTime);
        createAndDisburseLoanAccount(4, testDateTime, "WeeklyClientFlatLoanWithNoFee");
    }

    private void verifyEarlyExcessPayment(String accountID) throws UnsupportedEncodingException {
        DateTime paymentDate = systemDateTime.plusDays(1);
        navigationHelper.navigateToLoanAccountPage(accountID);
        makePaymentAndVerifyPayment(accountID, paymentDate, "280", RepaymentScheduleData.EARLY_EXCESS_FIRST_PAYMENT);
    }
    
    private void verifyOverdue() throws UnsupportedEncodingException {
        propertiesHelper.setOverdueInterestPaidFirst("true");
        DateTime testDateTime = new DateTime(2010, 10, 12, 0, 0, 0, 0);
        loanTestHelper.setApplicationTime(testDateTime);
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setDisbursalDateDD("12");
        disburseParams.setDisbursalDateMM("10");
        disburseParams.setDisbursalDateYYYY("2010");
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        LoanAccountPage loanAccountPage = 
                loanTestHelper.createLoanAccount(clientName, "WeeklyPawdepLoan")
                .changeAccountStatusToAccepted().disburseLoan(disburseParams);
        String accountId = loanAccountPage.getAccountId();
        loanTestHelper.setApplicationTime(testDateTime.plusWeeks(5));
        verifyOverdueSummary(accountId);
        verifyOverduePayment(accountId, testDateTime);
    }
    
    private void verifyOverdueSummary(String accountId) throws UnsupportedEncodingException {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(accountId);
        loanAccountPage.verifyAccountSummary(RepaymentScheduleData.ACCOUNT_SUMMARY_OVERDUE);
    }
    
    private void verifyOverduePayment(String accountId, DateTime disbursementDate) throws UnsupportedEncodingException {
        navigationHelper.navigateToLoanAccountPage(accountId);
        loanTestHelper.makePayment(disbursementDate.plusWeeks(5), "1023.5").verifyAccountSummary(
                RepaymentScheduleData.ACCOUNT_SUMMARY_OVERDUE_REPAYMENT);
    }

    private String makePaymentAndVerifyPayment(String accountId, DateTime paymentDate, String paymentAmount, String[][] expectedSchedule) throws UnsupportedEncodingException {
        loanTestHelper.makePayment(paymentDate, paymentAmount).
                navigateToRepaymentSchedulePage().
                verifyScheduleTable(expectedSchedule).navigateToLoanAccountPage();
        return accountId;
    }

    private LoanAccountPage createAndDisburseLoanAccount(int noOfInstallments, DateTime disbursalDate, String loanProductName) throws UnsupportedEncodingException {
        DisburseLoanParameters disburseLoanParameters = loanTestHelper.setDisbursalParams(disbursalDate);
        navigationHelper.navigateToHomePage();
        return loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(noOfInstallments).
                clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().
                submit().navigateToLoanAccountDetailsPage().
                navigateToEditAccountStatus().
                submitAndNavigateToNextPage(loanTestHelper.setApprovedStatusParameters()).
                submitAndNavigateToLoanAccountPage().
                navigateToDisburseLoan().
                submitAndNavigateToDisburseLoanConfirmationPage(disburseLoanParameters)
                .submitAndNavigateToLoanAccountPage().navigateToApplyCharge().applyFeeAndConfirm(setCharge());
    }

    private ChargeParameters setCharge() {
        ChargeParameters chargeParameters = new ChargeParameters();
        chargeParameters.setType(feeName);
        return chargeParameters;
    }

    private void verifyDecliningPrincipalLoanAccount(int noOfInstallments, String interestTypeName, DateTime disbursalDate, String loanProductName) {
        navigationHelper.navigateToHomePage();
        loanTestHelper.
                navigateToCreateLoanAccountEntryPageWithoutLogout(clientName, loanProductName).
                setDisbursalDate(disbursalDate).
                setInstallments(noOfInstallments).
                verifyInterestTypeInLoanCreation(interestTypeName).
                clickContinue().
                clickPreviewAndGoToReviewLoanAccountPage().
                verifyInterestTypeInLoanPreview(interestTypeName).
                submit().navigateToLoanAccountDetailsPage().
                verifyInterestTypeInLoanAccountDetails(interestTypeName).
                navigateToRepaymentSchedulePage().
                verifyScheduleDateField();
    }

}

