/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.loanproduct;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class EditLoanProductPage extends MifosPage {

    String configureVariableInstalmentsCheckbox = "";
    String minInstalmentGapTextBox = "";
    String maxInstalmentGapTextBox = "";
    private String minInstalmentAmountTextBox = "";

    public EditLoanProductPage(Selenium selenium) {
        super(selenium);
    }

    public EditLoanProductPage verifyPage() {
        verifyPage("EditLoanProduct");
        return this;
    }

    public EditLoanProductPreviewPage submitDescriptionAndInterestChanges(SubmitFormParameters parameters) {
        selenium.type("EditLoanProduct.input.description", parameters.getDescription());
        selenium.type("EditLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
        selenium.type("EditLoanProduct.input.minInterestRate", parameters.getMinInterestRate() );
        selenium.type("EditLoanProduct.input.defaultInterestRate", parameters.getDefaultInterestRate());
        selenium.click("EditLoanProduct.button.preview");
        waitForPageToLoad();
        return new EditLoanProductPreviewPage(selenium);
    }

    public void verifyModifiedDescriptionAndInterest(SubmitFormParameters formParameters) {
        Assert.assertEquals(getDescription(), formParameters.getDescription());
        Assert.assertEquals(getMinInterestRate(), formParameters.getMinInterestRate());
        Assert.assertEquals(getMaxInterestRate(), formParameters.getMaxInterestRate());
        Assert.assertEquals(getDefaultInterestRate(), formParameters.getDefaultInterestRate());
    }

    private String getDefaultInterestRate() {
        return selenium.getValue("EditLoanProduct.input.defaultInterestRate");
    }

    private String getMaxInterestRate() {
        return selenium.getValue("EditLoanProduct.input.maxInterestRate");
    }

    private String getMinInterestRate() {
        return selenium.getValue("EditLoanProduct.input.minInterestRate");
    }

    private String getDescription() {
        return selenium.getValue("EditLoanProduct.input.description");
    }

    public EditLoanProductPreviewPage submitInterestWaiverChanges(SubmitFormParameters formParameters) {
        if (formParameters.isInterestWaiver()) {
            selenium.check("EditLoanProduct.input.includeInterestWaiver");
        } else {
            selenium.uncheck("EditLoanProduct.input.includeInterestWaiver");
        }
        selenium.click("EditLoanProduct.button.preview");
        waitForPageToLoad();
        return new EditLoanProductPreviewPage(selenium);
    }

    public EditLoanProductPreviewPage submitVariableInstalmentChange(String maxGap, String minGap, String minInstalmentAmount) {
        if (!selenium.isChecked(configureVariableInstalmentsCheckbox)){
            selenium.check(configureVariableInstalmentsCheckbox);
        }
        selenium.type(maxInstalmentGapTextBox, maxGap);
        selenium.type(minInstalmentGapTextBox, minGap);
        selenium.type(minInstalmentAmountTextBox, minInstalmentAmount);
        selenium.click("EditLoanProduct.button.preview");
        waitForPageToLoad();
        return new EditLoanProductPreviewPage(selenium);
    }

    public void verifyVariableInstalmentOptionsDefaults() {
        Assert.assertTrue(selenium.isChecked(configureVariableInstalmentsCheckbox)
                & !selenium.isEditable(maxInstalmentGapTextBox)
                & !selenium.isEditable(minInstalmentGapTextBox)
                & !selenium.isEditable(minInstalmentAmountTextBox)
                & selenium.getValue(minInstalmentGapTextBox) == "1");
    }
    

    public void verifyMinimumAndMaximumInstalmentGapFields() {
        submitVariableInstalmentChange("text,.", "text,.", "1");
        Assert.assertTrue(selenium.isTextPresent("The max instalment gap is invalid because only numbers or decimal separator are allowed.") & selenium.isTextPresent("The min instalment gap is invalid because it is not in between 0.0 and 999.0."));
        submitVariableInstalmentChange("1000","1000", "1");
        Assert.assertTrue(selenium.isTextPresent("The min instalment gap is invalid because it is not in between 0.0 and 999.0.") & selenium.isTextPresent("The min instalment gap is invalid because it is not in between 0.0 and 999.0."));
        submitVariableInstalmentChange("-1","-1", "1");
        Assert.assertTrue(selenium.isTextPresent("The min instalment gap is invalid because it is not in between 0.0 and 999.0.") & selenium.isTextPresent("The min instalment gap is invalid because it is not in between 0.0 and 999.0."));
        submitVariableInstalmentChange("1","10", "1");
        Assert.assertTrue(selenium.isTextPresent("Please specify a valid Max gap instalment. Max gap instalment should be greater than or equal to Min instalment gap for all loans ."));
    }

    public void verifyMinimumVariableInstalmentAmountField() {
        submitVariableInstalmentChange("10","1","text");
        Assert.assertTrue(selenium.isTextPresent("The min variable instalment amount is invalid because only numbers or decimal separator are allowed."));
        submitVariableInstalmentChange("10","1","-1");
        Assert.assertTrue(selenium.isTextPresent("The min variable instalment amount is invalid because only numbers or decimal separator are allowed."));
    }

}
