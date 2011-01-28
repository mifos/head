/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.UndoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"loan","acceptance", "ui"})
public class UndoLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private static final String START_DATA_SET = "acceptance_small_006_dbunit.xml";

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium, navigationHelper);
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
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void undoClientLoanActiveInBadStanding() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);

        String clientID = "0005-000000028";
        String clientLoanID = "000100000000215";
        String resultClickLink = "Stu1233266109404 Client1233266109404: ID 0005-000000028";

        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) loanTestHelper.reverseLoanDisbursal(clientLoanID, clientID, false, resultClickLink);

        clientViewDetailsPage.verifyLoanDoesntExist("Acct #"+clientLoanID);
        loanTestHelper.verifyHistoryAndSummaryReversedLoan(clientViewDetailsPage.navigateToClosedAccountsPage(), clientLoanID);
    }

    /**
     * Verifies that the loan is successfully reversed if the Account ID
     * is of a loan which is "active in good standing".
     * Client loan.
     * http://mifosforge.jira.com/browse/MIFOSTEST-20
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void undoClientLoanActiveInGoodStanding() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);

        String clientID = "0005-000000028";
        String clientLoanID = "000100000000182";
        String resultClickLink = "Stu1233266109404 Client1233266109404: ID 0005-000000028";

        ClientViewDetailsPage clientViewDetailsPage = (ClientViewDetailsPage) loanTestHelper.reverseLoanDisbursal(clientLoanID, clientID, false, resultClickLink);

        clientViewDetailsPage.verifyLoanDoesntExist("Acct #"+clientLoanID);
        loanTestHelper.verifyHistoryAndSummaryReversedLoan(clientViewDetailsPage.navigateToClosedAccountsPage(), clientLoanID, "3003.0", "0.0", "3003.0");
    }

    /**
     * Verify whether the user can reverse a Group loan when the Account is in different statuses
     * Group loan.
     * http://mifosforge.jira.com/browse/MIFOSTEST-26
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void reverseGroupLoanWhenAccInDiffStatuses() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, START_DATA_SET, dataSource, selenium);

        String groupName = "MyGroup1233266255641";
        String groupID = "0006-000000045";
        String groupLoanID = "000100000000206";
        String resultClickLink = "MyGroup1233266255641: ID 0006-000000045";

        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString(groupName);
        searchParams.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");


        LoanAccountPage loanAccountPage = loanTestHelper.createDefaultLoanAccount(searchParams);
        String loanID = loanAccountPage.getAccountId();
        UndoLoanDisbursalSearchPage undoSearchPage = loanAccountPage
            .navigateToAdminPageUsingHeaderTab()
            .navigateToUndoLoanDisbursal();
        undoSearchPage.verifyLoanCantBeReversed(loanID);

        GroupViewDetailsPage groupViewDetailsPage = (GroupViewDetailsPage) loanTestHelper.reverseLoanDisbursal(groupLoanID, groupID, true, resultClickLink);

        groupViewDetailsPage.verifyLoanDoesntExist("Acct #"+groupLoanID);
    }
}
