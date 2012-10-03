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

package org.mifos.server.workspace;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Test of Google Homepage.
 * 
 * Mifos-unrelated test of the Selenium / WebDriver framework.
 * 
 * @author Michael Vorburger
 */
@Ignore
public class SeleniumTest {

	@Test
	public void testSearchMifosOnGoogle() throws Exception {
		WebDriver wd = new FirefoxDriver();
		try {
			wd.get("http://www.google.com");
			WebElement element = wd.findElement(By.name("q"));
			element.sendKeys("Mifos");
			element.submit();
		} finally {
			wd.quit();
		}
	}

}
