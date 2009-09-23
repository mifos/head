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

package org.mifos.test.acceptance.savings;

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"savings","acceptance","ui"})
public class SavingsAccountPerformanceHistoryTest extends UiTestCaseBase {
    
    private SavingsAccountHelper savingsAccountHelper;
    
    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

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
    public void savingsDepositWithdrawalAndVerifyPerformanceHistory() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml.zip", dataSource, selenium);
        
        DepositWithdrawalSavingsParameters params = new DepositWithdrawalSavingsParameters();
        
        params.setTrxnDateMM("09");
        params.setTrxnDateDD("09");
        params.setTrxnDateYYYY("2009");
        params.setAmount("888.8");
        params.setPaymentType(DepositWithdrawalSavingsParameters.CASH);
        params.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);
        
        // deposit initial amount to savings account
        SavingsAccountDetailPage savingsAccountDetailPage = savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000119", params);
        savingsAccountDetailPage.verifyPage();
        
        // withdraw portion of savings
        params.setAmount("123.0");
        params.setTrxnType(DepositWithdrawalSavingsParameters.WITHDRAWAL);
        
        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000119", params);
        
        // another deposit
        params.setAmount("10.0");
        params.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);
        
        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000119", params);
        
        // another withdrawal
        params.setAmount("20.0");
        params.setTrxnType(DepositWithdrawalSavingsParameters.WITHDRAWAL);
        
        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000119", params);
        // validate savings performance history 
        String[] tablesToValidate = { "SAVINGS_ACCOUNT", "SAVINGS_PERFORMANCE" };
       
        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile("SavingsPerformanceHistory_001_dbunit.xml.zip");
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);
        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);

    }
}
