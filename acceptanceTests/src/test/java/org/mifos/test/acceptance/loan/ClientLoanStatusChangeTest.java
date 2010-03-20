/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"acceptance","ui", "loan"})
public class ClientLoanStatusChangeTest extends UiTestCaseBase {
    private static final String ACCOUNT = "ACCOUNT";
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,1,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToPartialApplication() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // a loan account w/ id 000100000000003 is pending approval in the data set

        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.PARTIAL_APPLICATION);
        statusParameters.setNote("Test");

        loanTestHelper.changeLoanAccountStatus("000100000000003", statusParameters);
        verifyLoanAccountStatus("ClientLoanStatusChange_001_result_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToApplicationRejected() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // a loan account w/ id 000100000000003 is pending approval in the data set

        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.CANCEL);
        statusParameters.setCancelReason(EditLoanAccountStatusParameters.CANCEL_REASON_REJECTED);
        statusParameters.setNote("Test");

        loanTestHelper.changeLoanAccountStatus("000100000000003", statusParameters);
        verifyLoanAccountStatus("ClientLoanStatusChange_002_result_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToApplicationApproved() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // a loan account w/ id 000100000000003 is pending approval in the data set

        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParameters.setNote("Test");

        loanTestHelper.changeLoanAccountStatus("000100000000003", statusParameters);
        verifyLoanAccountStatus("ClientLoanStatusChange_003_result_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applicationApprovedToApplicationRejected() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // the data set has a loan that's approved w/ id 000100000000005.

        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(statusParameters.CANCEL);
        statusParameters.setCancelReason(EditLoanAccountStatusParameters.CANCEL_REASON_REJECTED);
        statusParameters.setNote("Test");

        loanTestHelper.changeLoanAccountStatus("000100000000005", statusParameters);
        verifyLoanAccountStatus("ClientLoanStatusChange_004_result_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void partialApplicationToPendingApproval() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // data set contains a loan acct w/ id 000100000000004 that's partially approved.
        EditLoanAccountStatusParameters statusParameters = new EditLoanAccountStatusParameters();
        statusParameters.setStatus(statusParameters.PENDING_APPROVAL);
        statusParameters.setNote("Test");

        loanTestHelper.changeLoanAccountStatus("000100000000004", statusParameters);
        verifyLoanAccountStatus("ClientLoanStatusChange_005_result_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyLoanAccountStatus(String resultDataSetFile) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { ACCOUNT });

        dbUnitUtilities.verifyTable(ACCOUNT, databaseDataSet, expectedDataSet);
    }
}
