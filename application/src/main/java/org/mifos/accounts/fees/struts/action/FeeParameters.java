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

package org.mifos.accounts.fees.struts.action;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.fees.business.CategoryTypeEntity;
import org.mifos.accounts.fees.business.FeeFormulaEntity;
import org.mifos.accounts.fees.business.FeeFrequencyTypeEntity;
import org.mifos.accounts.fees.business.FeePaymentEntity;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;

public class FeeParameters implements Serializable{
    private Map<String, String> categories;
    private Map<String, String> timesOfCharging;
    private Map<String, String> timesOfChargingCustomers;
    private Map<String, String> formulas;
    private Map<String, String> frequencies;
    private Map<String, String> glCodes;
    private Map<String, String> recurrenceTypes;
    private boolean multiCurrencyEnabled;
    private Map<String, String> currencies;


    public Map<String, String> getCurrencies() {
        return this.currencies;
    }

    public boolean isMultiCurrencyEnabled() {
        return this.multiCurrencyEnabled;
    }

    public Map<String, String> getCategories() {
        return this.categories;
    }

    public Map<String, String> getTimesOfCharging() {
        return this.timesOfCharging;
    }

    public Map<String, String> getTimesOfChargingCustomers() {
        return this.timesOfChargingCustomers;
    }

    public Map<String, String> getFormulas() {
        return this.formulas;
    }

    public Map<String, String> getFrequencies() {
        return this.frequencies;
    }

    public Map<String, String> getGlCodes() {
        return this.glCodes;
    }

    public Map<String, String> getRecurrenceTypes() {
        return this.recurrenceTypes;
    }

    public FeeParameters(List<CategoryTypeEntity> categories, List<FeePaymentEntity> timesOfCharging,
            List<MasterDataEntity> timesOfChargeingCustomer, List<FeeFormulaEntity> formulas,
            List<FeeFrequencyTypeEntity> frequencies, List<GLCodeEntity> glCodes) {
        this.categories = listToMap(categories);
        this.timesOfCharging = listToMap(timesOfCharging);
        this.timesOfChargingCustomers = listToMap(timesOfChargeingCustomer);
        this.formulas = listToMap(formulas);
        this.frequencies = listToMap(frequencies);
        this.glCodes = glCodesToMap(glCodes);
        //This is not provided by feeservicefacade.getFeeParameters ??
        this.recurrenceTypes = getFeeRecurrenceTypesMap();
        this.multiCurrencyEnabled = AccountingRules.isMultiCurrencyEnabled();
        this.currencies = getCurrenciesMap();

    }

    private Map<String, String> getCurrenciesMap() {
        Map<String, String> currencyMap = new HashMap<String, String>();
        for (MifosCurrency currency : AccountingRules.getCurrencies()) {
            currencyMap.put(currency.getCurrencyId().toString(), currency.getCurrencyCode());
        }
        return currencyMap;
    }

    private Map<String, String> getFeeRecurrenceTypesMap() {
        Map<String, String> recurrTypes = new HashMap<String, String>();
        /*for (RecurrenceType type : RecurrenceType.values()) {
            recurrTypes.put(type.getValue().toString(), type.name());
        }*/
        recurrTypes.put(RecurrenceType.WEEKLY.toString(), RecurrenceType.WEEKLY.name());
        recurrTypes.put(RecurrenceType.MONTHLY.toString(), RecurrenceType.MONTHLY.name());
        return recurrTypes;
    }

    private Map<String, String> glCodesToMap(List<GLCodeEntity> glCodes) {
        Map<String, String> idCodeMap = new HashMap<String, String>();
        for (GLCodeEntity glCode : glCodes) {
            idCodeMap.put(glCode.getGlcodeId().toString(), glCode.getGlcode());
        }
        return idCodeMap;
    }

    private Map<String, String> listToMap(List<? extends MasterDataEntity> masterDataEntityList) {
        Map<String, String> idNameMap = new HashMap<String, String>();
        for (MasterDataEntity masterDataEntity : masterDataEntityList) {
            idNameMap.put(masterDataEntity.getId().toString(), masterDataEntity.getName());
        }
        return idNameMap;
    }

}
