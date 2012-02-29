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

public class ViewInstallmentDetailsPage extends AbstractPage {

    public ViewInstallmentDetailsPage(Selenium selenium) {
        super(selenium);
//        verifyPage("SchedulePreview", "NextPaymentLoanAccount");
        verifyPage("NextPaymentLoanAccount");
    }

    public void verifyInstallmentAmount(int row, int column, String amount) {
        Assert.assertEquals(selenium.getTable("loanInstallmentTable." + row + "." + column), amount);
    }

    public void waiveOverdueInstallmentFee() {
        selenium.click("id=nextPayment_loanAccount.link.waiveFeeOverDue");
        waitForPageToLoad();
    }
    
    public void waiveOverdueInstallmentPenalty() {
        selenium.click("id=nextPayment_loanAccount.link.waivePenaltyOverDue");
        waitForPageToLoad();
    }
    
    public boolean isWaiveOverdueInstallmentPenalty() {
        return selenium.isElementPresent("id=nextPayment_loanAccount.link.waivePenaltyOverDue");
    }

    public ApplyAdjustmentPage navigateToApplyAdjustment() {
        selenium.click("nextPayment_loanAccount.link.applyAdjustment");
        waitForPageToLoad();
        return new ApplyAdjustmentPage(selenium);

    }
}
