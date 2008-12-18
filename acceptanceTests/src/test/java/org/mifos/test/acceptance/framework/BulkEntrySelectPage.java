/**
 * 
 */
package org.mifos.test.acceptance.framework;


import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

/**
 * @author keith
 *
 */
public class BulkEntrySelectPage extends AbstractPage {

	
	public BulkEntrySelectPage() {
		super();
	}

	public BulkEntrySelectPage(Selenium selenium) {
		super(selenium);
	}

	public void verifyPage() {
		Assert.assertTrue(selenium.isTextPresent(" Bulk entry - Select Center"), "Didn't reach Bulk entry select page");
	}

//	public BulkEntryEnterDataPage submitForm(
//						String branch, String loanOfficer, String center,
//						String transactionDay, String transactionMonth, String transactionYear, 
//						String paymentMode, String receiptId, 
//						String receiptDay, String receiptMonth, String receiptYear) {
//		
//		selenium.select("officeId",          "label=" + branch);
//		waitForPageToLoad();
//		selenium.select("loanOfficerId",     "label="+ loanOfficer);
//		waitForPageToLoad();
//		selenium.select("customerId",        "label="+ center);
//		waitForPageToLoad();
//		typeTextIfNotEmpty  ("transactionDateDD", transactionDay);
//		typeTextIfNotEmpty  ("transactionDateMM", transactionMonth);
//		typeTextIfNotEmpty  ("transactionDateYY", transactionYear);
//		selenium.select("paymentId",         "label=" + paymentMode);
//		typeTextIfNotEmpty  ("receiptId",         "123456789");
//		typeTextIfNotEmpty  ("receiptDateDD",     receiptDay);
//		typeTextIfNotEmpty  ("receiptDateMM",     receiptMonth);
//		typeTextIfNotEmpty  ("receiptDateYY",     receiptYear);
//		selenium.click ("//input[@value='Continue']");
//		waitForPageToLoad();
//		return new BulkEntryEnterDataPage(selenium);
//	}
	
	private void typeTextIfNotEmpty(String locator, String value) {
		if (value!= null && !value.isEmpty()) {
			selenium.type(locator, value);
		}
	}
}
