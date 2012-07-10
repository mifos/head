package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ImportSavingsSaveSummaryPage extends MifosPage {
    public ImportSavingsSaveSummaryPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("ImportSavingsSaved");
    }

    public void verifySuccesString(String succesNumber) {
        Assert.assertTrue(selenium.isTextPresent("Loans successfully saved: "+succesNumber+";"));
    }

    public void verifyErrorString(String errorNumber) {
        Assert.assertTrue(selenium.isTextPresent("Ignored rows: "+errorNumber+";"));
    }
}
