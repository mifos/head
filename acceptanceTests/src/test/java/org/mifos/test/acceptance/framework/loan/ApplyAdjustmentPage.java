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

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

@SuppressWarnings("PMD.SystemPrintln")
public class ApplyAdjustmentPage extends AbstractPage {

    public ApplyAdjustmentPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("ApplyAdjustment");
    }


    public LoanAccountPage verifyAdjustment(String adjustmentAmount) {
        Assert.assertTrue(selenium.isTextPresent("Last payment made: " + adjustmentAmount + " "));
        return fillAdjustmentFieldsAndSubmit(adjustmentAmount);
    }

    public LoanAccountPage fillAdjustmentFieldsAndSubmit(String adjustmentAmount) {
        applyAdjustment(adjustmentAmount);
        return new LoanAccountPage(selenium);
    }

    private void applyAdjustment(String adjustmentAmount) {
        if(selenium.isElementPresent("applyadjustment.input.revertLastPayment")) {
            selenium.click("applyadjustment.input.revertLastPayment");
            selenium.type("applyadjustment.input.note", "testNotes paid Amount: " + adjustmentAmount);
            selenium.click("applyadjustment.button.submit");
            waitForPageToLoad();
        }
        selenium.click("applyadjustment.button.submit");
        waitForPageToLoad();
    }

    public ApplyAdjustmentPage verifyAdjustBackdatedPermission() {
        applyAdjustment("10");
        Assert.assertTrue(selenium.isTextPresent("You do not have permissions to perform this activity. Contact your system administrator to grant you required permissions and try again."));
        return this;
    }

    public LoanAccountPage cancelAdjustment() {
        selenium.click("applyadjustment.button.cancel");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public LoanAccountPage verifyAdjustment(String adjustmentAmount, String note) {
        Assert.assertTrue(selenium.isTextPresent("Last payment made: " + adjustmentAmount + " "));
        selenium.click("applyadjustment.input.revertLastPayment");
        selenium.type("applyadjustment.input.note", note);
        selenium.click("applyadjustment.button.submit");
        waitForPageToLoad();
        selenium.click("applyadjustment.button.submit");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }
    public LoanAccountPage verifyRepayAdjustment(String loanAmount) {
        String correctLoanAmount = loanAmount;
        
        if(loanAmount.indexOf('.') < 0) {
            correctLoanAmount = loanAmount + ".0";
        }
        
        Assert.assertTrue(selenium.isTextPresent(correctLoanAmount.replace(",", "")));
        selenium.click("applyadjustment.button.submit");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public ApplyAdjustmentPage verifyAdjustBackdatedPermissionOnRepay() {
        selenium.click("applyadjustment.button.submit");
        waitForPageToLoad();
        Assert.assertTrue(selenium.isTextPresent("You do not have permissions to perform this activity. Contact your system administrator to grant you required permissions and try again."));
        return this;
    }
}
