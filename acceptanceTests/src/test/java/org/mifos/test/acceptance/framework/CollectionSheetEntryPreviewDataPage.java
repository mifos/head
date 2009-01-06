/**
 * 
 */
package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntryPreviewDataPage extends AbstractPage {

	public CollectionSheetEntryPreviewDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CollectionSheetEntryPreviewDataPage(Selenium selenium) {
		super(selenium);
	}
	

	public CollectionSheetEntryPreviewDataPage verifyPage(CollectionSheetEntrySelectPage.SubmitFormParameters parameters) {
	    Assert.assertTrue(selenium.isElementPresent("bulkentry_preview.heading"),"Didn't get to Bulk Entry Preview Data page");
		return this;
	}

    public CollectionSheetEntryConfirmationPage submitAndGotoCollectionSheetEntryConfirmationPage() {
        selenium.click("bulkentry_preview.button.submit");
        waitForPageToLoad();
        return new CollectionSheetEntryConfirmationPage(selenium);
        
    }

	

}
