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
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"collectionsheet","acceptance","ui"})
public class CollectionSheetEntryCustomerAccountTest extends UiTestCaseBase {

    public static final String FEE_TRXN_DETAIL = "FEE_TRXN_DETAIL";
    public static final String FINANCIAL_TRXN = "FINANCIAL_TRXN";
    public static final String CUSTOMER_ACCOUNT_ACTIVITY = "CUSTOMER_ACCOUNT_ACTIVITY";
    public static final String CUSTOMER_TRXN_DETAIL = "CUSTOMER_TRXN_DETAIL";
    public static final String ACCOUNT_TRXN = "ACCOUNT_TRXN";
    public static final String LOAN_TRXN_DETAIL = "LOAN_TRXN_DETAIL";
    public static final String ACCOUNT_PAYMENT = "ACCOUNT_PAYMENT";
    public static final String LOAN_SUMMARY = "LOAN_SUMMARY";
    public static final String LOAN_SCHEDULE = "LOAN_SCHEDULE";
    public static final String LOAN_ACTIVITY_DETAILS = "LOAN_ACTIVITY_DETAILS";
    public static final String ACCOUNT_STATUS_CHANGE_HISTORY = "ACCOUNT_STATUS_CHANGE_HISTORY";


    private static final double[] BASIC_CUSTOMER_ACCT_VALUES = new double[] {  17.0, 77.0, 123.0, 217.0, 44.0 };
    private static final double[] FIRST_PARTIAL_CUSTOMER_ACCT_VALUES = new double[] { 17.0, 0.0, 123.0, 0.0, 44.0 };


    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,2,23,1,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }


    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
         new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @Test(sequential = true, groups = {"smoke"})
    public void clientAccountFeesSavedToDatabase() throws Exception {
        try {
            SubmitFormParameters formParameters = getFormParametersForTestOffice();
            initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
            enterAndSubmitCustomerAccountData(formParameters, BASIC_CUSTOMER_ACCT_VALUES);
            verifyCollectionSheetData("ColSheetCustAcct_001_result_dbunit.xml.zip");
        } catch (Error e) {
            dbUnitUtilities.dumpDatabaseToTimestampedFileInConfigurationDirectory(dataSource);
            throw e;
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void previousPaidFeeNotDisplayedOnSecondCollectionSheetEntry() throws Exception {
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndSubmitCustomerAccountData(formParameters, BASIC_CUSTOMER_ACCT_VALUES);
        //navigate back to collection sheet entry
        HomePage homePage = confirmationPage.navigateToHomePage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CollectionSheetEntrySelectPage selectPage = clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
        selectPage.verifyPage();
        //enter same search data and inspect displayed client account (A/C Collection) values
        CollectionSheetEntryEnterDataPage enterDataPage = selectCenterAndContinue(formParameters, selectPage);
        enterDataPage.verifyCustomerAccountValue(0, 6, 0.0);
        enterDataPage.verifyCustomerAccountValue(1, 6, 0.0);
        enterDataPage.verifyCustomerAccountValue(2, 6, 0.0);
        enterDataPage.verifyCustomerAccountValue(3, 6, 0.0);
        enterDataPage.verifyCustomerAccountValue(4, 6, 0.0);
        enterDataPage.cancel();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void unpaidFeeDisplayedOnSecondCollectionSheetEntryAndSaved() throws Exception {
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        CollectionSheetEntryConfirmationPage confirmationPage = enterAndSubmitCustomerAccountData(formParameters, FIRST_PARTIAL_CUSTOMER_ACCT_VALUES);
        //navigate back to collection sheet entry
        HomePage homePage = confirmationPage.navigateToHomePage();
        ClientsAndAccountsHomepage clientsAndAccountsPage = homePage.navigateToClientsAndAccountsUsingHeaderTab();
        CollectionSheetEntrySelectPage selectPage = clientsAndAccountsPage.navigateToEnterCollectionSheetDataUsingLeftMenu();
        selectPage.verifyPage();
        //enter same search data and inspect displayed client account (A/C Collection) values
        CollectionSheetEntryEnterDataPage enterDataPage = selectCenterAndContinue(formParameters, selectPage);
        enterDataPage.verifyCustomerAccountValue(0, 6, 0.0);
        enterDataPage.verifyCustomerAccountValue(1, 6, 77.0);
        enterDataPage.verifyCustomerAccountValue(2, 6, 0.0);
        enterDataPage.verifyCustomerAccountValue(3, 6, 217.0);
        enterDataPage.verifyCustomerAccountValue(4, 6, 0.0);
        enterDataPage.cancel();

    }

    private CollectionSheetEntryConfirmationPage enterAndSubmitCustomerAccountData(SubmitFormParameters formParameters, double[] customerAcctValues) {
        CollectionSheetEntrySelectPage selectPage =
            new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = selectCenterAndContinue(formParameters, selectPage);

        enterFirstGroupCustomerAccountValues(enterDataPage, customerAcctValues);
        enterGenericCustomerAccountValues(enterDataPage);

        CollectionSheetEntryPreviewDataPage previewPage =
            enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        CollectionSheetEntryConfirmationPage confirmationPage =
            previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
        confirmationPage.verifyPage();
        return confirmationPage;
    }


    private CollectionSheetEntryEnterDataPage selectCenterAndContinue(SubmitFormParameters formParameters,
            CollectionSheetEntrySelectPage selectPage) {
        CollectionSheetEntryEnterDataPage enterDataPage =
            selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.verifyPage();
        return enterDataPage;
    }


    private void enterFirstGroupCustomerAccountValues(CollectionSheetEntryEnterDataPage enterDataPage, double[] customerAcctValues) {
        // first Group's information...
        enterDataPage.enterAccountValue(0,0,0.0);
        enterDataPage.enterAccountValue(0,1,183.0);
        enterDataPage.enterDepositAccountValue(0,2,0.0);
        enterDataPage.enterCustomerAccountValue(0, 6, customerAcctValues[0]);

        enterDataPage.enterAccountValue(1,0,0.0);
        enterDataPage.enterAccountValue(1,1,183.0);
        enterDataPage.enterDepositAccountValue(1,2,0.0);
        enterDataPage.enterCustomerAccountValue(1, 6, customerAcctValues[1]);

        enterDataPage.enterAccountValue(2,0,0.0);
        enterDataPage.enterAccountValue(2,1,183.0);
        enterDataPage.enterDepositAccountValue(2,2,0.0);
        enterDataPage.enterCustomerAccountValue(2, 6, customerAcctValues[2]);

        enterDataPage.enterAccountValue(3,0,0.0);
        enterDataPage.enterAccountValue(3,1,183.0);
        enterDataPage.enterDepositAccountValue(3,2,0.0);
        enterDataPage.enterCustomerAccountValue(3, 6, customerAcctValues[3]);

        enterDataPage.enterCustomerAccountValue(4, 6, customerAcctValues[4]);
    }

    private void enterGenericCustomerAccountValues(CollectionSheetEntryEnterDataPage enterDataPage) {

        // second Group's information...
        enterDataPage.enterAccountValue(5,0,0.0);
        enterDataPage.enterAccountValue(5,1,183.0);
        enterDataPage.enterDepositAccountValue(5,2,0.0);
        enterDataPage.enterCustomerAccountValue(5, 6, 0.0);

        enterDataPage.enterAccountValue(6,0,0.0);
        enterDataPage.enterAccountValue(6,1,183.0);
        enterDataPage.enterDepositAccountValue(6,2,0.0);
        enterDataPage.enterCustomerAccountValue(6, 6, 0.0);

        enterDataPage.enterAccountValue(7,0,0.0);
        enterDataPage.enterAccountValue(7,1,183.0);
        enterDataPage.enterDepositAccountValue(7,2,0.0);
        enterDataPage.enterCustomerAccountValue(7, 6, 0.0);

        enterDataPage.enterAccountValue(8,0,0.0);
        enterDataPage.enterAccountValue(8,1,183.0);
        enterDataPage.enterDepositAccountValue(8,2,0.0);
        enterDataPage.enterCustomerAccountValue(8, 6, 0.0);

        enterDataPage.enterCustomerAccountValue(9, 6, 0.0);

        // third Group's information...
        enterDataPage.enterAccountValue(10,0,0.0);
        enterDataPage.enterAccountValue(10,1,183.0);
        enterDataPage.enterDepositAccountValue(10,2,0.0);
        enterDataPage.enterCustomerAccountValue(10, 6, 0.0);

        enterDataPage.enterAccountValue(11,0,0.0);
        enterDataPage.enterAccountValue(11,1,183.0);
        enterDataPage.enterDepositAccountValue(11,2,0.0);
        enterDataPage.enterCustomerAccountValue(11, 6, 0.0);

        enterDataPage.enterAccountValue(12,0,0.0);
        enterDataPage.enterAccountValue(12,1,183.0);
        enterDataPage.enterDepositAccountValue(12,2,0.0);
        enterDataPage.enterCustomerAccountValue(12, 6, 0.0);

        enterDataPage.enterAccountValue(13,0,0.0);
        enterDataPage.enterAccountValue(13,1,183.0);
        enterDataPage.enterDepositAccountValue(13,2,0.0);
        enterDataPage.enterCustomerAccountValue(13, 6, 0.0);

        enterDataPage.enterCustomerAccountValue(14, 6, 0.0);

        // center charges
        enterDataPage.enterCustomerAccountValue(15, 6, 234.0);
    }

    private SubmitFormParameters getFormParametersForTestOffice() {
        SubmitFormParameters formParameters = new SubmitFormParameters();
      formParameters.setBranch("MyOffice1233265929385");
      formParameters.setLoanOfficer("Joe1233265931256 Guy1233265931256");
      formParameters.setCenter("MyCenter1233265933427");
      formParameters.setPaymentMode("Cash");
        return formParameters;
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyCollectionSheetData(String filename) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(filename);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { FEE_TRXN_DETAIL,
                                   ACCOUNT_TRXN,
                                   LOAN_TRXN_DETAIL,
                                   ACCOUNT_PAYMENT,
                                   LOAN_SUMMARY,
                                   LOAN_SCHEDULE,
                                   LOAN_ACTIVITY_DETAILS,
                                   ACCOUNT_STATUS_CHANGE_HISTORY,
                                   FINANCIAL_TRXN,
                                   CUSTOMER_ACCOUNT_ACTIVITY,
                                   CUSTOMER_TRXN_DETAIL });

        verifyTransactionsAfterSortingTables(expectedDataSet, databaseDataSet);
    }

    private void verifyTransactionsAfterSortingTables(IDataSet expectedDataSet, IDataSet databaseDataSet)
            throws DataSetException, DatabaseUnitException {
        String[] orderFinTrxnByColumns =  new String[]{"posted_amount", "glcode_id"};
        dbUnitUtilities.verifyTableWithSort(orderFinTrxnByColumns,CollectionSheetEntryCustomerAccountTest.FINANCIAL_TRXN, expectedDataSet, databaseDataSet );
        String [] orderFeeTrxnByColumns = new String[]{"fee_trxn_detail_id","account_trxn_id", "account_fee_id"};
        dbUnitUtilities.verifyTableWithSort(orderFeeTrxnByColumns,CollectionSheetEntryCustomerAccountTest.FEE_TRXN_DETAIL, expectedDataSet, databaseDataSet );
        String [] orderCustTrxnByColumns = new String[] {"total_amount"};
        dbUnitUtilities.verifyTableWithSort(orderCustTrxnByColumns, CollectionSheetEntryCustomerAccountTest.CUSTOMER_TRXN_DETAIL, expectedDataSet, databaseDataSet);
        String [] orderAcctTrxnByColumns = new String[] {"amount", "customer_id", "account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAcctTrxnByColumns, CollectionSheetEntryCustomerAccountTest.ACCOUNT_TRXN, expectedDataSet, databaseDataSet);
        String [] orderLoanTrxnDetailByColumns = new String[] {"principal_amount","account_trxn_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanTrxnDetailByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_TRXN_DETAIL, expectedDataSet, databaseDataSet);
        String [] orderAccountPaymentByColumns = new String[] {"amount","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAccountPaymentByColumns,CollectionSheetEntryCustomerAccountTest.ACCOUNT_PAYMENT, expectedDataSet, databaseDataSet);
        String [] orderLoanSummaryByColumns = new String[] {"raw_amount_total","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanSummaryByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_SUMMARY, expectedDataSet, databaseDataSet);
        String [] orderLoanScheduleByColumns = new String[] {"principal","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanScheduleByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_SCHEDULE, expectedDataSet, databaseDataSet);
        String [] orderLoanActivityDetailsByColumns = new String[] {"principal_amount","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanActivityDetailsByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_ACTIVITY_DETAILS, expectedDataSet, databaseDataSet);
        String [] orderAccountStatusChangeHistoryByColumns = new String[] {"account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAccountStatusChangeHistoryByColumns,CollectionSheetEntryCustomerAccountTest.ACCOUNT_STATUS_CHANGE_HISTORY, expectedDataSet, databaseDataSet);
        String [] orderCustomerAccountActivityByColumns = new String[] {"account_id"};
        dbUnitUtilities.verifyTableWithSort(orderCustomerAccountActivityByColumns,CollectionSheetEntryCustomerAccountTest.CUSTOMER_ACCOUNT_ACTIVITY, expectedDataSet, databaseDataSet);
     }
}
