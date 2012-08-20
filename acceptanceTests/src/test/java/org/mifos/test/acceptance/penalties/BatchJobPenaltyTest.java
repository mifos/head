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
import org.mifos.test.acceptance.framework.admin.PenaltyFormParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.RepayLoanParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.PenaltyHelper;
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
    private PenaltyHelper penaltyHelper;
    private DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService;
    
    @Override
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        
        this.navigationHelper = new NavigationHelper(selenium);
        this.penaltyHelper = new PenaltyHelper(selenium);
        this.dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_001_dbunit.xml", dataSource, selenium);
    }
    
    @AfterMethod
    public void logOut() throws Exception {
        (new MifosPage(selenium)).logout();
    }
    
    @Test(enabled = true)
    public void shouldCalculateOneTimePenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[0], PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_NONE, "0.1", "9,999,999,999");
        
        changeDateTime(04, 1);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "1", "0", "1" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "1", "451" }, { "0", "450" }, { "0", "450" }, { "0", "450" },
                                 { "0", "450" }, { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,151", "05/04/2012", "2,701" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "1", "0", "0", "0", "0", "0", "0", "0", "0" }, false);
    }

    @Test(enabled = true)
    public void shouldCalculateDailyAmountPenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[1], PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_DAILY, "0.1", "9,999,999,999");
        
        changeDateTime(04, 1);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "123", "0", "123" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "38", "488" }, { "31", "481" }, { "24", "474" }, { "17", "467" },
                                 { "10", "460" }, { "3", "453" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,273", "05/04/2012", "2,823" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "38", "31", "24", "17", "10", "3", "0", "0", "0" }, false);
    }
    
    @Test(enabled = true)
    public void shounldCalculateWeeklyAmountPenaltyOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[2], PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_WEEKLY, "0.1", "9,999,999,999");
        
        changeDateTime(04, 1);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "21", "0", "21" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "6", "456" }, { "5", "455" }, { "4", "454" }, { "3", "453" },
                                 { "2", "452" }, { "1", "451" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,171", "05/04/2012", "2,721" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "6", "5", "4", "3", "2", "1", "0", "0", "0" }, false);
        navigationHelper.navigateToLoanAccountPage(accountId).navigateToApplyAdjustment().submitAdjustment();
        
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "21", "0", "21" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "6", "456" }, { "5", "455" }, { "4", "454" }, { "3", "453" },
                                 { "2", "452" }, { "1", "451" }, { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" } },
                new String[] { "3,171", "05/04/2012", "2,721" }
        );
        
        navigationHelper.navigateToLoanAccountPage(accountId).removePenalty(1);
        verifyAfterRepayLoan(accountId, new String[] { "0", "0", "0", "0", "0", "0", "0", "0", "0", "0" }, true);
    }

    @Test(enabled = true)
    public void shouldCalculateWeeklyAmountPenaltyWithPeriodInstallmentsOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[3], PenaltyFormParameters.PERIOD_INSTALLMENTS,
                "1", PenaltyFormParameters.FREQUENCY_WEEKLY, "0.1", "9,999,999,999");
        
        changeDateTime(04, 1);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "19", "0", "19" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "4", "454" }, { "5", "455" }, { "4", "454" }, { "3", "453" },
                                 { "2", "452" }, { "1", "451" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,169", "05/04/2012", "2,719" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "4", "5", "4", "3", "2", "1", "0", "0", "0" }, false);
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithOutstandingOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[4], PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_WEEKLY, "0.5", PenaltyFormParameters.FORMULA_OUTSTANDING_LOAN, "0.1", "9,999,999,999");
        
        changeDateTime(03, 3);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "61.1", "0", "61.1" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "40.6", "490.6" }, { "20.5", "470.5" }, null /* Future Installments */,
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "1,411.1", "08/03/2012", "961.1" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "40.6", "20.5", "0", "0", "0", "0", "0", "0", "0" }, false);
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithOverdueOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[5], PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_WEEKLY, "1", PenaltyFormParameters.FORMULA_OVERDUE_AMOUNT, "0.1", "9,999,999,999");
        
        changeDateTime(03, 3);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "13.5", "0", "13.5" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "9", "459" }, { "4.5", "454.5" }, null /* Future Installments */,
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "1,363.5", "08/03/2012", "913.5" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "9", "4.5", "0", "0", "0", "0", "0", "0", "0" }, false);
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithPeriodOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[6], PenaltyFormParameters.PERIOD_DAYS, "7",
                PenaltyFormParameters.FREQUENCY_WEEKLY, "0.1", PenaltyFormParameters.FORMULA_OUTSTANDING_PRINCIPAL, "0.1", "9,999,999,999");
        
        changeDateTime(03, 22);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "27", "0", "27" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "13.5", "463.5" }, { "9", "459" }, { "4.5", "454.5" },
                { "0", "450" }, { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "2,277", "22/03/2012", "1,827" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "13.5", "9", "4.5", "0", "0", "0", "0", "0", "0" }, false);
    }
    
    @Test(enabled = true)
    public void shouldCalculateWeeklyRatePenaltyWithLimitsOnLoanAccount() throws Exception {
        String accountId = setUpPenaltyAndLoanAccount(PENALTY_NAME[7], PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_WEEKLY, "1", PenaltyFormParameters.FORMULA_OVERDUE_AMOUNT, "5", "10");
        
        changeDateTime(04, 1);
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "10", "0", "10" },
                new String[][] { { "0", "450" }, null /* Installments due */, { "9.6", "459.6" }, { "0.4", "450.4" }, { "0", "450" }, { "0", "450" },
                                 { "0", "450" }, { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "3,160", "05/04/2012", "2,710" }
        );
        
        verifyAfterRepayLoan(accountId, new String[] { "0", "9.6", "0.4", "0", "0", "0", "0", "0", "0", "0" }, false);
    }
    
    @Test(enabled = true)
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void shouldNotCalculatePenaltyAfterManuallyRunBatchJob() throws Exception {
        String accountId = setUpPenaltyAndLoanAccount("Penalty Correct", PenaltyFormParameters.PERIOD_NONE, "",
                PenaltyFormParameters.FREQUENCY_DAILY, "0.1", "9,999,999,999");
        
        changeDateTime(03, 15);
        
        for(int i = 0; i < 2; ++i) {
            penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                    new String[] { "42", "0", "42" },
                    new String[][] { { "0", "450" }, null /* Installments due */, { "21", "471" }, { "14", "464" }, { "7", "457" },
                    { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                    new String[] { "1,842", "15/03/2012", "1,392" }
            );
            
            navigationHelper.navigateToAdminPage();
            new BatchJobHelper(selenium).runSomeBatchJobs(Arrays.asList("ApplyPenaltyToLoanAccountsTaskJob"));
        }
    }
    
    private void verifyAfterRepayLoan(final String accountId, final String[] penalties, final boolean secondTime) throws Exception {
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
        
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { sumToString, sumToString, "0" },
                schedule,
                null
        );
        
        if(!secondTime) {
            changeDateTime(04, 5);
        }
        
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { sumToString, sumToString, "0" },
                schedule,
                null
        );
    }
    
    private String setUpPenaltyAndLoanAccount(final String name, final String period, final String duration,
            final String frequency, final String min, final String max) throws Exception {
        String penaltyName = name + StringUtil.getRandomString(4);
        
        changeDateTime(02, 15);
        penaltyHelper.createAmountPenalty(penaltyName, period, duration, frequency, min, max, "1");
        return createWeeklyLoanAccountWithPenalty(penaltyName);
    }
    
    private String setUpPenaltyAndLoanAccount(final String name, final String period, final String duration, final String frequency,
            final String rate, final String formula, final String min, final String max) throws Exception {
        String penaltyName = name + StringUtil.getRandomString(4);
        
        changeDateTime(02, 15);
        penaltyHelper.createRatePenalty(penaltyName, period, duration, frequency, rate, formula, min, max);
        return createWeeklyLoanAccountWithPenalty(penaltyName);
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
        
        LoanAccountPage loanAccountPage = penaltyHelper.createWeeklyLoanAccountWithPenalty(formParameters, "Client - Veronica Abisya", true);
        
        PaymentParameters formPayment = new PaymentParameters();
        formPayment.setTransactionDateDD("15");
        formPayment.setTransactionDateMM("2");
        formPayment.setTransactionDateYYYY("2012");
        formPayment.setAmount("450");
        formPayment.setPaymentType(PaymentParameters.CASH);
        
        loanAccountPage.navigateToApplyPayment()
            .submitAndNavigateToApplyPaymentConfirmationPage(formPayment)
            .submitAndNavigateToLoanAccountDetailsPage();
        
        penaltyHelper.verifyCalculatePenaltyWithPayment(loanAccountPage.getAccountId(),
                new String[] { "0", "0", "0" },
                new String[][] { { "0", "450" }, null /* Future Installments */, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" },
                                 { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" }, { "0", "450" } },
                new String[] { "0", "16/02/2012", "0" }
        );
        
        return loanAccountPage.getAccountId();
    }

    private void changeDateTime(final int month, final int day) throws Exception {
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2012, month, day, 13, 0, 0, 0));
                                
        navigationHelper.navigateToAdminPage();
        new BatchJobHelper(selenium).runSomeBatchJobs(Arrays.asList("ApplyPenaltyToLoanAccountsTaskJob"));
    }

}
