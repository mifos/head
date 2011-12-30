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
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.UndoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class UndoLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Verifies that the loan is successfully reversed if the Account ID
     * is of a loan which is "active in bad standing".
     * Client loan.
     * http://mifosforge.jira.com/browse/MIFOSTEST-22
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void undoClientLoanActiveInBadStanding() throws Exception {
        setTime(2011, 2, 28);
        String clientID = "0002-000000024";
        String clientLoanID = "000100000000040";
        String resultClickLink = "WeeklyClient Monday: ID 0002-000000024";
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) loanTestHelper.reverseLoanDisbursal(clientLoanID, clientID, false, resultClickLink);
        clientViewDetailsPage.verifyLoanDoesntExist("Acct #" + clientLoanID);
        loanTestHelper.verifyHistoryAndSummaryReversedLoan(clientViewDetailsPage.navigateToClosedAccountsPage(), clientLoanID);
    }

    private void setTime(int year, int monthOfYear, int dayOfMonth) throws UnsupportedEncodingException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(year, monthOfYear, dayOfMonth, 14, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    /**
     * Verifies that the loan is successfully reversed if the Account ID
     * is of a loan which is "active in good standing".
     * Client loan.
     * http://mifosforge.jira.com/browse/MIFOSTEST-20
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void undoClientLoanActiveInGoodStanding() throws Exception {
        setTime(2011, 3, 28);
        String clientID = "0002-000000024";
        String clientLoanID = "000100000000041";
        String resultClickLink = "WeeklyClient Monday: ID 0002-000000024";
        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) loanTestHelper.reverseLoanDisbursal(clientLoanID, clientID, false, resultClickLink);
        clientViewDetailsPage.verifyLoanDoesntExist("Acct #" + clientLoanID);
        loanTestHelper.verifyHistoryAndSummaryReversedLoan(clientViewDetailsPage.navigateToClosedAccountsPage(), clientLoanID, "104,613", "0", "104,613", 1);
    }

    /**
     * Verify whether the user can reverse a Group loan when the Account is in different status
     * Group loan.
     * http://mifosforge.jira.com/browse/MIFOSTEST-26
     *
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void reverseGroupLoanWhenAccInDiffStatus() throws Exception {
        String groupID = "0002-000000004";
        String groupLoanID = "000100000000042";
        String resultClickLink = "group1: ID 0002-000000004";
        GroupViewDetailsPage groupViewDetailsPage = (GroupViewDetailsPage) loanTestHelper.reverseLoanDisbursal(groupLoanID, groupID, true, resultClickLink);
        groupViewDetailsPage.verifyLoanDoesntExist("Acct #" + groupLoanID);
        String loanID = "000100000000011";
        UndoLoanDisbursalSearchPage undoSearchPage = new NavigationHelper(selenium)
                .navigateToAdminPage()
                .navigateToUndoLoanDisbursal();
        undoSearchPage.verifyLoanCantBeReversed(loanID);
    }
}
