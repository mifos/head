package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CreateFeesConfirmationPage extends MifosPage {



    public CreateFeesConfirmationPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("CreateFeesConfirmation");
    }

    public FeeDetailsPage navigateToViewDetailsNowPage(){
        selenium.click("CreateFeesConfirmation.label.viewFeeDetail");
        waitForPageToLoad();
        return new FeeDetailsPage(selenium);
    }


}