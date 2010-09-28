package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class ViewClientChargesDetail extends AbstractPage {

    public ViewClientChargesDetail(Selenium selenium){
        super(selenium);
    }

    public ApplyChargesPage navigateToApplyCharges(){
        selenium.click("view_clientchargesdetail.link.applyCharges");
        waitForPageToLoad();
        return new ApplyChargesPage(selenium);
    }


}
