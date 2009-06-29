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
}
