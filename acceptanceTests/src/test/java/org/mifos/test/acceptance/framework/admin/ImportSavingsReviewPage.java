package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ImportSavingsReviewPage extends MifosPage {
    public ImportSavingsReviewPage(Selenium selenium){
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("ImportSavingsReview");
    }

    public void validateErrors(String[] arrayOfErrors) {
        for (String string : arrayOfErrors) {
            Assert.assertTrue(selenium.isTextPresent(string));
        }
    }

    public void validateSuccesText(String numberOfGoodRows) {
        Assert.assertTrue(selenium.isTextPresent("Import Status: "+numberOfGoodRows+" rows parsed successfully"));
    }

    public ImportSavingsSaveSummaryPage saveSuccessfullRows(){
        selenium.click("importSavings.submit");
        waitForPageToLoad();
        ImportSavingsSaveSummaryPage summaryPage= new ImportSavingsSaveSummaryPage(selenium);
        summaryPage.verifyPage();
        return summaryPage;
    }
}
