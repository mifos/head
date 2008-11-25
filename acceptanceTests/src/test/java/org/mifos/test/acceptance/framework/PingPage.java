package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class PingPage extends AbstractPage {

    public PingPage(Selenium selenium) {
        super(selenium);
    }

    public void navigateToPingPage() {
        this.selenium.open("ping.ftl");
    }

    public void verifyPage() {
        Assert.assertEquals(this.selenium.getTitle(), "ping");
        Assert.assertTrue(this.selenium.isTextPresent("OK"), "'OK' not found on page.");
    }

}
