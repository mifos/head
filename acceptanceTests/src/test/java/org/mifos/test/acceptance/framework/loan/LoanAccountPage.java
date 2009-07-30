/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.HomePage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.SystemPrintln")
public class LoanAccountPage extends AbstractPage {

    public LoanAccountPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("LoanAccountDetail");
    }
    
    public void verifyFeeExists(String expectedFee) {
        Assert.assertEquals(selenium.getText("LoanAccountDetail.text.loanFees"), expectedFee);
    }

    public void verifyLoanAmount(String amount) {
        Assert.assertTrue(selenium.isTextPresent(amount));        
    }
   
    public void verifyLoanIsForClient(String clientName){
        Assert.assertTrue(selenium.isTextPresent(clientName));
    }
    
    public void verifyPurpose (String purpose){
        Assert.assertTrue(selenium.isTextPresent(purpose));
    }
    
    public void verifyLoanIsPendingApproval(){
        Assert.assertTrue(selenium.isTextPresent("Application Pending Approval"));
    }
    
    public void verifyLoanIsInPartialApplication(){
        Assert.assertTrue(selenium.isTextPresent("Partial Application "));
    }
    
    /**
     * Returns the id of the account displayed on the current page, or -1 if no id is found.
     * The id is the global id (0001...000...263, not just 263).
     * @return ID of the account.
     */
    public String getAccountId() {
        String returnId = "-1";
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
        return returnId;
    }
    
    public HomePage navigateToHomePage(){
        selenium.click("id=clientsAndAccountsHeader.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
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
    
    public EditLoanAccountStatusPage navigateToEditAccountStatus() {
        selenium.click("loanaccountdetail.link.editAccountStatus");
        waitForPageToLoad();
        return new EditLoanAccountStatusPage(selenium);
    }
    
    public DisburseLoanPage navigateToDisburseLoan() {
        selenium.click("loanaccountdetail.link.disburseLoan");
        waitForPageToLoad();
        return new DisburseLoanPage(selenium);
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
        selenium.click("loanaccountdetail.link.attachSurvey"); // TODO what?
        waitForPageToLoad();
        return new AttachSurveyPage(selenium);
    }
}
