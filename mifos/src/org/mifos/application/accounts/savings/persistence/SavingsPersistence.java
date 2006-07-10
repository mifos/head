/**

 * SavingsPersistence.java    version: 1.0

 

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
package org.mifos.application.accounts.savings.persistence;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.Money;

public class SavingsPersistence extends Persistence {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public List<PrdOfferingView> getSavingsProducts(OfficeBO branch,
			CustomerLevelEntity customerLevel, short savingsTypeId)
			throws PersistenceException {
		try {
			logger
					.debug("In SavingsPersistence::getSavingsProducts(), customerLevelId: "
							+ customerLevel.getLevelId());
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(AccountConstants.PRDTYPEID,
					ProductDefinitionConstants.SAVINGSID);
			queryParameters.put(AccountConstants.PRDSTATUS,
					ProductDefinitionConstants.SAVINGSACTIVE);
			queryParameters.put(AccountConstants.PRODUCT_APPLICABLE_TO,
					customerLevel.getProductApplicableType());
			return (List<PrdOfferingView>) executeNamedQuery(
					NamedQueryConstants.GET_APPLICABLE_SAVINGS_PRODUCT_OFFERINGS,
					queryParameters);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			Short entityType) throws PersistenceException {
		try {
			logger
					.debug("In SavingsPersistence::retrieveCustomFieldsDefinition(), entityType: "
							+ entityType);
			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(AccountConstants.ENTITY_TYPE, entityType);
			return (List<CustomFieldDefinitionEntity>) executeNamedQuery(
					NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public SavingsBO findById(Integer accountId) throws PersistenceException {
		SavingsBO savings;
		try {
			logger.debug("In SavingsPersistence::findById(), accountId: "
					+ accountId);
			Session session = HibernateUtil.getSessionTL();
			savings = (SavingsBO) session.get(SavingsBO.class, accountId);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return savings;
	}

	public SavingsBO findBySystemId(String globalAccountNumber)
			throws PersistenceException {
		SavingsBO savings = null;
		try {
			logger.debug("In SavingsPersistence::findBySystemId(), globalAccountNumber: "
							+ globalAccountNumber);

			Map<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put(AccountConstants.GLOBAL_ACCOUNT_NUMBER,
					globalAccountNumber);
			List queryResult = executeNamedQuery(
					NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID,
					queryParameters);
			savings = (SavingsBO) queryResult.get(0);
			if (savings != null && savings.getRecommendedAmount() == null)
				savings.setRecommendedAmount(new Money());
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return savings;
	}

	public List<SavingsAccountView> getSavingsAccountsForCustomer(
			Integer customerId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CUSTOMER_ID", customerId);
		List<SavingsAccountView> queryResult = executeNamedQuery(
				NamedQueryConstants.BULKENTRYSAVINGSACCOUNTS, queryParameters);
		return queryResult;
	}

	public List<AccountActionDateEntity> getSavingsAccountTransactionDetail(
			Integer accountId, Integer customerId, Date transactionDate,boolean isMandatory) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		queryParameters.put("CUSTOMER_ID", customerId);
		queryParameters.put("ACTION_DATE", transactionDate);
		queryParameters.put("PAYMENT_STATUS", AccountConstants.PAYMENT_UNPAID);
		List<AccountActionDateEntity> queryResult = null; 
		if(isMandatory) {
			queryResult = executeNamedQuery(NamedQueryConstants.GET_LISTOFACCOUNTSACTIONS_FOR_SAVINGS_MANDATORY,queryParameters);
		} else {
			queryResult = executeNamedQuery(NamedQueryConstants.GET_LISTOFACCOUNTSACTIONS_FOR_SAVINGS_VOLUNTORY,queryParameters);
		}
		return queryResult;
	}
	
	public SavingsTrxnDetailEntity retrieveLastTransaction(Integer accountId, Date date)throws PersistenceException{
		try{
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("accountId", accountId);
			queryParameters.put("date", date);
			List<SavingsTrxnDetailEntity> queryResult = executeNamedQuery(NamedQueryConstants.RETRIEVE_LAST_TRXN, queryParameters);
			if(queryResult.size()>0)
				return queryResult.get(0);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return null;
	}
	
	public SavingsTrxnDetailEntity retrieveFirstTransaction(Integer accountId)throws PersistenceException{
		try{
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("accountId", accountId);
			List<SavingsTrxnDetailEntity> queryResult = executeNamedQuery(NamedQueryConstants.RETRIEVE_FIRST_TRXN, queryParameters);
			if(queryResult.size()>0)
				return queryResult.get(0);
		}catch(HibernateException he){
			throw new PersistenceException(he);
		}
		return null;
	}
	
	public List<AccountStateEntity> retrieveAllAccountStateList(Short prdTypeId)
		throws PersistenceException {
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("prdTypeId", prdTypeId);
			List<AccountStateEntity> queryResult = executeNamedQuery(
					NamedQueryConstants.RETRIEVEALLACCOUNTSTATES,
					queryParameters);
			return queryResult;
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	
	}
	
	public AccountStateEntity getAccountStatusObject(Short accountStatusId) throws PersistenceException {
		AccountStateEntity accountStateEntity;
		try {
			logger.debug("In SavingsPersistence::getAccountStatusObject(), accountStatusId: "+ accountStatusId);
			Session session = HibernateUtil.getSessionTL();
			accountStateEntity = (AccountStateEntity) session.get(AccountStateEntity.class, accountStatusId);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return accountStateEntity;
	}
	
	public List<CheckListMaster> getStatusChecklist(Short accountStatusId, Short accountTypeId) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountTypeId", accountTypeId);
		queryParameters.put("accountStatus", accountStatusId);
		queryParameters.put("checklistStatus", 1);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.STATUSCHECKLIST, queryParameters);
		return queryResult;
		}
	
	public List<SavingsBO> getAllClosedAccount(Integer customerId) throws PersistenceException{
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("customerId", customerId);
			List queryResult = executeNamedQuery(NamedQueryConstants.VIEWALLSAVINGSCLOSEDACCOUNTS, queryParameters);
			return queryResult;
		}catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}
	
	public AccountStateFlagEntity getAccountStateFlag(Short flagId) throws PersistenceException {
		AccountStateFlagEntity accountStateFlagEntity;
		try {
			logger.debug("In SavingsPersistence::getAccountStateFlag(), flagId: "+ flagId);
			Session session = HibernateUtil.getSessionTL();
			accountStateFlagEntity = (AccountStateFlagEntity) session.get(AccountStateFlagEntity.class, flagId);
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
		return accountStateFlagEntity;
	}
	
	public List<Integer>retreiveAccountsPendingForIntCalc(Date currentDate)throws PersistenceException {
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("currentDate", currentDate);
			List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_INT_CALC, queryParameters);
			return queryResult;
		}catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}
	
	public List<Integer>retreiveAccountsPendingForIntPosting(Date currentDate)throws PersistenceException {
		try {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("currentDate", currentDate);
			List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_INT_POST, queryParameters);
			return queryResult;
		}catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}

	public int getMissedDeposits( Date currentDate) throws PersistenceException {
	try 	
	{		
			Integer count =0;
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			//queryParameters.put("accountId", accountId);
			queryParameters.put("ACCOUNT_TYPE_ID", AccountTypes.SAVINGSACCOUNT );
			queryParameters.put("ACTIVE", AccountStates.SAVINGS_ACC_APPROVED );
			queryParameters.put("CHECKDATE", currentDate);
			queryParameters.put("PAYMENTSTATUS", AccountConstants.PAYMENT_UNPAID);
			
			List queryResult=executeNamedQuery(NamedQueryConstants.GET_MISSED_DEPOSITS_COUNT,queryParameters);
			
			if(null!=queryResult && queryResult.size()>0){
				Object obj = queryResult.get(0);
				if(obj!=null)
					count = (Integer)obj;
			}		
			
			return count.intValue();
			
		}catch (HibernateException he) {
			throw new PersistenceException(he);
		}
	}
}
