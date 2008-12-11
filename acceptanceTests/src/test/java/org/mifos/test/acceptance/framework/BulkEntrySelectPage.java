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
}
