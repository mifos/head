package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;
import com.thoughtworks.selenium.Selenium;

public class DefineLabelsPage extends MifosPage {

    public DefineLabelsPage(Selenium selenium) {
        super(selenium);
    }

    public void verifyPage() {
        verifyPage("definelabels");
    }
    
    public void setLabelValue(String label, String value) {
        selenium.type("definelabels.input." + label, value);              
    }

    public String getCitizenshipLabel() {
        return selenium.getText("defineLabels.input.citizenship");
    }

    public String getGovtIdLabel() {
        return selenium.getText("defineLabels.input.govtId");
    }

    public void verifyLabelValue(String label, String value) {               
        Assert.assertEquals(selenium.getValue(label), value);
    }
    
    public void verifyCitizenshipLabel(DefineLabelsParameters labelParameters) {
        Assert.assertEquals(getCitizenshipLabel(), labelParameters.getCitizenship());
    }

    public void verifyGovtIdLabel(DefineLabelsParameters labelParameters) {
        Assert.assertEquals(getGovtIdLabel(), labelParameters.getGovtId());
    }
    
    public AdminPage submit() {
        selenium.click("definelabels.button.submit");
        waitForPageToLoad();
        return new AdminPage(selenium);
    }
   
}