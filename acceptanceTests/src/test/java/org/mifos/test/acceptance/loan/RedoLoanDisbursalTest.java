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
import org.joda.time.ReadableInstant;
import org.joda.time.format.DateTimeFormat;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.ApplyAdjustmentPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSchedulePreviewPage;
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


@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
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
        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursalWithGLIMandLSIM("Default Group", "GroupEmergencyLoan", paramsPastDate);
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
        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal("Default Group", "WeeklyGroupFlatLoanWithOnetimeFee", paramsPastDate, null, 0, false);
        verifyRedoLoanDisbursalWithPastDate(loanAccountPage);
    }

    private void verifyRedoLoanDisbursalWithPastDate(LoanAccountPage loanAccountPage) {
        loanAccountPage.verifyStatus("Closed- Obligation met");
        loanAccountPage.verifyTotalOriginalLoan("1029.0");
        loanAccountPage.verifyTotalAmountPaid("1029.0");
        loanAccountPage.verifyLoanTotalBalance("0.0");
        TransactionHistoryPage transactionHistoryPage = loanAccountPage.navigateToTransactionHistory();
        transactionHistoryPage.verifyTransactionHistory(1028.6, 4, 22);
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
        LoanAccountPage loanAccountPage = loanTestHelper.redoLoanDisbursal("Default Group", "WeeklyGroupFlatLoanWithOnetimeFee", paramsPastDate, paramsCurrentDate, 0, true);
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
        loanAccountPage.verifyStatus("Active in Good Standing");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalForVariableInstallmentLoan() throws Exception {
        dataSetUpForVariableInstallmentLoan();
        applicationDatabaseOperation.updateLSIM(1);
        
        int interest = 24;
        int noOfInstallments = 5;
        int loanAmount = 1000;
        DateTime disbursalDate = systemDateTime;
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = setLoanParams(disbursalDate, interest, noOfInstallments, loanAmount);
        
        loanTestHelper.setApplicationTime(systemDateTime.plusDays(14));
        
        RedoLoanDisbursalEntryPage redoLoanDisbursalEntryPage = navigateToRedoLoanPage();
        redoLoanDisbursalEntryPage.enterDisbursementDate(disbursalDate);
        
//        TODO - fix validation of use of fees on variable installment
//        String[] invalidFees = getInvalidFees();
//        redoLoanDisbursalEntryPage.verifyFeeBlockedForVariableInstallmentLoan(invalidFees);
        
//        RedoLoanDisbursalSchedulePreviewPage redoLoanDisbursalSchedulePreviewPage = 
            redoLoanDisbursalEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(redoLoanDisbursalParameters);
        
//        redoLoanDisbursalSchedulePreviewPage.validateRepaymentScheduleFieldDefault(noOfInstallments);
        
//        String maxGap = "10";
//        String minGap = "1";
//        redoLoanDisbursalSchedulePreviewPage.validateDateFieldValidations(disbursalDate, minGap, maxGap, noOfInstallments);
//        
//        String minInstalmentAmount = "100";
//        redoLoanDisbursalSchedulePreviewPage.verifyInstallmentTotalValidations(noOfInstallments, minInstalmentAmount, disbursalDate, minGap);
//        
//        redoLoanDisbursalSchedulePreviewPage.verifyValidData(noOfInstallments, minGap, minInstalmentAmount, disbursalDate, maxGap);
//        redoLoanDisbursalSchedulePreviewPage.verifyRecalculationWhenDateAndTotalChange();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalForDecliningBalanceLoan() throws Exception {
        dataSetUpForVariableInstallmentLoan();
        applicationDatabaseOperation.updateLSIM(1);
        String[] fees = getInvalidFees();

        loanTestHelper.setApplicationTime(systemDateTime.plusDays(14));
        RedoLoanDisbursalEntryPage redoLoanDisbursalEntryPage = navigateToRedoLoanPage().selectFee(new String[]{fees[0]});
        
        int interest = 24;
        int noOfInstallments = 5;
        int loanAmount = 1000;
        RedoLoanDisbursalParameters redoLoanDisbursalParameters = setLoanParams(systemDateTime, interest, noOfInstallments, loanAmount);        
        RedoLoanDisbursalSchedulePreviewPage redoLoanDisbursalSchedulePreviewPage = 
            redoLoanDisbursalEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(redoLoanDisbursalParameters);
        
        RedoLoanAccountPreviewPage redoLoanAccountPreviewPage = redoLoanDisbursalSchedulePreviewPage.setPaidField(RedoLoanScheduleData.DECLINING_PRINCIPAL_LATE_PAYMENT_1).clickPreviewAndGoToReviewLoanAccountPage();
        
        String[][] expectedRepaymentSchedule = new String[][] {
                {"1", "15-Oct-2010", "19-Oct-2010", "200.4", "4.6", "100", "305"},
                {"2", "22-Oct-2010", "19-Oct-2010", "0.4", "4.6", "100", "105"}};

        String[][] expectedFutureInstallments = new String[][] {
                {"2", "22-Oct-2010", "-", "200", "0", "0", "200"},
                {"3", "29-Oct-2010", "-", "200.4", "4.6", "100", "305"},
                {"4", "05-Nov-2010", "-", "200.4", "4.6", "100", "305"},
                {"5", "12-Nov-2010", "-", "198.4", "5.6", "100", "304"}};
        
        String[][] expectedRepaymentBalance = new String[][] {
                {"799.6", "19.4", "400", "1,219"},
                {"799.2", "14.8", "300", "1,114"}};
        
        redoLoanAccountPreviewPage.verifyRunningBalance(expectedRepaymentSchedule, expectedFutureInstallments, expectedRepaymentBalance);
    }

    private RedoLoanDisbursalEntryPage navigateToRedoLoanPage() {
        return navigationHelper.
                navigateToAdminPage().navigateToRedoLoanDisbursal().
                searchAndNavigateToRedoLoanDisbursalPage("Stu1233171716380").
                navigateToRedoLoanDisbursalChooseLoanProductPage("Stu1233171716380").
                submitAndNavigateToRedoLoanDisbursalEntryPage("WeeklyFlatLoanWithOneTimeFees");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void dataSetUpForVariableInstallmentLoan() throws Exception {
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

    private String[] getInvalidFees()        {
        return new String[]{"loanWeeklyFee", "fixedFeePerInterest", "fixedFeePerAmountAndInterest"};
    }
}