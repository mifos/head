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

package org.mifos.accounts.financial.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.accounts.financial.business.COABO;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.exceptions.FinancialExceptionConstants;

/**
 * TODO: make this into, for instance, a Spring-managed bean instead of a static
 * class. This would make testing cleaner.
 */
public class ChartOfAccountsCache {
    /** Keys are general ledger account codes, like "10000". */
    private static Map<String, COABO> cache = new HashMap<String, COABO>();

    public static boolean isInitialized() {
        return !cache.isEmpty();
    }

    public static void add(COABO coa) {
        if (coa == null) {
            throw new RuntimeException("Got a null coa reference");
        }
        if (cache.get(coa.getGlCode()) == null) {
            cache.put(coa.getGlCode(), coa);
        } else {
            throw new RuntimeException("ChartOfAcctionsCache already contains" + " an account with gl code: "
                    + coa.getGlCode() + ". name is: " + coa.getAccountName() + ", id is: " + coa.getAccountId());
        }
    }

    /**
     * Required for unit tests.
     */
    protected static COABO remove(COABO key) {
        return cache.remove(key.getGlCode());
    }

    public static COABO get(String glCode) throws FinancialException {
        COABO glAccount = cache.get(glCode);
        if (glAccount == null) {
            throw new FinancialException(FinancialExceptionConstants.ACCOUNT_NOT_FOUND, new String[] { glCode });
        }

        return glAccount;
    }

}
