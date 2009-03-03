/**

 * TestAdminDocumentPersistence.java    version: 1.0

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied.  

 *

 */

package org.mifos.application.admindocuments.persistence;

import java.util.List;

import org.mifos.application.admindocuments.business.AdminDocumentBO;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;


public class TestAdminDocumentPersistence extends MifosIntegrationTest {

	public TestAdminDocumentPersistence() throws SystemException, ApplicationException {
        super();
    }


    private AdminDocumentPersistence reportsPersistence;

	@Override
	protected void setUp() throws Exception {
		reportsPersistence = new AdminDocumentPersistence();
	}

	
	public void testGetAllAdminDocuments() throws PersistenceException {
		List<AdminDocumentBO>  listadmindoc = reportsPersistence
		.getAllAdminDocuments();
		assertEquals(0, listadmindoc.size());
	}
	
	
	public void testGetAdminDocumentById() throws NumberFormatException, PersistenceException {
		AdminDocumentBO  admindoc = reportsPersistence
		.getAdminDocumentById(Short.valueOf("1"));
		assertEquals(null, admindoc);
	}

}





