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
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
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
public class ClientLoanStatusHistoryTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;
    
    // acceptance_small_007 account id's:
    private static final String ACCOUNT_PENDING_APPROVAL_ID = "000100000000003";
    private static final String ACCOUNT_PARTIAL_APPLICATION_ID = "000100000000004";
    private static final String ACCOUNT_APPROVED_ID = "000100000000005";

    // the table we verify the account status history against
    private static final String ACCOUNT_STATUS_CHANGE_HISTORY = "ACCOUNT_STATUS_CHANGE_HISTORY";
    
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
        DateTime targetTime = new DateTime(2009,7,4,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        
        loanTestHelper = new LoanTestHelper(selenium);
    }
    
    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newLoan() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        
        searchParameters.setLoanProduct("Cattle Loan");
        searchParameters.setSearchString("First Tester");
        submitAccountParameters.setAmount("1000");
        
        loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        
        verifyStatusHistory("ClientLoanStatusHistory_001_result_dbunit.xml.zip");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToApproved() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.APPROVED);
        params.setNote("Test");
        loanTestHelper.changeLoanAccountStatus(ACCOUNT_PENDING_APPROVAL_ID, params);
        
        verifyStatusHistory("ClientLoanStatusHistory_002_result_dbunit.xml.zip");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void pendingApprovalToPartialApplication() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.PARTIAL_APPLICATION);
        params.setNote("Test");
        loanTestHelper.changeLoanAccountStatus(ACCOUNT_PENDING_APPROVAL_ID, params);
        
        verifyStatusHistory("ClientLoanStatusHistory_003_result_dbunit.xml.zip"); 
        
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void approvedToRejected() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.CANCEL);
        params.setCancelReason("Rejected");
        params.setNote("Test");
        loanTestHelper.changeLoanAccountStatus(ACCOUNT_APPROVED_ID, params);
        
        verifyStatusHistory("ClientLoanStatusHistory_004_result_dbunit.xml.zip");
        
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void partialApplicationToPendingApproval() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.PENDING_APPROVAL);
        params.setNote("Test");
        loanTestHelper.changeLoanAccountStatus(ACCOUNT_PARTIAL_APPLICATION_ID, params);
        
        verifyStatusHistory("ClientLoanStatusHistory_005_result_dbunit.xml.zip");
        
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newLoanToApproved() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        
        searchParameters.setLoanProduct("Cattle Loan");
        searchParameters.setSearchString("First Tester");
        submitAccountParameters.setAmount("1000");
        
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.APPROVED);
        params.setNote("Approving a new loan.");
        
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);     
        
        // do the edit in one long chain (we do this instead of using loanTestHelper because we already have a LoanAccountPage and
        // we're missing the ID.
        loanAccountPage.navigateToEditAccountStatus().submitAndNavigateToEditStatusConfirmationPage(params).submitAndNavigateToLoanAccountPage();
        
        verifyStatusHistory("ClientLoanStatusHistory_006_result_dbunit.xml.zip");
        
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void newLoanToPartialApplicationToPendingApprovalToApproved() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        
        searchParameters.setLoanProduct("Cattle Loan");
        searchParameters.setSearchString("First Tester");
        submitAccountParameters.setAmount("1000");
        
        LoanAccountPage loanAccountPage = loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters);
        
        // the created loan has id 000100000000006. In an ideal world this would be 
        // extracted from LoanAccountPage but I (Johan) found no easy way to do this.
        String loanId = loanAccountPage.getAccountId();
        
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        
        params.setStatus(EditLoanAccountStatusParameters.PARTIAL_APPLICATION);
        params.setNote("Partial app.");
        loanTestHelper.changeLoanAccountStatus(loanId, params);
        
        params.setStatus(EditLoanAccountStatusParameters.PENDING_APPROVAL);
        params.setNote("More data arrived.");
        loanTestHelper.changeLoanAccountStatus(loanId, params);
        
        params.setStatus(EditLoanAccountStatusParameters.APPROVED);
        params.setNote("Approved.");
        loanTestHelper.changeLoanAccountStatus(loanId, params);
        
        verifyStatusHistory("ClientLoanStatusHistory_007_result_dbunit.xml.zip");
    }
 
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyStatusHistory(String resultDataSetFile) throws Exception {
        
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { ACCOUNT_STATUS_CHANGE_HISTORY });
        
        dbUnitUtilities.verifyTable(ACCOUNT_STATUS_CHANGE_HISTORY, databaseDataSet, expectedDataSet);
    }
}
