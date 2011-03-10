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


public class ViewLatenessAndDormancyDefinitionPage extends MifosPage {
    public ViewLatenessAndDormancyDefinitionPage(Selenium selenium) {
        super(selenium);
        verifyPage("view_lateness_and_dormancy_definition");
    }

    public AdminPage submitAndNavigateToAdminPage(String lateness, String dormancy){
        fillFromAndSubmit(lateness,dormancy);
        return new AdminPage(selenium);
    }

    public ViewLatenessAndDormancyDefinitionPage submitWithInvalidData(String lateness, String dormancy){
        fillFromAndSubmit(lateness,dormancy);
        return new ViewLatenessAndDormancyDefinitionPage(selenium);
    }

    public void verifyLatenessAndDormancy(String lateness, String dormancy){
        Assert.assertEquals(selenium.getValue("lateness"),lateness);
        Assert.assertEquals(selenium.getValue("dormancy"),dormancy);
    }

    public void verifyIsLatenessErrorDisplayed(boolean flag){
        Assert.assertEquals(selenium.getText("class=error").contains("Please specify valid lateness days."), flag);
    }

    public void verifyIsDormancyErrorDisplayed(boolean flag){
        Assert.assertEquals(selenium.getText("class=error").contains("Please specify valid dormancy days."), flag);
    }

    private void fillFromAndSubmit(String lateness, String dormancy){
        selenium.typeKeys("lateness", lateness);
        selenium.typeKeys("dormancy", dormancy);
        selenium.click("name=submit");
        waitForPageToLoad();
    }
}


