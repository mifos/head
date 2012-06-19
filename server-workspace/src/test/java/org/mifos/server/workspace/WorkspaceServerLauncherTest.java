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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 * Tests launching of Mifos in the Workspace through simple embedded Servlet Container Web Server. All the dependencies
 * / web modules are simply on the (Maven provided) classpath in this test.
 */
@Ignore
public class WorkspaceServerLauncherTest {

    private static WorkspaceServerLauncher serverLauncher;

    private static final String UID = "login.input.username";
    private static final String PWD = "login.input.password";
    private static final String BTN = "login.button.login";

    @BeforeClass
    public static void beforeClass() throws Exception {
        serverLauncher = new WorkspaceServerLauncher(8088, "mifos");
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
    public void testLogin() throws Exception {
        WebDriver wd = new FirefoxDriver();
        wd.get(getAppURL());

        wd.findElement(By.id(UID)).sendKeys("mifos");
        wd.findElement(By.id(PWD)).sendKeys("testmifos");
        wd.findElement(By.id(BTN)).click();

        Assert.assertTrue(wd.getPageSource().contains("Mifos"));
        Assert.assertTrue(wd.getPageSource().contains("Home"));
        Assert.assertTrue(wd.getPageSource().contains("Search"));

        wd.quit();
    }

    /**
     * Application base URL.
     *
     * @return String of App's URL, including a trailing slash after the context.
     */
    protected String getAppURL() {
        return "http://localhost:" + serverLauncher.getPort() + "/" + serverLauncher.getContext() + "/";
    }

}
