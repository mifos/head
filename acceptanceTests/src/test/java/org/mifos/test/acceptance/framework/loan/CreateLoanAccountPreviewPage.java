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

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;
import org.testng.Assert;

public class CreateLoanAccountPreviewPage extends AbstractPage {
    String editScheduleButton = "//input[@id='createloanpreview.button.edit' and @name='editButton' and @value='Edit Loan Schedule Information']";

    public CreateLoanAccountPreviewPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("CreateLoanPreview");
    }

    public CreateLoanAccountPreviewPage() {
        super();
    }

    public CreateLoanAccountPreviewPage verifyInterestTypeInLoanPreview(String interestType) {
        Assert.assertTrue(selenium.isTextPresent("Interest Rate Type :  " + interestType));
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
        Assert.assertTrue(selenium.isElementPresent(editScheduleButton));
    }

    public CreateLoanAccountConfirmationPage submit() {
        selenium.click("submitForApprovalButton");
        waitForPageToLoad();
        return new CreateLoanAccountConfirmationPage(selenium);
    }

    public void verifyWarningForThreshold(String warningThreshold) {
        verifyIsTextPresentInPage("Installment amount for September 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
        //verifyIsTextPresentInPage("Installment amount for November 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
    }

    private void verifyIsTextPresentInPage(String validationMessage) {
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li[text()=' ']"),"Blank Error message is thrown");
        Assert.assertTrue(selenium.isTextPresent(validationMessage),validationMessage);
        Assert.assertTrue(!selenium.isElementPresent("//span[@id='schedulePreview.error.message']/li[text()='']"),"Blank Error message is thrown");
    }

    public CreateLoanAccountPreviewPage verifyNegativeCashFlowWarning() {
        verifyIsTextPresentInPage("Cash flow for September 2010 is negative");
        verifyIsTextPresentInPage("Cash flow for October 2010 is negative");
        verifyIsTextPresentInPage("Cash flow for November 2010 is negative");
        verifyIsTextPresentInPage("Cash flow for December 2010 is negative");
        return this;
        //To change body of created methods use File | Settings | File Templates.
    }

    public CreateLoanAccountPreviewPage verifyZeroCashFlowWarning(String warningThreshold) {
        verifyIsTextPresentInPage("Cash flow for September 2010 is zero");
        verifyIsTextPresentInPage("Installment amount for September 2010 as % of warning threshold exceeds the allowed warning threshold of " + warningThreshold+ "%");
        return this;
    }

    public void verifyLoanAmount(String amount) {
        Assert.assertEquals(getLoanAmount(), amount);
    }

    public String getLoanAmount() {
        return selenium.getText("createloanpreview.text.loanamount");
    }

    public QuestionResponsePage navigateToQuestionResponsePage() {
        selenium.click("editQuestionResponses_button");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }
}
