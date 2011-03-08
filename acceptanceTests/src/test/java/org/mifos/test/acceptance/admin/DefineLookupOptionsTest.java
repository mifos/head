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
import org.mifos.test.acceptance.framework.admin.DefineLookupOptionParameters;
import org.mifos.test.acceptance.framework.admin.DefineLookupOptionsPage;
import org.mifos.test.acceptance.framework.testhelpers.AdminTestHelper;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"admin", "acceptance", "ui", "no_db_unit"})
public class DefineLookupOptionsTest extends UiTestCaseBase {

    private AdminTestHelper adminTestHelper;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();
        adminTestHelper = new AdminTestHelper(selenium);
    }

    @AfterMethod
    public void logout() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void verifyAttributesSuccessfullyAdded() throws Exception {
        DefineLookupOptionParameters lookupOptionParams = new DefineLookupOptionParameters();
        lookupOptionParams.setType(DefineLookupOptionParameters.TYPE_SALUTATION);
        lookupOptionParams.setName("Shri");
        DefineLookupOptionsPage defineLookupOptionsPage = adminTestHelper.navigateToDefineLookupOptionsPage();
        defineLookupOptionsPage.verifyLookupOptions();
        adminTestHelper.defineNewLookupOption(lookupOptionParams);
        adminTestHelper.verifyDefinedLookupOptionOnCreateNewClientPage("MyOfficeDHMFT", lookupOptionParams);
    }
}
