/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.config.persistence;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED;
import static org.mifos.accounts.util.helpers.AccountConstants.MONTH_CLOSING_DAY_CONFIG_KEY;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.config.util.helpers.ConfigConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.LegacyGenericDao;

/**
 * This class concerns certain configuration settings, especially
 * {@link ConfigurationKeyValue} and friends.
 */
public class ConfigurationPersistence extends LegacyGenericDao {

    private static final String KEY_QUERY_PARAMETER = "KEY";

    public static final String CONFIGURATION_KEY_JASPER_REPORT_IS_HIDDEN = ConfigConstants.JASPER_REPORT_IS_HIDDEN;

    @SuppressWarnings("unchecked")
    public MifosCurrency getCurrency(String currencyCode) throws RuntimeException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("currencyCode", currencyCode);
        List queryResult;
        try {
            queryResult = executeNamedQuery(NamedQueryConstants.GET_CURRENCY, queryParameters);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (queryResult.size() == 0) {
            return null;
        }
        if (queryResult.size() > 1) {
            throw new RuntimeException("Multiple currencies found for currency code: " + currencyCode);
        }
        return (MifosCurrency) queryResult.get(0);
    }

    public ConfigurationKeyValue getConfigurationKeyValue(String key) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(KEY_QUERY_PARAMETER, key);
        try {
        	return (ConfigurationKeyValue) execUniqueResultNamedQuery(
        			NamedQueryConstants.GET_CONFIGURATION_KEYVALUE_BY_KEY, queryParameters);
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }
    
    /**
     * Lookup a known persistent integer configuration value.
     *
     * @throws RuntimeException
     *             thrown if no value is found for the key.
     */
    public int getConfigurationValueInteger(String key) {
        ConfigurationKeyValue keyValue = getConfigurationKeyValue(key);

        if (keyValue != null && ConfigurationKeyValue.Type.INTEGER.getTypeId().equals(keyValue.getType())) {
            return Integer.parseInt(keyValue.getValue());
        }

        throw new RuntimeException("Configuration parameter not found for key: " + "'" + key + "'");
    }

    /**
     * Update the value of a persistent integer configuration value;
     */
    public void updateConfigurationKeyValueInteger(String key, int value) throws PersistenceException {
        ConfigurationKeyValue keyValue = getConfigurationKeyValue(key);

        if (keyValue != null && ConfigurationKeyValue.Type.INTEGER.getTypeId().equals(keyValue.getType())) {
            keyValue.setValue(Integer.toString(value));
            createOrUpdate(keyValue);
        }
        else {
            throw new RuntimeException("Configuration parameter not found for key: " + "'" + key + "'");
        }
    }

    /**
     * Create a new persistent integer configuration key value pair.
     */
    public void addConfigurationKeyValueInteger(String key, int value) throws PersistenceException {
        ConfigurationKeyValue keyValue = new ConfigurationKeyValue(key, value);
        createOrUpdate(keyValue);
    }

    /**
     * Delete a persistent configuration key value pair.
     *
     * If the key doesn't exist, then nothing happens.
     *
     * @param key ConfigurationKeyValue key
     *
     * @throws PersistenceException when the ConfigurationKeyValue cannot be deleted
     */
    public void deleteConfigurationKeyValue(String key) throws PersistenceException {
        ConfigurationKeyValue keyValue = getConfigurationKeyValue(key);
        if (keyValue != null) {
            delete(keyValue);
        }
    }

    /**
     * Helper method for loan repayments independent of meeting schedule.
     */
    public boolean isRepaymentIndepOfMeetingEnabled() {
        return getConfigurationValueInteger(REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED) != 0;
    }

    @SuppressWarnings("unchecked")
    public List<ConfigurationKeyValue> getAllConfigurationKeyValues() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.GET_ALL_CONFIGURATION_VALUES, Collections.EMPTY_MAP);
    }

    public boolean isGlimEnabled() {
        return (getConfigurationValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED) == LoanConstants.GLIM_ENABLED_VALUE);
    }
    
    public boolean isMonthClosingDaySet(){
    	return getConfigurationKeyValue(MONTH_CLOSING_DAY_CONFIG_KEY) != null; 
    }
    
    public void createOrUpdateConfigurationKeyValueString(String key, String value) throws PersistenceException {
        ConfigurationKeyValue keyValue = getConfigurationKeyValue(key);

        if (keyValue == null) {
            keyValue = new ConfigurationKeyValue(key, value);
        }
        else if (ConfigurationKeyValue.Type.TEXT.getTypeId().equals(keyValue.getType())) {
            keyValue.setValue(value);
        }
        else {
            throw new RuntimeException("Invalid configuration type for key: " + "'" + key + "'");
        }

        createOrUpdate(keyValue);
    }
    
    public int getConfigurationValueIntegerWithoutFlush(String key) {
    	ConfigurationKeyValue keyValue = getConfigurationKeyValueWithoutFlush(key);

        if (keyValue != null && ConfigurationKeyValue.Type.INTEGER.getTypeId().equals(keyValue.getType())) {
            return Integer.parseInt(keyValue.getValue());
        }

        throw new RuntimeException("Configuration parameter not found for key: " + "'" + key + "'");
    }

    public ConfigurationKeyValue getConfigurationKeyValueWithoutFlush(String key) {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(KEY_QUERY_PARAMETER, key);
		try {
			return (ConfigurationKeyValue) execUniqueResultNamedQueryWithoutFlush(
					NamedQueryConstants.GET_CONFIGURATION_KEYVALUE_BY_KEY,
					queryParameters);
		} catch (PersistenceException e) {
			throw new MifosRuntimeException(e);
		}
    }
}
