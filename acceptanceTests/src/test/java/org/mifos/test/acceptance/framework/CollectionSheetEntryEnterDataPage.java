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
	
    public CollectionSheetEntryEnterDataPage verifyPage(CollectionSheetEntrySelectPage.SubmitFormParameters parameters) {
        Assert.assertTrue(selenium.isElementPresent("bulkentry_data.heading"),"Didn't get to Bulk Entry Enter Data page");
		return this;
	}

	public CollectionSheetEntryPreviewDataPage submitAndGotoCollectionSheetEntryPreviewDataPage() {
		selenium.click("bulkentry_data.button.preview");
		waitForPageToLoad();
		return new CollectionSheetEntryPreviewDataPage(selenium);
		
	}

    public CollectionSheetEntryEnterDataPage enterAccountValue(int row, int column, double amount) {
        selenium.type("enteredAmount[" + row + "][" + column + "]", Double.toString(amount));
        return this;
    }

    public CollectionSheetEntryEnterDataPage enterCustomerAccountValue(int row, int column, double amount) {
        selenium.type("customerAccountAmountEntered[" + row + "][" + column + "]", Double.toString(amount));
        return this;
    }

}
