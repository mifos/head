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

import org.mifos.accounts.financial.business.FinancialActionTypeEntity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.financial.exceptions.FinancialExceptionConstants;

// TODO: why does this cache exist? Is FinancialActionConstants not enough?
public class FinancialActionCache {

    private static Map<Short, FinancialActionTypeEntity> financialCacheRepository = new HashMap<Short, FinancialActionTypeEntity>();

    public static void addToCache(FinancialActionTypeEntity financialAction) {
        if ((financialAction != null) && (financialCacheRepository.get(financialAction.getId()) == null)) {
            financialCacheRepository.put(financialAction.getId(), financialAction);
        }
    }

    public static FinancialActionTypeEntity getFinancialAction(FinancialActionConstants financialActionId)
            throws FinancialException {
        FinancialActionTypeEntity financialAction = financialCacheRepository.get(financialActionId.getValue());
        if (financialAction == null) {
            throw new FinancialException(FinancialExceptionConstants.ACTIONNOTFOUND);
        }

        return financialAction;
    }

}
