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

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.customers.center.struts.action.OfficeHierarchyDto;
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

    public void testShouldReturnOfficeHierarchyList() throws ServiceException, JsonGenerationException, JsonMappingException, IOException {
        OfficeHierarchyDto officeHierarchy = officeFacade.headOfficeHierarchy();
        assertNotNull(officeHierarchy);
        assertEquals("Mifos HO ",officeHierarchy.getText());
        assertEquals("1",officeHierarchy.getId());
        assertNotNull(officeHierarchy.getChildren());
        assertEquals(1,officeHierarchy.getChildren().size());
        OfficeHierarchyDto areaOfficeHierarchy = officeHierarchy.getChildren().get(0);
        assertNotNull(areaOfficeHierarchy);
        assertEquals("2",areaOfficeHierarchy.getId());
        assertEquals("TestAreaOffice",areaOfficeHierarchy.getText());
        assertNotNull(areaOfficeHierarchy.getChildren());
        assertEquals(1,areaOfficeHierarchy.getChildren().size());
        OfficeHierarchyDto branchOficeHierarchy = areaOfficeHierarchy.getChildren().get(0);
        assertNotNull(branchOficeHierarchy);
        assertEquals("3",branchOficeHierarchy.getId());
        assertEquals("TestBranchOffice",branchOficeHierarchy.getText());
        assertNotNull(branchOficeHierarchy.getChildren());
        assertEquals(0,branchOficeHierarchy.getChildren().size());
    }

    public void testShouldReturnTopLevelOfficeNames() {
        String officeNames = officeFacade.topLevelOfficeNames("3,2,1");
        assertEquals("Mifos HO ", officeNames);
        officeNames = officeFacade.topLevelOfficeNames("1");
        assertEquals("Mifos HO ", officeNames);
        officeNames = officeFacade.topLevelOfficeNames("2,3");
        assertEquals("TestAreaOffice", officeNames);
        officeNames = officeFacade.topLevelOfficeNames("3");
        assertEquals("TestBranchOffice", officeNames);
    }

}
