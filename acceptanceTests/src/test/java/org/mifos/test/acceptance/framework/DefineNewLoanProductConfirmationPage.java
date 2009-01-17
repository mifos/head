package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;


public class DefineNewLoanProductConfirmationPage extends MifosPage {

    /**
     * @param selenium
     */
    public DefineNewLoanProductConfirmationPage(Selenium selenium) {
        super(selenium);
    }
    

    public UserViewDetailsPage navigateToViewLoanDetails() {
        selenium.click("createLoanProductConfirmation.link.viewLoanDetails");
        waitForPageToLoad();
        return new UserViewDetailsPage(selenium);
    }

    public DefineNewLoanProductConfirmationPage verifyPage() {
    	Assert.assertTrue(selenium.isElementPresent("createLoanProductConfirmation.text.plsnote"));
    	return this;
        
    }

}
