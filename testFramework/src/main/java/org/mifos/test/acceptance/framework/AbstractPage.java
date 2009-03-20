/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

/**
 * Base class for all Page objects -
 * There should be a Page object for each page referenced in an Selenium-based acceptance test.
 * Page objects should be the place that all verification and navigation is done,
 * to keep this activity in one place. If the web page changes, then only one class
 * needs to change to make all the tests that reference it work.
 */
public class AbstractPage {

	private static final String MAX_WAIT_FOR_PAGE_TO_LOAD_IN_MILLISECONDS = "30000";
	protected Selenium selenium;

	public AbstractPage() {
		// do nothing
	}
	
	public AbstractPage(Selenium selenium) {
		this.selenium = selenium;
	}
	
	protected void waitForPageToLoad() {
		selenium.waitForPageToLoad(MAX_WAIT_FOR_PAGE_TO_LOAD_IN_MILLISECONDS);
	}
	
	final public void setSelenium(Selenium selenium) {
		this.selenium = selenium;
	}

	public Selenium getSelenium() {
		return this.selenium;
	}

	public void openUri (String uri) {
		selenium.open(uri);
		waitForPageToLoad();
	}

	public void verifyPage(String pageName) {
		Assert.assertEquals(selenium.getValue("page.id"), pageName);
	}

}
