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

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class CreateLoanAccountsSearchPage extends AbstractPage {

    public CreateLoanAccountsSearchPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("CreateMultipleLoanAccounts");
    }

    public CreateLoanAccountsEntryPage searchAndNavigateToCreateMultipleLoanAccountsEntryPage(CreateMultipleLoanAccountSelectParameters formParameters) {
        selenium.select("id=createMultipleLoanAccounts.select.branchOffice", "label=" + formParameters.getBranch());
        waitForPageToLoad();
        selenium.select("id=createMultipleLoanAccounts.select.loanOfficer", "label="+ formParameters.getLoanOfficer());
        waitForPageToLoad();
        selenium.select("id=createMultipleLoanAccounts.select.center", "label="+ formParameters.getCenter());
        waitForPageToLoad();
        selenium.select("id=createMultipleLoanAccounts.select.loanProduct", "label="+ formParameters.getLoanProduct());
        selenium.click ("id=createMultipleLoanAccounts.button.submit");
        waitForPageToLoad();

        return new CreateLoanAccountsEntryPage(selenium);
    }

}
