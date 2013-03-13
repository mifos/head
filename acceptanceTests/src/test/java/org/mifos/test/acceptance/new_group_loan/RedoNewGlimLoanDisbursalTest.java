/*
* Copyright (c) 2005-2011 Grameen Foundation USA
* All rights reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
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

import org.joda.time.DateTime;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.loan.LoanAccountPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalChooseLoanInstancePage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalEntryPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalParameters;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSchedulePreviewPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchPage;
import org.mifos.test.acceptance.framework.loan.RedoLoanDisbursalSearchResultsPage;
import org.mifos.test.acceptance.framework.loan.ViewRepaymentSchedulePage;
import org.mifos.test.acceptance.framework.testhelpers.CustomPropertiesHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.DateTimeUpdaterRemoteTestingService;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"loan", "acceptance", "ui", "no_db_unit"})
@SuppressWarnings("PMD")
public class RedoNewGlimLoanDisbursalTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private CustomPropertiesHelper customPropertiesHelper;
    
    @Override
    @BeforeMethod(alwaysRun=true)
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
        customPropertiesHelper = new CustomPropertiesHelper(selenium);
        customPropertiesHelper.setNewGroupLoanWithMembers(true);
        
        DateTimeUpdaterRemoteTestingService dateTimeUpdaterRemoteTestingService = new DateTimeUpdaterRemoteTestingService(selenium);
        DateTime targetTime = new DateTime(2011, 03, 15, 13, 0, 0, 0);
        dateTimeUpdaterRemoteTestingService.setDateTime(targetTime);
    }

    @Test(enabled=true)
    public void checkRedoLoanDisbursal() throws Exception {
    	AdminPage adminPage = navigationHelper.navigateToAdminPage();
    	RedoLoanDisbursalSearchPage redoLoanDisbursalSearchPage = adminPage.navigateToRedoLoanDisbursal();
    	RedoLoanDisbursalSearchResultsPage redoLoanDisbursalSearchResultsPage = 
    			redoLoanDisbursalSearchPage.searchAndNavigateToRedoLoanDisbursalPage("Default Group");
    	RedoLoanDisbursalChooseLoanInstancePage redoLoanDisbursalChooseLoanInstancePage  = 
    			redoLoanDisbursalSearchResultsPage.navigateToRedoLoanDisbursalChooseLoanProductPage("Default Group:ID0002-000000011");
    	RedoLoanDisbursalEntryPage redoLoanDisbursalEntryPage = 
    			redoLoanDisbursalChooseLoanInstancePage.submitAndNavigateToRedoLoanDisbursalEntryPage("WeeklyGroupDeclineLoanWithPeriodicFee");
    	RedoLoanDisbursalParameters redoLoanDisbursalParameters = new RedoLoanDisbursalParameters();
    	redoLoanDisbursalParameters.addClient(0, "1000", "0001-Cow Purchase");
    	redoLoanDisbursalParameters.addClient(1, "2000", "0001-Cow Purchase");
    	redoLoanDisbursalParameters.setInterestRate("10.0");
    	redoLoanDisbursalParameters.setDisbursalDateDD("23"); 
    	redoLoanDisbursalParameters.setDisbursalDateMM("02"); //one month earlier
    	redoLoanDisbursalParameters.setDisbursalDateYYYY("2011");
    	RedoLoanDisbursalSchedulePreviewPage redoLoanDisbursalPreviewSchedulePage = 
    			redoLoanDisbursalEntryPage.submitWithGLIMandLSIPAndNavigateToPreviewPage(redoLoanDisbursalParameters, true);
    	redoLoanDisbursalPreviewSchedulePage.typeIndividualPaidInstallment(12, 0, "0", "10/03/2011");
    	redoLoanDisbursalPreviewSchedulePage.typeIndividualPaidInstallment(13, 0, "0", "10/03/2011");
    	redoLoanDisbursalPreviewSchedulePage.typeIndividualPaidInstallment(12, 1, "200", "11/03/2011");
    	redoLoanDisbursalPreviewSchedulePage.typeIndividualPaidInstallment(13, 1, "300", "11/03/2011");
    	redoLoanDisbursalPreviewSchedulePage.typeIndividualPaidInstallment(12, 2, "100", "12/03/2011");
    	redoLoanDisbursalPreviewSchedulePage.typeIndividualPaidInstallment(13, 2, "100", "12/03/2011");
    	LoanAccountPage loanAccountDetailsPage = 
    			redoLoanDisbursalPreviewSchedulePage.submitAndNavigateToRedoLoanDisbursalPreviewPage(true)
    			.submitAndNavigateToLoanAccountConfirmationPage().navigateToLoanAccountDetailsPage();
    	loanAccountDetailsPage.verifyStatus(LoanAccountPage.ACTIVE);
    	
    	loanAccountDetailsPage.verifyAccountSummary("512", "18/03/2011", "209");
    	loanAccountDetailsPage.verifyNumberOfInstallments("10");
    	loanAccountDetailsPage.verifyTotalAmountPaid("700");
    	loanAccountDetailsPage.verifyTotalOriginalLoan("3,033");
    	loanAccountDetailsPage.verifyPrincipalBalance("2,315.5");
    	loanAccountDetailsPage.verifyInterestOriginal("33");
    	
    	ViewRepaymentSchedulePage viewRepaymentSchedulePage = loanAccountDetailsPage.navigateToRepaymentSchedulePage();
    
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(3, "1", "25-Feb-2011", "11-Mar-2011", "297.3", "5.7", "0", "0", "14", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(4, "2", "04-Mar-2011", "12-Mar-2011", "297.8", "5.2", "0", "0", "8", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(5, "3", "11-Mar-2011", "12-Mar-2011", "89.4", "4.6", "0", "0", "1", "94");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(7, "3", "11-Mar-2011", "-", "209", "0", "0", "0", "1", "209");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(9, "4", "18-Mar-2011", "-", "299", "4", "0", "0", "0", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(10, "5", "25-Mar-2011", "-", "299.5", "3.5", "0", "0", "0", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(11, "6", "01-Apr-2011", "-", "300.1", "2.9", "0", "0", "0", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(12, "7", "08-Apr-2011", "-", "300.7", "2.3", "0", "0", "0", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(13, "8", "15-Apr-2011", "-", "301.2", "1.8", "0", "0", "0", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(14, "9", "22-Apr-2011", "-", "301.8", "1.2", "0", "0", "0", "303");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(15, "10", "29-Apr-2011", "-", "304.2", "1.8", "0", "0", "0", "306");
    	
    	loanAccountDetailsPage = viewRepaymentSchedulePage.navigateToLoanAccountPage().navigateToIndividualLoanAccountPage(0);
    	
    	loanAccountDetailsPage.verifyAccountSummary("104", "18/03/2011", "3");
    	loanAccountDetailsPage.verifyNumberOfInstallments("10");
    	loanAccountDetailsPage.verifyTotalAmountPaid("300");
    	loanAccountDetailsPage.verifyTotalOriginalLoan("1,011");
    	loanAccountDetailsPage.verifyPrincipalBalance("705.1");
    	loanAccountDetailsPage.verifyInterestOriginal("11");
    	
    	viewRepaymentSchedulePage = loanAccountDetailsPage.navigateToRepaymentSchedulePage();
    	
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(3, "1", "25-Feb-2011", "11-Mar-2011", "99.1", "1.9", "0", "0", "14", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(4, "2", "04-Mar-2011", "12-Mar-2011", "99.3", "1.7", "0", "0", "8", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(5, "3", "11-Mar-2011", "12-Mar-2011", "96.5", "1.5", "0", "0", "1", "98");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(7, "3", "11-Mar-2011", "-", "3", "0", "0", "0", "1", "3");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(9, "4", "18-Mar-2011", "-", "99.7", "1.3", "0", "0", "0", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(10, "5", "25-Mar-2011", "-", "99.8", "1.2", "0", "0", "0", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(11, "6", "01-Apr-2011", "-", "100", "1", "0", "0", "0", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(12, "7", "08-Apr-2011", "-", "100.2", "0.8", "0", "0", "0", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(13, "8", "15-Apr-2011", "-", "100.4", "0.6", "0", "0", "0", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(14, "9", "22-Apr-2011", "-", "100.6", "0.4", "0", "0", "0", "101");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(15, "10", "29-Apr-2011", "-", "101.4", "0.6", "0", "0", "0", "102");
    	
    	loanAccountDetailsPage = viewRepaymentSchedulePage.navigateToLoanAccountPage();
    	loanAccountDetailsPage = loanAccountDetailsPage.navigateToGroupLoanPageFromIndividualLoanPage();
    	
    	loanAccountDetailsPage = loanAccountDetailsPage.navigateToIndividualLoanAccountPage(1);
    	loanAccountDetailsPage.verifyAccountSummary("408", "18/03/2011", "206");
    	loanAccountDetailsPage.verifyNumberOfInstallments("10");
    	loanAccountDetailsPage.verifyTotalAmountPaid("400");
    	loanAccountDetailsPage.verifyTotalOriginalLoan("2,022");
    	loanAccountDetailsPage.verifyPrincipalBalance("1,607.3");
    	loanAccountDetailsPage.verifyInterestOriginal("22");
    	viewRepaymentSchedulePage = loanAccountDetailsPage.navigateToViewRepaymentSchedule();
    	
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(3, "1", "25-Feb-2011", "11-Mar-2011", "198.2", "3.8", "0", "0", "14", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(4, "2", "04-Mar-2011", "12-Mar-2011", "194.5", "3.5", "0", "0", "8", "198");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(6, "2", "04-Mar-2011", "-", "4", "0", "0", "0", "8", "4");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(7, "3", "11-Mar-2011", "-", "198.9", "3.1", "0", "0", "4", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(9, "4", "18-Mar-2011", "-", "199.3", "2.7", "0", "0", "0", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(10, "5", "25-Mar-2011", "-", "199.7", "2.3", "0", "0", "0", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(11, "6", "01-Apr-2011", "-", "200.1", "1.9", "0", "0", "0", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(12, "7", "08-Apr-2011", "-", "200.5", "1.5", "0", "0", "0", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(13, "8", "15-Apr-2011", "-", "200.8", "1.2", "0", "0", "0", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(14, "9", "22-Apr-2011", "-", "201.2", "0.8", "0", "0", "0", "202");
    	viewRepaymentSchedulePage.verifyRepaymentScheduleTableRow(15, "10", "29-Apr-2011", "-", "202.8", "1.2", "0", "0", "0", "204");
    }	
    
    @AfterMethod
    public void logOut() {
    	customPropertiesHelper.setNewGroupLoanWithMembers(false);
    	new MifosPage(selenium).logout();
    }
}
