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

package org.mifos.accounts.financial.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.spring.SpringUtil;

/**
 * A Spring bean useful in resolving financial actions with their associated GL
 * (general ledger) codes. GL codes are unique to GL accounts.
 * <p>
 * Example use case: principal is added to a new loan (a credit) and GL accounts
 * appropriate for this action must be fetched. <a
 * href="http://www.mifos.org/knowledge/functional-specs/accounting-in-mifos"
 * >More use cases</a>.
 * <p>
 * Spring must be initialized prior to using this class. This is currently
 * performed via {@link SpringUtil#initializeSpring()}.
 */
public class FinancialRules {
    /** Values are general ledger account codes. */
    private Map<FinancialActionConstants, String> actionToDebitAccount = new HashMap<FinancialActionConstants, String>();
    /** Values are general ledger account codes. */
    private Map<FinancialActionConstants, String> actionToCreditAccount = new HashMap<FinancialActionConstants, String>();

    private static FinancialRules financialRules = new FinancialRules();

    public static final FinancialRules getInstance() {
        return financialRules;
    }

    public String getGLAccountForAction(short financialActionId, FinancialConstants type) throws FinancialException {
        FinancialActionConstants financialAction = FinancialActionConstants.getFinancialAction(financialActionId);
        return getGLAccountForAction(financialAction, type);
    }

    public String getGLAccountForAction(FinancialActionConstants financialAction, FinancialConstants type)
            throws FinancialException {
        if (type.equals(FinancialConstants.DEBIT)) {
            return actionToDebitAccount.get(financialAction);
        } else if (type.equals(FinancialConstants.CREDIT)) {
            return actionToCreditAccount.get(financialAction);
        } else {
            throw new IllegalArgumentException("Unrecognized FinancialConstants type: " + type
                    + ". Only DEBIT and CREDIT are allowed.");
        }
    }

    public Map<FinancialActionConstants, String> getActionToDebitAccount() {
        return actionToDebitAccount;
    }

    public void setActionToDebitAccount(Map<FinancialActionConstants, String> actionToDebitAccount) {
        this.actionToDebitAccount = actionToDebitAccount;
    }

    public Map<FinancialActionConstants, String> getActionToCreditAccount() {
        return actionToCreditAccount;
    }

    public void setActionToCreditAccount(Map<FinancialActionConstants, String> actionToCreditAccount) {
        this.actionToCreditAccount = actionToCreditAccount;
    }
}
