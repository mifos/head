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

package org.mifos.test.acceptance.framework.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class ManageRolePage  extends MifosPage {

    public ManageRolePage(Selenium selenium) {
        super(selenium);
    }

    public ManageRolePage verifyPage() {
        verifyPage("managerole");
        return this;
    }
    public ManageRolePage disablePermission(String permissionValue) {
        Assert.assertEquals(selenium.getValue(permissionValue), "on");
        selenium.click(permissionValue);
        return this;
    }

    public ManageRolePage enablePermission(String permissionValue) {
        Assert.assertEquals("off", selenium.getValue(permissionValue));
        selenium.click(permissionValue);
        return this;
    }


    public ViewRolesPage submitAndGotoViewRolesPage() {
        selenium.click("managerole.button.submit");
        waitForPageToLoad();
        return new ViewRolesPage(selenium);
    }
    
    public Boolean isPermissionEnable(String permisson) {
    	return selenium.getValue(permisson).equalsIgnoreCase("on");
    }

    public ManageRolePage verifyPermissionText(String permisson, String description) {
        Assert.assertEquals(2,selenium.getXpathCount("//input[@id='" + permisson + "']/parent::td/following-sibling::td/span[text()='" + description + "']"));
        return this;
    }
    
    public ManageRolePage verifyAmountTextField() {
        selenium.click("managerole.button.submit");
        selenium.waitForPageToLoad("30000");
        Assert.assertTrue(selenium.isTextPresent("Please specify correct Maximum loan amount"));
        return this;
    }
    
}
