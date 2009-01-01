/**
 * 
 */
package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntryConfirmationPage extends AbstractPage {

    public CollectionSheetEntryConfirmationPage(Selenium selenium) {
        super(selenium);
    }

    public CollectionSheetEntryConfirmationPage verifyPage() {
        Assert.assertTrue(selenium.isElementPresent("bulkentry_preview.label.entersuccess"),"Didn't get to Collection Sheet Entry confirmation page");
        return this;
    }

}
