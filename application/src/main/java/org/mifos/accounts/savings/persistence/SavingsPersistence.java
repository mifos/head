/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.accounts.savings.persistence;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.Money;

public class SavingsPersistence extends Persistence {

    private final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    public List<PrdOfferingView> getSavingsProducts(OfficeBO branch, CustomerLevelEntity customerLevel,
            short savingsTypeId) throws PersistenceException {
        logger.debug("In SavingsPersistence::getSavingsProducts(), customerLevelId: " + customerLevel.getId());
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDTYPEID, ProductType.SAVINGS.getValue());
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        queryParameters.put(AccountConstants.PRODUCT_APPLICABLE_TO, customerLevel.getProductApplicableType());
        return executeNamedQuery(NamedQueryConstants.GET_APPLICABLE_SAVINGS_PRODUCT_OFFERINGS, queryParameters);
    }

    public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(Short entityType)
            throws PersistenceException {
        logger.debug("In SavingsPersistence::retrieveCustomFieldsDefinition(), entityType: " + entityType);
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.ENTITY_TYPE, entityType);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
    }

    public SavingsBO findById(Integer accountId) throws PersistenceException {
        logger.debug("In SavingsPersistence::findById(), accountId: " + accountId);
        return (SavingsBO) getPersistentObject(SavingsBO.class, accountId);
    }

    public SavingsBO findBySystemId(String globalAccountNumber) throws PersistenceException {
        logger.debug("In SavingsPersistence::findBySystemId(), globalAccountNumber: " + globalAccountNumber);
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.GLOBAL_ACCOUNT_NUMBER, globalAccountNumber);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
        SavingsBO savings = queryResult == null ? null : (SavingsBO) queryResult;
        if (savings != null && savings.getRecommendedAmount() == null) {
            savings.setRecommendedAmount(new Money(savings.getCurrency()));
            initialize(savings.getAccountActionDates());
            initialize(savings.getAccountNotes());
            initialize(savings.getAccountFlags());
        }
        return savings;

    }

    public SavingsTrxnDetailEntity retrieveLastTransaction(Integer accountId, Date date) throws PersistenceException {

        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("accountId", accountId);
        queryParameters.put("date", date);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.RETRIEVE_LAST_TRXN, queryParameters);
        return queryResult == null ? null : (SavingsTrxnDetailEntity) queryResult;
    }

    public SavingsTrxnDetailEntity retrieveFirstTransaction(Integer accountId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("accountId", accountId);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.RETRIEVE_FIRST_TRXN, queryParameters);
        return queryResult == null ? null : (SavingsTrxnDetailEntity) queryResult;
    }

    public AccountStateEntity getAccountStatusObject(Short accountStatusId) throws PersistenceException {
        logger.debug("In SavingsPersistence::getAccountStatusObject(), accountStatusId: " + accountStatusId);
        return (AccountStateEntity) getPersistentObject(AccountStateEntity.class, accountStatusId);
    }

    public List<SavingsBO> getAllClosedAccount(Integer customerId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        List queryResult = executeNamedQuery(NamedQueryConstants.VIEWALLSAVINGSCLOSEDACCOUNTS, queryParameters);
        return queryResult;
    }

    public List<Integer> retreiveAccountsPendingForIntCalc(Date currentDate) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("currentDate", currentDate);
        List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_INT_CALC,
                queryParameters);
        return queryResult;
    }

    public List<Integer> retreiveAccountsPendingForIntPosting(Date currentDate) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("currentDate", currentDate);
        List<Integer> queryResult = executeNamedQuery(NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_INT_POST,
                queryParameters);
        return queryResult;
    }

    public int getMissedDeposits(Integer accountId, Date currentDate) throws PersistenceException {
        Integer count = 0;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", accountId);
        queryParameters.put("ACCOUNT_TYPE_ID", AccountTypes.SAVINGS_ACCOUNT.getValue());
        queryParameters.put("ACTIVE", AccountStates.SAVINGS_ACC_APPROVED);
        queryParameters.put("CHECKDATE", currentDate);
        queryParameters.put("PAYMENTSTATUS", PaymentStatus.UNPAID.getValue());

        List queryResult = executeNamedQuery(NamedQueryConstants.GET_MISSED_DEPOSITS_COUNT, queryParameters);

        if (null != queryResult && queryResult.size() > 0) {
            Object obj = queryResult.get(0);
            if (obj != null) {
                count = ((Number) obj).intValue();
            }
        }
        return count.intValue();
    }

    public int getMissedDepositsPaidAfterDueDate(Integer accountId) throws PersistenceException {
        Long count = 0L;
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", accountId);
        queryParameters.put("ACCOUNT_TYPE_ID", AccountTypes.SAVINGS_ACCOUNT.getValue());
        queryParameters.put("ACTIVE", AccountStates.SAVINGS_ACC_APPROVED);
        queryParameters.put("PAYMENTSTATUS", PaymentStatus.PAID.getValue());

        List queryResult = executeNamedQuery(NamedQueryConstants.GET_MISSED_DEPOSITS_PAID_AFTER_DUEDATE,
                queryParameters);

        if (null != queryResult && queryResult.size() > 0) {
            Object obj = queryResult.get(0);
            if (obj != null) {
                count = (Long) obj;
            }
        }
        return count.intValue();
    }

    public AccountBO getSavingsAccountWithAccountActionsInitialized(Integer accountId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("accountId", accountId);
        List obj = executeNamedQuery("accounts.retrieveSavingsAccountWithAccountActions", queryParameters);
        Object[] obj1 = (Object[]) obj.get(0);
        return (AccountBO) obj1[0];
    }

    public List<SavingsBO> getAllSavingsAccount() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();

        List<SavingsBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_SAVINGS_ACCOUNTS, queryParameters);
        return queryResult;

    }

    public void persistSavingAccounts(ClientBO clientBO) throws CustomerException {
        for (AccountBO account : clientBO.getAccounts()) {
            if (account.getType() == AccountTypes.SAVINGS_ACCOUNT && account.getGlobalAccountNum() == null) {
                try {
                    ((SavingsBO) account).save();
                } catch (AccountException ae) {
                    throw new CustomerException(ae);
                }
            }
        }
    }

    public void save(List<SavingsBO> savingsAccounts) {
        final Session session = getSession();
        for (SavingsBO savingsBO : savingsAccounts) {
            session.saveOrUpdate(savingsBO);
        }
    }
}
