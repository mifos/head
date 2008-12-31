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
	

	public void verifyPage(String branch, String loanOfficer, String center,
			String transactionDay, String transactionMonth, String transactionYear, String paymentMode,
			String receiptId, String receiptDay, String receiptMonth, String receiptYear) {
		Assert.assertTrue(selenium.isTextPresent("Bulk entry-") && selenium.isTextPresent("Preview data"), "Didn't get to Bulk Entry Preview Data page");
	}


}
