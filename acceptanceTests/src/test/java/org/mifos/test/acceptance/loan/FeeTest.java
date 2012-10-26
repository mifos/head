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
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class FeeTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private FeeTestHelper feeTestHelper;
    private TestDataSetup dataSetup;

    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009, 7, 11, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);

        loanTestHelper = new LoanTestHelper(selenium);
        feeTestHelper = new FeeTestHelper(dataSetup, new NavigationHelper(selenium));
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyFee() throws Exception {

        String accountId = "000100000000046"; // approved loan
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString(accountId);

        ChargeParameters params = new ChargeParameters();

        params.setType(ChargeParameters.MISC_FEES);
        params.setAmount("10");

        loanTestHelper.verifyOriginalValues(searchParams, "10,000", "461", "0", "0", "10461.0");
        loanTestHelper.applyCharge(accountId, params);
        loanTestHelper.verifyOriginalValues(searchParams, "10,000", "461", "10", "0", "10471.0");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyAndWaiveFee() throws Exception {
        String accountId = "000100000000047"; // approved loan
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString(accountId);

        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("15");
        feeParameters.setType(ChargeParameters.MISC_FEES);

        loanTestHelper.verifyOriginalValues(searchParams, "10,000", "461", "0", "0", "10461.0");
        loanTestHelper.applyCharge(accountId, feeParameters);
        loanTestHelper.verifyOriginalValues(searchParams, "10,000", "461", "15", "0", "10476.0");
        loanTestHelper.waiveFee(accountId);
        loanTestHelper.verifyOriginalValues(searchParams, "10,000", "461", "0", "0", "10461.0");
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyAndRemoveFees() throws Exception {

        // this account has an approved but not disbursed loan.
        String pendingApprovalAccountId = "000100000000043";
        String partialApplicationAccountId = "000100000000044";
        String applicationApprovedAccountId = "000100000000045";
        String client = "WeeklyClient Monday";

        String oneTimeFee = feeTestHelper.createNoRateFee("oneTimeFee", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 10);
        String periodicFee = feeTestHelper.createPeriodicRateFee("periodicFee", FeesCreatePage.SubmitFormParameters.LOAN, FeesCreatePage.SubmitFormParameters.PERIODIC_FEE_FREQUENCY, 1, 10, FeesCreatePage.SubmitFormParameters.LOAN_AMOUNT);

        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("10");
        feeParameters.setType(oneTimeFee);
        // ONE TIME FEE
        // add and remove the fee from a pending approval account
        loanTestHelper.applyChargeUsingFeeLabel(pendingApprovalAccountId, feeParameters);
        loanTestHelper.verifyOneTimeFee(oneTimeFee, 1);
        loanTestHelper.removeOneTimeFee(1);
        loanTestHelper.verifyNoOneTimeFeesExist();

        // add and remove the fee from a partial application account
        loanTestHelper.applyChargeUsingFeeLabel(partialApplicationAccountId, feeParameters);
        loanTestHelper.verifyOneTimeFee(oneTimeFee, 1);
        loanTestHelper.removeOneTimeFee(1);
        loanTestHelper.verifyNoOneTimeFeesExist();

        // add and verify that the fee cannot be removed from an application approved account
        loanTestHelper.applyChargeUsingFeeLabel(applicationApprovedAccountId, feeParameters);
        loanTestHelper.verifyOneTimeFee(oneTimeFee, 1);
        loanTestHelper.verifyNoOneTimeFeeRemovalLinkExists(1);
        
        
        // PERIODIC FEE
        feeParameters.setType(periodicFee);
        
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString(client);
        searchParams.setLoanProduct("WeeklyClientFlatLoanWithNoFee");
		DisburseLoanParameters disburseParams = new DisburseLoanParameters();
		disburseParams.setDisbursalDateDD("28");
		disburseParams.setDisbursalDateMM("02");
		disburseParams.setDisbursalDateYYYY("2011");
		disburseParams.setPaymentType(PaymentParameters.CASH);
		disburseParams.setAmount("10,000");
		
        // add and verify that the fee can be removed from an application approved account
        loanTestHelper.applyChargeUsingFeeLabel("000100000000047", feeParameters);
        loanTestHelper.verifyNoPeriodicFee(periodicFee, 1);
        loanTestHelper.removePeriodicFee(1);
		
        // add and verify that the fee can't be removed from an application approved account
        // after repay of first instalment
        PaymentParameters paymentParams = new PaymentParameters();
        paymentParams.setTransactionDateDD("28");
        paymentParams.setTransactionDateMM("02");
        paymentParams.setTransactionDateYYYY("2011");
        paymentParams.setPaymentType(PaymentParameters.CASH);
        paymentParams.setAmount("3000");
        paymentParams.setReceiptId("");
        paymentParams.setReceiptDateDD("");
        paymentParams.setReceiptDateMM("");
        paymentParams.setReceiptDateYYYY("");
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 2, 28, 12, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

		String accountWithDisburs = loanTestHelper.createActivateAndDisburseDefaultLoanAccount(searchParams , disburseParams ).getAccountId();
        loanTestHelper.applyChargeUsingFeeLabel(accountWithDisburs, feeParameters);
        loanTestHelper.applyPayment(accountWithDisburs, paymentParams);
        loanTestHelper.removePeriodicFee(1);
        loanTestHelper.verifyValidationErrorAppear();
        
        
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void canApplyOneTimeFeeAfterPaymentsHaveBeenMade() throws Exception {
        String client = "WeeklyClient Monday";
        
        DateTime currentTime = new DateTime();
        new DateTimeUpdaterRemoteTestingService(selenium).setDateTime(currentTime);
        
        String dd = Integer.toString(currentTime.getDayOfMonth());
        String mm = Integer.toString(currentTime.getMonthOfYear());
        String yy = Integer.toString(currentTime.getYear());
        applicationDatabaseOperation.updateLSIM(1);
        
        LoanAccountPage loanAccountPage = loanTestHelper.createActivateDisburstAndApplyPaymentForDefaultLoanAccount(
                client, dd, mm, yy);
        double feesBefore = Double.parseDouble(loanAccountPage.getFeesBalance());
        
        loanAccountPage = loanTestHelper.applyCharge("Misc Fees", "1");
        double feesAfter = Double.parseDouble(loanAccountPage.getFeesBalance());
        
        Assert.assertEquals(feesBefore + 1.0, feesAfter);
    }
        
}