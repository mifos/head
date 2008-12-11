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
	
	private static String USERNAME_INPUT_ID = "login.input.username";
	private static String PASSWORD_INPUT_ID = "login.input.password";
	private static String LOGIN_SUBMIT_BUTTON_ID = "foo";
	
	
	

	public LoginPage() {
		super();
	}
	
	public LoginPage(Selenium selenium) {
		super(selenium);
	}
	
	public HomePage loginSuccessfulAs(String userName, String password) {
		selenium.open("loginAction.do?method=load");
		selenium.type("userName", userName);
		selenium.type("password", password);
		selenium.click("//input[@value='Login']");
		waitForPageToLoad();
		return new HomePage(selenium);
	}
	
	public LoginPage loginFailedAs(String userName, String password) {
		selenium.open("loginAction.do?method=load");
		selenium.type("userName", userName);
		selenium.type("password", password);
		selenium.click("//input[@value='Login']");
		waitForPageToLoad();
		return new LoginPage(selenium);
	}

	public LoginPage logout() {
		selenium.open("loginAction.do?method=logout");
		waitForPageToLoad();
		return new LoginPage(selenium);
	}

	public LoginPage verifyPage() {
        Assert.assertEquals(selenium.getText("login.label.heading"), "Login");
        Assert.assertEquals(selenium.getText("login.label.welcome"), "Welcome to mifos");
        Assert.assertEquals(selenium.getText("login.label.username"), "Username");
        Assert.assertEquals(selenium.getText("login.label.password"), "Password");
		return this;
	}

	public LoginPage verifyFailedLogin() {
        Assert.assertEquals(selenium.getText("login.error.message"), "Please specify valid username/password to access the application.");
		return this;
	}

}
