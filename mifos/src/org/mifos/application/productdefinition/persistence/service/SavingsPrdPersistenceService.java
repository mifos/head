/**

 * SavingsPrdPersistenceService.java    version: 1.0

 

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
package org.mifos.application.productdefinition.persistence.service;

import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.persistence.SavingsPrdPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.service.PersistenceService;

public class SavingsPrdPersistenceService extends PersistenceService{
	
	private SavingsPrdPersistence serviceImpl = new SavingsPrdPersistence();
	
	public SavingsOfferingBO getSavingsProduct(Short prdOfferingId)throws PersistenceException {
		return serviceImpl.getSavingsProduct(prdOfferingId);
    }	
	
	public List<SavingsBO> retrieveSavingsAccountsForPrd(Short prdOfferingId)throws PersistenceException{
		return serviceImpl.retrieveSavingsAccountsForPrd(prdOfferingId);
	}
	
	public Short retrieveDormancyDays() throws PersistenceException{
		return serviceImpl.retrieveDormancyDays();
	}
}
