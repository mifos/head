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

package org.mifos.accounts.penalties.util.helpers;

import org.mifos.config.LocalizedTextLookup;
import org.mifos.framework.exceptions.PropertyNotFoundException;

public enum PenaltyFormula implements LocalizedTextLookup {
    OUTSTANDING_PRINCIPAL_AMOUNT(1, "PenaltyFormula-OutstandingPrincipalAmount"),
    OUTSTANDING_LOAN_AMOUNT(2, "PenaltyFormula-OutstandingLoanAmount"),
    OVERDUE_AMOUNT_DUE(3, "PenaltyFormula-OverdueAmountDue"),
    OVERDUE_PRINCIPAL(4, "PenaltyFormula-OverduePrincipal");

    Short value;
    String messageKey;

    private PenaltyFormula(int value, String key) {
        this.value = (short) value;
        this.messageKey = key;
    }

    public Short getValue() {
        return value;
    }

    public static PenaltyFormula getPenaltyFormula(Short value) throws PropertyNotFoundException {
        for (PenaltyFormula formula : PenaltyFormula.values()) {
            if (formula.getValue().equals(value)) {
                return formula;
            }
        }
        throw new PropertyNotFoundException("PenaltyFormula");
    }

    @Override
    public String getPropertiesKey() {
        return messageKey;
    }
}
