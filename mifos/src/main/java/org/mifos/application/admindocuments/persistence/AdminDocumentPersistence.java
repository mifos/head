/**

 * AdminDocumentPersistence.java    version: 1.0



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

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.admindocuments.business.AdminDocumentBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;



public class AdminDocumentPersistence extends Persistence {

	public AdminDocumentPersistence() {
	}

	public AdminDocumentBO getAdminDocumentById(Short adminDocumentId)
			throws PersistenceException {
		return (AdminDocumentBO) getPersistentObject(AdminDocumentBO.class,
				adminDocumentId);
	}

	public List<AdminDocumentBO> getAllAdminDocuments()
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		return executeNamedQuery(
				NamedQueryConstants.GET_ALL_ACTIVE_ADMINISTRATIVE_DOCUMENT,
				queryParameters);

	}
	
}