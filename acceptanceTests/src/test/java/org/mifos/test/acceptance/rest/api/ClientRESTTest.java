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

package org.mifos.test.acceptance.rest.api;

import static org.mifos.test.acceptance.rest.api.RESTAPITestHelper.Type;
import static org.mifos.test.acceptance.rest.api.RESTAPITestHelper.By;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(enabled=false, singleThreaded = true, groups = { "rest", "acceptance", "no_db_unit" })
@SuppressWarnings("PMD.SignatureDeclareThrowsException")
public class ClientRESTTest extends UiTestCaseBase {

    RESTAPITestHelper helper;

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @BeforeClass
    public void setUp() throws Exception {
        super.setUp();
        helper = new RESTAPITestHelper(selenium);
        helper.navigateToJsonAjaxPage();
    }

    @AfterClass
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void clientByGlobalNumJSON() throws Exception {
        String actualJSON = helper.getJSONFromUI(Type.CLIENT, By.GLOBAL_NUMBER, "0002-000000014");
        String expectedJSON = helper.getJSONFromDataSet(Type.CLIENT, By.GLOBAL_NUMBER, "0002-000000014");
        helper.assertEquals(expectedJSON, actualJSON);
    }
}
