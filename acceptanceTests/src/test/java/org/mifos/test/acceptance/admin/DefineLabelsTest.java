/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
import org.mifos.test.acceptance.framework.admin.DefineLabelsPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.StringUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"admin","acceptance","ui"})
public class DefineLabelsTest  extends UiTestCaseBase {

    private NavigationHelper navigationHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        navigationHelper = new NavigationHelper(selenium);
    }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * FIXME - keithw - ignoring for now.
     */
    @Test(enabled=false)
    public void defineLabelsTest() {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        DefineLabelsPage defineLabelsPage = adminPage.navigateToDefineLabelsPage();

        String citizenshipVal = "citizenship.testval." + StringUtil.getRandomString(6);
        String govtIdVal = "govtId.testval." + StringUtil.getRandomString(6);

        defineLabelsPage.setLabelValue( "citizenship", citizenshipVal );
        defineLabelsPage.setLabelValue( "govtId", govtIdVal );

        adminPage = defineLabelsPage.submit();
        defineLabelsPage = adminPage.navigateToDefineLabelsPage();

        defineLabelsPage.verifyLabelValue( "citizenship", citizenshipVal );
        defineLabelsPage.verifyLabelValue( "govtId", govtIdVal );

        //Restore to previous values (presumes EN)
        defineLabelsPage.setLabelValue( "citizenship", "Citizenship" );
        defineLabelsPage.setLabelValue( "govtId", "Government ID" );
        adminPage = defineLabelsPage.submit();
    }
}