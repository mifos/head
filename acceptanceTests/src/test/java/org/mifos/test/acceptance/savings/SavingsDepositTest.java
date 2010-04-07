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

package org.mifos.test.acceptance.savings;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"savings","acceptance","ui"})
public class SavingsDepositTest extends UiTestCaseBase {

    private SavingsAccountHelper savingsAccountHelper;
    public static final String SAVINGS_TRXN_DETAIL = "SAVINGS_TRXN_DETAIL";


    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;

    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private static final String startDataSet = "acceptance_small_008_dbunit.xml.zip";


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,9,9,8,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        savingsAccountHelper = new SavingsAccountHelper(selenium);

    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void makeDepositToClientSavingsAccount() throws Exception {
        initData();

        DepositWithdrawalSavingsParameters params = new DepositWithdrawalSavingsParameters();

        params.setTrxnDateMM("09");
        params.setTrxnDateDD("09");
        params.setTrxnDateYYYY("2009");
        params.setAmount("543.2");
        params.setPaymentType(DepositWithdrawalSavingsParameters.CASH);
        params.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);
        params.setReceiptDateDD("09");
        params.setReceiptDateMM("09");
        params.setReceiptDateYYYY("2009");

        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000036", params);

        String[] tablesToRetrieve = { "SAVINGS_ACTIVITY_DETAILS", "SAVINGS_ACCOUNT", "SAVINGS_PERFORMANCE", "SAVINGS_TRXN_DETAIL" };
        String[] tablesToValidate = { "SAVINGS_ACTIVITY_DETAILS", "SAVINGS_ACCOUNT", "SAVINGS_PERFORMANCE" };

        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile("SavingsDeposit_001_result_dbunit.xml.zip");
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToRetrieve);
        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);

        // verify savings transaction table with sorting
        String[] orderSavingsTrxnByColumns =  new String[]{"deposit_amount"};
        dbUnitUtilities.verifyTableWithSort(orderSavingsTrxnByColumns,SavingsDepositTest.SAVINGS_TRXN_DETAIL, expectedDataSet, databaseDataSet );


    }


    private void initData() throws DatabaseUnitException, SQLException, IOException, URISyntaxException {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, startDataSet, dataSource, selenium);
    }


}




