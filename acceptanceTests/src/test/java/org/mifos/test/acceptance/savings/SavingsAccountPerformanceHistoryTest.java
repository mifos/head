/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.joda.time.DateTime;
import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.savings.DepositWithdrawalSavingsParameters;
import org.mifos.test.acceptance.framework.savings.SavingsAccountDetailPage;
import org.mifos.test.acceptance.framework.testhelpers.SavingsAccountHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups={"savings","acceptance","ui", "no_db_unit"})
public class SavingsAccountPerformanceHistoryTest extends UiTestCaseBase {

    private SavingsAccountHelper savingsAccountHelper;


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,3,28,8,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        savingsAccountHelper = new SavingsAccountHelper(selenium);

    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void savingsDepositWithdrawalAndVerifyPerformanceHistory() throws Exception {
        DepositWithdrawalSavingsParameters params = new DepositWithdrawalSavingsParameters();

        params.setTrxnDateMM("28");
        params.setTrxnDateDD("03");
        params.setTrxnDateYYYY("2011");
        params.setAmount("888.8");
        params.setPaymentType(DepositWithdrawalSavingsParameters.CASH);
        params.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);

        // deposit initial amount to savings account
        SavingsAccountDetailPage savingsAccountDetailPage = savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000067", params);
        savingsAccountDetailPage.verifyPage();

        // withdraw portion of savings
        params.setAmount("123.0");
        params.setTrxnType(DepositWithdrawalSavingsParameters.WITHDRAWAL);

        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000067", params);

        // another deposit
        params.setAmount("10.0");
        params.setTrxnType(DepositWithdrawalSavingsParameters.DEPOSIT);

        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000067", params);

        // another withdrawal
        params.setAmount("20.0");
        params.setTrxnType(DepositWithdrawalSavingsParameters.WITHDRAWAL);

        savingsAccountHelper.makeDepositOrWithdrawalOnSavingsAccount("000100000000067", params);

        Assert.assertEquals("Performance history", selenium.getTable("performanceHistoryTable.0.0"));
        Assert.assertEquals("Date account opened: 14/03/2011", selenium.getTable("performanceHistoryTable.2.0"));
        Assert.assertEquals("Total deposits: 898.8", selenium.getTable("performanceHistoryTable.3.0"));
        Assert.assertEquals("Total interest earned: 0.0", selenium.getTable("performanceHistoryTable.4.0"));
        Assert.assertEquals("Total withdrawals: 143.0", selenium.getTable("performanceHistoryTable.5.0"));
    }
}
