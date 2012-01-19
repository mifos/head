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

import org.apache.commons.lang.StringUtils;
import org.dbunit.DatabaseUnitException;
import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.AccountActivityPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanConfirmationPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanPage;
import org.mifos.test.acceptance.framework.loan.RepayLoanParameters;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.loanproduct.LoanProductTestHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"acceptance", "ui", "loan", "no_db_unit"})
public class LoanRepayTest extends UiTestCaseBase {
    private String loanId = "000100000000037";
    private NavigationHelper navigationHelper;
    private LoanProductTestHelper loanProductTestHelper;


    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")

    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        loanProductTestHelper= new LoanProductTestHelper(selenium);
        setupTime();
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    public void repay() {
        testRepayLoanPageWhenProductHasNoInterestWaiver();
        loanProductTestHelper.enableInterestWaiver("InterestWaiverLoan", true);
        testRepayLoanPageWhenProductHasInterestWaiver();
        repayLoanWithInterestWaived();
        verifyLoanStateAndAccountSummary();
        verifyRepaymentSchedule();
        verifyAccountActivity();
    }

    //http://mifosforge.jira.com/browse/MIFOSTEST-251
    private void verifyPaymentTypesForLoanRepayments() {
        String[] modesOfPayment = selenium.getSelectOptions("RepayLoan.input.modeOfRepayment");
        Assert.assertEquals(RepayLoanParameters.CASH, modesOfPayment[1]);
        Assert.assertEquals(RepayLoanParameters.CHEQUE, modesOfPayment[2]);
        Assert.assertEquals(RepayLoanParameters.VOUCHER, modesOfPayment[3]);
    }

    private void verifyAccountActivity() {
        LoanAccountPage accountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        AccountActivityPage accountActivityPage = accountPage.navigateToAccountActivityPage();
        Assert.assertEquals(accountActivityPage.getLastPrinciplePaid(2), "1,000");
        Assert.assertEquals(accountActivityPage.getLastInterestPaid(2), "4.6");
        Assert.assertEquals(accountActivityPage.getLastFeePaid(2).trim(), "10");
        Assert.assertEquals(accountActivityPage.getLastPenalty(2), "5");
        Assert.assertEquals(accountActivityPage.getLastTotalPaid(2), "1019.6");
        Assert.assertEquals(accountActivityPage.getRunningPrinciple(2), "0");
        Assert.assertEquals(accountActivityPage.getRunningInterest(2), "0");
        Assert.assertEquals(accountActivityPage.getRunningFees(2), "0");
        Assert.assertEquals(accountActivityPage.getRunningTotal(2), "0");
    }

    private void verifyRepaymentSchedule() {
        LoanAccountPage accountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        accountPage.navigateToRepaymentSchedulePage();
        Assert.assertEquals(selenium.getTable("repaymentScheduleTable.3.4").trim(), "4.6");
    }

    private void verifyLoanStateAndAccountSummary() {
        LoanAccountPage accountPage = navigationHelper.navigateToLoanAccountPage(loanId);
        Assert.assertTrue(selenium.isTextPresent("Closed- Obligation met"));
        Assert.assertEquals(accountPage.getOriginalLoanAmount(), "1,000");
        Assert.assertEquals(accountPage.getPrinciplePaid(), "1,000");
        Assert.assertEquals(accountPage.getPrincipleBalance(), "0");
        Assert.assertEquals(accountPage.getOriginalInterestAmount(), "4.6");
        Assert.assertEquals(accountPage.getInterestPaid(), "4.6");
        Assert.assertEquals(accountPage.getInterestBalance(), "0");
        Assert.assertEquals(accountPage.getOriginalFeesAmount(), "10");
        Assert.assertEquals(accountPage.getFeesPaid(), "10");
        Assert.assertEquals(accountPage.getFeesBalance(), "0");
        Assert.assertEquals(accountPage.getOriginalPenaltyAmount(), "5");
        Assert.assertEquals(accountPage.getPenaltyPaid(), "5");
        Assert.assertEquals(accountPage.getPenaltyBalance(), "0");
        Assert.assertEquals(accountPage.getOriginalTotalAmount(), "1,019.6");
        Assert.assertEquals(accountPage.getTotalPaid(), "1,019.6");
        Assert.assertEquals(accountPage.getTotalBalance(), "0");
    }

