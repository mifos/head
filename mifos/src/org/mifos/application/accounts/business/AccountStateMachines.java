/**

 * SavingsStateMachine.java    version: 1.0

 

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
package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.CustomerStatusFlagEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.business.StateEntity;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.stateMachineFactory.StateMachine;
import org.mifos.framework.components.stateMachineFactory.StateXMLParser;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;

public class AccountStateMachines implements StateMachine {
	AccountBusinessService accountBusinessService = new AccountBusinessService();
	CustomerBusinessService customerBusinessService = new CustomerBusinessService();

	private AccountStateMachines() {
		super();
	}

	private static AccountStateMachines statemachine = new AccountStateMachines();

	public static AccountStateMachines getInstance() {
		return statemachine;
	}

	private Map<StateEntity, List<StateEntity>> statesMapForLoan = new HashMap<StateEntity, List<StateEntity>>();

	private Map<Short, List<AccountStateEntity>> statesViewMapForLoan = new HashMap<Short, List<AccountStateEntity>>();

	private Map<StateEntity, List<StateEntity>> statesMapForSavings = new HashMap<StateEntity, List<StateEntity>>();

	private Map<Short, List<AccountStateEntity>> statesViewMapForSavings = new HashMap<Short, List<AccountStateEntity>>();

	private Map<StateEntity, List<StateEntity>> statesMapForCenter = new HashMap<StateEntity, List<StateEntity>>();

	private Map<Short, List<CustomerStatusEntity>> statesViewMapForCenter = new HashMap<Short, List<CustomerStatusEntity>>();

	private Map<StateEntity, List<StateEntity>> statesMapForClient = new HashMap<StateEntity, List<StateEntity>>();

	private Map<Short, List<CustomerStatusEntity>> statesViewMapForClient = new HashMap<Short, List<CustomerStatusEntity>>();
	
	private Map<StateEntity, List<StateEntity>> statesMapForGroup = new HashMap<StateEntity, List<StateEntity>>();

	private Map<Short, List<CustomerStatusEntity>> statesViewMapForGroup = new HashMap<Short, List<CustomerStatusEntity>>();

	List<AccountStateEntity> accountStateEntityListForLoan = new ArrayList<AccountStateEntity>();

	List<AccountStateEntity> accountStateEntityListForSavings = new ArrayList<AccountStateEntity>();

	List<CustomerStatusEntity> customerStatusListForCenter = new ArrayList<CustomerStatusEntity>();

	List<CustomerStatusEntity> customerStatusListForClient = new ArrayList<CustomerStatusEntity>();
	
	List<CustomerStatusEntity> customerStatusListForGroup = new ArrayList<CustomerStatusEntity>();

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public void initialize(Short localeId, Short officeId, Short accountTypeId,
			Short levelId) throws StatesInitializationException {
		logger.debug("In SavingsStateMachine::initialize()");
		try {
			if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue())) {
				statesMapForLoan = StateXMLParser.getInstance().loadMapFromXml(
						AccountStates.TRANSITION_CONFIG_FILE_PATH_LOAN,
						getConfigurationName(officeId, accountTypeId, levelId));
				accountStateEntityListForLoan = retrieveAllAccountStateList(accountTypeId);
				for (AccountStateEntity accountState : accountStateEntityListForLoan)
					Hibernate.initialize(accountState.getFlagSet());
				populateLoanStatesViewMap();
			} else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
				statesMapForSavings = StateXMLParser
						.getInstance()
						.loadMapFromXml(
								AccountStates.TRANSITION_CONFIG_FILE_PATH_SAVINGS,
								getConfigurationName(officeId, accountTypeId,
										levelId));
				accountStateEntityListForSavings = retrieveAllAccountStateList(accountTypeId);
				for (AccountStateEntity accountState : accountStateEntityListForSavings)
					Hibernate.initialize(accountState.getFlagSet());
				populateSavingsStatesViewMap();
			} else if (accountTypeId
					.equals(AccountTypes.CUSTOMERACCOUNT.getValue())) {
				if (levelId.equals(CustomerLevel.CENTER.getValue())) {
					statesMapForCenter = StateXMLParser
							.getInstance()
							.loadMapFromXml(
									CustomerConstants.TRANSITION_CONFIG_FILE_PATH_CENTER,
									getConfigurationName(officeId, accountTypeId,
											levelId));
					customerStatusListForCenter = retrieveAllCustomerStatusList(accountTypeId);
					for (CustomerStatusEntity customerStatus : customerStatusListForCenter)
						Hibernate.initialize(customerStatus.getFlagSet());
					populateCenterStatesViewMap();
				} else if (levelId.equals(CustomerLevel.GROUP.getValue())) {
					statesMapForGroup = StateXMLParser
							.getInstance()
							.loadMapFromXml(
									CustomerConstants.TRANSITION_CONFIG_FILE_PATH_GROUP,
									getConfigurationName(officeId, accountTypeId,
											levelId));
					customerStatusListForGroup = retrieveAllCustomerStatusList(levelId);
					for (CustomerStatusEntity customerStatus : customerStatusListForGroup) {
						for(CustomerStatusFlagEntity customerStatusFlagEntity : customerStatus.getFlagSet()) {
							Hibernate.initialize(customerStatusFlagEntity);
							Hibernate.initialize(customerStatusFlagEntity.getNames());
						}
					}
					populateGroupStatesViewMap();
				} else if (levelId.equals(CustomerLevel.CLIENT.getValue())) {
					statesMapForClient = StateXMLParser
					.getInstance()
					.loadMapFromXml(
							CustomerConstants.TRANSITION_CONFIG_FILE_PATH_CLIENT,
							getConfigurationName(officeId, accountTypeId,
									levelId));
			customerStatusListForClient = retrieveAllCustomerStatusList(levelId);
			System.out.println("*****************customerStatusListForClient="+customerStatusListForClient.size());
			for (CustomerStatusEntity customerStatus : customerStatusListForClient) {
				for(CustomerStatusFlagEntity customerStatusFlagEntity : customerStatus.getFlagSet()) {
					Hibernate.initialize(customerStatusFlagEntity);
					Hibernate.initialize(customerStatusFlagEntity.getNames());
				}
			}
			populateClientStatesViewMap();
		}
			}
		} catch (Exception e) {
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private void populateLoanStatesViewMap() throws StatesInitializationException {
		if (null != statesMapForLoan) {
			Set<StateEntity> accountStateEntityKeySet = statesMapForLoan
					.keySet();
			try {
				if (null != accountStateEntityKeySet
						&& null != accountStateEntityListForLoan) {
					for (StateEntity accountStateEntity : accountStateEntityKeySet) {
						for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityListForLoan) {
							if (accountStateEntity
									.equals(accountStateEntityQueryResultObj)) {
								statesViewMapForLoan
										.put(
												accountStateEntityQueryResultObj
														.getId(),
												retrieveNextPossibleAccountStateObjectsForLoan(accountStateEntity));
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				throw new StatesInitializationException(
						SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
			}
		}
	}

	private void populateSavingsStatesViewMap() throws StatesInitializationException{
		if (null != statesMapForSavings) {
			Set<StateEntity> accountStateEntityKeySet = statesMapForSavings
					.keySet();
			try {
				if (null != accountStateEntityKeySet
						&& null != accountStateEntityListForSavings) {
					for (StateEntity accountStateEntity : accountStateEntityKeySet) {
						for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityListForSavings) {
							if (accountStateEntity
									.equals(accountStateEntityQueryResultObj)) {
								statesViewMapForSavings
										.put(
												accountStateEntityQueryResultObj
														.getId(),
												retrieveNextPossibleAccountStateObjectsForSavings(accountStateEntity));
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				throw new StatesInitializationException(
						SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
			}
		}
	}

	public List<AccountStateEntity> getStatusList(
			AccountStateEntity accountStateEntity, Short accountTypeId) {
		if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue())) {
			return statesViewMapForLoan.get(accountStateEntity.getId());
		} else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
			return statesViewMapForSavings.get(accountStateEntity.getId());
		}
		return null;
	}

	private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForLoan(
			StateEntity accountStateEntityObj) throws ApplicationException {
		logger
				.debug("In SavingsStateMachine::retrieveNextPossibleAccountStateObjects()");
		List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
		try {
			List<StateEntity> stateList = statesMapForLoan
					.get(accountStateEntityObj);
			if (null != stateList) {
				for (StateEntity accountStateEntity : stateList) {
					for (AccountStateEntity accountStateEnty : accountStateEntityListForLoan) {
						if (accountStateEntity.equals(accountStateEnty)) {
							stateEntityList.add(accountStateEnty);
							break;
						}
					}
				}
			}
			return stateEntityList;
		} catch (Exception e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		}
	}

	private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForSavings(
			StateEntity accountStateEntityObj) throws ApplicationException {
		logger
				.debug("In SavingsStateMachine::retrieveNextPossibleAccountStateObjects()");
		List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
		try {
			List<StateEntity> stateList = statesMapForSavings
					.get(accountStateEntityObj);
			if (null != stateList) {
				for (StateEntity accountStateEntity : stateList) {
					for (AccountStateEntity accountStateEnty : accountStateEntityListForSavings) {
						if (accountStateEntity.equals(accountStateEnty)) {
							stateEntityList.add(accountStateEnty);
							break;
						}
					}
				}
			}
			return stateEntityList;
		} catch (Exception e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		}
	}

	private List<AccountStateEntity> retrieveAllAccountStateList(Short accountTypeId) throws ApplicationException{
		logger.debug("In SavingsStateMachine::retrieveAllAccountStateList()");
		try {
			return accountBusinessService.retrieveAllAccountStateList(accountTypeId);
		} catch (PersistenceException e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		} catch (ServiceException e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		}
	}

	public String getStatusName(Short localeId, Short accountStatusId,
			Short accountTypeId){
			if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue())) {
				for (AccountStateEntity accountStateEntityObj : accountStateEntityListForLoan) {
					if (accountStateEntityObj.getId().equals(accountStatusId)) {
						return accountStateEntityObj.getName(localeId);
					}
				}
			} else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
				for (AccountStateEntity accountStateEntityObj : accountStateEntityListForSavings) {
					if (accountStateEntityObj.getId().equals(accountStatusId)) {
						return accountStateEntityObj.getName(localeId);
					}
				}
			}
			return null;
	}

	public String getFlagName(Short flagId, Short accountTypeId){
		if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue())) {
			for (AccountStateEntity accountStateEntity : accountStateEntityListForLoan) {
				for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity
						.getFlagSet()) {
					if (null != accountStateFlagEntity.getId()) {
						if (accountStateFlagEntity.getId().equals(flagId)) {
							return accountStateFlagEntity.getFlagDescription();
						}
					}
				}
			}
		} else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
			for (AccountStateEntity accountStateEntity : accountStateEntityListForSavings) {
				for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity
						.getFlagSet()) {
					if (null != accountStateFlagEntity.getId()) {
						if (accountStateFlagEntity.getId().equals(flagId)) {
							return accountStateFlagEntity.getFlagDescription();
						}
					}
				}
			}
		}
		return null;
	}

	public AccountStateEntity retrieveAccountStateEntityMasterObject(
			AccountStateEntity obj, Short accountTypeId) {
		if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue())) {
			for (AccountStateEntity object : accountStateEntityListForLoan) {
				if (object.equals(obj))
					return object;
			}
		} else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
			for (AccountStateEntity object : accountStateEntityListForSavings) {
				if (object.equals(obj))
					return object;
			}
		}
		return null;
	}

	protected boolean isTransitionAllowed(Object info,
			AccountStateEntity newState) throws ApplicationException {

		logger.debug("In SavingsStateMachine::isTransitionAllowed()");
		AccountStateEntity currentState = null;
		List<AccountStateEntity> applicableTransitions = null;
		if (info instanceof SavingsBO) {
			SavingsBO savingsBO = (SavingsBO) info;
			currentState = savingsBO.getAccountState();
			applicableTransitions = statesViewMapForSavings.get(currentState
					.getId());
		} else if (info instanceof LoanBO) {
			LoanBO loanBO = (LoanBO) info;
			currentState = loanBO.getAccountState();
			applicableTransitions = statesViewMapForLoan.get(currentState
					.getId());
		}
		return applicableTransitions.contains(newState);
	}

	private String getConfigurationName(Short officeId, Short accountTypeId,
			Short levelId) {
		String configurationName = null;
		if (accountTypeId.equals(AccountTypes.LOANACCOUNT.getValue())) {
			if (Configuration.getInstance().getAccountConfig(officeId)
					.isDisbursedToLOStateDefinedForLoan()) {
				if (Configuration.getInstance().getAccountConfig(officeId)
						.isPendingApprovalStateDefinedForLoan())
					configurationName = "configuration 1";
				else
					configurationName = "configuration 2";
			} else {
				if (Configuration.getInstance().getAccountConfig(officeId)
						.isPendingApprovalStateDefinedForLoan())
					configurationName = "configuration 3";
				else
					configurationName = "configuration 4";
			}
		} else if (accountTypeId.equals(AccountTypes.SAVINGSACCOUNT.getValue())) {
			if (Configuration.getInstance().getAccountConfig(officeId)
					.isPendingApprovalStateDefinedForSavings())
				configurationName = "configuration 1";
			else
				configurationName = "configuration 2";
		} else if (accountTypeId.equals(AccountTypes.CUSTOMERACCOUNT.getValue())) {
			if (accountTypeId.equals(CustomerLevel.CENTER.getValue())) {
				configurationName = "configuration 1";
			} else if (accountTypeId.equals(CustomerLevel.GROUP.getValue())) {
				if (Configuration.getInstance().getCustomerConfig(officeId)
						.isPendingApprovalStateDefinedForGroup())
					configurationName = "configuration 1";
				else
					configurationName = "configuration 2";
			} else if (accountTypeId.equals(CustomerLevel.CLIENT.getValue())) {
				if (Configuration.getInstance().getCustomerConfig(officeId)
						.isPendingApprovalStateDefinedForClient())
					configurationName = "configuration 1";
				else
					configurationName = "configuration 2";
			}
		}
		return configurationName;
	}

	private List<CustomerStatusEntity> retrieveAllCustomerStatusList(
			Short levelId) throws AccountException {
		logger.debug("In SavingsStateMachine::retrieveAllAccountStateList()");
		try {
			return customerBusinessService.retrieveAllCustomerStatusList(levelId);
		} catch (PersistenceException pe) {
			throw new AccountException(pe);
		}
	}

	private void populateCenterStatesViewMap() throws StatesInitializationException {
		if (null != statesMapForCenter) {

			Set<StateEntity> customerStatusEntityKeySet = statesMapForCenter
					.keySet();

			try {
				if (null != customerStatusListForCenter
						&& null != customerStatusListForCenter) {
					for (StateEntity customerStateEntity : customerStatusEntityKeySet) {
						for (CustomerStatusEntity customerStateEntityQueryResultObj : customerStatusListForCenter) {
							if (customerStateEntity
									.equals(customerStateEntityQueryResultObj)) {
								statesViewMapForCenter
										.put(
												customerStateEntityQueryResultObj
														.getId(),
												retrieveNextPossibleCustomerStateForCenter(customerStateEntity));
								break;
							}
						}
					}
				}

			} catch (Exception e) {
				throw new StatesInitializationException(
						SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
			}
		}

	}
	
	private void populateGroupStatesViewMap() throws StatesInitializationException {
		if (null != statesMapForGroup) {

			Set<StateEntity> customerStatusEntityKeySet = statesMapForGroup
					.keySet();

			try {
				if (null != customerStatusListForGroup
						&& null != customerStatusListForGroup) {
					for (StateEntity customerStateEntity : customerStatusEntityKeySet) {
						for (CustomerStatusEntity customerStateEntityQueryResultObj : customerStatusListForGroup) {
							if (customerStateEntity
									.equals(customerStateEntityQueryResultObj)) {
								statesViewMapForGroup
										.put(
												customerStateEntityQueryResultObj
														.getId(),
												retrieveNextPossibleCustomerStateForGroup(customerStateEntity));
								break;
							}
						}
					}
				}

			} catch (Exception e) {
				throw new StatesInitializationException(
						SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
			}
		}

	}

	private void populateClientStatesViewMap() throws StatesInitializationException {
		if (null != statesMapForClient) {

			Set<StateEntity> customerStatusEntityKeySet = statesMapForClient
					.keySet();

			try {
				if (null != customerStatusListForClient
						&& null != customerStatusListForClient) {
					for (StateEntity customerStateEntity : customerStatusEntityKeySet) {
						for (CustomerStatusEntity customerStateEntityQueryResultObj : customerStatusListForClient) {
							if (customerStateEntity
									.equals(customerStateEntityQueryResultObj)) {
								statesViewMapForClient
										.put(
												customerStateEntityQueryResultObj
														.getId(),
												retrieveNextPossibleCustomerStateForClient(customerStateEntity));
								break;
							}
						}
					}
				}

			} catch (Exception e) {
				throw new StatesInitializationException(
						SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
			}
		}

	}

	private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForCenter(
			StateEntity customerStateEntityObj) throws ApplicationException{
		logger
				.debug("In SavingsStateMachine::retrieveNextPossibleAccountStateObjects()");
		List<CustomerStatusEntity> stateEntityList = new ArrayList<CustomerStatusEntity>();
		try {
			List<StateEntity> stateList = statesMapForCenter
					.get(customerStateEntityObj);
			if (null != stateList) {
				for (StateEntity customerStateEntity : stateList) {
					for (CustomerStatusEntity customerStatusEntry : customerStatusListForCenter) {
						if (customerStatusEntry.equals(customerStateEntity)) {
							stateEntityList.add(customerStatusEntry);
							break;
						}
					}
				}
			}
			return stateEntityList;
		} catch (Exception e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		}
	}
	
	private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForGroup(
			StateEntity customerStateEntityObj) throws ApplicationException{
		List<CustomerStatusEntity> stateEntityList = new ArrayList<CustomerStatusEntity>();
		try {
			List<StateEntity> stateList = statesMapForGroup
					.get(customerStateEntityObj);
			if (null != stateList) {
				for (StateEntity customerStateEntity : stateList) {
					for (CustomerStatusEntity customerStatusEntry : customerStatusListForGroup) {
						if (customerStatusEntry.equals(customerStateEntity)) {
							stateEntityList.add(customerStatusEntry);
							break;
						}
					}
				}
			}
			return stateEntityList;
		} catch (Exception e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		}
	}

	private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForClient(
			StateEntity customerStateEntityObj) throws ApplicationException{
		List<CustomerStatusEntity> stateEntityList = new ArrayList<CustomerStatusEntity>();
		try {
			List<StateEntity> stateList = statesMapForClient
					.get(customerStateEntityObj);
			if (null != stateList) {
				for (StateEntity customerStateEntity : stateList) {
					for (CustomerStatusEntity customerStatusEntry : customerStatusListForClient) {
						if (customerStatusEntry.equals(customerStateEntity)) {
							stateEntityList.add(customerStatusEntry);
							break;
						}
					}
				}
			}
			return stateEntityList;
		} catch (Exception e) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,
					e);
		}
	}

	public String getCustomerStatusName(Short localeId, Short customerStatusId,
			Short accountTypeId) {
			if (accountTypeId.equals(CustomerLevel.CENTER.getValue())) {
				for (CustomerStatusEntity customerStatus : customerStatusListForCenter) {
					if (customerStatus.getId().equals(customerStatusId)) {
						return customerStatus.getName(localeId);
					}
				}
			} else if (accountTypeId.equals(CustomerLevel.GROUP.getValue())) {
				for (CustomerStatusEntity customerStatus : customerStatusListForGroup) {
					if (customerStatus.getId().equals(customerStatusId)) {
						return customerStatus.getName(localeId);
					}
				}
			} else if (accountTypeId.equals(CustomerLevel.CLIENT.getValue())) {
				for (CustomerStatusEntity customerStatus : customerStatusListForClient) {
					if (customerStatus.getId().equals(customerStatusId)) {
						return customerStatus.getName(localeId);
					}
				}
			}
			return null;
	}

	public String getCustomerFlagName(Short localeId, Short flagId,
			Short accountTypeId) {
		if (accountTypeId.equals(CustomerLevel.CENTER.getValue())) {
			for (CustomerStatusEntity customerStatus : customerStatusListForCenter) {
				for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus
						.getFlagSet()) {
					if (null != customerStateFlagEntity.getId()) {
						if (customerStateFlagEntity.getId().equals(flagId)) {
							return customerStateFlagEntity.getName(localeId);
						}
					}
				}
			}
		} else if (accountTypeId.equals(CustomerLevel.GROUP.getValue())) {
			for (CustomerStatusEntity customerStatus : customerStatusListForGroup) {
				for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus
						.getFlagSet()) {
					if (null != customerStateFlagEntity.getId()) {
						if (customerStateFlagEntity.getId().equals(flagId)) {
							return customerStateFlagEntity.getName(localeId);
						}
					}
				}
			}
		} else if (accountTypeId.equals(CustomerLevel.CLIENT.getValue())) {
			for (CustomerStatusEntity customerStatus : customerStatusListForClient) {
				for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus
						.getFlagSet()) {
					if (null != customerStateFlagEntity.getId()) {
						if (customerStateFlagEntity.getId().equals(flagId)) {
							return customerStateFlagEntity.getName(localeId);
						}
					}
				}
			}
		}
		return null;
	}

	public List<CustomerStatusEntity> getStatusList(
			CustomerStatusEntity customerStatus, Short levelId) {
		if (levelId.equals(CustomerLevel.CENTER.getValue())) {
			if (statesViewMapForCenter.containsKey(customerStatus.getId())) {
				return statesViewMapForCenter.get(customerStatus.getId());
			}
		} else if (levelId.equals(CustomerLevel.GROUP.getValue())) {
			if (statesViewMapForGroup.containsKey(customerStatus.getId())) {
				return statesViewMapForGroup.get(customerStatus.getId());
			}
		} else if (levelId.equals(CustomerLevel.CLIENT.getValue())) {
			if (statesViewMapForClient.containsKey(customerStatus.getId())) {
				return statesViewMapForClient.get(customerStatus.getId());
			}
		}
		return null;
	}
}
