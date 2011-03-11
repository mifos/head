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
import org.mifos.test.acceptance.framework.admin.DefineLabelsParameters;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.mifos.test.acceptance.framework.testhelpers.UserHelper;
import org.mifos.test.acceptance.framework.user.EditUserDataPage;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"admin","acceptance","ui", "no_db_unit"})
public class DefineLabelsTest  extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;
    private UserHelper userHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
        userHelper = new UserHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        restoreDefaultENLabels();
        (new MifosPage(selenium)).logout();
    }

    private void restoreDefaultENLabels() {
        DefineLabelsParameters defineLabelsParams = new DefineLabelsParameters();
        defineLabelsParams.setLabel(DefineLabelsParameters.CITIZENSHIP, "Citizenship");
        defineLabelsParams.setLabel(DefineLabelsParameters.GOVERNMENT_ID, "Government ID");

        adminTestHelper.defineLabels(defineLabelsParams);
    }

    /**
     * Verify that new labels can be defined by Admin
     * http://mifosforge.jira.com/browse/MIFOSTEST-631
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void defineLabelsTest() throws Exception {
        String chineese = "地址";
        String arabic = "عربية";
        DefineLabelsParameters defineLabelsParams = new DefineLabelsParameters();
        defineLabelsParams.setLabel(DefineLabelsParameters.STATE, new String(arabic.getBytes(), "UTF-8"));
        defineLabelsParams.setLabel(DefineLabelsParameters.ADDRESS1, new String(chineese.getBytes(), "UTF-8"));

        adminTestHelper.defineLabels(defineLabelsParams);

        adminTestHelper.verifyLabels(defineLabelsParams);
        EditUserDataPage editUserDataPage = userHelper.navigateToEditUserDataPage("mifos");
        editUserDataPage.verifyLabels(defineLabelsParams);
    }
}