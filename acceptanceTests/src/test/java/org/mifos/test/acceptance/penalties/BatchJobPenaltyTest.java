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

import java.math.BigDecimal;
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
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
public class BatchJobPenaltyTest extends UiTestCaseBase {
    private static final String[] PENALTY_NAME = new String[] {
        "Penalty 5.7.1", "Penalty 5.7.2", "Penalty 5.7.3", "Penalty 5.7.4", "Penalty 5.7.5",
        "Penalty 5.7.6", "Penalty 5.7.8", "Penalty 5.7.9"
    };
    
    private NavigationHelper navigationHelper;
    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;
    
    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        
        this.navigationHelper = new NavigationHelper(selenium);
        this.dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
    }
    
    @AfterMethod
    public void logOut() throws Exception {
        (new MifosPage(selenium)).logout();
    }
    
    @Test(enabled = true)
    public void shouldCalculateOneTimePenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[0], null, null, null);
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "1", "0", "1" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "1", "451" }, { "0", "450" }, { "0", "450" },
                                 { "0", "450" }, { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,151", "05/04/2012", "2,701" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "1", "0", "0", "0", "0", "0", "0", "0" });
    }

    @Test(enabled = true)
    public void shouldCalculateDailyAmountPenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[1], null, null, PenaltyFormParameters.FREQUENCY_DAILY);
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "38", "0", "38" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "6", "456" }, { "7", "457" }, { "7", "457" },
                                 { "7", "457" }, { "7", "457" }, null /* Future Installments */, { "4", "454" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,188", "05/04/2012", "2,734" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "6", "7", "7", "7", "7", "4", "0", "0" });
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyAmountPenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[2], null, null, PenaltyFormParameters.FREQUENCY_WEEKLY);
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "6", "0", "6" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "1", "451" }, { "1", "451" }, { "1", "451" },
                                 { "1", "451" }, { "1", "451" }, null /* Future Installments */, { "1", "451" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,156", "05/04/2012", "2,705" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "1", "1", "1", "1", "1", "1", "0", "0" });
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyAmountPenaltyWithPeriodInstallmentsOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[3], PenaltyFormParameters.PERIOD_INSTALLMENTS, "1", PenaltyFormParameters.FREQUENCY_WEEKLY);
        
        changeDateTime(04, 1);
        verifyCalculatePenalty(accountId,
                new String[] { "5", "0", "5" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "0", "450" }, { "1", "451" }, { "1", "451" },
                                 { "1", "451" }, { "1", "451" }, null /* Future Installments */, { "1", "451" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,155", "05/04/2012", "2,704" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "0", "1", "1", "1", "1", "1", "0", "0" });
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithOutstandingOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[4], null, null,
                PenaltyFormParameters.FREQUENCY_WEEKLY, "0.5", PenaltyFormParameters.FORMULA_OUTSTANDING_LOAN);
        
        changeDateTime(02, 24);
        verifyCalculatePenalty(accountId,
                new String[] { "20.2", "0", "20.2" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, null /* Future Installments */, { "20.2", "470.2" },
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "920.2", "01/03/2012", "450" }
        );
        
        changeDateTime(03, 3);
        verifyCalculatePenalty(accountId,
                new String[] { "40.6", "0", "40.6" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "20.2", "470.2" }, null /* Future Installments */,
                                 { "20.4", "470.4" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "1,390.6", "08/03/2012", "920.2" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "20.2", "20.4", "0", "0", "0", "0", "0", "0" });
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithOverdueOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[5], null, null,
                PenaltyFormParameters.FREQUENCY_WEEKLY, "1", PenaltyFormParameters.FORMULA_OVERDUE_AMOUNT);
        
        changeDateTime(02, 24);
        verifyCalculatePenalty(accountId,
                new String[] { "4.5", "0", "4.5" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, null /* Future Installments */, { "4.5", "454.5" },
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "904.5", "01/03/2012", "450" }
        );
        
        changeDateTime(03, 3);
        verifyCalculatePenalty(accountId,
                new String[] { "9", "0", "9" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "4.5", "454.5" }, null /* Future Installments */,
                                 { "4.5", "454.5" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "1,359", "08/03/2012", "904.5" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "4.5", "4.5", "0", "0", "0", "0", "0", "0" });
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithPeriodOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[6], PenaltyFormParameters.PERIOD_DAYS, "7",
                PenaltyFormParameters.FREQUENCY_WEEKLY, "0.1", PenaltyFormParameters.FORMULA_OUTSTANDING_PRINCIPAL);
        
        changeDateTime(03, 01);
        verifyCalculatePenalty(accountId,
                new String[] { "0", "0", "0" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "0", "450" }, null /* Future Installments */, 
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "900", "01/03/2012", "450" }
        );
        
        changeDateTime(03, 15);
        verifyCalculatePenalty(accountId,
                new String[] { "9", "0", "9" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, { "0", "450" }, { "4.5", "454.5" },
                { "4.5", "454.5" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "1,809", "15/03/2012", "1,354.5" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "0", "4.5", "4.5", "0", "0", "0", "0", "0" });
    }
    
    private void verifyAfterRepayLoan(final String accountId, final String[] penalties) throws Exception {
        RepayLoanParameters params = new RepayLoanParameters();
        params.setModeOfRepayment(RepayLoanParameters.CASH);
        
        navigationHelper.navigateToLoanAccountPage(accountId)
            .navigateToRepayLoan().submitAndNavigateToRepayLoanConfirmationPage(params)
            .submitAndNavigateToLoanAccountDetailsPage();
        
        BigDecimal sum = BigDecimal.ZERO;
        BigDecimal principal = new BigDecimal(450);
        String[][] schedule = new String[penalties.length][2];
        
        for(int i = 0; i < penalties.length; ++i) {
            BigDecimal penalty = BigDecimal.valueOf(Double.valueOf(penalties[i]));
            
            sum = sum.add(penalty.setScale(1, BigDecimal.ROUND_HALF_UP));
            
            schedule[i][0] = penalties[i];
            schedule[i][1] = StringUtil.formatNumber(penalty.add(principal).setScale(1, BigDecimal.ROUND_HALF_UP).toString());
        }
        
        String sumToString = StringUtil.formatNumber(sum.toString());
        
        verifyCalculatePenalty(accountId,
                new String[] { sumToString, sumToString, "0" },
                schedule,
                null
        );
        
        changeDateTime(05, 1);
        verifyCalculatePenalty(accountId,
                new String[] { sumToString, sumToString, "0" },
                schedule,
                null
        );
    }
    
    private String setUpPenaltyAndLoanAccount(final String name, final String period, final String duration, final String frequency) throws Exception {
        String penaltyName = name + StringUtil.getRandomString(4);
        
        changeDateTime(02, 15);
        createAmountPenalty(penaltyName, period, duration, frequency);
        return createWeeklyLoanAccountWithPenalty(penaltyName);
    }
    
    private String setUpPenaltyAndLoanAccount(final String name, final String period, final String duration, final String frequency,
            final String rate, final String formula) throws Exception {
        String penaltyName = name + StringUtil.getRandomString(4);
        
        changeDateTime(02, 15);
        createRatePenalty(penaltyName, period, duration, frequency, rate, formula);
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
                new String[][] { { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" },
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "0", "16/02/2012", "0" }
        );
        
        changeDateTime(02, 23);
        verifyCalculatePenalty(accountId,
                new String[] { "0", "0", "0" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "0", "450" }, null /* Future Installments */, { "0", "450" },
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "450", "23/02/2012", "0" }
        );
        
        return accountId;
    }

    private void createAmountPenalty(final String name, final String period, final String duration, final String frequency) throws Exception {
        final PenaltyFormParameters param = new PenaltyFormParameters();
        param.setName(name);
        param.setApplies(PenaltyFormParameters.APPLIES_LOANS);
        param.setPeriod(period == null ? PenaltyFormParameters.PERIOD_NONE : period);
        param.setDuration(duration == null ? "" : duration);
        param.setMin("1");
        param.setMax("2");
        param.setAmount("1");
        param.setFrequency(frequency == null ? PenaltyFormParameters.FREQUENCY_NONE : frequency);
        param.setGlCode("31102");
        
        navigationHelper.navigateToAdminPage().navigateToDefineNewPenaltyPage()
            .fillParameters(param).submitPageAndGotoPenaltyPreviewPage(NewPenaltyPreviewPage.class).submit();
    }
    
    private void createRatePenalty(String name, String period, String duration, String frequency, String rate,
            String formula) throws Exception {
        final PenaltyFormParameters param = new PenaltyFormParameters();
        param.setName(name);
        param.setApplies(PenaltyFormParameters.APPLIES_LOANS);
        param.setPeriod(period == null ? PenaltyFormParameters.PERIOD_NONE : period);
        param.setDuration(duration == null ? "" : duration);
        param.setMin("1");
        param.setMax("2");
        param.setRate(rate);
        param.setFormula(formula);
        param.setFrequency(frequency == null ? PenaltyFormParameters.FREQUENCY_NONE : frequency);
        param.setGlCode("31102");
        
        navigationHelper.navigateToAdminPage().navigateToDefineNewPenaltyPage()
            .fillParameters(param).submitPageAndGotoPenaltyPreviewPage(NewPenaltyPreviewPage.class).submit();
    }
    
    private void changeDateTime(final int month, final int day) throws Exception {
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2012, month, day, 13, 0, 0, 0));
                                
        navigationHelper.navigateToAdminPage();
        
        new BatchJobHelper(selenium).runSomeBatchJobs(Arrays.asList("ApplyPenaltyToLoanAccountsTaskJob"));
    }

}
