package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class ClientsAndAccountsHomepage extends AbstractPage {
	
	public ClientsAndAccountsHomepage() {
		super();
	}
	
	public ClientsAndAccountsHomepage(Selenium selenium) {
		super(selenium);
	}

	public CollectionSheetEntrySelectPage navigateToEnterCollectionSheetDataUsingLeftMenu() {
		selenium.click("link=Enter Collection Sheet Data");
		waitForPageToLoad();
		return new CollectionSheetEntrySelectPage(selenium);
	}

}
