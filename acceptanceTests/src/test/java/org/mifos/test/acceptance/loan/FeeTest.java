/*
 * Copyright Grameen Foundation USA
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
import org.mifos.test.acceptance.admin.FeeTestHelper;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.mifos.test.acceptance.util.ApplicationDatabaseOperation;
import org.mifos.test.acceptance.util.TestDataSetup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"loan","acceptance","ui"})
public class FeeTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private FeeTestHelper feeTestHelper;
    private TestDataSetup dataSetup;

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;
    @Autowired
    private ApplicationDatabaseOperation applicationDatabaseOperation;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();

        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2009,7,11,12,0,0,0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
        dataSetup = new TestDataSetup(selenium, applicationDatabaseOperation);

        loanTestHelper = new LoanTestHelper(selenium);
        feeTestHelper = new FeeTestHelper(dataSetup);
    }

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @Test(groups = {"smoke"})
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyFee() throws Exception {

        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml", dataSource, selenium);

        // the data set contains an approved loan w/ id 000100000000005
        // we use this loan for our test
        ChargeParameters params = new ChargeParameters();

        params.setType(ChargeParameters.MISC_FEES);
        params.setAmount("10");

        loanTestHelper.applyCharge("000100000000005", params);

        verifyFees("FeeTest_001_result_dbunit.xml");
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyAndWaiveFee() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml", dataSource, selenium);

        // this account has an approved but not disbursed loan.
        String accountId = "000100000000005";


        DisburseLoanParameters disbursalParameters = new DisburseLoanParameters();

        disbursalParameters.setDisbursalDateDD("08");
        disbursalParameters.setDisbursalDateMM("07");
        disbursalParameters.setDisbursalDateYYYY("2009");
        disbursalParameters.setPaymentType(DisburseLoanParameters.CASH);

        // we disburse the loan so that we can waive the fee.
        loanTestHelper.disburseLoan(accountId, disbursalParameters);

        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("15");
        feeParameters.setType(ChargeParameters.MISC_FEES);

        loanTestHelper.applyCharge(accountId, feeParameters);

        loanTestHelper.waiveFee(accountId);

        verifyFees("FeeTest_002_result_dbunit.xml");
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void applyAndRemoveOneTimeFee() throws Exception {
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_007_dbunit.xml", dataSource, selenium);

        // this account has an approved but not disbursed loan.
        String pendingApprovalAccountId = "000100000000003";
        String partialApplicationAccountId = "000100000000004";
        String applicationApprovedAccountId = "000100000000005";

        String oneTimeFee = feeTestHelper.createNoRateFee("oneTimeFee", FeesCreatePage.SubmitFormParameters.LOAN, "Upfront", 10);

        ChargeParameters feeParameters = new ChargeParameters();
        feeParameters.setAmount("15");
        feeParameters.setType(oneTimeFee);

        // add and remove the fee from a pending approval account
        loanTestHelper.applyChargeUsingFeeLabel(pendingApprovalAccountId, feeParameters);
        loanTestHelper.verifyOneTimeFee(oneTimeFee, 1);
        loanTestHelper.removeOneTimeFee(1);
        loanTestHelper.verifyNoOneTimeFeesExist();

        // add and remove the fee from a partial application account
        loanTestHelper.applyChargeUsingFeeLabel(partialApplicationAccountId, feeParameters);
        loanTestHelper.verifyOneTimeFee(oneTimeFee, 1);
        loanTestHelper.removeOneTimeFee(1);
        loanTestHelper.verifyNoOneTimeFeesExist();

        // add and verify that the fee cannot be removed from an application approved account
        loanTestHelper.applyChargeUsingFeeLabel(applicationApprovedAccountId, feeParameters);
        loanTestHelper.verifyOneTimeFee(oneTimeFee, 1);
        loanTestHelper.verifyNoOneTimeFeeRemovalLinkExists(1);
    }


    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyFees(String resultDataSetFile) throws Exception {
        String[] tablesToValidate = { "LOAN_ACTIVITY_DETAILS",  "LOAN_SUMMARY" };

        IDataSet expectedDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(resultDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

        dbUnitUtilities.verifyTables(tablesToValidate, databaseDataSet, expectedDataSet);

    }
}