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
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.PenaltyFormParameters;
import org.mifos.test.acceptance.framework.loan.AccountActivityPage;
import org.mifos.test.acceptance.framework.loan.ApplyAdjustmentPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.PenaltyHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class PenaltyTest extends UiTestCaseBase {

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;
    
    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 28, 14, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")

    public void applyPenaltyOnApprovedLoan() throws Exception {
        ChargeParameters params = new ChargeParameters();
        params.setType(ChargeParameters.MISC_PENALTY);
        params.setAmount("10");
        LoanAccountPage loanAccountPage = loanTestHelper.applyCharge("000100000000038", params);
        verifySummaryAndActivity(loanAccountPage, "10", "10", "Misc penalty applied", 2);
        
        loanAccountPage.navigateBack();
        verifyRepaymentSchelude(loanAccountPage, "10", "7", "3");
    }

    private void verifySummaryAndActivity
            (LoanAccountPage
                     loanAccountPage, String
                    penalty, String
                    penaltyBalance, String
                    activity, int row) {
        Assert.assertEquals(loanAccountPage.getPenaltyPaid(), "0");
        Assert.assertEquals(loanAccountPage.getPenaltyBalance(), penaltyBalance);
        AccountActivityPage accountActivityPage = loanAccountPage.navigateToAccountActivityPage();
        Assert.assertEquals(accountActivityPage.getLastPenalty(row), penalty);
        Assert.assertEquals(accountActivityPage.getActivity(row), activity);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyAndWaivePenaltyOnDisbursedLoan
            () throws Exception {
        String accountId = "000100000000039";
        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("15");
        feeParameters.setType(ChargeParameters.MISC_PENALTY);
        LoanAccountPage loanAccountPage = loanTestHelper.applyCharge(accountId, feeParameters);
        verifySummaryAndActivity(loanAccountPage, "15", "15", "Misc penalty applied", 2);
        loanTestHelper.waivePenalty(accountId);
        loanAccountPage = new NavigationHelper(selenium).navigateToLoanAccountPage(accountId);
        verifySummaryAndActivity(loanAccountPage, "15", "0", "Penalty waived", 2);

    }
    
    private void verifyRepaymentSchelude(LoanAccountPage loanAccountPage, String penalty, String payment, String diff) {
        DisburseLoanParameters disburseLoanParameters = new DisburseLoanParameters();
        disburseLoanParameters.setPaymentType(DisburseLoanParameters.CASH);
        
        loanAccountPage.navigateToDisburseLoan()
            .submitAndNavigateToDisburseLoanConfirmationPage(disburseLoanParameters)
            .submitAndNavigateToLoanAccountPage();
        
        ViewRepaymentSchedulePage repaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();
        
        repaymentSchedulePage.verifyRepaymentScheduleTablePenalties(3, 6, penalty);
        
        ApplyPaymentPage paymentPage = repaymentSchedulePage.navigateToApplyPaymentPage();
        
        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setTransactionDateDD("28");
        paymentParameters.setTransactionDateMM("02");
        paymentParameters.setTransactionDateYYYY("2011");
        paymentParameters.setAmount(payment);
        paymentParameters.setPaymentType(PaymentParameters.CASH);
        
        paymentPage.submitAndNavigateToApplyPaymentConfirmationPage(paymentParameters)
            .submitAndNavigateToLoanAccountDetailsPage().navigateToRepaymentSchedulePage();
        
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(3, 6, payment);
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(3, 8, payment);
        repaymentSchedulePage.verifyRepaymentScheduleTableRow(5, 6, diff);
        
        repaymentSchedulePage.verifyRunningBalanceTableRow(3, 3, diff);
        
        ApplyAdjustmentPage adjustmentPage = repaymentSchedulePage.navigateToApplyAdjustment();
        
        adjustmentPage.fillAdjustmentFieldsAndSubmit(payment).navigateToRepaymentSchedulePage();
        
        repaymentSchedulePage.verifyRepaymentScheduleTablePenalties(3, 6, penalty);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private LoanAccountPage prepareLoanForPenaltyTest() throws Exception {
        String client = "WeeklyClient Monday";
        
        DateTime currentTime = new DateTime().withYear(2012).withDayOfMonth(3).withMonthOfYear(12);
        new DateTimeUpdaterRemoteTestingService(selenium).setDateTime(currentTime);
        
        String dd = Integer.toString(currentTime.getDayOfMonth());
        String mm = Integer.toString(currentTime.getMonthOfYear());
        String yy = Integer.toString(currentTime.getYear());
        applicationDatabaseOperation.updateLSIM(1);
        
        return loanTestHelper.createActivateDisburstAndApplyPaymentForDefaultLoanAccount(client, dd, mm, yy);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canApplyOneTimePenaltyAfterPaymentsHaveBeenMade() throws Exception {

        LoanAccountPage loanAccountPage = prepareLoanForPenaltyTest();
        
        double penaltyBefore = Double.parseDouble(loanAccountPage.getPenaltyBalance());
        
        loanAccountPage = loanTestHelper.applyCharge("Misc Penalty", "1");
        double penaltyAfter = Double.parseDouble(loanAccountPage.getPenaltyBalance());
        
        Assert.assertEquals(penaltyBefore + 1.0, penaltyAfter);
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canRemoveAutomaticPenaltyAfterPaymentHasBeenMade() throws Exception {
        
        LoanAccountPage loanAccountPage = prepareLoanForPenaltyTest();
        String accountId = loanAccountPage.getAccountId();
        
        PenaltyHelper penaltyHelper = new PenaltyHelper(selenium);
        
        String penaltyName = "Penalty_" + StringUtil.getRandomString(6);
        
        penaltyHelper.createRatePenalty(penaltyName, PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_MONTHLY, "33.3", PenaltyFormParameters.FORMULA_OVERDUE_AMOUNT,
                "0", "9999999");
        ChargeParameters chargeParams = new ChargeParameters();
        chargeParams.setType(penaltyName);
        chargeParams.setAmount("");
        
        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        loanAccountPage = navigationHelper.navigateToLoanAccountPage(accountId);
        loanTestHelper.applyChargeUsingFeeLabel(accountId, chargeParams);
        
        new DateTimeUpdaterRemoteTestingService(selenium).setDateTime(new DateTime().plusYears(1)); 
        loanAccountPage = navigationHelper.navigateToLoanAccountPage(accountId);
        loanAccountPage = loanAccountPage.removePenalty(1);
        loanAccountPage.verifyNoPenaltyRemovalLinkExists(1);
        
    }
}
