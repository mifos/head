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

@SuppressWarnings("PMD.AtLeastOneConstructor") // constructor unneeded for a constants class
public class AccountingRulesConstants {

    public static final String DIGITS_AFTER_DECIMAL = "AccountingRules.DigitsAfterDecimal";
    public static final String MULTIPLE_DIGITS_AFTER_DECIMAL = "AccountingRules.MultipleDigitsAfterDecimal";
    public static final String ROUNDING_RULE = "AccountingRules.RoundingRule";
    public static final String NUMBER_OF_INTEREST_DAYS = "AccountingRules.NumberOfInterestDays";
    public static final String AMOUNT_TO_BE_ROUNDED_TO = "AccountingRules.AmountToBeRoundedTo";
    public static final String CURRENCY_CODE = "AccountingRules.CurrencyCode";
    public static final String ADDITIONAL_CURRENCY_CODES = "AccountingRules.AdditionalCurrencyCodes";
    public static final String DIGITS_AFTER_DECIMAL_FOR_INTEREST = "AccountingRules.DigitsAfterDecimalForInterest";
    public static final String DIGITS_AFTER_DECIMAL_FOR_CASHFLOW_VALIDATIONS = "AccountingRules.DigitsAfterDecimalForCashFlowValidations";
    public static final String MAX_INTEREST = "AccountingRules.MaxInterest";
    public static final String MIN_INTEREST = "AccountingRules.MinInterest";
    public static final String MAX_CASH_FLOW_THRESHOLD = "AccountingRules.MaxCashFlowThreshold";
    public static final String MIN_CASH_FLOW_THRESHOLD = "AccountingRules.MinCashFlowThreshold";
    public static final String MAX_INDEBTEDNESS_RATIO = "AccountingRules.MaxIndebtednessRatio";
    public static final String MIN_INDEBTEDNESS_RATIO = "AccountingRules.MinIndebtednessRatio";
    public static final String MAX_REPAYMENT_CAPACITY = "AccountingRules.MaxRepaymentCapacity";
    public static final String MIN_REPAYMENT_CAPACITY= "AccountingRules.MinRepaymentCapacity";
    public static final String INITIAL_ROUNDING_MODE = "AccountingRules.InitialRoundingMode";
    public static final String INITIAL_ROUND_OFF_MULTIPLE = "AccountingRules.InitialRoundOffMultiple";
    public static final String FINAL_ROUNDING_MODE = "AccountingRules.FinalRoundingMode";
    public static final String FINAL_ROUND_OFF_MULTIPLE = "AccountingRules.FinalRoundOffMultiple";
    public static final String CURRENCY_ROUNDING_MODE = "AccountingRules.CurrencyRoundingMode";
    public static final String BACKDATED_TRANSACTIONS_ALLOWED = "BackDatedTransactionsAllowed";
    public static final String BACKDATED_APPROVALS_ALLOWED = "BackDatedApprovalsAllowed";
    public static final String OVERDUE_INTEREST_PAID_FIRST = "OverdueInterestPaidFirst";
    public static final String GL_NAMES_MODE = "AccountingRules.GlNamesMode";
    public static final String ENABLE_SIMPLE_ACCOUNTING = "AccountingRules.SimpleAccounting"; 
    public static final String GROUP_LOAN_WITH_MEMBERS = "AccountingRules.GroupLoanWithMembers";
}
