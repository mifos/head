package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class ImportSavingsPage extends MifosPage {
    
    public ImportSavingsPage(Selenium selenium){
        super(selenium);
    }

    public void verifyPage(){
        verifyPage("ImportSavingsSelectFile");
    }

    public ImportSavingsReviewPage submitToReview(String importFile) {
        selenium.type("importSaving.file", importFile);
        selenium.click("importSaving.review");
        waitForPageToLoad();
        return new ImportSavingsReviewPage(selenium);
    }
}
