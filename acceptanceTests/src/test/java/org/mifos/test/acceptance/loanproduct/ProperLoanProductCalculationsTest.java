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

package org.mifos.test.acceptance.loanproduct;


import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"loanproduct", "acceptance"})
public class ProperLoanProductCalculationsTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    private LoanProductTestHelper loanProductTestHelper;
    private LoanTestHelper loanTestHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        loanProductTestHelper = new LoanProductTestHelper(selenium);
        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Flat interest rate weekly loan calculates and displays proper interest and payment information.
     * http://mifosforge.jira.com/browse/MIFOSTEST-63
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyProperInterestAndPaymentWeeklyProduct() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml", dataSource, selenium);

        DefineNewLoanProductPage.SubmitFormParameters productParams = FormParametersHelper.getWeeklyLoanProductParameters();
        productParams.setOfferingName("product63");
        productParams.setOfferingShortName("p63");
        productParams.setDefaultInterestRate("36");
        productParams.setMaxInterestRate("50");
        productParams.setInterestTypes(SubmitFormParameters.FLAT);
        productParams.setDefaultLoanAmount("2500");
        productParams.setDefInstallments("11");
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setSearchString("Stu1233266063395 Client1233266063395");
        searchParams.setLoanProduct("product63");

        loanProductTestHelper.defineNewLoanProduct(productParams);
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);

        loanAccountPage.verifyPrincipalOriginal("2500.0");
        loanAccountPage.verifyInterestOriginal("190.0");
        loanAccountPage.verifyFeesOriginal("0.0");
        loanAccountPage.verifyPenaltyOriginal("0.0");
        loanAccountPage.verifyTotalOriginalLoan("2690.0");
    }
}
