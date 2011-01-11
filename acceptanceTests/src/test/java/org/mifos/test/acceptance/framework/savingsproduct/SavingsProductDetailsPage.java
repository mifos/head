package org.mifos.test.acceptance.framework.savingsproduct;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class SavingsProductDetailsPage extends MifosPage {

    public SavingsProductDetailsPage(Selenium selenium) {
        super(selenium);
        verifyPage("SavingsProductDetails");
    }

}
