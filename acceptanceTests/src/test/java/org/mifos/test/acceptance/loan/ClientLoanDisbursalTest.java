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
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineAcceptedPaymentTypesPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(sequential = true, groups = {"acceptance", "ui", "loan", "no_db_unit"})
public class ClientLoanDisbursalTest extends UiTestCaseBase {
    private LoanTestHelper loanTestHelper;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        loanTestHelper = new LoanTestHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // http://mifosforge.jira.com/browse/MIFOSTEST-249
    public void verifyAcceptedPaymentTypesForDisbursementsOfLoan() throws Exception {
        // Given
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_008_dbunit.xml", dataSource, selenium);
        // When
        NavigationHelper navigationHelper = new NavigationHelper(selenium);
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAcceptedPaymentTypesPage defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanDisbursementsPaymentType(defineAcceptedPaymentTypesPage.CHEQUE);

        adminPage = navigationHelper.navigateToAdminPage();
        defineAcceptedPaymentTypesPage = adminPage.navigateToDefineAcceptedPaymentType();
        defineAcceptedPaymentTypesPage.addLoanDisbursementsPaymentType(defineAcceptedPaymentTypesPage.VOUCHER);

        LoanTestHelper loanTestHelper = new LoanTestHelper(selenium);
        CreateLoanAccountSearchParameters searchParams = new CreateLoanAccountSearchParameters();
        searchParams.setLoanProduct("MonthlyClientFlatLoanWithFees");
        searchParams.setSearchString("Stu1232993852651 Client1232993852651");
        LoanAccountPage loanAccountPage = loanTestHelper.createAndActivateDefaultLoanAccount(searchParams);
        DisburseLoanPage disburseLoanPage = loanAccountPage.navigateToDisburseLoan();
        //Then
        disburseLoanPage.verifyModeOfPayments();
        //When
        disburseLoanPage = navigationHelper.navigateToLoanAccountPage("000100000000004").navigateToDisburseLoan();
        //Then
        disburseLoanPage.verifyModeOfPayments();

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void ensurePaymentModeOfPaymentTypeIsEditable() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService =
                new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010, 2, 12, 1, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_013_dbunit.xml",
                dataSource, selenium);

        DisburseLoanPage loanAccountPage = loanTestHelper.prepareToDisburseLoan("000100000000004");
        loanAccountPage.verifyPaymentModeOfPaymentIsEditable(
                "payment mode of payment must be editable when a disbursal fee exists.");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void ensurePaymentModeOfPaymentTypeIsCleared() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService =
                new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010, 2, 12, 1, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_013_dbunit.xml",
                dataSource, selenium);

        DisburseLoanPage loanAccountPage = loanTestHelper.prepareToDisburseLoan("000100000000004");
        loanAccountPage.setModesOfPaymentAndReviewTransaction();

        HomePage homePage = loanAccountPage.navigateToHomePage();
        homePage.verifyPage();
        loanAccountPage = loanTestHelper.prepareToDisburseLoanWithoutLogout(homePage, "000100000000004");
        loanAccountPage.verifyPaymentModesOfPaymentAreEmpty();
    }
}
