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

import org.mifos.test.acceptance.framework.login.LoginPage;

import com.thoughtworks.selenium.Selenium;

/**
 * Responsible for common actions that are executable by a logged-in user.
 *
 */
public class MifosPage extends AbstractPage {
	
	public MifosPage() {
		super();
	}
	
	public MifosPage(Selenium selenium) {
		super(selenium);
	}
	
	public LoginPage logout() {
		selenium.open("loginAction.do?method=logout");
		waitForPageToLoad();
		return new LoginPage(selenium);
	}

    protected void typeTextIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.type(locator, value);
        }
    }
    
    protected void selectIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.select(locator, "label=" + value);
        }
    } 
    
    protected void checkIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.check(locator);
        }
    } 
    
    protected void selectAndWaitIfNotEmpty(String locator, String value) {
        if (!isEmpty(value)) {
            selenium.select(locator, "label=" + value);
            waitForPageToLoad();
        }
    }     

    private boolean isEmpty(String value) {
        boolean empty = true;
        if (value!= null && !value.isEmpty()) {
            empty = false;
        }
        return empty;
    }
}
