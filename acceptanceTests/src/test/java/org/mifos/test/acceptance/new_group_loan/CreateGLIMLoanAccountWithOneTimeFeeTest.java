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

package org.mifos.test.acceptance.new_group_loan;

import java.sql.SQLException;

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.loan.AccountChangeStatusPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.PaymentParameters;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.LoanTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class CreateGLIMLoanAccountWithOneTimeFeeTest extends UiTestCaseBase {

    private LoanTestHelper loanTestHelper;
    private NavigationHelper navigationHelper;
    private CustomPropertiesHelper customPropertiesHelper;
    
    private final static String[] EXPECTED_PRINCIPALS = {"101", "100", "100", "100", "100", "100", "100", "100", "100", "99"};
    private final static String[] EXPECTED_INTERESTS = {"4", "4", "4", "4", "4", "4", "4", "4", "4", "6"};
    private final static String[] EXPECTED_FEES = {"1", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    private final static String[] EXPECTED_TOTALS = {"106", "104", "104", "104", "104", "104", "104", "104", "104", "105"};
    
    private final static String[] EXPECTED_PRINCIPALS_CLIENT1 = {"20", "20.2", "20.2", "20.2", "20.2", "20.2", "20.2", "20.2", "20.2", "18.4"};
    private final static String[] EXPECTED_INTERESTS_CLIENT1 = {"0.8", "0.8", "0.8", "0.8", "0.8", "0.8", "0.8", "0.8", "0.8", "1.6"};
    private final static String[] EXPECTED_FEES_CLIENT1 = {"0.2", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    private final static String[] EXPECTED_TOTALS_CLIENT1 = {"21", "21", "21", "21", "21", "21", "21", "21", "21", "20"};
    	
    private final static String[] EXPECTED_PRINCIPALS_CLIENT2 = {"30.5", "29.8", "29.8", "29.8", "29.8", "29.8", "29.8", "29.8", "29.8", "31.1"};
    private final static String[] EXPECTED_INTERESTS_CLIENT2 = {"1.2", "1.2", "1.2", "1.2", "1.2", "1.2", "1.2", "1.2", "1.2", "1.9"};
    private final static String[] EXPECTED_FEES_CLIENT2 = {"0.3", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    private final static String[] EXPECTED_TOTALS_CLIENT2 = {"32", "31", "31", "31", "31", "31", "31", "31", "31", "33"};
    
    private final static String[] EXPECTED_PRINCIPALS_CLIENT3 = {"50.5", "50", "50", "50", "50", "50", "50", "50", "50", "49.5"};
    private final static String[] EXPECTED_INTERESTS_CLIENT3 = {"2", "2", "2", "2", "2", "2", "2", "2", "2", "2.5"};
    private final static String[] EXPECTED_FEES_CLIENT3 = {"0.5", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
    private final static String[] EXPECTED_TOTALS_CLIENT3 = {"53", "52", "52", "52", "52", "52", "52", "52", "52", "52"};

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        loanTestHelper = new LoanTestHelper(selenium);
        navigationHelper = new NavigationHelper(selenium);
        customPropertiesHelper = new CustomPropertiesHelper(selenium);
        customPropertiesHelper.setNewGroupLoanWithMembers(true);
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 4, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @AfterMethod
    public void logOut() throws SQLException {
    	customPropertiesHelper.setNewGroupLoanWithMembers(false);
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void checkGLIMLoanWithThreeClientsCreeatedCorrectly() throws Exception {

        LoanAccountPage loanAccountPage = createLoan();
        loanAccountPage.verifyLoanIsPendingApproval();
        loanAccountPage.verifyNumberOfInstallments("10");
        loanAccountPage.verifyDisbursalDate("04/03/2011");
        loanAccountPage.verifyPrincipalOriginal("1,000");
        loanAccountPage.verifyLoanTotalBalance("1,043");
        loanAccountPage.verifyFeesOriginal("1");
        loanAccountPage.verifyInterestOriginal("42");
        Assert.assertTrue(selenium.isTextPresent("oneTimeFee: 1"));
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        verifyRepaymentSchedule(viewRepaymentSchedulePage, 10, EXPECTED_PRINCIPALS, EXPECTED_INTERESTS, EXPECTED_FEES, EXPECTED_TOTALS);
        loanAccountPage = viewRepaymentSchedulePage.navigateToLoanAccountPage();
        AccountChangeStatusPage accountChangeStatusPage = loanAccountPage.navigateToEditAccountStatus();
        EditLoanAccountStatusParameters params = new EditLoanAccountStatusParameters();
        params.setNote("asd");
        params.setStatus(EditLoanAccountStatusParameters.APPROVED);
        loanAccountPage = accountChangeStatusPage.submitAndNavigateToNextPage(params).submitAndNavigateToLoanAccountPage();
        DisburseLoanParameters disburseParams = new DisburseLoanParameters();
        disburseParams.setDisbursalDateDD("04");
        disburseParams.setDisbursalDateMM("03");
        disburseParams.setDisbursalDateYYYY("2011");
        disburseParams.setPaymentType(DisburseLoanParameters.CASH);
        loanAccountPage = loanAccountPage.disburseLoan(disburseParams);
        
        viewRepaymentSchedulePage = loanAccountPage.navigateToIndividualLoanAccountPage(0).navigateToRepaymentSchedulePage();
        verifyRepaymentSchedule(viewRepaymentSchedulePage, 10, EXPECTED_PRINCIPALS_CLIENT1, EXPECTED_INTERESTS_CLIENT1, EXPECTED_FEES_CLIENT1, EXPECTED_TOTALS_CLIENT1);
        loanAccountPage = navigateBackToGroupLoanPageFromIndividualRepamentSchedule();
        viewRepaymentSchedulePage = loanAccountPage.navigateToIndividualLoanAccountPage(1).navigateToRepaymentSchedulePage();
        verifyRepaymentSchedule(viewRepaymentSchedulePage, 10, EXPECTED_PRINCIPALS_CLIENT2, EXPECTED_INTERESTS_CLIENT2, EXPECTED_FEES_CLIENT2, EXPECTED_TOTALS_CLIENT2);
        loanAccountPage = navigateBackToGroupLoanPageFromIndividualRepamentSchedule();
        viewRepaymentSchedulePage = loanAccountPage.navigateToIndividualLoanAccountPage(2).navigateToRepaymentSchedulePage();
        verifyRepaymentSchedule(viewRepaymentSchedulePage, 10, EXPECTED_PRINCIPALS_CLIENT3, EXPECTED_INTERESTS_CLIENT3, EXPECTED_FEES_CLIENT3, EXPECTED_TOTALS_CLIENT3);
        loanAccountPage = navigateBackToGroupLoanPageFromIndividualRepamentSchedule();
        
        loanAccountPage = clientRepayLoan(loanAccountPage, 0); 
        loanAccountPage = clientRepayLoan(loanAccountPage, 1);
        loanAccountPage = clientRepayLoan(loanAccountPage, 2);
        loanAccountPage.verifyLoanStatus(LoanAccountPage.CLOSED);
    }
    
    private LoanAccountPage createLoan() {
    	 ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
         CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
         CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
         formParameters.setSearchString("Default Group");
         
         formParameters.setLoanProduct("WeeklyGroupFlatLoanWithOnetimeFee");

         CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);
         
         createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 3, 4, 15, 0, 0, 0));
         createLoanAccountEntryPage.selectGLIMClients(0,"Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "200", "0009-Horse");
         createLoanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "300", "0001-Cow Purchase");
         createLoanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "500", "0003-Goat Purchase");
         createLoanAccountEntryPage.setInstallments("10");
         createLoanAccountEntryPage.setInterestRate("21");
         selenium.type("defaultFeeIndividualAmounts[0][0]", "0.2");
         selenium.type("defaultFeeIndividualAmounts[0][1]", "0.3");
         selenium.type("defaultFeeIndividualAmounts[0][2]", "0.5");
         CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = createLoanAccountEntryPage.navigateToReviewInstallmentsPage();
         CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
         CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
         return createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
    }
    
    private LoanAccountPage clientRepayLoan(LoanAccountPage loanAccountPage, int clientIndex) {
    	LoanAccountPage individualLoanAccountPage = loanAccountPage.navigateToIndividualLoanAccountPage(clientIndex);
    	String loanId = individualLoanAccountPage.getAccountId();
    	String totalBalance = individualLoanAccountPage.getTotalBalance();
        PaymentParameters paymentParams = new PaymentParameters();
        paymentParams.setPaymentType(PaymentParameters.CASH);
        paymentParams.setTransactionDateDD("04");
        paymentParams.setTransactionDateMM("03");
        paymentParams.setTransactionDateYYYY("2011");
        paymentParams.setAmount(totalBalance);
        loanAccountPage = loanTestHelper.applyGroupIndividualClientPayment(loanId, paymentParams);
        loanAccountPage.verifyLoanStatus(LoanAccountPage.CLOSED);
        return loanAccountPage.navigateToGroupLoanPageFromIndividualLoanPage();
    }
    
    private LoanAccountPage navigateBackToGroupLoanPageFromIndividualRepamentSchedule() {
    	selenium.goBack();
        selenium.waitForPageToLoad("30000");
        selenium.goBack();
        selenium.waitForPageToLoad("30000");
        return new LoanAccountPage(selenium);
    }
    
    private void verifyRepaymentSchedule(ViewRepaymentSchedulePage viewRepaymentSchedulePage, int installmentCount, String[] principals, String[] interests, String[] fees, String[] totals) {
        for (int i=3; i<=installmentCount+2; i+=1) { 
            viewRepaymentSchedulePage.verifyRepaymentScheduleTablePrincipal(i, principals[i-3]);
            viewRepaymentSchedulePage.verifyRepaymentScheduleTableInterest(i, interests[i-3]);
            viewRepaymentSchedulePage.verifyRepaymentScheduleTableFees(i, fees[i-3]);
            Assert.assertEquals(selenium.getTable("installments." + i + ".8"), totals[i-3]);           
        }
    }
}
