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

import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalChooseLoanInstancePage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchResultsPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class RedoLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;
    
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
        DateTime targetTime = new DateTime(2009,7,26,15,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
                
        navigationHelper = new NavigationHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium, navigationHelper);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    } 
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void redoLoanDisbursalWithPastDate() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        
        RedoLoanDisbursalParameters params = new RedoLoanDisbursalParameters();
        
        params.setDisbursalDateDD("23");
        params.setDisbursalDateMM("07");
        params.setDisbursalDateYYYY("2009");
        
        loanTestHelper.redoLoanDisbursal("MyGroup1233266255641", "WeeklyGroupFlatLoanWithOnetimeFee", params);
        
        verifyRedoneLoanDisbursal("RedoLoanDisbursalTest_001_result_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void redoLoanDisbursalWithCurrentOrFutureDate() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        
        RedoLoanDisbursalParameters params = new RedoLoanDisbursalParameters();
        
        params.setDisbursalDateDD("26");
        params.setDisbursalDateMM("07");
        params.setDisbursalDateYYYY("2009");
        
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        adminPage.verifyPage();
        
        RedoLoanDisbursalSearchPage searchPage = adminPage.navigateToRedoLoanDisbursal();
        searchPage.verifyPage();
        
        RedoLoanDisbursalSearchResultsPage resultsPage = searchPage.searchAndNavigateToRedoLoanDisbursalPage("MyGroup1233266255641");
        resultsPage.verifyPage();
        
        RedoLoanDisbursalChooseLoanInstancePage chooseLoanPage = resultsPage.navigateToRedoLoanDisbursalChooseLoanProductPage("MyGroup1233266255641");
        chooseLoanPage.verifyPage();
        
        RedoLoanDisbursalEntryPage dataEntryPage = chooseLoanPage.submitAndNavigateToRedoLoanDisbursalEntryPage("WeeklyGroupFlatLoanWithOnetimeFee");
        dataEntryPage.verifyPage();
        
        MifosPage schedulePreviewPage = dataEntryPage.submitAndNavigateToRedoLoanDisbursalSchedulePreviewPage(params);
        schedulePreviewPage.verifyPage("LoanCreationDetail"); // the page.id for dataEntryPage, we want to stay there.
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyRedoneLoanDisbursal(String resultDataSetFile) throws Exception {
        String[] tablesToValidate = { /*"ACCOUNT_STATUS_CHANGE_HISTORY",*/ "LOAN_ACCOUNT", "LOAN_ACTIVITY_DETAILS", "LOAN_SUMMARY", "LOAN_TRXN_DETAIL"};
        
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);     
        
    }
    
}
