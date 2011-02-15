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

package org.mifos.security.util;

import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.springframework.beans.factory.annotation.Autowired;

public class SecurityHelperIntegrationTest extends MifosIntegrationTestCase {

    @Autowired
    private LegacyRolesPermissionsDao legacyRolesPermissionsDao;

    private OfficePersistence officePersistence = new OfficePersistence();

    @SuppressWarnings("unchecked")
    @Test
    public void testGetUserRoles() throws Exception {
        Set<RoleBO> userRoles = legacyRolesPermissionsDao.getUserRoles((short) 1);
       Assert.assertEquals(1, userRoles.size());
    }

    @Test
    public void testGetPersonnelOffices() throws Exception {
        List<OfficeSearch> officeSearchList = officePersistence.getPersonnelOffices(Short.valueOf("1"));
       Assert.assertEquals(3, officeSearchList.size());
    }

    @Test
    public void testGetOffices() throws Exception {
        List<OfficeSearch> officeSearchList = officePersistence.getOffices();
       Assert.assertEquals(3, officeSearchList.size());
    }

}
