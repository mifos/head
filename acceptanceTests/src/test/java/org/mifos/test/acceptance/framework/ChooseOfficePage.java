/**
 * 
 */
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

public class ChooseOfficePage extends MifosPage {

    public ChooseOfficePage(Selenium selenium) {
        super(selenium);
    }

    public OfficeViewDetailsPage navigateToOfficeViewDetailsPage() {
        selenium.click("createNewOfficeSuccessful.link.viewOfficeDetails");
        waitForPageToLoad();
        return new OfficeViewDetailsPage(selenium);
    }
    
    public CreateUserEnterDataPage selectOffice(String officeName) {
        selenium.click("link=" + officeName);
        waitForPageToLoad();
        return new CreateUserEnterDataPage(selenium);
    }

}
