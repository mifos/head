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

package org.mifos.test.acceptance.loan;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.AppLauncher;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewInstallmentDetailsPage;
import org.mifos.test.acceptance.framework.login.LoginPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class WaiveLoanFeeTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    private AppLauncher appLauncher;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        appLauncher = new AppLauncher(selenium);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled=false)  // issue 2397 needs to be resolved for this test to pass
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void waiveFeeOnLastInstallment() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_006_dbunit.xml.zip", dataSource, selenium);
        LoginPage loginPage = appLauncher.launchMifos();
        HomePage homePage = loginPage.loginSuccessfullyUsingDefaultCredentials();
        SearchResultsPage searchResultsPage = homePage.search("000100000000215");
        searchResultsPage.verifyPage();
        LoanAccountPage loanAccountPage = searchResultsPage.navigateToLoanAccountDetailPage("000100000000215");
        ViewInstallmentDetailsPage viewInstallmentDetailsPage = loanAccountPage.navigateToViewInstallmentDetails();
        viewInstallmentDetailsPage.verifyInstallmentAmount(11, 2, "16.0");
        viewInstallmentDetailsPage.waiveOverdueInstallmentFee();
        viewInstallmentDetailsPage.verifyInstallmentAmount(11, 2, "0.0");
    }


}
