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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.admin.EditPenaltyPreviewPage;
import org.mifos.test.acceptance.framework.admin.NewPenaltyPreviewPage;
import org.mifos.test.acceptance.framework.admin.PenaltyFormParameters;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.AvoidFinalLocalVariable"})
public class PenaltyHelper {
    private final NavigationHelper navigationHelper;

    public PenaltyHelper(Selenium selenium) {
        this.navigationHelper = new NavigationHelper(selenium);
    }
    
    public void createAmountPenalty(final String name, final String period, final String duration,
            final String frequency, final String min, final String max, final String amount) throws Exception {
        final PenaltyFormParameters param = createPenalty(name, period, duration, frequency, min, max);
        param.setAmount(amount);
        
        navigationHelper.navigateToAdminPage().navigateToDefineNewPenaltyPage()
            .fillParameters(param).submitPageAndGotoPenaltyPreviewPage(NewPenaltyPreviewPage.class).submit();
    }
    
    public void editAmountPenalty(final String oldName, final String newName, final String period, final String duration,
            final String frequency, final String min, final String max, final String amount) throws Exception {
        final PenaltyFormParameters param = createPenalty(newName, period, duration, frequency, min, max);
        param.setAmount(amount);
        
        navigationHelper.navigateToAdminPage().navigateToViewPenaltiesPage().navigateToViewPenaltyPage(oldName)
            .navigateToEditPenaltyPage().fillParameters(param)
            .submitPageAndGotoPenaltyPreviewPage(EditPenaltyPreviewPage.class).submit();
    }
    
    public void createRatePenalty(String name, String period, String duration, String frequency, String rate,
            String formula, final String min, final String max) throws Exception {
        final PenaltyFormParameters param = createPenalty(name, period, duration, frequency, min, max);
        param.setRate(rate);
        param.setFormula(formula);
        
        navigationHelper.navigateToAdminPage().navigateToDefineNewPenaltyPage()
            .fillParameters(param).submitPageAndGotoPenaltyPreviewPage(NewPenaltyPreviewPage.class).submit();
    }
    
    public LoanAccountPage createWeeklyLoanAccountWithPenalty(final SubmitFormParameters formParameters, final String clientName, final boolean disbursal) throws Exception {
        navigationHelper.navigateToAdminPage().verifyPage().defineLoanProduct(formParameters);
        
        final CreateLoanAccountSearchParameters searchParam = new CreateLoanAccountSearchParameters();
        searchParam.setSearchString(clientName);
        searchParam.setLoanProduct(formParameters.getOfferingName());
        
        LoanAccountPage loanAccountPage = navigationHelper.navigateToClientsAndAccountsPage().navigateToCreateLoanAccountUsingLeftMenu()
            .searchAndNavigateToCreateLoanAccountPage(searchParam)
            .navigateToReviewInstallmentsPage()
            .clickPreviewAndGoToReviewLoanAccountPage()
            .submitForApprovalAndNavigateToConfirmationPage()
            .navigateToLoanAccountDetailsPage();
        
        if (disbursal) {
            loanAccountPage.changeAccountStatusToAccepted();
            
            final DisburseLoanParameters disburseParams = new DisburseLoanParameters();
            disburseParams.setAmount(formParameters.getDefaultLoanAmount());
            disburseParams.setDisbursalDateDD("15");
            disburseParams.setDisbursalDateMM("2");
            disburseParams.setDisbursalDateYYYY("2012");
            disburseParams.setPaymentType(DisburseLoanParameters.CASH);

            loanAccountPage.disburseLoan(disburseParams);
        }
        
        return loanAccountPage;
    }
    
    public void verifyCalculatePenaltyWithPayment(final String accountId, final String[] summaryPenalty,
            final String[][] schedulePenalty, final String[] accountSummary) {
        ViewRepaymentSchedulePage repaymentSchedulePage = navigationHelper.navigateToLoanAccountPage(accountId).navigateToRepaymentSchedulePage();

        for (int i = 0; i < schedulePenalty.length; ++i) {
            if (schedulePenalty[i] != null) {
                repaymentSchedulePage.verifyRepaymentScheduleTableRow(3 + i, 6, schedulePenalty[i][0]);
                repaymentSchedulePage.verifyRepaymentScheduleTableRow(3 + i, 8, schedulePenalty[i][1]);
            }
        }
        
        verifyCalculatePenaltyOnLoanAccountScreen(repaymentSchedulePage.navigateToLoanAccountPage(), summaryPenalty, accountSummary);
    }
    
    public void verifyCalculatePenaltyWithoutPayment(final String accountId, final String[] summaryPenalty,
            final String[][] schedulePenalty, final String[] accountSummary) {
        LoanAccountPage loanAccountPage = navigationHelper.navigateToLoanAccountPage(accountId);
        
        verifyCalculatePenaltyOnLoanAccountScreen(loanAccountPage, summaryPenalty, accountSummary);
        
        ViewRepaymentSchedulePage repaymentSchedulePage = loanAccountPage.navigateToRepaymentSchedulePage();
        
        for (int i = 0; i < schedulePenalty.length; ++i) {
            if (schedulePenalty[i] != null) {
                repaymentSchedulePage.verifyInstallmentAmount(4 + i, 7, schedulePenalty[i][0]);
                repaymentSchedulePage.verifyInstallmentAmount(4 + i, 9, schedulePenalty[i][1]);
            }
        }
    }
    
    private void verifyCalculatePenaltyOnLoanAccountScreen(final LoanAccountPage loanAccountPage,
            final String[] summaryPenalty, final String[] accountSummary) {
        if (accountSummary != null && accountSummary.length >= 3) {
            loanAccountPage.verifyAccountSummary(accountSummary[0], accountSummary[1], accountSummary[2]);
        }

        if (summaryPenalty != null && summaryPenalty.length >= 3) {
            loanAccountPage.verifyPenaltyOriginal(summaryPenalty[0]);
            loanAccountPage.verifyPenaltyPaid(summaryPenalty[1]);
            loanAccountPage.verifyPenaltyBalance(summaryPenalty[2]);
        }
    }
    
    private PenaltyFormParameters createPenalty(final String name, final String period, final String duration,
            final String frequency, final String min, final String max) {
        final PenaltyFormParameters param = new PenaltyFormParameters();
        param.setName(name);
        param.setApplies(PenaltyFormParameters.APPLIES_LOANS);
        param.setPeriod(period);
        param.setDuration(duration);
        param.setMin(min);
        param.setMax(max);
        param.setFrequency(frequency);
        param.setGlCode("31102 - Penalty");

        return param;
    }

}