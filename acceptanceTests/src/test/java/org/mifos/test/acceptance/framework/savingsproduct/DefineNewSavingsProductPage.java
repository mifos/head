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

import com.thoughtworks.selenium.Selenium;

public class DefineNewSavingsProductPage extends MifosPage {
    public DefineNewSavingsProductPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("CreateSavingsProduct");
    }

    public DefineNewSavingsProductPreviewPage submitAndNavigateToDefineNewSavingsProductPreviewPage(SavingsProductParameters productParameters) {
        selenium.type("CreateSavingsProduct.input.prdOfferingName", productParameters.getProductInstanceName());
        selenium.type("CreateSavingsProduct.input.prdOfferingShortName", productParameters.getShortName());
        selenium.type("startDateDD", productParameters.getStartDateDD());
        selenium.type("startDateMM", productParameters.getStartDateMM());
        selenium.type("startDateYY", productParameters.getStartDateYYYY());

        selectValueIfNotZero("savingsProduct.generalDetails.selectedCategory", productParameters.getProductCategory());

        selectValueIfNotZero("savingsProduct.generalDetails.selectedApplicableFor", productParameters.getApplicableFor());
        selectValueIfNotZero("savingsProduct.selectedDepositType", productParameters.getTypeOfDeposits());
        typeTextIfNotEmpty("savingsProduct.amountForDeposit", productParameters.getMandatoryAmount());
        selectValueIfNotZero("savingsProduct.selectedGroupSavingsApproach", productParameters.getAmountAppliesTo());
        selenium.type("savingsProduct.interestRate", productParameters.getInterestRate());

        selectValueIfNotZero("savingsProduct.selectedInterestCalculation", productParameters.getBalanceUsedForInterestCalculation());

        selenium.type("savingsProduct.interestCalculationFrequency", productParameters.getNumberOfDaysOrMonthsForInterestCalculation());
        selectValueIfNotZero("savingsProduct.selectedFequencyPeriod", productParameters.getDaysOrMonthsForInterestCalculation());

        selenium.type("savingsProduct.selectedFequencyPeriod", productParameters.getFrequencyOfInterestPostings());

        selectIfNotEmpty("savingsProduct.selectedPrincipalGlCode", productParameters.getGlCodeForDeposit());
        selectIfNotEmpty("savingsProduct.selectedInterestGlCode", productParameters.getGlCodeForInterest());

        selenium.click("CreateSavingsProduct.button.preview");
        waitForPageToLoad();

        return new DefineNewSavingsProductPreviewPage(selenium);
    }
}
