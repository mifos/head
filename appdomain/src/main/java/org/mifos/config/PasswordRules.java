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

package org.mifos.config;

import org.mifos.config.business.MifosConfigurationManager;
import org.mifos.config.exceptions.ConfigurationException;

public class PasswordRules {
    public static final String PASSWORD_EXPIRATION_DATE_PRELONGATION = "PasswordRules.PasswordExpirationDatePrelongation";
    public static final String PASSWORD_HISTORY_COUNT = "PasswordRules.PasswordHistoryCount";
    public static final String MIN_PASSWORD_LENGTH = "PasswordRules.MinPasswordLength";
    public static final String MUST_CONTAIN_DIGIT = "PasswordRules.MustContainDigit";
    public static final String MUST_CONTAIN_SPECIAL = "PasswordRules.MustContainSpecial";
    public static final String MUST_CONTAIN_BOTH_CASE_LETTERS = "PasswordRules.MustContainBothCaseLetters";

    private static int passwordExpirationDatePrelongation;
    private static int passwordHistoryCount;
    private static int minPasswordLength;
    private static boolean mustContainDigit;
    private static boolean mustContainSpecial;
    private static boolean mustContainBothCaseLetters;

    public static void init() throws ConfigurationException {
    	passwordExpirationDatePrelongation = getPasswordExpirationDatePrelongationFromConfig();
    	passwordHistoryCount = getPasswordHistoryCountFromConfig();
    	minPasswordLength = getMinPasswordLengthFromConfig();
    	mustContainDigit = isMustContainDigitFromConfig();
    	mustContainSpecial = isMustContainSpecialFromConfig();
    	mustContainBothCaseLetters = isMustContainBothCaseLettersFromConfig();
    }
    
    public static int getPasswordExpirationDatePrelongation() {
		return passwordExpirationDatePrelongation;
	}

	public static int getPasswordHistoryCount() {
		return passwordHistoryCount;
	}

	public static int getMinPasswordLength() {
		return minPasswordLength;
	}

	public static boolean isMustContainDigit() {
		return mustContainDigit;
	}

	public static boolean isMustContainSpecial() {
		return mustContainSpecial;
	}

	public static boolean isMustContainBothCaseLetters() {
		return mustContainBothCaseLetters;
	}

	private static int getPasswordExpirationDatePrelongationFromConfig() throws ConfigurationException {
    	int passwordExpirationDateProlongation = 90;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(PASSWORD_EXPIRATION_DATE_PRELONGATION)) {
            passwordExpirationDateProlongation = Integer.parseInt(configMgr.getString(PASSWORD_EXPIRATION_DATE_PRELONGATION));
        }

        if (passwordExpirationDateProlongation < 0) {
            throw new ConfigurationException("The PasswordRules.PasswordExpirationDatePrelongation defined in the "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME
                    + " must be a positive numer.");
        }

        return passwordExpirationDateProlongation;
    }
    
	private static int getPasswordHistoryCountFromConfig() throws ConfigurationException {
    	int passwordHistoryCount = 6;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(PASSWORD_HISTORY_COUNT)) {
        	passwordHistoryCount = Integer.parseInt(configMgr.getString(PASSWORD_HISTORY_COUNT));
        }

        if (passwordHistoryCount < 1 || passwordHistoryCount > 20) {
            throw new ConfigurationException("The PasswordRules.PasswordHistoryCount defined in the "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME
                    + " must be a number between 0 and 20.");
        }

        return passwordHistoryCount;
    }
    
	private static int getMinPasswordLengthFromConfig() throws ConfigurationException {
    	int minPasswordLength = 6;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(MIN_PASSWORD_LENGTH)) {
        	minPasswordLength = Integer.parseInt(configMgr.getString(MIN_PASSWORD_LENGTH));
        }

        if (minPasswordLength < 3 || minPasswordLength > 20) {
            throw new ConfigurationException("The PasswordRules.MinPasswordLength defined in the "
                    + MifosConfigurationManager.DEFAULT_CONFIG_PROPS_FILENAME
                    + " must be a number between 3 and 20.");
        }

        return minPasswordLength;
    }
    
	private static boolean isMustContainDigitFromConfig() throws ConfigurationException {
    	boolean isMustContainDigit = true;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(MUST_CONTAIN_DIGIT)) {
        	isMustContainDigit = Boolean.parseBoolean(configMgr.getString(MUST_CONTAIN_DIGIT));
        }
        return isMustContainDigit;
    }
    
	private static boolean isMustContainSpecialFromConfig() throws ConfigurationException {
    	boolean isMustContainSpecial = true;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(MUST_CONTAIN_SPECIAL)) {
        	isMustContainSpecial = Boolean.parseBoolean(configMgr.getString(MUST_CONTAIN_SPECIAL));
        }
        return isMustContainSpecial;
    }

	private static boolean isMustContainBothCaseLettersFromConfig() throws ConfigurationException {
    	boolean isMustContainBothCase = true;
        MifosConfigurationManager configMgr = MifosConfigurationManager.getInstance();
        if (configMgr.containsKey(MUST_CONTAIN_BOTH_CASE_LETTERS)) {
        	isMustContainBothCase = Boolean.parseBoolean(configMgr.getString(MUST_CONTAIN_BOTH_CASE_LETTERS));
        }
        return isMustContainBothCase;
    }
    
}