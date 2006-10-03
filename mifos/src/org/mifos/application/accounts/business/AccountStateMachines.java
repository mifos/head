/**

 * AccountStateMachines.java    version: 1.0

 

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

import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountStateFlag;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.CustomerStatusFlagEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.master.business.StateEntity;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.stateMachineFactory.StateMachine;
import org.mifos.framework.components.stateMachineFactory.StateXMLParser;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;

public class AccountStateMachines implements StateMachine {

	private static AccountStateMachines statemachine = new AccountStateMachines();
	
	private AccountStateMachines() {
	}

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

	private List<AccountStateEntity> accountStateEntityListForLoan = new ArrayList<AccountStateEntity>();

	private List<AccountStateEntity> accountStateEntityListForSavings = new ArrayList<AccountStateEntity>();

	private List<CustomerStatusEntity> customerStatusListForCenter = new ArrayList<CustomerStatusEntity>();

	private List<CustomerStatusEntity> customerStatusListForClient = new ArrayList<CustomerStatusEntity>();

	private List<CustomerStatusEntity> customerStatusListForGroup = new ArrayList<CustomerStatusEntity>();

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public void initialize(Short localeId, Short officeId,
			AccountTypes accountType, CustomerLevel level)
			throws StatesInitializationException {
		logger.debug("In AccountStateMachines::initialize()");
		String configName = getConfigurationName(officeId, accountType, level);
		try {
			if (accountType.equals(AccountTypes.LOANACCOUNT)) {
				statesMapForLoan = loadMap(
						AccountStates.TRANSITION_CONFIG_FILE_PATH_LOAN,
						configName);
				accountStateEntityListForLoan = retrieveAllAccountStateList(accountType);
				populateLoanStatesViewMap();
			} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT)) {
				statesMapForSavings = loadMap(
						AccountStates.TRANSITION_CONFIG_FILE_PATH_SAVINGS,
						configName);
				accountStateEntityListForSavings = retrieveAllAccountStateList(accountType);
				populateSavingsStatesViewMap();
			} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT)) {
				if (level.equals(CustomerLevel.CENTER)) {
					statesMapForCenter = loadMap(
							CustomerConstants.TRANSITION_CONFIG_FILE_PATH_CENTER,
							configName);
					customerStatusListForCenter = retrieveAllCustomerStatusList(level);
					populateCenterStatesViewMap();
				} else if (level.equals(CustomerLevel.GROUP)) {
					statesMapForGroup = loadMap(
							CustomerConstants.TRANSITION_CONFIG_FILE_PATH_GROUP,
							configName);
					customerStatusListForGroup = retrieveAllCustomerStatusList(level);
					populateGroupStatesViewMap();
				} else if (level.equals(CustomerLevel.CLIENT)) {
					statesMapForClient = loadMap(
							CustomerConstants.TRANSITION_CONFIG_FILE_PATH_CLIENT,
							configName);
					customerStatusListForClient = retrieveAllCustomerStatusList(level);
					populateClientStatesViewMap();
				}
			}
		} catch (Exception e) {
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	public String getAccountStatusName(Short localeId,
			AccountState accountState, AccountTypes accountType) {
		logger.debug("In AccountStateMachines::getAccountStatusName()");
		if (accountType.equals(AccountTypes.LOANACCOUNT)) {
			for (AccountStateEntity accountStateEntityObj : accountStateEntityListForLoan) {
				if (accountStateEntityObj.getId().equals(
						accountState.getValue())) {
					return accountStateEntityObj.getName(localeId);
				}
			}
		} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT)) {
			for (AccountStateEntity accountStateEntityObj : accountStateEntityListForSavings) {
				if (accountStateEntityObj.getId().equals(
						accountState.getValue())) {
					return accountStateEntityObj.getName(localeId);
				}
			}
		}
		return null;
	}

	public String getAccountFlagName(Short localeId, AccountStateFlag flag,
			AccountTypes accountType) {
		logger.debug("In AccountStateMachines::getAccountFlagName()");
		if (accountType.equals(AccountTypes.LOANACCOUNT)) {
			for (AccountStateEntity accountStateEntity : accountStateEntityListForLoan) {
				for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity
						.getFlagSet()) {
					if (accountStateFlagEntity.getId().equals(flag.getValue())) {
						return accountStateFlagEntity.getName(localeId);
					}
				}
			}
		} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT)) {
			for (AccountStateEntity accountStateEntity : accountStateEntityListForSavings) {
				for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity
						.getFlagSet()) {
					if (accountStateFlagEntity.getId().equals(flag.getValue())) {
						return accountStateFlagEntity.getName(localeId);
					}
				}
			}
		}
		return null;
	}

	public String getCustomerStatusName(Short localeId,
			CustomerStatus customerStatus, CustomerLevel customerLevel) {
		logger.debug("In AccountStateMachines::getCustomerStatusName()");
		if (customerLevel.equals(CustomerLevel.CENTER)) {
			for (CustomerStatusEntity customerStatusEntity : customerStatusListForCenter) {
				if (customerStatusEntity.getId().equals(
						customerStatus.getValue())) {
					return customerStatusEntity.getName(localeId);
				}
			}
		} else if (customerLevel.equals(CustomerLevel.GROUP)) {
			for (CustomerStatusEntity customerStatusEntity : customerStatusListForGroup) {
				if (customerStatusEntity.getId().equals(
						customerStatus.getValue())) {
					return customerStatusEntity.getName(localeId);
				}
			}
		} else if (customerLevel.equals(CustomerLevel.CLIENT)) {
			for (CustomerStatusEntity customerStatusEntity : customerStatusListForClient) {
				if (customerStatusEntity.getId().equals(
						customerStatus.getValue())) {
					return customerStatusEntity.getName(localeId);
				}
			}
		}
		return null;
	}

	public String getCustomerFlagName(Short localeId,
			CustomerStatusFlag customerStatusFlag, CustomerLevel customerLevel) {
		logger.debug("In AccountStateMachines::getCustomerFlagName()");
		if (customerLevel.equals(CustomerLevel.CENTER)) {
			for (CustomerStatusEntity customerStatus : customerStatusListForCenter) {
				for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus
						.getFlagSet()) {
					if (null != customerStateFlagEntity.getId()) {
						if (customerStateFlagEntity.getId().equals(
								customerStatusFlag.getValue())) {
							return customerStateFlagEntity.getName(localeId);
						}
					}
				}
			}
		} else if (customerLevel.equals(CustomerLevel.GROUP)) {
			for (CustomerStatusEntity customerStatus : customerStatusListForGroup) {
				for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus
						.getFlagSet()) {
					if (null != customerStateFlagEntity.getId()) {
						if (customerStateFlagEntity.getId().equals(
								customerStatusFlag.getValue())) {
							return customerStateFlagEntity.getName(localeId);
						}
					}
				}
			}
		} else if (customerLevel.equals(CustomerLevel.CLIENT)) {
			for (CustomerStatusEntity customerStatus : customerStatusListForClient) {
				for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus
						.getFlagSet()) {
					if (null != customerStateFlagEntity.getId()) {
						if (customerStateFlagEntity.getId().equals(
								customerStatusFlag.getValue())) {
							return customerStateFlagEntity.getName(localeId);
						}
					}
				}
			}
		}
		return null;
	}

	public List<AccountStateEntity> getStatusList(
			AccountStateEntity accountStateEntity, AccountTypes accountTypes) {
		logger.debug("In AccountStateMachines::getStatusList()");
		if (accountTypes.equals(AccountTypes.LOANACCOUNT)) {
			return statesViewMapForLoan.get(accountStateEntity.getId());
		} else if (accountTypes.equals(AccountTypes.SAVINGSACCOUNT)) {
			return statesViewMapForSavings.get(accountStateEntity.getId());
		}
		return null;
	}

	public List<CustomerStatusEntity> getStatusList(
			CustomerStatusEntity customerStatus, CustomerLevel customerLevel) {
		logger.debug("In AccountStateMachines::getStatusList()");
		if (customerLevel.equals(CustomerLevel.CENTER)) {
			if (statesViewMapForCenter.containsKey(customerStatus.getId())) {
				return statesViewMapForCenter.get(customerStatus.getId());
			}
		} else if (customerLevel.equals(CustomerLevel.GROUP)) {
			if (statesViewMapForGroup.containsKey(customerStatus.getId())) {
				return statesViewMapForGroup.get(customerStatus.getId());
			}
		} else if (customerLevel.equals(CustomerLevel.CLIENT)) {
			if (statesViewMapForClient.containsKey(customerStatus.getId())) {
				return statesViewMapForClient.get(customerStatus.getId());
			}
		}
		return null;
	}

	private String getConfigurationName(Short officeId,
			AccountTypes accountType, CustomerLevel level) {
		logger.debug("In AccountStateMachines::getConfigurationName()");
		String configurationName = null;
		if (accountType.equals(AccountTypes.LOANACCOUNT)) {
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
		} else if (accountType.equals(AccountTypes.SAVINGSACCOUNT)) {
			if (Configuration.getInstance().getAccountConfig(officeId)
					.isPendingApprovalStateDefinedForSavings())
				configurationName = "configuration 1";
			else
				configurationName = "configuration 2";
		} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT)) {
			if (level.equals(CustomerLevel.CENTER)) {
				configurationName = "configuration 1";
			} else if (level.equals(CustomerLevel.GROUP)) {
				if (Configuration.getInstance().getCustomerConfig(officeId)
						.isPendingApprovalStateDefinedForGroup())
					configurationName = "configuration 1";
				else
					configurationName = "configuration 2";
			} else if (level.equals(CustomerLevel.CLIENT)) {
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
			CustomerLevel customerLevel) throws StatesInitializationException {
		logger
				.debug("In AccountStateMachines::retrieveAllCustomerStatusList()");
		try {
			return new CustomerBusinessService()
					.retrieveAllCustomerStatusList(customerLevel.getValue());
		} catch (ServiceException pe) {
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, pe);
		}
	}

	private void populateCenterStatesViewMap()
			throws StatesInitializationException {
		logger.debug("In AccountStateMachines::populateCenterStatesViewMap()");
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

	private void populateGroupStatesViewMap()
			throws StatesInitializationException {
		logger.debug("In AccountStateMachines::populateGroupStatesViewMap()");
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

	private void populateClientStatesViewMap()
			throws StatesInitializationException {
		logger.debug("In AccountStateMachines::populateClientStatesViewMap()");
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
			StateEntity customerStateEntityObj) throws ApplicationException {
		logger
				.debug("In AccountStateMachines::retrieveNextPossibleCustomerStateForCenter()");
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
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForGroup(
			StateEntity customerStateEntityObj) throws ApplicationException {
		logger
				.debug("In AccountStateMachines::retrieveNextPossibleCustomerStateForGroup()");
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
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForClient(
			StateEntity customerStateEntityObj) throws ApplicationException {
		logger
				.debug("In AccountStateMachines::retrieveNextPossibleCustomerStateForClient()");
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
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForLoan(
			StateEntity accountStateEntityObj) throws ApplicationException {
		logger
				.debug("In AccountStateMachines::retrieveNextPossibleAccountStateObjectsForLoan()");
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
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForSavings(
			StateEntity accountStateEntityObj) throws ApplicationException {
		logger
				.debug("In AccountStateMachines::retrieveNextPossibleAccountStateObjectsForSavings()");
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
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private List<AccountStateEntity> retrieveAllAccountStateList(
			AccountTypes accountType) throws ApplicationException {
		logger.debug("In AccountStateMachines::retrieveAllAccountStateList()");
		try {
			return new AccountBusinessService()
					.retrieveAllAccountStateList(accountType);
		} catch (ServiceException e) {
			throw new ApplicationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private void populateLoanStatesViewMap()
			throws StatesInitializationException {
		logger.debug("In AccountStateMachines::populateLoanStatesViewMap()");
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

	private void populateSavingsStatesViewMap()
			throws StatesInitializationException {
		logger.debug("In AccountStateMachines::populateSavingsStatesViewMap()");
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

	private Map<StateEntity, List<StateEntity>> loadMap(String filename,
			String configurationName) {
		return StateXMLParser.getInstance().loadMapFromXml(filename,
				configurationName);
	}
}
