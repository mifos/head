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
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.ApplyAdjustmentPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.TransactionHistoryPage;
import org.mifos.test.acceptance.framework.loan.ViewNextInstallmentDetailsPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.SQLException;


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class RedoLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    LoanProductTestHelper loanProductTestHelper;
    DateTime systemDateTime;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2012, 02, 22, 15, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() throws SQLException {
        applicationDatabaseOperation.updateGLIM(0);
        applicationDatabaseOperation.updateLSIM(0);
        (new MifosPage(selenium)).logout();
    }

    /**
     * Verify whether a loan can be redone on a past date with GLIM and LSIM turned on.
     * http://mifosforge.jira.com/browse/MIFOSTEST-18
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanOnPastDateWithLSIMAndGLIM() throws Exception {
        applicationDatabaseOperation.updateGLIM(1);
        applicationDatabaseOperation.updateLSIM(1);
        RedoLoanDisbursalParameters paramsPastDate = new RedoLoanDisbursalParameters();
        paramsPastDate.setDisbursalDateDD("21");
        paramsPastDate.setDisbursalDateMM("02");
        paramsPastDate.setDisbursalDateYYYY("2012");
        paramsPastDate.addClient(1, "3000.0", "0009-Horse");
        paramsPastDate.addClient(2, "3000.0", "0001-Cow Purchase");
        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursalWithGLIMandLSIM("MyGroup1233266297718", "GroupEmergencyLoan", paramsPastDate);
        loanAccountPage.verifyStatus(LoanAccountPage.ACTIVE);
        loanAccountPage.verifyPrincipalOriginal("6000.0");
    }

    /*
     * Verify a redone loan directly moves into "Closed-Met Obligation"
     * state when the loan is wholly paid off before the current date.
     *
     * http://mifosforge.jira.com/browse/MIFOSTEST-28
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalWithPastDate() throws Exception {
        RedoLoanDisbursalParameters paramsPastDate = new RedoLoanDisbursalParameters();
        paramsPastDate.setDisbursalDateDD("25");
        paramsPastDate.setDisbursalDateMM("02");
        paramsPastDate.setDisbursalDateYYYY("2011");
        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal("MyGroup1233266297718", "WeeklyGroupFlatLoanWithOnetimeFee", paramsPastDate, null, 0, false);
        verifyRedoLoanDisbursalWithPastDate(loanAccountPage);
    }

    private void verifyRedoLoanDisbursalWithPastDate(LoanAccountPage loanAccountPage) {
        loanAccountPage.verifyStatus("Closed- Obligation met");
        //   loanAccountPage.verifyTotalOriginalLoan("4290.0");
        //    loanAccountPage.verifyTotalAmountPaid("4290.0");
        //   loanAccountPage.verifyLoanTotalBalance("0.0");
        TransactionHistoryPage transactionHistoryPage = loanAccountPage.navigateToTransactionHistory();
        transactionHistoryPage.verifyTransactionHistory(1028.6, 4, 217);
    }

    /*
     * Verify that the status of the loan is Active in Good Standing
     * when the loan is not wholly paid off before current date.
     * Also verifies that loan cannot be redone on a date equal to
     * or greater than the current date.
     *
     * http://mifosforge.jira.com/browse/MIFOSTEST-15
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalWithPastDateUnpaid() throws Exception {
        // Testing redo loan
        RedoLoanDisbursalParameters paramsPastDate = new RedoLoanDisbursalParameters();
        paramsPastDate.setDisbursalDateDD("25");
        paramsPastDate.setDisbursalDateMM("02");
        paramsPastDate.setDisbursalDateYYYY("2011");
        paramsPastDate.setLoanAmount("3000.0");
        paramsPastDate.setInterestRate("10");
        paramsPastDate.setNumberOfInstallments("52");
        RedoLoanDisbursalParameters paramsCurrentDate = new RedoLoanDisbursalParameters();
        paramsCurrentDate.setDisbursalDateDD("22");
        paramsCurrentDate.setDisbursalDateMM("2");
        paramsCurrentDate.setDisbursalDateYYYY("2012");
        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal("MyGroup1233266297718", "WeeklyGroupFlatLoanWithOnetimeFee", paramsPastDate, paramsCurrentDate, 0, true);
        loanAccountPage.verifyStatus("Active in Good Standing");
        loanAccountPage.verifyPerformanceHistory("51", "0");
        // Testing multiple reverse payments
        String payAmount = "63.0";
        String reverseNote = "Reversed ";
        int loanBalance = (int) (Float.parseFloat(loanAccountPage.getTotalBalance()) + 63 * 3);
        for (int i = 0; i < 3; i++) {
            ApplyAdjustmentPage applyAdjustmentPage = loanAccountPage.navigateToApplyAdjustment();
            applyAdjustmentPage.verifyAdjustment(payAmount, reverseNote + (i + 1));
        }
        verifyMultipleReversePayments(loanAccountPage, payAmount, reverseNote, loanBalance);
    }

    private void verifyMultipleReversePayments(LoanAccountPage loanAccountPage, String payAmount, String reverseNote, int loanBalance) {
        TransactionHistoryPage transactionHistoryPage = loanAccountPage.navigateToTransactionHistory();
        transactionHistoryPage.verifyTableForReversedValues(payAmount, 3, reverseNote);
        transactionHistoryPage.navigateBack();

        ViewNextInstallmentDetailsPage installmentPage = loanAccountPage.navigateToViewNextInstallmentDetails();
        installmentPage.verifyInstallmentAmount(6, 2, "0.0");
        installmentPage.verifyInstallmentAmount(12, 2, "0.0");
        installmentPage.navigateBack();

        ViewRepaymentSchedulePage repaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(51, 0, "Installments due");
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(52, 6, "63.0");
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(53, 6, "63.0");
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(54, 6, "63.0");
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(55, 0, "Future Installments");
        repaymentSchedulePage.navigateBack();

        loanAccountPage.verifyLoanTotalBalance(loanBalance + ".0");
        loanAccountPage.verifyPerformanceHistory("48", "3");
        loanAccountPage.verifyStatus("Active in Bad Standing");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void redoLoanDisbursalForVariableInstallmentLoan() throws Exception {
        dataSetUpForVariableInstallmentLoan();
        applicationDatabaseOperation.updateLSIM(1);
        String[] invalidFees = getInvalidFees();
        int interest = 24;
        int noOfInstallments = 5;
        int loanAmount = 1000;
        String maxGap = "10";
        String minGap = "1";
        String minInstalmentAmount = "100";
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = setLoanParams(systemDateTime, interest, noOfInstallments, loanAmount);

        loanTestHelper.setApplicationTime(systemDateTime.plusDays(14));
        verifyRedoLoanForVariableInstallmentLoan(noOfInstallments, invalidFees, redoLoanDisbursalParameters, maxGap, minGap, minInstalmentAmount);
        verifyPaidFieldsForVariableInstalment(redoLoanDisbursalParameters);
    }

    private void verifyPaidFieldsForVariableInstalment(RedoLoanDisbursalParameters redoLoanDisbursalParameters) {
        navigateToRedoLoanPage().
                submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(redoLoanDisbursalParameters).
                clickPreviewAndGoToReviewLoanAccountPage().
                verifyRunningBalance(RedoLoanScheduleData.VARIABLE_LOAN_SCHEDULE_ONE, RedoLoanScheduleData.VARIABLE_LOAN_RUNNING_BALANCE_ONE).editSchedule().
                setPaidField(RedoLoanScheduleData.VARIABLE_LOAN_PAYMENT_2).clickPreviewAndGoToReviewLoanAccountPage().
                verifyRunningBalance(RedoLoanScheduleData.VARIABLE_LOAN_SCHEDULE_2, RedoLoanScheduleData.VARIABLE_LOAN_RUNNING_BALANCE_2);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled = false)
    public void redoLoanDisbursalForDecliningBalanceLoan() throws Exception {
        dataSetUpForVariableInstallmentLoan();
        applicationDatabaseOperation.updateLSIM(1);
        String[] fees = getInvalidFees();
        int interest = 24;
        int noOfInstallments = 5;
        int loanAmount = 1000;
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = setLoanParams(systemDateTime, interest, noOfInstallments, loanAmount);

        loanTestHelper.setApplicationTime(systemDateTime.plusDays(14));
        verifyRedoLoanPreview(fees, redoLoanDisbursalParameters, RedoLoanScheduleData.DECLINING_PRINCIPAL_LATE_PAYMENT_1, RedoLoanScheduleData.DECLINING_PRINCIPAL_LATE_SCHEDULE_1, RedoLoanScheduleData.DECLINING_PRINCIPAL_LATE_BALANCE_1);
        verifyRedoLoanPreview(fees, redoLoanDisbursalParameters, RedoLoanScheduleData.DECLINING_PRINCIPAL_EARLY_PAYMENT_1, RedoLoanScheduleData.DECLINING_PRINCIPAL_EARLY_SCHEDULE_1, RedoLoanScheduleData.DECLINING_PRINCIPAL_EARLY_BALANCE_1);
        loanTestHelper.setApplicationTime(systemDateTime.plusDays(36));
        verifyRedoLoanPreview(fees, redoLoanDisbursalParameters, RedoLoanScheduleData.DECLINING_PRINCIPAL_ENTIRE_PAYMENT_1, RedoLoanScheduleData.DECLINING_PRINCIPAL_ENTIRE_SCHEDULE_1, RedoLoanScheduleData.DECLINING_PRINCIPAL_ENTIRE_BALANCE_1);
    }

    private void verifyRedoLoanPreview(String[] fees, RedoLoanDisbursalParameters redoLoanDisbursalParameters, String[][] payment, String[][] schedule, String[][] balance) {
        navigateToRedoLoanPage().
                selectFee(new String[]{fees[0]}).
                submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(redoLoanDisbursalParameters).
                setPaidField(payment).clickPreviewAndGoToReviewLoanAccountPage().
                verifyRunningBalance(schedule, balance);
    }

    private RedoLoanDisbursalEntryPage navigateToRedoLoanPage() {
        return navigationHelper.
                navigateToAdminPage().navigateToRedoLoanDisbursal().
                searchAndNavigateToRedoLoanDisbursalPage("Stu1233171716380").
                navigateToRedoLoanDisbursalChooseLoanProductPage("Stu1233171716380").
                submitAndNavigateToRedoLoanDisbursalEntryPage("WeeklyFlatLoanWithOneTimeFees");
    }

    private void verifyRedoLoanForVariableInstallmentLoan(int noOfInstallments, String[] invalidFees, RedoLoanDisbursalParameters redoLoanDisbursalParameters, String maxGap, String minGap, String minInstalmentAmount) {
        DateTime disbursalDate = systemDateTime;
        navigateToRedoLoanPage().
                verifyFeeBlockedForVariableInstallmentLoan(invalidFees).
                submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(redoLoanDisbursalParameters).
                validateRepaymentScheduleFieldDefault(noOfInstallments).
                validateDateFieldValidations(disbursalDate, minGap, maxGap, noOfInstallments).
                verifyInstallmentTotalValidations(noOfInstallments, minInstalmentAmount, disbursalDate, minGap).
                verifyValidData(noOfInstallments, minGap, minInstalmentAmount, disbursalDate, maxGap).
                verifyRecalculationWhenDateAndTotalChange();

        //verify fee, after creation
        //date picker
        //holiday
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void dataSetUpForVariableInstallmentLoan() throws Exception {
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        systemDateTime = new DateTime(2010, 10, 11, 10, 0, 0, 0);
        TestDataSetup dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);
        loanTestHelper.setApplicationTime(systemDateTime);
        dataSetup.addDecliningPrincipalBalance();
    }

    private RedoLoanDisbursalParameters setLoanParams(ReadableInstant validDisbursalDate, int interest, int noOfInstallments, int loanAmount) {
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = new RedoLoanDisbursalParameters();
        redoLoanDisbursalParameters.setInterestRate(String.valueOf(interest));
        redoLoanDisbursalParameters.setNumberOfInstallments(String.valueOf(noOfInstallments));
        redoLoanDisbursalParameters.setLoanAmount(String.valueOf(loanAmount));
        redoLoanDisbursalParameters.setDisbursalDateDD(DateTimeFormat.forPattern("dd").print(validDisbursalDate));
        redoLoanDisbursalParameters.setDisbursalDateMM(DateTimeFormat.forPattern("MM").print(validDisbursalDate));
        redoLoanDisbursalParameters.setDisbursalDateYYYY(DateTimeFormat.forPattern("yyyy").print(validDisbursalDate));
        return redoLoanDisbursalParameters;
    }

    private String[] getInvalidFees() throws SQLException {
        return new String[]{"loanWeeklyFee", "fixedFeePerInterest", "fixedFeePerAmountAndInterest"};
    }

}
