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

package org.mifos.test.acceptance.collectionsheet;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"workInProgress", "CollectionSheetEntryTest","acceptance","ui"})
public class CollectionSheetEntryCustomerAccountTest extends UiTestCaseBase {

    private static final String FEE_TRXN_DETAIL = "FEE_TRXN_DETAIL";
    private static final String FINANCIAL_TRXN = "FINANCIAL_TRXN";
    private static final String CUSTOMER_ACCOUNT_ACTIVITY = "CUSTOMER_ACCOUNT_ACTIVITY";
    private static final String CUSTOMER_TRXN_DETAIL = "CUSTOMER_TRXN_DETAIL";

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    
    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }
  

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void clientAccountFeesSavedToDatabase() throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("MyOffice1233265929385");
        formParameters.setLoanOfficer("Joe1233265931256 Guy1233265931256");
        formParameters.setCenter("MyCenter1233265933427");
        formParameters.setPaymentMode("Cash");
        
        dbUnitUtilities.loadDataFromFile("acceptance_small_003_dbunit.xml.zip", dataSource);
        
        CollectionSheetEntrySelectPage selectPage = 
            new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = 
            selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.verifyPage();

        // submit fee information...

        CollectionSheetEntryPreviewDataPage previewPage = 
            enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        CollectionSheetEntryConfirmationPage confirmationPage = 
            previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
        confirmationPage.verifyPage();
        
        verifyCollectionSheetData("ColSheetCustAcct_001_result_dbunit.zip");

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyCollectionSheetData(String filename) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromFile(filename);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { FEE_TRXN_DETAIL,
                                   FINANCIAL_TRXN,
                                   CUSTOMER_ACCOUNT_ACTIVITY,
                                   CUSTOMER_TRXN_DETAIL });
        verifyTablesWithoutSorting(expectedDataSet, databaseDataSet);   
        verifyTransactionsAfterSortingTables(expectedDataSet, databaseDataSet);

    }
    
    private void verifyTablesWithoutSorting(IDataSet expectedDataSet, IDataSet databaseDataSet) throws DataSetException,
    DatabaseUnitException {
        dbUnitUtilities.verifyTables(new String[] { CUSTOMER_ACCOUNT_ACTIVITY }, databaseDataSet, expectedDataSet);
    }

    private void verifyTransactionsAfterSortingTables(IDataSet expectedDataSet, IDataSet databaseDataSet)
            throws DataSetException, DatabaseUnitException {
        String[] orderFinTrxnByColumns = 
            new String[]{"account_trxn_id","glcode_id","fin_action_id","debit_credit_flag"};  
        dbUnitUtilities.verifySortedTableWithOrdering(FINANCIAL_TRXN, databaseDataSet, expectedDataSet, 
                orderFinTrxnByColumns, false, false);
        String [] orderFeeTrxnByColumns = new String[]{"fee_trxn_detail_id", "account_trxn_id", "account_fee_id"};
        dbUnitUtilities.verifySortedTableWithOrdering(FEE_TRXN_DETAIL, databaseDataSet, expectedDataSet, 
                orderFeeTrxnByColumns, true, true);
        String [] orderCustTrxnByColumns = new String[] {"account_trxn_id"};
        dbUnitUtilities.verifySortedTableWithOrdering(CUSTOMER_TRXN_DETAIL, databaseDataSet, expectedDataSet, 
                orderCustTrxnByColumns, true, true);
        
        
      
     }
    
}
