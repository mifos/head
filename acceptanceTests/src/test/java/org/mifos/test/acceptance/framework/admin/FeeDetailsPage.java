package org.mifos.test.acceptance.framework.admin;

import junit.framework.Assert;

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
        Assert.assertTrue(selenium.isTextPresent("Fee Applies To: " + formParameters.getFeeAppliesToName()));
        Assert.assertTrue(selenium.isTextPresent("Default fees: " + formParameters.getDefaultFeesName()));
        Assert.assertTrue(selenium.isTextPresent("Frequency: " + formParameters.getFrequencyName()));
        Assert.assertTrue(selenium.isTextPresent("Time of charge: " + formParameters.getTimeOfChargeName()));
        Assert.assertTrue(selenium.isTextPresent("Amount: " + formParameters.getAmountName()));
        Assert.assertTrue(selenium.isTextPresent("GL Code: " + formParameters.getGlCodeName()));
    }
}