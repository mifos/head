package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.AbstractPage;

import com.thoughtworks.selenium.Selenium;

public class ApplyChargesPage extends AbstractPage{

    public ApplyChargesPage(Selenium selenium){
        super(selenium);
    }

    public void applyCharge(String chargeType){
        selenium.select("applyCharges.input.type", "label="+chargeType);
        selenium.click("applyCharges.button.submit");
    }

}
