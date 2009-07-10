package org.mifos.test.acceptance.framework.loan;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class ApplyPaymentConfirmationPage extends AbstractPage {

    public ApplyPaymentConfirmationPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        this.verifyPage("ReviewApplyPayment");
    }

    public LoanAccountPage submitAndNavigateToLoanAccountDetailsPage() {
        selenium.click("reviewapplypayment.button.submit");
        waitForPageToLoad();
        return new LoanAccountPage(selenium);
    }
    
}
