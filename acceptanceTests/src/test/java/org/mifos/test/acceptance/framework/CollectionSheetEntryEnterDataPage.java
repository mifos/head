/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.test.acceptance.framework;

import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntryEnterDataPage extends AbstractPage {

	public CollectionSheetEntryEnterDataPage() {
		super();
	}

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
