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

package org.mifos.customers.checklist.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.checklist.business.AccountCheckListBO;
import org.mifos.customers.checklist.business.CheckListBO;
import org.mifos.customers.checklist.business.CustomerCheckListBO;
import org.mifos.customers.checklist.util.helpers.CheckListMasterView;
import org.mifos.customers.checklist.util.helpers.CheckListStatesView;
import org.mifos.framework.exceptions.PersistenceException;

public class CheckListPersistence extends MasterPersistence {

    public CheckListPersistence() {
    }

    public List<CheckListMasterView> getCheckListMasterData(Short localeId) throws PersistenceException {
        List<CheckListMasterView> checkListMaster = new ArrayList();
        List<CheckListMasterView> masterData = new ArrayList();

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        masterData = executeNamedQuery(NamedQueryConstants.MASTERDATA_CUSTOMER_CHECKLIST, queryParameters);
        for (CheckListMasterView checkListMasterDataView : masterData) {
            checkListMasterDataView.setIsCustomer(true);
        }
        checkListMaster.addAll(masterData);
        masterData = executeNamedQuery(NamedQueryConstants.MASTERDATA_PRODUCT_CHECKLIST, queryParameters);
        for (CheckListMasterView checkListMasterDataView : masterData) {
            checkListMasterDataView.setIsCustomer(false);
        }
        checkListMaster.addAll(masterData);
        masterData = null;
        return checkListMaster;
    }

    public List<CheckListStatesView> retrieveAllCustomerStatusList(Short levelId, Short localeId)
            throws PersistenceException {
        List<CheckListStatesView> checkListStatesView = new ArrayList<CheckListStatesView>();
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("LEVEL_ID", levelId);
        List<CustomerStatusEntity> queryResult = executeNamedQuery(
                NamedQueryConstants.CHECKLIST_GET_VALID_CUSTOMER_STATES, queryParameters);
        for (CustomerStatusEntity customerStatus : queryResult) {
            checkListStatesView.add(new CheckListStatesView(customerStatus.getId(), customerStatus.getName(),
                    customerStatus.getCustomerLevel().getId()));
        }
        return checkListStatesView;
    }

    public List<CheckListStatesView> retrieveAllAccountStateList(Short prdTypeId, Short localeId)
            throws PersistenceException {
        List<CheckListStatesView> checkListStatesView = new ArrayList<CheckListStatesView>();
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("prdTypeId", prdTypeId);
        List<AccountStateEntity> queryResult = executeNamedQuery(
                NamedQueryConstants.CHECKLIST_GET_VALID_ACCOUNT_STATES, queryParameters);
        for (AccountStateEntity accountStatus : queryResult) {
            checkListStatesView.add(new CheckListStatesView(accountStatus.getId(), accountStatus.getName(),
                    accountStatus.getPrdType().getProductTypeID()));
        }
        return checkListStatesView;
    }

    public CheckListBO getCheckList(Short checkListId) throws PersistenceException {
        return (CheckListBO) getPersistentObject(CheckListBO.class, checkListId);
    }

    public long isValidCheckListState(Short levelId, Short stateId, boolean isCustomer) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("levelId", levelId);
        queryParameters.put("stateId", stateId);
        if (isCustomer) {
            return (Long) execUniqueResultNamedQuery(NamedQueryConstants.CUSTOMER_VALIDATESTATE, queryParameters);
        } else {
            return (Long) execUniqueResultNamedQuery(NamedQueryConstants.PRODUCT_VALIDATESTATE, queryParameters);
        }
    }

    public List<CustomerCheckListBO> retreiveAllCustomerCheckLists() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.LOAD_ALL_CUSTOMER_CHECKLISTS, null);
    }

    public List<AccountCheckListBO> retreiveAllAccountCheckLists() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.LOAD_ALL_ACCOUNT_CHECKLISTS, null);
    }
}
