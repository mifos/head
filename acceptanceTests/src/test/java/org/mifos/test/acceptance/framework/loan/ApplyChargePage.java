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

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;


public class ApplyChargePage extends MifosPage {
    public ApplyChargePage(Selenium selenium) {
        super(selenium);
        this.verifyPage("ApplyCharges");
    }

    public LoanAccountPage submitAndNavigateToApplyChargeConfirmationPage(ChargeParameters params)
    {
        selenium.select("applyCharges.input.type", "value=" + params.getTypeValue());
        this.typeTextIfNotEmpty("applyCharges.input.amount", params.getAmount());
        selenium.click("applyCharges.button.submit");
        waitForPageToLoad();

        return new LoanAccountPage(selenium);
    }
    
    public DivideGroupChargesPage submitAndNavigateToDivideGroupChargesPage(ChargeParameters params)
    {
        selenium.select("applyCharges.input.type", "value=" + params.getTypeValue());
        this.typeTextIfNotEmpty("applyCharges.input.amount", params.getAmount());
        selenium.click("applyCharges.button.submit");
        waitForPageToLoad();

        return new DivideGroupChargesPage(selenium);
    }

    public LoanAccountPage submitUsingLabelAndNavigateToApplyChargeConfirmationPage(ChargeParameters params)
    {
        selenium.select("applyCharges.input.type", params.getType());
        this.typeTextIfNotEmpty("applyCharges.input.amount", params.getAmount());
        selenium.click("applyCharges.button.submit");
        waitForPageToLoad();

        return new LoanAccountPage(selenium);
    }

    public void verifyBlockedFee(String[] blockedInterest) {
        for (String element : blockedInterest) {
            String fee = element;
            Assert.assertTrue(!selenium.isElementPresent("//select[@name='chargeType']/option[text()='" + fee + "']"));
        }
    }

    public LoanAccountPage applyFeeAndConfirm(ChargeParameters chargeParameters) {
        selenium.select("applyCharges.input.type","label=" + chargeParameters.getType());
        selenium.click("applyCharges.button.submit");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }
}
