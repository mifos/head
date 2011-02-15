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

package org.mifos.framework.util.helpers;

import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;

public class TestGeneralLedgerCode {
    static LegacyAccountDao legacyAccountDao = ApplicationContextProvider.getBean(LegacyAccountDao.class);

    public static final Short ASSETS = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("10000");
    public static final Short CASH_AND_BANK_BALANCES = legacyAccountDao
            .getAccountIdFromGlCodeDuringInitialization("11000");
    public static final Short BANK_ACCOUNT_ONE = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("11201");
    public static final Short LOANS_TO_CLIENTS = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("13101");
    public static final Short FEES = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("31301");
    public static final Short INTEREST_ON_LOANS = legacyAccountDao
            .getAccountIdFromGlCodeDuringInitialization("31101");
    public static final Short MARGIN_MONEY_ONE = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("4602");
    public static final Short INTEREST_CLIENT_MANDATORY_SAVINGS = legacyAccountDao
            .getAccountIdFromGlCodeDuringInitialization("41102");
    public static final Short MANAGED_ICICI_SPLOAN = legacyAccountDao
            .getAccountIdFromGlCodeDuringInitialization("1504");
    public static final Short INTEREST = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("5001");
    public static final Short COST_OF_FUNDS = legacyAccountDao.getAccountIdFromGlCodeDuringInitialization("41100");
}
