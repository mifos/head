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

package org.mifos.framework.components.fieldConfiguration.util.helpers;

import junit.framework.Assert;

import org.mifos.application.master.MessageLookup;
import org.mifos.config.util.helpers.ConfigurationConstants;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.UserContext;

public class FieldConfigurationHelperIntegrationTest extends MifosIntegrationTestCase {

    public FieldConfigurationHelperIntegrationTest() throws Exception {
        super();
    }

    private UserContext userContext = TestUtils.makeUser();

    @Override
    protected void setUp() throws Exception {
        MessageLookup.getInstance().setCustomLabel(ConfigurationConstants.ADDRESS3, "Village", userContext);
    }

    @Override
    protected void tearDown() throws Exception {
        MessageLookup.getInstance().setCustomLabel(ConfigurationConstants.ADDRESS3, "NULL", userContext);
    }

    public void testGetConfiguredFieldName() throws Exception {
        String fieldName = FieldConfigurationHelper.getConfiguredFieldName("Center.Address3", TestUtils
                .makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
       Assert.assertEquals("Village", fieldName);
        fieldName = FieldConfigurationHelper.getConfiguredFieldName("Center.SomeField", TestUtils
                .makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
       Assert.assertEquals(null, fieldName);
    }

    public void testGetLocalSpecificFieldNames() throws Exception {
        String fieldName = FieldConfigurationHelper.getLocalSpecificFieldNames("Center.Address3", TestUtils
                .makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
       Assert.assertEquals("Village", fieldName);

        fieldName = FieldConfigurationHelper.getLocalSpecificFieldNames("Center.SomeField", TestUtils
                .makeUser(RolesAndPermissionConstants.ADMIN_ROLE));
       Assert.assertEquals("Center.SomeField", fieldName);
    }

}
