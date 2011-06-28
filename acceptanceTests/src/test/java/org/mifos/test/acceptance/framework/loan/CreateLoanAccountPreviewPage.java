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

import java.util.List;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CreateLoanAccountPreviewPage extends AbstractPage {
    String editScheduleButton = "//input[@id='redoloanpreview.button.editloanschedule']";

    public CreateLoanAccountPreviewPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("CreateLoanPreview");
    }

    public CreateLoanAccountPreviewPage() {
        super();
    }

    public CreateLoanAccountPreviewPage verifyInterestTypeInLoanPreview(String interestType) {
        Assert.assertTrue(selenium.isTextPresent("Interest rate type:"));
        Assert.assertTrue(selenium.isTextPresent(interestType));
        return this;
    }

    public CreateLoanAccountReviewInstallmentPage verifyEditSchedule() {
        Assert.assertTrue(selenium.isElementPresent(editScheduleButton));
        selenium.click(editScheduleButton);
        waitForPageToLoad();
        verifyPage("SchedulePreview");
        return new CreateLoanAccountReviewInstallmentPage(selenium);
    }

    public void verifyEditScheduleDisabled() {
        Assert.assertFalse(selenium.isElementPresent(editScheduleButton));
    }

    public CreateLoanAccountConfirmationPage submit() {
        selenium.click("_eventId_submit");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
    }

    public void verifyWarningForThreshold(String warningThreshold) {
        isTextPresentInPage("Installment amount for September 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
        //isTextPresentInPage("Installment amount for November 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
    }

    private void isTextPresentInPage(String validationMessage) {
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li[text()=' ']"),"Blank Error message is thrown");
        Assert.assertTrue(selenium.isTextPresent(validationMessage),validationMessage);
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li[text()='']"),"Blank Error message is thrown");
    }

    public CreateLoanAccountPreviewPage verifyZeroCashFlowWarning(String warningThreshold) {
        isTextPresentInPage("Cash flow for September 2010 is zero");
        isTextPresentInPage("Installment amount for September 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
        return this;
    }

    public void verifyLoanAmount(String amount) {
        try {
            Assert.assertEquals(getLoanAmount()+ ".0", amount);
        } catch (AssertionError assertionError) {
            Assert.assertEquals(getLoanAmount(), amount);
        }
    }

    public String getLoanAmount() {
        return selenium.getText("createloanpreview.text.loanamount");
    }

    public QuestionResponsePage navigateToQuestionResponsePage() {
        selenium.click("_eventId_editQuestionGroups");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }

    public CreateLoanAccountConfirmationPage submitForApprovalAndNavigateToConfirmationPage() {
        selenium.isVisible("createloanpreview.button.submitForApproval");
        selenium.click("createloanpreview.button.submitForApproval");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
    }

    public CreateLoanAccountConfirmationPage submitForLaterAndNavigateToConfirmationPage() {
        selenium.isVisible("createloanpreview.button.saveForLater");
        selenium.click("createloanpreview.button.saveForLater");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
    }
    
    public ClientsAndAccountsHomepage cancel(){
        selenium.click("_eventId_cancel");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }

    public void verifyDueDate(int installement, String dueDate) {
        Assert.assertEquals(selenium.getText("//table[@id='installments']//tbody//tr[" + (installement ) + "]/td[2]"), dueDate);
    }
    
    public CreateLoanAccountEntryPage editAccountInformation() {
        selenium.click("createloanpreview.button.edit");
        waitForPageToLoad();
        return new CreateLoanAccountEntryPage(selenium);
    }
    
    private String getDueDateForInstallment(Integer installment){
    	return selenium.getText("//div[2]/table/tbody/tr["  + installment + "]/td[2]");
    }
    
    private String getTotalForInstallment(Integer installment){
    	return selenium.getText("//div[2]/table/tbody/tr["  + installment + "]/td[6]");
    }
    
    public void verifyInstallmentsSchedule(List<String> totals, List<String> dueDates, Integer noOfIntallments){
    	for(int i=0; i<noOfIntallments; i++){
    		Assert.assertEquals(getDueDateForInstallment((i+1)), dueDates.get(i));
    		Assert.assertEquals(getTotalForInstallment((i+1)), totals.get(i));
    	}
    }
}