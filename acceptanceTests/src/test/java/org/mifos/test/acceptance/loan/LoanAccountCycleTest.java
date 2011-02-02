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

import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.LoanProductDetailsPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
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
@Test(sequential=true, groups={"acceptance", "ui", "loan"})
public class LoanAccountCycleTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011,2,2,15,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Verify loan amount with number of installments is same for all loan can be used to create new loans.
     * http://mifosforge.jira.com/browse/MIFOSTEST-105
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyNumberOfInstallmentsSameForAllLoans() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product105");
        productParams.setOfferingShortName("p105");
        productParams.setCalculateLoanAmount(SubmitFormParameters.BY_LOAN_CYCLE);
        String[][] cycleLoanAmount = {
            {"1000.0", "5000.0", "3000.0"},
            {"2000.0", "6000.0", "4000.0"},
            {"3000.0", "7000.0", "5000.0"},
            {"4000.0", "8000.0", "6000.0"},
            {"5000.0", "9000.0", "7000.0"},
            {"6000.0", "10000.0", "8000.0"}
        };
        productParams.setCycleLoanAmount(cycleLoanAmount);
        productParams.setMinInstallemnts("10");
        productParams.setMaxInstallments("100");
        productParams.setDefInstallments("50");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233266053368 Client1233266053368");
        searchParams.setLoanProduct("product105");
        DisburseLoanParameters disburseParams = DisburseLoanParameters.getDisbursalParameters("02", "02", "2011");

        LoanProductDetailsPage loanProductDetailsPage = loanTestHelper.defineNewLoanProduct(productParams);
        loanProductDetailsPage.verifyLoanAmountTableTypeFromCycle(cycleLoanAmount);
        loanProductDetailsPage.verifyInstallments("10", "100", "50");
        LoanAccountPage loanAccountPage = loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1000.0", "5000.0", "3000.0"}, null, new String[]{"10", "100", "50"});
        String loanFirstID = loanAccountPage.getAccountId();
        loanAccountPage.disburseLoan(disburseParams);
        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"1000.0", "5000.0", "3000.0"}, null, new String[]{"10", "100", "50"});
        loanTestHelper.repayLoan(loanFirstID);

        loanTestHelper.createWithVerificationAndActivationLoanAccount(searchParams, new String[]{"2000.0", "6000.0", "4000.0"}, null, new String[]{"10", "100", "50"});
    }
}
