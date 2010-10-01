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


import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class DefineNewLoanProductPreviewPage extends AbstractPage {

    public DefineNewLoanProductPreviewPage() {
        super();
    }

    public DefineNewLoanProductPreviewPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("CreateLoanProductPreview");
    }

    public DefineNewLoanProductConfirmationPage submit() {
        selenium.click("createLoanProductPreview.button.submit");
        waitForPageToLoad();
        DefineNewLoanProductConfirmationPage defineNewLoanProductConfirmationPage = new DefineNewLoanProductConfirmationPage(selenium);
        defineNewLoanProductConfirmationPage.verifyPage();
        return defineNewLoanProductConfirmationPage;
     }

    public void verifyErrorInForm(String error)
    {
        Assert.assertTrue(selenium.isTextPresent(error));
    }

    public DefineNewLoanProductPreviewPage verifyVariableInstalmentOption(String maxGap, String minGap, String minInstalmentAmount, String variableInstalmentChoice) {
        Assert.assertTrue(selenium.isTextPresent("Minimum gap between installments: " + minGap));
        if ("".equals(maxGap)) {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Maximum gap between installments: " + maxGap  + " days"));
        }
        if ("".equals(minInstalmentAmount)) {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(selenium.isTextPresent("Minimum installment amount: " + minInstalmentAmount)) ;
        }
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: " + variableInstalmentChoice));
        return new DefineNewLoanProductPreviewPage(selenium);
    }

    public DefineNewLoanProductPreviewPage verifyVariableInstalmentUnChecked() {
        Assert.assertTrue(!selenium.isTextPresent("Minimum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Maximum gap between installments:"));
        Assert.assertTrue(!selenium.isTextPresent("Minimum installment amount:" )) ;
        Assert.assertTrue(selenium.isTextPresent("Can configure variable installments: No"));
        return this;
    }

    public DefineNewLoanProductPreviewPage verifyCashFlowInPreview(String warningThreshold) {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: Yes"));
        if ("".equals(warningThreshold)) {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: N/A"));
        } else {
            Assert.assertTrue(selenium.isTextPresent("Warning Threshold: " + warningThreshold + " %"));
        }
        return this;
    }

    public DefineNewLoanProductPreviewPage verifyCashFlowUnCheckedInPreview() {
        Assert.assertTrue(selenium.isTextPresent("Compare with Cash Flow: No"));
        Assert.assertTrue(!selenium.isTextPresent("Warning Threshold:"));
        return this;
    }
}
