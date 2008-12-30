/**
 * 
 */
package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntryEnterDataPage extends AbstractPage {

	public CollectionSheetEntryEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CollectionSheetEntryEnterDataPage(Selenium selenium) {
		super(selenium);
	}
	
    public void verifyPage(CollectionSheetEntrySelectPage.SubmitFormParameters parameters) {
		Assert.assertTrue(selenium.isTextPresent("Bulk entry-") && selenium.isTextPresent("Enter data"), "Didn't get to Bulk Entry Enter Data page");
	}


}
