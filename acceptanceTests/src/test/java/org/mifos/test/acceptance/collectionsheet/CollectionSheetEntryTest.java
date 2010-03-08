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
@Test(sequential=true, groups={"collectionsheet","acceptance","ui"})
public class CollectionSheetEntryTest extends UiTestCaseBase {

    private static final String FINANCIAL_TRXN = "FINANCIAL_TRXN";
    private static final String CUSTOMER_ACCOUNT_ACTIVITY = "CUSTOMER_ACCOUNT_ACTIVITY";
    private static final String ACCOUNT_TRXN = "ACCOUNT_TRXN";
    private static final String LOAN_TRXN_DETAIL = "LOAN_TRXN_DETAIL";
    private static final String ACCOUNT_PAYMENT = "ACCOUNT_PAYMENT";
    private static final String LOAN_SUMMARY = "LOAN_SUMMARY";
    private static final String LOAN_SCHEDULE = "LOAN_SCHEDULE";
    private static final String LOAN_ACTIVITY_DETAILS = "LOAN_ACTIVITY_DETAILS";
    private static final String ACCOUNT_STATUS_CHANGE_HISTORY = "ACCOUNT_STATUS_CHANGE_HISTORY";
    private static final String CUSTOMER_ATTENDANCE = "CUSTOMER_ATTENDANCE";



    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,2,23,2,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
        new DateTimeUpdaterRemoteTestingService(selenium).resetDateTime();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void defaultAdminUserSelectsValidCollectionSheetEntryParameters() throws Exception {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("Office1");
        formParameters.setLoanOfficer("Bagonza Wilson");
        formParameters.setCenter("Center1");
        formParameters.setPaymentMode("Cash");

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml.zip", dataSource, selenium);

        CollectionSheetEntrySelectPage selectPage =
            new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage =
            selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.enterAccountValue(0,0,99.0);
        enterDataPage.enterAccountValue(1,1,0.0);
        enterDataPage.enterAccountValue(2,0,0.0);
        CollectionSheetEntryPreviewDataPage previewPage =
            enterDataPage.submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        CollectionSheetEntryConfirmationPage confirmationPage =
            previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
        confirmationPage.verifyPage();

        verifyCollectionSheetData("acceptance_small_002_dbunit.xml.zip");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void twoLoansWithSameProductHasMergedLoanAmount() throws Exception {
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryEnterData(formParameters);
        //check amount due for client who has two loan accounts on the same product
        enterDataPage.verifyLoanAmountValue(3, 0, 2088.0);
        enterDataPage.cancel();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void defaultAmountsForMediumCenterSavedToDatabase() throws Exception {
        try {
            SubmitFormParameters formParameters = new SubmitFormParameters();
            formParameters.setBranch("MyOffice1233266931234");
            formParameters.setLoanOfficer("Joe1233266933656 Guy1233266933656");
            formParameters.setCenter("MyCenter1233266935468");
            formParameters.setPaymentMode("Cash");

            initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_medium_005_dbunit.xml.zip", dataSource, selenium);
            CollectionSheetEntryConfirmationPage confirmPage = new CollectionSheetEntryTestHelper(selenium).submitDefaultCollectionSheetEntryData(formParameters);
            confirmPage.verifyPage();
            verifyCollectionSheetData("ColSheetLoanTest_001_result_dbunit.xml.zip");
        } catch (Exception e) {
            dbUnitUtilities.dumpDatabaseToTimestampedFileInConfigurationDirectory(dataSource);
            throw e;
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyCollectionSheetData(String filename) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(filename);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] {
                ACCOUNT_TRXN,
                LOAN_TRXN_DETAIL,
                ACCOUNT_PAYMENT,
                LOAN_SUMMARY,
                LOAN_SCHEDULE,
                LOAN_ACTIVITY_DETAILS,
                ACCOUNT_STATUS_CHANGE_HISTORY,
                FINANCIAL_TRXN,
                CUSTOMER_ACCOUNT_ACTIVITY,
                CUSTOMER_ATTENDANCE });


        verifyTablesWithoutSorting(expectedDataSet, databaseDataSet);
        verifyTransactionsAfterSortingTables(expectedDataSet, databaseDataSet);

    }

    private void verifyTablesWithoutSorting(IDataSet expectedDataSet, IDataSet databaseDataSet) throws DataSetException,
    DatabaseUnitException {
        dbUnitUtilities.verifyTables(new String[] { CollectionSheetEntryCustomerAccountTest.CUSTOMER_ACCOUNT_ACTIVITY }, databaseDataSet, expectedDataSet);
    }

    private void verifyTransactionsAfterSortingTables(IDataSet expectedDataSet, IDataSet databaseDataSet)
            throws DataSetException, DatabaseUnitException {

        String [] orderLoanTrxnDetailByColumns = new String[] {"principal_amount","account_trxn_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanTrxnDetailByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_TRXN_DETAIL, expectedDataSet, databaseDataSet);

        String [] orderAccountPaymentByColumns = new String[] {"amount","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAccountPaymentByColumns,CollectionSheetEntryCustomerAccountTest.ACCOUNT_PAYMENT, expectedDataSet, databaseDataSet);

        String [] orderLoanSummaryByColumns = new String[] {"raw_amount_total","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanSummaryByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_SUMMARY, expectedDataSet, databaseDataSet);

        String[] orderFinTrxnByColumns = new String[] { "posted_amount", "glcode_id" };
        dbUnitUtilities.verifyTableWithSort(orderFinTrxnByColumns,CollectionSheetEntryCustomerAccountTest.FINANCIAL_TRXN, expectedDataSet, databaseDataSet );

        String [] orderAcctTrxnByColumns = new String[] {"amount", "customer_id", "account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAcctTrxnByColumns, CollectionSheetEntryCustomerAccountTest.ACCOUNT_TRXN, expectedDataSet, databaseDataSet);

        String [] orderLoanScheduleByColumns = new String[] {"principal","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanScheduleByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_SCHEDULE, expectedDataSet, databaseDataSet);

        String [] orderLoanActivityDetailsByColumns = new String[] {"principal_amount","account_id"};
        dbUnitUtilities.verifyTableWithSort(orderLoanActivityDetailsByColumns,CollectionSheetEntryCustomerAccountTest.LOAN_ACTIVITY_DETAILS, expectedDataSet, databaseDataSet);

        String [] orderAccountStatusChangeHistoryByColumns = new String[] {"account_id"};
        dbUnitUtilities.verifyTableWithSort(orderAccountStatusChangeHistoryByColumns,CollectionSheetEntryCustomerAccountTest.ACCOUNT_STATUS_CHANGE_HISTORY, expectedDataSet, databaseDataSet);

     }

    private CollectionSheetEntryEnterDataPage navigateToCollectionSheetEntryEnterData(SubmitFormParameters formParameters) {
        CollectionSheetEntrySelectPage selectPage =
            new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage.submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        enterDataPage.verifyPage();
        return enterDataPage;
    }


    private SubmitFormParameters getFormParametersForTestOffice() {
        SubmitFormParameters formParameters = new SubmitFormParameters();
      formParameters.setBranch("MyOffice1233265929385");
      formParameters.setLoanOfficer("Joe1233265931256 Guy1233265931256");
      formParameters.setCenter("MyCenter1233265933427");
      formParameters.setPaymentMode("Cash");
        return formParameters;
    }

}

