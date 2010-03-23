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

import org.dbunit.dataset.IDataSet;
import org.joda.time.DateTime;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanPage;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
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

    @Test( groups={"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void disburseLoan() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,13,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml.zip", dataSource, selenium);

        // account w/ id 000100000000005 has an approved but not disbursed loan.

        DisburseLoanParameters params = new DisburseLoanParameters();

        params.setDisbursalDateDD("08");
        params.setDisbursalDateMM("07");
        params.setDisbursalDateYYYY("2009");
        params.setPaymentType(DisburseLoanParameters.CASH);

        loanTestHelper.disburseLoan("000100000000005", params);

        String[] tablesToValidate = { "ACCOUNT_PAYMENT",  "ACCOUNT_TRXN", "ACCOUNT_STATUS_CHANGE_HISTORY" };

        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile("ClientLoanDisbursalTest_001_result_dbunit.xml.zip");
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);

    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void ensurePaymentModeOfPaymentTypeIsEditable() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService =
            new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010,2,12,1,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_013_dbunit.xml.zip",
                dataSource, selenium);

        DisburseLoanPage loanAccountPage = loanTestHelper.prepareToDisburseLoan("000100000000004");
        loanAccountPage.verifyPaymentModeOfPaymentIsEditable(
                "payment mode of payment must be editable when a disbursal fee exists.");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void ensurePaymentModeOfPaymentTypeIsCleared() throws Exception {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService =
            new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2010,2,12,1,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_013_dbunit.xml.zip",
                dataSource, selenium);

        DisburseLoanPage loanAccountPage = loanTestHelper.prepareToDisburseLoan("000100000000004");
        loanAccountPage.setModesOfPaymentAndReviewTransaction();

        HomePage homePage = loanAccountPage.navigateToHomePage();
        homePage.verifyPage();
        loanAccountPage = loanTestHelper.prepareToDisburseLoanWithoutLogout(homePage, "000100000000004");
        loanAccountPage.verifyPaymentModesOfPaymentAreEmpty();
    }
}
