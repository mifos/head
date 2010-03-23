/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.collectionsheet.CollectionSheetEntryCustomerAccountTest;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
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
public class ClientLoanTransactionHistoryTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

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


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,2,7,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void transactionHistoryTest() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);

        /*
         * This test consists of:
         * 1. Make a single payment of the fee, 900
         * 2. Make a single payment of the principal and interest, 183.
         * 3. Verify the results (loan_trxn_detail table).
         *
         * The data set contains a loan w/ id 000100000000174 that's disbursed and who's loan
         * product contains a fee.
         */

        PaymentParameters paymentParameters = new PaymentParameters();
        paymentParameters.setAmount("990"); // this covers the fee (990)
        paymentParameters.setTransactionDateDD("06");
        paymentParameters.setTransactionDateMM("02");
        paymentParameters.setTransactionDateYYYY("2009");
        paymentParameters.setPaymentType(PaymentParameters.CASH);

        loanTestHelper.applyPayment("000100000000174", paymentParameters);

        // use the same date and payment type for the principal and the interest
        paymentParameters.setAmount("183");
        loanTestHelper.applyPayment("000100000000174", paymentParameters);

        verifyCollectionSheetData("ClientLoanTransactionHistory_001_result_dbunit.xml.zip");
    }


    // the code below is the same that's used in CollectionSheetEntryCustomerAccountTest.

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    private void verifyCollectionSheetData(String filename) throws Exception {
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(filename);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, new String[] { FEE_TRXN_DETAIL,
                                   ACCOUNT_TRXN,
                                   LOAN_TRXN_DETAIL,
                                   ACCOUNT_PAYMENT,
                                   LOAN_SCHEDULE,
                                   LOAN_SUMMARY,
                                   LOAN_ACTIVITY_DETAILS,
                                   ACCOUNT_STATUS_CHANGE_HISTORY,
                                   FINANCIAL_TRXN,
                                   CUSTOMER_ACCOUNT_ACTIVITY,
                                   CUSTOMER_TRXN_DETAIL });


        verifyTablesWithoutSorting(expectedDataSet, databaseDataSet);

        /* The order that the transactions are entered into the database (and accordingly the
         * id that they're assigned) is as far as I can tell random. This means that the sorting
         * is random and not verifyable.
         */
        //verifyTransactionsAfterSortingTables(expectedDataSet, databaseDataSet);

    }
    private void verifyTablesWithoutSorting(IDataSet expectedDataSet, IDataSet databaseDataSet) throws DataSetException,
    DatabaseUnitException {
        dbUnitUtilities.verifyTables(new String[] { CollectionSheetEntryCustomerAccountTest.CUSTOMER_ACCOUNT_ACTIVITY }, databaseDataSet, expectedDataSet);
    }
}