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
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
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
    public void applyAndRemoveOneTimeFee() throws Exception {

        // this account has an approved but not disbursed loan.
        String pendingApprovalAccountId = "000100000000043";
        String partialApplicationAccountId = "000100000000044";
        String applicationApprovedAccountId = "000100000000045";

        String oneTimeFee = feeTestHelper.createNoRateFee("oneTimeFee", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 10);

        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("10");
        feeParameters.setType(oneTimeFee);

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
    }
}