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

package org.mifos.test.acceptance.framework.loan;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.HomePage;

import com.thoughtworks.selenium.Selenium;

public class CreateLoanAccountsSuccessPage extends AbstractPage {

    public CreateLoanAccountsSuccessPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("CreateMultipleLoanAccountsConfirm");
    }

    public LoanAccountPage selectLoansAndNavigateToLoanAccountPage(int loanNumberToChoose) {
        selenium.click("id=CreateMultipleLoanAccountsConfirm.link.account." + loanNumberToChoose);
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }

    public List<String> verifyAndGetLoanAccountNumbers(int expectedLoanAccountNumbers){
        String[] links = selenium.getAllLinks();
        List<String> accountNumbers = new ArrayList<String>();
        for (String link : links) {
            if (link.startsWith("CreateMultipleLoanAccountsConfirm.link.account.")){
                String text = selenium.getText("id="+link);
                String accountNumber = text.split("#")[1];
                accountNumbers.add(accountNumber);
            }
        }

        Assert.assertEquals(expectedLoanAccountNumbers, accountNumbers.size());

        return accountNumbers;
    }

    public HomePage navigateToHomePage(){
        selenium.click("id=clientsAndAccountsHeader.link.home");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

}
