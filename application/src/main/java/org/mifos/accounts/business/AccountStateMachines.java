/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mifos.accounts.business.service.AccountBusinessService;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStateFlag;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.CustomerStatusFlagEntity;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.customers.util.helpers.CustomerStatusFlag;
import org.mifos.application.master.business.StateEntity;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.components.stateMachineFactory.StateXMLParser;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;

/**
 * At first glance this code seems to be part of the excessively complicated
 * machinery ({@link StateEntity} for example), which is being replaced by enums
 * ({@link AccountState} for example).
 */
public class AccountStateMachines {

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

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    public void initialize(Short localeId, Short officeId, AccountTypes accountType, CustomerLevel level)
            throws StatesInitializationException {
        logger.debug("In AccountStateMachines::initialize()");
        String configName = getConfigurationName(officeId, accountType, level);
        try {
            if (accountType.equals(AccountTypes.LOAN_ACCOUNT)) {
                statesMapForLoan = loadMap(AccountStates.TRANSITION_CONFIG_FILE_PATH_LOAN, configName);
                accountStateEntityListForLoan = retrieveAllAccountStateList(accountType);
                removeLoanReversalFlagForCancelState();
                populateLoanStatesViewMap();
            } else if (accountType.equals(AccountTypes.SAVINGS_ACCOUNT)) {
                statesMapForSavings = loadMap(AccountStates.TRANSITION_CONFIG_FILE_PATH_SAVINGS, configName);
                accountStateEntityListForSavings = retrieveAllAccountStateList(accountType);
                populateSavingsStatesViewMap();
            } else if (accountType.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
                if (level.equals(CustomerLevel.CENTER)) {
                    statesMapForCenter = loadMap(CustomerConstants.TRANSITION_CONFIG_FILE_PATH_CENTER, configName);
                    customerStatusListForCenter = retrieveAllCustomerStatusList(level);
                    populateCenterStatesViewMap();
                } else if (level.equals(CustomerLevel.GROUP)) {
                    statesMapForGroup = loadMap(CustomerConstants.TRANSITION_CONFIG_FILE_PATH_GROUP, configName);
                    customerStatusListForGroup = retrieveAllCustomerStatusList(level);
                    populateGroupStatesViewMap();
                } else if (level.equals(CustomerLevel.CLIENT)) {
                    statesMapForClient = loadMap(CustomerConstants.TRANSITION_CONFIG_FILE_PATH_CLIENT, configName);
                    customerStatusListForClient = retrieveAllCustomerStatusList(level);
                    populateClientStatesViewMap();
                }
            }
        } catch (Exception e) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    public String getAccountStatusName(Short localeId, AccountState accountState, AccountTypes accountType) {
        logger.debug("In AccountStateMachines::getAccountStatusName()");
        if (accountType.equals(AccountTypes.LOAN_ACCOUNT)) {
            for (AccountStateEntity accountStateEntityObj : accountStateEntityListForLoan) {
                if (accountStateEntityObj.getId().equals(accountState.getValue())) {
                    return accountStateEntityObj.getName();
                }
            }
        } else if (accountType.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            for (AccountStateEntity accountStateEntityObj : accountStateEntityListForSavings) {
                if (accountStateEntityObj.getId().equals(accountState.getValue())) {
                    return accountStateEntityObj.getName();
                }
            }
        }
        return null;
    }

    public String getAccountFlagName(Short localeId, AccountStateFlag flag, AccountTypes accountType) {
        logger.debug("In AccountStateMachines::getAccountFlagName()");
        if (accountType.equals(AccountTypes.LOAN_ACCOUNT)) {
            for (AccountStateEntity accountStateEntity : accountStateEntityListForLoan) {
                for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity.getFlagSet()) {
                    if (accountStateFlagEntity.getId().equals(flag.getValue())) {
                        return accountStateFlagEntity.getName();
                    }
                }
            }
        } else if (accountType.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            for (AccountStateEntity accountStateEntity : accountStateEntityListForSavings) {
                for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity.getFlagSet()) {
                    if (accountStateFlagEntity.getId().equals(flag.getValue())) {
                        return accountStateFlagEntity.getName();
                    }
                }
            }
        }
        return null;
    }

    public String getCustomerStatusName(Short localeId, CustomerStatus customerStatus, CustomerLevel customerLevel) {
        logger.debug("In AccountStateMachines::getCustomerStatusName()");
        if (customerLevel.equals(CustomerLevel.CENTER)) {
            for (CustomerStatusEntity customerStatusEntity : customerStatusListForCenter) {
                if (customerStatusEntity.getId().equals(customerStatus.getValue())) {
                    return customerStatusEntity.getName();
                }
            }
        } else if (customerLevel.equals(CustomerLevel.GROUP)) {
            for (CustomerStatusEntity customerStatusEntity : customerStatusListForGroup) {
                if (customerStatusEntity.getId().equals(customerStatus.getValue())) {
                    return customerStatusEntity.getName();
                }
            }
        } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
            for (CustomerStatusEntity customerStatusEntity : customerStatusListForClient) {
                if (customerStatusEntity.getId().equals(customerStatus.getValue())) {
                    return customerStatusEntity.getName();
                }
            }
        }
        return null;
    }

    public String getCustomerFlagName(Short localeId, CustomerStatusFlag customerStatusFlag, CustomerLevel customerLevel) {
        logger.debug("In AccountStateMachines::getCustomerFlagName()");
        if (customerLevel.equals(CustomerLevel.CENTER)) {
            for (CustomerStatusEntity customerStatus : customerStatusListForCenter) {
                for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus.getFlagSet()) {
                    if (null != customerStateFlagEntity.getId()) {
                        if (customerStateFlagEntity.getId().equals(customerStatusFlag.getValue())) {
                            return customerStateFlagEntity.getName();
                        }
                    }
                }
            }
        } else if (customerLevel.equals(CustomerLevel.GROUP)) {
            for (CustomerStatusEntity customerStatus : customerStatusListForGroup) {
                for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus.getFlagSet()) {
                    if (null != customerStateFlagEntity.getId()) {
                        if (customerStateFlagEntity.getId().equals(customerStatusFlag.getValue())) {
                            return customerStateFlagEntity.getName();
                        }
                    }
                }
            }
        } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
            for (CustomerStatusEntity customerStatus : customerStatusListForClient) {
                for (CustomerStatusFlagEntity customerStateFlagEntity : customerStatus.getFlagSet()) {
                    if (null != customerStateFlagEntity.getId()) {
                        if (customerStateFlagEntity.getId().equals(customerStatusFlag.getValue())) {
                            return customerStateFlagEntity.getName();
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<AccountStateEntity> getStatusList(AccountStateEntity accountStateEntity, AccountTypes accountTypes) {
        logger.debug("In AccountStateMachines::getStatusList()");
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
            return statesViewMapForLoan.get(accountStateEntity.getId());
        } else if (accountTypes.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            return statesViewMapForSavings.get(accountStateEntity.getId());
        }
        return null;
    }

    public List<CustomerStatusEntity> getStatusList(CustomerStatusEntity customerStatus, CustomerLevel customerLevel) {
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

    private void removeLoanReversalFlagForCancelState() {
        if (accountStateEntityListForLoan != null && accountStateEntityListForLoan.size() > 0) {
            for (AccountStateEntity accountState : accountStateEntityListForLoan) {
                if (accountState.getId().equals(AccountState.LOAN_CANCELLED.getValue())) {
                    for (Iterator<AccountStateFlagEntity> iter = accountState.getFlagSet().iterator(); iter.hasNext();) {
                        AccountStateFlagEntity accountStateFlag = iter.next();
                        if (accountStateFlag.getId().equals(AccountStateFlag.LOAN_REVERSAL.getValue())) {
                            iter.remove();
                        }
                    }

                }
            }
        }
    }

    private String getConfigurationName(Short officeId, AccountTypes accountType, CustomerLevel level) {
        logger.debug("In AccountStateMachines::getConfigurationName()");
        String configurationName = null;
        if (accountType.equals(AccountTypes.LOAN_ACCOUNT)) {
            if (ProcessFlowRules.isLoanDisbursedToLoanOfficerStateEnabled()) {
                if (ProcessFlowRules.isLoanPendingApprovalStateEnabled())
                    configurationName = "configuration 1";
                else
                    configurationName = "configuration 2";
            } else {
                if (ProcessFlowRules.isLoanPendingApprovalStateEnabled())
                    configurationName = "configuration 3";
                else
                    configurationName = "configuration 4";
            }
        } else if (accountType.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            if (ProcessFlowRules.isSavingsPendingApprovalStateEnabled())
                configurationName = "configuration 1";
            else
                configurationName = "configuration 2";
        } else if (accountType.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (level.equals(CustomerLevel.CENTER)) {
                configurationName = "configuration 1";
            } else if (level.equals(CustomerLevel.GROUP)) {
                if (ProcessFlowRules.isGroupPendingApprovalStateEnabled())
                    configurationName = "configuration 1";
                else
                    configurationName = "configuration 2";
            } else if (level.equals(CustomerLevel.CLIENT)) {
                if (ProcessFlowRules.isClientPendingApprovalStateEnabled())
                    configurationName = "configuration 1";
                else
                    configurationName = "configuration 2";
            }
        }
        return configurationName;
    }

    private List<CustomerStatusEntity> retrieveAllCustomerStatusList(CustomerLevel customerLevel)
            throws StatesInitializationException {
        logger.debug("In AccountStateMachines::retrieveAllCustomerStatusList()");
        try {
            return new CustomerBusinessService().retrieveAllCustomerStatusList(customerLevel.getValue());
        } catch (ServiceException pe) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, pe);
        }
    }

    private void populateCenterStatesViewMap() throws StatesInitializationException {
        logger.debug("In AccountStateMachines::populateCenterStatesViewMap()");
        if (null != statesMapForCenter) {

            Set<StateEntity> customerStatusEntityKeySet = statesMapForCenter.keySet();

            try {
                if (null != customerStatusListForCenter && null != customerStatusListForCenter) {
                    for (StateEntity customerStateEntity : customerStatusEntityKeySet) {
                        for (CustomerStatusEntity customerStateEntityQueryResultObj : customerStatusListForCenter) {
                            if (customerStateEntity.sameId(customerStateEntityQueryResultObj)) {
                                statesViewMapForCenter.put(customerStateEntityQueryResultObj.getId(),
                                        retrieveNextPossibleCustomerStateForCenter(customerStateEntity));
                                break;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
            }
        }

    }

    private void populateGroupStatesViewMap() throws StatesInitializationException {
        logger.debug("In AccountStateMachines::populateGroupStatesViewMap()");
        if (null != statesMapForGroup) {

            Set<StateEntity> customerStatusEntityKeySet = statesMapForGroup.keySet();

            try {
                if (null != customerStatusListForGroup && null != customerStatusListForGroup) {
                    for (StateEntity customerStateEntity : customerStatusEntityKeySet) {
                        for (CustomerStatusEntity customerStateEntityQueryResultObj : customerStatusListForGroup) {
                            if (customerStateEntity.sameId(customerStateEntityQueryResultObj)) {
                                statesViewMapForGroup.put(customerStateEntityQueryResultObj.getId(),
                                        retrieveNextPossibleCustomerStateForGroup(customerStateEntity));
                                break;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
            }
        }

    }

    private void populateClientStatesViewMap() throws StatesInitializationException {
        logger.debug("In AccountStateMachines::populateClientStatesViewMap()");
        if (null != statesMapForClient) {

            Set<StateEntity> customerStatusEntityKeySet = statesMapForClient.keySet();

            try {
                if (null != customerStatusListForClient && null != customerStatusListForClient) {
                    for (StateEntity customerStateEntity : customerStatusEntityKeySet) {
                        for (CustomerStatusEntity customerStateEntityQueryResultObj : customerStatusListForClient) {
                            if (customerStateEntity.sameId(customerStateEntityQueryResultObj)) {
                                statesViewMapForClient.put(customerStateEntityQueryResultObj.getId(),
                                        retrieveNextPossibleCustomerStateForClient(customerStateEntity));
                                break;
                            }
                        }
                    }
                }

            } catch (Exception e) {
                throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
            }
        }

    }

    private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForCenter(StateEntity customerStateEntityObj)
            throws ApplicationException {
        logger.debug("In AccountStateMachines::retrieveNextPossibleCustomerStateForCenter()");
        List<CustomerStatusEntity> stateEntityList = new ArrayList<CustomerStatusEntity>();
        try {
            List<StateEntity> stateList = statesMapForCenter.get(customerStateEntityObj);
            if (null != stateList) {
                for (StateEntity customerStateEntity : stateList) {
                    for (CustomerStatusEntity customerStatusEntry : customerStatusListForCenter) {
                        if (customerStatusEntry.sameId(customerStateEntity)) {
                            stateEntityList.add(customerStatusEntry);
                            break;
                        }
                    }
                }
            }
            return stateEntityList;
        } catch (Exception e) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForGroup(StateEntity customerStateEntityObj)
            throws ApplicationException {
        logger.debug("In AccountStateMachines::retrieveNextPossibleCustomerStateForGroup()");
        List<CustomerStatusEntity> stateEntityList = new ArrayList<CustomerStatusEntity>();
        try {
            List<StateEntity> stateList = statesMapForGroup.get(customerStateEntityObj);
            if (null != stateList) {
                for (StateEntity customerStateEntity : stateList) {
                    for (CustomerStatusEntity customerStatusEntry : customerStatusListForGroup) {
                        if (customerStatusEntry.sameId(customerStateEntity)) {
                            stateEntityList.add(customerStatusEntry);
                            break;
                        }
                    }
                }
            }
            return stateEntityList;
        } catch (Exception e) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    private List<CustomerStatusEntity> retrieveNextPossibleCustomerStateForClient(StateEntity customerStateEntityObj)
            throws ApplicationException {
        logger.debug("In AccountStateMachines::retrieveNextPossibleCustomerStateForClient()");
        List<CustomerStatusEntity> stateEntityList = new ArrayList<CustomerStatusEntity>();
        try {
            List<StateEntity> stateList = statesMapForClient.get(customerStateEntityObj);
            if (null != stateList) {
                for (StateEntity customerStateEntity : stateList) {
                    for (CustomerStatusEntity customerStatusEntry : customerStatusListForClient) {
                        if (customerStatusEntry.sameId(customerStateEntity)) {
                            stateEntityList.add(customerStatusEntry);
                            break;
                        }
                    }
                }
            }
            return stateEntityList;
        } catch (Exception e) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForLoan(StateEntity accountStateEntityObj)
            throws ApplicationException {
        logger.debug("In AccountStateMachines::retrieveNextPossibleAccountStateObjectsForLoan()");
        List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
        try {
            List<StateEntity> stateList = statesMapForLoan.get(accountStateEntityObj);
            if (null != stateList) {
                for (StateEntity accountStateEntity : stateList) {
                    for (AccountStateEntity accountStateEnty : accountStateEntityListForLoan) {
                        if (accountStateEntity.sameId(accountStateEnty)) {
                            stateEntityList.add(accountStateEnty);
                            break;
                        }
                    }
                }
            }
            return stateEntityList;
        } catch (Exception e) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    private List<AccountStateEntity> retrieveNextPossibleAccountStateObjectsForSavings(StateEntity accountStateEntityObj)
            throws ApplicationException {
        logger.debug("In AccountStateMachines::retrieveNextPossibleAccountStateObjectsForSavings()");
        List<AccountStateEntity> stateEntityList = new ArrayList<AccountStateEntity>();
        try {
            List<StateEntity> stateList = statesMapForSavings.get(accountStateEntityObj);
            if (null != stateList) {
                for (StateEntity accountStateEntity : stateList) {
                    for (AccountStateEntity accountStateEnty : accountStateEntityListForSavings) {
                        if (accountStateEntity.sameId(accountStateEnty)) {
                            stateEntityList.add(accountStateEnty);
                            break;
                        }
                    }
                }
            }
            return stateEntityList;
        } catch (Exception e) {
            throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    private List<AccountStateEntity> retrieveAllAccountStateList(AccountTypes accountType) throws ApplicationException {
        logger.debug("In AccountStateMachines::retrieveAllAccountStateList()");
        try {
            return new AccountBusinessService().retrieveAllAccountStateList(accountType);
        } catch (ServiceException e) {
            throw new ApplicationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
        }
    }

    private void populateLoanStatesViewMap() throws StatesInitializationException {
        logger.debug("In AccountStateMachines::populateLoanStatesViewMap()");
        if (null != statesMapForLoan) {
            Set<StateEntity> accountStateEntityKeySet = statesMapForLoan.keySet();
            try {
                if (null != accountStateEntityKeySet && null != accountStateEntityListForLoan) {
                    for (StateEntity accountStateEntity : accountStateEntityKeySet) {
                        for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityListForLoan) {
                            if (accountStateEntity.sameId(accountStateEntityQueryResultObj)) {
                                statesViewMapForLoan.put(accountStateEntityQueryResultObj.getId(),
                                        retrieveNextPossibleAccountStateObjectsForLoan(accountStateEntity));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
            }
        }
    }

    private void populateSavingsStatesViewMap() throws StatesInitializationException {
        logger.debug("In AccountStateMachines::populateSavingsStatesViewMap()");
        if (null != statesMapForSavings) {
            Set<StateEntity> accountStateEntityKeySet = statesMapForSavings.keySet();
            try {
                if (null != accountStateEntityKeySet && null != accountStateEntityListForSavings) {
                    for (StateEntity accountStateEntity : accountStateEntityKeySet) {
                        for (AccountStateEntity accountStateEntityQueryResultObj : accountStateEntityListForSavings) {
                            if (accountStateEntity.sameId(accountStateEntityQueryResultObj)) {
                                statesViewMapForSavings.put(accountStateEntityQueryResultObj.getId(),
                                        retrieveNextPossibleAccountStateObjectsForSavings(accountStateEntity));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new StatesInitializationException(SavingsConstants.STATEINITIALIZATION_EXCEPTION, e);
            }
        }
    }

    private Map<StateEntity, List<StateEntity>> loadMap(String filename, String configurationName) {
        return StateXMLParser.getInstance().loadMapFromXml(filename, configurationName);
    }
}
