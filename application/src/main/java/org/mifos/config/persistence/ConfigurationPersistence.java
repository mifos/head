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

package org.mifos.config.persistence;

import static org.mifos.accounts.loan.util.helpers.LoanConstants.REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.business.ConfigurationKeyValueInteger;
import org.mifos.config.util.helpers.ConfigConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

/**
 * This class concerns certain configuration settings, especially
 * {@link ConfigurationKeyValueInteger} and friends.
 */
public class ConfigurationPersistence extends Persistence {
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

    private static final String KEY_QUERY_PARAMETER = "KEY";

    public static final String CONFIGURATION_KEY_JASPER_REPORT_IS_HIDDEN = ConfigConstants.JASPER_REPORT_IS_HIDDEN;

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

    /**
     * Lookup an integer valued, persistent configuration key-value pair based
     * on the key. This is intended to be more of a helper method than to be
     * used directly.
     *
     * @return if the key is found return the corresponding
     *         ConfigurationKeyValueInteger, if not then return null.
     */
    public ConfigurationKeyValueInteger getConfigurationKeyValueInteger(String key) throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(KEY_QUERY_PARAMETER, key);
        ConfigurationKeyValueInteger keyValue = (ConfigurationKeyValueInteger) execUniqueResultNamedQuery(
                NamedQueryConstants.GET_CONFIGURATION_KEYVALUE_BY_KEY, queryParameters);
        return keyValue;
    }

    /**
     * Lookup a known persistent integer configuration value.
     *
     * @throws RuntimeException
     *             thrown if no value is found for the key.
     */
    public int getConfigurationValueInteger(String key) throws PersistenceException {
        ConfigurationKeyValueInteger keyValue = getConfigurationKeyValueInteger(key);
        if (keyValue != null) {
            return keyValue.getValue();
        } else {
            throw new RuntimeException("Configuration parameter not found for key: " + "'" + key + "'");
        }
    }

    /**
     * Update the value of a persistent integer configuration value;
     */
    public void updateConfigurationKeyValueInteger(String key, int value) throws PersistenceException {
        ConfigurationKeyValueInteger keyValue = getConfigurationKeyValueInteger(key);
        keyValue.setValue(value);
        createOrUpdate(keyValue);
    }

    /**
     * Create a new persistent integer configuration key value pair.
     */
    public void addConfigurationKeyValueInteger(String key, int value) throws PersistenceException {
        ConfigurationKeyValueInteger keyValue = new ConfigurationKeyValueInteger(key, value);
        createOrUpdate(keyValue);
    }

    /**
     * Delete a persistent integer configuration key value pair.
     */
    public void deleteConfigurationKeyValueInteger(String key) throws PersistenceException {
        ConfigurationKeyValueInteger keyValue = getConfigurationKeyValueInteger(key);
        delete(keyValue);
    }

    /**
     * Helper method for loan repayments independent of meeting schedule. TODO
     * Find a better home for me
     */
    public boolean isRepaymentIndepOfMeetingEnabled() throws PersistenceException {
        Integer repIndepOfMeetingEnabled = getConfigurationKeyValueInteger(
                REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED).getValue();
        return !(repIndepOfMeetingEnabled == null || repIndepOfMeetingEnabled == 0);
    }

    public List<ConfigurationKeyValueInteger> getAllConfigurationKeyValueIntegers() throws PersistenceException {
        return executeNamedQuery(NamedQueryConstants.GET_ALL_CONFIGURATION_VALUES, Collections.EMPTY_MAP);
    }

    public boolean isGlimEnabled() throws PersistenceException {
        return (getConfigurationValueInteger(LoanConstants.LOAN_INDIVIDUAL_MONITORING_IS_ENABLED) == LoanConstants.GLIM_ENABLED_VALUE);
    }
}
