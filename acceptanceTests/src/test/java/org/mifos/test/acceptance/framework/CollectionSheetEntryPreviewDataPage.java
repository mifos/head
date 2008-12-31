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
	

	public void verifyPage(CollectionSheetEntrySelectPage.SubmitFormParameters parameters) {
		Assert.assertTrue(selenium.isTextPresent("Bulk entry-") && selenium.isTextPresent("Preview data"), "Didn't get to Bulk Entry Preview Data page");
	}


}
