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

import java.math.BigDecimal;

@SuppressWarnings("PMD.AtLeastOneConstructor")
public class CashFlowConstants {
    public static final String EMPTY_EXPENSE = "CashFlowForm.monthlyCashFlows.expense";
    public static final String EMPTY_REVENUE = "CashFlowForm.monthlyCashFlows.revenue";
    public static final String EMPTY_NOTES = "CashFlowForm.monthlyCashFlows.notes";
    public static final String CANCEL_URL = "cancelUrl";
    public static final String JOIN_URL = "joinUrl";
    public static final String NO_OF_MONTHS = "noOfMonths";
    public static final String START_YEAR = "startYear";
    public static final String START_MONTH = "startMonth";
    public static final String CASH_FLOW_FORM = "cashFlow";
    public static final String CAPTURE_CAPITAL_LIABILITY_INFO = "captureCapitalLiabilityInfo";
    public static final String TOTAL_LIABILITY = "cashFlow.totalLiability";
    public static final String TOTAL_CAPITAL= "cashFlow.totalCapital";
    public static final String TOTAL_CAPITAL_SHOULD_BE_GREATER_THAN_ZERO = "cashFlowForm.totalCapital.should.be.greater.than.zero";
    public static final String TOTAL_CAPITAL_SHOULD_NOT_BE_EMPTY = "cashFlowForm.totalCapital.should.not.be.empty";
    public static final String TOTAL_LIABILITY_SHOULD_NOT_BE_EMPTY = "cashFlowForm.totalLiability.should.not.be.empty";
    public static final String TOTAL_LIABILITY_SHOULD_BE_NON_NEGATIVE = "cashFlowForm.totalLiability.should.be.non.negative";
    public static final String INDEBTEDNESS_RATIO_MORE_THAN_ALLOWED= "cashFlowForm.indebtednessRatio.more.than.allowed";
    public static final String INDEBTEDNESS_RATIO="indebtednessRatio";

    public static final int FIRST_DAY = 1;
    public static final int EXTRA_DURATION_FOR_CASH_FLOW_SCHEDULE = 1;
    public static final String LOAN_AMOUNT_VALUE="loanAmountValue";
    public static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    public static final String LOCALE = "locale";
}
