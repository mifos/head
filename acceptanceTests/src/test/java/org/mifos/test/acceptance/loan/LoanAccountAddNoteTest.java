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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.AccountAddNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountPreviewNotesPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"loan","acceptance","ui"})
public class LoanAccountAddNoteTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    
    private static final String startDataSet = "acceptance_small_003_dbunit.xml.zip";

    private static final String TEST_ACCOUNT = "000100000000004";
    private static final String TEST_ACCOUNT_NOTE = "Acceptance Test note for Issue 2456";

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,6,25,8,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void addNoteToLoanAccountAndVerifyRecentNotes() throws Exception {
        initData();
        LoanAccountPage loanAccountPage = addNoteToAccount();
        loanAccountPage.verifyPage();
        assertTextFoundOnPage(TEST_ACCOUNT_NOTE);       
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void addNoteToLoanAccountAndVerifyAllNotes() throws Exception {
        initData();

        LoanAccountPage loanAccountPage = addNoteToAccount();        
        loanAccountPage.verifyPage();
        AccountNotesPage accountNotesPage = loanAccountPage.navigateToAccountNotesPage();
        accountNotesPage.verifyPage();
        assertTextFoundOnPage(TEST_ACCOUNT_NOTE);       
    }
    
    private void initData() throws DatabaseUnitException, SQLException, IOException, URISyntaxException {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, startDataSet, dataSource, selenium);
    }
    
    private LoanAccountPage addNoteToAccount() {
        // find the loan w/ id TEST_ACCOUNT
        NavigationHelper helper = new NavigationHelper(selenium);

        LoanAccountPage loanAccountPage = helper.navigateToLoanAccountPage(TEST_ACCOUNT);
        loanAccountPage.verifyPage();

        AccountAddNotesPage addNotesPage = loanAccountPage.navigateToAddNotesPage();
        addNotesPage.verifyPage();
        AccountPreviewNotesPage previewPage = addNotesPage.submitAndNavigateToAccountAddNotesPreviewPage(TEST_ACCOUNT_NOTE);
        previewPage.verifyPage();
        loanAccountPage = previewPage.submitAndNavigateToLoanAccountPage();
        
        return loanAccountPage;
    }
    
    
}
