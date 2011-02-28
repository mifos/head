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

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

public class EditLoanProductPreviewPage extends MifosPage {

    public EditLoanProductPreviewPage(Selenium selenium) {
        super(selenium);
    }

    public EditLoanProductPreviewPage verifyPage() {
        verifyPage("EditLoanProductPreview");
        return this;
    }
    public LoanProductDetailsPage submit() {
        selenium.click("EditLoanProductPreview.button.submit");
        waitForPageToLoad();
        return new LoanProductDetailsPage(selenium);
    }


    public EditLoanProductPreviewPage verifyVariableInstalmentOption(String maximumGap, String minGap, String minimumInstalmentAmount) {
        Assert.assertTrue(isTextPresentInPage("Minimum gap between installments: " + minGap + " days"));
        if ("".equals(maximumGap)) {
            Assert.assertTrue(isTextPresentInPage("Maximum gap between installments: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Maximum gap between installments: " + maximumGap  + " days"));
        }
        if ("".equals(minimumInstalmentAmount)) {
            Assert.assertTrue(isTextPresentInPage("Minimum installment amount: N/A")) ;
        } else {
            Assert.assertTrue(isTextPresentInPage("Minimum installment amount: " + minimumInstalmentAmount)) ;
        }
        Assert.assertTrue(isTextPresentInPage("Can configure variable installments: Yes"));
        return this;
    }

    public EditLoanProductPreviewPage verifyVariableInstalmentUnChecked() {
        Assert.assertTrue(!isTextPresentInPage("Minimum gap between installments:"));
        Assert.assertTrue(!isTextPresentInPage("Maximum gap between installments:"));
        Assert.assertTrue(!isTextPresentInPage("Minimum installment amount:")) ;
        Assert.assertTrue(isTextPresentInPage("Can configure variable installments: No"));
        return this;
    }


    public EditLoanProductPreviewPage verifyCashflowThresholdInEditPreview(String warningThreshold, String indebetedValues, String repaymentCapacityValue) {
        Assert.assertTrue(isTextPresentInPage("Compare with Cash Flow: Yes"));
        if ("".equals(warningThreshold)) {
            Assert.assertTrue(isTextPresentInPage("Warning Threshold: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Warning Threshold: " + warningThreshold + " %"));
        }
        if ("".equals(indebetedValues)) {
            Assert.assertTrue(isTextPresentInPage("Indebtedness Rate: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Indebtedness Rate: " + indebetedValues + " %"));
        }
        if ("".equals(repaymentCapacityValue)) {
            Assert.assertTrue(isTextPresentInPage("Repayment Capacity: N/A"));
        } else {
            Assert.assertTrue(isTextPresentInPage("Repayment Capacity: " + repaymentCapacityValue + " %"));
        }
        return this;
    }

    public EditLoanProductPreviewPage verifyCashFlowUncheckedInEditPreview() {
        Assert.assertTrue(isTextPresentInPage("Compare with Cash Flow: No"));
        Assert.assertTrue(!isTextPresentInPage("Warning Threshold:"));
        return this;
    }
}
