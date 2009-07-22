package org.mifos.test.acceptance.framework.loan;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CenterViewDetailsPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;
import org.mifos.test.acceptance.framework.group.GroupViewDetailsPage;

import com.thoughtworks.selenium.Selenium;

public class ClosedAccountsPage extends MifosPage {
    public ClosedAccountsPage(Selenium selenium) {
        super(selenium);
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
}
