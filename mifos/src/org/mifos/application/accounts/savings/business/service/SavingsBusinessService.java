/**

 * SavingsBusinessService.java    version: 1.0

 

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
package org.mifos.application.accounts.savings.business.service;

import java.util.List;

import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class SavingsBusinessService extends BusinessService{
	private SavingsPersistenceService dbService;
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
	
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new SavingsBO(userContext);
	}

	public List<PrdOfferingView> getSavingProducts(OfficeBO branch, CustomerLevelEntity customerLevel, short accountType)throws ServiceException,PersistenceException {
		logger.debug("In SavingsBusinessService::getSavingProducts()");
		return getDBService().getSavingsProducts(branch,customerLevel,accountType);
    }
	
	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition()throws ServiceException,PersistenceException{
		logger.debug("In SavingsBusinessService::retrieveCustomFieldsDefinition()");
		return getDBService().retrieveCustomFieldsDefinition(SavingsConstants.SAVINGS_CUSTOM_FIELD_ENTITY_TYPE);
	}
	
	public SavingsBO findById(Integer accountId)throws ServiceException,PersistenceException{
		logger.debug("In SavingsBusinessService::findById(), accountId: "+ accountId);
		return getDBService().findById(accountId);
	}
	
	public SavingsBO findBySystemId(String globalAccountNumber)throws ServiceException,PersistenceException{
		logger.debug("In SavingsBusinessService::findBySystemId(), globalAccountNumber: "+ globalAccountNumber);
		return getDBService().findBySystemId(globalAccountNumber);
	}
	
	private SavingsPersistenceService getDBService()throws ServiceException{
		if(dbService==null){
			dbService=(SavingsPersistenceService) ServiceFactory.getInstance().getPersistenceService(
				PersistenceServiceName.Savings);
		}
		return dbService;
	}
	
	public List<SavingsBO> getAllClosedAccounts(Integer customerId) throws ServiceException, PersistenceException {
		return getDBService().getAllClosedAccounts(customerId);
	}
	
}
