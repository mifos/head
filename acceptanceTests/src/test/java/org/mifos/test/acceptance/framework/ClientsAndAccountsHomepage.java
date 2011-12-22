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

package org.mifos.test.acceptance.framework;

import org.mifos.test.acceptance.framework.center.CreateCenterChooseOfficePage;
import org.mifos.test.acceptance.framework.client.ClientSearchResultsPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.group.CreateGroupSearchPage;
import org.mifos.test.acceptance.framework.group.GroupSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountSearchPage;
import org.mifos.test.acceptance.framework.loan.CreateLoanAccountsSearchPage;
import org.mifos.test.acceptance.framework.savings.CreateSavingsAccountSearchPage;
import org.testng.Assert;


import com.thoughtworks.selenium.Selenium;

public class ClientsAndAccountsHomepage extends AbstractPage {

    public ClientsAndAccountsHomepage() {
        super();
    }

    public ClientsAndAccountsHomepage(Selenium selenium) {
        super(selenium);
        verifyPage("ClientsAccounts");
    }

    // TODO fix these 5 following methods. They all belong in a navigation helper.
    public CollectionSheetEntrySelectPage navigateToEnterCollectionSheetDataUsingLeftMenu() {
        selenium.click("id=menu.link.label.entercollectionsheetdata");
        waitForPageToLoad();
        return new CollectionSheetEntrySelectPage(selenium);
    }

    public CreateLoanAccountsSearchPage navigateToCreateMultipleLoanAccountsUsingLeftMenu() {
        selenium.click("menu.link.label.createmultipleloanaccounts");
        waitForPageToLoad();
        return new CreateLoanAccountsSearchPage(selenium);
    }

    public CreateLoanAccountSearchPage navigateToCreateLoanAccountUsingLeftMenu() {
        selenium.click("menu.link.label.createloanaccount");
        waitForPageToLoad();
        return new CreateLoanAccountSearchPage(selenium);
    }

    public CreateSavingsAccountSearchPage navigateToCreateSavingsAccountUsingLeftMenu() {
        selenium.click("menu.link.label.createsavingsaccount");
        waitForPageToLoad();
        return new CreateSavingsAccountSearchPage(selenium);
    }

    public CreateCenterChooseOfficePage navigateToCreateNewCenterPage() {
        selenium.click("menu.link.label.createnewcenter");
        waitForPageToLoad();
        return new CreateCenterChooseOfficePage(selenium);
    }

    public GroupSearchPage navigateToCreateNewClientPage() {
        selenium.click("menu.link.label.createnewclient");
        waitForPageToLoad();
        return new GroupSearchPage(selenium);
    }

    public CreateGroupSearchPage navigateToCreateNewGroupPage() {
        selenium.click("menu.link.label.createnewgroup");
        waitForPageToLoad();
        return new CreateGroupSearchPage(selenium);
    }

    // TODO belongs in a helper

    // TODO is this not in SearchHelper?
    public ClientSearchResultsPage searchForClient(String searchString)
    {
        selenium.type("clients_accounts.input.search", searchString);
        selenium.click("clients_accounts.button.search");
        waitForPageToLoad();
        return new ClientSearchResultsPage(selenium);
    }
    
    public void verifyMenuWithCenterHierarchyOn() {
        Assert.assertTrue(selenium.isElementPresent("menu.link.label.createnewcenter"));
        Assert.assertTrue(selenium.isTextPresent("To review or edit a Client, Group, Center or account"));
    }
    
    public void verifyMenuWithCenterHierarchyOff() {
        Assert.assertFalse(selenium.isElementPresent("menu.link.label.createnewcenter"));
        Assert.assertTrue(selenium.isTextPresent("To review or edit a Client, Group or account"));    	
    }    
    
    public String getClientsAndAccountsCreateCenterLink() {
        return selenium.getText("menu.link.label.createnewcenter");
    }
    
    public String getClientsAndAccountsChangeAccountStatusLink() {
        return selenium.getText("menu.link.label.approveMultipleLoans");
    }
    
    public String getClientsAndAccountsCreateMultipleLoansLink() {
        return selenium.getText("menu.link.label.createmultipleloanaccounts");
    }
    
    public String getClientsAndAccountsCreateClientLink() {
        return selenium.getText("menu.link.label.createnewclient");
    }
    
    public String getClientsAndAccountsCreateGroupLink() {
        return selenium.getText("menu.link.label.createnewgroup");
    }
    
    public String getClientsAndAccountsCreateLoanAccountLink() {
        return selenium.getText("menu.link.label.createloanaccount");
    }
    
    public String getClientsAndAccountsAdminHeaderTab() {
        return selenium.getText("clientsAndAccountsHeader.link.admin"); 
    }
    
    public String getClientsAndAccountsHeaderTab() {
        return selenium.getText("clientsAndAccountsHeader.link.clientsAndAccounts"); 
    }
    
    public String getClientsAndAccountsHomeHeaderTab() {
        return selenium.getText("clientsAndAccountsHeader.link.home"); 
    }
    
    public String getClientsAndAccountsReportsHeaderTab() {
        return selenium.getText("clientsAndAccountsHeader.link.reports"); 
    }
}
