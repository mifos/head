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
 
package org.mifos.test.acceptance.admin;

import org.dbunit.dataset.IDataSet;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ImportTransactionsConfirmationPage;
import org.mifos.test.acceptance.framework.admin.ImportTransactionsPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(enabled = false, sequential = true, groups = {"admin", "acceptance","ui"})
public class ImportTransactionsTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    
    public static final String ACCOUNT_PAYMENT = "ACCOUNT_PAYMENT";
    public static final String ACCOUNT_TRXN = "ACCOUNT_TRXN";
    public static final String FINANCIAL_TRXN = "FINANCIAL_TRXN";
    public static final String LOAN_ACTIVITY_DETAILS = "LOAN_ACTIVITY_DETAILS";
    public static final String LOAN_SCHEDULE = "LOAN_SCHEDULE";
    public static final String LOAN_SUMMARY = "LOAN_SUMMARY";
    public static final String LOAN_TRXN_DETAIL = "LOAN_TRXN_DETAIL";

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);

    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void importAudiBankTransactions() throws Exception {

        String importFile = this.getClass().getResource("/AudiUSD-OneTransactionEA00002.txt").toString();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_009_dbunit.xml.zip", dataSource, selenium);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ImportTransactionsPage importTransactionsPage = adminPage.navigateToImportTransactionsPage();
        importTransactionsPage.verifyPage();
        ImportTransactionsConfirmationPage importTransactionsConfirmationPage = importTransactionsPage.importAudiTransactions(importFile);
        importTransactionsConfirmationPage.verifyPage();
        
        String expectedDataSetFile = "ImportTransactions_001_result_dbunit.xml.zip";
        
        verifyImportTransactions(expectedDataSetFile);     

        
     }
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyImportTransactions(String expectedDataSetFile) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(expectedDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { ACCOUNT_PAYMENT, 
                                    ACCOUNT_TRXN,
                                    FINANCIAL_TRXN,
                                    LOAN_ACTIVITY_DETAILS,
                                    LOAN_SCHEDULE,
                                    LOAN_SUMMARY,
                                    LOAN_TRXN_DETAIL  });

        dbUnitUtilities.verifyTable(ACCOUNT_TRXN, databaseDataSet, expectedDataSet);     
        dbUnitUtilities.verifyTable(LOAN_ACTIVITY_DETAILS, databaseDataSet, expectedDataSet);     
        dbUnitUtilities.verifyTable(LOAN_SCHEDULE, databaseDataSet, expectedDataSet);     
        dbUnitUtilities.verifyTable(LOAN_SUMMARY, databaseDataSet, expectedDataSet);     
        dbUnitUtilities.verifyTable(LOAN_TRXN_DETAIL, databaseDataSet, expectedDataSet);

        String[] orderAccountPaymentByColumns = new String[] {"amount","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAccountPaymentByColumns,ACCOUNT_PAYMENT, expectedDataSet, databaseDataSet);
        String[] orderFinTrxnByColumns =  new String[]{"posted_amount", "glcode_id"};  
        dbUnitUtilities.verifyTableWithSort(orderFinTrxnByColumns,FINANCIAL_TRXN, expectedDataSet, databaseDataSet );
        
    }
 
}
