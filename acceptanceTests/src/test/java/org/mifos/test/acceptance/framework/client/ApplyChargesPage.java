package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.ViewCenterChargesDetailPage;
import org.mifos.test.acceptance.framework.group.ViewGroupChargesDetailPage;
import org.mifos.test.acceptance.framework.loan.ChargeParameters;

import com.thoughtworks.selenium.Selenium;

public class ApplyChargesPage extends MifosPage{

    public ApplyChargesPage(Selenium selenium){
        super(selenium);
        this.verifyPage("ApplyCharges");
    }

    @Deprecated
    public ViewClientChargesDetail applyCharge(String chargeType){
        selenium.select("applyCharges.input.type", "label="+chargeType);
        selenium.click("applyCharges.button.submit");
        return new ViewClientChargesDetail(selenium);
    }

    private void applyCharge(ChargeParameters chargeParameters){
        selenium.select("applyCharges.input.type", "label="+chargeParameters.getType());
        this.typeTextIfNotEmpty("applyCharges.input.amount", chargeParameters.getAmount());
        selenium.click("applyCharges.button.submit");
        waitForPageToLoad();
    }

    public ViewClientChargesDetail applyChargeAndNaviagteToViewClientChargesDetail(ChargeParameters chargeParameters){
        applyCharge(chargeParameters);
        return new ViewClientChargesDetail(selenium);
    }

    public ViewCenterChargesDetailPage applyChargeAndNaviagteToViewCenterChargesDetailPage(ChargeParameters chargeParameters){
        applyCharge(chargeParameters);
        return new ViewCenterChargesDetailPage(selenium);
    }

    public ViewGroupChargesDetailPage applyChargeAndNaviagteToViewGroupChargesDetailPage(ChargeParameters chargeParameters){
        applyCharge(chargeParameters);
        return new ViewGroupChargesDetailPage(selenium);
    }
}
