package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.user.UserViewDetailsPage;

import com.thoughtworks.selenium.Selenium;

public class ViewSystemUsersPage extends MifosPage {

    public ViewSystemUsersPage(Selenium selenium) {
        super(selenium);
        verifyPage("viewusers");
    }

    public UserViewDetailsPage searchAndNavigateToUserViewDetailsPage(String userName) {
        selenium.type("viewusers.input.search", userName);
        selenium.click("viewusers.button.search");
        waitForPageToLoad();
        selenium.click("link=*"+userName+"*");
        waitForPageToLoad();
        return new UserViewDetailsPage(selenium);
    }
}
