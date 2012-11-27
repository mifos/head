package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ClientDataImportReviewPage extends MifosPage {
    public ClientDataImportReviewPage(Selenium selenium){
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("ImportClientsReview");
    }

    public void validateErrors(String[] arrayOfErrors) {
        for (String string : arrayOfErrors) {
            Assert.assertTrue(selenium.isTextPresent(string));
        }
    }

    public void validateSuccesText(String numberOfGoodRows) {
        Assert.assertTrue(selenium.isTextPresent("Import Status: "+numberOfGoodRows+" rows parsed successfully"));
    }

    public ClientDataImportSaveSummaryPage saveSuccessfullRows(){
        selenium.click("importClients.submit");
        waitForPageToLoad();
        ClientDataImportSaveSummaryPage summaryPage= new ClientDataImportSaveSummaryPage(selenium);
        summaryPage.verifyPage();
        return summaryPage;
    }
}
