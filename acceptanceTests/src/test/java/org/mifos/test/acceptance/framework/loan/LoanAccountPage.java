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

package org.mifos.test.acceptance.framework.loan;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.questionnaire.ViewQuestionResponseDetailPage;
import org.mifos.test.acceptance.util.StringUtil;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.SystemPrintln")
public class LoanAccountPage extends MifosPage {

    public final static String ACTIVE = "Active in Good Standing";
    public final static String ACTIVE_BAD = "Active in Bad Standing";
    public final static String CLOSED = "Closed- Obligation met";

    String loanSummaryTable = "//table[@id='loanSummaryTable']";

    public LoanAccountPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("LoanAccountDetail");
    }

    public void verifyFeeExists(String expectedFee) {
        Assert.assertEquals(selenium.getText("LoanAccountDetail.text.loanFees"), expectedFee);
    }

    public void verifyOneTimeFeeExists(String expectedFee, int feeIndex) {
        Assert.assertEquals(selenium.getText("loanAccountDetail.text.oneTimeFeeName_" + feeIndex), expectedFee);
    }

    public void verifyNoOneTimeFeesExist() {
        Assert.assertFalse(selenium.isElementPresent("id=loanAccountDetail.text.oneTimeFeeName_1"));
    }

    public void verifyNoOneTimeFeeRemovalLinkExists(int feeIndex) {
        Assert.assertFalse(selenium.isElementPresent("id=loanAccountDetail.link.removeOneTimeFee_" + feeIndex));
    }
    
    public void verifyNoOneTimePenaltyRemovalLinkExists(int penaltyIndex) {
        Assert.assertFalse(selenium.isElementPresent("id=loanAccountDetail.link.removeOneTimePenalty_" + penaltyIndex));
    }
    
    public void verifyNoPenaltyRemovalLinkExists(int penaltyIndex) {
        Assert.assertFalse(selenium.isElementPresent("id=loanAccountDetail.link.removePenalty_" + penaltyIndex));
    }

    public void verifyLoanAmount(String amount) {
        Assert.assertEquals(getOriginalLoanAmount(), StringUtil.formatNumber(amount));
    }

    public void verifyExactLoanAmount(String amount) {
        Assert.assertEquals(getOriginalLoanAmount(), amount);
    }

    public void verifyLoanIsForClient(String clientName){
        Assert.assertTrue(selenium.isTextPresent(clientName));
    }

    public void verifyPurpose (String purpose){
        Assert.assertTrue(selenium.isTextPresent(purpose));
    }

    public void verifyGLIMPurpose(String purpose, int index) {
        Assert.assertEquals(selenium.getText("xpath=//table[@id='loanAccountDetailsView'][1]/tbody[1]/tr[" + (index+1) + "]/td[5]"), purpose);
    }
    
    public void verifyGLIMIndividualScheduleLinks(int clientCount, boolean hidden) {
        Assert.assertEquals(selenium.getXpathCount("//table[@id='loanAccountDetailsView'][1]/tbody[1]/tr[1]/td").intValue() == 5, hidden);
        
        if(!hidden) {
            for(int i = 1; i <= clientCount; ++i) {
                String xpath = "//table[@id='loanAccountDetailsView'][1]/tbody[1]/tr[" + (i + 1) + "]/td[6]";
                Assert.assertEquals(selenium.getText(xpath), "show");
                
                selenium.click(xpath + "/a");
                waitForPageToLoad();
                
                Assert.assertTrue(selenium.isElementPresent("originalInstallments"));
                
                selenium.click("loanRepayment.button.return");
                waitForPageToLoad();
            }
        }
    }
    
    public void verifyLoanIsPendingApproval(){
        Assert.assertTrue(selenium.isTextPresent("Application Pending Approval"));
    }

    public void verifyLoanIsInPartialApplication(){
        Assert.assertTrue(selenium.isTextPresent("Partial Application "));
    }

    public void verifyClosedLoanPerformanceHistory() {
        Assert.assertTrue(selenium.isTextPresent("# of payments: 0"));
        Assert.assertTrue(selenium.isTextPresent("# of missed payments: 0"));
        Assert.assertTrue(selenium.isTextPresent("Days in arrears:0"));
    }

    public void verifyLoanTotalBalance(String amount) {
        Assert.assertEquals(getTotalBalance(), amount);
    }

    public void verifyTotalOriginalLoan(String amount) {
        Assert.assertEquals(getOriginalTotalAmount(), StringUtil.formatNumber(amount));
    }

    public void verifyTotalAmountPaid(String amount) {
        Assert.assertEquals(getTotalPaid(), amount);
    }

    public void verifyPrincipalOriginal(String value) {
        Assert.assertEquals(getOriginalLoanAmount(), value);
    }

    public void verifyPrincipalBalance(String value) {
        Assert.assertEquals(getPrincipleBalance(), value);
    }

    public void verifyInterestOriginal(String value) {
        Assert.assertEquals(getOriginalInterestAmount(), value);
    }

    public void verifyFeesOriginal(String value) {
        Assert.assertEquals(getOriginalFeesAmount(), value);
    }

    public void verifyPenaltyOriginal(String value) {
        Assert.assertEquals(getOriginalPenaltyAmount(), value);
    }
    
    public void verifyPenaltyBalance(String value) {
        Assert.assertEquals(getPenaltyBalance(), value);
    }
    
    public void verifyPenaltyPaid(String value) {
        Assert.assertEquals(getPenaltyPaid(), value);
    }

    public void verifyPerformanceHistory(String payments, String missedPayments) {
        Assert.assertTrue(selenium.isTextPresent("of payments: "+payments));
        Assert.assertTrue(selenium.isTextPresent("of missed payments: "+missedPayments));
    }

    public void verifyAccountSummary(String totalAmount, String date, String amountInArrears) {
        String totalAmountDue = String.format("Total amount due on %s: %s", date, totalAmount);
        String inArrears = String.format("Amount in arrears: %s", amountInArrears);
        
        Assert.assertTrue(selenium.isTextPresent(totalAmountDue), String.format("Not found text: '%s'", totalAmountDue));
        Assert.assertTrue(selenium.isTextPresent(inArrears), String.format("Not found text: '%s'", inArrears));
    }

    public void verifyStatus(String status) {
        verifyStatus(status, null);
    }

    public void verifyStatus(String status, String cancelReason) {
        if (EditLoanAccountStatusParameters.CANCEL.equals(status)) {
            Assert.assertEquals(selenium.getText("loanaccountdetail.text.status"), status + "  " + cancelReason);
        }
        else {
            Assert.assertEquals(selenium.getText("loanaccountdetail.text.status"), status);
        }
    }

    public void verifyError(String error) {
        Assert.assertTrue(selenium.isElementPresent("//span[@id='loanaccountdetail.error.message']/li[text()='"+error+"']"));
    }

    public void verifyNumberOfInstallments(String numberOfInstallments) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.text.noOfInst"), numberOfInstallments);
    }

    public void verifyInterestRate(String interestRate) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.text.interestRate"), StringUtil.formatNumber(interestRate));
    }

    public void verifyPurposeOfLoan(String purpose) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.text.purposeofloan"), purpose);
    }

    public void verifyCollateralNotes(String note) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.text.collateralnote"), note);
    }

    public void verifyCollateralType(String type) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.text.collateraltype"), type);
    }

    public void verifyExternalId(String id) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.text.externalid"), id);
    }

    public void verifyLoanDetails(CreateLoanAccountSubmitParameters submitAccountParameters, EditLoanAccountInformationParameters editLoanAccountInformationParameters) {
        if(submitAccountParameters.getAmount()!=null){
            verifyLoanAmount(submitAccountParameters.getAmount());
        }
        if(submitAccountParameters.getNumberOfInstallments()!=null){
            verifyNumberOfInstallments(submitAccountParameters.getNumberOfInstallments());
        }
        if(submitAccountParameters.getInterestRate()!=null){
            verifyInterestRate(submitAccountParameters.getInterestRate());
        }
        if(editLoanAccountInformationParameters.getCollateralNotes()!=null){
            verifyCollateralNotes(editLoanAccountInformationParameters.getCollateralNotes());
        }
        if(editLoanAccountInformationParameters.getCollateralType()!=null){
            verifyCollateralType(editLoanAccountInformationParameters.getCollateralType());
        }
        if(editLoanAccountInformationParameters.getExternalID()!=null){
            verifyExternalId(editLoanAccountInformationParameters.getExternalID());
        }
        if(editLoanAccountInformationParameters.getPurposeOfLoan()!=null){
            verifyPurposeOfLoan(editLoanAccountInformationParameters.getPurposeOfLoan());
        }
    }

    public void verifyNumberOfInstallments(String min, String max, String expected) {

        String expectedInstallmentText2 = "(Allowed Number of Installments: " + min + " - " + max; 
        String expectedInstallmentText = "(Allowed number of installments: " + min + " - " + max + ")";
        boolean result = selenium.isTextPresent(expected);
        boolean result2 = selenium.isTextPresent(expectedInstallmentText);
        boolean result3 = selenium.isTextPresent(expectedInstallmentText2);
        if (!result) {
            Assert.fail(expected + " :not found on page: " + selenium.getLocation());
        }
        if (result2 || result3) {
            Assert.assertTrue(true);
        } else {
            Assert.fail(expectedInstallmentText + " :not found on page: " + selenium.getLocation());
        }
    }

    /**
     * Returns the id of the account displayed on the current page, or -1 if no id is found.
     * The id is the global id (0001...000...263, not just 263).
     * @return ID of the account.
     */
    public String getAccountId() {
        String returnId = "-1";
        if(selenium.isElementPresent("loanaccountdetail.text.loanid")) {
            returnId = selenium.getText("loanaccountdetail.text.loanid");
        }
        else {
            String heading = selenium.getAttribute("loanaccountdetail.link.editAccountInformation@href");
            System.err.println("heADING: " + heading);
            String[] linkParts = heading.split("&");
            for (String part : linkParts) {
                String[] partOfLink = part.split("=");
                // this is an ID that identifies the account
                if ("globalAccountNum".equals(partOfLink[0])) {
                    returnId = partOfLink[1];
                }
            }
        }
        return returnId;
    }

    public HomePage navigateToHomePage(){
        selenium.click("id=header.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public ViewRepaymentSchedulePage navigateToRepaymentSchedulePage() {
        selenium.click("id=loanaccountdetail.link.viewRepaymentSchedule");
        waitForPageToLoad();
        return new ViewRepaymentSchedulePage(selenium);
    }
    
    public ViewOriginalSchedulePage navigateToIndividualSchedulePage(int row) {
        selenium.click("//table[@id='loanAccountDetailsView'][1]/tbody[1]/tr[" + row + "]/td[6]/a");
        waitForPageToLoad();
        
        return new ViewOriginalSchedulePage(selenium);
    }

    public AccountActivityPage navigateToAccountActivityPage() {
        selenium.click("link=View all account activity");
        waitForPageToLoad();
        return new AccountActivityPage(selenium);
    }

    public ViewNextInstallmentDetailsPage navigateToViewNextInstallmentDetails() {
        selenium.click("id=loanaccountdetail.link.viewInstallmentDetails");
        waitForPageToLoad();
        return new ViewNextInstallmentDetailsPage(selenium);
    }

    public ViewInstallmentDetailsPage navigateToViewInstallmentDetails() {
        selenium.click("id=loanaccountdetail.link.viewInstallmentDetails");
        waitForPageToLoad();
        return new ViewInstallmentDetailsPage(selenium);
    }

    public RepayLoanPage navigateToRepayLoan() {
        selenium.click("id=loanaccountdetail.link.repayLoan");
        waitForPageToLoad();
        return new RepayLoanPage(selenium);
    }

    public AccountChangeStatusPage navigateToEditAccountStatus() {
        selenium.click("loanaccountdetail.link.editAccountStatus");
        waitForPageToLoad();
        return new AccountChangeStatusPage(selenium);
    }

    public EditLoanAccountInformationPage navigateToEditAccountInformation() {
        selenium.click("loanaccountdetail.link.editAccountInformation");
        waitForPageToLoad();
        return new EditLoanAccountInformationPage(selenium);
    }

    public DisburseLoanPage navigateToDisburseLoan() {
        selenium.click("loanaccountdetail.link.disburseLoan");
        waitForPageToLoad();
        return new DisburseLoanPage(selenium);
    }

    public LoanAccountPage tryNavigatingToDisburseLoanWithError() {
        selenium.click("loanaccountdetail.link.disburseLoan");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public ApplyChargePage navigateToApplyCharge() {
        selenium.click("loanaccountdetail.link.applyCharges");
        waitForPageToLoad();
        return new ApplyChargePage(selenium);
    }

    public ApplyPaymentPage navigateToApplyPayment() {
        selenium.click("loanaccountdetail.link.applyPayment");
        waitForPageToLoad();
        return new ApplyPaymentPage(selenium);
    }

    public AccountAddNotesPage navigateToAddNotesPage() {
        selenium.click("loanaccountdetail.link.addNote");
        waitForPageToLoad();
        return new AccountAddNotesPage(selenium);
    }

    public AccountNotesPage navigateToAccountNotesPage() {
        selenium.click("loanaccountdetail.link.seeAllNotes");
        waitForPageToLoad();
        return new AccountNotesPage(selenium);
    }

    public AttachSurveyPage navigateToAttachSurveyPage() {
        selenium.click("loanaccountdetail.link.attachSurvey");
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }

    public TransactionHistoryPage navigateToTransactionHistory() {
        selenium.click("loanaccountdetail.link.viewTransactionHistory");
        waitForPageToLoad();
        return new TransactionHistoryPage(selenium);
    }

    public ViewRepaymentSchedulePage navigateToViewRepaymentSchedule() {
        selenium.click("id=loanaccountdetail.link.viewRepaymentSchedule");
        waitForPageToLoad();
        return new ViewRepaymentSchedulePage(selenium);
    }

    public ViewQuestionResponseDetailPage navigateToAdditionalInformationPage() {
        selenium.click("id=loanaccountdetail.link.questionGroups");
        waitForPageToLoad();
        return new ViewQuestionResponseDetailPage(selenium);
    }

    public ViewQuestionResponseDetailPage navigateToViewQuestionResponseDetailPage(String questionGroupName) {
        selenium.click("link="+questionGroupName);
        waitForPageToLoad();
        return new ViewQuestionResponseDetailPage(selenium);
    }

    public ViewLoanStatusHistoryPage navigateToViewLoanStatusHistoryPage() {
        selenium.click("id=loanaccountdetail.link.viewStatusHistory");
        waitForPageToLoad();
        return new ViewLoanStatusHistoryPage(selenium);
    }

    public AccountActivityPage navigateToViewLoanAccountActivityPage() {
        selenium.click("id=loanaccountdetail.link.viewAccountActivity");
        waitForPageToLoad();
        return new AccountActivityPage(selenium);
    }

    public ClientsAndAccountsHomepage navigateToClientsAndAccountsUsingHeaderTab() {
        selenium.click("header.link.clientsAndAccounts");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    @Override
    public AdminPage navigateToAdminPageUsingHeaderTab() {
        selenium.click("header.link.admin");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }

    public String getTotalBalance() {
        return selenium.getTable("loanSummaryTable.5.3").trim();
    }

    public String getTotalPaid() {
        return selenium.getTable("loanSummaryTable.5.2").trim();
    }

    public String getOriginalTotalAmount() {
        return selenium.getTable("loanSummaryTable.5.1").trim();
    }

    public String getPenaltyBalance() {
        return selenium.getTable("loanSummaryTable.4.3").trim();
    }

    public String getPenaltyPaid() {
        return selenium.getTable("loanSummaryTable.4.2").trim();
    }

    public String getOriginalPenaltyAmount() {
        return selenium.getTable("loanSummaryTable.4.1").trim();
    }

    public String getFeesBalance() {
        return selenium.getTable("loanSummaryTable.3.3").trim();
    }

    public String getFeesPaid() {
        return selenium.getTable("loanSummaryTable.3.2").trim();
    }

    public String getOriginalFeesAmount() {
        return selenium.getTable("loanSummaryTable.3.1").trim();
    }

    public String getInterestBalance() {
        return selenium.getTable("loanSummaryTable.2.3").trim();
    }

    public String getInterestPaid() {
        return selenium.getTable("loanSummaryTable.2.2").trim();
    }

    public String getOriginalInterestAmount() {
        return selenium.getTable("loanSummaryTable.2.1").trim();
    }

    public String getPrincipleBalance() {
        return selenium.getTable("loanSummaryTable.1.3").trim();
    }

    public String getPrinciplePaid() {
        return selenium.getTable("loanSummaryTable.1.2").trim();
    }

    public String getOriginalLoanAmount() {
        return selenium.getTable("loanSummaryTable.1.1").trim();
    }
    
    public String getDisbursalDate(){
    	return selenium.getText("loanaccountdetail.details.disbursaldate");
    }
    
    public void verifyDisbursalDate(DateTime disbursalDate){
    	DateTimeFormatter formater = DateTimeFormat.forPattern("dd/MM/yyyy");
    	Assert.assertEquals(getDisbursalDate(), formater.print(disbursalDate));
    }

    public LoanAccountPage verifyInterestTypeInLoanAccountDetails(String interestType) {
        Assert.assertTrue(selenium.isTextPresent("Interest Rate Type:  " + interestType));
        return this;

    }

    public LoanAccountPage removeOneTimeFee(int feeIndex) {
        selenium.click("loanAccountDetail.link.removeOneTimeFee_" + feeIndex);
        waitForPageToLoad();
        return this;
    }
    
    public LoanAccountPage removeOneTimePenalty(int penaltyIndex) {
        selenium.click("loanAccountDetail.link.removeOneTimePenalty_" + penaltyIndex);
        waitForPageToLoad();
        return this;
    }
    
    public LoanAccountPage removePenalty(int penaltyIndex) {
        selenium.click("loanAccountDetail.link.removePenalty_" + penaltyIndex);
        waitForPageToLoad();
        return this;
    }

    public ApplyAdjustmentPage navigateToApplyAdjustment() {
        selenium.click("loanaccountdetail.link.applyAdjustment");
        waitForPageToLoad();
        return new ApplyAdjustmentPage(selenium);
    }

    public TransactionHistoryPage navigateToTransactionHistoryPage() {
        selenium.click("loanaccountdetail.link.viewTransactionHistory");
        waitForPageToLoad();
        return new TransactionHistoryPage(selenium);
    }

    public LoanAccountPage changeAccountStatus(EditLoanAccountStatusParameters statusParams) {
        return navigateToEditAccountStatus()
                .submitAndNavigateToNextPage(statusParams)
                .submitAndNavigateToLoanAccountPage();
    }
    
    public LoanAccountPage changeAccountStatusToAccepted() {
        EditLoanAccountStatusParameters statusParams = new EditLoanAccountStatusParameters();
        statusParams.setStatus(EditLoanAccountStatusParameters.APPROVED);
        statusParams.setNote("OK");
        return navigateToEditAccountStatus()
                .submitAndNavigateToNextPage(statusParams)
                .submitAndNavigateToLoanAccountPage();
    }

    public LoanAccountPage verifyLoanStatus(String status) {
        Assert.assertTrue(selenium.isTextPresent(status));
        return this;
    }

    public LoanAccountPage verifyAccountSummary(String[][] accountSummaryTable) {
        if (accountSummaryTable != null) {
            for (int rowIndex = 0; rowIndex < accountSummaryTable.length; rowIndex++) {
                String[] rowValues = accountSummaryTable[rowIndex];
                int row = rowIndex + 1;
                for (int columnIndex = 0; columnIndex < rowValues.length; columnIndex++) {
                    String cellValue = rowValues[columnIndex];
                    int column = columnIndex + 1;
                    if (!"".equals(cellValue)) {
                        String actualCellValue = selenium.getText(loanSummaryTable + "//tr[" + row + "]/td[" + column + "]");
                        Assert.assertEquals(actualCellValue, cellValue, "In Schedule Table for row " + row + " and column " + column + " expected value is " + cellValue + " but the actual value is " + actualCellValue);
                    }
                }
            }
        }
        return this;
    }

    public LoanAccountPage disburseLoan(DisburseLoanParameters disburseParams) {
        return navigateToDisburseLoan()
        .submitAndNavigateToDisburseLoanConfirmationPage(disburseParams)
        .submitAndNavigateToLoanAccountPage();
    }
    
    public void verifyDisbursalDate(String disbursalDate) {
        Assert.assertEquals(selenium.getText("loanaccountdetail.details.disbursaldate"), disbursalDate);
    }
    

}

