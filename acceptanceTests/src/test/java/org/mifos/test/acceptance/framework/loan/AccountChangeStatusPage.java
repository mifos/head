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

import org.apache.commons.lang.StringUtils;
import org.testng.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.account.EditAccountStatusParameters;
import org.mifos.test.acceptance.framework.questionnaire.QuestionResponsePage;

import com.thoughtworks.selenium.Selenium;


public class AccountChangeStatusPage extends MifosPage {
    public AccountChangeStatusPage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ChangeStatus");
    }

    @Deprecated
    public EditAccountStatusConfirmationPage submitAndNavigateToNextPage(EditLoanAccountStatusParameters params) {
        /* usually this would be an "id=..." locator but since this is a radio button we have to use
           name + value (id + value is not supported by Selenium). */
        selenium.check("name=newStatusId value=" + params.getStatusValue());

        if(selenium.isElementPresent("name=newStatusId value=10")) {
            selenium.fireEvent("name=newStatusId value=10", "click");

            // TODO: fix me, it'll never be empty.
            if (StringUtils.isNotEmpty(params.getCancelReason())) {
                selenium.select("change_status.input.cancel_reason", "value=" + params.getCancelReasonValue());
            }
        }
        
        selenium.type("change_status.input.note", params.getNote());

        submit();
        return new EditAccountStatusConfirmationPage(selenium);
    }

    public EditAccountStatusConfirmationPage setChangeStatusParametersAndSubmit(EditAccountStatusParameters editAccountStatusParameters){
        populateFields(editAccountStatusParameters);
        submit();
        return new EditAccountStatusConfirmationPage(selenium);
    }

    public QuestionResponsePage submitAndNavigateToQuestionResponsePage(EditAccountStatusParameters editAccountStatusParameters) {
        populateFields(editAccountStatusParameters);
        submit();
        return new QuestionResponsePage(selenium);
    }

    private void populateFields(EditAccountStatusParameters editAccountStatusParameters) {
        selenium.check("name=newStatusId value=" + editAccountStatusParameters.getAccountStatus().getId());
        selenium.fireEvent("name=newStatusId value=" + editAccountStatusParameters.getAccountStatus().getId(), "click");

        selenium.type("change_status.input.note", editAccountStatusParameters.getNote());
    }

    private void submit() {
        selenium.click("change_status.button.submit");
        waitForPageToLoad();
    }
    
    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(selenium.isTextPresent(expectedText), errorMessage);
    }
    
    public void verifyNotTextPresent(String expectedText, String errorMessage) {
        Assert.assertFalse(selenium.isTextPresent(expectedText), errorMessage);
    }
}
