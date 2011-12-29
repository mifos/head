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

package org.mifos.test.acceptance.loan;

import org.joda.time.DateTime;
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
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class LoanAccountPerformanceHistoryTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        //appLauncher = new AppLauncher(selenium);

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 04, 1, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    // http://mifosforge.jira.com/browse/MIFOSTEST-359
    @Test(enabled=true)
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void repayMultipleLoansAndVerifyPerformanceHistory() throws Exception {
        //Given
        CreateLoanAccountSearchParameters searchParameters = new CreateLoanAccountSearchParameters();
        CreateLoanAccountSubmitParameters submitAccountParameters = new CreateLoanAccountSubmitParameters();
        searchParameters.setLoanProduct("WeeklyClientFlatLoanWithNoFee");
        searchParameters.setSearchString("WeeklyOld Monday");
        submitAccountParameters.setInterestRate("24.0");
        submitAccountParameters.setNumberOfInstallments("10");
        submitAccountParameters.setLoanPurpose("0008-Animal Trading");
        DisburseLoanParameters disburseParameters = new DisburseLoanParameters();
        disburseParameters.setDisbursalDateDD("04");
        disburseParameters.setDisbursalDateMM("03");
        disburseParameters.setDisbursalDateYYYY("2011");
        disburseParameters.setPaymentType(PaymentParameters.CASH);
        PerformanceHistoryAtributes performanceHistoryAtributes = new PerformanceHistoryAtributes();
        performanceHistoryAtributes.setDelinquentPortfolio(0.0);

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
        searchParameters.setLoanProduct("AnotherWeeklyClientFlatLoanWithNoFee");
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
            
            String lastLoan = amount.substring(0, amount.length() - 2);
            lastLoan = lastLoan.substring(0, 1) + "," + lastLoan.substring(1);
            
            performanceHistoryAtributes.setAmountOfLastLoan(lastLoan);
            performanceHistoryAtributes.decrementNoOfActiveLoan();
            loanTestHelper.verifyPerformenceHistory(searchParameters.getSearchString(), performanceHistoryAtributes);
        }

    }
}
