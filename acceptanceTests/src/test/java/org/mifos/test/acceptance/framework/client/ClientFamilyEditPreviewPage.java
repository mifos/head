package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class ClientFamilyEditPreviewPage extends MifosPage {
    
    public ClientFamilyEditPreviewPage(Selenium selenium) {
        super(selenium);
    }
    
    public void verifyPage() {
        this.verifyPage("PreviewEditClientFamilyInfo");
    }
    
    public ClientViewDetailsPage submit() {
        selenium.click("preview_EditClientFamilyInfo.button.submit");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }
}
