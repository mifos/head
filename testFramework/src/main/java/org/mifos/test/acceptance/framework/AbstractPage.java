/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

import org.testng.Assert;

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

    public void openUri(String uri) {
        selenium.open(uri);
        waitForPageToLoad();
    }

    public void verifyPage(String pageName) {
        String pageId = selenium.getAttribute("page.id@title");
        if("Exception".equals(pageId)){
            String stackTrace = selenium.getText("css=div.stackTrace pre");
            Assert.fail("Expected page <" +pageName +">, actual page <"+pageId+">!!! with error message > " + stackTrace);
        }
        else if(!pageId.equals(pageName)) {
            String errors = getErrors();
            Assert.fail("Expected page <" +pageName +">, actual page <"+pageId+">!!! with error message > " + errors);
        }
    }
    
    public String getErrors() {
    	int totalErrorElements = selenium.getXpathCount("//*[contains(@id, \'error.message\')]").intValue();
    	StringBuilder sb = new StringBuilder();
    	for (int i = 1; i <= totalErrorElements; i++) {
    		String text = selenium.getText("//*[contains(@id, \'error.message\')][" + i + "]");
    		if (text != null) {
    			sb.append(text);
    		}
    	}
    	return sb.toString();
    }

    public String getTextIfExists(String locator) {
        String text = null;
        try {
         text = selenium.getText("error.messages");
        } catch (SeleniumException se) { // NOPMD by ugupta on 15/3/11 10:16 PM
            // do nothing
        }
        return text;
    }

    public void verifyPage(String pageName, String secondName) {
        String pageID = selenium.getAttribute("page.id@title");
        if(!pageID.equals(pageName)) {
            Assert.assertEquals(pageID, secondName);
        }
    }

    public void typeText(String locator, String text) {
        selenium.focus(locator);
        selenium.type(locator,text);
//        selenium.keyDown(locator, tabKey);
//        selenium.keyUp(locator, tabKey);
        selenium.keyPressNative("9");
    }
    public void waitForElementToPresent(String locator){
        selenium.waitForCondition("selenium.isElementPresent(\"" + locator + "\")",MAX_WAIT_FOR_PAGE_TO_LOAD_IN_MILLISECONDS);
    }

    public void navigateBack(){
        selenium.goBack();
    }

}
