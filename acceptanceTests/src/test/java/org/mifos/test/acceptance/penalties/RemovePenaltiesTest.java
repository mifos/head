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
import org.mifos.test.acceptance.framework.admin.PenaltyFormParameters;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.BatchJobHelper;
import org.mifos.test.acceptance.framework.testhelpers.FormParametersHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.PenaltyHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"penalties", "acceptance", "ui"})
@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
public class RemovePenaltiesTest extends UiTestCaseBase {
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
    public void shouldWaivedPenaltyOverDueOnLoanAccount() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount();
        
        navigationHelper.navigateToLoanAccountPage(accountId).navigateToViewInstallmentDetails().waiveOverdueInstallmentPenalty();
        
        penaltyHelper.verifyCalculatePenaltyWithoutPayment(accountId,
                new String[] { "0", "0", "0" },
                new String[][] { { "0", "250" }, { "0", "250" }, { "0", "250" }, { "0", "250" }, { "0", "250" },
                                 { "0", "250" }, { "0", "250" }, null /* Future Installments */, { "0", "250" }, { "0", "250" }, { "0", "250" } },
                new String[] { "2,000", "05/04/2012", "1,750" }
        );
    }
    
    @Test(enabled = true)
    public void shouldWaivedPenaltyOverDueOnLoanAccountAfterPayment() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount();
        
        PaymentParameters param = new PaymentParameters();
        param.setAmount("285");
        param.setPaymentType(PaymentParameters.CASH);
        param.setTransactionDateDD("01");
        param.setTransactionDateMM("04");
        param.setTransactionDateYYYY("2012");
        navigationHelper.navigateToLoanAccountPage(accountId).navigateToApplyPayment()
                .submitAndNavigateToApplyPaymentConfirmationPage(param).submitAndNavigateToLoanAccountDetailsPage();
        
        navigationHelper.navigateToLoanAccountPage(accountId).navigateToViewInstallmentDetails().waiveOverdueInstallmentPenalty();
        
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "35", "35", "0" },
                new String[][] { { "35", "285" }, null /* Installments due */, { "0", "250" }, { "0", "250" }, { "0", "250" }, { "0", "250" },
                                 { "0", "250" }, { "0", "250" }, null /* Future Installments */, { "0", "250" }, { "0", "250" }, { "0", "250" } },
                new String[] { "1,750", "05/04/2012", "1,500" }
        );
    }
    
    @Test(enabled = true)
    public void shouldNotWaivedPenaltyOverDueOnLoanAccountAfterFullPayment() throws Exception {
        final String accountId = setUpPenaltyAndLoanAccount();
        
        PaymentParameters param = new PaymentParameters();
        param.setAmount("1,890");
        param.setPaymentType(PaymentParameters.CASH);
        param.setTransactionDateDD("01");
        param.setTransactionDateMM("04");
        param.setTransactionDateYYYY("2012");
        navigationHelper.navigateToLoanAccountPage(accountId).navigateToApplyPayment()
                .submitAndNavigateToApplyPaymentConfirmationPage(param).submitAndNavigateToLoanAccountDetailsPage();
        
        Assert.assertTrue(!navigationHelper.navigateToLoanAccountPage(accountId).navigateToViewInstallmentDetails().isWaiveOverdueInstallmentPenalty());
        
        penaltyHelper.verifyCalculatePenaltyWithPayment(accountId,
                new String[] { "140", "140", "0" },
                new String[][] { { "35", "285" }, { "30", "280" }, { "25", "275" }, { "20", "270" }, { "15", "265" },
                                 { "10", "260" }, { "5", "255" }, null /* Future Installments */, { "0", "250" }, { "0", "250" }, { "0", "250" } },
                new String[] { "250", "05/04/2012", "0" }
        );
    }
    
    private String setUpPenaltyAndLoanAccount() throws Exception {
        changeDateTime(02, 15);
        String penaltyName = "Penalty Waive" + StringUtil.getRandomString(4);
        
        penaltyHelper.createAmountPenalty(penaltyName, PenaltyFormParameters.PERIOD_NONE, "", PenaltyFormParameters.FREQUENCY_WEEKLY, "0.1", "9,999,999,999", "5");
        
        String accountId = createWeeklyLoanAccountWithPenalty(penaltyName);
        
        changeDateTime(04, 1);
        penaltyHelper.verifyCalculatePenaltyWithoutPayment(accountId,
                new String[] { "140", "0", "140" },
                new String[][] { { "35", "285" }, { "30", "280" }, { "25", "275" }, { "20", "270" }, { "15", "265" },
                                 { "10", "260" }, { "5", "255" }, null /* Future Installments */, { "0", "250" }, { "0", "250" }, { "0", "250" } },
                new String[] { "2,140", "05/04/2012", "1,890" }
        );
        
        return accountId;
    }
    
    private String createWeeklyLoanAccountWithPenalty(final String penaltyName) throws Exception {
        final SubmitFormParameters formParameters = FormParametersHelper.getWeeklyLoanProductParameters();
        formParameters.addPenalty(penaltyName);
        formParameters.setInterestTypes(SubmitFormParameters.FLAT);
        formParameters.setMinInterestRate("0");
        formParameters.setDefaultInterestRate("0");
        formParameters.setDefInstallments("10");
        
        return penaltyHelper.createWeeklyLoanAccountWithPenalty(formParameters, "Client - Veronica Abisya").getAccountId();
    }
    
    private void changeDateTime(final int month, final int day) throws Exception {
        dateTimeUpdaterRemoteTestingService.setDateTime(new DateTime(2012, month, day, 13, 0, 0, 0));
                                
        navigationHelper.navigateToAdminPage();
        
        new BatchJobHelper(selenium).runSomeBatchJobs(Arrays.asList("ApplyPenaltyToLoanAccountsTaskJob"));
    }
}
