/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class CreateOfficeConfirmationPage extends MifosPage {

    public CreateOfficeConfirmationPage(Selenium selenium) {
        super(selenium);
    }
    
    public String getConfirmation() {
        return selenium.getText("createNewOfficeSuccessful.text.confirmation");
    }
    
   
    public OfficeViewDetailsPage navigateToOfficeViewDetailsPage() {
        selenium.click("createNewOfficeSuccessful.link.viewOfficeDetails");
        waitForPageToLoad();
        return new OfficeViewDetailsPage(selenium);
    }

}
