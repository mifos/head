/**

 * SavingsStateMachine.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
package org.mifos.application.accounts.savings.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
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
public class SavingsStateMachine implements StateMachine {
	SavingsBusinessService savingsBusinessService = new SavingsBusinessService();

	private SavingsStateMachine() {
		super();
	}

	private static SavingsStateMachine savingStatemachine = new SavingsStateMachine();

	public static SavingsStateMachine getInstance() {
		return savingStatemachine;
	}

	private Map<AccountStateEntity, List<AccountStateEntity>> statesMap = new HashMap<AccountStateEntity, List<AccountStateEntity>>();

	private Map<AccountStateEntity, List<AccountStateEntity>> statesViewMap = new HashMap<AccountStateEntity, List<AccountStateEntity>>();

	List<AccountStateEntity> accountStateEntityList;

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mifos.framework.components.stateMachineFactory.StateMachine#initialize(java.util.Locale)
	 */
	public void initialize(Short localeId, Short officeId) throws StatesInitializationException {
		logger.debug("In SavingsStateMachine::initialize()");
		try {
			statesMap = StateXMLParser.getInstance().loadMapFromXml(
					AccountStates.TRANSITION_CONFIG_FILE_PATH,
					getConfigurationName(officeId));
			accountStateEntityList = retrieveAllAccountStateList(new Short(
					AccountTypes.SAVINGSACCOUNT));
			for (AccountStateEntity accountState : accountStateEntityList)
				Hibernate.initialize(accountState.getFlagSet());
			populateStatesViewMap();
		} catch (Exception e) {
			throw new StatesInitializationException(
					SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
		}
	}

	private void populateStatesViewMap() throws ApplicationException,
			SystemException {
		logger.debug("In SavingsStateMachine::populateStatesViewMap()");
		if (null != statesMap) {
			Set<AccountStateEntity> accountStateEntityKeySet = statesMap
					.keySet();
			try {
				if (null != accountStateEntityKeySet
						&& null != accountStateEntityList) {
					for (AccountStateEntity accountStateEntity : accountStateEntityKeySet) {
						for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityList) {
							if (accountStateEntityQueryResultObj
									.equals(accountStateEntity)) {
								statesViewMap
										.put(
												accountStateEntityQueryResultObj,
												retrieveNextPossibleAccountStateObjects(accountStateEntityQueryResultObj));
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
			AccountStateEntity accountStateEntity) {
		logger.debug("In SavingsStateMachine::getStatusList()");
		List<AccountStateEntity> statusList = new ArrayList<AccountStateEntity>();
		statusList = statesViewMap.get(accountStateEntity);
		return statusList;
	}

	private List<AccountStateEntity> retrieveNextPossibleAccountStateObjects(
			AccountStateEntity accountStateEntityObj)
			throws ApplicationException {
		logger
				.debug("In SavingsStateMachine::retrieveNextPossibleAccountStateObjects()");
		List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
		try {
			List<AccountStateEntity> stateList = statesMap
					.get(accountStateEntityObj);
			if (null != stateList) {
				for (AccountStateEntity accountStateEntity : stateList) {
					for (AccountStateEntity accountStateEnty : accountStateEntityList) {
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
		return savingsBusinessService.retrieveAllAccountStateList(prdTypeId);
	}

	public String getStatusName(Short localeId, Short accountStatusId)
			throws ApplicationException, SystemException, NumberFormatException {
		String name = null;
		try {
			for (AccountStateEntity accountStateEntityObj : accountStateEntityList) {
				if (accountStateEntityObj.getId().shortValue() == accountStatusId
						.shortValue()) {
					name = accountStateEntityObj.getName(localeId);
				}
			}
			return name;
		} catch (NumberFormatException ne) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,ne);
		} catch (LazyInitializationException le) {
			throw new ApplicationException(SavingsConstants.UNKNOWN_EXCEPTION,le);
		}
	}

	public String getFlagName(Short flagId) throws ApplicationException,
			SystemException {
		for (AccountStateEntity accountStateEntity : accountStateEntityList) {
			for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity
					.getFlagSet()) {
				if (null != accountStateFlagEntity.getId()) {
					if (accountStateFlagEntity.getId().equals(flagId)) {
						return accountStateFlagEntity.getFlagDescription();
					}
				}
			}
		}
		return null;
	}
	
	protected AccountStateEntity retrieveAccountStateEntityMasterObject(AccountStateEntity obj) {
		for(AccountStateEntity object:accountStateEntityList) {
			if(object.equals(obj)) return object;
		}
		return null;
	}

	public boolean isTransitionAllowed(Object info, AccountStateEntity newState)
			throws ApplicationException {

		logger.debug("In SavingsStateMachine::isTransitionAllowed()");
		SavingsBO savingsBO = (SavingsBO) info;
		AccountStateEntity currentState = savingsBO.getAccountState();
		List<AccountStateEntity> applicableTransitions = statesViewMap.get(currentState);
		return applicableTransitions.contains(newState);
	}



	private String getConfigurationName(Short officeId) {
		return Configuration.getInstance().getAccountConfig(officeId).isPendingApprovalStateDefinedForSavings() ? "configuration 1"
				: "configuration 2";
	}
}
