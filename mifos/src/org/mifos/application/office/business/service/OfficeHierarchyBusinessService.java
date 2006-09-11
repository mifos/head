/**

 * OfficeHierarchyBusinessService.java    version: 1.0

 

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
package org.mifos.application.office.business.service;

import java.util.List;

import org.mifos.application.office.business.OfficeLevelEntity;
import org.mifos.application.office.persistence.OfficeHierarchyPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;

public class OfficeHierarchyBusinessService extends BusinessService {

	public OfficeHierarchyBusinessService() {
		super();
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public List<OfficeLevelEntity> getOfficeLevels(Short localeId) throws ServiceException {
		try {
			return new OfficeHierarchyPersistence().getOfficeLevels(localeId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}
