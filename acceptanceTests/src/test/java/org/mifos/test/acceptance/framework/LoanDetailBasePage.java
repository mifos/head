package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;
import org.testng.Assert;

public class LoanDetailBasePage extends AbstractPage {

    public LoanDetailBasePage() {
        super();
    }

    public LoanDetailBasePage(Selenium selenium) {
        super(selenium);
    }

    public LoanDetailBasePage verifyInterestType(String interestType) {
        Assert.assertTrue(isTextPresentInPage("Interest rate type: " + interestType));
        return this;
    }

}
