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

package org.mifos.test.acceptance.penalties;

import java.util.Arrays;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.NewPenaltyPreviewPage;
import org.mifos.test.acceptance.framework.admin.PenaltyFormParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.RepayLoanParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"penalties", "acceptance", "ui"})
public class BatchJobPenaltyTest extends UiTestCaseBase {
    private static final String[] PENALTY_NAME = new String[] {
        "Penalty 5.7.1", "Penalty 5.7.2", "Penalty 5.7.3", "Penalty 5.7.4", "Penalty 5.7.5",
        "Penalty 5.7.6", "Penalty 5.7.7", "Penalty 5.7.8", "Penalty 5.7.9"
    };
    
    private NavigationHelper navigationHelper;
    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;
    
    @Override
    @BeforeMethod
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void setUp() throws Exception {
        super.setUp();
        
        this.navigationHelper = new NavigationHelper(selenium);
        this.dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
    }
    
    @AfterMethod
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void logOut() throws Exception {
        (new MifosPage(selenium)).logout();
    }
    
    @Test(enabled = true)
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
    public void shouldCalculateOneTimePenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[0], null, null);
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "1", "0", "1" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "1", "451" }, { "0", "450" } },
                new String[] { "3,151", "05/04/2012", "2,701" }
        );
        
        final RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment(RepayLoanParameters.CASH);
        navigationHelper.navigateToLoanAccountPage(accountId)
            .navigateToRepayLoan().submitAndNavigateToRepayLoanConfirmationPage(params)
            .submitAndNavigateToLoanAccountDetailsPage();
        
        verifyCalculatePenalty(accountId,
                new String[] { "1", "1", "0" },
                new String[][] { { "0", "450" }, { "1", "451" }, { "0", "450" } },
                null
        );
        
        changeDateTime(05, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "1", "1", "0" },
                new String[][] { { "0", "450" }, { "1", "451" }, { "0", "450" } },
                null
        );
    }
    
    @Test(enabled = true)
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
    public void shouldCalculateDailyPenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[1], null, PenaltyFormParameters.FREQUENCY_DAILY);
        
        changeDateTime(03, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "7", "0", "7" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "7", "457" }, { "0", "450" } },
                new String[] { "907", "01/03/2012", "457" }
        );
        
        final RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment(RepayLoanParameters.CASH);
        navigationHelper.navigateToLoanAccountPage(accountId)
            .navigateToRepayLoan().submitAndNavigateToRepayLoanConfirmationPage(params)
            .submitAndNavigateToLoanAccountDetailsPage();
        
        verifyCalculatePenalty(accountId,
                new String[] { "7", "7", "0" },
                new String[][] { { "0", "450" }, { "7", "457" }, { "0", "450" } },
                null
        );
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "7", "7", "0" },
                new String[][] { { "0", "450" }, { "7", "457" }, { "0", "450" } },
                null
        );
    }
    
    @Test(enabled = true)
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
    public void shouldCalculateWeeklyPenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[2], null, PenaltyFormParameters.FREQUENCY_WEEKLY);
        
        changeDateTime(03, 3);
        verifyCalculatePenalty(accountId,
                new String[] { "2", "0", "2" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "2", "452" }, { "0", "450" } },
                new String[] { "1,352", "08/03/2012", "902" }
        );
        
        final RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment(RepayLoanParameters.CASH);
        navigationHelper.navigateToLoanAccountPage(accountId)
            .navigateToRepayLoan().submitAndNavigateToRepayLoanConfirmationPage(params)
            .submitAndNavigateToLoanAccountDetailsPage();
        
        verifyCalculatePenalty(accountId,
                new String[] { "2", "2", "0" },
                new String[][] { { "0", "450" }, { "2", "452" }, { "0", "450" } },
                null
        );
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "2", "2", "0" },
                new String[][] { { "0", "450" }, { "2", "452" }, { "0", "450" } },
                null
        );
    }
    
    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
    private String setUpPenaltyAndLoanAccount(final String name, final String period, final String frequency) throws Exception {
        String penaltyName = name + StringUtil.getRandomString(4);
        
        changeDateTime(02, 15);
        createAmountPenalty(penaltyName, period, frequency);
        return createWeeklyLoanAccountWithPenalty(penaltyName);
    }
    
    private void verifyCalculatePenalty(final String accountId, final String[] summaryPenalty,
            final String[][] schedulePenalty, final String[] accountSummary) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(accountId);

        if (accountSummary != null && accountSummary.length >= 3) {
            loanAccountPage.verifyAccountSummary(accountSummary[0], accountSummary[1], accountSummary[2]);
        }
        
        if (summaryPenalty != null && summaryPenalty.length >= 3) {
            loanAccountPage.verifyPenaltyOriginal(summaryPenalty[0]);
            loanAccountPage.verifyPenaltyPaid(summaryPenalty[1]);
            loanAccountPage.verifyPenaltyBalance(summaryPenalty[2]);
        }
        
        ViewRepaymentSchedulePage repaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();

        for (int i = 0; i < schedulePenalty.length; ++i) {
            if (schedulePenalty[i] != null) {
                repaymentSchedulePage.verifyRepaymentScheduleTableRow(3 + i, 6, schedulePenalty[i][0]);
                repaymentSchedulePage.verifyRepaymentScheduleTableRow(3 + i, 7, schedulePenalty[i][1]);
            }
        }
        
        repaymentSchedulePage.navigateToLoanAccountPage();
    }

    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
    private String createWeeklyLoanAccountWithPenalty(final String penaltyName) throws Exception {
        final SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.addPenalty(penaltyName);
        formParameters.setDefaultLoanAmount("4500");
        formParameters.setInterestTypes(SubmitFormParameters.FLAT);
        formParameters.setMinInterestRate("0");
        formParameters.setMaxInterestRate("0");
        formParameters.setDefaultInterestRate("0");
        formParameters.setMaxInstallments("10");
        formParameters.setDefInstallments("10");
        
        navigationHelper.navigateToAdminPage().verifyPage().defineLoanProduct(formParameters);
        
        final CreateLoanAccountSearchParameters searchParam = new CreateLoanAccountSearchParameters();
        searchParam.setSearchString("Client - Veronica Abisya");
        searchParam.setLoanProduct(formParameters.getOfferingName());
        
        LoanAccountPage loanAccountPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParam)
            .navigateToReviewInstallmentsPage()
            .clickPreviewAndGoToReviewLoanAccountPage()
            .submitForApprovalAndNavigateToConfirmationPage()
            .navigateToLoanAccountDetailsPage()
            .changeAccountStatusToAccepted();
        
        final String accountId = loanAccountPage.getAccountId();
        
        final DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setAmount("4500.0");
        disburseParams.setDisbursalDateDD("15");
        disburseParams.setDisbursalDateMM("2");
        disburseParams.setDisbursalDateYYYY("2012");
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        
        loanAccountPage.disburseLoan(disburseParams);
        
        PaymentParameters formPayment = new PaymentParameters();
        formPayment.setTransactionDateDD("15");
        formPayment.setTransactionDateMM("2");
        formPayment.setTransactionDateYYYY("2012");
        formPayment.setAmount("450");
        formPayment.setPaymentType(PaymentParameters.CASH);
        
        loanAccountPage.navigateToApplyPayment()
            .submitAndNavigateToApplyPaymentConfirmationPage(formPayment)
            .submitAndNavigateToLoanAccountDetailsPage();
        
        verifyCalculatePenalty(accountId,
                new String[] { "0", "0", "0" },
                new String[][] { { "0", "450" }, null, /* Installments due */ { "0", "450" }, { "0", "450" } },
                new String[] { "0", "16/02/2012", "0" }
        );
        
        changeDateTime(02, 23);
        verifyCalculatePenalty(accountId,
                new String[] { "0", "0", "0" },
                new String[][] { { "0", "450" }, null, /* Installments due */ { "0", "450" }, null, /* Future Installments */ { "0", "450" } },
                new String[] { "450", "23/02/2012", "0" }
        );
        
        return accountId;
    }

    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
    private void createAmountPenalty(final String name, final String period, final String frequency) throws Exception {
        final PenaltyFormParameters param = new PenaltyFormParameters();
        param.setName(name);
        param.setApplies(PenaltyFormParameters.APPLIES_LOANS);
        param.setPeriod(period == null ? PenaltyFormParameters.PERIOD_NONE : period);
        param.setMin("1");
        param.setMax("2");
        param.setAmount("1");
        param.setFrequency(frequency == null ? PenaltyFormParameters.FREQUENCY_NONE : frequency);
        param.setGlCode("31102");
        
        navigationHelper.navigateToAdminPage().navigateToDefineNewPenaltyPage()
            .fillParameters(param).submitPageAndGotoPenaltyPreviewPage(NewPenaltyPreviewPage.class).submit();
    }
    
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void changeDateTime(final int month, final int day) throws Exception {
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2012, month, day, 13, 0, 0, 0));
                                
        navigationHelper.navigateToAdminPage();
        
        new BatchJobHelper(selenium).runSomeBatchJobs(Arrays.asList("ApplyPenaltyToLoanAccountsTaskJob"));
    }

}
