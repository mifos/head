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
import org.hibernate.LazyInitializationException;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountType;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.stateMachineFactory.StateMachine;
import org.mifos.framework.components.stateMachineFactory.StateXMLParser;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;

/**
 * @author rohitr
 * 
 */
public class AccountStateMachines implements StateMachine {
	AccountBusinessService accountBusinessService = new AccountBusinessService();

	private AccountStateMachines() {
		super();
	}

	private static AccountStateMachines statemachine = new AccountStateMachines();

	public static AccountStateMachines getInstance() {
		return statemachine;
	}

	private Map<AccountStateEntity, List<AccountStateEntity>> statesMapForLoan = new HashMap<AccountStateEntity, List<AccountStateEntity>>();

	private Map<AccountStateEntity, List<AccountStateEntity>> statesViewMapForLoan = new HashMap<AccountStateEntity, List<AccountStateEntity>>();
	
	private Map<AccountStateEntity, List<AccountStateEntity>> statesMapForSavings = new HashMap<AccountStateEntity, List<AccountStateEntity>>();

	private Map<AccountStateEntity, List<AccountStateEntity>> statesViewMapForSavings = new HashMap<AccountStateEntity, List<AccountStateEntity>>();

	List<AccountStateEntity> accountStateEntityListForLoan;
	List<AccountStateEntity> accountStateEntityListForSavings;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public void initialize(Short localeId, Short officeId, Short prdTypeId) throws StatesInitializationException {
		logger.debug("In SavingsStateMachine::initialize()");
		try {
			if(prdTypeId.equals(AccountType.LOANACCOUNT.getValue())) {
				statesMapForLoan = StateXMLParser.getInstance().loadMapFromXml(
						AccountStates.TRANSITION_CONFIG_FILE_PATH_LOAN,	getConfigurationName(officeId,prdTypeId));
				accountStateEntityListForLoan = retrieveAllAccountStateList(prdTypeId);
				for (AccountStateEntity accountState : accountStateEntityListForLoan)
					Hibernate.initialize(accountState.getFlagSet());
				populateLoanStatesViewMap();
			}else if(prdTypeId.equals(AccountType.SAVINGSACCOUNT.getValue())) {
				statesMapForSavings = StateXMLParser.getInstance().loadMapFromXml(
						AccountStates.TRANSITION_CONFIG_FILE_PATH_SAVINGS,	getConfigurationName(officeId,prdTypeId));
				accountStateEntityListForSavings = retrieveAllAccountStateList(prdTypeId);
				for (AccountStateEntity accountState : accountStateEntityListForSavings)
					Hibernate.initialize(accountState.getFlagSet());
				populateSavingsStatesViewMap();
			}
		} catch (Exception e) {
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private void populateLoanStatesViewMap() throws ApplicationException,
			SystemException {
		logger.debug("In SavingsStateMachine::populateStatesViewMap()");
		if (null != statesMapForLoan) {
			Set<AccountStateEntity> accountStateEntityKeySet = statesMapForLoan.keySet();
			try {
				if (null != accountStateEntityKeySet && null != accountStateEntityListForLoan) {
					for (AccountStateEntity accountStateEntity : accountStateEntityKeySet) {
						for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityListForLoan) {
							if (accountStateEntityQueryResultObj.equals(accountStateEntity)) {
								statesViewMapForLoan.put(accountStateEntityQueryResultObj,retrieveNextPossibleAccountStateObjectsForLoan(accountStateEntityQueryResultObj));
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
	
	private void populateSavingsStatesViewMap() throws ApplicationException,SystemException {
		if (null != statesMapForSavings) {
			Set<AccountStateEntity> accountStateEntityKeySet = statesMapForSavings.keySet();
		try {
			if (null != accountStateEntityKeySet && null != accountStateEntityListForSavings) {
				for (AccountStateEntity accountStateEntity : accountStateEntityKeySet) {
					for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityListForSavings) {
						if (accountStateEntityQueryResultObj.equals(accountStateEntity)) {
							statesViewMapForSavings.put(accountStateEntityQueryResultObj,retrieveNextPossibleAccountStateObjectsForSavings(accountStateEntityQueryResultObj));
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

	public List<AccountStateEntity> getStatusList(AccountStateEntity accountStateEntity, Short prdTypeId) {
		if(prdTypeId.equals(AccountType.LOANACCOUNT.getValue())) {
			return statesViewMapForLoan.get(accountStateEntity);
		}else if(prdTypeId.equals(AccountType.SAVINGSACCOUNT.getValue())) {
			return statesViewMapForSavings.get(accountStateEntity);
		}
		return null;
	}

	private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForLoan(
			AccountStateEntity accountStateEntityObj)
			throws ApplicationException {
		logger
				.debug("In SavingsStateMachine::retrieveNextPossibleAccountStateObjects()");
		List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
		try {
			List<AccountStateEntity> stateList = statesMapForLoan.get(accountStateEntityObj);
			if (null != stateList) {
				for (AccountStateEntity accountStateEntity : stateList) {
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
			AccountStateEntity accountStateEntityObj)
			throws ApplicationException {
		logger
				.debug("In SavingsStateMachine::retrieveNextPossibleAccountStateObjects()");
		List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
		try {
			List<AccountStateEntity> stateList = statesMapForSavings.get(accountStateEntityObj);
			if (null != stateList) {
				for (AccountStateEntity accountStateEntity : stateList) {
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

	private List<AccountStateEntity> retrieveAllAccountStateList(Short prdTypeId)
			throws ApplicationException, SystemException {
		logger.debug("In SavingsStateMachine::retrieveAllAccountStateList()");
		return accountBusinessService.retrieveAllAccountStateList(prdTypeId);
	}

	public String getStatusName(Short localeId, Short accountStatusId,Short prdTypeId)
			throws ApplicationException, SystemException, NumberFormatException {
		try {
			if(prdTypeId.equals(AccountType.LOANACCOUNT.getValue())) {
				for (AccountStateEntity accountStateEntityObj : accountStateEntityListForLoan) {
					if (accountStateEntityObj.getId().equals(accountStatusId)) {
						return accountStateEntityObj.getName(localeId);
					}
				}
			}else if(prdTypeId.equals(AccountType.SAVINGSACCOUNT.getValue())) {
				for (AccountStateEntity accountStateEntityObj : accountStateEntityListForSavings) {
					if (accountStateEntityObj.getId().equals(accountStatusId)) {
						return accountStateEntityObj.getName(localeId);
					}
				}
			}
			return null;
		} catch (NumberFormatException ne) {
			ne.printStackTrace();
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,ne);
		} catch (LazyInitializationException le) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,le);
		}
	}

	public String getFlagName(Short flagId,Short prdTypeId) throws ApplicationException,
			SystemException {
		if(prdTypeId.equals(AccountType.LOANACCOUNT.getValue())) {
			for (AccountStateEntity accountStateEntity : accountStateEntityListForLoan) {
				for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity.getFlagSet()) {
					if (null != accountStateFlagEntity.getId()) {
						if (accountStateFlagEntity.getId().equals(flagId)) {
							return accountStateFlagEntity.getFlagDescription();
						}
					}
				}
			}
		} else if(prdTypeId.equals(AccountType.SAVINGSACCOUNT.getValue())) {
			for (AccountStateEntity accountStateEntity : accountStateEntityListForSavings) {
				for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity.getFlagSet()) {
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
	
	public AccountStateEntity retrieveAccountStateEntityMasterObject(AccountStateEntity obj,Short prdTypeId) {
		if(prdTypeId.equals(AccountType.LOANACCOUNT.getValue())) {
			for(AccountStateEntity object:accountStateEntityListForLoan) {
				if(object.equals(obj)) return object;
			}
		}else if(prdTypeId.equals(AccountType.SAVINGSACCOUNT.getValue())) {
			for(AccountStateEntity object:accountStateEntityListForSavings) {
				if(object.equals(obj)) return object;
			}
		}
		return null;
	}

	public boolean isTransitionAllowed(Object info, AccountStateEntity newState)
			throws ApplicationException {

		logger.debug("In SavingsStateMachine::isTransitionAllowed()");
		AccountStateEntity currentState = null;
		List<AccountStateEntity> applicableTransitions = null;
		if(info instanceof SavingsBO) {
			SavingsBO savingsBO = (SavingsBO) info;
			currentState = savingsBO.getAccountState();
			applicableTransitions = statesViewMapForSavings.get(currentState);
		}else if(info instanceof LoanBO) {
			LoanBO loanBO = (LoanBO) info;
			currentState = loanBO.getAccountState();
			applicableTransitions = statesViewMapForLoan.get(currentState);
		}
		return applicableTransitions.contains(newState);
	}
	
	private String getConfigurationName(Short officeId, Short prdTypeId) {
		String configurationName = null;
		if(prdTypeId.equals(AccountType.LOANACCOUNT.getValue())) {
			if(Configuration.getInstance().getAccountConfig(officeId).isDisbursedToLOStateDefinedForLoan()) {
				if(Configuration.getInstance().getAccountConfig(officeId).isPendingApprovalStateDefinedForLoan())
					configurationName = "configuration 1";
				else
					configurationName = "configuration 2";
			}else {
				if(Configuration.getInstance().getAccountConfig(officeId).isPendingApprovalStateDefinedForLoan())
					configurationName = "configuration 3";
				else
					configurationName = "configuration 4";
			}
		}else if(prdTypeId.equals(AccountType.SAVINGSACCOUNT.getValue())){
			if(Configuration.getInstance().getAccountConfig(officeId).isPendingApprovalStateDefinedForSavings())
				configurationName = "configuration 1";
			else
				configurationName = "configuration 2";
		}		
		return configurationName;
	}
}
