/**
 * Copyright (c) 2005-2007 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.config;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.helpers.Constants;

public class ClientRules {
	public static final String ClientRulesCenterHierarchyExists = "ClientRules.CenterHierarchyExists";
	public static final String ClientRulesClientCanExistOutsideGroup = "ClientRules.ClientCanExistOutsideGroup";
	public static final String ClientRulesGroupCanApplyLoans = "ClientRules.GroupCanApplyLoans";
	public static final String ClientRulesNameSequence = "ClientRules.NameSequence";
	public static final String ClientCanExistOutsideGroupKey = "ClientCanExistOutsideGroup";
	public static final String GroupCanApplyLoansKey = "GroupCanApplyLoans";
	private static Boolean centerHierarchyExists;
	private static Boolean groupCanApplyLoans;
	private static Boolean clientCanExistOutsideGroup;

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

	private static MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.CONFIGURATION_LOGGER);

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
		if (!isValidNameSequence())
			throw new ConfigurationException("error in configured value for "
					+ ClientRulesNameSequence);
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
	}

	public static Boolean getCenterHierarchyExists() {
		if (centerHierarchyExists == null)
			centerHierarchyExists = getCenterHierarchyExistsFromConfig();
		return centerHierarchyExists;
	}

	/** Can group loans exist? */
	public static Boolean getGroupCanApplyLoans() throws ConfigurationException {
		if (groupCanApplyLoans == null)
			groupCanApplyLoans = getGroupCanApplyLoansFromConfig();
		return groupCanApplyLoans;
	}

	public static Boolean getClientCanExistOutsideGroup()
			throws ConfigurationException {
		if (clientCanExistOutsideGroup == null)
			clientCanExistOutsideGroup = getClientCanExistOutsideGroupFromConfig();
		return clientCanExistOutsideGroup;
	}

	private static Boolean getCenterHierarchyExistsFromConfig() {
		ConfigurationManager configMgr = ConfigurationManager.getInstance();
		return configMgr.getBoolean(ClientRulesCenterHierarchyExists);
	}

	private static String getBadOverrideMsg(String key, String detailMsg) {
		return "The value for key " + key + " in the file "
				+ ConfigurationManager.CUSTOM_CONFIG_PROPS_FILENAME
				+ " must to be set to 1 because it was set to 1"
				+ " in the database, hence can't be set to to 0 in the custom"
				+ " configuration file as this might invalidate existing data. "
				+ detailMsg + " Also, "
				+ ConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME
				+ " must never be changed--make sure this"
				+ " file is untouched.";
	}

	private static boolean getGroupCanApplyLoansFromConfig()
			throws ConfigurationException {
		ConfigurationPersistence configPersistence = new ConfigurationPersistence();
		boolean cfgValue;
		try {
			int dbValue = configPersistence.getConfigurationKeyValueInteger(
					GroupCanApplyLoansKey).getValue();
			ConfigurationManager configMgr = ConfigurationManager.getInstance();
			cfgValue = configMgr.getBoolean(ClientRulesGroupCanApplyLoans);

			if (dbValue == Constants.NO && cfgValue == true) {
				configPersistence.updateConfigurationKeyValueInteger(
						GroupCanApplyLoansKey, Constants.YES);
			}
			else if (dbValue == Constants.YES && cfgValue == false) {
				// Trying to override db value of "true/yes" with "false/no"
				// in the config file violates business rules.
				throw new ConfigurationException(
						getBadOverrideMsg(GroupCanApplyLoansKey,
								"Group loans may already exist."));
			}
		}
		catch (PersistenceException ex) {
			throw new ConfigurationException(ex);
		}

		return cfgValue;
	}

	private static boolean getClientCanExistOutsideGroupFromConfig()
			throws ConfigurationException {
		ConfigurationPersistence configPersistence = new ConfigurationPersistence();
		boolean cfgValue;
		try {
			int dbValue = configPersistence.getConfigurationKeyValueInteger(
					ClientCanExistOutsideGroupKey).getValue();
			ConfigurationManager configMgr = ConfigurationManager.getInstance();
			cfgValue = configMgr
					.getBoolean(ClientRulesClientCanExistOutsideGroup);

			if (dbValue == Constants.NO && cfgValue == true) {
				configPersistence.updateConfigurationKeyValueInteger(
						ClientCanExistOutsideGroupKey, Constants.YES);
			}
			else if (dbValue == Constants.YES && cfgValue == false) {
				// Trying to override db value of "true/yes" with "false/no"
				// in the config file violates business rules.
				throw new ConfigurationException(getBadOverrideMsg(
						ClientCanExistOutsideGroupKey,
						"Clients outside of groups may already exist."));
			}
		}
		catch (PersistenceException ex) {
			throw new ConfigurationException(ex);
		}

		return cfgValue;
	}

	/**
	 * Fetches and populates {@link #nameSequence}.
	 */
	public static String[] getNameSequence() {
		if (nameSequence == null) {
			ConfigurationManager configMgr = ConfigurationManager.getInstance();
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
		// instance, ClientNameDetailView.getDisplayName() is called
		if (null == nameSequence) {
			logger.error("nameSequence must not be null");
			return false;
		}
		if (nameSequence.length < 1) {
			logger.error("nameSequence must contain elements");
			return false;
		}

		// disallowed parts would cause errors when a name format when, for
		// instance, ClientNameDetailView.getDisplayName() is called
		for (String s : nameSequence) {
			if (!allowedNameParts.contains(s)) {
				String allowed = StringUtils.join(allowedNameParts, ",");
				logger.error(s
						+ " is not a known name part. Allowed values are: "
						+ allowed + ". Any ordering is allowed.");
				return false;
			}
		}

		return true;
	}

	/**
	 * Delegates to {@link #isValidNameSequence(String[])} to check that the
	 * configured nameSequence is valid. Automatically fetches and populates
	 * {@link #nameSequence} if necessary.
	 * <p>
	 * A name sequence is the order in which client names are displayed.
	 * Example: first name, then middle name, then last name.
	 */
	public static boolean isValidNameSequence() {
		return isValidNameSequence(getNameSequence());
	}
}
