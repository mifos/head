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

package org.mifos.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.loan.persistance.LegacyLoanDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.config.exceptions.ConfigurationException;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.config.util.helpers.ConfigConstants;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientRules {
    public static final String ClientRulesCenterHierarchyExists = "ClientRules.CenterHierarchyExists";
    public static final String ClientRulesClientCanExistOutsideGroup = "ClientRules.ClientCanExistOutsideGroup";
    public static final String ClientRulesGroupCanApplyLoans = "ClientRules.GroupCanApplyLoans";
    public static final String ClientRulesNameSequence = "ClientRules.NameSequence";
    public static final String ClientCanExistOutsideGroupKey = "ClientCanExistOutsideGroup";
    public static final String GroupCanApplyLoansKey = "GroupCanApplyLoans";
    public static final String MaximumAgeForNewClients = "ClientRules.MaximumAgeForNewClients";
    public static final String MinimumAgeForNewClients = "ClientRules.MinimumAgeForNewClients";
    private static Boolean centerHierarchyExists;
    private static Boolean groupCanApplyLoans;
    private static Boolean clientCanExistOutsideGroup;
    private static int minimumAgeForNewClient;
    private static int maximumAgeForNewClient;
    private static boolean ageCheckEnabled;
    private static int maximumNumberOfFamilyMembers;
    public static int getMaximumNumberOfFamilyMembers() {
        return maximumNumberOfFamilyMembers;
    }

    public static void setMaximumNumberOfFamilyMembers(int maximumNumberOfFamilyMembers) {
        ClientRules.maximumNumberOfFamilyMembers = maximumNumberOfFamilyMembers;
    }

    private static boolean familyDetailsRequired=false;

    public static boolean isFamilyDetailsRequired() {
        return familyDetailsRequired;
    }

    public static void setFamilyDetailsRequired(boolean familyDetailsRequired) {
        ClientRules.familyDetailsRequired = familyDetailsRequired;
    }

    /**
     * A name sequence is the order in which client names are displayed.
     * Example: first name, then middle name, then last name.
     * <p>
     * This member variable stores which of the {@link #allowedNameParts} are to
     * be used when displaying a client's name.
     */
    private static String[] nameSequence;

    /**
     * Stores strings that are allowed to be part of {@link #nameSequence}.
     */
    private static Set<String> allowedNameParts;

    private static final Logger logger = LoggerFactory.getLogger(ClientRules.class);

    private static ConfigurationPersistence configPersistence;

    public static ConfigurationPersistence getConfigPersistence() {
        if (configPersistence == null) {
            configPersistence = new ConfigurationPersistence();
        }

        return configPersistence;
    }

    public static void setConfigPersistence(ConfigurationPersistence configPersistence) {
        ClientRules.configPersistence = configPersistence;
    }

    static {
        allowedNameParts = new HashSet<String>();
        allowedNameParts.add(ConfigConstants.FIRST_NAME);
        allowedNameParts.add(ConfigConstants.MIDDLE_NAME);
        allowedNameParts.add(ConfigConstants.LAST_NAME);
        allowedNameParts.add(ConfigConstants.SECOND_LAST_NAME);
    }

    /**
     * Performs startup sanity checks. While not a requirement, it is considered
     * good practice to call this method prior to any other methods in this
     * class.
     */
    public static void init() throws ConfigurationException {
        if (!isValidNameSequence()) {
            throw new ConfigurationException("error in configured value for " + ClientRulesNameSequence);
        }
        // If the configuration is invalid with respect to Client Rules, this
        // will force discovery of the problem upon initialization
        refresh();
    }

    // "protected" visibility so it can be unit tested; it would otherwise
    // be private
    protected static void refresh() throws ConfigurationException {
        centerHierarchyExists = null;
        groupCanApplyLoans = null;
        clientCanExistOutsideGroup = null;
        nameSequence = null;
        centerHierarchyExists = getCenterHierarchyExists();
        groupCanApplyLoans = getGroupCanApplyLoans();
        clientCanExistOutsideGroup = getClientCanExistOutsideGroup();
        nameSequence = getNameSequence();
        initializeAges();
        intializeFamilyConfig();
    }

    public static Boolean getCenterHierarchyExists() {
        if (centerHierarchyExists == null) {
            centerHierarchyExists = getCenterHierarchyExistsFromConfig();
        }
        return centerHierarchyExists;
    }

    public static void setCenterHierarchyExists(boolean exists) {
        centerHierarchyExists = exists;
    }

    public static void setGroupCanApplyLoans(boolean flag) {
        groupCanApplyLoans = flag;
    }

    public static void setClientCanExistOutsideGroup(boolean flag){
        clientCanExistOutsideGroup = flag;
    }


    /** Can group loans exist? */
    public static Boolean getGroupCanApplyLoans() {
        if (groupCanApplyLoans == null) {
            groupCanApplyLoans = getGroupCanApplyLoansFromConfig();
        }
        return groupCanApplyLoans;
    }

    public static boolean getClientCanExistOutsideGroup() {
        if (clientCanExistOutsideGroup == null) {
            clientCanExistOutsideGroup = getClientCanExistOutsideGroupFromConfig();
        }
        return clientCanExistOutsideGroup;
    }

    private static Boolean getCenterHierarchyExistsFromConfig() {
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        return configMgr.getBoolean(ClientRulesCenterHierarchyExists);
    }

    private static String getBadOverrideMsg(String key, String detailMsg) {
        return "The value for key " + key + " in the file " + MifosConfigurationManager.CUSTOM_CONFIG_PROPS_FILENAME
                + " must to be set to 1 because it was set to 1"
                + " in the database, hence can't be set to to 0 in the custom"
                + " configuration file as this might invalidate existing data. " + detailMsg + " Also, "
                + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME + " must never be changed--make sure this"
                + " file is untouched.";
    }

    private static boolean getGroupCanApplyLoansFromConfig() {
        boolean cfgValue;
        try {
            int dbValue = getConfigPersistence().getConfigurationKeyValueInteger(GroupCanApplyLoansKey).getValue();
            MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
            cfgValue = configMgr.getBoolean(ClientRulesGroupCanApplyLoans);

            if (dbValue == Constants.NO && cfgValue == true) {
                getConfigPersistence().updateConfigurationKeyValueInteger(GroupCanApplyLoansKey, Constants.YES);
            } else if (dbValue == Constants.YES && cfgValue == false) {

                if (ApplicationContextProvider.getBean(LegacyLoanDao.class).countOfGroupLoanAccounts() > 0) {

                    // Trying to override db value of "true/yes" with "false/no"
                    // in the config file violates business rules.
                    throw new MifosRuntimeException(getBadOverrideMsg(GroupCanApplyLoansKey, "Group loans may already exist."));
                }

                makeDatabaseConfigMatchFilebasedConfig();
            }
        } catch (PersistenceException ex) {
            throw new MifosRuntimeException(ex);
        }

        return cfgValue;
    }

    private static void makeDatabaseConfigMatchFilebasedConfig() throws PersistenceException {
        getConfigPersistence().updateConfigurationKeyValueInteger(GroupCanApplyLoansKey, Constants.NO);
    }

    private static boolean getClientCanExistOutsideGroupFromConfig() {
        boolean cfgValue;
        try {
            int dbValue = getConfigPersistence().getConfigurationKeyValueInteger(ClientCanExistOutsideGroupKey)
                    .getValue();
            MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
            cfgValue = configMgr.getBoolean(ClientRulesClientCanExistOutsideGroup);

            if (dbValue == Constants.NO && cfgValue == true) {
                getConfigPersistence().updateConfigurationKeyValueInteger(ClientCanExistOutsideGroupKey, Constants.YES);
            } else if (dbValue == Constants.YES && cfgValue == false) {
                // Trying to override db value of "true/yes" with "false/no"
                // in the config file violates business rules.
                throw new MifosRuntimeException(getBadOverrideMsg(ClientCanExistOutsideGroupKey, "Clients outside of groups may already exist."));
            }
        } catch (PersistenceException ex) {
            throw new MifosRuntimeException(ex);
        }

        return cfgValue;
    }

    public static void setNameSequence(String[] nameSequence) {
        ClientRules.nameSequence = nameSequence;
    }

    /*
     * Fetches and populates ClientRules#nameSequence.
     */
    public static String[] getNameSequence() {
        if (nameSequence == null) {
            MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
            nameSequence = configMgr.getStringArray(ClientRulesNameSequence);
        }
        return nameSequence;
    }

    /**
     * Check that the given nameSequence is valid.
     * <p>
     * A name sequence is the order in which client names are displayed.
     * Example: first name, then middle name, then last name.
     * <p>
     * Throws no exceptions, but reasons for invalid sequences are logged as
     * errors.
     */
    public static boolean isValidNameSequence(String[] nameSequence) {
        // a null or empty part would cause errors when a name format when, for
        // instance, ClientNameDetailDto.getDisplayName() is called
        if (null == nameSequence) {
            logger.error("nameSequence must not be null");
            return false;
        }
        if (nameSequence.length < 1) {
            logger.error("nameSequence must contain elements");
            return false;
        }

        // disallowed parts would cause errors when a name format when, for
        // instance, ClientNameDetailDto.getDisplayName() is called
        for (String s : nameSequence) {
            if (!allowedNameParts.contains(s)) {
                String allowed = StringUtils.join(allowedNameParts, ",");
                logger.error(s + " is not a known name part. Allowed values are: " + allowed
                        + ". Any ordering is allowed.");
                return false;
            }
        }

        return true;
    }

    /**
     * Delegates to {@link #isValidNameSequence(String[])} to check that the
     * configured <i>nameSequence</i> is valid. Automatically fetches and populates
     * <i>nameSequence</i> if necessary.
     * <p>
     * A name sequence is the order in which client names are displayed.
     * Example: first name, then middle name, then last name.
     */
    public static boolean isValidNameSequence() {
        return isValidNameSequence(getNameSequence());
    }

    /*
     * will initialize the client minimum age and maximum age constants, Also if
     * both of them are set to zero it will initialize the client constants.age
     * check to zero
     */
    public static void initializeAges() throws ConfigurationException {
        setMinimumAgeForNewClient(getMinimumAge());
        setMaximumAgeForNewClient(getMaximumAge());
        if (getMaximumAge() < getMinimumAge()) {
            throw new ConfigurationException("The minimum age for clients cannot be greater than the maximum age in "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME);
        }

        updateAgeCheckEnabled();
    }

    private static void updateAgeCheckEnabled() {
        if ((getMaximumAgeForNewClient() == 0) && (getMinimumAgeForNewClient() == 0)) {
            setAgeCheckEnabled(false);
        } else {
            setAgeCheckEnabled(true);
        }
    }

    public static int getMinimumAgeForNewClient() {
        return minimumAgeForNewClient;
    }

    public static void setMinimumAgeForNewClient(int minimumAgeForNewClient) {
        ClientRules.minimumAgeForNewClient = minimumAgeForNewClient;
        updateAgeCheckEnabled();
    }

    public static int getMaximumAgeForNewClient() {
        return maximumAgeForNewClient;
    }

    public static void setMaximumAgeForNewClient(int maximumAgeForNewClient) {
        ClientRules.maximumAgeForNewClient = maximumAgeForNewClient;
        updateAgeCheckEnabled();
    }

    public static boolean isAgeCheckEnabled() {
        return ageCheckEnabled;
    }

    public static void setAgeCheckEnabled(boolean ageCheckEnabled) {
        ClientRules.ageCheckEnabled = ageCheckEnabled;
    }

    /*
     * reads the maximum age specified in the application configuration file,
     * Also Checks if its in the range of 0 to 150
     */
    public static int getMinimumAge() throws ConfigurationException {
        int minimumAge = 0;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(ClientRules.MinimumAgeForNewClients)) {
            minimumAge = Integer.parseInt(configMgr.getString(ClientRules.MinimumAgeForNewClients));
        } else {
            throw new ConfigurationException("The Minimum Age for a client is not defined in "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME);
        }

        if (minimumAge < 0 || minimumAge > 150) {
            throw new ConfigurationException("The Minimum Age defined in the "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME
                    + "is not within the acceptable range (0 to 150)");
        }

        return minimumAge;

    }

    /*
     * Gets the minimum age specified in the application configuration file,
     * Also Checks if its in the range of 0 to 150
     */
    public static int getMaximumAge() throws ConfigurationException {
        int maximumAge = 0;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(ClientRules.MaximumAgeForNewClients)) {
            maximumAge = Integer.parseInt(configMgr.getString(ClientRules.MaximumAgeForNewClients));
        } else {
            throw new ConfigurationException("The Maximum Age for a client is not defined in "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME);
        }

        if (maximumAge > 150 || maximumAge < 0) {
            throw new ConfigurationException("The Maximum Age defined in the "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME
                    + "is not within the acceptable range (0 to 150)");
        }
        return maximumAge;

    }

    public static void intializeFamilyConfig() throws ConfigurationException {
        setFamilyDetailsRequired(ClientFamilyInfoConfig.getAreFamilyDetailsRequired());
        setMaximumNumberOfFamilyMembers(ClientFamilyInfoConfig.getMaximumNumberOfFamilyMembers());
    }
}