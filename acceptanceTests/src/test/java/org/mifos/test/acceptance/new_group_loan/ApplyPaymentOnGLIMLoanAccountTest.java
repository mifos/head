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
import org.mifos.test.acceptance.framework.loan.ApplyGroupPaymentConfirmationPage;
import org.mifos.test.acceptance.framework.loan.ApplyGroupPaymentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountConfirmationPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountEntryPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountPreviewPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountReviewInstallmentPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchParameters;
import org.mifos.test.acceptance.framework.loan.DisburseLoanParameters;
import org.mifos.test.acceptance.framework.loan.EditLoanAccountStatusParameters;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@SuppressWarnings("PMD")
@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
public class ApplyPaymentOnGLIMLoanAccountTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    
    private CustomPropertiesHelper customPropertiesHelper;
    
    private final static String[][] EXPECTED_PAID_INSTALLMENTS = { {"175", "0", "0", "0", "0", "175"},
                                                                   {"15", "0", "0", "0", "0", "15"} };
    private final static String[][][] INDIVIDUAL_EXPECTED_PAID_INSTALLMENTS = {
        { {  "20", "0", "0", "0", "0", "20"} }, //Client 1
        { {  "50", "0", "0", "0", "0", "50"} }, //Client 2
        { { "100", "0", "0", "0", "0", "100"}, //Client 3
          {  "20", "0", "0", "0", "0", "20"} }
    };
    
    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
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
    public void checkApplyPaymentOnGLIMLoanAccount() throws Exception {

        LoanAccountPage loanAccountPage = createNewLoan();
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
        
        ApplyGroupPaymentPage applyGroupPaymentPage = loanAccountPage.navigateToApplyGroupPayment();
        applyGroupPaymentPage.setAmount("140");
        applyGroupPaymentPage.verifyIndividualAmount(0, "20.0");
        applyGroupPaymentPage.verifyIndividualAmount(1, "40.0");
        applyGroupPaymentPage.verifyIndividualAmount(2, "80.0");
        applyGroupPaymentPage.setIndividualAmount(0, "20"); //principal 25, less
        applyGroupPaymentPage.setIndividualAmount(1, "50"); //principal 50, equal
        applyGroupPaymentPage.setIndividualAmount(2, "120"); //principal 100, more
        applyGroupPaymentPage.verifyAmount("190");
        applyGroupPaymentPage.setPaymentMethod("1");
        applyGroupPaymentPage.setDate("04", "03", "2011");
        ApplyGroupPaymentConfirmationPage applyGroupPaymentConfirmation = applyGroupPaymentPage.submit();
        loanAccountPage = applyGroupPaymentConfirmation.submitAndNavigateToLoanAccountDetailsPage();
        
        ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountPage.navigateToViewRepaymentSchedule();
        verifyRepaymentScheduleInstallmentsPaid(viewRepaymentSchedulePage, EXPECTED_PAID_INSTALLMENTS);
        loanAccountPage = viewRepaymentSchedulePage.navigateToLoanAccountPage();
        
        for (int i=0; i<=2; i+=1) {
            viewRepaymentSchedulePage = loanAccountPage.navigateToIndividualRepaymentSchedulePage(i);
            verifyRepaymentScheduleInstallmentsPaid(viewRepaymentSchedulePage, INDIVIDUAL_EXPECTED_PAID_INSTALLMENTS[i]);
            loanAccountPage = viewRepaymentSchedulePage.navigateToLoanAccountPage();
        }
    }
    
    private void verifyRepaymentScheduleInstallmentsPaid(ViewRepaymentSchedulePage viewRepaymentSchedulePage, String[][] expectedInstallments) {
    	for (int i=0; i<expectedInstallments.length; i+=1) {
    		for (int j=0; j<expectedInstallments[i].length; j+=1) {
    			viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(3+i, 3+j, expectedInstallments[i][j]);
    		}
    	}
    }
    
    private LoanAccountPage createNewLoan() {
    	 ClientsAndAccountsHomepage clientsAndAccountsHomepage = navigationHelper.navigateToClientsAndAccountsPage();
         CreateLoanAccountSearchPage createLoanAccountSearchPage = clientsAndAccountsHomepage.navigateToCreateLoanAccountUsingLeftMenu();
         CreateLoanAccountSearchParameters formParameters = new CreateLoanAccountSearchParameters();
         formParameters.setSearchString("Default Group");
         
         formParameters.setLoanProduct("GroupEmergencyLoan");

         CreateLoanAccountEntryPage createLoanAccountEntryPage = createLoanAccountSearchPage.searchAndNavigateToCreateLoanAccountPage(formParameters);
         
         createLoanAccountEntryPage.setDisbursalDate(new DateTime(2011, 3, 4, 15, 0, 0, 0));
         createLoanAccountEntryPage.selectGLIMClients(0,"Stu1233266299995 Client1233266299995 Client Id: 0002-000000012", "250", "0009-Horse");
         createLoanAccountEntryPage.selectGLIMClients(1, "Stu1233266309851 Client1233266309851 Client Id: 0002-000000013", "500", "0001-Cow Purchase");
         createLoanAccountEntryPage.selectGLIMClients(2, "Stu1233266319760 Client1233266319760 Client Id: 0002-000000014", "1000", "0003-Goat Purchase");
         createLoanAccountEntryPage.setInstallments("10");
         createLoanAccountEntryPage.setInterestRate("0");
         CreateLoanAccountReviewInstallmentPage createLoanAccountReviewInstallmentPage = createLoanAccountEntryPage.navigateToReviewInstallmentsPage();
         CreateLoanAccountPreviewPage createLoanAccountPreviewPage = createLoanAccountReviewInstallmentPage.clickPreviewAndGoToReviewLoanAccountPage();
         CreateLoanAccountConfirmationPage createLoanAccountConfirmationPage = createLoanAccountPreviewPage.submitForApprovalAndNavigateToConfirmationPage();
         return createLoanAccountConfirmationPage.navigateToLoanAccountDetailsPage();
    }
}
