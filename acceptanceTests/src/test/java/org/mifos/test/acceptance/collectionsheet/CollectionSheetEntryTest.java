/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

package org.mifos.test.acceptance.collectionsheet;

import java.sql.Connection;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"CollectionSheetEntryTest","acceptance","ui"})
public class CollectionSheetEntryTest extends UiTestCaseBase {
    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
  
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void defaultAdminUserSelectsValidCollectionSheetEntryParameters() throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("Office1");
        formParameters.setLoanOfficer("Bagonza Wilson");
        formParameters.setCenter("Center1");
        formParameters.setPaymentMode("Cash");
        
        dbUnitUtilities.loadDataFromFile("acceptance_small_001_dbunit.xml", dataSource);
        
        CollectionSheetEntrySelectPage selectPage = 
            loginAndNavigateToCollectionSheetEntrySelectPage("mifos", "testmifos");
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = 
            selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.verifyPage(formParameters);
        enterDataPage.enterAccountValue(0,0,99.0);
        enterDataPage.enterAccountValue(1,1,0.0);
        enterDataPage.enterAccountValue(2,0,0.0);
        CollectionSheetEntryPreviewDataPage previewPage = 
            enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        CollectionSheetEntryConfirmationPage confirmationPage = 
            previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
        confirmationPage.verifyPage();
        
        verifyCollectionSheetData("acceptance_small_002_dbunit.xml");
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyCollectionSheetData(String filename) throws Exception {
        Connection jdbcConnection = null;
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            IDatabaseTester databaseTester = new DataSourceDatabaseTester(dataSource);
            IDatabaseConnection databaseConnection = databaseTester.getConnection();
            IDataSet databaseDataSet = databaseConnection.createDataSet();
            IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromFile(filename);
            
            dbUnitUtilities.verifyTable("ACCOUNT_PAYMENT", databaseDataSet, expectedDataSet);   
            dbUnitUtilities.verifyTable("ACCOUNT_TRXN", databaseDataSet, expectedDataSet);  
            // the ordering of the financial transactions varies per test run,
            // so sort the columns to get a fixed order
            String[] orderByColumns = 
                new String[]{"account_trxn_id","fin_action_id","debit_credit_flag"};   
            dbUnitUtilities.verifySortedTable("FINANCIAL_TRXN", databaseDataSet, expectedDataSet, 
                orderByColumns);
            dbUnitUtilities.verifyTable("LOAN_ACTIVITY_DETAILS", databaseDataSet, expectedDataSet);  
            dbUnitUtilities.verifyTable("LOAN_SCHEDULE", databaseDataSet, expectedDataSet);  
            dbUnitUtilities.verifyTable("LOAN_TRXN_DETAIL", databaseDataSet, expectedDataSet);              
            // Note: CUSTOMER_ATTENDANCE is updated but we are not verifying it in this test
        }
        finally {
            jdbcConnection.close();
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }


    private CollectionSheetEntrySelectPage loginAndNavigateToCollectionSheetEntrySelectPage(String userName, String password) {
        return appLauncher
         .launchMifos()
         .loginSuccessfulAs(userName, password)
         .navigateToClientsAndAccountsUsingHeaderTab()
         .navigateToEnterCollectionSheetDataUsingLeftMenu();
    }

    
}

