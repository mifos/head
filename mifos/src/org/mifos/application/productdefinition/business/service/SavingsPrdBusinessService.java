/**

 * SavingsPrdBusinessService.java    version: 1.0

 

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
package org.mifos.application.productdefinition.business.service;

import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.persistence.service.SavingsPrdPersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class SavingsPrdBusinessService extends BusinessService{
	private SavingsPrdPersistenceService dbService;
	
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public SavingsOfferingBO getSavingsProduct(Short prdOfferingId)throws ServiceException,PersistenceException {
		return getDBService().getSavingsProduct(prdOfferingId);
    }
	
	private SavingsPrdPersistenceService getDBService()throws ServiceException{
		if(dbService==null){
			dbService=(SavingsPrdPersistenceService) ServiceFactory.getInstance().getPersistenceService(
					PersistenceServiceName.SavingsProduct);
		}
		return dbService;
	}
}
