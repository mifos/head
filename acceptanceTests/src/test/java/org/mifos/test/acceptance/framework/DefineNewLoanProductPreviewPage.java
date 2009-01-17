package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class DefineNewLoanProductPreviewPage extends AbstractPage {

    public DefineNewLoanProductPreviewPage() {
        super();
    }

    public DefineNewLoanProductPreviewPage(Selenium selenium) {
        super(selenium);
    }
 
    public DefineNewLoanProductPreviewPage verifyPage() {
        Assert.assertTrue(selenium.isElementPresent("createLoanProductPreview.heading"),"Didn't reach Create Loan Product Preview page");
        return this;
    }

    public DefineNewLoanProductConfirmationPage submit() {
        selenium.click("createLoanProductPreview.button.submit");
        waitForPageToLoad();
        return new DefineNewLoanProductConfirmationPage(selenium);
     }

}
