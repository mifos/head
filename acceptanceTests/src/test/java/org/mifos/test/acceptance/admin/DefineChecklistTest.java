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
import org.mifos.test.acceptance.framework.admin.ChecklistDetailsPage;
import org.mifos.test.acceptance.framework.admin.DefineChecklistParameters;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"admin","acceptance","ui"})
public class DefineChecklistTest extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
    }

    @AfterMethod(alwaysRun = true)
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    /**
     * Define and Edit checklist
     * http://mifosforge.jira.com/browse/MIFOSTEST-1096
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyCreatingAndEditingChecklist() throws Exception {
        DefineChecklistParameters checklistParams = new DefineChecklistParameters();
        checklistParams.setName("newCenterChecklist");
        checklistParams.setType(DefineChecklistParameters.TYPE_CENTER);
        checklistParams.setDisplayedWhenMovingIntoStatus(DefineChecklistParameters.STATUS_ACTIVE);
        checklistParams.addItem("center item 1");
        checklistParams.addItem("center item 2");
        DefineChecklistParameters editParams = new DefineChecklistParameters();
        editParams.setName("editedCenterChecklist");

        adminTestHelper.defineNewChecklist(checklistParams);
        adminTestHelper.editChecklist(checklistParams.getName(), editParams);
        ChecklistDetailsPage checklistDetailsPage = adminTestHelper.navigateToChecklistDetailsPage(editParams.getName());

        checklistDetailsPage.verifyName(editParams.getName());
    }
}
