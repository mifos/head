/**

 * SavingsPersistenceService.java    version: 1.0

 

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
package org.mifos.application.accounts.savings.persistence.service;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.savings.persistence.SavingsPersistence;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.service.PersistenceService;

public class SavingsPersistenceService extends PersistenceService {

	private SavingsPersistence serviceImpl = new SavingsPersistence();

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public List<PrdOfferingView> getSavingsProducts(OfficeBO office,
			CustomerLevelEntity customerLevel, short savingsType)
			throws PersistenceException {
		logger.debug("In SavingsPersistenceService::getSavingsProducts()");
		return serviceImpl.getSavingsProducts(office, customerLevel,
				savingsType);
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			Short entityType) throws PersistenceException {
		logger
				.debug("In SavingsPersistenceService::retrieveCustomFieldsDefinition()");
		return serviceImpl.retrieveCustomFieldsDefinition(entityType);
	}

	public void save(SavingsBO savings)throws PersistenceException {
		try{
			logger.debug("In SavingsPersistenceService::save()");
			serviceImpl.createOrUpdate(savings);
			logger
					.info("In SavingsPersistenceService::save(), successfully created");
		}catch(HibernateException he){
			logger.error("Savings account creation failed: "+he.getMessage());
			new PersistenceException(SavingsConstants.CREATE_FAILED,he);
		}
	}

	public SavingsBO findById(Integer accountId) throws PersistenceException {
		logger.debug("In SavingsPersistenceService::findById(), accountId: "
				+ accountId);
		return serviceImpl.findById(accountId);
	}

	public SavingsBO findBySystemId(String globalAccountNumber)
			throws PersistenceException {
		logger.debug("In SavingsPersistenceService::findBySystemId(), globalAccountNumber: "
						+ globalAccountNumber);
		return serviceImpl.findBySystemId(globalAccountNumber);
	}

	public void update(SavingsBO savings) throws PersistenceException{
		try{
			logger.debug("In SavingsPersistenceService::update()");
			serviceImpl.createOrUpdate(savings);
			logger.info("In SavingsPersistenceService::update(), successfully updated");
		}
		catch(HibernateException he){
			logger.error("update on savings account with accountId: "+ savings.getAccountId()+ " failed. ::" +he.getMessage());
			new PersistenceException(SavingsConstants.UPDATE_FAILED,he);
		}
	}

	public List<SavingsAccountView> getSavingsAccountsForCustomer(
			Integer customerId) {
		return serviceImpl.getSavingsAccountsForCustomer(customerId);
	}

	public List<AccountActionDateEntity> getTransactionDetailForSavingsAccount(
			Integer accountId, Integer customerId, Date transactionDate,boolean isMandatory) {
		return serviceImpl.getSavingsAccountTransactionDetail(accountId,
				customerId, transactionDate,isMandatory);
	}
	
	public SavingsTrxnDetailEntity retrieveLastTransaction(Integer accountId, Date date)throws PersistenceException{
		return serviceImpl.retrieveLastTransaction(accountId,date);
	}
	
	public SavingsTrxnDetailEntity retrieveFirstTransaction(Integer accountId)throws PersistenceException{
		return serviceImpl.retrieveFirstTransaction(accountId);
	}
	
	public AccountStateEntity getAccountStatusObject(Short accountStatusId) throws PersistenceException {
		return serviceImpl.getAccountStatusObject(accountStatusId);
	}
	
	public List<SavingsBO> getAllClosedAccounts(Integer customerId) throws PersistenceException {
		return serviceImpl.getAllClosedAccount(customerId);
	}

	public int getMissedDeposits(Integer accountId , Date currentDate ) throws PersistenceException {
		return serviceImpl.getMissedDeposits(accountId ,currentDate);
		
	}

	public int getMissedDepositsPaidAfterDueDate(Integer accountId) throws PersistenceException {
		return serviceImpl.getMissedDepositsPaidAfterDueDate(accountId );
	}

}
