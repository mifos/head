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
package org.mifos.customers.office.business.service;

import java.util.List;

import org.mifos.customers.office.persistence.OfficeDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;

public class OfficeFacadeIntegrationTest extends MifosIntegrationTestCase {

    OfficeFacade officeFacade;

    public OfficeFacadeIntegrationTest() throws Exception {
        super();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        officeFacade = new OfficeFacade(new OfficeBusinessService());
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testShouldReturnOfficeHierarchyList() throws ServiceException {
        List<OfficeDto> officeHierarchy = officeFacade.depthFirstHeadOfficeHierarchy();
        assertNotNull(officeHierarchy);
        assertEquals(3, officeHierarchy.size());
        assertEquals("Mifos HO", officeHierarchy.get(0).getName());
        assertEquals(new Short((short) 1), officeHierarchy.get(0).getId());
        assertEquals("TestAreaOffice", officeHierarchy.get(1).getName());
        assertEquals(new Short((short) 2), officeHierarchy.get(1).getId());
        assertEquals("TestBranchOffice", officeHierarchy.get(2).getName());
        assertEquals(new Short((short) 3), officeHierarchy.get(2).getId());
    }
}