    private void testRepayLoanPageWhenProductHasInterestWaiver() {
        RepayLoanPage repayLoanPage = navigateToRepayLoanPage();
        verifySelectionOfWaiveInterest(repayLoanPage);
        verifySelectionInConfirmationPage(repayLoanPage, "Yes");
        repayLoanPage.interestWaiver(false);
        verifySelectionOfDoNotWaiveInterest(repayLoanPage, true);
        verifySelectionInConfirmationPage(repayLoanPage, "No");
    }

    private void repayLoanWithInterestWaived() {
        RepayLoanPage repayLoanPage = navigateToRepayLoanPage();
        verifyPaymentTypesForLoanRepayments();
        repayLoanPage.interestWaiver(true);
        repayLoanPage.submitAndNavigateToRepayLoanConfirmationPage(getRepayLoanParameters()).submitAndNavigateToLoanAccountDetailsPage();
    }

    private void verifySelectionOfWaiveInterest(RepayLoanPage repayLoanPage) {
        Assert.assertTrue(repayLoanPage.isWaiveInterestSelected());
        Assert.assertEquals(repayLoanPage.totalRepaymentAmount(), "1,024.2");
        Assert.assertFalse(repayLoanPage.isTotalRepaymentAmountVisible());
        Assert.assertEquals(repayLoanPage.waivedRepaymentAmount(), "1,019.6");
        Assert.assertTrue(repayLoanPage.isWaivedRepaymentAmoutVisible());
        Assert.assertTrue(repayLoanPage.isWaiverInterestWarningVisible());
        Assert.assertTrue(repayLoanPage.isWaiverInterestSelectorVisible());

    }

    private void testRepayLoanPageWhenProductHasNoInterestWaiver() {
        RepayLoanPage repayLoanPage = navigateToRepayLoanPage();
        verifySelectionOfDoNotWaiveInterest(repayLoanPage, false);
        verifySelectionInConfirmationPage(repayLoanPage, null);
    }

    private RepayLoanPage navigateToRepayLoanPage() {
        return navigationHelper.navigateToLoanAccountPage(loanId).navigateToRepayLoan();
    }

    private void verifySelectionInConfirmationPage(RepayLoanPage repayLoanPage, String waiveInterestConfirmationText) {
        RepayLoanConfirmationPage repayLoanConfirmationPage = repayLoanPage.submitAndNavigateToRepayLoanConfirmationPage(getRepayLoanParameters());
        Assert.assertEquals(repayLoanConfirmationPage.getSelectedValueForInterestWaiver(), waiveInterestConfirmationText);
        repayLoanPage = repayLoanConfirmationPage.edit();
        boolean stateToBeRetainedDuringEdit = StringUtils.equals("Yes", waiveInterestConfirmationText);
        Assert.assertEquals(repayLoanPage.isWaiveInterestSelected(), stateToBeRetainedDuringEdit);
    }

    private void verifySelectionOfDoNotWaiveInterest(RepayLoanPage repayLoanPage, boolean isWaiverInterestSelectorVisible) {
        Assert.assertFalse(repayLoanPage.isWaiveInterestSelected());
        Assert.assertEquals(repayLoanPage.totalRepaymentAmount(), "1,024.2");
        Assert.assertTrue(repayLoanPage.isTotalRepaymentAmountVisible());
        Assert.assertEquals(repayLoanPage.waivedRepaymentAmount(), "1,019.6");
        Assert.assertFalse(repayLoanPage.isWaivedRepaymentAmoutVisible());
        Assert.assertFalse(repayLoanPage.isWaiverInterestWarningVisible());
        Assert.assertEquals(repayLoanPage.isWaiverInterestSelectorVisible(), isWaiverInterestSelectorVisible);
    }

    private RepayLoanParameters getRepayLoanParameters() {
        RepayLoanParameters repayLoanParameters = new RepayLoanParameters();
        repayLoanParameters.setModeOfRepayment(RepayLoanParameters.CASH);
        return repayLoanParameters;
    }

    private void setupTime() throws DatabaseUnitException, SQLException, IOException, URISyntaxException {
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 3, 13, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

}
