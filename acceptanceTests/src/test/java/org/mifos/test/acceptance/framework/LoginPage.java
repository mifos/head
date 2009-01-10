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

/**
 * Encapsulates the GUI based actions that can
 * be done from the Login page and the page 
 * that will be navigated to.
 *
 */
public class LoginPage extends AbstractPage {
	
	private static final String USERNAME_INPUT_ID	= "login.input.username";
	private static final String PASSWORD_INPUT_ID = "login.input.password";
	private static final String LOGIN_BUTTON_ID 	= "login.button.login";
	private static final String HEADING_LABEL_ID 	= "login.label.heading";
	private static final String WELCOME_LABEL_ID 	= "login.label.welcome";
	private static final String USERNAME_LABEL_ID = "login.label.username";
	private static final String PASSWORD_LABEL_ID = "login.label.password";
	private static final String MESSAGE_ERROR_ID 	= "login.error.message";
	
	

	public LoginPage() {
		super();
	}
	
	public LoginPage(Selenium selenium) {
		super(selenium);
	}
	
	public HomePage loginSuccessfulAs(String userName, String password) {
		selenium.open("loginAction.do?method=load");
		selenium.type(USERNAME_INPUT_ID, userName);
		selenium.type(PASSWORD_INPUT_ID, password);
		selenium.click(LOGIN_BUTTON_ID);
		waitForPageToLoad();
		return new HomePage(selenium);
	}
	
	public LoginPage loginFailedAs(String userName, String password) {
		selenium.open("loginAction.do?method=load");
		selenium.type(USERNAME_INPUT_ID, userName);
		selenium.type(PASSWORD_INPUT_ID, password);
		selenium.click(LOGIN_BUTTON_ID);
		waitForPageToLoad();
		return new LoginPage(selenium);
	}

	public LoginPage logout() {
		selenium.open("loginAction.do?method=logout");
		waitForPageToLoad();
		return new LoginPage(selenium);
	}

	public LoginPage verifyPage() {
        Assert.assertEquals(selenium.getText(HEADING_LABEL_ID), "Login");
        Assert.assertEquals(selenium.getText(WELCOME_LABEL_ID), "Welcome to mifos");
        Assert.assertEquals(selenium.getText(USERNAME_LABEL_ID), "Username");
        Assert.assertEquals(selenium.getText(PASSWORD_LABEL_ID), "Password");
		return this;
	}

	public LoginPage verifyFailedLoginBadPassword() {
        Assert.assertEquals(selenium.getText(MESSAGE_ERROR_ID), "Please specify valid username/password to access the application.");
		return this;
	}

	public LoginPage verifyFailedLoginNoPassword() {
	        Assert.assertEquals(selenium.getText(MESSAGE_ERROR_ID), "Please specify the value for Password.");
			return this;
	}

	public LoginPage verifyFailedLoginNoUsername() {
		        Assert.assertEquals(selenium.getText(MESSAGE_ERROR_ID), "Please specify the value for Username.");
				return this;
	}

}
