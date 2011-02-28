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

package org.mifos.test.acceptance.framework.loanproduct;


import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

public class DefineNewLoanProductPreviewPage extends AbstractPage {

    public DefineNewLoanProductPreviewPage() {
        super();
    }

    public DefineNewLoanProductPreviewPage(Selenium selenium) {
        super(selenium);
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
        Assert.assertTrue(isTextPresentInPage(error));
    }

    public DefineNewLoanProductPreviewPage verifyVariableInstalmentOption(String maxGap, String minGap, String minInstalmentAmount) {
        Assert.assertTrue(isTextPresentInPage("Minimum gap between installments: " + minGap));
        if ("".equals(maxGap)) {
            Assert.assertTrue(isTextPresentInPage("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Maximum gap between installments: " + maxGap  + " days"));
        }
        if ("".equals(minInstalmentAmount)) {
            Assert.assertTrue(isTextPresentInPage("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(isTextPresentInPage("Minimum installment amount: " + minInstalmentAmount)) ;
        }
        Assert.assertTrue(isTextPresentInPage("Can configure variable installments: Yes"));
        return new DefineNewLoanProductPreviewPage(selenium);
    }

    public DefineNewLoanProductPreviewPage verifyVariableInstalmentUnChecked() {
        Assert.assertTrue(!isTextPresentInPage("Minimum gap between installments:"));
        Assert.assertTrue(!isTextPresentInPage("Maximum gap between installments:"));
        Assert.assertTrue(!isTextPresentInPage("Minimum installment amount:" )) ;
        Assert.assertTrue(isTextPresentInPage("Can configure variable installments: No"));
        return this;
    }

    public DefineNewLoanProductPreviewPage verifyCashFlowInPreview(String warningThreshold, String indebtentValue, String repaymentValue) {
        Assert.assertTrue(isTextPresentInPage("Compare with Cash Flow: Yes"));
        if ("".equals(warningThreshold)) {
            Assert.assertTrue(isTextPresentInPage("Warning Threshold: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Warning Threshold: " + warningThreshold + " %"));
        }
        if ("".equals(indebtentValue)) {
            Assert.assertTrue(isTextPresentInPage("Indebtedness Rate: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Indebtedness Rate: " + indebtentValue + " %"));
        }
        if ("".equals(repaymentValue)) {
            Assert.assertTrue(isTextPresentInPage("Repayment Capacity: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Repayment Capacity: " + repaymentValue + " %"));
        }
        return this;
    }

    public DefineNewLoanProductPreviewPage verifyCashFlowUnCheckedInPreview() {
        Assert.assertTrue(isTextPresentInPage("Compare with Cash Flow: No"));
        Assert.assertTrue(!isTextPresentInPage("Warning Threshold:"));
        Assert.assertTrue(!isTextPresentInPage("Indebtedness Rate:"));
        Assert.assertTrue(!isTextPresentInPage("Repayment Capacity:"));
        return this;
    }

    public DefineNewLoanProductPreviewPage verifyInterestTypeInPreview(String interestType) {
        String expectedText = "Interest rate type: " + interestType;
        Assert.assertTrue(isTextPresentInPage(expectedText), expectedText + " does not exist on page.");
        return this;
    }
}
