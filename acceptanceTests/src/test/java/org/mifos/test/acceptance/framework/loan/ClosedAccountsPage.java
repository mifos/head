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

import junit.framework.Assert;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;

import com.thoughtworks.selenium.Selenium;

public class ClosedAccountsPage extends MifosPage {
    public ClosedAccountsPage(Selenium selenium) {
        super(selenium);
        verifyPage("GetAllClosedAccounts");
    }

    public ClosedAccountsPage verifyPage() {
        verifyPage("GetAllClosedAccounts");
        return this;
    }

    public CenterViewDetailsPage returnToCenterViewDetailsPage() {
        selenium.click("getallclosedaccounts.button.cancel");
        waitForPageToLoad();
        return new CenterViewDetailsPage(selenium);
    }

    public GroupViewDetailsPage returnToGroupViewDetailsPage() {
        selenium.click("getallclosedaccounts.button.cancel");
        waitForPageToLoad();
        return new GroupViewDetailsPage(selenium);
    }

    public ClientViewDetailsPage returnToClientViewDetailsPage() {
        selenium.click("getallclosedaccounts.button.cancel");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }

    public LoanAccountPage navigateToLoanAccountPage() {
        selenium.click("getallclosedaccounts.link.viewLoanAccount");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public LoanAccountPage verifyAndNavigateToOneClosedLoan(String loanID) {
        Assert.assertTrue(isTextPresentInPage(loanID));
        return navigateToLoanAccountPage();
    }
}
