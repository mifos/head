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

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.ClientsAndAccountsHomepage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CreateLoanAccountSearchPage extends AbstractPage {

    public CreateLoanAccountSearchPage(Selenium selenium) {
        super(selenium);
        verifyPage("CustSearchAccount");
    }

    public void verifyPage() {
        this.verifyPage("CustSearchAccount");
    }


    public CreateLoanAccountEntryPage searchAndNavigateToCreateLoanAccountPage(CreateLoanAccountSearchParameters formParameters) {
        selenium.type("cust_search_account.input.searchString", formParameters.getSearchString());
        selenium.click("cust_search_account.button.search");
        waitForPageToLoad();
        selenium.click("link=*" + formParameters.getSearchString() + "*");
        waitForPageToLoad();
        selenium.select("id=loancreationprodofferingselect.select.loanProduct", "label="+ formParameters.getLoanProduct());
        selenium.click ("id=loancreationprdofferingselect.button.continue");
        waitForPageToLoad();
        return new CreateLoanAccountEntryPage(selenium);
    }
    
    public ClientsAndAccountsHomepage cancel(){
        selenium.click("_eventId_cancel");
        waitForPageToLoad();
        return new ClientsAndAccountsHomepage(selenium);
    }
            
    public CreateLoanAccountSearchPage navigateToCreateLoanAccountEntryPage(CreateLoanAccountSearchParameters formParameters){
        selenium.type("cust_search_account.input.searchString", formParameters.getSearchString());
        selenium.click("cust_search_account.button.search");
        waitForPageToLoad();
        return this;
    }

    public CreateLoanAccountSelectLoanProductPage navigateToCreateLoanAccountSelectLoanProductPage(CreateLoanAccountSearchParameters formParameters){
        selenium.type("cust_search_account.input.searchString", formParameters.getSearchString());
        selenium.click("cust_search_account.button.search");
        waitForPageToLoad();
        selenium.click("link=*" + formParameters.getSearchString() + "*");
        waitForPageToLoad();
        return new CreateLoanAccountSelectLoanProductPage(selenium);
    }
    
    public void verifyTextPresent(String expectedText, String errorMessage) {
        Assert.assertTrue(selenium.isTextPresent(expectedText), errorMessage);
    }
            
    public void verifyNoSelectLoanProduct(CreateLoanAccountSearchParameters formParameters, String expectedMessage) {
        selenium.type("cust_search_account.input.searchString", formParameters.getSearchString());
        selenium.click("cust_search_account.button.search");
        waitForPageToLoad();
        selenium.click("link=*" + formParameters.getSearchString() + "*");
        waitForPageToLoad();
        selenium.click("id=loancreationprdofferingselect.button.continue");
        waitForPageToLoad();
        verifyTextPresent(expectedMessage, "No text <"+ expectedMessage +"> present on the page");     
    }
}
