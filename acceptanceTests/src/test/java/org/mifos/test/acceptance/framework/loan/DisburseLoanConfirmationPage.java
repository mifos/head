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

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

public class DisburseLoanConfirmationPage extends MifosPage {
    public DisburseLoanConfirmationPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ReviewLoanDisbursement");
    }

    public LoanAccountPage submitAndNavigateToLoanAccountPage() {
        selenium.click("Review_loanDisbursement.button.submit");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public void submitButDisbursalFailed(String msg) {
        selenium.click("Review_loanDisbursement.button.submit");
        waitForPageToLoad();
        Assert.assertTrue(selenium.isElementPresent("//span[@id='Review_loanDisbursement.error.message']/ul/li[text()='"+msg+"']"));
    }

    public QuestionResponsePage navigateToEditAdditionalInformation() {
        selenium.click("editQuestionResponses_button");
        waitForPageToLoad();
        return new QuestionResponsePage(selenium);
    }
}
