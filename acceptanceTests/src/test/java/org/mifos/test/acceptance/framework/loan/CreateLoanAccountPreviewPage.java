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

package org.mifos.test.acceptance.framework.loan;

import com.thoughtworks.selenium.Selenium;
import org.mifos.test.acceptance.framework.AbstractPage;
import org.testng.Assert;

public class CreateLoanAccountPreviewPage extends AbstractPage {
    String editScheduleButton = "//input[@id='createloanpreview.button.edit' and @name='editButton' and @value='Edit Loan Schedule Information']";

    public CreateLoanAccountPreviewPage(Selenium selenium) {
        super(selenium);
    }

    public CreateLoanAccountPreviewPage() {
        super();
    }

    public CreateLoanAccountPreviewPage verifyPage() {
        this.verifyPage("CreateLoanPreview");
        return this;
    }

    public CreateLoanAccountPreviewPage verifyInterestTypeInLoanPreview(String interestType) {
        Assert.assertTrue(selenium.isTextPresent("Interest Rate Type :  " + interestType));
        return this;
    }

    public ViewInstallmentDetailsPage verifyEditSchedule() {
        Assert.assertTrue(selenium.isElementPresent(editScheduleButton));
        selenium.click(editScheduleButton);
        waitForPageToLoad();
        verifyPage("SchedulePreview");
        return new ViewInstallmentDetailsPage(selenium);
    }

    public void verifyEditScheduleDisabled() {
        Assert.assertTrue(!selenium.isElementPresent(editScheduleButton));
    }
}
