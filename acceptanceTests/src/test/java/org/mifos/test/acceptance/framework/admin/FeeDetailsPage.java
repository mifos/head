package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.admin.FeesCreatePage.SubmitFormParameters;

import com.thoughtworks.selenium.Selenium;

public class FeeDetailsPage extends MifosPage {

    public FeeDetailsPage(Selenium selenium) {
        super(selenium);
    }

    public FeeDetailsPage verifyPage() {
        verifyPage("FeeDetails");
        return this;
    }

    public void verifyFeeDetails(SubmitFormParameters formParameters) {
        this.verifyPage("FeeDetails");

        // selenium.getBodyText().contains(new String());
    }
}