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
package org.mifos.platform.cashflow;

import org.joda.time.DateTime;
import org.joda.time.Months;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class CashFlowHelper {

    private static final int FIRST_DAY = 1;
    private static final int EXTRA_DURATION_FOR_CASHFLOW_SCHEDULE = 1;

    public void prepareCashFlowContext(String joinUrl, String cancelUrl, DateTime disbursementDate,
                                       DateTime lastInstallmentDueDate, HttpSession session) {
        Map<String, Integer> cashFlowBoundaries = getCashFlowBoundaries(disbursementDate, lastInstallmentDueDate);
        session.setAttribute(CashFlowConstants.START_MONTH, cashFlowBoundaries.get(CashFlowConstants.START_MONTH));
        session.setAttribute(CashFlowConstants.START_YEAR, cashFlowBoundaries.get(CashFlowConstants.START_YEAR));
        session.setAttribute(CashFlowConstants.NO_OF_MONTHS, cashFlowBoundaries.get(CashFlowConstants.NO_OF_MONTHS));
        session.setAttribute(CashFlowConstants.JOIN_URL, joinUrl);
        session.setAttribute(CashFlowConstants.CANCEL_URL, cancelUrl);
    }

    public Map<String, Integer> getCashFlowBoundaries(DateTime disbursementDate, DateTime lastInstallmentDueDate) {
        DateTime monthAfterLastInstallment = lastInstallmentDueDate.plusMonths(EXTRA_DURATION_FOR_CASHFLOW_SCHEDULE).withDayOfMonth(FIRST_DAY);

        DateTime monthBeforeDisbursalDate = disbursementDate.minusMonths(EXTRA_DURATION_FOR_CASHFLOW_SCHEDULE).withDayOfMonth(FIRST_DAY);
        int noOfMonths = Months.monthsBetween(monthBeforeDisbursalDate, monthAfterLastInstallment).getMonths() + 1;

        Map<String, Integer> cashFlowBoundaries = new HashMap<String, Integer>();
        cashFlowBoundaries.put(CashFlowConstants.START_MONTH, monthBeforeDisbursalDate.getMonthOfYear());
        cashFlowBoundaries.put(CashFlowConstants.START_YEAR, monthBeforeDisbursalDate.getYear());
        cashFlowBoundaries.put(CashFlowConstants.NO_OF_MONTHS, noOfMonths);

        return cashFlowBoundaries;
    }
/*
    TODO: use this method after saving cash flow in loan create service
    private void cleanCashFlowContext(HttpSession session) {
        session.removeAttribute(START_MONTH);
        session.removeAttribute(START_YEAR);
        session.removeAttribute(NO_OF_MONTHS);
        session.removeAttribute(JOIN_URL);
        session.removeAttribute(CANCEL_URL);
    }
*/
}
