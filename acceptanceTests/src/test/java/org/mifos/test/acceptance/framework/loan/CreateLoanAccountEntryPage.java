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

public class CreateLoanAccountEntryPage extends AbstractPage {

    public void verifyPage() {
        this.verifyPage("LoanCreationDetail");
    }

    public void verifyAdditionalFeesAreEmpty() {
        Assert.assertEquals(selenium.getSelectedValue("selectedFee[0].feeId"), "");
        Assert.assertEquals(selenium.getValue("selectedFee[0].amount"), "");

        Assert.assertEquals(selenium.getSelectedValue("selectedFee[1].feeId"), "");
        Assert.assertEquals(selenium.getValue("selectedFee[1].amount"), "");

        Assert.assertEquals(selenium.getSelectedValue("selectedFee[2].feeId"), "");
        Assert.assertEquals(selenium.getValue("selectedFee[2].amount"), "");
    }

    public CreateLoanAccountEntryPage(Selenium selenium) {
        super(selenium);
    }
   
    public CreateLoanAccountConfirmationPage submitAndNavigateToLoanAccountConfirmationPage(CreateLoanAccountSubmitParameters formParameters) {
        selenium.type("loancreationdetails.input.sumLoanAmount",formParameters.getAmount());
        if (formParameters.getLsimFrequencyWeeks() != null)  
        {
            selenium.click("loancreationdetails.input.frequencyWeeks");
            selenium.type("loancreationdetails.input.weekFrequency",formParameters.getLsimWeekFrequency());
            selenium.select("weekDay", "label=Friday");
        }
        if (formParameters.getLsimMonthTypeDayOfMonth() != null)  
        {
            selenium.click("loancreationdetails.input.monthType1");
            selenium.type("loancreationdetails.input.dayOfMonth", formParameters.getLsimDayOfMonth());
        }
        if (formParameters.getLsimMonthTypeNthWeekdayOfMonth() != null)  
        {
            selenium.click("loancreationdetails.input.monthType2");
            selenium.select("monthRank", formParameters.getLsimMonthRank());
            selenium.select("monthWeek", formParameters.getLsimWeekDay());           
        }

        selenium.click("loancreationdetails.button.continue");
        waitForPageToLoad();
        selenium.click("schedulePreview.button.preview");
        waitForPageToLoad();
        selenium.isVisible("createloanpreview.button.submitForApproval");
        selenium.click("createloanpreview.button.submitForApproval");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
         
    }

    public HomePage navigateToHomePage(){
        selenium.click("id=clientsAndAccountsHeader.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public void selectAdditionalFees() {
        selenium.select("selectedFee[0].feeId", "label=One Time Upfront Fee");
        selenium.type("loancreationdetails.input.feeAmount", "6.6");

        selenium.select("selectedFee[1].feeId", "label=One Time Upfront Fee");
        selenium.type("selectedFee[1].amount", "3.3");
    }

    public void selectTwoClientsForGlim() {
        selenium.click("glimLoanForm.input.select");
        selenium.type("glimLoanForm.input.loanAmount", "1234");

        selenium.click("clients[1]");
        selenium.type("clientDetails[1].loanAmount", "4321");
    }

    public void selectPurposeForGlim() {
        selenium.select("clientDetails[0].businessActivity", "label=0003-Goat Purchase");

        selenium.select("clientDetails[1].businessActivity", "label=0010-Camel");
    }

    public void clickContinue(){
        selenium.click("loancreationdetails.button.continue");
        waitForPageToLoad();
        selenium.isVisible("schedulePreview.button.preview");
    }
}
