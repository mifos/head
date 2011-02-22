package org.mifos.test.acceptance.framework.center;

import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.client.ApplyChargesPage;
import org.mifos.test.acceptance.framework.loan.ApplyPaymentPage;

import com.thoughtworks.selenium.Selenium;

public class ViewCenterChargesDetailPage extends AbstractPage {

    public ViewCenterChargesDetailPage(Selenium selenium){
        super(selenium);
        verifyPage("view_centerchargesdetail");
    }

    public ApplyPaymentPage navigateToApplyPayments(){
        selenium.click("view_centerchargesdetail.link.applyPayment");
        waitForPageToLoad();
        return new ApplyPaymentPage(selenium);
    }

    public ApplyChargesPage navigateToApplyCharges(){
        selenium.click("view_centerchargesdetail.link.applyCharges");
        waitForPageToLoad();
        return new ApplyChargesPage(selenium);
    }

}
