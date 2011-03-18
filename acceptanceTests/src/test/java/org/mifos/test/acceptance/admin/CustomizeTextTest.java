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

import org.junit.Assert;
import static org.hamcrest.CoreMatchers.is;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.CustomizeTextViewPage;
import org.mifos.test.acceptance.framework.admin.DefineLabelsParameters;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.EditUserDataPage;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(singleThreaded = true, groups = {"admin","acceptance","ui", "no_db_unit"})
public class CustomizeTextTest  extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;
    private NavigationHelper navigationHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
        this.navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }


    @Test
    public void addAndRemoveCustomizedText() {
    	String originalText = "Client";
    	String customText = "Person";
    	CustomizeTextViewPage customizeTextViewPage = 
    		adminTestHelper.addCustomizedText(originalText, customText);
    	
    	// verify that it shows up
    	customizeTextViewPage.verifyCustomTextIsPresent(originalText, customText);
    	
    	// remove the custom text
    	customizeTextViewPage = customizeTextViewPage.removeCustomizedText(originalText);
    	
    	// verify that custom text is gone
    	Assert.assertThat(customizeTextViewPage.getCustomizedTextCount(), is(0));    	
    }
}