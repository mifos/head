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

package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ViewLatenessAndDormancyDefinitionPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"acceptance","ui", "no_db_unit"})

public class ViewLatenessAndDormancyDefinitionTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    @Test(enabled=true)
    //http://mifosforge.jira.com/browse/MIFOSTEST-705
    public void verifyViewLatenessAndDormancyDefinitionPage() {
        //When
        ViewLatenessAndDormancyDefinitionPage viewLatenessAndDormancyDefinitionPage =
            navigationHelper.navigateToAdminPage().navigateToViewLatenessAndDormancyDefinitionPage();
        viewLatenessAndDormancyDefinitionPage.submitWithInvalidData("aa", "bb");
        //Then
        viewLatenessAndDormancyDefinitionPage.verifyIsDormancyErrorDisplayed(true);
        viewLatenessAndDormancyDefinitionPage.verifyIsLatenessErrorDisplayed(true);
        //When
        AdminPage adminPage = viewLatenessAndDormancyDefinitionPage.submitAndNavigateToAdminPage("20", "10");
        viewLatenessAndDormancyDefinitionPage = adminPage.navigateToViewLatenessAndDormancyDefinitionPage();
        //Then
        viewLatenessAndDormancyDefinitionPage.verifyLatenessAndDormancy("20", "10");
        //When
        adminPage = viewLatenessAndDormancyDefinitionPage.submitAndNavigateToAdminPage("10", "30");

        // MIFOS-4774
        adminPage.
                navigateToViewLatenessAndDormancyDefinitionPage().
                submitAndNavigateToAdminPage("1234", "4321").
                navigateToViewLatenessAndDormancyDefinitionPage().
                submitAndNavigateToAdminPage();
    }
}

