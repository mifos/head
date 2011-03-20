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
import org.mifos.test.acceptance.framework.loan.AccountActivityPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class PenaltyTest extends UiTestCaseBase {

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
        verifySummaryAndActivity(loanAccountPage, "10.0", "10.0", "Misc penalty applied", 2);
    }

    private void verifySummaryAndActivity
            (LoanAccountPage
                     loanAccountPage, String
                    penalty, String
                    penaltyBalance, String
                    activity, int row) {
        Assert.assertEquals(loanAccountPage.getPenaltyPaid(), "0.0");
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
        verifySummaryAndActivity(loanAccountPage, "15.0", "15.0", "Misc penalty applied", 2);
        loanTestHelper.waivePenalty(accountId);
        loanAccountPage = new NavigationHelper(selenium).navigateToLoanAccountPage(accountId);
        verifySummaryAndActivity(loanAccountPage, "15.0", "0.0", "Penalty waived", 2);

    }
}
