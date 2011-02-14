package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;

import com.thoughtworks.selenium.Selenium;

public class ViewGroupChargesDetailPage extends AbstractPage {

    public ViewGroupChargesDetailPage(Selenium selenium){
        super(selenium);
        verifyPage("ViewGroupChargesDetail");
    }

    public ApplyPaymentPage navigateToApplyPayments(){
        selenium.click("view_groupchargesdetail.link.applyPayment");
        waitForPageToLoad();
        return new ApplyPaymentPage(selenium);
    }


}
