/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

public class CreateOfficeConfirmationPage extends MifosPage {

    public CreateOfficeConfirmationPage(Selenium selenium) {
        super(selenium);
    }
    
    public void verifyPage() {
        Assert.assertTrue(selenium.isElementPresent("createNewOfficeSuccessful.text.confirmation"), "Confirmation message not found on create new office confirmation page");
    }
    
    public OfficeViewDetailsPage navigateToOfficeViewDetailsPage() {
        selenium.click("createNewOfficeSuccessful.link.viewOfficeDetails");
        waitForPageToLoad();
        return new OfficeViewDetailsPage(selenium);
    }

}
