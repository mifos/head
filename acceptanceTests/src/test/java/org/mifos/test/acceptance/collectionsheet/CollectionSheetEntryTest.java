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

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dbunit.Assertion;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.mifos.core.MifosRuntimeException;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"CollectionSheetEntryTest","acceptance","ui"})
public class CollectionSheetEntryTest extends UiTestCaseBase {

    private AppLauncher appLauncher;

    @Autowired
    private DriverManagerDataSource dataSource;
    
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
        
        loadDataFromFile("acceptance_small_001_dbunit.xml");
        
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
            IDataSet expectedDataSet = getDataSetFromFile(filename);
            
            verifyTable("ACCOUNT_PAYMENT", databaseDataSet, expectedDataSet);   
            verifyTable("ACCOUNT_TRXN", databaseDataSet, expectedDataSet);  
            // the ordering of the financial transactions is not fixed, attempting
            // to get a fixed order by sorting columns-- not yet working
            // verifySortedTable("FINANCIAL_TRXN", databaseDataSet, expectedDataSet, 
            //    new String[]{"account_trxn_id","fin_action_id","glcode_id"});  
            verifyTable("LOAN_ACTIVITY_DETAILS", databaseDataSet, expectedDataSet);  
            verifyTable("LOAN_SCHEDULE", databaseDataSet, expectedDataSet);  
            verifyTable("LOAN_TRXN_DETAIL", databaseDataSet, expectedDataSet);              
            // Note: CUSTOMER_ATTENDANCE is updated but we are not verifying it in this test
        }
        finally {
            jdbcConnection.close();
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }

    // TODO: Refactor this to better encapsulate this information + move it to a utility class
    static Map<String, String[]> columnsToIgnore = new HashMap<String, String[]>();
    static {
        columnsToIgnore.put("ACCOUNT_PAYMENT", new String[] { "payment_id","payment_date" });
        columnsToIgnore.put("ACCOUNT_TRXN", new String[] { "account_trxn_id","created_date","action_date","payment_id" });        
        columnsToIgnore.put("FINANCIAL_TRXN", new String[] { "trxn_id","action_date", "account_trxn_id","balance_amount" });        
        columnsToIgnore.put("LOAN_ACTIVITY_DETAILS", new String[] { "id","created_date" });        
        columnsToIgnore.put("LOAN_SCHEDULE", new String[] { "id","payment_date" });        
        columnsToIgnore.put("LOAN_TRXN_DETAIL", new String[] { "account_trxn_id" });        
    }

    private void verifyTable(String tableName, IDataSet databaseDataSet, IDataSet expectedDataSet) throws DataSetException,
            DatabaseUnitException {
        
        Assert.assertNotNull(columnsToIgnore.get(tableName), "Didn't find requested table [" + tableName + "] in columnsToIgnore map.");
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);
        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, 
                expectedTable.getTableMetaData().getColumns());
        Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnore.get(tableName));
    }

    /*  referenced by temporarily commented out code above
    private void verifySortedTable(String tableName, IDataSet databaseDataSet, 
            IDataSet expectedDataSet, String[] sortingColumns) throws DataSetException,
    DatabaseUnitException {

        Assert.assertNotNull(columnsToIgnore.get(tableName), "Didn't find requested table [" + tableName + "] in columnsToIgnore map.");
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);
        SortedTable sortedExpectedTable = new SortedTable(expectedTable, sortingColumns);
        sortedExpectedTable.setUseComparable(true);
        expectedTable = sortedExpectedTable;
        SortedTable sortedActualTable = new SortedTable(actualTable, sortingColumns);
        sortedActualTable.setUseComparable(true);
        actualTable = sortedActualTable;
        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, 
                expectedTable.getTableMetaData().getColumns());
        
        Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnore.get(tableName));
    }
    */
    
    private CollectionSheetEntrySelectPage loginAndNavigateToCollectionSheetEntrySelectPage(String userName, String password) {
        return appLauncher
         .launchMifos()
         .loginSuccessfulAs(userName, password)
         .navigateToClientsAndAccountsUsingHeaderTab()
         .navigateToEnterCollectionSheetDataUsingLeftMenu();
    }

    private void loadDataFromFile(String filename) throws DatabaseUnitException, SQLException, IOException {
        Connection jdbcConnection = null;
        IDataSet dataSet = getDataSetFromFile(filename);
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        }
        finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }

    private IDataSet getDataSetFromFile(String filename) throws IOException, DataSetException {
        boolean enableColumnSensing = true;
        URL url = DbUnitResource.getInstance().getUrl(filename);
        if (url == null) {
            throw new MifosRuntimeException("Couldn't find file:" + filename);
        }
        return new FlatXmlDataSet(url,false,enableColumnSensing);
    }

    
}

