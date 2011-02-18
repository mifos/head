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
package org.mifos.test.acceptance.framework.savingsproduct;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.savingsproduct.DefineNewSavingsProductPage.SubmitSavingsFormParameters;
import org.testng.Assert;



import com.thoughtworks.selenium.Selenium;

public class EditSavingsProductPage extends MifosPage {
    public EditSavingsProductPage(Selenium selenium) {
        super(selenium);
        verifyPage("Edit_SavingsProduct");
    }

    public EditSavingsProductPreviewPage submitRequiredDescriptionAndInterestChanges(SubmitSavingsFormParameters parameters) {
        selenium.type("createSavingsProduct.input.prdOfferingName", parameters.getOfferingName());
        selenium.type("createSavingsProduct.input.prdOfferingShortName", parameters.getOfferingShortName());
        selenium.type("startDateDD", parameters.getStartDateDd());
        selenium.type("startDateMM", parameters.getStartDateMm());
        selenium.type("startDateYY", parameters.getStartDateYy());
        selenium.type("interestRate", parameters.getInterestRate());
        selenium.type("interestPostingMonthlyFrequency", parameters.getFrequencyInterest());
        selenium.type("interestCalculationFrequency", parameters.getTimePeriodInterest());

        if(parameters.getProductCategory()==0) {
            selenium.select("generalDetails.selectedCategory", "value=");
        } else {
            selenium.select("generalDetails.selectedCategory", "value="+parameters.getProductCategory());
        }
        if (parameters.getApplicableFor()==0) {
            selenium.select("generalDetails.selectedApplicableFor", "value=");
        } else {
            selenium.select("generalDetails.selectedApplicableFor", "value=" + parameters.getApplicableFor());
        }
        if(parameters.getDepositType()==0) {
            selenium.select("selectedDepositType","value=");
        } else {
            selenium.select("selectedDepositType","value=" + parameters.getDepositType());
        }
        if(parameters.getStatus()==0) {
            selenium.select("generalDetails.selectedStatus","value=");
        } else {
            selenium.select("generalDetails.selectedStatus","value=" + parameters.getStatus());
        }
        if(parameters.getBalanceInterest()==0) {
            selenium.select("selectedInterestCalculation","value=");
        } else {
            selenium.select("selectedInterestCalculation","value=" + parameters.getBalanceInterest());
        }

        return editSubmit();
    }

    public EditSavingsProductPreviewPage editSubmit() {
        selenium.click("createSavingsProduct.button.preview");
        waitForPageToLoad();
        return new EditSavingsProductPreviewPage(selenium);
    }

    public void verifyModifiedSavingsProduct(SubmitSavingsFormParameters formSavingsParameters) {
        Assert.assertEquals(selenium.getValue("createSavingsProduct.input.prdOfferingName"), formSavingsParameters.getOfferingName());
        Assert.assertEquals(selenium.getValue("createSavingsProduct.input.prdOfferingShortName"), formSavingsParameters.getOfferingShortName());
        Assert.assertEquals(selenium.getValue("startDateDD"), formSavingsParameters.getStartDateDd());
        Assert.assertEquals(selenium.getValue("startDateMM"), formSavingsParameters.getStartDateMm());
        Assert.assertEquals(selenium.getValue("startDateYY"), formSavingsParameters.getStartDateYy());
        Assert.assertEquals(selenium.getValue("interestRate"), formSavingsParameters.getInterestRate());
        Assert.assertEquals(selenium.getValue("interestRate"), formSavingsParameters.getInterestRate());
        Assert.assertEquals(selenium.getValue("interestPostingMonthlyFrequency"), formSavingsParameters.getFrequencyInterest());
        Assert.assertEquals(selenium.getValue("interestCalculationFrequency"), formSavingsParameters.getTimePeriodInterest());
        Assert.assertEquals(selenium.getValue("interestCalculationFrequency"), formSavingsParameters.getTimePeriodInterest());
        Assert.assertEquals(selenium.getValue("interestCalculationFrequency"), formSavingsParameters.getTimePeriodInterest());

        Assert.assertEquals(selenium.getSelectedValue("generalDetails.selectedCategory"), Integer.toString(formSavingsParameters.getProductCategory()));
        Assert.assertEquals(selenium.getSelectedValue("generalDetails.selectedApplicableFor"), Integer.toString(formSavingsParameters.getApplicableFor()));
        Assert.assertEquals(selenium.getSelectedValue("selectedDepositType"), Integer.toString(formSavingsParameters.getDepositType()));
        Assert.assertEquals(selenium.getSelectedValue("generalDetails.selectedStatus"), Integer.toString(formSavingsParameters.getStatus()));
        Assert.assertEquals(selenium.getSelectedValue("selectedInterestCalculation"), Integer.toString(formSavingsParameters.getBalanceInterest()));


    }
}
