package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class PreviewFeesCreatePage extends AbstractPage {

    public PreviewFeesCreatePage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage(){
         super.verifyPage("previewfeescreate");
    }

    public CreateFeesConfirmationPage submit(){
        selenium.click("previewfeescreate.button.submitBtn");
        waitForPageToLoad();
        return new CreateFeesConfirmationPage(selenium);
    }



}