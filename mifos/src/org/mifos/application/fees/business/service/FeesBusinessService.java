/**

 * FeesBusinessService.java    version: 1.0

 

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
package org.mifos.application.fees.business.service;

import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.persistence.service.FeePersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class FeesBusinessService extends BusinessService {

	private FeePersistenceService feePersistenceService;

	public FeesBusinessService() throws ServiceException {
		feePersistenceService = (FeePersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.Fees);
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new FeesBO(userContext);
	}
	
	public FeesBO getFees(Short feeId){
		return feePersistenceService.getFees(feeId);
	}
}
