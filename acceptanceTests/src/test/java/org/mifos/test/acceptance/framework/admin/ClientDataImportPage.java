package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class ClientDataImportPage extends MifosPage {
    
    public ClientDataImportPage(Selenium selenium){
        super(selenium);
    }

    public void verifyPage(){
        verifyPage("ImportUsersSelectFile");
    }

    public ClientDataImportReviewPage submitToReview(String importFile) {
        selenium.type("importClients.file", importFile);
        selenium.click("importClients.review");
        waitForPageToLoad();
        return new ClientDataImportReviewPage(selenium);
    }
}
