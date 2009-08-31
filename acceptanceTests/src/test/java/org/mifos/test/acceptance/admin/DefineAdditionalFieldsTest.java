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
 
package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.DefineAdditionalFieldPreviewPage;
import org.mifos.test.acceptance.framework.admin.DefineAdditionalFieldsPage;
import org.mifos.test.acceptance.framework.admin.ViewAdditionalFieldCategoriesPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"admin","acceptance","ui"})
public class DefineAdditionalFieldsTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    public void defineClientAdditionalTextFieldTest() {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineAdditionalFieldsPage defineAdditionalFieldsPage = adminPage.navigateToDefineAdditionalFieldsPage();
        defineAdditionalFieldsPage.verifyPage();
        String category = "Client";
        String label =  "clientLabel " + StringUtil.getRandomString(12);
        String dataType =  "Text";
        
        createAndVerifyNewAdditonalField(adminPage, defineAdditionalFieldsPage, category, label, dataType);
    }

    private void createAndVerifyNewAdditonalField(AdminPage adminPage,
            DefineAdditionalFieldsPage defineAdditionalFieldsPage, String category, String label, String dataType) {
        DefineAdditionalFieldPreviewPage defineAdditionalFieldPreviewPage = defineAdditionalFieldsPage.defineAdditionalField(adminPage, category, label, dataType);
        defineAdditionalFieldPreviewPage.verifyPage();
        defineAdditionalFieldPreviewPage.submit();
        ViewAdditionalFieldCategoriesPage viewAdditionalFieldCategoriesPage = adminPage.navigateToViewAdditionalFields();
        viewAdditionalFieldCategoriesPage.verifyPage();
        // TODO fix this navigation issue, it depends on the link being called "Client".
        //ViewAdditionalFieldsPage viewAdditionalFieldsPage = viewAdditionalFieldCategoriesPage.selectCategory(category);
        //viewAdditionalFieldsPage.verifyPage();
        //viewAdditionalFieldsPage.verifyFieldLabelIsDisplayed(label);
    }
}
