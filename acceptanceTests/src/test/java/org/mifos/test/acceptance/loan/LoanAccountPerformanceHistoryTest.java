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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSubmitParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.PerformanceHistoryAtributes;
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
@Test(sequential=true, groups={"loan","acceptance","ui","smoke"})
public class LoanAccountPerformanceHistoryTest extends UiTestCaseBase {

    //private static final String CLIENT_PERFORMANCE_HISTORY = "CLIENT_PERF_HISTORY";
    //private AppLauncher appLauncher;
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        //appLauncher = new AppLauncher(selenium);

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,2,7,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-359
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void repayMultipleLoansAndVerifyPerformanceHistory() throws Exception {
        //Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_011_dbunit.xml", dataSource, selenium);
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        searchParameters.setLoanProduct("WeeklyFlatLoanWithOneTimeFees");
        searchParameters.setSearchString("Stu1233171716380");
        submitAccountParameters.setInterestRate("3.0");
        submitAccountParameters.setNumberOfInstallments("11");
        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setDisbursalDateDD("07");
        disburseParameters.setDisbursalDateMM("02");
        disburseParameters.setDisbursalDateYYYY("2009");
        disburseParameters.setPaymentType(PaymentParameters.CASH);
        PerformanceHistoryAtributes performanceHistoryAtributes = new PerformanceHistoryAtributes();
        performanceHistoryAtributes.setDelinquentPortfolio(0.0);
        loanTestHelper.editLoanProductIncludeInLoanCounter("WeeklyFlatLoanWithOneTimeFees", true);
        loanTestHelper.editLoanProductIncludeInLoanCounter("MyLoanProduct1232993826860", true);

        //When
        Map<String,String> loanIds = new HashMap<String,String>();
        submitAccountParameters.setAmount("2000.0");
        loanIds.put(submitAccountParameters.getAmount(), loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId());
        performanceHistoryAtributes.incrementLoanCycle();
        performanceHistoryAtributes.incrementLoanCycleForProduct(searchParameters.getLoanProduct());
        submitAccountParameters.setAmount("3000.0");
        loanIds.put(submitAccountParameters.getAmount(), loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId());
        performanceHistoryAtributes.incrementLoanCycle();
        performanceHistoryAtributes.incrementLoanCycleForProduct(searchParameters.getLoanProduct());
        submitAccountParameters.setAmount("5000.0");
        searchParameters.setLoanProduct("MyLoanProduct1232993826860");
        loanIds.put(submitAccountParameters.getAmount(), loanTestHelper.createLoanAccount(searchParameters, submitAccountParameters).getAccountId());
        performanceHistoryAtributes.incrementLoanCycle();
        performanceHistoryAtributes.incrementLoanCycleForProduct(searchParameters.getLoanProduct());
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setStatus(EditLoanAccountStatusParameters.APPROVED);
        params.setNote("Approved.");
        Set<String>amounts = loanIds.keySet();
        for (String accountid : loanIds.values()) {
            loanTestHelper.changeLoanAccountStatus(accountid, params);
            loanTestHelper.disburseLoan(accountid, disburseParameters);
            performanceHistoryAtributes.incrementNoOfActiveLoan();
        }

        //Then
        for (String amount : amounts) {
            loanTestHelper.repayLoan(loanIds.get(amount));
            performanceHistoryAtributes.setAmountOfLastLoan(amount);
            performanceHistoryAtributes.decrementNoOfActiveLoan();
            loanTestHelper.verifyPerformenceHistory(searchParameters.getSearchString(), performanceHistoryAtributes);
        }

    }
}
