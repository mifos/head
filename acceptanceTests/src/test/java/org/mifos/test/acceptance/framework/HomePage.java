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
 
package org.mifos.test.acceptance.framework;

import org.mifos.test.acceptance.framework.group.CreateGroupSearchPage;
import org.mifos.test.acceptance.framework.search.SearchResultsPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * Encapsulates the GUI based actions that can
 * be done from the Home page and the page 
 * that will be navigated to.
 *
 */
public class HomePage extends MifosPage {

	public HomePage() {
		super();
	}

	public HomePage(Selenium selenium) {
		super(selenium);
	}
	

    public HomePage verifyPage() {
		Assert.assertTrue(selenium.isTextPresent("You can navigate your way through Mifos using:"));
		return this;
    }

	public ClientsAndAccountsHomepage navigateToClientsAndAccountsUsingHeaderTab() {
		selenium.click("homeheader.link.clientsAndAccounts");
		waitForPageToLoad();
		return new ClientsAndAccountsHomepage(selenium);
	}
	
	public AdminPage navigateToAdminPage() {
        selenium.click("homeheader.link.admin");
        waitForPageToLoad();
        return new AdminPage(selenium);	    
	}
	
    public String getWelcome() {
        return selenium.getText("home.text.welcome");
    }
    
    public SearchResultsPage search(String searchFor) {
        selenium.type("home.input.search", searchFor);
        selenium.click("home.button.search");
        waitForPageToLoad();
        return new SearchResultsPage(selenium); 
    }

    public CreateGroupSearchPage navigateToCreateNewGroupSearchPage() {
        selenium.click("menu.link.create.new.group");
        waitForPageToLoad();
        return new CreateGroupSearchPage(selenium);        
    }
      
}
