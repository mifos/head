/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class CreateUserConfirmationPage extends MifosPage {

    /**
     * @param selenium
     */
    public CreateUserConfirmationPage(Selenium selenium) {
        super(selenium);
    }
    
    public String getConfirmation() {
        return selenium.getText("createuser_confirmation.text.confirmation");
    }    

    public UserViewDetailsPage navigateToUserViewDetailsPage() {
        selenium.click("createuser_confirmation.link.viewUser");
        waitForPageToLoad();
        return new UserViewDetailsPage(selenium);
    }

}
