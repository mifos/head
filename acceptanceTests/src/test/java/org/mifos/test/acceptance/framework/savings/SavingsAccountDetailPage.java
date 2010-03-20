/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.test.acceptance.framework.savings;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.loan.AccountAddNotesPage;
import org.mifos.test.acceptance.framework.loan.AccountNotesPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

@SuppressWarnings("PMD.SystemPrintln")
public class SavingsAccountDetailPage extends AbstractPage {

    public SavingsAccountDetailPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("savingsaccountdetail");
    }

    public void verifySavingsAmount(String amount) {
        Assert.assertTrue(selenium.isTextPresent(amount));
    }

    public void verifySavingsProduct(String savingsProduct) {
        Assert.assertTrue(selenium.isTextPresent(savingsProduct));

    }
    public AccountAddNotesPage navigateToAddNotesPage() {
        selenium.click("savingsaccountdetail.link.addANotes");
        waitForPageToLoad();
        return new AccountAddNotesPage(selenium);
    }

    public AccountNotesPage navigateToAccountNotesPage() {
        selenium.click("savingsaccountdetail.link.seeAllNotes");
        waitForPageToLoad();
        return new AccountNotesPage(selenium);
    }

    public SavingsDepositWithdrawalPage navigateToDepositWithdrawalPage() {
        selenium.click("savingsaccountdetail.link.makeDepositWithdrawal");
        waitForPageToLoad();
        return new SavingsDepositWithdrawalPage(selenium);
    }

}