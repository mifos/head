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

package org.mifos.accounts.productdefinition.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.meeting.business.RecurrenceTypeEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.accounts.productdefinition.util.helpers.ProductType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class SavingsPrdPersistence extends Persistence {

    public SavingsOfferingBO getSavingsProduct(Short prdOfferingId) throws PersistenceException {
        return (SavingsOfferingBO) getPersistentObject(SavingsOfferingBO.class, prdOfferingId);
    }

    public List<SavingsBO> retrieveSavingsAccountsForPrd(Short prdOfferingId) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(ProductDefinitionConstants.PRDOFFERINGID, prdOfferingId);
        return executeNamedQuery(NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT, queryParameters);
    }

    public Short retrieveDormancyDays() throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("productTypeId", ProductType.SAVINGS.getValue());
        Object obj = execUniqueResultNamedQuery(NamedQueryConstants.GET_DORMANCY_DAYS, queryParameters);
        return obj != null ? (Short) obj : null;
    }

    public List<RecurrenceTypeEntity> getSavingsApplicableRecurrenceTypes() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.SAVINGS_APPL_RECURRENCETYPES, queryParameters);
    }

    public List<SavingsOfferingBO> getAllSavingsProducts() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        return executeNamedQuery(NamedQueryConstants.GET_ALLSAVINGS_PRODUCTS, queryParameters);
    }

    public List<SavingsOfferingBO> getAllActiveSavingsProducts() throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());
        return executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVE_SAVINGS_PRODUCTS, queryParameters);
    }

    public List<SavingsOfferingBO> getSavingsOfferingsNotMixed(Short localeId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE.getValue());

        List<SavingsOfferingBO> savingsOfferings = executeNamedQuery(
                NamedQueryConstants.PRODUCT_NOTMIXED_SAVING_PRODUCTS, queryParameters);
        if (null != savingsOfferings && savingsOfferings.size() > 0) {
            for (SavingsOfferingBO savingOffering : savingsOfferings) {
                savingOffering.getPrdStatus().getPrdState().setLocaleId(localeId);
            }
        }
        return savingsOfferings;
    }

}
