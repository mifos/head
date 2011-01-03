/*
 * Copyright (c) 2011 Grameen Foundation USA
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

package org.mifos.server;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


/**
 * Tests launching of Mifos through simple embedded Servlet Container Web Server.
 * 
 * @author Michael Vorburger
 */
public class MifosServerLauncherTest {

	private static ServerLauncher serverLauncher;

	@BeforeClass
	public static void beforeClass() throws Exception {
		serverLauncher = new ServerLauncher();
		serverLauncher.startServer();
	}
	
	@AfterClass
	public static void afterClass() throws Exception {
		serverLauncher.stopServer();
		serverLauncher = null;
	}
	
	@Test
	public void testStartup() throws Exception {
		// Do nothing here... just ensure that the beforeClass
		// did not actually fail already.
	}

	@Test
	public void testAndAssertLoginPageSeen() throws Exception {
		WebDriver wd = new HtmlUnitDriver(true);
		wd.get(getAppURL());
		
		// TODO Better assert that the correct thing is on the Login Page, e.g. by ID
		Assert.assertTrue("Not on Login page", wd.getPageSource().contains("Login"));
	}
	
	protected String getAppURL() {
		return "http://localhost:" + serverLauncher.getPort() + "/" + serverLauncher.getContext() + "/";
	}


}
