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

import org.apache.commons.collections.CollectionUtils;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.loanproduct.DefineNewLoanProductPage.SubmitFormParameters;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

import java.util.List;

public class EditLoanProductPage extends MifosPage {

    String configureVariableInstalmentsCheckbox = "canConfigureVariableInstallments";
    String minInstalmentGapTextBox = "minimumGapBetweenInstallments";
    String maxInstalmentGapTextBox = "maximumGapBetweenInstallments";
    String minInstalmentAmountTextBox = "minimumInstallmentAmount";
    String cashFlowCheckbox = "cashFlowValidation";
    String cashFlowThresholdTextBox = "cashFlowThreshold";
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
        return editSubmit();
    }

    public EditLoanProductPreviewPage submitRequiredDescriptionAndInterestChanges(SubmitFormParameters parameters) {
            selenium.type("EditLoanProduct.input.name", parameters.getOfferingName());
            selenium.type("EditLoanProduct.input.shortName", parameters.getOfferingShortName());
            selenium.type("EditLoanProduct.input.description", parameters.getDescription());
            selenium.type("startDateDD", parameters.getStartDateDd());
            selenium.type("startDateMM", parameters.getStartDateMm());
            selenium.type("startDateYY", parameters.getStartDateYy());
            selenium.type("EditLoanProduct.input.maxInterestRate", parameters.getMaxInterestRate());
            selenium.type("EditLoanProduct.input.minInterestRate", parameters.getMinInterestRate() );
            selenium.type("EditLoanProduct.input.defaultInterestRate", parameters.getDefaultInterestRate());

            if(parameters.getProductCategory()==0) {
                selenium.select("EditLoanProduct.input.category", "value=");
            } else {
                selenium.select("EditLoanProduct.input.category", "value="+parameters.getProductCategory());
            }
            if (parameters.getApplicableFor()==0) {
                selenium.select("EditLoanProduct.input.applicableFor", "value=");
            } else {
                selenium.select("EditLoanProduct.input.applicableFor", "value=" + parameters.getApplicableFor());
            }

            if(parameters.getStatus()==0) {
                selenium.select("EditLoanProduct.input.status","value=");
            } else {
                selenium.select("EditLoanProduct.input.status","value=" + parameters.getStatus());
            }

            if(parameters.getInterestTypes()==0) {
                selenium.select("EditLoanProduct.input.interestTypes", "value=" );
            } else {
                selenium.select("EditLoanProduct.input.interestTypes", "value=" + parameters.getInterestTypes());
            }
        return editSubmit();
    }

    public void verifyModifiedDescriptionAndInterest(SubmitFormParameters formParameters) {
        Assert.assertEquals(getDescription(), formParameters.getDescription());
        Assert.assertEquals(getMinInterestRate(), formParameters.getMinInterestRate());
        Assert.assertEquals(getMaxInterestRate(), formParameters.getMaxInterestRate());
        Assert.assertEquals(getDefaultInterestRate(), formParameters.getDefaultInterestRate());
    }

    public void verifyModifiedLoanProduct(SubmitFormParameters formParameters) {
        Assert.assertEquals(getDescription(), formParameters.getDescription());
        Assert.assertEquals(getMinInterestRate(), formParameters.getMinInterestRate());
        Assert.assertEquals(getMaxInterestRate(), formParameters.getMaxInterestRate());
        Assert.assertEquals(getDefaultInterestRate(), formParameters.getDefaultInterestRate());
        Assert.assertEquals(selenium.getValue("EditLoanProduct.input.name"), formParameters.getOfferingName());
        Assert.assertEquals(selenium.getValue("EditLoanProduct.input.shortName"), formParameters.getOfferingShortName());
        Assert.assertEquals(selenium.getValue("startDateDD"), formParameters.getStartDateDd());
        Assert.assertEquals(selenium.getValue("startDateMM"), formParameters.getStartDateMm());
        Assert.assertEquals(selenium.getValue("startDateYY"), formParameters.getStartDateYy());

        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.applicableFor"), Integer.toString(formParameters.getApplicableFor()));
        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.status"), Integer.toString(formParameters.getStatus()));
        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.interestTypes"), Integer.toString(formParameters.getInterestTypes()));
        Assert.assertEquals(selenium.getSelectedValue("EditLoanProduct.input.category"), Integer.toString(formParameters.getProductCategory()));
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
        return editSubmit();
    }

    public EditLoanProductPreviewPage submitIncludeInLoanCounter(SubmitFormParameters formParameters) {
        if (formParameters.isIncludeInLoanCounter()) {
            selenium.check("EditLoanProduct.input.includeInLoanCycleCounter");
        } else {
            selenium.uncheck("EditLoanProduct.input.includeInLoanCycleCounter");
        }
        return editSubmit();
    }

    public EditLoanProductPreviewPage submitQuestionGroupChanges(SubmitFormParameters formParameters) {
        List<String> questionGroups = formParameters.getQuestionGroups();
        if (CollectionUtils.isNotEmpty(questionGroups)) {
            for (String questionGroup : questionGroups) {
                selenium.addSelection("name=id", questionGroup);
            }
            selenium.click("SrcQGList.button.add");
        }
        return editSubmit();
    }
    public EditLoanProductPreviewPage submitVariableInstalmentChange(String maxGap, String minGap, String minInstalmentAmount) {
        if (!selenium.isChecked(configureVariableInstalmentsCheckbox)){
            selenium.click(configureVariableInstalmentsCheckbox);
        }
        selenium.waitForCondition("selenium.isVisible('minimumInstallmentAmount')","10000");
        selenium.type(maxInstalmentGapTextBox, maxGap);
        selenium.type(minInstalmentGapTextBox, minGap);
        selenium.type(minInstalmentAmountTextBox, minInstalmentAmount);
        return editSubmit();
    }
    public EditLoanProductPreviewPage editSubmit() {
        selenium.click("EditLoanProduct.button.preview");
        waitForPageToLoad();
        return new EditLoanProductPreviewPage(selenium);
    }
    public void verifyVariableInstalmentOptionDefaults() {
        verifyVariableInstalmentOptionsWhenPageLoads();
        selenium.click(configureVariableInstalmentsCheckbox);
        verifyVariableInstalmentFieldsWhenSelected();
    }
    private void verifyVariableInstalmentFieldsWhenSelected() {
        selenium.waitForCondition("selenium.isVisible('minimumInstallmentAmount')","10000");
        Assert.assertTrue(selenium.isVisible(maxInstalmentGapTextBox));
        Assert.assertTrue(selenium.isVisible(minInstalmentGapTextBox));
        Assert.assertTrue(selenium.isVisible(minInstalmentAmountTextBox));
//      Assert.assertTrue(selenium.getValue(minInstalmentGapTextBox).equals("1"));
        Assert.assertTrue(selenium.getValue(maxInstalmentGapTextBox).equals(""));
        Assert.assertTrue(selenium.getValue(minInstalmentAmountTextBox).equals(""));
    }
    private void verifyVariableInstalmentOptionsWhenPageLoads() {
        Assert.assertTrue(!selenium.isChecked(configureVariableInstalmentsCheckbox)
                & !selenium.isVisible(maxInstalmentGapTextBox)
                & !selenium.isVisible(minInstalmentGapTextBox)
                & !selenium.isVisible(minInstalmentAmountTextBox));
    }
    public void verifyVariableInstalmentOptionFields() {
        submitVariableInstalmentChange("text,", "text,", "text,");
        Assert.assertTrue(selenium.isTextPresent("The min installment amount for variable installments is invalid because only numbers or decimal separator are allowed"));
        Assert.assertTrue(!selenium.getValue(maxInstalmentGapTextBox).contains("text") & !selenium.getValue(maxInstalmentGapTextBox).contains(","));
        Assert.assertTrue(!selenium.getValue(minInstalmentGapTextBox).contains("text") & !selenium.getValue(minInstalmentGapTextBox).contains(","));

        submitVariableInstalmentChange("1000","1000", "1");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must be less than 4 digits for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("Maximum gap must be less than 4 digits for loans with variable installments"));

        submitVariableInstalmentChange("-1","-1", "-1");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("The min installment amount for variable installments is invalid because only numbers or decimal separator are allowed."));

        submitVariableInstalmentChange("0","0", "0");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must not be zero or negative for loans with variable installments"));

        submitVariableInstalmentChange("1","10", "1");
        Assert.assertTrue(selenium.isTextPresent("Minimum gap must be less than the maximum gap for loans with variable installments"));
    }
    public void verifyVariableInstalmentOptionDisabled() {
        Assert.assertTrue(!selenium.isElementPresent(configureVariableInstalmentsCheckbox));
    }

    public EditLoanProductPage setCashFlowThreshold(String warningThreshold) {
        if (!selenium.isChecked(cashFlowCheckbox)) {
            selenium.click(cashFlowCheckbox);
        }
        selenium.type(cashFlowThresholdTextBox, warningThreshold);
        return this;
    }

    public EditLoanProductPage verifyCashFlowDefaultsInEditProduct() {
        Assert.assertTrue(!selenium.isChecked(cashFlowCheckbox));
        Assert.assertTrue(!selenium.isVisible(cashFlowThresholdTextBox));

        selenium.click(cashFlowCheckbox);
        Assert.assertTrue(selenium.isVisible(cashFlowThresholdTextBox));
        return this;
    }

}
