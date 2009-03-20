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

public class AppInfoNoSecurityPage extends AbstractPage {

	public AppInfoNoSecurityPage() {
		super();
	}
	
	public AppInfoNoSecurityPage(Selenium selenium) {
		super(selenium);
	}
	
    public AppInfoNoSecurityPage navigateToAppInfoNoSecurityPage() {
        selenium.open("appInfoNoSecurity.ftl");
        return new AppInfoNoSecurityPage(selenium);
    }
	
	public AppInfoNoSecurityPage verifyPage() {
		Assert.assertEquals(selenium.getTitle(), "System Information");
        Assert.assertEquals(selenium.getText("id=appInfo.svn.revision.message"), "SVN revision number");
        Assert.assertTrue(selenium.getText("id=appInfo.svn.revision").length() > 0, "SVN revision number should not be blank.");
        Assert.assertEquals(selenium.getText("id=appInfo.build.tag.message"), "Build tag");
        Assert.assertTrue(selenium.getText("id=appInfo.build.tag").length() > 0, "Build tag should not be blank.");
        Assert.assertEquals(selenium.getText("id=appInfo.build.id.message"), "Build ID");
        Assert.assertTrue(selenium.getText("id=appInfo.build.id").length() > 0, "Build id should not be blank.");
		return this;
	}

}
